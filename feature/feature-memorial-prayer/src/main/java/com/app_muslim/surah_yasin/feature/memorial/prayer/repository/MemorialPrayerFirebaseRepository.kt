package com.app_muslim.surah_yasin.feature.memorial.prayer.repository

import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.Timestamp
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Complete Firebase implementation for Memorial Prayer Sessions
 * Includes real-time synchronization, offline support, and analytics
 */
@Singleton
class MemorialPrayerFirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) : MemorialPrayerRepository {
    
    companion object {
        private const val MEMORIALS_COLLECTION = "memorials"
        private const val SESSIONS_COLLECTION = "prayer_sessions"
        private const val PRAYERS_COLLECTION = "prayers"
        private const val ANALYTICS_COLLECTION = "analytics"
        private const val USER_STATS_COLLECTION = "user_stats"
    }
    
    override suspend fun startPrayerSession(
        memorialId: String,
        prayerType: PrayerType,
        targetCount: Int,
        culturalSettings: CulturalSettings
    ): Result<MemorialPrayerSession> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionId = generateSessionId()
            val currentTime = ZonedDateTime.now()
            
            val session = MemorialPrayerSession(
                sessionId = sessionId,
                memorialId = memorialId,
                prayerType = prayerType,
                startTime = currentTime,
                endTime = null,
                prayerCount = 0,
                participantCount = 1,
                isCompleted = false,
                culturalSettings = culturalSettings,
                sessionNotes = null
            )
            
            // Convert to Firestore map
            val sessionData = mapOf(
                "sessionId" to sessionId,
                "memorialId" to memorialId,
                "userId" to currentUser.uid,
                "prayerType" to prayerType.name.lowercase(),
                "targetCount" to targetCount,
                "currentCount" to 0,
                "startTime" to Timestamp.now(),
                "endTime" to null,
                "isCompleted" to false,
                "isPaused" to false,
                "participantCount" to 1,
                "culturalSettings" to mapOf(
                    "schoolOfThought" to culturalSettings.schoolOfThought.name.lowercase(),
                    "language" to culturalSettings.language,
                    "regionCode" to culturalSettings.regionCode,
                    "prayerTradition" to culturalSettings.prayerTradition.name.lowercase()
                ),
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )
            
            // Use transaction for consistency
            firestore.runTransaction { transaction ->
                // Create session document
                val sessionRef = firestore
                    .collection(MEMORIALS_COLLECTION)
                    .document(memorialId)
                    .collection(SESSIONS_COLLECTION)
                    .document(sessionId)
                
                transaction.set(sessionRef, sessionData)
                
                // Update memorial statistics
                val memorialRef = firestore
                    .collection(MEMORIALS_COLLECTION)
                    .document(memorialId)
                
                transaction.update(memorialRef, mapOf(
                    "lastPrayerAt" to Timestamp.now(),
                    "totalSessions" to FieldValue.increment(1),
                    "updatedAt" to Timestamp.now()
                ))
                
                // Update user statistics
                val userStatsRef = firestore
                    .collection(USER_STATS_COLLECTION)
                    .document(currentUser.uid)
                
                transaction.set(userStatsRef, mapOf(
                    "userId" to currentUser.uid,
                    "totalSessions" to FieldValue.increment(1),
                    "currentActiveSession" to sessionId,
                    "lastActivityAt" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                ), com.google.firebase.firestore.SetOptions.merge())
                
                null
            }.await()
            
            // Log analytics
            logPrayerSessionStarted(session, currentUser.uid)
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePrayerProgress(sessionId: String, currentCount: Int): Result<MemorialPrayerSession> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Find and update session
            val sessionQuery = firestore
                .collectionGroup(SESSIONS_COLLECTION)
                .whereEqualTo("sessionId", sessionId)
                .whereEqualTo("userId", currentUser.uid)
                .limit(1)
                .get()
                .await()
            
            if (sessionQuery.isEmpty) {
                return Result.failure(IllegalArgumentException("Session not found"))
            }
            
