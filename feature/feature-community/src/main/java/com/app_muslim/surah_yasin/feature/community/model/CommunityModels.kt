package com.app_muslim.surah_yasin.feature.community.model

import java.time.ZonedDateTime

/**
 * Data models for Community Prayer Features
 * Real-time community engagement, leaderboards, and global prayer participation
 */

// Global Prayer Statistics
data class GlobalPrayerStats(
    val totalActivePrayers: Long = 0L,
    val totalParticipants: Long = 0L,
    val totalPrayersToday: Long = 0L,
    val totalMemorials: Long = 0L,
    val activeRegions: Int = 0,
    val topPrayerType: String = "Tahlil",
    val lastUpdated: ZonedDateTime = ZonedDateTime.now(),
    val dailyGrowth: Float = 0.0f,
    val weeklyGrowth: Float = 0.0f,
    val globalTrend: TrendDirection = TrendDirection.STABLE,
    val peakHour: Int = 12, // Peak prayer hour (24-hour format)
    val totalCountries: Int = 0,
    val totalCities: Int = 0
)

// Enhanced Regional Prayer Statistics with geographical data
data class RegionalPrayerStats(
    val regionCode: String,
    val regionName: String,
    val countryCode: String,
    val countryName: String,
    val activePrayers: Long = 0L,
    val totalParticipants: Long = 0L,
    val popularPrayerType: String = "Fatihah",
    val rank: Int = 0,
    val percentageOfGlobal: Float = 0.0f,
    val timeZone: String = "UTC",
    val currentLocalTime: ZonedDateTime = ZonedDateTime.now(),
    val dailyTrend: TrendDirection = TrendDirection.STABLE,
    val flag: String = ""
)

// Country Prayer Statistics for world map visualization
data class CountryPrayerStats(
    val countryCode: String,
    val countryName: String,
    val totalPrayers: Long = 0L,
    val activeParticipants: Long = 0L,
    val popularPrayerTypes: List<PrayerTypeCount> = emptyList(),
    val flag: String = "",
    val heatLevel: Float = 0.0f, // 0.0 to 1.0 for heat map coloring
    val rank: Int = 0,
    val lastActiveAt: ZonedDateTime = ZonedDateTime.now()
)

// Prayer analytics models
data class DailyPrayerAnalytics(
    val date: String, // YYYY-MM-DD format
    val totalPrayers: Long = 0L,
    val uniqueParticipants: Long = 0L,
    val averageSessionDuration: Double = 0.0, // in minutes
    val prayerTypeBreakdown: Map<CommunityPrayerType, Long> = emptyMap(),
    val peakHour: Int = 12,
    val regionsActive: Int = 0,
    val newMemorials: Long = 0L,
    val completedSessions: Long = 0L
)

data class WeeklyPrayerAnalytics(
    val weekStartDate: String, // YYYY-MM-DD format (Monday)
    val totalPrayers: Long = 0L,
    val averageDailyPrayers: Double = 0.0,
    val uniqueParticipants: Long = 0L,
    val growthRate: Float = 0.0f,
    val topRegions: List<RegionalPrayerStats> = emptyList(),
    val dailyBreakdown: List<DailyPrayerAnalytics> = emptyList(),
    val milestones: List<GlobalMilestone> = emptyList()
)

// Global milestones for community celebrations
data class GlobalMilestone(
    val milestoneId: String,
    val type: MilestoneType,
    val title: String,
    val description: String,
    val targetValue: Long,
    val currentValue: Long,
    val achievedAt: ZonedDateTime? = null,
    val isCompleted: Boolean = false,
    val celebrationMessage: String = "",
    val participatingCountries: List<String> = emptyList(),
    val icon: String = "🎉"
)

// Prayer type count for analytics
data class PrayerTypeCount(
    val prayerType: CommunityPrayerType,
    val count: Long,
    val percentage: Float
)

