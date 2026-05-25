package com.app_muslim.surah_yasin.feature.memorial.prayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Prayer types specifically for memorial prayers
 * Based on authentic Islamic traditions for remembering the deceased
 */
enum class MemorialPrayerType(val displayName: String, val arabicName: String, val defaultTarget: Int) {
    TAHLIL("Tahlil", "تهليل", 100),           // La ilaha illa Allah
    YASIN("Surah Yasin", "سورة يس", 1),       // Complete Surah recitation
    FATIHAH("Al-Fatihah", "الفاتحة", 7),      // Opening chapter
    DUA("Du'a", "دعاء", 10),                  // Personal supplications
    ISTIGHFAR("Istighfar", "استغفار", 100),   // Seeking forgiveness
    SALAWAT("Salawat", "صلوات", 100)          // Blessings on Prophet
}

/**
 * Prayer session state for UI management
 */
enum class PrayerSessionState {
    NOT_STARTED,
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    CANCELLED
}

/**
 * Memorial prayer session data class
 * Tracks individual prayer sessions for specific memorials
 */
data class MemorialPrayerSession(
    val id: String = "",
    val memorialId: String = "",
    val userId: String = "",
    val prayerType: MemorialPrayerType = MemorialPrayerType.TAHLIL,
    val currentCount: Int = 0,
    val targetCount: Int = prayerType.defaultTarget,
    val state: PrayerSessionState = PrayerSessionState.NOT_STARTED,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val pausedDuration: Long = 0L, // milliseconds paused
    val isCompleted: Boolean = false,
    val memorialName: String = "", // For display purposes
    val memorialPhotoUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    /**
     * Calculate session duration in minutes
     */
    val sessionDuration: Long
        get() = if (startTime != null && endTime != null) {
            (endTime!!.time - startTime!!.time - pausedDuration) / 1000 / 60
        } else 0L

    /**
     * Calculate progress percentage
     */
    val progress: Float
        get() = if (targetCount > 0) currentCount.toFloat() / targetCount.toFloat() else 0f

    /**
     * Check if session can be resumed
     */
    val canResume: Boolean
        get() = state == PrayerSessionState.PAUSED && currentCount < targetCount

    /**
     * Check if session is active (in progress or paused)
     */
    val isActive: Boolean
        get() = state == PrayerSessionState.IN_PROGRESS || state == PrayerSessionState.PAUSED
}

/**
 * Prayer session statistics for analytics
 */
data class PrayerSessionStats(
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val totalPrayers: Int = 0,
    val averageSessionDuration: Long = 0L, // in minutes
    val favoriteMemorial: String? = null,
    val favoritePrayerType: MemorialPrayerType? = null,
    val streakDays: Int = 0,
    val thisWeekSessions: Int = 0,
    val thisMonthSessions: Int = 0,
    val lastSessionDate: Date? = null
) {
    /**
     * Completion rate percentage
     */
    val completionRate: Float
        get() = if (totalSessions > 0) completedSessions.toFloat() / totalSessions.toFloat() else 0f
}

/**
 * Prayer counter UI state
 */
data class PrayerCounterUiState(
    val session: MemorialPrayerSession? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCompletionDialog: Boolean = false,
    val stats: PrayerSessionStats = PrayerSessionStats(),
    val recentSessions: List<MemorialPrayerSession> = emptyList(),
    val canStartNewSession: Boolean = true
)

/**
 * Room entity for offline prayer session storage
 */
@Entity(tableName = "memorial_prayer_sessions")
data class MemorialPrayerSessionEntity(
    @PrimaryKey
    val id: String,
    val memorialId: String,
    val userId: String,
    val prayerType: String, // MemorialPrayerType.name
    val currentCount: Int,
    val targetCount: Int,
    val state: String, // PrayerSessionState.name
    val startTime: Long?, // timestamp
    val endTime: Long?, // timestamp
    val pausedDuration: Long,
    val isCompleted: Boolean,
    val memorialName: String,
    val memorialPhotoUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

/**
 * Extension functions for conversions
 */
fun MemorialPrayerSession.toEntity(): MemorialPrayerSessionEntity {
    return MemorialPrayerSessionEntity(
        id = id,
        memorialId = memorialId,
        userId = userId,
        prayerType = prayerType.name,
        currentCount = currentCount,
        targetCount = targetCount,
        state = state.name,
        startTime = startTime?.time,
        endTime = endTime?.time,
        pausedDuration = pausedDuration,
        isCompleted = isCompleted,
        memorialName = memorialName,
        memorialPhotoUrl = memorialPhotoUrl,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time
    )
}

fun MemorialPrayerSessionEntity.toDomain(): MemorialPrayerSession {
    return MemorialPrayerSession(
        id = id,
        memorialId = memorialId,
        userId = userId,
        prayerType = MemorialPrayerType.valueOf(prayerType),
        currentCount = currentCount,
        targetCount = targetCount,
        state = PrayerSessionState.valueOf(state),
        startTime = startTime?.let { Date(it) },
        endTime = endTime?.let { Date(it) },
        pausedDuration = pausedDuration,
        isCompleted = isCompleted,
        memorialName = memorialName,
        memorialPhotoUrl = memorialPhotoUrl,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt)
    )
}

/**
 * Prayer text content for display
 */
data class PrayerTextContent(
    val prayerType: MemorialPrayerType,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val meaning: String,
    val references: List<String> = emptyList()
)

/**
 * Predefined prayer texts for memorial prayers
 */
object MemorialPrayerTexts {
    val TAHLIL = PrayerTextContent(
        prayerType = MemorialPrayerType.TAHLIL,
        arabicText = "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ",
        transliteration = "Lā ilāha illā Allāh",
        translation = "There is no god except Allah",
        meaning = "Declaration of God's oneness and absolute sovereignty"
    )
    
    val ISTIGHFAR = PrayerTextContent(
        prayerType = MemorialPrayerType.ISTIGHFAR,
        arabicText = "أَسْتَغْفِرُ ٱللَّٰهَ ٱلْعَظِيمَ",
        transliteration = "Astaghfiru Allāha al-'Aẓīm",
        translation = "I seek forgiveness from Allah, the Most Great",
        meaning = "Seeking Allah's forgiveness for the deceased and ourselves"
    )
    
    val SALAWAT = PrayerTextContent(
        prayerType = MemorialPrayerType.SALAWAT,
        arabicText = "ٱللَّٰهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
        transliteration = "Allāhumma ṣalli 'alā Muḥammadin wa 'alā āli Muḥammad",
        translation = "O Allah, send blessings upon Muhammad and the family of Muhammad",
        meaning = "Sending blessings upon the Prophet for spiritual benefit"
    )
    
    fun getTextForPrayerType(type: MemorialPrayerType): PrayerTextContent {
        return when (type) {
            MemorialPrayerType.TAHLIL -> TAHLIL
            MemorialPrayerType.ISTIGHFAR -> ISTIGHFAR
            MemorialPrayerType.SALAWAT -> SALAWAT
            else -> TAHLIL // Default fallback
        }
    }
}