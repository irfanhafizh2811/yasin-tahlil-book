package com.app_muslim.surah_yasin.core.data.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "photos",
    indices = [
        Index(value = ["memorialId"]),
        Index(value = ["userId"]),
        Index(value = ["isUploaded"]),
        Index(value = ["createdAt"])
    ]
)
data class PhotoEntity(
    @PrimaryKey
    val id: String,
    
    val memorialId: String?,
    val userId: String,
    
    // File paths and URLs
    val originalPath: String?,
    val croppedPath: String?,
    val compressedPath: String?,
    val firebaseUrl: String?,
    
    // File information
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
    
    // Processing information
    val frameStyle: String,
    val compressionQuality: Int,
    val isUploaded: Boolean,
    val isOptimized: Boolean,
    val isCropped: Boolean,
    
    // Metadata
    val cameraInfo: String?, // JSON string of camera metadata
    val captureDate: Date?,
    val orientation: Int,
    val flashUsed: Boolean,
    val isFromCamera: Boolean,
    
    // Timestamps
    val createdAt: Date,
    val updatedAt: Date,
    val uploadedAt: Date?
)

@Entity(
    tableName = "photo_processing_queue",
    indices = [Index(value = ["status"])]
)
data class PhotoProcessingQueueEntity(
    @PrimaryKey
    val id: String,
    
    val photoId: String,
    val memorialId: String?,
    val userId: String,
    
    val operation: String, // OPTIMIZE, CROP, FRAME_APPLY, UPLOAD
    val status: String, // PENDING, PROCESSING, COMPLETED, FAILED
    val priority: Int, // 1 (high) to 5 (low)
    
    val inputPath: String,
    val outputPath: String?,
    val parameters: String?, // JSON string of operation parameters
    
    val progress: Float,
    val errorMessage: String?,
    
    val createdAt: Date,
    val startedAt: Date?,
    val completedAt: Date?,
    
    val retryCount: Int,
    val maxRetries: Int
)

@Entity(
    tableName = "photo_cache",
    indices = [
        Index(value = ["url"], unique = true),
        Index(value = ["lastAccessedAt"]),
        Index(value = ["cacheSize"])
    ]
)
data class PhotoCacheEntity(
    @PrimaryKey
    val id: String,
    
    val url: String,
    val localPath: String,
    val cacheSize: Long,
    val expiresAt: Date,
    
    val lastAccessedAt: Date,
    val accessCount: Int,
    val createdAt: Date
)

// Room type converters
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}