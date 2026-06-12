package com.app_muslim.surah_yasin.feature.memorial.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*


enum class PhotoQuality(val displayName: String, val quality: Int, val maxSize: Int) {
    HIGH("High Quality", 95, 2048),
    MEDIUM("Medium Quality", 85, 1536), 
    LOW("Low Quality", 70, 1024),
    OPTIMIZED("Optimized", 80, 1200)
}

enum class PhotoCropAspectRatio(
    val displayName: String, 
    val ratio: Float,
    val description: String
) {
    MEMORIAL_STANDARD("Memorial Standard", 3f / 4f, "Traditional memorial format"),
    SQUARE("Square", 1f, "Instagram-style square format"),
    PORTRAIT("Portrait", 3f / 5f, "Classic portrait format"),
    LANDSCAPE("Landscape", 4f / 3f, "Landscape orientation"),
    ORIGINAL("Original", 0f, "Keep original photo proportions")
}

@Parcelize
data class PhotoEditingState(
    val originalPhoto: PhotoData = PhotoData(),
    val currentPhoto: PhotoData = PhotoData(),
    val cropAspectRatio: PhotoCropAspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
    val selectedFrame: IslamicFrameStyle = IslamicFrameStyle.NONE,
    val quality: PhotoQuality = PhotoQuality.MEDIUM,
    val isEditing: Boolean = false,
    val isCropping: Boolean = false,
    val isApplyingFrame: Boolean = false,
    val isCompressing: Boolean = false,
    val isUploading: Boolean = false,
    val editHistory: List<PhotoEditAction> = emptyList()
) : Parcelable

@Parcelize
data class PhotoEditAction(
    val type: PhotoEditType,
    val timestamp: Date = Date(),
    val description: String = "",
    val parameters: Map<String, String> = emptyMap()
) : Parcelable

enum class PhotoEditType {
    CROP,
    FRAME_APPLIED,
    COMPRESS,
    ROTATE,
    FLIP,
    FILTER_APPLIED,
    RESET
}


object PhotoConstants {
    const val MAX_FILE_SIZE_MB = 10
    const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024
    const val MIN_IMAGE_DIMENSION = 200
    const val MAX_IMAGE_DIMENSION = 4096
    const val DEFAULT_COMPRESSION_QUALITY = 85
    const val MEMORIAL_ASPECT_RATIO = 3f / 4f
    
    val SUPPORTED_IMAGE_FORMATS = listOf("image/jpeg", "image/jpg", "image/png", "image/webp")
    val SUPPORTED_FILE_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")
}

// Photo validation helpers
object PhotoValidator {
    
    fun validatePhoto(uri: Uri, context: android.content.Context): PhotoValidationResult {
        val errors = mutableListOf<PhotoValidationError>()
        
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            
            // Check file format
            if (mimeType == null || !PhotoConstants.SUPPORTED_IMAGE_FORMATS.contains(mimeType)) {
                errors.add(PhotoValidationError.UNSUPPORTED_FORMAT)
            }
            
            // Check file size
            val inputStream = contentResolver.openInputStream(uri)
            val fileSize = inputStream?.available()?.toLong() ?: 0
            inputStream?.close()
            
            if (fileSize > PhotoConstants.MAX_FILE_SIZE_BYTES) {
                errors.add(PhotoValidationError.FILE_TOO_LARGE)
            }
            
            if (fileSize == 0L) {
                errors.add(PhotoValidationError.EMPTY_FILE)
            }
            
        } catch (e: Exception) {
            errors.add(PhotoValidationError.CORRUPTED_FILE)
        }
        
        return PhotoValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}

data class PhotoValidationResult(
    val isValid: Boolean,
    val errors: List<PhotoValidationError>
)

enum class PhotoValidationError(val message: String) {
    UNSUPPORTED_FORMAT("Photo format not supported. Please use JPG, PNG, or WebP"),
    FILE_TOO_LARGE("Photo is too large. Maximum size is ${PhotoConstants.MAX_FILE_SIZE_MB}MB"),
    EMPTY_FILE("Photo file appears to be empty or corrupted"),
    CORRUPTED_FILE("Photo file is corrupted and cannot be processed"),
    DIMENSION_TOO_SMALL("Photo resolution is too small. Minimum ${PhotoConstants.MIN_IMAGE_DIMENSION}x${PhotoConstants.MIN_IMAGE_DIMENSION}"),
    DIMENSION_TOO_LARGE("Photo resolution is too large. Maximum ${PhotoConstants.MAX_IMAGE_DIMENSION}x${PhotoConstants.MAX_IMAGE_DIMENSION}"),
    NETWORK_ERROR("Network error during photo processing"),
    PERMISSION_DENIED("Camera or storage permission required"),
    STORAGE_ERROR("Failed to save photo to device storage")
}