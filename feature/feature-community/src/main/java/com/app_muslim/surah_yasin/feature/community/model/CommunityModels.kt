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
    val lastUpdated: ZonedDateTime = ZonedDateTime.now()
)

// Regional Prayer Statistics  
data class RegionalPrayerStats(
    val regionCode: String,
    val regionName: String,
    val countryCode: String,
    val activePrayers: Long = 0L,
    val totalParticipants: Long = 0L,
    val popularPrayerType: String = "Fatihah",
    val rank: Int = 0,
    val percentageOfGlobal: Float = 0.0f
)

// Community Prayer Session
data class CommunityPrayerSession(
    val sessionId: String,
    val memorialId: String,
    val hostUserId: String,
    val hostDisplayName: String,
    val prayerType: CommunityPrayerType,
    val isPublic: Boolean = true,
    val regionCode: String,
    val participants: List<PrayerParticipant> = emptyList(),
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