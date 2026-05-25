package com.app_muslim.surah_yasin.feature.memorial.repository

import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTrackingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val PRAYER_SESSIONS_COLLECTION = "prayer_sessions"
        private const val PRAYER_STATS_COLLECTION = "prayer_stats"
        private const val ISLAMIC_EVENTS_COLLECTION = "islamic_events"
        private const val COMMUNITY_PARTICIPATION_COLLECTION = "community_participation"
    }

    suspend fun startPrayerSession(
        memorialId: String,
        prayerType: PrayerType,
        location: PrayerLocation? = null
    ): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val sessionId = firestore.collection(PRAYER_SESSIONS_COLLECTION).document().id
        
        val session = PrayerSession(
            id = sessionId,
            memorialId = memorialId,
            participantId = currentUser.uid,
            participantName = currentUser.displayName ?: "Anonymous",
            prayerType = prayerType,
            startTime = Date(),
            location = location,
            participationLevel = ParticipationLevel.INDIVIDUAL
        )

        firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .document(sessionId)
            .set(session.toFirestoreMap())
            .await()

        return sessionId
    }

    suspend fun completePrayerSession(
        sessionId: String,
        recitationCount: Int,
        notes: String = "",
        spiritualState: SpiritualState = SpiritualState.FOCUSED,
        sessionQuality: SessionQuality = SessionQuality.NORMAL
    ) {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val sessionDoc = firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .document(sessionId)
            .get()
            .await()

        if (!sessionDoc.exists()) {
            throw IllegalArgumentException("Prayer session not found")
        }

        val session = sessionDoc.toPrayerSession()
        if (session?.participantId != currentUser.uid) {
            throw SecurityException("Only the session creator can complete it")
        }

        val endTime = Date()
        val duration = (endTime.time - session.startTime.time) / 1000 // in seconds

        val updatedMetadata = session.metadata.copy(
            sessionQuality = sessionQuality,
            spiritualState = spiritualState,
            updatedAt = endTime
        )

        val updates = mapOf(
            "endTime" to endTime,
            "duration" to duration,
            "isCompleted" to true,
            "recitationCount" to recitationCount,
            "notes" to notes,
            "metadata" to updatedMetadata.toFirestoreMap()
        )

        firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .document(sessionId)
            .update(updates)
            .await()

        // Update user prayer statistics
        updateUserPrayerStats(currentUser.uid, session.copy(
            endTime = endTime,
            duration = duration,
            isCompleted = true,
            recitationCount = recitationCount,
            notes = notes,
            metadata = updatedMetadata
        ))
    }

    fun getUserPrayerSessions(userId: String, limit: Int = 50): Flow<List<PrayerSession>> {
        return firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("participantId", userId)
            .orderBy("startTime", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toPrayerSession() }
            }
    }

    fun getMemorialPrayerSessions(memorialId: String, limit: Int = 100): Flow<List<PrayerSession>> {
        return firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .whereEqualTo("isCompleted", true)
            .orderBy("startTime", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toPrayerSession() }
            }
    }

    suspend fun getUserPrayerStats(userId: String): PrayerTrackingStats {
        val sessionsSnapshot = firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("participantId", userId)
            .whereEqualTo("isCompleted", true)
            .get()
            .await()

        val sessions = sessionsSnapshot.documents.mapNotNull { it.toPrayerSession() }
        
        return calculatePrayerStats(sessions)
    }

    suspend fun getCommunityPrayerParticipation(
        memorialId: String,
        timeRange: TimeRange = TimeRange.LAST_30_DAYS
    ): CommunityParticipation {
        val cutoffDate = getTimeRangeCutoff(timeRange)
        
        val sessionsSnapshot = firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .whereEqualTo("isCompleted", true)
            .whereGreaterThan("startTime", cutoffDate)
            .get()
            .await()

        val sessions = sessionsSnapshot.documents.mapNotNull { it.toPrayerSession() }
        
        return calculateCommunityParticipation(sessions, memorialId)
    }

    suspend fun addCommunityPrayerGroup(
        memorialId: String,
        groupName: String,
        participants: List<String>,
        scheduledTime: Date,
        prayerType: PrayerType
    ): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val groupId = firestore.collection(COMMUNITY_PARTICIPATION_COLLECTION).document().id
        
        val communityGroup = CommunityPrayerGroup(
            id = groupId,
            memorialId = memorialId,
            name = groupName,
            organizer = currentUser.uid,
            createdAt = Date()
        )

        firestore.collection(COMMUNITY_PARTICIPATION_COLLECTION)
            .document(groupId)
            .set(communityGroup.toFirestoreMap())
            .await()

        return groupId
    }

    fun getUpcomingCommunityPrayers(
        userId: String,
        daysAhead: Int = 7
    ): Flow<List<CommunityPrayerGroup>> {
        val endDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, daysAhead)
        }.time

        return firestore.collection(COMMUNITY_PARTICIPATION_COLLECTION)
            .whereArrayContains("participants", userId)
            .whereGreaterThan("scheduledTime", Date())
            .whereLessThan("scheduledTime", endDate)
            .orderBy("scheduledTime", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toCommunityPrayerGroup() }
            }
    }

    private suspend fun updateUserPrayerStats(userId: String, session: PrayerSession) {
        // This would update aggregated statistics in a separate collection
        // for performance optimization
        val statsDoc = firestore.collection(PRAYER_STATS_COLLECTION)
            .document(userId)
            .get()
            .await()

        // Implementation would aggregate stats efficiently
        // This is a simplified version
        val updates = mapOf(
            "totalSessions" to com.google.firebase.firestore.FieldValue.increment(1),
            "totalDuration" to com.google.firebase.firestore.FieldValue.increment(session.duration),
            "lastSessionDate" to session.endTime,
            "updatedAt" to Date()
        )

        firestore.collection(PRAYER_STATS_COLLECTION)
            .document(userId)
            .set(updates, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    private fun calculatePrayerStats(sessions: List<PrayerSession>): PrayerTrackingStats {
        if (sessions.isEmpty()) return PrayerTrackingStats()

        val totalSessions = sessions.size.toLong()
        val totalDuration = sessions.sumOf { it.duration }
        val averageDuration = if (totalSessions > 0) totalDuration / totalSessions else 0L
        val completedSessions = sessions.count { it.isCompleted }.toLong()
        val completionRate = if (totalSessions > 0) completedSessions.toFloat() / totalSessions else 0f

        val sessionsByType = sessions.groupBy { it.prayerType }
            .mapValues { it.value.size.toLong() }

        val sessionsByHour = sessions.groupBy { 
            SimpleDateFormat("HH", Locale.getDefault()).format(it.startTime)
        }.mapValues { it.value.size.toLong() }

        val longestSession = sessions.maxOfOrNull { it.duration } ?: 0L
        val firstSession = sessions.minByOrNull { it.startTime }
        val lastSession = sessions.maxByOrNull { it.startTime }

        // Calculate streaks
        val (currentStreak, longestStreak) = calculatePrayerStreaks(sessions)

        val personalBests = PrayerPersonalBests(
            longestSingleSession = longestSession,
            mostSessionsInDay = calculateMostSessionsInDay(sessions),
            mostConsecutiveDays = longestStreak,
            highestRecitationCount = sessions.maxOfOrNull { it.recitationCount } ?: 0
        )

        return PrayerTrackingStats(
            totalSessions = totalSessions,
            totalDuration = totalDuration,
            averageSessionDuration = averageDuration,
            completedSessions = completedSessions,
            completionRate = completionRate,
            longestSession = longestSession,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            sessionsByPrayerType = sessionsByType,
            sessionsByTime = sessionsByHour,
            lastSessionDate = lastSession?.startTime,
            firstSessionDate = firstSession?.startTime,
            personalBests = personalBests
        )
    }

    private fun calculateCommunityParticipation(
        sessions: List<PrayerSession>, 
        memorialId: String
    ): CommunityParticipation {
        val uniqueParticipants = sessions.map { it.participantId }.distinct()
        val totalPrayers = sessions.sumOf { it.recitationCount }
        val participantsByRegion = sessions.groupBy { it.location?.country ?: "Unknown" }
            .mapValues { it.value.map { session -> session.participantId }.distinct().size }

        return CommunityParticipation(
            memorialId = memorialId,
            totalParticipants = uniqueParticipants.size,
            totalSessions = sessions.size,
            totalPrayers = sessions.sumOf { it.recitationCount },
            participantsByRegion = participantsByRegion,
            recentSessions = sessions.take(10),
            averageSessionDuration = if (sessions.isNotEmpty()) 
                sessions.sumOf { it.duration } / sessions.size else 0L
        )
    }

    private fun calculatePrayerStreaks(sessions: List<PrayerSession>): Pair<Int, Int> {
        if (sessions.isEmpty()) return Pair(0, 0)

        val sessionDates = sessions.map { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.startTime) 
        }.distinct().sorted()

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 1

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time) }

        // Calculate current streak
        if (sessionDates.contains(today)) {
            currentStreak = 1
            // Look backwards for consecutive days
            val cal = Calendar.getInstance()
            for (i in 1 until sessionDates.size) {
                cal.add(Calendar.DAY_OF_MONTH, -1)
                val checkDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                if (sessionDates.contains(checkDate)) {
                    currentStreak++
                } else {
                    break
                }
            }
        } else if (sessionDates.contains(yesterday)) {
            currentStreak = 1
            // Similar logic for yesterday
        }

        // Calculate longest streak
        for (i in 1 until sessionDates.size) {
            val prevDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(sessionDates[i-1])!!
                add(Calendar.DAY_OF_MONTH, 1)
            }
            val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(prevDate.time)
            
            if (expectedDate == sessionDates[i]) {
                tempStreak++
            } else {
                longestStreak = maxOf(longestStreak, tempStreak)
                tempStreak = 1
            }
        }
        longestStreak = maxOf(longestStreak, tempStreak)

        return Pair(currentStreak, longestStreak)
    }

    private fun calculateMostSessionsInDay(sessions: List<PrayerSession>): Int {
        return sessions.groupBy { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.startTime) 
        }.values.maxOfOrNull { it.size } ?: 0
    }

    private fun getTimeRangeCutoff(timeRange: TimeRange): Date {
        val calendar = Calendar.getInstance()
        when (timeRange) {
            TimeRange.LAST_24_HOURS -> calendar.add(Calendar.HOUR_OF_DAY, -24)
            TimeRange.LAST_7_DAYS -> calendar.add(Calendar.DAY_OF_MONTH, -7)
            TimeRange.LAST_30_DAYS -> calendar.add(Calendar.DAY_OF_MONTH, -30)
            TimeRange.LAST_90_DAYS -> calendar.add(Calendar.DAY_OF_MONTH, -90)
            TimeRange.LAST_YEAR -> calendar.add(Calendar.YEAR, -1)
            TimeRange.ALL_TIME -> calendar.add(Calendar.YEAR, -10)
        }
        return calendar.time
    }

    // Extension functions for Firestore mapping
    private fun PrayerSession.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "participantId" to participantId,
            "participantName" to participantName,
            "prayerType" to prayerType.name,
            "startTime" to startTime,
            "endTime" to endTime,
            "duration" to duration,
            "isCompleted" to isCompleted,
            "recitationCount" to recitationCount,
            "notes" to notes,
            "location" to location?.toFirestoreMap(),
            "qiblaDirection" to qiblaDirection,
            "isVerified" to isVerified,
            "verificationSource" to verificationSource.name,
            "participationLevel" to participationLevel.name,
            "metadata" to metadata.toFirestoreMap()
        )
    }

    private fun PrayerLocation.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "city" to city,
            "country" to country,
            "mosque" to mosque,
            "isAtMosque" to isAtMosque
        )
    }

    private fun PrayerSessionMetadata.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "deviceInfo" to deviceInfo,
            "appVersion" to appVersion,
            "sessionQuality" to sessionQuality.name,
            "backgroundNoise" to backgroundNoise,
            "interruptions" to interruptions,
            "prayerIntention" to prayerIntention,
            "spiritualState" to spiritualState.name,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    private fun CommunityPrayerGroup.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "name" to name,
            "description" to description,
            "organizer" to organizer,
            "members" to members.map { member ->
                mapOf(
                    "userId" to member.userId,
                    "name" to member.name,
                    "role" to member.role.name,
                    "joinedAt" to member.joinedAt,
                    "isActive" to member.isActive,
                    "totalSessions" to member.totalSessions
                )
            },
            "scheduledSessions" to scheduledSessions.map { session ->
                mapOf(
                    "id" to session.id,
                    "title" to session.title,
                    "description" to session.description,
                    "scheduledTime" to session.scheduledTime,
                    "duration" to session.duration,
                    "prayerType" to session.prayerType.name,
                    "participants" to session.participants,
                    "status" to session.status.name,
                    "createdBy" to session.createdBy
                )
            },
            "createdAt" to createdAt,
            "isActive" to isActive,
            "maxMembers" to maxMembers,
            "privacy" to privacy.name,
            "requirements" to mapOf(
                "minimumAge" to requirements.minimumAge,
                "requiresVerification" to requirements.requiresVerification,
                "allowsGuests" to requirements.allowsGuests,
                "requiresIntroduction" to requirements.requiresIntroduction,
                "moderatorApproval" to requirements.moderatorApproval
            )
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toPrayerSession(): PrayerSession? {
        return try {
            PrayerSession(
                id = getString("id") ?: "",
                memorialId = getString("memorialId") ?: "",
                participantId = getString("participantId") ?: "",
                participantName = getString("participantName") ?: "",
                prayerType = getString("prayerType")?.let { 
                    try { PrayerType.valueOf(it) } catch (e: Exception) { PrayerType.TAHLIL }
                } ?: PrayerType.TAHLIL,
                startTime = getDate("startTime") ?: Date(),
                endTime = getDate("endTime"),
                duration = getLong("duration") ?: 0,
                isCompleted = getBoolean("isCompleted") ?: false,
                recitationCount = getLong("recitationCount")?.toInt() ?: 0,
                notes = getString("notes") ?: "",
                qiblaDirection = getDouble("qiblaDirection")?.toFloat() ?: 0f,
                isVerified = getBoolean("isVerified") ?: false,
                verificationSource = getString("verificationSource")?.let {
                    try { VerificationSource.valueOf(it) } catch (e: Exception) { VerificationSource.SELF_REPORTED }
                } ?: VerificationSource.SELF_REPORTED,
                participationLevel = getString("participationLevel")?.let {
                    try { ParticipationLevel.valueOf(it) } catch (e: Exception) { ParticipationLevel.INDIVIDUAL }
                } ?: ParticipationLevel.INDIVIDUAL
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toCommunityPrayerGroup(): CommunityPrayerGroup? {
        return try {
            CommunityPrayerGroup(
                id = getString("id") ?: "",
                memorialId = getString("memorialId") ?: "",
                name = getString("name") ?: "",
                description = getString("description") ?: "",
                organizer = getString("organizer") ?: "",
                createdAt = getDate("createdAt") ?: Date(),
                isActive = getBoolean("isActive") ?: true,
                maxMembers = getLong("maxMembers")?.toInt() ?: 50,
                privacy = getString("privacy")?.let {
                    try { GroupPrivacy.valueOf(it) } catch (e: Exception) { GroupPrivacy.PUBLIC }
                } ?: GroupPrivacy.PUBLIC
            )
        } catch (e: Exception) {
            null
        }
    }
}


// @Parcelize
// Temporarily comment out Parcelable data classes to avoid dependency issues
// Move to model package when ready
/*
data class CommunityParticipation(
    val memorialId: String = "",
    val totalParticipants: Long = 0,
    val totalPrayers: Long = 0,
    val participantsByRegion: Map<String, Long> = emptyMap(),
    val recentSessions: List<PrayerSession> = emptyList(),
    val averageSessionDuration: Long = 0
) // : Parcelable

// @Parcelize
data class CommunityPrayerGroup(
    val id: String = "",
    val memorialId: String = "",
    val organizerId: String = "",
    val groupName: String = "",
    val participants: List<String> = emptyList(),
    val scheduledTime: Date = Date(),
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val isActive: Boolean = true,
    val maxParticipants: Int = 50,
    val actualParticipants: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val completedAt: Date? = null
)
*/