// Trend direction for analytics
enum class TrendDirection {
    INCREASING,
    DECREASING,
    STABLE
}

// Milestone types
enum class MilestoneType {
    TOTAL_PRAYERS,
    GLOBAL_PARTICIPANTS,
    COUNTRIES_REACHED,
    DAILY_PEAK,
    COMMUNITY_SESSIONS,
    MEMORIAL_CREATED,
    FAMILY_SHARING
}

// Community Prayer Session
data class CommunityPrayerSession(
    val sessionId: String,
    val memorialId: String,
    val hostUserId: String,
    val hostDisplayName: String,
    val prayerType: CommunityPrayerType,
    val isPublic: Boolean = true,
    val regionCode: String,
    val participants: List<SessionParticipant> = emptyList(),
    val startTime: ZonedDateTime,
    val estimatedDuration: Long, // in minutes
    val targetPrayerCount: Int,
    val currentPrayerCount: Int = 0,
    val status: SessionStatus = SessionStatus.WAITING,
    val allowJoinAfterStart: Boolean = true,
    val maxParticipants: Int = 100,
    val sessionNotes: String? = null,
    val createdAt: ZonedDateTime = ZonedDateTime.now()
)

// Prayer Participant
data class PrayerParticipant(
    val userId: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val joinedAt: ZonedDateTime,
    val currentPrayerCount: Int = 0,
    val isActive: Boolean = true,
    val regionCode: String,
    val contributionPercentage: Float = 0.0f
)

// Community Prayer Types
enum class CommunityPrayerType(
    val displayName: String,
    val arabicName: String,
    val defaultDuration: Long, // in minutes
    val defaultTarget: Int,
    val description: String
) {
    TAHLIL("Tahlil", "تهليل", 15, 100, "La ilaha illa Allah recitation"),
    YASIN("Yasin", "يس", 20, 1, "Surah Ya-Sin recitation"),
    FATIHAH("Al-Fatihah", "الفاتحة", 5, 7, "Opening chapter recitation"),
    DHIKR("Dhikr", "ذكر", 10, 33, "Remembrance of Allah"),
    DUA("Dua", "دعاء", 5, 1, "Supplication for the deceased"),
    QURAN("Quran", "قرآن", 30, 1, "General Quran recitation"),
    COMMUNITY_PRAYER("Community Prayer", "صلاة الجماعة", 25, 1, "Collective prayer session")
}

// Session Status
enum class SessionStatus {
    WAITING,      // Waiting for participants
    STARTING,     // About to begin
    IN_PROGRESS,  // Active prayer session
    PAUSED,       // Temporarily paused
    COMPLETED,    // Successfully completed
    CANCELLED,    // Cancelled by host
    EXPIRED       // Expired due to inactivity
}

// Prayer Leaderboard Entry
data class PrayerLeaderboardEntry(
    val userId: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val rank: Int,
    val totalPrayers: Long,
    val totalSessions: Long,
    val regionCode: String,
    val regionName: String,
    val favoriteParticipationType: CommunityPrayerType,
    val currentStreak: Int, // consecutive days
    val badges: List<PrayerBadge> = emptyList(),
    val joinedCommunitySince: ZonedDateTime
)

// Prayer Badges/Achievements
data class PrayerBadge(
    val badgeId: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val category: BadgeCategory,
    val level: BadgeLevel,
    val earnedAt: ZonedDateTime,
    val requirements: String
)

enum class BadgeCategory {
    PARTICIPATION,  // Regular participation
    LEADERSHIP,     // Hosting sessions
    DEDICATION,     // Consistency and streaks
    COMMUNITY,      // Social engagement
    MILESTONE,      // Achievement milestones
    SPECIAL         // Special events
}

enum class BadgeLevel {
    BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
}

