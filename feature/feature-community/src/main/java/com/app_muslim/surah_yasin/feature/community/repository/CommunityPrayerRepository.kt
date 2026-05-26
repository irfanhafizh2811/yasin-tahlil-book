package com.app_muslim.surah_yasin.feature.community.repository

import com.app_muslim.surah_yasin.feature.community.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.Timestamp
import java.time.ZonedDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for Community Prayer Features
 * Handles real-time community engagement and global prayer statistics
 */
interface CommunityPrayerRepository {
    // Global Statistics
    fun getGlobalPrayerStatsFlow(): Flow<GlobalPrayerStats>
    fun getRegionalStatsFlow(limit: Int = 20): Flow<List<RegionalPrayerStats>>
    
    // Community Sessions
    fun getActiveSessionsFlow(regionCode: String? = null): Flow<List<CommunityPrayerSession>>
    fun getSessionFlow(sessionId: String): Flow<CommunityPrayerSession?>
    suspend fun createCommunitySession(session: CommunityPrayerSession): Result<String>
    suspend fun joinSession(sessionId: String, participant: PrayerParticipant): Result<Unit>
    suspend fun leaveSession(sessionId: String, userId: String): Result<Unit>
    suspend fun updateSessionProgress(sessionId: String, prayerCount: Int): Result<Unit>
    
    // Activity Feed
    fun getPrayerActivityFlow(regionCode: String? = null, limit: Int = 50): Flow<List<PrayerActivityItem>>
    suspend fun addActivityItem(activity: PrayerActivityItem): Result<Unit>
    
    // Leaderboards
    fun getLeaderboardFlow(timeFrame: LeaderboardTimeFrame, regionCode: String? = null): Flow<List<PrayerLeaderboardEntry>>
    fun getPrayerLeaderboardFlow(timeFrame: LeaderboardTimeFrame, region: String? = null, limit: Int = 100): Flow<List<PrayerLeaderboardEntry>>
    fun getUserRankFlow(userId: String, timeFrame: LeaderboardTimeFrame): Flow<PrayerLeaderboardEntry?>
    suspend fun updateUserStats(userId: String, prayerCount: Int, prayerType: CommunityPrayerType): Result<Unit>
    
    // Events
    fun getCommunityEventsFlow(regionCode: String? = null): Flow<List<CommunityPrayerEvent>>
    suspend fun createCommunityEvent(event: CommunityPrayerEvent): Result<String>
    
    // User Achievements
    suspend fun checkAndAwardBadges(userId: String): Result<List<PrayerBadge>>
    fun getUserBadgesFlow(userId: String): Flow<List<PrayerBadge>>
    
    // Global Statistics (P5.A)
    fun getCountryPrayerStatsFlow(): Flow<List<CountryPrayerStats>>
    fun getDailyPrayerAnalyticsFlow(daysBack: Int = 30): Flow<List<DailyPrayerAnalytics>>
    fun getWeeklyPrayerAnalyticsFlow(weeksBack: Int = 12): Flow<List<WeeklyPrayerAnalytics>>
    fun getGlobalMilestonesFlow(): Flow<List<GlobalMilestone>>
}

/**
 * Firebase implementation of Community Prayer Repository
 * Provides real-time community features with Firestore
 */
@Singleton
class CommunityPrayerFirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : CommunityPrayerRepository {
    
    companion object {
        private const val COMMUNITY_STATS_COLLECTION = "community_stats"
        private const val REGIONAL_STATS_COLLECTION = "regional_stats"
        private const val COMMUNITY_SESSIONS_COLLECTION = "community_sessions"
        private const val COMMUNITY_PARTICIPANTS_COLLECTION = "session_participants"
        private const val PRAYER_ACTIVITY_COLLECTION = "prayer_activity"
        private const val LEADERBOARDS_COLLECTION = "leaderboards"
        private const val COMMUNITY_EVENTS_COLLECTION = "community_events"
        private const val USER_BADGES_COLLECTION = "user_badges"
        private const val USER_COMMUNITY_STATS_COLLECTION = "user_community_stats"
    }
    
