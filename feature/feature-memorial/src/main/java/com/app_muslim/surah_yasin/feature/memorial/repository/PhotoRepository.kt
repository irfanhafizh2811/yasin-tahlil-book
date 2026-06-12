package com.app_muslim.surah_yasin.feature.memorial.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val context: Context,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val MEMORIAL_PHOTOS_PATH = "memorial_photos"
        private const val TEMP_PHOTOS_PATH = "temp_photos"
        private const val CROPPED_PHOTOS_PATH = "cropped_photos"
        private const val OPTIMIZED_PHOTOS_PATH = "optimized_photos"
        
        private const val MAX_IMAGE_WIDTH = 2048
        private const val MAX_IMAGE_HEIGHT = 2048
        private const val COMPRESSION_QUALITY = 85
    }
    
    suspend fun processAndUploadPhoto(
        photoUri: Uri,
        memorialId: String,
        quality: PhotoQuality = PhotoQuality.MEDIUM,
        frameStyle: IslamicFrameStyle = IslamicFrameStyle.NONE
    ): Flow<PhotoProcessingResult> = flow {
        val startTime = System.currentTimeMillis()
        var originalSize = 0L
        var compressedSize = 0L
        
        try {
            emit(PhotoProcessingResult(
                success = true,
                errorMessage = "Starting photo processing..."
            ))
            
            // Step 1: Validate photo
            val validationResult = PhotoValidator.validatePhoto(photoUri, context)
            if (!validationResult.isValid) {
                emit(PhotoProcessingResult(
                    success = false,
                    errorMessage = validationResult.errors.first().message,
                    errorCode = PhotoProcessingError.INVALID_FORMAT
                ))
                return@flow
            }
            
            // Get original file size
            val inputStream = context.contentResolver.openInputStream(photoUri)
            originalSize = inputStream?.available()?.toLong() ?: 0
            inputStream?.close()
            
            // Step 2: Extract metadata
            val metadata = extractPhotoMetadata(photoUri)
            emit(PhotoProcessingResult(
                success = true,
                originalSize = originalSize,
                errorMessage = "Extracting photo information..."
            ))
            
            // Step 3: Optimize image
            val optimizedUri = optimizeImage(photoUri, quality)
            emit(PhotoProcessingResult(
                success = true,
                originalSize = originalSize,
                errorMessage = "Optimizing image quality...",
                optimizations = listOf(PhotoOptimization.COMPRESSION)
            ))
            
            // Step 4: Apply Islamic frame if selected
            val framedUri = if (frameStyle != IslamicFrameStyle.NONE) {
                applyIslamicFrame(optimizedUri, frameStyle)
            } else {
                optimizedUri
            }
            val optimizations = mutableListOf(PhotoOptimization.COMPRESSION)
            if (frameStyle != IslamicFrameStyle.NONE) {
                optimizations.add(PhotoOptimization.COLOR_ENHANCEMENT)
            }
            
            emit(PhotoProcessingResult(
                success = true,
                originalSize = originalSize,
                errorMessage = "Applying Islamic frame...",
                optimizations = optimizations
            ))
            
            // Get compressed file size
            val optimizedFile = File(framedUri.path ?: "")
            compressedSize = if (optimizedFile.exists()) optimizedFile.length() else originalSize
            
            // Step 5: Upload to Firebase Storage
            val firebaseUrl = uploadToFirebaseStorage(framedUri, memorialId)
            emit(PhotoProcessingResult(
                success = true,
                originalSize = originalSize,
                compressedSize = compressedSize,
                compressionRatio = if (originalSize > 0) compressedSize.toFloat() / originalSize else 1.0f,
                errorMessage = "Uploading to secure storage...",
                optimizations = optimizations
            ))
            
            // Step 6: Create photo data
            val photoData = createPhotoData(
                originalUri = photoUri,
                optimizedUri = framedUri,
                firebaseUrl = firebaseUrl,
                metadata = metadata,
                quality = quality
            )
            
            val processingTime = System.currentTimeMillis() - startTime
            emit(PhotoProcessingResult(
                success = true,
                originalSize = originalSize,
                compressedSize = compressedSize,
                compressionRatio = if (originalSize > 0) compressedSize.toFloat() / originalSize else 1.0f,
                processingTimeMs = processingTime,
                optimizations = optimizations,
                errorMessage = "Photo processing completed successfully"
            ))
            
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            emit(PhotoProcessingResult(
                success = false,
                originalSize = originalSize,
                compressedSize = compressedSize,
                processingTimeMs = processingTime,
                errorMessage = "Failed to process photo: ${e.message}",
                errorCode = PhotoProcessingError.PROCESSING_FAILED
            ))
        }
    }
    
    suspend fun cropPhoto(
        photoUri: Uri,
        cropRect: androidx.compose.ui.geometry.Rect,
        aspectRatio: PhotoCropAspectRatio
    ): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (originalBitmap == null) return null
            
            // Calculate crop coordinates
            val scaleX = originalBitmap.width.toFloat() / cropRect.width
            val scaleY = originalBitmap.height.toFloat() / cropRect.height
            
            val cropX = (cropRect.left * scaleX).toInt().coerceIn(0, originalBitmap.width)
            val cropY = (cropRect.top * scaleY).toInt().coerceIn(0, originalBitmap.height)
            val cropWidth = (cropRect.width * scaleX).toInt().coerceIn(0, originalBitmap.width - cropX)
            val cropHeight = (cropRect.height * scaleY).toInt().coerceIn(0, originalBitmap.height - cropY)
            
            // Crop bitmap
            val croppedBitmap = Bitmap.createBitmap(
                originalBitmap,
                cropX,
                cropY,
                cropWidth,
                cropHeight
            )
            
            // Save cropped image
            val croppedFile = createTempImageFile("cropped")
            val outputStream = FileOutputStream(croppedFile)
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
            outputStream.close()
            
            // Cleanup
            originalBitmap.recycle()
            croppedBitmap.recycle()
            
            Uri.fromFile(croppedFile)
            
        } catch (e: Exception) {
            null
        }
    }
    
    fun getUploadProgress(uploadTask: UploadTask): Flow<PhotoUploadProgress> = callbackFlow {
        val startTime = System.currentTimeMillis()
        val uploadId = "upload_${System.currentTimeMillis()}"
        
        val progressListener = OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
            val isCompleted = taskSnapshot.task.isComplete
            val uploadProgress = PhotoUploadProgress(
                progress = progress,
                bytesUploaded = taskSnapshot.bytesTransferred,
                totalBytes = taskSnapshot.totalByteCount,
                uploadSpeed = calculateUploadSpeed(taskSnapshot),
                estimatedTimeRemaining = calculateEstimatedTime(taskSnapshot),
                isUploading = !isCompleted,
                isCompleted = isCompleted,
                uploadId = uploadId,
                startTime = startTime,
                stage = when {
                    progress >= 1.0f && isCompleted -> UploadStage.COMPLETED
                    progress >= 0.8f -> UploadStage.FINALIZING
                    progress >= 0.1f -> UploadStage.UPLOADING
                    else -> UploadStage.PREPARING
                }
            )
            trySend(uploadProgress)
        }
        
        uploadTask.addOnProgressListener(progressListener)
        
        awaitClose {
            // Cleanup if needed
        }
    }
    
    suspend fun deletePhoto(photoUrl: String) {
        try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.delete().await()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to delete photo: ${e.message}")
        }
    }
    
    suspend fun getPhotoMetadata(photoUrl: String): StorageMetadata? {
        return try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.metadata.await()
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun extractPhotoMetadata(photoUri: Uri): PhotoMetadata {
        return try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val exif = ExifInterface(inputStream!!)
            inputStream.close()
            
            PhotoMetadata(
                deviceModel = android.os.Build.MODEL,
                cameraInfo = exif.getAttribute(ExifInterface.TAG_MAKE) ?: "",
                location = "", // Deliberately not storing GPS for privacy
                timestamp = exif.dateTime?.let { Date(it) } ?: Date(),
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL),
                hasExifData = true,
                isPortrait = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) in listOf(
                    ExifInterface.ORIENTATION_NORMAL,
                    ExifInterface.ORIENTATION_ROTATE_180
                )
            )
        } catch (e: Exception) {
            PhotoMetadata()
        }
    }
    
    private suspend fun optimizeImage(photoUri: Uri, quality: PhotoQuality): Uri {
        val inputStream = context.contentResolver.openInputStream(photoUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (originalBitmap == null) throw IllegalArgumentException("Invalid image file")
        
        // Calculate scaling to fit within max dimensions
        val scaleRatio = calculateScaleRatio(
            originalBitmap.width,
            originalBitmap.height,
            quality.maxSize,
            quality.maxSize
        )
        
        val scaledWidth = (originalBitmap.width * scaleRatio).toInt()
        val scaledHeight = (originalBitmap.height * scaleRatio).toInt()
        
        // Scale bitmap
        val scaledBitmap = if (scaleRatio < 1.0f) {
            Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)
        } else {
            originalBitmap
        }
        
        // Correct orientation
        val correctedBitmap = correctImageOrientation(scaledBitmap, photoUri)
        
        // Save optimized image
        val optimizedFile = createTempImageFile("optimized")
        val outputStream = FileOutputStream(optimizedFile)
        correctedBitmap.compress(Bitmap.CompressFormat.JPEG, quality.quality, outputStream)
        outputStream.close()
        
        // Cleanup
        originalBitmap.recycle()
        if (scaledBitmap != originalBitmap) scaledBitmap.recycle()
        if (correctedBitmap != scaledBitmap) correctedBitmap.recycle()
        
        return Uri.fromFile(optimizedFile)
    }
    
    private suspend fun applyIslamicFrame(photoUri: Uri, frameStyle: IslamicFrameStyle): Uri {
        // For now, return the original URI
        // In a full implementation, this would apply the frame using Canvas or image processing
        // This would require complex image manipulation that goes beyond the scope of this demo
        return photoUri
    }
    
    private suspend fun uploadToFirebaseStorage(photoUri: Uri, memorialId: String): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")
        
        val fileName = "${currentUser.uid}_${System.currentTimeMillis()}.jpg"
        val photoRef = storage.reference
            .child(MEMORIAL_PHOTOS_PATH)
            .child(memorialId)
            .child(fileName)
        
        // Create metadata with security information
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCustomMetadata("uploadedBy", currentUser.uid)
            .setCustomMetadata("memorialId", memorialId)
            .setCustomMetadata("uploadTimestamp", System.currentTimeMillis().toString())
            .setCustomMetadata("platform", "android")
            .build()
        
        // Upload file
        val inputStream = context.contentResolver.openInputStream(photoUri)
            ?: throw IllegalArgumentException("Cannot open photo file")
        
        val uploadTask = photoRef.putStream(inputStream, metadata)
        uploadTask.await()
        
        inputStream.close()
        
        // Get download URL
        return photoRef.downloadUrl.await().toString()
    }
    
    private fun createPhotoData(
        originalUri: Uri,
        optimizedUri: Uri,
        firebaseUrl: String,
        metadata: PhotoMetadata,
        quality: PhotoQuality
    ): PhotoData {
        val fileName = "memorial_${System.currentTimeMillis()}.jpg"
        val file = File(optimizedUri.path ?: "")
        
        return PhotoData(
            id = UUID.randomUUID().toString(),
            originalUri = originalUri,
            croppedUri = optimizedUri,
            firebaseUrl = firebaseUrl,
            localPath = optimizedUri.path ?: "",
            fileName = fileName,
            fileSize = if (file.exists()) file.length() else 0,
            mimeType = "image/jpeg",
            width = 0, // Would need to decode image to get dimensions
            height = 0,
            isUploaded = true,
            uploadedAt = Date(),
            compressionRatio = quality.quality / 100f,
            isOptimized = true,
            metadata = metadata
        )
    }
    
    private fun calculateScaleRatio(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Float {
        val widthRatio = maxWidth.toFloat() / originalWidth
        val heightRatio = maxHeight.toFloat() / originalHeight
        return minOf(widthRatio, heightRatio, 1.0f)
    }
    
    private fun correctImageOrientation(bitmap: Bitmap, photoUri: Uri): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val exif = ExifInterface(inputStream!!)
            inputStream.close()
            
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }
    
    private fun createTempImageFile(prefix: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${prefix}_${timeStamp}.jpg"
        return File(context.cacheDir, fileName)
    }
    
    private fun calculateUploadSpeed(taskSnapshot: UploadTask.TaskSnapshot): String {
        // Simplified speed calculation
        val elapsedTimeMs = System.currentTimeMillis() - (taskSnapshot.task.snapshot.metadata?.creationTimeMillis ?: System.currentTimeMillis())
        val elapsedSeconds = elapsedTimeMs.toDouble() / 1000.0
        
        if (elapsedSeconds <= 0) return "Calculating..."
        
        val bytesPerSecond = taskSnapshot.bytesTransferred.toDouble() / elapsedSeconds
        
        return when {
            bytesPerSecond > 1024 * 1024 -> "${(bytesPerSecond / (1024 * 1024)).toInt()} MB/s"
            bytesPerSecond > 1024 -> "${(bytesPerSecond / 1024).toInt()} KB/s"
            else -> "${bytesPerSecond.toInt()} B/s"
        }
    }
    
    private fun calculateEstimatedTime(taskSnapshot: UploadTask.TaskSnapshot): String {
        val remainingBytes = taskSnapshot.totalByteCount - taskSnapshot.bytesTransferred
        val elapsedTimeMs = System.currentTimeMillis() - (taskSnapshot.task.snapshot.metadata?.creationTimeMillis ?: System.currentTimeMillis())
        val elapsedSeconds = elapsedTimeMs.toDouble() / 1000.0
        
        if (elapsedSeconds <= 0) return "Calculating..."
        
        val bytesPerSecond = taskSnapshot.bytesTransferred.toDouble() / elapsedSeconds
        
        if (bytesPerSecond <= 0) return "Calculating..."
        
        val remainingSeconds = (remainingBytes.toDouble() / bytesPerSecond).toInt()
        
        return when {
            remainingSeconds > 60 -> "${remainingSeconds / 60}m ${remainingSeconds % 60}s"
            else -> "${remainingSeconds}s"
        }
    }
}