// Community Prayer Events
data class CommunityPrayerEvent(
    val eventId: String,
    val title: String,
    val description: String,
    val eventType: CommunityEventType,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val prayerType: CommunityPrayerType,
    val isGlobal: Boolean = false,
    val targetRegions: List<String> = emptyList(),
    val organizer: String,
    val maxParticipants: Int? = null,
    val currentParticipants: Int = 0,
    val specialNotes: String? = null,
    val imageUrl: String? = null
)

enum class CommunityEventType {
    GROUP_PRAYER,          // General group prayer session
    MEMORIAL_REMEMBRANCE,  // Special memorial prayer
    FRIDAY_GATHERING,      // Friday special prayers
    RAMADAN_SPECIAL,       // Ramadan community prayers
    HAJJ_PRAYERS,          // Hajj-related prayers
    COMMUNITY_MILESTONE,   // Celebrating achievements
    CHARITY_PRAYER,        // Prayer for charity causes
    GLOBAL_UNITY           // Global unity prayers
}

// Real-time Prayer Activity Feed
data class PrayerActivityItem(
    val activityId: String,
    val activityType: ActivityType,
    val userId: String,
    val userDisplayName: String,
    val memorialId: String? = null,
    val memorialName: String? = null,
    val prayerType: CommunityPrayerType? = null,
    val prayerCount: Int? = null,
    val regionCode: String,
    val timestamp: ZonedDateTime,
    val isPublic: Boolean = true,
    val message: String
)

enum class ActivityType {
    PRAYER_COMPLETED,      // Completed individual prayers
    SESSION_JOINED,        // Joined community session
    SESSION_HOSTED,        // Hosted community session
    MILESTONE_REACHED,     // Achieved prayer milestone
    BADGE_EARNED,          // Earned new badge
    MEMORIAL_CREATED,      // Created new memorial
    STREAK_ACHIEVEMENT     // Achieved prayer streak
}

