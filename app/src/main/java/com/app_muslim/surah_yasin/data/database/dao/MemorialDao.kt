package com.app_muslim.surah_yasin.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Memorial operations
 * Note: This is for local caching. Primary storage is Firebase Firestore.
 */
@Dao
interface MemorialDao {

    @Query("SELECT * FROM memorial_cache WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserMemorials(userId: String): Flow<List<MemorialCacheEntity>>

    @Query("SELECT * FROM memorial_cache WHERE id = :memorialId")
    suspend fun getMemorial(memorialId: String): MemorialCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorial(memorial: MemorialCacheEntity)

    @Update
    suspend fun updateMemorial(memorial: MemorialCacheEntity)

    @Delete
    suspend fun deleteMemorial(memorial: MemorialCacheEntity)

    @Query("DELETE FROM memorial_cache WHERE id = :memorialId")
    suspend fun deleteMemorialById(memorialId: String)

    @Query("DELETE FROM memorial_cache WHERE userId = :userId")
    suspend fun deleteUserMemorials(userId: String)

    @Query("DELETE FROM memorial_cache")
    suspend fun deleteAllMemorials()

    @Query("SELECT COUNT(*) FROM memorial_cache WHERE userId = :userId")
    suspend fun getUserMemorialCount(userId: String): Int

    @Query("SELECT * FROM memorial_cache WHERE expiresAt < :currentTime")
    suspend fun getExpiredMemorials(currentTime: Long): List<MemorialCacheEntity>

    @Query("UPDATE memorial_cache SET lastSyncTime = :syncTime WHERE id = :memorialId")
    suspend fun updateSyncTime(memorialId: String, syncTime: Long)
}

/**
 * Room entity for caching memorial data locally
 */
@Entity(
    tableName = "memorial_cache",
    indices = [Index(value = ["userId"])]
)
data class MemorialCacheEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val description: String? = null,
    val photoUrl: String? = null,
    val privacy: String, // MemorialPrivacy enum as string
    val duration: Int = 40, // Duration in days
    val createdAt: Long,
    val expiresAt: Long? = null,
    val lastSyncTime: Long = 0,
    val isActive: Boolean = true,
    val totalPrayers: Long = 0,
    val lastPrayerTime: Long? = null
)