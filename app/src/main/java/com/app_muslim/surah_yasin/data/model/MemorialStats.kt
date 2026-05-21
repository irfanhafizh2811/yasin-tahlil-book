package com.app_muslim.surah_yasin.data.model

import java.util.Date

/**
 * Statistics for global prayer activities
 */
data class GlobalPrayerStats(
    val totalMemorials: Long = 0,
    val totalPrayers: Long = 0,
    val totalUsers: Long = 0,
    val totalPrayerSessions: Long = 0,
    val averagePrayersPerMemorial: Double = 0.0,
    val mostActivePrayerType: PrayerType? = null,
    val prayersByRegion: Map<IslamicRegion, Long> = emptyMap(),
    val prayersByType: Map<PrayerType, Long> = emptyMap(),
    val lastUpdated: Date = Date()
) {
    fun getTotalPrayerTime(): Long {
        return prayersByType.entries.sumOf { (type, count) ->
            count * type.defaultCount
        }
    }
}

/**
 * Regional prayer statistics
 */
data class RegionalStats(
    val region: IslamicRegion,
    val totalMemorials: Long = 0,
    val totalPrayers: Long = 0,
    val totalUsers: Long = 0,
    val mostPopularPrayerType: PrayerType? = null,
    val averagePrayersPerUser: Double = 0.0,
    val prayersByType: Map<PrayerType, Long> = emptyMap(),
    val lastUpdated: Date = Date()
)

/**
 * Statistics for a specific memorial
 */
data class MemorialStats(
    val memorialId: String,
    val totalPrayers: Long = 0,
    val totalSessions: Long = 0,
    val totalParticipants: Int = 0,
    val totalPrayerTime: Long = 0, // in milliseconds
    val averagePrayersPerSession: Double = 0.0,
    val prayersByType: Map<PrayerType, Long> = emptyMap(),
    val prayersByDate: Map<String, Long> = emptyMap(), // Date string to count
    val participantStats: Map<String, Long> = emptyMap(), // User ID to prayer count
    val lastPrayerDate: Date? = null,
    val createdAt: Date = Date(),
    val lastUpdated: Date = Date()
) {
    fun getTotalPrayerTimeInMinutes(): Long {
        return totalPrayerTime / (1000 * 60)
    }
    
    fun getTotalPrayerTimeInHours(): Double {
        return totalPrayerTime.toDouble() / (1000 * 60 * 60)
    }
    
    fun getMostActivePrayer(): PrayerType? {
        return prayersByType.maxByOrNull { it.value }?.key
    }
    
    fun getMostActiveParticipant(): String? {
        return participantStats.maxByOrNull { it.value }?.key
    }
    
    companion object {
        fun fromFirestoreMap(data: Map<String, Any>): GlobalPrayerStats {
            return GlobalPrayerStats(
                totalMemorials = (data["totalMemorials"] as? Number)?.toLong() ?: 0L,
                totalPrayers = (data["totalPrayers"] as? Number)?.toLong() ?: 0L,
                totalUsers = (data["totalUsers"] as? Number)?.toLong() ?: 0L,
                totalPrayerSessions = (data["totalPrayerSessions"] as? Number)?.toLong() ?: 0L,
                averagePrayersPerMemorial = (data["averagePrayersPerMemorial"] as? Number)?.toDouble() ?: 0.0,
                lastUpdated = (data["lastUpdated"] as? java.util.Date) ?: java.util.Date()
            )
        }
    }
}