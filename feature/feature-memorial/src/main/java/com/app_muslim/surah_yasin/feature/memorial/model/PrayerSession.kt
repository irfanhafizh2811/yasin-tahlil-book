package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PrayerSession(
    val id: String = "",
    val memorialId: String = "",
    val participantId: String = "",
    val participantName: String = "",
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val duration: Long = 0, // in seconds
    val isCompleted: Boolean = false,
    val recitationCount: Int = 0, // Number of times prayer was recited
    val notes: String = "",
    val location: PrayerLocation? = null,
    val qiblaDirection: Float = 0f, // Direction in degrees
    val isVerified: Boolean = false, // Community verification
    val verificationSource: VerificationSource = VerificationSource.SELF_REPORTED,
    val participationLevel: ParticipationLevel = ParticipationLevel.INDIVIDUAL,
    val metadata: PrayerSessionMetadata = PrayerSessionMetadata()
) : Parcelable

@Parcelize
data class PrayerLocation(
    val city: String = "",
    val country: String = "",
    val mosque: String? = null,
    val isAtMosque: Boolean = false
) : Parcelable

@Parcelize
data class PrayerSessionMetadata(
    val deviceInfo: String = "",
    val appVersion: String = "",
    val sessionQuality: SessionQuality = SessionQuality.NORMAL,
    val backgroundNoise: Boolean = false,
    val interruptions: Int = 0,
    val prayerIntention: String = "", // Niyyah in Arabic/Local language
    val spiritualState: SpiritualState = SpiritualState.FOCUSED,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

enum class VerificationSource {
    SELF_REPORTED,
    COMMUNITY_WITNESS,
    IMAM_VERIFICATION,
    FAMILY_CONFIRMATION,
    AUTO_DETECTED
}

enum class ParticipationLevel {
    INDIVIDUAL,
    FAMILY_GROUP,
    COMMUNITY_GROUP,
    MOSQUE_CONGREGATION
}

enum class SessionQuality {
    EXCELLENT,
    GOOD,
    NORMAL,
    INTERRUPTED,
    INCOMPLETE
}

enum class SpiritualState {
    DEEPLY_FOCUSED,
    FOCUSED,
    CALM,
    DISTRACTED,
    EMOTIONAL,
    GRIEVING
}

@Parcelize
data class PrayerTrackingStats(
    val totalSessions: Long = 0,
    val totalDuration: Long = 0, // in seconds
    val averageSessionDuration: Long = 0,
    val completedSessions: Long = 0,
    val completionRate: Float = 0f,
    val longestSession: Long = 0,
    val consecutiveDays: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val sessionsByPrayerType: Map<PrayerType, Long> = emptyMap(),
    val sessionsByTime: Map<String, Long> = emptyMap(), // Hour of day -> count
    val weeklyAverage: Float = 0f,
    val monthlyProgress: Map<String, Long> = emptyMap(), // Month -> total sessions
    val lastSessionDate: Date? = null,
    val firstSessionDate: Date? = null,
    val personalBests: PrayerPersonalBests = PrayerPersonalBests()
) : Parcelable

@Parcelize
data class PrayerPersonalBests(
    val longestSingleSession: Long = 0, // in seconds
    val mostSessionsInDay: Int = 0,
    val mostConsecutiveDays: Int = 0,
    val highestRecitationCount: Int = 0,
    val earliestPrayerTime: String = "", // Time format
    val latestPrayerTime: String = ""
) : Parcelable

@Parcelize
data class IslamicPrayerTimes(
    val fajr: String = "",
    val sunrise: String = "",
    val dhuhr: String = "",
    val asr: String = "",
    val maghrib: String = "",
    val isha: String = "",
    val qiblaDirection: Float = 0f,
    val hijriDate: HijriDate = HijriDate(),
    val islamicEvents: List<IslamicEvent> = emptyList()
) : Parcelable

@Parcelize
data class IslamicEvent(
    val name: String = "",
    val nameArabic: String = "",
    val description: String = "",
    val date: Date = Date(),
    val type: IslamicEventType = IslamicEventType.GENERAL,
    val significance: EventSignificance = EventSignificance.MEDIUM,
    val recommendedActions: List<String> = emptyList()
) : Parcelable

enum class IslamicEventType {
    RAMADAN,
    EID,
    HAJJ,
    LAYLAT_AL_QADR,
    ASHURA,
    MAWLID,
    GENERAL,
    MEMORIAL_ANNIVERSARY,
    FRIDAY_PRAYER
}

enum class EventSignificance {
    HIGH,
    MEDIUM,
    LOW,
    PERSONAL
}

@Parcelize
data class CommunityParticipation(
    val memorialId: String = "",
    val totalParticipants: Int = 0,
    val totalSessions: Int = 0,
    val totalPrayers: Int = 0,
    val averageSessionDuration: Long = 0,
    val participantsByRegion: Map<String, Int> = emptyMap(),
    val sessionsByDay: Map<String, Int> = emptyMap(),
    val topParticipants: List<ParticipantStats> = emptyList(),
    val recentSessions: List<PrayerSession> = emptyList(),
    val participationTrend: ParticipationTrend = ParticipationTrend.STABLE,
    val lastUpdated: Date = Date()
) : Parcelable

@Parcelize
data class ParticipantStats(
    val participantId: String = "",
    val participantName: String = "",
    val totalSessions: Int = 0,
    val totalPrayers: Int = 0,
    val averageDuration: Long = 0,
    val lastSessionDate: Date? = null,
    val rank: Int = 0
) : Parcelable

enum class ParticipationTrend {
    INCREASING,
    STABLE,
    DECREASING
}

@Parcelize
data class CommunityPrayerGroup(
    val id: String = "",
    val memorialId: String = "",
    val name: String = "",
    val description: String = "",
    val organizer: String = "",
    val members: List<GroupMember> = emptyList(),
    val scheduledSessions: List<ScheduledPrayerSession> = emptyList(),
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val maxMembers: Int = 50,
    val privacy: GroupPrivacy = GroupPrivacy.PUBLIC,
    val requirements: GroupRequirements = GroupRequirements()
) : Parcelable

@Parcelize
data class GroupMember(
    val userId: String = "",
    val name: String = "",
    val role: GroupRole = GroupRole.MEMBER,
    val joinedAt: Date = Date(),
    val isActive: Boolean = true,
    val totalSessions: Int = 0
) : Parcelable

@Parcelize
data class ScheduledPrayerSession(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val scheduledTime: Date = Date(),
    val duration: Long = 0,
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val participants: List<String> = emptyList(),
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val createdBy: String = ""
) : Parcelable

enum class GroupRole {
    ORGANIZER,
    MODERATOR,
    MEMBER
}

enum class GroupPrivacy {
    PUBLIC,
    PRIVATE,
    INVITE_ONLY
}

enum class SessionStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

@Parcelize
data class GroupRequirements(
    val minimumAge: Int = 0,
    val requiresVerification: Boolean = false,
    val allowsGuests: Boolean = true,
    val requiresIntroduction: Boolean = false,
    val moderatorApproval: Boolean = false
) : Parcelable

enum class TimeRange {
    LAST_24_HOURS,
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS,
    LAST_YEAR,
    ALL_TIME
}