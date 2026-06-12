package com.app_muslim.surah_yasin.feature.memorial.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoData(
    val id: String = "",
    val originalUri: Uri? = null,
    val croppedUri: Uri? = null,
    val firebaseUrl: String = "",
    val localPath: String = "",
    val fileName: String = "",
    val fileSize: Long = 0,
    val mimeType: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val isUploaded: Boolean = false,
    val uploadedAt: java.util.Date? = null,
    val compressionRatio: Float = 1.0f,
    val isOptimized: Boolean = false,
    val metadata: PhotoMetadata = PhotoMetadata()
) : Parcelable {
    
    val hasValidUri: Boolean
        get() = originalUri != null || croppedUri != null
        
    val displayUri: Uri?
        get() = croppedUri ?: originalUri
        
    val isLocalOnly: Boolean
        get() = firebaseUrl.isEmpty() && hasValidUri
        
    val fileSizeFormatted: String
        get() = when {
            fileSize < 1024 -> "${fileSize} B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> "${fileSize / (1024 * 1024)} MB"
        }
}

@Parcelize
data class PhotoMetadata(
    val deviceModel: String = "",
    val cameraInfo: String = "",
    val location: String = "",
    val timestamp: java.util.Date = java.util.Date(),
    val orientation: Int = 0,
    val hasExifData: Boolean = false,
    val isPortrait: Boolean = true
) : Parcelable