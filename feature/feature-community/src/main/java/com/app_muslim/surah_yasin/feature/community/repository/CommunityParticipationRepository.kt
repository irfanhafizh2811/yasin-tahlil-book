package com.app_muslim.surah_yasin.feature.community.repository

import com.app_muslim.surah_yasin.feature.community.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.FieldValue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Community Prayer Participation Tracking
 * Manages prayer sessions, participant tracking, and community engagement
 */
@Singleton
class CommunityParticipationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    /**
     * Create a new community prayer session
     */
    suspend fun createPrayerSession(
        memorialId: String,
        memorialName: String,
        sessionType: CommunitySessionType,
        prayerType: CommunityPrayerType,
        targetPrayerCount: Int = 100,
        maxParticipants: Int = 50,
        scheduledStartTime: ZonedDateTime? = null,
        sessionNotes: String = "",
        privacyLevel: SessionPrivacyLevel = SessionPrivacyLevel.PUBLIC
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionId = firestore.collection("community_prayer_sessions").document().id
            
            val sessionData = mapOf(
                "session_id" to sessionId,
                "memorial_id" to memorialId,
                "memorial_name" to memorialName,
                "host_user_id" to userId,
                "host_name" to getCurrentUserName(),
                "session_type" to sessionType.name,
                "prayer_type" to prayerType.name,
                "target_prayer_count" to targetPrayerCount,
                "current_prayer_count" to 0,
                "max_participants" to maxParticipants,
                "scheduled_start_time" to scheduledStartTime?.let { 
                    com.google.firebase.Timestamp(it.toEpochSecond(), it.nano) 
                },
                "actual_start_time" to null,
                "end_time" to null,
                "estimated_duration" to calculateEstimatedDuration(prayerType, targetPrayerCount),
                "actual_duration" to 0L,
                "is_active" to false,
                "is_completed" to false,
                "region" to getCurrentUserRegion(),
                "language" to getCurrentUserLanguage(),
                "session_notes" to sessionNotes,
                "privacy_level" to privacyLevel.name,
                "created_at" to FieldValue.serverTimestamp(),
                "updated_at" to FieldValue.serverTimestamp(),
                "participants" to emptyList<Map<String, Any>>(),
                "participant_count" to 0
            )
            
            firestore.collection("community_prayer_sessions")
                .document(sessionId)
                .set(sessionData)
                .await()
            
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Join a community prayer session
     */
    suspend fun joinPrayerSession(
        sessionId: String,
        role: ParticipantRole = ParticipantRole.PARTICIPANT,
        isAnonymous: Boolean = false
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionRef = firestore.collection("community_prayer_sessions").document(sessionId)
            
            firestore.runTransaction { transaction ->
                val sessionDoc = transaction.get(sessionRef)
                
                if (!sessionDoc.exists()) {
                    throw Exception("Prayer session not found")
                }
                
                val sessionData = sessionDoc.data!!
                val currentParticipants = sessionData["participants"] as? List<Map<String, Any>> ?: emptyList()
                val maxParticipants = (sessionData["max_participants"] as? Number)?.toInt() ?: 50
                
                // Check if user is already a participant
                val isAlreadyParticipant = currentParticipants.any { 
                    it["user_id"] == userId 
                }
                
                if (isAlreadyParticipant) {
                    throw Exception("You are already a participant in this session")
                }
                
                // Check participant limit
                if (currentParticipants.size >= maxParticipants) {
                    throw Exception("Session has reached maximum participants")
                }
                
                // Create participant entry
                val participant = mapOf(
                    "user_id" to userId,
                    "display_name" to if (isAnonymous) "Anonymous" else getCurrentUserName(),
                    "joined_at" to FieldValue.serverTimestamp(),
                    "prayer_count" to 0,
                    "is_active" to true,
                    "region" to getCurrentUserRegion(),
                    "role" to role.name,
                    "is_anonymous" to isAnonymous,
                    "last_activity" to FieldValue.serverTimestamp()
                )
                
                // Update session with new participant
                val updatedParticipants = currentParticipants + participant
                
                transaction.update(sessionRef, mapOf(
                    "participants" to updatedParticipants,
                    "participant_count" to updatedParticipants.size,
                    "updated_at" to FieldValue.serverTimestamp()
                ))
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Start a prayer session (for hosts)
     */
    suspend fun startPrayerSession(sessionId: String): Result<Unit> {
        return try {
            val sessionRef = firestore.collection("community_prayer_sessions").document(sessionId)
            
            firestore.runTransaction { transaction ->
                val sessionDoc = transaction.get(sessionRef)
                
                if (!sessionDoc.exists()) {
                    throw Exception("Prayer session not found")
                }
                
                val sessionData = sessionDoc.data!!
                val hostUserId = sessionData["host_user_id"] as String
                
                // Verify user is the host
                if (auth.currentUser?.uid != hostUserId) {
                    throw Exception("Only the session host can start the prayer")
                }
                
                // Check if already started
                if (sessionData["is_active"] as? Boolean == true) {
                    throw Exception("Session is already active")
                }
                
                transaction.update(sessionRef, mapOf(
                    "is_active" to true,
                    "actual_start_time" to FieldValue.serverTimestamp(),
                    "updated_at" to FieldValue.serverTimestamp()
                ))
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Record prayer participation during a session
     */
    suspend fun recordPrayerParticipation(
        sessionId: String,
        prayerCount: Int = 1
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionRef = firestore.collection("community_prayer_sessions").document(sessionId)
            
            firestore.runTransaction { transaction ->
                val sessionDoc = transaction.get(sessionRef)
                
                if (!sessionDoc.exists()) {
                    throw Exception("Prayer session not found")
                }
                
                val sessionData = sessionDoc.data!!
                val participants = (sessionData["participants"] as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()
                
                // Find and update participant
                var participantFound = false
                for (i in participants.indices) {
                    val participant = participants[i].toMutableMap()
                    if (participant["user_id"] == userId) {
                        val currentCount = (participant["prayer_count"] as? Number)?.toInt() ?: 0
                        participant["prayer_count"] = currentCount + prayerCount
                        participant["last_activity"] = FieldValue.serverTimestamp()
                        participants[i] = participant
                        participantFound = true
                        break
                    }
                }
                
                if (!participantFound) {
                    throw Exception("You are not a participant in this session")
                }
                
                // Update session prayer count
                val currentSessionCount = (sessionData["current_prayer_count"] as? Number)?.toInt() ?: 0
                val newSessionCount = currentSessionCount + prayerCount
                
                transaction.update(sessionRef, mapOf(
                    "participants" to participants,
                    "current_prayer_count" to newSessionCount,
                    "updated_at" to FieldValue.serverTimestamp()
                ))
                
                // Check if target is reached
                val targetCount = (sessionData["target_prayer_count"] as? Number)?.toInt() ?: 100
                if (newSessionCount >= targetCount) {
                    // Mark session as completed
                    transaction.update(sessionRef, mapOf(
                        "is_completed" to true,
                        "is_active" to false,
                        "end_time" to FieldValue.serverTimestamp(),
                        "actual_duration" to calculateSessionDuration(sessionData)
                    ))
                }
                
            }.await()
            
            // Record individual participation session
            recordIndividualParticipation(sessionId, prayerCount)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get active prayer sessions
     */
    fun getActivePrayerSessions(
        region: String? = null,
        prayerType: CommunityPrayerType? = null,
        limit: Int = 20
    ): Flow<List<CommunityPrayerSession>> {
        return firestore.collection("community_prayer_sessions")
            .let { query ->
                if (region != null) {
                    query.whereEqualTo("region", region)
                } else query
            }
            .let { query ->
                if (prayerType != null) {
                    query.whereEqualTo("prayer_type", prayerType.name)
                } else query
            }
            .whereEqualTo("is_active", true)
            .whereEqualTo("privacy_level", SessionPrivacyLevel.PUBLIC.name)
            .orderBy("actual_start_time", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToCommunityPrayerSession(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Get upcoming scheduled sessions
     */
    fun getUpcomingSessions(
        region: String? = null,
        limit: Int = 10
    ): Flow<List<CommunityPrayerSession>> {
        val currentTime = com.google.firebase.Timestamp.now()
        
        return firestore.collection("community_prayer_sessions")
            .let { query ->
                if (region != null) {
                    query.whereEqualTo("region", region)
                } else query
            }
            .whereEqualTo("session_type", CommunitySessionType.MEMORIAL_PRAYER.name)
            .whereEqualTo("is_active", false)
            .whereEqualTo("is_completed", false)
            .whereGreaterThan("scheduled_start_time", currentTime)
            .orderBy("scheduled_start_time", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToCommunityPrayerSession(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Get user's prayer participation history
     */
    fun getUserParticipationHistory(
        userId: String? = null,
        limit: Int = 50
    ): Flow<List<PrayerParticipationSession>> {
        val targetUserId = userId ?: auth.currentUser?.uid ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        
        return firestore.collection("prayer_participation_sessions")
            .whereEqualTo("participant_id", targetUserId)
            .orderBy("start_time", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToPrayerParticipationSession(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Get session details by ID
     */
    suspend fun getSessionById(sessionId: String): Result<CommunityPrayerSession?> {
        return try {
            val doc = firestore.collection("community_prayer_sessions")
                .document(sessionId)
                .get()
                .await()
            
            if (doc.exists()) {
                val data = doc.data!!
                val session = mapDocumentToCommunityPrayerSession(doc.id, data)
                Result.success(session)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Leave a prayer session
     */
    suspend fun leavePrayerSession(sessionId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionRef = firestore.collection("community_prayer_sessions").document(sessionId)
            
            firestore.runTransaction { transaction ->
                val sessionDoc = transaction.get(sessionRef)
                
                if (!sessionDoc.exists()) {
                    throw Exception("Prayer session not found")
                }
                
                val sessionData = sessionDoc.data!!
                val participants = (sessionData["participants"] as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()
                
                // Remove participant
                participants.removeAll { participant ->
                    participant["user_id"] == userId
                }
                
                transaction.update(sessionRef, mapOf(
                    "participants" to participants,
                    "participant_count" to participants.size,
                    "updated_at" to FieldValue.serverTimestamp()
                ))
                
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Complete a prayer session (for hosts)
     */
    suspend fun completePrayerSession(sessionId: String): Result<Unit> {
        return try {
            val sessionRef = firestore.collection("community_prayer_sessions").document(sessionId)
            
            firestore.runTransaction { transaction ->
                val sessionDoc = transaction.get(sessionRef)
                
                if (!sessionDoc.exists()) {
                    throw Exception("Prayer session not found")
                }
                
                val sessionData = sessionDoc.data!!
                val hostUserId = sessionData["host_user_id"] as String
                
                // Verify user is the host
                if (auth.currentUser?.uid != hostUserId) {
                    throw Exception("Only the session host can complete the prayer")
                }
                
                transaction.update(sessionRef, mapOf(
                    "is_completed" to true,
                    "is_active" to false,
                    "end_time" to FieldValue.serverTimestamp(),
                    "actual_duration" to calculateSessionDuration(sessionData),
                    "updated_at" to FieldValue.serverTimestamp()
                ))
                
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper Methods
    
    private suspend fun recordIndividualParticipation(
        sessionId: String,
        prayerCount: Int
    ) {
        try {
            val userId = auth.currentUser?.uid ?: return
            
            val participationData = mapOf(
                "session_id" to sessionId,
                "participant_id" to userId,
                "participant_name" to getCurrentUserName(),
                "participant_region" to getCurrentUserRegion(),
                "prayer_count_increment" to prayerCount,
                "timestamp" to FieldValue.serverTimestamp(),
                "device_info" to mapOf(
                    "platform" to "Android",
                    "version" to android.os.Build.VERSION.RELEASE,
                    "country" to getCurrentUserRegion(),
                    "time_zone" to ZonedDateTime.now().zone.id
                )
            )
            
            firestore.collection("prayer_participation_sessions")
                .add(participationData)
                .await()
        } catch (e: Exception) {
            // Log error but don't fail the main operation
        }
    }
    
    private fun calculateEstimatedDuration(
        prayerType: CommunityPrayerType,
        targetCount: Int
    ): Long {
        val baseMinutesPerPrayer = when (prayerType) {
            CommunityPrayerType.TAHLIL -> 2
            CommunityPrayerType.YASIN -> 15
            CommunityPrayerType.FATIHAH -> 1
            CommunityPrayerType.DHIKR -> 2
            CommunityPrayerType.DUA -> 3
            CommunityPrayerType.QURAN -> 10
            CommunityPrayerType.COMMUNITY_PRAYER -> 8
        }
        
        return (baseMinutesPerPrayer * targetCount / 10).toLong() // Assuming 10 people on average
    }
    
    private fun calculateSessionDuration(sessionData: Map<String, Any>): Long {
        val startTime = sessionData["actual_start_time"] as? com.google.firebase.Timestamp
        return if (startTime != null) {
            val now = System.currentTimeMillis()
            val start = startTime.seconds * 1000
            (now - start) / 60000 // Duration in minutes
        } else 0L
    }
    
    private fun mapDocumentToCommunityPrayerSession(
        documentId: String,
        data: Map<String, Any>
    ): CommunityPrayerSession {
        return CommunityPrayerSession(
            sessionId = documentId,
            memorialId = data["memorial_id"] as? String ?: "",
            hostUserId = data["host_user_id"] as? String ?: "",
            hostDisplayName = data["host_name"] as? String ?: "",
            prayerType = CommunityPrayerType.valueOf(
                data["prayer_type"] as? String ?: CommunityPrayerType.TAHLIL.name
            ),
            isPublic = data["is_public"] as? Boolean ?: true,
            regionCode = data["region"] as? String ?: "",
            participants = mapParticipantsList(data["participants"] as? List<Map<String, Any>> ?: emptyList()),
            startTime = (data["start_time"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            estimatedDuration = (data["estimated_duration"] as? Number)?.toLong() ?: 60L,
            targetPrayerCount = (data["target_prayer_count"] as? Number)?.toInt() ?: 100,
            currentPrayerCount = (data["current_prayer_count"] as? Number)?.toInt() ?: 0,
            status = SessionStatus.valueOf(
                data["status"] as? String ?: SessionStatus.WAITING.name
            ),
            allowJoinAfterStart = data["allow_join_after_start"] as? Boolean ?: true,
            maxParticipants = (data["max_participants"] as? Number)?.toInt() ?: 50,
            sessionNotes = data["session_notes"] as? String,
            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now()
        )
    }
    
    private fun mapParticipantsList(participantsList: List<Map<String, Any>>): List<SessionParticipant> {
        return participantsList.mapNotNull { participantData ->
            try {
                SessionParticipant(
                    userId = participantData["user_id"] as? String ?: "",
                    displayName = participantData["display_name"] as? String ?: "",
                    role = ParticipantRole.valueOf(
                        participantData["role"] as? String ?: ParticipantRole.PARTICIPANT.name
                    ),
                    joinedAt = (participantData["joined_at"] as? com.google.firebase.Timestamp)?.let {
                        ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
                    } ?: ZonedDateTime.now(),
                    currentPrayerCount = (participantData["prayer_count"] as? Number)?.toInt() ?: 0,
                    isActive = participantData["is_active"] as? Boolean ?: true,
                    contributionPercentage = (participantData["contribution_percentage"] as? Number)?.toFloat() ?: 0.0f,
                    regionCode = participantData["region"] as? String ?: "",
                    isAnonymous = participantData["is_anonymous"] as? Boolean ?: false
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun mapDocumentToPrayerParticipationSession(
        documentId: String,
        data: Map<String, Any>
    ): PrayerParticipationSession {
        return PrayerParticipationSession(
            sessionId = documentId,
            memorialId = data["memorial_id"] as? String ?: "",
            participantId = data["participant_id"] as? String ?: "",
            prayerType = CommunityPrayerType.valueOf(
                data["prayer_type"] as? String ?: CommunityPrayerType.TAHLIL.name
            ),
            startTime = (data["start_time"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            endTime = (data["end_time"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            },
            completedPrayers = (data["prayer_count"] as? Number)?.toInt() ?: 0,
            targetPrayers = (data["target_prayer_count"] as? Number)?.toInt() ?: 100,
            isCompleted = data["is_completed"] as? Boolean ?: false,
            notes = data["notes"] as? String ?: "",
            region = data["participant_region"] as? String ?: ""
        )
    }
    
    // Helper methods - would normally get from user service/preferences
    private fun getCurrentUserName(): String = "Family Member" // TODO: Get from user profile
    private fun getCurrentUserRegion(): String = "Middle East" // TODO: Get from user profile/location
    private fun getCurrentUserLanguage(): String = "en" // TODO: Get from user preferences
}