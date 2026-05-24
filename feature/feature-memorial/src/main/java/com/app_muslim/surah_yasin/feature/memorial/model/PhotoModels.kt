package com.app_muslim.surah_yasin.feature.memorial.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PhotoData(
    val id: String = "",
    val originalUri: Uri? = null,
    val croppedUri: Uri? = null,
    val compressedUri: Uri? = null,
    val firebaseUrl: String = "",
    val fileName: String = "",
    val fileSize: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val aspectRatio: Float = 3f / 4f, // Memorial 3:4 aspect ratio
    val frameStyle: IslamicFrameStyle = IslamicFrameStyle.NONE,
    val compressionQuality: Int = 85,
    val isUploaded: Boolean = false,
    val isOptimized: Boolean = false,
    val metadata: PhotoMetadata = PhotoMetadata(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

@Parcelize
data class PhotoMetadata(
    val camera: String = "",
    val captureDate: Date? = null,
    val gpsLocation: String = "", // Removed for privacy
    val orientation: Int = 0,
    val flashUsed: Boolean = false,
    val focalLength: Float = 0f,
    val iso: Int = 0,
    val shutterSpeed: String = "",
    val aperture: String = "",
    val isFromCamera: Boolean = false,
    val isFromGallery: Boolean = false
) : Parcelable

enum class IslamicFrameStyle(
    val displayName: String,
    val description: String,
    val culturalSignificance: String
) {
    NONE("No Frame", "Clean and simple memorial", "Simple elegance honoring the deceased"),
    
    GEOMETRIC_GOLD("Golden Geometry", "Islamic geometric patterns in gold", 
        "Traditional Islamic art representing divine perfection"),
    
    CALLIGRAPHY_BORDER("Calligraphy Border", "Arabic calligraphy border with prayers", 
        "Sacred verses offering comfort and blessings"),
    
    MOSQUE_ARCH("Mosque Arch", "Elegant mosque architecture frame", 
        "Inspired by sacred Islamic architecture"),
    
    CRESCENT_STARS("Crescent & Stars", "Islamic crescent moon with stars", 
        "Symbols of Islamic faith and divine guidance"),
    
    ARABESQUE_PATTERN("Arabesque Pattern", "Traditional arabesque floral design", 
        "Classical Islamic ornamental art"),
    
    BISMILLAH_FRAME("Bismillah Frame", "Frame with Bismillah inscription", 
        "Beginning all things with Allah's name"),
    
    MEMORIAL_VERSES("Memorial Verses", "Quranic verses for the deceased", 
        "Sacred verses offering peace for the departed soul")
}

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

data class PhotoProcessingResult(
    val success: Boolean,
    val photoData: PhotoData?,
    val errorMessage: String?,
    val processingTimeMs: Long = 0
)

data class PhotoUploadProgress(
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val bytesUploaded: Long = 0,
    val totalBytes: Long = 0,
    val uploadSpeed: String = "",
    val estimatedTimeRemaining: String = ""
)

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