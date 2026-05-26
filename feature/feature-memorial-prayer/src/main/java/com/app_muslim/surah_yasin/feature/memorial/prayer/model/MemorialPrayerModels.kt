package com.app_muslim.surah_yasin.feature.memorial.prayer.model

import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * Data models for Memorial Prayer feature
 * Comprehensive model set for prayer sessions, tracking, and Islamic memorial practices
 */

// Prayer Session Models
data class MemorialPrayerSession(
    val sessionId: String,
    val memorialId: String,
    val prayerType: PrayerType,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime? = null,
    val prayerCount: Int = 0,
    val participantCount: Int = 1,
    val isCompleted: Boolean = false,
    val culturalSettings: CulturalSettings,
    val sessionNotes: String? = null
)

data class CulturalSettings(
    val schoolOfThought: SchoolOfThought,
    val language: String,
    val regionCode: String,
    val prayerTradition: PrayerTradition
)

// Islamic Prayer Types
enum class PrayerType(val displayName: String, val arabicName: String, val defaultCount: Int) {
    TAHLIL("Tahlil", "تهليل", 100),
    YASIN("Yasin", "يس", 1),
    FATIHAH("Al-Fatihah", "الفاتحة", 7),
    DUA("Dua", "دعاء", 1),
    ISTIGHFAR("Istighfar", "استغفار", 100),
    CUSTOM("Custom", "مخصص", 33)
}

enum class SchoolOfThought(val displayName: String, val arabicName: String) {
    HANAFI("Hanafi", "حنفي"),
    HANBALI("Hanbali", "حنبلي"),
    MALIKI("Maliki", "مالكي"),
    SHAFII("Shafi'i", "شافعي"),
    JAAFARI("Ja'fari", "جعفري"),
    OTHER("Other", "أخرى")
}

enum class PrayerTradition(val displayName: String) {
    INDIVIDUAL("Individual"),
    COMMUNITY("Community"),
    FAMILY("Family"),
    MOSQUE("Mosque")
}

// Prayer Progress Tracking
data class PrayerProgress(
    val currentCount: Int,
    val targetCount: Int,
    val percentage: Float,
    val estimatedTimeRemaining: Long, // in milliseconds
    val averagePrayerSpeed: Float // prayers per minute
)

// Memorial Prayer Statistics
data class MemorialPrayerStats(
    val totalSessions: Int,
    val totalPrayers: Int,
    val totalTimeSpent: Long, // in milliseconds
    val favoriteTimeOfDay: String,
    val mostUsedPrayerType: PrayerType,
    val longestSession: Long, // in milliseconds
    val currentStreak: Int // consecutive days
)

// Session State Management
sealed class MemorialPrayerState {
    object Idle : MemorialPrayerState()
    object Loading : MemorialPrayerState()
    data class InProgress(val session: MemorialPrayerSession, val progress: PrayerProgress) : MemorialPrayerState()
    data class Paused(val session: MemorialPrayerSession, val progress: PrayerProgress) : MemorialPrayerState()
    data class Completed(val session: MemorialPrayerSession) : MemorialPrayerState()
    data class Error(val message: String, val throwable: Throwable? = null) : MemorialPrayerState()
}

// UI State Models
data class PrayerScreenUiState(
    val memorialPrayerState: MemorialPrayerState = MemorialPrayerState.Idle,
    val availablePrayerTypes: List<PrayerType> = emptyList(),
    val recentSessions: List<MemorialPrayerSession> = emptyList(),
    val statistics: MemorialPrayerStats? = null,
    val isVibrationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Event Models
sealed class MemorialPrayerEvent {
    data class StartSession(val memorialId: String, val prayerType: PrayerType, val targetCount: Int) : MemorialPrayerEvent()
    object PauseSession : MemorialPrayerEvent()
    object ResumeSession : MemorialPrayerEvent()
    object CompleteSession : MemorialPrayerEvent()
    object IncrementPrayer : MemorialPrayerEvent()
    data class UpdateSettings(val culturalSettings: CulturalSettings) : MemorialPrayerEvent()
    object LoadRecentSessions : MemorialPrayerEvent()
    object LoadStatistics : MemorialPrayerEvent()
    data class DeleteSession(val sessionId: String) : MemorialPrayerEvent()
}