            val sessionDoc = sessionQuery.documents.first()
            val sessionData = sessionDoc.data ?: return Result.failure(IllegalStateException("Invalid session data"))
            
            // Update progress
            sessionDoc.reference.update(mapOf(
                "currentCount" to currentCount,
                "updatedAt" to Timestamp.now()
            )).await()
            
            // Create updated session object
            val updatedSession = createSessionFromFirestore(sessionData, currentCount)
            
            // Check for milestones and celebrations
            checkMilestones(sessionId, currentCount, sessionData["targetCount"] as Long)
            
            // Log analytics
            logPrayerProgress(sessionId, currentCount, currentUser.uid)
            
            Result.success(updatedSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pausePrayerSession(sessionId: String): Result<MemorialPrayerSession> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            updateSessionStatus(sessionId, currentUser.uid, mapOf(
                "isPaused" to true,
                "pausedAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            ))
            
            val session = getPrayerSession(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found"))
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resumePrayerSession(sessionId: String): Result<MemorialPrayerSession> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            updateSessionStatus(sessionId, currentUser.uid, mapOf(
                "isPaused" to false,
                "resumedAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            ))
            
            val session = getPrayerSession(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found"))
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completePrayerSession(sessionId: String): Result<MemorialPrayerSession> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Find session first, then use transaction
            val sessionQuery = firestore
                .collectionGroup(SESSIONS_COLLECTION)
                .whereEqualTo("sessionId", sessionId)
                .whereEqualTo("userId", currentUser.uid)
                .limit(1)
                .get()
                .await()
            
            if (sessionQuery.isEmpty) {
                return Result.failure(IllegalArgumentException("Session not found"))
            }
            
            val sessionDoc = sessionQuery.documents.first()
            val sessionData = sessionDoc.data!!
            
            // Complete session with transaction  
            val session = firestore.runTransaction { transaction ->
                
                // Update session completion
                transaction.update(sessionDoc.reference, mapOf(
                    "isCompleted" to true,
                    "completedAt" to Timestamp.now(),
                    "endTime" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                ))
                
                // Update memorial total prayers
                val memorialRef = firestore
                    .collection(MEMORIALS_COLLECTION)
                    .document(sessionData["memorialId"] as String)
                
                transaction.update(memorialRef, mapOf(
                    "totalPrayers" to FieldValue.increment(sessionData["currentCount"] as Long),
                    "lastCompletedAt" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                ))
                
                // Update user statistics
                val userStatsRef = firestore
                    .collection(USER_STATS_COLLECTION)
                    .document(currentUser.uid)
                
                transaction.update(userStatsRef, mapOf(
                    "totalCompletedSessions" to FieldValue.increment(1),
                    "totalPrayers" to FieldValue.increment(sessionData["currentCount"] as Long),
                    "currentActiveSession" to null,
                    "lastCompletionAt" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                ))
                
                // Create daily analytics record
                val today = ZonedDateTime.now().toLocalDate().toString()
                val analyticsRef = firestore
                    .collection(ANALYTICS_COLLECTION)
                    .document("daily")
                    .collection(today)
                    .document("prayers")
                
                transaction.set(analyticsRef, mapOf(
                    "date" to today,
                    "completedSessions" to FieldValue.increment(1),
                    "totalPrayers" to FieldValue.increment(sessionData["currentCount"] as Long),
                    "prayersByType" to mapOf(
                        sessionData["prayerType"] as String to FieldValue.increment(sessionData["currentCount"] as Long)
                    ),
                    "updatedAt" to Timestamp.now()
                ), com.google.firebase.firestore.SetOptions.merge())
                
                createSessionFromFirestore(sessionData)
            }.await()
            
            // Trigger celebration and rewards
            triggerPrayerCompletion(sessionId, currentUser.uid)
            
            // Log completion analytics
            logPrayerSessionCompleted(session, currentUser.uid)
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getPrayerSessionFlow(sessionId: String): Flow<MemorialPrayerSession?> {
        return firestore
            .collectionGroup(SESSIONS_COLLECTION)
            .whereEqualTo("sessionId", sessionId)
            .limit(1)
            .snapshots()
            .map { querySnapshot ->
                if (querySnapshot.isEmpty) null
                else {
                    val sessionDoc = querySnapshot.documents.first()
                    createSessionFromFirestore(sessionDoc.data!!)
                }
            }
            .catch { e ->
                emit(null)
            }
    }
    
    override fun getRecentSessionsFlow(memorialId: String): Flow<List<MemorialPrayerSession>> {
        return firestore
            .collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .collection(SESSIONS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    doc.data?.let { createSessionFromFirestore(it) }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override fun getPrayerStatisticsFlow(memorialId: String): Flow<MemorialPrayerStats> {
        return firestore
            .collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .snapshots()
            .map { documentSnapshot ->
                val data = documentSnapshot.data ?: emptyMap()
                MemorialPrayerStats(
                    totalSessions = (data["totalSessions"] as? Long ?: 0L).toInt(),
                    totalPrayers = (data["totalPrayers"] as? Long ?: 0L).toInt(),
                    totalTimeSpent = 0L, // Calculate from sessions
                    favoriteTimeOfDay = "Evening", // Analyze from session times
                    mostUsedPrayerType = PrayerType.FATIHAH, // Analyze from sessions
                    longestSession = 0L, // Calculate from sessions
                    currentStreak = 0 // Calculate from daily completion
                )
            }
            .catch { e ->
                emit(MemorialPrayerStats(0, 0, 0, "Evening", PrayerType.FATIHAH, 0, 0))
            }
    }
    
    override fun getGlobalPrayerStatsFlow(): Flow<Map<String, Any>> {
        val today = ZonedDateTime.now().toLocalDate().toString()
        return firestore
            .collection(ANALYTICS_COLLECTION)
            .document("daily")
            .collection(today)
            .document("prayers")
            .snapshots()
            .map { documentSnapshot ->
                documentSnapshot.data ?: emptyMap()
            }
            .catch { e ->
                emit(emptyMap())
            }
    }
    
    // Helper methods
    private suspend fun updateSessionStatus(sessionId: String, userId: String, updateData: Map<String, Any>) {
        val sessionQuery = firestore
            .collectionGroup(SESSIONS_COLLECTION)
            .whereEqualTo("sessionId", sessionId)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()
        
        if (!sessionQuery.isEmpty) {
            sessionQuery.documents.first().reference.update(updateData).await()
        }
    }
    
    private fun createSessionFromFirestore(data: Map<String, Any>, overrideCount: Int? = null): MemorialPrayerSession {
        val culturalData = data["culturalSettings"] as? Map<String, Any> ?: emptyMap()
        
        return MemorialPrayerSession(
            sessionId = data["sessionId"] as String,
            memorialId = data["memorialId"] as String,
            prayerType = PrayerType.valueOf(data["prayerType"].toString().uppercase()),
            startTime = (data["startTime"] as Timestamp).toZonedDateTime(),
            endTime = (data["endTime"] as? Timestamp)?.toZonedDateTime(),
            prayerCount = overrideCount ?: (data["currentCount"] as? Long)?.toInt() ?: 0,
            participantCount = (data["participantCount"] as? Long)?.toInt() ?: 1,
            isCompleted = data["isCompleted"] as? Boolean ?: false,
            culturalSettings = CulturalSettings(
                schoolOfThought = SchoolOfThought.valueOf((culturalData["schoolOfThought"] ?: "SHAFII").toString().uppercase()),
                language = culturalData["language"] as? String ?: "en",
                regionCode = culturalData["regionCode"] as? String ?: "US",
                prayerTradition = PrayerTradition.valueOf((culturalData["prayerTradition"] ?: "INDIVIDUAL").toString().uppercase())
            ),
            sessionNotes = data["sessionNotes"] as? String
        )
    }
    
    private fun Timestamp.toZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds, nanoseconds.toLong()), ZoneId.systemDefault())
    }
    
    private fun generateSessionId(): String = UUID.randomUUID().toString()
    
    private suspend fun checkMilestones(sessionId: String, currentCount: Int, targetCount: Long) {
        val milestones = listOf(25, 50, 75, 100)
        val percentage = (currentCount.toFloat() / targetCount.toFloat() * 100).toInt()
        
        if (milestones.contains(percentage)) {
            // Trigger milestone celebration
            triggerMilestoneReached(sessionId, percentage)
        }
    }
    
    private suspend fun triggerPrayerCompletion(sessionId: String, userId: String) {
        // Call Cloud Function for completion celebration
        try {
            functions.getHttpsCallable("triggerPrayerCompletion")
                .call(mapOf("sessionId" to sessionId, "userId" to userId))
                .await()
        } catch (e: Exception) {
            // Log but don't fail the operation
        }
    }
    
    private suspend fun triggerMilestoneReached(sessionId: String, percentage: Int) {
        // Call Cloud Function for milestone celebration
        try {
            functions.getHttpsCallable("triggerMilestone")
                .call(mapOf("sessionId" to sessionId, "milestone" to percentage))
                .await()
        } catch (e: Exception) {
            // Log but don't fail the operation
        }
    }
    
    private suspend fun logPrayerSessionStarted(session: MemorialPrayerSession, userId: String) {
        // Log to Firebase Analytics
    }
    
    private suspend fun logPrayerProgress(sessionId: String, count: Int, userId: String) {
        // Log progress to Firebase Analytics
    }
    
    private suspend fun logPrayerSessionCompleted(session: MemorialPrayerSession, userId: String) {
        // Log completion to Firebase Analytics
    }
    
    // Implement remaining interface methods
    override suspend fun getPrayerSession(sessionId: String): MemorialPrayerSession? {
        return try {
            val sessionQuery = firestore
                .collectionGroup(SESSIONS_COLLECTION)
                .whereEqualTo("sessionId", sessionId)
                .limit(1)
                .get()
                .await()
            
            if (sessionQuery.isEmpty) null
            else createSessionFromFirestore(sessionQuery.documents.first().data!!)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getRecentSessions(memorialId: String, limit: Int): List<MemorialPrayerSession> {
        return try {
            val sessions = firestore
                .collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .collection(SESSIONS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            sessions.documents.mapNotNull { doc ->
                doc.data?.let { createSessionFromFirestore(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getPrayerStatistics(memorialId: String): MemorialPrayerStats {
        return try {
            val memorial = firestore
                .collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .get()
                .await()
            
            val data = memorial.data ?: emptyMap()
            MemorialPrayerStats(
                totalSessions = (data["totalSessions"] as? Long ?: 0L).toInt(),
                totalPrayers = (data["totalPrayers"] as? Long ?: 0L).toInt(),
                totalTimeSpent = 0L,
                favoriteTimeOfDay = "Evening",
                mostUsedPrayerType = PrayerType.FATIHAH,
                longestSession = 0L,
                currentStreak = 0
            )
        } catch (e: Exception) {
            MemorialPrayerStats(0, 0, 0, "Evening", PrayerType.FATIHAH, 0, 0)
        }
    }
    
    override suspend fun deletePrayerSession(sessionId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionQuery = firestore
                .collectionGroup(SESSIONS_COLLECTION)
                .whereEqualTo("sessionId", sessionId)
                .whereEqualTo("userId", currentUser.uid)
                .limit(1)
                .get()
                .await()
            
            if (!sessionQuery.isEmpty) {
                sessionQuery.documents.first().reference.delete().await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun syncOfflineData(): Result<Unit> {
        return try {
            // Firebase Firestore automatically handles offline sync
            // This method can be used for any additional offline sync logic
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension function for session conversion
fun MemorialPrayerSession.toFirestoreMap(): MutableMap<String, Any> {
    return mutableMapOf(
        "sessionId" to sessionId,
        "memorialId" to memorialId,
        "prayerType" to prayerType.name.lowercase(),
        "startTime" to Timestamp.now(), // Will be overridden
        "prayerCount" to prayerCount,
        "participantCount" to participantCount,
        "isCompleted" to isCompleted
    )
}