// UI State Models for Community Features
data class CommunityHomeUiState(
    val globalStats: GlobalPrayerStats = GlobalPrayerStats(),
    val regionalStats: List<RegionalPrayerStats> = emptyList(),
    val activeSessions: List<CommunityPrayerSession> = emptyList(),
    val recentActivity: List<PrayerActivityItem> = emptyList(),
    val upcomingEvents: List<CommunityPrayerEvent> = emptyList(),
    val userRegion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class PrayerLeaderboardUiState(
    val globalLeaderboard: List<PrayerLeaderboardEntry> = emptyList(),
    val regionalLeaderboard: List<PrayerLeaderboardEntry> = emptyList(),
    val userRank: PrayerLeaderboardEntry? = null,
    val selectedTimeFrame: LeaderboardTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
    val selectedRegion: String = "global",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class LeaderboardTimeFrame {
    TODAY, THIS_WEEK, THIS_MONTH, ALL_TIME
}

data class CommunitySessionUiState(
    val session: CommunityPrayerSession? = null,
    val isJoined: Boolean = false,
    val userParticipant: PrayerParticipant? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class ConnectionStatus {
    CONNECTED,
    CONNECTING,
    DISCONNECTED,
    ERROR
}

// Events for Community Features
sealed class CommunityEvent {
    object LoadGlobalStats : CommunityEvent()
    object LoadRegionalStats : CommunityEvent()
    object LoadActiveSessions : CommunityEvent()
    object LoadRecentActivity : CommunityEvent()
    object LoadUpcomingEvents : CommunityEvent()
    data class JoinSession(val sessionId: String) : CommunityEvent()
    data class LeaveSession(val sessionId: String) : CommunityEvent()
    data class CreateSession(val session: CommunityPrayerSession) : CommunityEvent()
    data class UpdatePrayerProgress(val sessionId: String, val prayerCount: Int) : CommunityEvent()
    data class LoadLeaderboard(val timeFrame: LeaderboardTimeFrame, val region: String) : CommunityEvent()
    data class ChangeRegion(val regionCode: String) : CommunityEvent()
}

/**
 * Memorial Interaction Models
 */
data class MemorialInteraction(
    val interactionId: String,
    val memorialId: String,
    val userId: String,
    val userName: String,
    val interactionType: MemorialInteractionType,
    val content: String,
    val timestamp: ZonedDateTime,
    val isPublic: Boolean = true,
    val likes: Int = 0,
    val replies: List<InteractionReply> = emptyList(),
    val isAnonymous: Boolean = false,
    val region: String,
    val language: String = "en"
)

enum class MemorialInteractionType {
    PRAYER_COMPLETION,
    DUA_RECITATION,
    REMEMBRANCE_MESSAGE,
    GRATITUDE_EXPRESSION,
    FAMILY_UPDATE,
    COMMUNITY_SUPPORT
}

data class InteractionReply(
    val replyId: String,
    val userId: String,
    val userName: String,
    val content: String,
    val timestamp: ZonedDateTime,
    val isAnonymous: Boolean = false
)

/**
 * Regional Communities
 */
data class RegionalIslamicCommunity(
    val communityId: String,
    val name: String,
    val nameArabic: String? = null,
    val region: String,
    val country: String,
    val city: String? = null,
    val islamicSchool: IslamicSchoolOfThought = IslamicSchoolOfThought.GENERAL,
    val language: String = "en",
    val memberCount: Long = 0L,
    val activeMemberCount: Long = 0L,
    val totalMemorials: Long = 0L,
    val totalPrayers: Long = 0L,
    val weeklyGoal: Long = 1000L,
    val currentWeekProgress: Long = 0L,
    val isVerified: Boolean = false,
    val createdAt: ZonedDateTime,
    val lastActiveAt: ZonedDateTime,
    val description: String = "",
    val guidelines: List<String> = emptyList(),
    val timeZone: String = "UTC"
)

enum class IslamicSchoolOfThought {
    GENERAL, SUNNI, SHIA, HANAFI, MALIKI, SHAFI, HANBALI
}

enum class CommunitySessionType {
    MEMORIAL_PRAYER, GROUP_DHIKR, QURAN_STUDY, COMMUNITY_DUA, SPECIAL_OCCASION
}

enum class SessionPrivacyLevel {
    PUBLIC, FAMILY_ONLY, INVITED_ONLY, PRIVATE
}

enum class ParticipantRole {
    HOST, PARTICIPANT, MODERATOR, GUEST
}

/**
 * Session Participant Model
 */
data class SessionParticipant(
    val userId: String,
    val displayName: String,
    val role: ParticipantRole = ParticipantRole.PARTICIPANT,
    val joinedAt: ZonedDateTime,
    val currentPrayerCount: Int = 0,
    val isActive: Boolean = true,
    val contributionPercentage: Float = 0.0f,
    val regionCode: String,
    val isAnonymous: Boolean = false
)

/**
 * Prayer Participation Session
 */
data class PrayerParticipationSession(
    val sessionId: String,
    val memorialId: String,
    val participantId: String,
    val prayerType: CommunityPrayerType,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime? = null,
    val completedPrayers: Int = 0,
    val targetPrayers: Int,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val region: String
)

/**
 * Community Engagement Metrics
 */
data class CommunityEngagementMetrics(
    val totalCommunities: Long = 0L,
    val activeCommunities: Long = 0L,
    val totalMembers: Long = 0L,
    val activeMembers: Long = 0L,
    val totalInteractions: Long = 0L,
    val averageParticipationRate: Float = 0.0f,
    val lastCalculated: ZonedDateTime
)

/**
 * Community Events (Not to be confused with the UI Event sealed class)
 */
data class CommunityScheduledEvent(
    val eventId: String,
    val title: String,
    val titleArabic: String? = null,
    val description: String,
    val type: CommunityEventType,
    val scheduledTime: ZonedDateTime,
    val duration: Long, // in minutes
    val communityId: String? = null,
    val region: String,
    val participantLimit: Int? = null,
    val currentParticipants: Int = 0,
    val requirements: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val isActive: Boolean = true
)

// Firebase Data Transfer Objects
data class CommunityStatsFirestore(
    val totalActivePrayers: Long = 0L,
    val totalParticipants: Long = 0L,
    val totalPrayersToday: Long = 0L,
    val totalMemorials: Long = 0L,
    val activeRegions: Int = 0,
    val topPrayerType: String = "tahlil",
    val lastUpdated: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val regionBreakdown: Map<String, Long> = emptyMap()
)

data class SessionFirestore(
    val sessionId: String = "",
    val memorialId: String = "",
    val hostUserId: String = "",
    val hostDisplayName: String = "",
    val prayerType: String = "tahlil",
    val isPublic: Boolean = true,
    val regionCode: String = "",
    val participantIds: List<String> = emptyList(),
    val startTime: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val estimatedDuration: Long = 15L,
    val targetPrayerCount: Int = 100,
    val currentPrayerCount: Int = 0,
    val status: String = "waiting",
    val allowJoinAfterStart: Boolean = true,
    val maxParticipants: Int = 100,
    val sessionNotes: String? = null,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)

// Family Memorial Sharing Models
data class SharedMemorial(
    val memorialId: String,
    val deceasedName: String,
    val photoUrl: String?,
    val sharedByName: String,
    val sharedByUserId: String,
    val familyMembers: List<FamilyMember>,
    val totalPrayers: Long,
    val isActivePrayerSession: Boolean,
    val sharedAt: ZonedDateTime
)

data class PrayerInvitation(
    val invitationId: String,
    val memorialId: String,
    val memorialName: String,
    val inviterName: String,
    val inviterUserId: String,
    val prayerType: CommunityPrayerType,
    val targetPrayerCount: Int,
    val isUrgent: Boolean,
    val acceptedFamilyMembers: List<FamilyMember>,
    val invitedAt: ZonedDateTime
)

data class FamilyMember(
    val userId: String,
    val name: String,
    val profilePhotoUrl: String?,
    val relationshipType: String
)

enum class FamilyTab(val displayName: String) {
    SHARED_MEMORIALS("Shared Memorials"),
    PRAYER_INVITATIONS("Prayer Invitations")
}

/**
 * Memorial Discovery Models
 */
data class MemorialDiscoveryFilter(
    val searchQuery: String = "",
    val prayerType: CommunityPrayerType? = null,
    val region: String? = null,
    val privacyLevel: MemorialPrivacyLevel? = null,
    val sortType: MemorialSortType = MemorialSortType.MOST_RECENT,
    val hasActivePrayers: Boolean? = null,
    val dateRange: DateRange? = null,
    val minPrayerCount: Int? = null,
    val maxResults: Int = 50
)

data class DiscoverableMemorial(
    val memorialId: String,
    val deceasedName: String,
    val deceasedNameArabic: String? = null,
    val photoUrl: String? = null,
    val createdByUserId: String,
    val createdByName: String,
    val privacyLevel: MemorialPrivacyLevel,
    val region: String,
    val totalPrayers: Long = 0L,
    val activePrayerCount: Long = 0L,
    val participantCount: Int = 0,
    val lastPrayerAt: ZonedDateTime? = null,
    val createdAt: ZonedDateTime,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val averageRating: Float = 0.0f,
    val ratingCount: Int = 0
)

enum class MemorialSortType {
    MOST_RECENT,
    MOST_PRAYERS,
    MOST_ACTIVE,
    ALPHABETICAL,
    NEAREST,
    HIGHEST_RATED
}

enum class MemorialPrivacyLevel {
    PUBLIC,
    COMMUNITY,
    FAMILY_ONLY,
    PRIVATE
}

data class DateRange(
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime
)

/**
 * Leaderboard Models
 */
data class LeaderboardCategory(
    val categoryId: String,
    val name: String,
    val description: String,
    val icon: String,
    val sortBy: LeaderboardSortType,
    val isActive: Boolean = true
) {
    companion object {
        val TOTAL_PRAYERS = LeaderboardCategory("total_prayers", "Total Prayers", "Total prayers completed", "🤲", LeaderboardSortType.TOTAL_PRAYERS)
        val MEMORIAL_PARTICIPATION = LeaderboardCategory("memorial_participation", "Memorial Participation", "Memorial sessions joined", "💜", LeaderboardSortType.PARTICIPATION_RATE)
        val COMMUNITY_ENGAGEMENT = LeaderboardCategory("community_engagement", "Community Engagement", "Community interactions", "🤝", LeaderboardSortType.COMMUNITY_CONTRIBUTION)
        val HELPING_FAMILIES = LeaderboardCategory("helping_families", "Helping Families", "Family support provided", "👨‍👩‍👧‍👦", LeaderboardSortType.COMMUNITY_CONTRIBUTION)
        val CONSISTENCY = LeaderboardCategory("consistency", "Consistency", "Consistent prayer streaks", "📈", LeaderboardSortType.STREAK_DAYS)
    }
}

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val rank: Int,
    val score: Long,
    val previousRank: Int = rank,
    val change: RankChange = RankChange.NO_CHANGE,
    val regionCode: String,
    val regionName: String,
    val badges: List<String> = emptyList(),
    val statistics: Map<String, Long> = emptyMap(),
    val lastActiveAt: ZonedDateTime
)

enum class LeaderboardType {
    GLOBAL,
    REGIONAL,
    COMMUNITY,
    FAMILY
}

enum class LeaderboardSortType {
    TOTAL_PRAYERS,
    WEEKLY_PRAYERS,
    MONTHLY_PRAYERS,
    PARTICIPATION_RATE,
    COMMUNITY_CONTRIBUTION,
    STREAK_DAYS
}

enum class RankChange {
    UP, DOWN, NO_CHANGE
}

data class CommunityLeaderboard(
    val leaderboardId: String,
    val type: LeaderboardType,
    val category: LeaderboardCategory,
    val timeFrame: LeaderboardTimeFrame,
    val region: String? = null,
    val entries: List<LeaderboardEntry> = emptyList(),
    val totalEntries: Int = 0,
    val lastUpdated: ZonedDateTime,
    val isRealTime: Boolean = true
)

/**
 * UI State Models for Leaderboards
 */
data class CommunityLeaderboardUiState(
    val leaderboards: List<CommunityLeaderboard> = emptyList(),
    val selectedTimeFrame: LeaderboardTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
    val selectedCategory: LeaderboardCategory? = null,
    val selectedRegion: String = "global",
    val userRank: LeaderboardEntry? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val refreshEnabled: Boolean = true
)

/**
 * Memorial Discovery UI State
 */
data class MemorialDiscoveryUiState(
    val memorials: List<DiscoverableMemorial> = emptyList(),
    val filter: MemorialDiscoveryFilter = MemorialDiscoveryFilter(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val hasMoreResults: Boolean = true,
    val totalResults: Int = 0
)

/**
 * Additional Community Models
 */
data class CommunityPrayerStats(
    val totalPrayers: Long = 0L,
    val weeklyPrayers: Long = 0L,
    val monthlyPrayers: Long = 0L,
    val participationRate: Float = 0.0f,
    val averageSessionDuration: Double = 0.0,
    val topPrayerTypes: List<PrayerTypeCount> = emptyList(),
    val streakDays: Int = 0,
    val totalSessions: Long = 0L,
    val completionRate: Float = 0.0f
)

data class PrayerProgress(
    val sessionId: String,
    val participantId: String,
    val currentCount: Int = 0,
    val targetCount: Int,
    val startTime: ZonedDateTime,
    val lastUpdateTime: ZonedDateTime,
    val isCompleted: Boolean = false,
    val completionPercentage: Float = 0.0f
)