package com.app_muslim.surah_yasin.feature.memorial.prayer.repository

import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Memorial Prayer Sessions
 * Handles both local (Room) and remote (Firestore) data operations
 */
interface MemorialPrayerRepository {
    
    /**
     * Session Management
     */
    suspend fun createPrayerSession(
        memorialId: String,
        memorialName: String,
        memorialPhotoUrl: String?,
        prayerType: MemorialPrayerType,
        targetCount: Int? = null
    ): Result<MemorialPrayerSession>
    
    suspend fun startPrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun pausePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun resumePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun incrementPrayerCount(sessionId: String): Result<MemorialPrayerSession>
    suspend fun completePrayerSession(sessionId: String): Result<MemorialPrayerSession>
    suspend fun cancelPrayerSession(sessionId: String): Result<Unit>
    
    /**
     * Session Queries
     */
    suspend fun getPrayerSession(sessionId: String): Result<MemorialPrayerSession?>
    fun getPrayerSessionsForUser(userId: String): Flow<List<MemorialPrayerSession>>
    fun getPrayerSessionsForMemorial(memorialId: String): Flow<List<MemorialPrayerSession>>
    suspend fun getActiveSession(userId: String): Result<MemorialPrayerSession?>
    suspend fun getRecentSessions(userId: String, limit: Int = 10): Result<List<MemorialPrayerSession>>
    
    /**
     * Analytics and Statistics
     */
    suspend fun getPrayerStatistics(userId: String): Result<PrayerSessionStats>
    suspend fun getMemorialPrayerCount(memorialId: String): Result<Int>
    suspend fun getUserPrayerStreak(userId: String): Result<Int>
    suspend fun getWeeklyStats(userId: String): Result<Map<String, Int>>
    suspend fun getMonthlyStats(userId: String): Result<Map<String, Int>>
    
    /**
     * Synchronization
     */
    suspend fun syncPrayerSessions(userId: String): Result<Unit>
    suspend fun syncPrayerSession(session: MemorialPrayerSession): Result<Unit>
    
    /**
     * Community Features
     */
    suspend fun getGlobalPrayerCount(): Result<Long>
    suspend fun getTodaysPrayerParticipants(): Result<Int>
    suspend fun getMemorialCommunityStats(memorialId: String): Result<Map<String, Any>>
    
    /**
     * Maintenance
     */
    suspend fun cleanupOldSessions(daysToKeep: Int = 90): Result<Int>
    suspend fun clearAllLocalSessions(userId: String): Result<Unit>
}