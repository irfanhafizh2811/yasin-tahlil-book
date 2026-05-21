package com.app_muslim.surah_yasin.data.model

import java.util.Date
import java.util.UUID

data class MemorialPrayer(
    val id: String = UUID.randomUUID().toString(),
    val memorialId: String,
    val userId: String,
    val prayerType: PrayerType,
    val prayerCount: Int = 0, // Number of prayers completed in this session
    val targetCount: Int = 0, // Target number for this session (e.g., 100x Tahlil)
    val isCompleted: Boolean = false,
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Long = 0, // Duration in milliseconds
    val location: String? = null, // Where the prayer was performed
    val notes: String? = null, // Personal notes or intentions
    val isPrivate: Boolean = false, // Whether this prayer session should be private
    val sessionData: Map<String, Any> = emptyMap(), // Flexible session data
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    
    // Helper methods
    fun isInProgress(): Boolean {
        return !isCompleted && endTime == null
    }
    
    fun getProgressPercentage(): Float {
        return if (targetCount > 0) {
            (prayerCount.toFloat() / targetCount.toFloat() * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }
    }
    
    fun getRemainingCount(): Int {
        return if (targetCount > prayerCount) targetCount - prayerCount else 0
    }
    
    fun getDurationInMinutes(): Long {
        return duration / (1000 * 60)
    }
    
    fun getDurationInSeconds(): Long {
        return duration / 1000
    }
    
    fun getPrayersPerMinute(): Float {
        val minutes = getDurationInMinutes()
        return if (minutes > 0) prayerCount.toFloat() / minutes else 0f
    }
    
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "userId" to userId,
            "prayerType" to prayerType.name,
            "prayerCount" to prayerCount,
            "targetCount" to targetCount,
            "isCompleted" to isCompleted,
            "startTime" to startTime,
            "endTime" to endTime,
            "duration" to duration,
            "location" to location,
            "notes" to notes,
            "isPrivate" to isPrivate,
            "sessionData" to sessionData,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}

enum class PrayerType(
    val displayName: String,
    val arabicName: String,
    val description: String,
    val defaultTargetCount: Int,
    val estimatedTimePerUnit: Long // in seconds
) {
    TAHLIL(
        displayName = "Tahlil",
        arabicName = "تهليل",
        description = "La ilaha illa Allah - Declaration of faith",
        defaultTargetCount = 100,
        estimatedTimePerUnit = 2L
    ),
    YASIN(
        displayName = "Surah Yasin",
        arabicName = "سورة يس",
        description = "Recitation of Surah Yasin",
        defaultTargetCount = 1,
        estimatedTimePerUnit = 900L // 15 minutes
    ),
    FATIHAH(
        displayName = "Al-Fatihah",
        arabicName = "الفاتحة",
        description = "The Opening - First chapter of Quran",
        defaultTargetCount = 7,
        estimatedTimePerUnit = 60L // 1 minute
    ),
    ISTIGHFAR(
        displayName = "Istighfar",
        arabicName = "استغفار",
        description = "Seeking forgiveness from Allah",
        defaultTargetCount = 100,
        estimatedTimePerUnit = 3L
    ),
    SALAWAT(
        displayName = "Salawat",
        arabicName = "صلوات",
        description = "Blessings upon Prophet Muhammad (PBUH)",
        defaultTargetCount = 100,
        estimatedTimePerUnit = 4L
    ),
    DHIKR(
        displayName = "Dhikr",
        arabicName = "ذكر",
        description = "Remembrance of Allah",
        defaultTargetCount = 100,
        estimatedTimePerUnit = 2L
    ),
    QURAN_RECITATION(
        displayName = "Quran Recitation",
        arabicName = "تلاوة القرآن",
        description = "Recitation from the Holy Quran",
        defaultTargetCount = 1,
        estimatedTimePerUnit = 600L // 10 minutes
    ),
    DUA(
        displayName = "Du'a",
        arabicName = "دعاء",
        description = "Supplication for the deceased",
        defaultTargetCount = 1,
        estimatedTimePerUnit = 300L // 5 minutes
    );
    
    fun getEstimatedSessionTime(): Long {
        return estimatedTimePerUnit * defaultTargetCount
    }
    
    fun getEstimatedSessionTimeInMinutes(): Long {
        return getEstimatedSessionTime() / 60
    }
    
    companion object {
        fun getCommonPrayerTypes(): List<PrayerType> {
            return listOf(TAHLIL, YASIN, FATIHAH, ISTIGHFAR)
        }
        
        fun getAllPrayerTypes(): List<PrayerType> {
            return values().toList()
        }
        
        fun fromString(name: String): PrayerType? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

data class PrayerSession(
    val id: String = UUID.randomUUID().toString(),
    val memorialPrayerId: String,
    val sessionStartTime: Date,
    val sessionEndTime: Date? = null,
    val prayerCount: Int = 0,
    val isActive: Boolean = true,
    val sessionNotes: String? = null
) {
    fun getDuration(): Long {
        return if (sessionEndTime != null) {
            sessionEndTime.time - sessionStartTime.time
        } else {
            Date().time - sessionStartTime.time
        }
    }
    
    fun getDurationInMinutes(): Long {
        return getDuration() / (1000 * 60)
    }
}

data class PrayerStats(
    val userId: String,
    val memorialId: String? = null, // null means overall stats
    val totalSessions: Int = 0,
    val totalPrayers: Int = 0,
    val totalTimeInMillis: Long = 0,
    val averagePrayersPerSession: Float = 0f,
    val averageSessionDuration: Long = 0, // in milliseconds
    val prayerTypeBreakdown: Map<PrayerType, Int> = emptyMap(),
    val longestSession: Long = 0, // in milliseconds
    val currentStreak: Int = 0, // consecutive days with prayer
    val lastPrayerDate: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun getTotalTimeInMinutes(): Long {
        return totalTimeInMillis / (1000 * 60)
    }
    
    fun getTotalTimeInHours(): Float {
        return totalTimeInMillis.toFloat() / (1000 * 60 * 60)
    }
    
    fun getFavoriteRewardType(): PrayerType? {
        return prayerTypeBreakdown.maxByOrNull { it.value }?.key
    }
}

// Extension functions for convenience
fun List<MemorialPrayer>.getTotalPrayerCount(): Int {
    return sumOf { it.prayerCount }
}

fun List<MemorialPrayer>.getTotalDuration(): Long {
    return sumOf { it.duration }
}

fun List<MemorialPrayer>.getCompletedSessions(): List<MemorialPrayer> {
    return filter { it.isCompleted }
}

fun List<MemorialPrayer>.getActiveSessions(): List<MemorialPrayer> {
    return filter { it.isInProgress() }
}

fun List<MemorialPrayer>.groupByPrayerType(): Map<PrayerType, List<MemorialPrayer>> {
    return groupBy { it.prayerType }
}

fun List<MemorialPrayer>.getSessionsForDate(date: Date): List<MemorialPrayer> {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = date
    val targetDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
    val targetYear = calendar.get(java.util.Calendar.YEAR)
    
    return filter { prayer ->
        calendar.time = prayer.startTime
        val prayerDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
        val prayerYear = calendar.get(java.util.Calendar.YEAR)
        prayerDay == targetDay && prayerYear == targetYear
    }
}