package com.app_muslim.surah_yasin.core.data.dao

import androidx.room.*
import com.app_muslim.surah_yasin.core.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PhotoDao {
    
    // Photo CRUD operations
    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhoto(photoId: String): PhotoEntity?
    
    @Query("SELECT * FROM photos WHERE memorialId = :memorialId ORDER BY createdAt DESC")
    fun getMemorialPhotos(memorialId: String): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun getUserPhotos(userId: String, limit: Int = 50): Flow<List<PhotoEntity>>
    
    @Query("SELECT * FROM photos WHERE isUploaded = 0 AND userId = :userId")
    suspend fun getPendingUploads(userId: String): List<PhotoEntity>
    
    @Query("SELECT * FROM photos WHERE isUploaded = 1 AND firebaseUrl IS NOT NULL ORDER BY uploadedAt DESC LIMIT :limit")
    fun getUploadedPhotos(limit: Int = 100): Flow<List<PhotoEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)
    
    @Update
    suspend fun updatePhoto(photo: PhotoEntity)
    
    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)
    
    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: String)
    
    @Query("DELETE FROM photos WHERE memorialId = :memorialId")
    suspend fun deleteMemorialPhotos(memorialId: String)
    
    // Photo statistics
    @Query("SELECT COUNT(*) FROM photos WHERE userId = :userId")
    suspend fun getUserPhotoCount(userId: String): Int
    
    @Query("SELECT SUM(fileSize) FROM photos WHERE userId = :userId")
    suspend fun getUserPhotosSize(userId: String): Long?
    
    @Query("SELECT COUNT(*) FROM photos WHERE isUploaded = 1 AND userId = :userId")
    suspend fun getUploadedPhotoCount(userId: String): Int
    
    // Cleanup operations
    @Query("DELETE FROM photos WHERE createdAt < :beforeDate AND isUploaded = 1")
    suspend fun deleteOldPhotos(beforeDate: Date): Int
    
    @Query("SELECT * FROM photos WHERE originalPath IS NOT NULL AND originalPath NOT LIKE 'http%'")
    suspend fun getLocalPhotos(): List<PhotoEntity>
}

@Dao
interface PhotoProcessingQueueDao {
    
    @Query("SELECT * FROM photo_processing_queue WHERE status = 'PENDING' ORDER BY priority ASC, createdAt ASC LIMIT :limit")
    suspend fun getPendingProcessingJobs(limit: Int = 10): List<PhotoProcessingQueueEntity>
    
    @Query("SELECT * FROM photo_processing_queue WHERE status = 'PROCESSING'")
    suspend fun getProcessingJobs(): List<PhotoProcessingQueueEntity>
    
    @Query("SELECT * FROM photo_processing_queue WHERE photoId = :photoId ORDER BY createdAt DESC")
    fun getPhotoProcessingJobs(photoId: String): Flow<List<PhotoProcessingQueueEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingJob(job: PhotoProcessingQueueEntity)
    
    @Update
    suspend fun updateProcessingJob(job: PhotoProcessingQueueEntity)
    
    @Delete
    suspend fun deleteProcessingJob(job: PhotoProcessingQueueEntity)
    
    @Query("DELETE FROM photo_processing_queue WHERE status = 'COMPLETED' AND completedAt < :beforeDate")
    suspend fun deleteCompletedJobs(beforeDate: Date): Int
    
    @Query("DELETE FROM photo_processing_queue WHERE status = 'FAILED' AND retryCount >= maxRetries")
    suspend fun deleteFailedJobs(): Int
    
    @Query("UPDATE photo_processing_queue SET status = 'FAILED', errorMessage = :errorMessage WHERE status = 'PROCESSING' AND startedAt < :timeoutDate")
    suspend fun markTimedOutJobsAsFailed(timeoutDate: Date, errorMessage: String): Int
}

@Dao
interface PhotoCacheDao {
    
    @Query("SELECT * FROM photo_cache WHERE url = :url")
    suspend fun getCachedPhoto(url: String): PhotoCacheEntity?
    
    @Query("SELECT * FROM photo_cache WHERE expiresAt > :currentTime ORDER BY lastAccessedAt DESC")
    suspend fun getValidCachedPhotos(currentTime: Date): List<PhotoCacheEntity>
    
    @Query("SELECT SUM(cacheSize) FROM photo_cache")
    suspend fun getTotalCacheSize(): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPhoto(cachedPhoto: PhotoCacheEntity)
    
    @Update
    suspend fun updateCachedPhoto(cachedPhoto: PhotoCacheEntity)
    
    @Query("UPDATE photo_cache SET lastAccessedAt = :accessTime, accessCount = accessCount + 1 WHERE url = :url")
    suspend fun updateLastAccessed(url: String, accessTime: Date)
    
    @Delete
    suspend fun deleteCachedPhoto(cachedPhoto: PhotoCacheEntity)
    
    @Query("DELETE FROM photo_cache WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredCache(currentTime: Date): Int
    
    @Query("DELETE FROM photo_cache WHERE id IN (SELECT id FROM photo_cache ORDER BY lastAccessedAt ASC LIMIT :count)")
    suspend fun deleteLeastRecentlyUsed(count: Int): Int
    
    @Query("SELECT * FROM photo_cache ORDER BY cacheSize DESC LIMIT :limit")
    suspend fun getLargestCachedPhotos(limit: Int): List<PhotoCacheEntity>
}