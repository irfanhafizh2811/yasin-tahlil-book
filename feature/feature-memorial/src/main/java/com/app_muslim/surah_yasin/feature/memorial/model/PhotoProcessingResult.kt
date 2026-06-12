package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoProcessingResult(
    val success: Boolean = false,
    val originalSize: Long = 0,
    val compressedSize: Long = 0,
    val compressionRatio: Float = 1.0f,
    val processingTimeMs: Long = 0,
    val optimizations: List<PhotoOptimization> = emptyList(),
    val errorMessage: String? = null,
    val errorCode: PhotoProcessingError? = null,
    val warnings: List<String> = emptyList(),
    val metadata: ProcessingMetadata = ProcessingMetadata()
) : Parcelable {
    
    val sizeSavingPercentage: Int
        get() = if (originalSize > 0) {
            ((1 - (compressedSize.toFloat() / originalSize)) * 100).toInt()
        } else 0
    
    val hasWarnings: Boolean
        get() = warnings.isNotEmpty()
    
    val isOptimized: Boolean
        get() = optimizations.isNotEmpty()
}

@Parcelize
data class ProcessingMetadata(
    val algorithm: String = "JPEG_OPTIMIZATION",
    val qualityLevel: Int = 85,
    val dimensions: String = "",
    val colorSpace: String = "sRGB",
    val processingDate: java.util.Date = java.util.Date()
) : Parcelable

enum class PhotoOptimization(val displayName: String) {
    COMPRESSION("Size optimization"),
    ORIENTATION_FIX("Orientation correction"),
    COLOR_ENHANCEMENT("Color enhancement"),
    NOISE_REDUCTION("Noise reduction"),
    SHARPENING("Image sharpening"),
    METADATA_REMOVAL("Privacy protection")
}

enum class PhotoProcessingError(val code: String, val message: String) {
    INVALID_FORMAT("E001", "Unsupported image format"),
    FILE_TOO_LARGE("E002", "File size exceeds limit"),
    CORRUPTED_FILE("E003", "File appears to be corrupted"),
    PROCESSING_FAILED("E004", "Processing failed"),
    INSUFFICIENT_STORAGE("E005", "Insufficient storage space"),
    NETWORK_ERROR("E006", "Network connection required"),
    PERMISSION_DENIED("E007", "Storage permission required"),
    UNKNOWN_ERROR("E999", "Unknown error occurred")
}