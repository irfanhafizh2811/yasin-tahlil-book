package com.app_muslim.surah_yasin.core.data.repository

import android.content.Context
import android.net.Uri
import com.app_muslim.surah_yasin.core.data.dao.*
import com.app_muslim.surah_yasin.core.data.entity.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoManagementRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoDao: PhotoDao,
    private val processingQueueDao: PhotoProcessingQueueDao,
    private val cacheDao: PhotoCacheDao,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val MAX_CACHE_SIZE_MB = 100
        private const val MAX_CACHE_SIZE_BYTES = MAX_CACHE_SIZE_MB * 1024 * 1024
        private const val CACHE_EXPIRY_DAYS = 30
        private const val PROCESSING_TIMEOUT_MINUTES = 30
    }
    
    // Photo management
    suspend fun savePhoto(
        uri: Uri,
        memorialId: String?,
        frameStyle: String,
        quality: Int
    ): String {
        val photoId = UUID.randomUUID().toString()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        
        val photoEntity = PhotoEntity(
            id = photoId,
            memorialId = memorialId,
            userId = userId,
            originalPath = uri.toString(),
            croppedPath = null,
            compressedPath = null,
            firebaseUrl = null,
            fileName = "photo_${System.currentTimeMillis()}.jpg",
            fileSize = getFileSize(uri),
            mimeType = context.contentResolver.getType(uri) ?: "image/jpeg",
            width = 0, // Would need to decode image to get actual dimensions
            height = 0,
            aspectRatio = 3f / 4f,
            frameStyle = frameStyle,
            compressionQuality = quality,
            isUploaded = false,
            isOptimized = false,
            isCropped = false,
            cameraInfo = null,
            captureDate = Date(),
            orientation = 0,
            flashUsed = false,
            isFromCamera = uri.scheme == "file",
            createdAt = Date(),
            updatedAt = Date(),
            uploadedAt = null
        )
        
        photoDao.insertPhoto(photoEntity)
        return photoId
    }
    
    suspend fun updatePhotoUploadStatus(photoId: String, firebaseUrl: String) {
        val photo = photoDao.getPhoto(photoId) ?: return
        val updatedPhoto = photo.copy(
            firebaseUrl = firebaseUrl,
            isUploaded = true,
            uploadedAt = Date(),
            updatedAt = Date()
        )
        photoDao.updatePhoto(updatedPhoto)
    }
    
    fun getMemorialPhotos(memorialId: String): Flow<List<PhotoEntity>> {
        return photoDao.getMemorialPhotos(memorialId)
    }
    
    fun getUserPhotos(userId: String, limit: Int = 50): Flow<List<PhotoEntity>> {
        return photoDao.getUserPhotos(userId, limit)
    }
    
    suspend fun deletePhoto(photoId: String) {
        val photo = photoDao.getPhoto(photoId) ?: return
        
        // Delete local files
        photo.originalPath?.let { deleteLocalFile(it) }
        photo.croppedPath?.let { deleteLocalFile(it) }
        photo.compressedPath?.let { deleteLocalFile(it) }
        
        // Delete from Firebase Storage
        photo.firebaseUrl?.let { url ->
            try {
                storage.getReferenceFromUrl(url).delete()
            } catch (e: Exception) {
                // Log error but continue with local deletion
            }
        }
        
        // Delete from database
        photoDao.deletePhotoById(photoId)
    }
    
    // Photo processing queue management
    suspend fun queuePhotoProcessing(
        photoId: String,
        memorialId: String?,
        operation: String,
        parameters: Map<String, Any>? = null,
        priority: Int = 3
    ): String {
        val jobId = UUID.randomUUID().toString()
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        
        val job = PhotoProcessingQueueEntity(
            id = jobId,
            photoId = photoId,
            memorialId = memorialId,
            userId = userId,
            operation = operation,
            status = "PENDING",
            priority = priority,
            inputPath = "", // Would be set based on photo
            outputPath = null,
            parameters = parameters?.let { convertMapToJson(it) },
            progress = 0f,
            errorMessage = null,
            createdAt = Date(),
            startedAt = null,
            completedAt = null,
            retryCount = 0,
            maxRetries = 3
        )
        
        processingQueueDao.insertProcessingJob(job)
        return jobId
    }
    
    suspend fun getNextProcessingJob(): PhotoProcessingQueueEntity? {
        return processingQueueDao.getPendingProcessingJobs(1).firstOrNull()
    }
    
    suspend fun updateProcessingJobStatus(
        jobId: String,
        status: String,
        progress: Float = 0f,
        errorMessage: String? = null,
        outputPath: String? = null
    ) {
        // This would be implemented to update the job status
        // For brevity, showing the concept
    }
    
    // Photo cache management
    suspend fun getCachedPhoto(url: String): PhotoCacheEntity? {
        val cached = cacheDao.getCachedPhoto(url)
        if (cached != null && cached.expiresAt.after(Date())) {
            cacheDao.updateLastAccessed(url, Date())
            return cached
        }
        return null
    }
    
    suspend fun cachePhoto(url: String, localPath: String, fileSize: Long) {
        // Check cache size limits
        val currentCacheSize = cacheDao.getTotalCacheSize() ?: 0
        if (currentCacheSize + fileSize > MAX_CACHE_SIZE_BYTES) {
            // Clean up cache
            cleanupCache(fileSize)
        }
        
        val expiresAt = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, CACHE_EXPIRY_DAYS)
        }.time
        
        val cacheEntity = PhotoCacheEntity(
            id = UUID.randomUUID().toString(),
            url = url,
            localPath = localPath,
            cacheSize = fileSize,
            expiresAt = expiresAt,
            lastAccessedAt = Date(),
            accessCount = 1,
            createdAt = Date()
        )
        
        cacheDao.insertCachedPhoto(cacheEntity)
    }
    
    // Cleanup operations
    suspend fun performCleanup() = withContext(Dispatchers.IO) {
        val currentTime = Date()
        
        // Clean expired cache
        cacheDao.deleteExpiredCache(currentTime)
        
        // Clean up old processing jobs
        val thirtyDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -30)
        }.time
        processingQueueDao.deleteCompletedJobs(thirtyDaysAgo)
        processingQueueDao.deleteFailedJobs()
        
        // Mark timed out jobs as failed
        val timeoutDate = Calendar.getInstance().apply {
            add(Calendar.MINUTE, -PROCESSING_TIMEOUT_MINUTES)
        }.time
        processingQueueDao.markTimedOutJobsAsFailed(timeoutDate, "Processing timeout")
        
        // Clean up old photos if needed
        photoDao.deleteOldPhotos(thirtyDaysAgo)
    }
    
    // Statistics
    suspend fun getPhotoStatistics(userId: String): PhotoStatistics {
        return PhotoStatistics(
            totalPhotos = photoDao.getUserPhotoCount(userId),
            uploadedPhotos = photoDao.getUploadedPhotoCount(userId),
            totalSize = photoDao.getUserPhotosSize(userId) ?: 0,
            cacheSize = cacheDao.getTotalCacheSize() ?: 0
        )
    }
    
    // Helper functions
    private suspend fun cleanupCache(requiredSpace: Long) {
        val currentSize = cacheDao.getTotalCacheSize() ?: 0
        if (currentSize + requiredSpace > MAX_CACHE_SIZE_BYTES) {
            // Delete least recently used items
            val itemsToDelete = ((currentSize + requiredSpace - MAX_CACHE_SIZE_BYTES) / (1024 * 1024)).toInt() + 1
            cacheDao.deleteLeastRecentlyUsed(itemsToDelete)
        }
    }
    
    private fun deleteLocalFile(path: String) {
        try {
            if (path.startsWith("file://")) {
                File(Uri.parse(path).path ?: return).delete()
            } else if (!path.startsWith("http")) {
                File(path).delete()
            }
        } catch (e: Exception) {
            // Log error but don't throw
        }
    }
    
    private fun getFileSize(uri: Uri): Long {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.available().toLong()
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    private fun convertMapToJson(map: Map<String, Any>): String {
        // Simple JSON conversion - in production use proper JSON library
        return map.entries.joinToString(",", "{", "}") { (k, v) ->
            "\"$k\":\"$v\""
        }
    }
}

data class PhotoStatistics(
    val totalPhotos: Int,
    val uploadedPhotos: Int,
    val totalSize: Long,
    val cacheSize: Long
)