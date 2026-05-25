package com.app_muslim.surah_yasin.core.data.dao

import androidx.room.*
import com.app_muslim.surah_yasin.core.data.entity.MemorialPrayerSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for Memorial Prayer Sessions
 * Handles offline storage and synchronization
 */
@Dao
interface MemorialPrayerSessionDao {
    
    /**
     * Insert a new prayer session
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerSession(session: MemorialPrayerSessionEntity)
    
    /**
     * Update existing prayer session
     */
    @Update
    suspend fun updatePrayerSession(session: MemorialPrayerSessionEntity)
    
    /**
     * Delete prayer session
     */
    @Delete
    suspend fun deletePrayerSession(session: MemorialPrayerSessionEntity)
    
    /**
     * Get prayer session by ID
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE id = :sessionId")
    suspend fun getPrayerSessionById(sessionId: String): MemorialPrayerSessionEntity?
    
    /**
     * Get all prayer sessions for a user (Flow for reactive updates)
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPrayerSessionsForUser(userId: String): Flow<List<MemorialPrayerSessionEntity>>
    
    /**
     * Get prayer sessions for specific memorial
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE memorialId = :memorialId ORDER BY createdAt DESC")
    fun getPrayerSessionsForMemorial(memorialId: String): Flow<List<MemorialPrayerSessionEntity>>
    
    /**
     * Get active (in progress or paused) sessions for user
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId AND state IN ('IN_PROGRESS', 'PAUSED') ORDER BY updatedAt DESC")
    suspend fun getActiveSessionsForUser(userId: String): List<MemorialPrayerSessionEntity>
    
    /**
     * Get completed sessions for user
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId AND isCompleted = 1 ORDER BY endTime DESC")
    fun getCompletedSessionsForUser(userId: String): Flow<List<MemorialPrayerSessionEntity>>
    
    /**
     * Get recent sessions (last 30 days)
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId AND createdAt > :thirtyDaysAgo ORDER BY createdAt DESC LIMIT 20")
    suspend fun getRecentSessions(userId: String, thirtyDaysAgo: Long): List<MemorialPrayerSessionEntity>
    
    /**
     * Get sessions by prayer type for analytics
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId AND prayerType = :prayerType ORDER BY createdAt DESC")
    suspend fun getSessionsByPrayerType(userId: String, prayerType: String): List<MemorialPrayerSessionEntity>
    
    /**
     * Get unsynced sessions (for Firebase sync)
     */
    @Query("SELECT * FROM memorial_prayer_sessions WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedSessions(userId: String): List<MemorialPrayerSessionEntity>
    
    /**
     * Mark session as synced
     */
    @Query("UPDATE memorial_prayer_sessions SET isSynced = 1 WHERE id = :sessionId")
    suspend fun markSessionAsSynced(sessionId: String)
    
    /**
     * Get session statistics for user
     */
    @Query("""
        SELECT 
            COUNT(*) as totalSessions,
            SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completedSessions,
            SUM(currentCount) as totalPrayers,
            AVG(CASE WHEN isCompleted = 1 AND startTime IS NOT NULL AND endTime IS NOT NULL 
                THEN (endTime - startTime - pausedDuration) / 60000 ELSE 0 END) as avgDuration
        FROM memorial_prayer_sessions 
        WHERE userId = :userId
    """)
    suspend fun getSessionStatistics(userId: String): SessionStatistics
    
    /**
     * Get sessions for date range (for streak calculation)
     */
    @Query("""
        SELECT * FROM memorial_prayer_sessions 
        WHERE userId = :userId 
        AND createdAt >= :startDate 
        AND createdAt <= :endDate 
        AND isCompleted = 1
        ORDER BY createdAt ASC
    """)
    suspend fun getSessionsInDateRange(userId: String, startDate: Long, endDate: Long): List<MemorialPrayerSessionEntity>
    
    /**
     * Get most prayed memorial
     */
    @Query("""
        SELECT memorialId, COUNT(*) as sessionCount
        FROM memorial_prayer_sessions 
        WHERE userId = :userId AND isCompleted = 1
        GROUP BY memorialId 
        ORDER BY sessionCount DESC 
        LIMIT 1
    """)
    suspend fun getMostPrayedMemorial(userId: String): MemorialPrayerCount?
    
    /**
     * Get most used prayer type
     */
    @Query("""
        SELECT prayerType, COUNT(*) as count
        FROM memorial_prayer_sessions 
        WHERE userId = :userId AND isCompleted = 1
        GROUP BY prayerType 
        ORDER BY count DESC 
        LIMIT 1
    """)
    suspend fun getMostUsedPrayerType(userId: String): PrayerTypeCount?
    
    /**
     * Delete old completed sessions (cleanup)
     */
    @Query("DELETE FROM memorial_prayer_sessions WHERE isCompleted = 1 AND endTime < :cutoffDate")
    suspend fun deleteOldCompletedSessions(cutoffDate: Long): Int
    
    /**
     * Get total prayer count for memorial
     */
    @Query("SELECT SUM(currentCount) FROM memorial_prayer_sessions WHERE memorialId = :memorialId AND isCompleted = 1")
    suspend fun getTotalPrayersForMemorial(memorialId: String): Int?
}

/**
 * Data classes for query results
 */
data class SessionStatistics(
    val totalSessions: Int,
    val completedSessions: Int,
    val totalPrayers: Int,
    val avgDuration: Double
)

data class MemorialPrayerCount(
    val memorialId: String,
    val sessionCount: Int
)

data class PrayerTypeCount(
    val prayerType: String,
    val count: Int
)