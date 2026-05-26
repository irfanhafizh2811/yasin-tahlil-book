package com.app_muslim.surah_yasin.feature.memorial.prayer.repository

import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Memorial Prayer operations
 * Handles prayer session data with Firebase integration
 */
interface MemorialPrayerRepository {
    suspend fun startPrayerSession(
        memorialId: String,
        prayerType: PrayerType,
        targetCount: Int,
        culturalSettings: CulturalSettings
    ): Result<MemorialPrayerSession>
    
    suspend fun updatePrayerProgress(sessionId: String, currentCount: Int): Result<MemorialPrayerSession>
    suspend fun pausePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun resumePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun completePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    
    fun getPrayerSessionFlow(sessionId: String): Flow<MemorialPrayerSession?>
    fun getRecentSessionsFlow(memorialId: String): Flow<List<MemorialPrayerSession>>
    fun getPrayerStatisticsFlow(memorialId: String): Flow<MemorialPrayerStats>
    fun getGlobalPrayerStatsFlow(): Flow<Map<String, Any>>
    
    suspend fun getPrayerSession(sessionId: String): MemorialPrayerSession?
    suspend fun getRecentSessions(memorialId: String, limit: Int = 10): List<MemorialPrayerSession>
    suspend fun getPrayerStatistics(memorialId: String): MemorialPrayerStats
    suspend fun deletePrayerSession(sessionId: String): Result<Unit>
    suspend fun syncOfflineData(): Result<Unit>
}