    override fun getGlobalPrayerStatsFlow(): Flow<GlobalPrayerStats> {
        return firestore
            .collection(COMMUNITY_STATS_COLLECTION)
            .document("global")
            .snapshots()
            .map { documentSnapshot ->
                val data = documentSnapshot.data ?: emptyMap()
                GlobalPrayerStats(
                    totalActivePrayers = data["totalActivePrayers"] as? Long ?: 0L,
                    totalParticipants = data["totalParticipants"] as? Long ?: 0L,
                    totalPrayersToday = data["totalPrayersToday"] as? Long ?: 0L,
                    totalMemorials = data["totalMemorials"] as? Long ?: 0L,
                    activeRegions = (data["activeRegions"] as? Long ?: 0L).toInt(),
                    topPrayerType = data["topPrayerType"] as? String ?: "Tahlil",
                    lastUpdated = (data["lastUpdated"] as? Timestamp)?.toZonedDateTime() ?: ZonedDateTime.now()
                )
            }
            .catch { e ->
                emit(GlobalPrayerStats())
            }
    }
    
    override fun getRegionalStatsFlow(limit: Int): Flow<List<RegionalPrayerStats>> {
        return firestore
            .collection(REGIONAL_STATS_COLLECTION)
            .orderBy("activePrayers", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapIndexedNotNull { index, doc ->
                    val data = doc.data ?: return@mapIndexedNotNull null
                    RegionalPrayerStats(
                        regionCode = doc.id,
                        regionName = data["regionName"] as? String ?: doc.id,
                        countryCode = data["countryCode"] as? String ?: "",
                        countryName = data["countryName"] as? String ?: "",
                        activePrayers = data["activePrayers"] as? Long ?: 0L,
                        totalParticipants = data["totalParticipants"] as? Long ?: 0L,
                        popularPrayerType = data["popularPrayerType"] as? String ?: "Fatihah",
                        rank = index + 1,
                        percentageOfGlobal = (data["percentageOfGlobal"] as? Double ?: 0.0).toFloat()
                    )
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override fun getActiveSessionsFlow(regionCode: String?): Flow<List<CommunityPrayerSession>> {
        val baseQuery = firestore
            .collection(COMMUNITY_SESSIONS_COLLECTION)
            .whereIn("status", listOf("waiting", "starting", "in_progress"))
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
        
        val query = if (regionCode != null) {
            baseQuery.whereEqualTo("regionCode", regionCode)
        } else baseQuery
        
        return query.snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    createCommunitySessionFromFirestore(doc.data ?: return@mapNotNull null)
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override fun getSessionFlow(sessionId: String): Flow<CommunityPrayerSession?> {
        return firestore
            .collection(COMMUNITY_SESSIONS_COLLECTION)
            .document(sessionId)
            .snapshots()
            .map { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    createCommunitySessionFromFirestore(documentSnapshot.data!!)
                } else null
            }
            .catch { e ->
                emit(null)
            }
    }
    
    override suspend fun createCommunitySession(session: CommunityPrayerSession): Result<String> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val sessionData = mapOf(
                "sessionId" to session.sessionId,
                "memorialId" to session.memorialId,
                "hostUserId" to currentUser.uid,
                "hostDisplayName" to (currentUser.displayName ?: "Anonymous"),
                "prayerType" to session.prayerType.name.lowercase(),
                "isPublic" to session.isPublic,
                "regionCode" to session.regionCode,
                "participantIds" to listOf(currentUser.uid),
                "startTime" to Timestamp.now(),
                "estimatedDuration" to session.estimatedDuration,
                "targetPrayerCount" to session.targetPrayerCount,
                "currentPrayerCount" to 0,
                "status" to "waiting",
                "allowJoinAfterStart" to session.allowJoinAfterStart,
                "maxParticipants" to session.maxParticipants,
                "sessionNotes" to session.sessionNotes,
                "createdAt" to Timestamp.now()
            )
            
            // Use transaction to ensure consistency
            firestore.runTransaction { transaction ->
                val sessionRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(session.sessionId)
                
                transaction.set(sessionRef, sessionData)
                
                // Add host as participant
                val participantRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(session.sessionId)
                    .collection(COMMUNITY_PARTICIPANTS_COLLECTION)
                    .document(currentUser.uid)
                
                transaction.set(participantRef, mapOf(
                    "userId" to currentUser.uid,
                    "displayName" to (currentUser.displayName ?: "Anonymous"),
                    "joinedAt" to Timestamp.now(),
                    "currentPrayerCount" to 0,
                    "isActive" to true,
                    "regionCode" to session.regionCode,
                    "contributionPercentage" to 0.0f
                ))
                
                // Update regional stats
                val regionalRef = firestore
                    .collection(REGIONAL_STATS_COLLECTION)
                    .document(session.regionCode)
                
                transaction.update(regionalRef, mapOf(
                    "activeSessions" to FieldValue.increment(1),
                    "lastActivity" to Timestamp.now()
                ))
                
                null
            }.await()
            
            // Add activity item
            addActivityItem(PrayerActivityItem(
                activityId = "${session.sessionId}_created",
                activityType = ActivityType.SESSION_HOSTED,
                userId = currentUser.uid,
                userDisplayName = currentUser.displayName ?: "Anonymous",
                memorialId = session.memorialId,
                prayerType = session.prayerType,
                regionCode = session.regionCode,
                timestamp = ZonedDateTime.now(),
                message = "Started a ${session.prayerType.displayName} community session"
            ))
            
            Result.success(session.sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun joinSession(sessionId: String, participant: PrayerParticipant): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Check session exists and is joinable
            val sessionDoc = firestore
                .collection(COMMUNITY_SESSIONS_COLLECTION)
                .document(sessionId)
                .get()
                .await()
            
            if (!sessionDoc.exists()) {
                return Result.failure(IllegalArgumentException("Session not found"))
            }
            
            val sessionData = sessionDoc.data!!
            val status = sessionData["status"] as String
            val maxParticipants = sessionData["maxParticipants"] as Long
            val currentParticipants = (sessionData["participantIds"] as List<*>).size
            
            if (status !in listOf("waiting", "starting") && !(sessionData["allowJoinAfterStart"] as Boolean)) {
                return Result.failure(IllegalStateException("Session not accepting new participants"))
            }
            
            if (currentParticipants >= maxParticipants) {
                return Result.failure(IllegalStateException("Session is full"))
            }
            
            // Join session with transaction
            firestore.runTransaction { transaction ->
                // Add participant
                val participantRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                    .collection(COMMUNITY_PARTICIPANTS_COLLECTION)
                    .document(currentUser.uid)
                
                transaction.set(participantRef, mapOf(
                    "userId" to participant.userId,
                    "displayName" to participant.displayName,
                    "joinedAt" to Timestamp.now(),
                    "currentPrayerCount" to 0,
                    "isActive" to true,
                    "regionCode" to participant.regionCode,
                    "contributionPercentage" to 0.0f
                ))
                
                // Update session participant list
                val sessionRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                
                transaction.update(sessionRef, mapOf(
                    "participantIds" to FieldValue.arrayUnion(currentUser.uid)
                ))
                
                null
            }.await()
            
            // Add activity
            addActivityItem(PrayerActivityItem(
                activityId = "${sessionId}_${currentUser.uid}_joined",
                activityType = ActivityType.SESSION_JOINED,
                userId = currentUser.uid,
                userDisplayName = participant.displayName,
                regionCode = participant.regionCode,
                timestamp = ZonedDateTime.now(),
                message = "Joined a community prayer session"
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun leaveSession(sessionId: String, userId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                // Remove participant
                val participantRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                    .collection(COMMUNITY_PARTICIPANTS_COLLECTION)
                    .document(userId)
                
                transaction.delete(participantRef)
                
                // Update session participant list
                val sessionRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                
                transaction.update(sessionRef, mapOf(
                    "participantIds" to FieldValue.arrayRemove(userId)
                ))
                
                null
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSessionProgress(sessionId: String, prayerCount: Int): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            firestore.runTransaction { transaction ->
                // Update participant progress
                val participantRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                    .collection(COMMUNITY_PARTICIPANTS_COLLECTION)
                    .document(currentUser.uid)
                
                transaction.update(participantRef, mapOf(
                    "currentPrayerCount" to prayerCount
                ))
                
                // Update session total
                val sessionRef = firestore
                    .collection(COMMUNITY_SESSIONS_COLLECTION)
                    .document(sessionId)
                
                transaction.update(sessionRef, mapOf(
                    "currentPrayerCount" to FieldValue.increment(1)
                ))
                
                null
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getPrayerActivityFlow(regionCode: String?, limit: Int): Flow<List<PrayerActivityItem>> {
        val baseQuery = firestore
            .collection(PRAYER_ACTIVITY_COLLECTION)
            .whereEqualTo("isPublic", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
        
        val query = if (regionCode != null) {
            baseQuery.whereEqualTo("regionCode", regionCode)
        } else baseQuery
        
        return query.snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    createActivityItemFromFirestore(doc.data ?: return@mapNotNull null)
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override suspend fun addActivityItem(activity: PrayerActivityItem): Result<Unit> {
        return try {
            val activityData = mapOf(
                "activityId" to activity.activityId,
                "activityType" to activity.activityType.name.lowercase(),
                "userId" to activity.userId,
                "userDisplayName" to activity.userDisplayName,
                "memorialId" to activity.memorialId,
                "memorialName" to activity.memorialName,
                "prayerType" to activity.prayerType?.name?.lowercase(),
                "prayerCount" to activity.prayerCount,
                "regionCode" to activity.regionCode,
                "timestamp" to Timestamp.now(),
                "isPublic" to activity.isPublic,
                "message" to activity.message
            )
            
            firestore
                .collection(PRAYER_ACTIVITY_COLLECTION)
                .document(activity.activityId)
                .set(activityData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getLeaderboardFlow(timeFrame: LeaderboardTimeFrame, regionCode: String?): Flow<List<PrayerLeaderboardEntry>> {
        val timeFrameStr = timeFrame.name.lowercase()
        val documentPath = if (regionCode != null) "${regionCode}_$timeFrameStr" else "global_$timeFrameStr"
        
        return firestore
            .collection(LEADERBOARDS_COLLECTION)
            .document(documentPath)
            .collection("entries")
            .orderBy("rank")
            .limit(100)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    createLeaderboardEntryFromFirestore(doc.data ?: return@mapNotNull null)
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override suspend fun updateUserStats(userId: String, prayerCount: Int, prayerType: CommunityPrayerType): Result<Unit> {
        return try {
            val userStatsRef = firestore
                .collection(USER_COMMUNITY_STATS_COLLECTION)
                .document(userId)
            
            userStatsRef.set(mapOf(
                "totalPrayers" to FieldValue.increment(prayerCount.toLong()),
                "totalSessions" to FieldValue.increment(1),
                "lastActivity" to Timestamp.now(),
                "favoriteParticipationType" to prayerType.name.lowercase(),
                "currentStreak" to FieldValue.increment(1) // Simplified streak logic
            ), com.google.firebase.firestore.SetOptions.merge()).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getPrayerLeaderboardFlow(timeFrame: LeaderboardTimeFrame, region: String?, limit: Int): Flow<List<PrayerLeaderboardEntry>> {
        return getLeaderboardFlow(timeFrame, region)
    }
    
    override fun getUserRankFlow(userId: String, timeFrame: LeaderboardTimeFrame): Flow<PrayerLeaderboardEntry?> {
        val timeFrameStr = timeFrame.name.lowercase()
        
        return firestore
            .collection(LEADERBOARDS_COLLECTION)
            .document("global_$timeFrameStr")
            .collection("entries")
            .whereEqualTo("userId", userId)
            .limit(1)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.firstOrNull()?.let { doc ->
                    createLeaderboardEntryFromFirestore(doc.data ?: return@map null)
                }
            }
            .catch { e ->
                emit(null)
            }
    }
    
    override fun getCommunityEventsFlow(regionCode: String?): Flow<List<CommunityPrayerEvent>> {
        val baseQuery = firestore
            .collection(COMMUNITY_EVENTS_COLLECTION)
            .whereGreaterThan("endTime", Timestamp.now())
            .orderBy("endTime")
            .orderBy("startTime")
            .limit(20)
        
        return baseQuery.snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    createCommunityEventFromFirestore(doc.data ?: return@mapNotNull null)
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override suspend fun createCommunityEvent(event: CommunityPrayerEvent): Result<String> {
        return try {
            val eventData = mapOf(
                "eventId" to event.eventId,
                "title" to event.title,
                "description" to event.description,
                "eventType" to event.eventType.name.lowercase(),
                "startTime" to Timestamp.now(), // Will be set to actual start time
                "endTime" to Timestamp.now(), // Will be set to actual end time
                "prayerType" to event.prayerType.name.lowercase(),
                "isGlobal" to event.isGlobal,
                "targetRegions" to event.targetRegions,
                "organizer" to event.organizer,
                "maxParticipants" to event.maxParticipants,
                "currentParticipants" to 0,
                "specialNotes" to event.specialNotes,
                "imageUrl" to event.imageUrl
            )
            
            firestore
                .collection(COMMUNITY_EVENTS_COLLECTION)
                .document(event.eventId)
                .set(eventData)
                .await()
            
            Result.success(event.eventId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkAndAwardBadges(userId: String): Result<List<PrayerBadge>> {
        return try {
            // Simplified badge checking - in real implementation, this would be more complex
            val userStats = firestore
                .collection(USER_COMMUNITY_STATS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            val badges = mutableListOf<PrayerBadge>()
            
            if (userStats.exists()) {
                val data = userStats.data!!
                val totalPrayers = data["totalPrayers"] as? Long ?: 0L
                val totalSessions = data["totalSessions"] as? Long ?: 0L
                
                // Check for milestone badges
                if (totalPrayers >= 100L) {
                    badges.add(PrayerBadge(
                        badgeId = "prayer_milestone_100",
                        name = "Prayer Devotee",
                        description = "Completed 100 prayers",
                        iconUrl = "",
                        category = BadgeCategory.MILESTONE,
                        level = BadgeLevel.BRONZE,
                        earnedAt = ZonedDateTime.now(),
                        requirements = "Complete 100 prayers"
                    ))
                }
                
                if (totalSessions >= 10L) {
                    badges.add(PrayerBadge(
                        badgeId = "session_participant_10",
                        name = "Community Member",
                        description = "Participated in 10 sessions",
                        iconUrl = "",
                        category = BadgeCategory.PARTICIPATION,
                        level = BadgeLevel.SILVER,
                        earnedAt = ZonedDateTime.now(),
                        requirements = "Participate in 10 sessions"
                    ))
                }
            }
            
            Result.success(badges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getUserBadgesFlow(userId: String): Flow<List<PrayerBadge>> {
        return firestore
            .collection(USER_BADGES_COLLECTION)
            .document(userId)
            .collection("badges")
            .orderBy("earnedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    createBadgeFromFirestore(doc.data ?: return@mapNotNull null)
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    // Helper functions to convert Firestore data to model objects
    /**
     * Get country-wise prayer statistics for world map visualization
     */
    override fun getCountryPrayerStatsFlow(): Flow<List<CountryPrayerStats>> {
        return firestore
            .collection("country_stats")
            .orderBy("total_prayers", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        CountryPrayerStats(
                            countryCode = document.getString("country_code") ?: "",
                            countryName = document.getString("country_name") ?: "",
                            totalPrayers = document.getLong("total_prayers") ?: 0L,
                            activeParticipants = document.getLong("active_participants") ?: 0L,
                            popularPrayerTypes = (document.get("popular_prayer_types") as? List<Map<String, Any>>)?.map { 
                                PrayerTypeCount(
                                    prayerType = CommunityPrayerType.valueOf(it["prayer_type"] as String),
                                    count = (it["count"] as Number).toLong(),
                                    percentage = (it["percentage"] as Number).toFloat()
                                )
                            } ?: emptyList(),
                            flag = document.getString("flag") ?: "",
                            heatLevel = document.getDouble("heat_level")?.toFloat() ?: 0.0f,
                            rank = document.getLong("rank")?.toInt() ?: 0,
                            lastActiveAt = (document.getTimestamp("last_active_at")?.toDate()?.let { 
                                ZonedDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault()) 
                            }) ?: ZonedDateTime.now()
                        )
                    } catch (e: Exception) {
                        null // Skip malformed documents
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }

    /**
     * Get daily prayer analytics for the last 30 days
     */
    override fun getDailyPrayerAnalyticsFlow(daysBack: Int): Flow<List<DailyPrayerAnalytics>> {
        val startDate = LocalDate.now().minusDays(daysBack.toLong())
        
        return firestore
            .collection("daily_analytics")
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .orderBy("date", Query.Direction.ASCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        DailyPrayerAnalytics(
                            date = document.getString("date") ?: "",
                            totalPrayers = document.getLong("total_prayers") ?: 0L,
                            uniqueParticipants = document.getLong("unique_participants") ?: 0L,
                            averageSessionDuration = document.getDouble("average_session_duration") ?: 0.0,
                            prayerTypeBreakdown = (document.get("prayer_type_breakdown") as? Map<String, Long>)?.mapKeys { 
                                CommunityPrayerType.valueOf(it.key) 
                            } ?: emptyMap(),
                            peakHour = document.getLong("peak_hour")?.toInt() ?: 12,
                            regionsActive = document.getLong("regions_active")?.toInt() ?: 0,
                            newMemorials = document.getLong("new_memorials") ?: 0L,
                            completedSessions = document.getLong("completed_sessions") ?: 0L
                        )
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
     * Get weekly prayer analytics for the last 12 weeks
     */
    override fun getWeeklyPrayerAnalyticsFlow(weeksBack: Int): Flow<List<WeeklyPrayerAnalytics>> {
        val startWeek = LocalDate.now().minusWeeks(weeksBack.toLong()).with(DayOfWeek.MONDAY)
        
        return firestore
            .collection("weekly_analytics")
            .whereGreaterThanOrEqualTo("week_start_date", startWeek.toString())
            .orderBy("week_start_date", Query.Direction.ASCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        WeeklyPrayerAnalytics(
                            weekStartDate = document.getString("week_start_date") ?: "",
                            totalPrayers = document.getLong("total_prayers") ?: 0L,
                            averageDailyPrayers = document.getDouble("average_daily_prayers") ?: 0.0,
                            uniqueParticipants = document.getLong("unique_participants") ?: 0L,
                            growthRate = document.getDouble("growth_rate")?.toFloat() ?: 0.0f,
                            topRegions = emptyList(), // This would need a separate query
                            dailyBreakdown = emptyList(), // This would need a separate query
                            milestones = emptyList() // This would need a separate query
                        )
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
     * Get current active global milestones
     */
    override fun getGlobalMilestonesFlow(): Flow<List<GlobalMilestone>> {
        return firestore
            .collection("global_milestones")
            .whereEqualTo("is_active", true)
            .orderBy("target_value", Query.Direction.ASCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    try {
                        GlobalMilestone(
                            milestoneId = document.id,
                            type = MilestoneType.valueOf(document.getString("type") ?: "TOTAL_PRAYERS"),
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: "",
                            targetValue = document.getLong("target_value") ?: 0L,
                            currentValue = document.getLong("current_value") ?: 0L,
                            achievedAt = document.getTimestamp("achieved_at")?.toDate()?.let {
                                ZonedDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
                            },
                            isCompleted = document.getBoolean("is_completed") ?: false,
                            celebrationMessage = document.getString("celebration_message") ?: "",
                            participatingCountries = (document.get("participating_countries") as? List<String>) ?: emptyList(),
                            icon = document.getString("icon") ?: "🎉"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }

    // Helper methods
    private fun createCommunitySessionFromFirestore(data: Map<String, Any>): CommunityPrayerSession {
        return CommunityPrayerSession(
            sessionId = data["sessionId"] as String,
            memorialId = data["memorialId"] as String,
            hostUserId = data["hostUserId"] as String,
            hostDisplayName = data["hostDisplayName"] as String,
            prayerType = CommunityPrayerType.valueOf(data["prayerType"].toString().uppercase()),
            isPublic = data["isPublic"] as? Boolean ?: true,
            regionCode = data["regionCode"] as String,
            participants = emptyList(), // Will be loaded separately if needed
            startTime = (data["startTime"] as Timestamp).toZonedDateTime(),
            estimatedDuration = data["estimatedDuration"] as Long,
            targetPrayerCount = (data["targetPrayerCount"] as Long).toInt(),
            currentPrayerCount = (data["currentPrayerCount"] as? Long ?: 0L).toInt(),
            status = SessionStatus.valueOf(data["status"].toString().uppercase()),
            allowJoinAfterStart = data["allowJoinAfterStart"] as? Boolean ?: true,
            maxParticipants = (data["maxParticipants"] as? Long ?: 100L).toInt(),
            sessionNotes = data["sessionNotes"] as? String,
            createdAt = (data["createdAt"] as Timestamp).toZonedDateTime()
        )
    }
    
    private fun createActivityItemFromFirestore(data: Map<String, Any>): PrayerActivityItem {
        return PrayerActivityItem(
            activityId = data["activityId"] as String,
            activityType = ActivityType.valueOf(data["activityType"].toString().uppercase()),
            userId = data["userId"] as String,
            userDisplayName = data["userDisplayName"] as String,
            memorialId = data["memorialId"] as? String,
            memorialName = data["memorialName"] as? String,
            prayerType = data["prayerType"]?.let { CommunityPrayerType.valueOf(it.toString().uppercase()) },
            prayerCount = (data["prayerCount"] as? Long)?.toInt(),
            regionCode = data["regionCode"] as String,
            timestamp = (data["timestamp"] as Timestamp).toZonedDateTime(),
            isPublic = data["isPublic"] as? Boolean ?: true,
            message = data["message"] as String
        )
    }
    
    private fun createLeaderboardEntryFromFirestore(data: Map<String, Any>): PrayerLeaderboardEntry {
        return PrayerLeaderboardEntry(
            userId = data["userId"] as String,
            displayName = data["displayName"] as String,
            profilePictureUrl = data["profilePictureUrl"] as? String,
            rank = (data["rank"] as Long).toInt(),
            totalPrayers = data["totalPrayers"] as Long,
            totalSessions = data["totalSessions"] as Long,
            regionCode = data["regionCode"] as String,
            regionName = data["regionName"] as String,
            favoriteParticipationType = CommunityPrayerType.valueOf(data["favoriteParticipationType"].toString().uppercase()),
            currentStreak = (data["currentStreak"] as? Long ?: 0L).toInt(),
            badges = emptyList(), // Load separately if needed
            joinedCommunitySince = (data["joinedCommunitySince"] as Timestamp).toZonedDateTime()
        )
    }
    
    private fun createCommunityEventFromFirestore(data: Map<String, Any>): CommunityPrayerEvent {
        return CommunityPrayerEvent(
            eventId = data["eventId"] as String,
            title = data["title"] as String,
            description = data["description"] as String,
            eventType = CommunityEventType.valueOf(data["eventType"].toString().uppercase()),
            startTime = (data["startTime"] as Timestamp).toZonedDateTime(),
            endTime = (data["endTime"] as Timestamp).toZonedDateTime(),
            prayerType = CommunityPrayerType.valueOf(data["prayerType"].toString().uppercase()),
            isGlobal = data["isGlobal"] as? Boolean ?: false,
            targetRegions = (data["targetRegions"] as? List<*>)?.map { it.toString() } ?: emptyList(),
            organizer = data["organizer"] as String,
            maxParticipants = (data["maxParticipants"] as? Long)?.toInt(),
            currentParticipants = (data["currentParticipants"] as? Long ?: 0L).toInt(),
            specialNotes = data["specialNotes"] as? String,
            imageUrl = data["imageUrl"] as? String
        )
    }
    
    private fun createBadgeFromFirestore(data: Map<String, Any>): PrayerBadge {
        return PrayerBadge(
            badgeId = data["badgeId"] as String,
            name = data["name"] as String,
            description = data["description"] as String,
            iconUrl = data["iconUrl"] as String,
            category = BadgeCategory.valueOf(data["category"].toString().uppercase()),
            level = BadgeLevel.valueOf(data["level"].toString().uppercase()),
            earnedAt = (data["earnedAt"] as Timestamp).toZonedDateTime(),
            requirements = data["requirements"] as String
        )
    }
    
    private fun Timestamp.toZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds, nanoseconds.toLong()), ZoneId.systemDefault())
    }
}