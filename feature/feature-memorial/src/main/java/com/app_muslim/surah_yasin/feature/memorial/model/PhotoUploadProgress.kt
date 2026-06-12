package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoUploadProgress(
    val progress: Float = 0f, // 0.0 to 1.0
    val bytesUploaded: Long = 0,
    val totalBytes: Long = 0,
    val uploadSpeed: String = "", // e.g., "1.2 MB/s"
    val estimatedTimeRemaining: String = "", // e.g., "2 min 30 sec"
    val isUploading: Boolean = false,
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,
    val errorMessage: String? = null,
    val uploadId: String = "",
    val startTime: Long = 0,
    val stage: UploadStage = UploadStage.PREPARING
) : Parcelable {
    
    val progressPercentage: Int
        get() = (progress * 100).toInt()
    
    val remainingBytes: Long
        get() = totalBytes - bytesUploaded
    
    val isInProgress: Boolean
        get() = isUploading && !isCompleted && !isCancelled
    
    val hasError: Boolean
        get() = errorMessage != null
}

enum class UploadStage(val displayName: String) {
    PREPARING("Preparing upload..."),
    COMPRESSING("Optimizing photo..."),
    UPLOADING("Uploading to secure storage..."),
    PROCESSING("Processing photo..."),
    FINALIZING("Finishing upload..."),
    COMPLETED("Upload complete"),
    FAILED("Upload failed")
}