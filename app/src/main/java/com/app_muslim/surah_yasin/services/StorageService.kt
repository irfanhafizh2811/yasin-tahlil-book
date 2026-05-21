package com.app_muslim.surah_yasin.services

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.InputStream
import java.util.UUID

class StorageService(private val storage: FirebaseStorage) {
    
    companion object {
        private const val MEMORIALS_PATH = "memorials"
        private const val PROFILE_PHOTOS_PATH = "users"
        private const val TEMP_UPLOADS_PATH = "temp"
    }
    
    // Memorial Photo Operations
    suspend fun uploadMemorialPhoto(
        memorialId: String, 
        photoUri: Uri, 
        fileName: String? = null
    ): Result<String> {
        return try {
            val actualFileName = fileName ?: "${UUID.randomUUID()}.jpg"
            val photoRef = storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$actualFileName")
            
            val uploadTask = photoRef.putFile(photoUri).await()
            val downloadUrl = photoRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun uploadMemorialPhotoFromBytes(
        memorialId: String,
        photoBytes: ByteArray,
        fileName: String? = null
    ): Result<String> {
        return try {
            val actualFileName = fileName ?: "${UUID.randomUUID()}.jpg"
            val photoRef = storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$actualFileName")
            
            val uploadTask = photoRef.putBytes(photoBytes).await()
            val downloadUrl = photoRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun uploadMemorialPhotoFromStream(
        memorialId: String,
        photoStream: InputStream,
        fileName: String? = null
    ): Result<String> {
        return try {
            val actualFileName = fileName ?: "${UUID.randomUUID()}.jpg"
            val photoRef = storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$actualFileName")
            
            val uploadTask = photoRef.putStream(photoStream).await()
            val downloadUrl = photoRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Delete memorial photo
    suspend fun deleteMemorialPhoto(memorialId: String, fileName: String): Result<Unit> {
        return try {
            storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$fileName")
                .delete()
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Delete all photos for a memorial
    suspend fun deleteAllMemorialPhotos(memorialId: String): Result<Unit> {
        return try {
            val memorialRef = storage.reference.child("$MEMORIALS_PATH/$memorialId")
            val listResult = memorialRef.listAll().await()
            
            // Delete all items in the memorial folder
            listResult.items.forEach { item ->
                item.delete().await()
            }
            
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Get download URL for memorial photo
    suspend fun getMemorialPhotoUrl(memorialId: String, fileName: String): Result<String> {
        return try {
            val downloadUrl = storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$fileName")
                .downloadUrl
                .await()
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // List all photos for a memorial
    suspend fun listMemorialPhotos(memorialId: String): Result<List<StorageReference>> {
        return try {
            val memorialRef = storage.reference.child("$MEMORIALS_PATH/$memorialId")
            val listResult = memorialRef.listAll().await()
            Result.success(listResult.items)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Profile Photo Operations
    suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): Result<String> {
        return try {
            val photoRef = storage.reference
                .child("$PROFILE_PHOTOS_PATH/$userId/profile/profile_photo.jpg")
            
            val uploadTask = photoRef.putFile(photoUri).await()
            val downloadUrl = photoRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun uploadProfilePhotoFromBytes(userId: String, photoBytes: ByteArray): Result<String> {
        return try {
            val photoRef = storage.reference
                .child("$PROFILE_PHOTOS_PATH/$userId/profile/profile_photo.jpg")
            
            val uploadTask = photoRef.putBytes(photoBytes).await()
            val downloadUrl = photoRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun deleteProfilePhoto(userId: String): Result<Unit> {
        return try {
            storage.reference
                .child("$PROFILE_PHOTOS_PATH/$userId/profile/profile_photo.jpg")
                .delete()
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun getProfilePhotoUrl(userId: String): Result<String> {
        return try {
            val downloadUrl = storage.reference
                .child("$PROFILE_PHOTOS_PATH/$userId/profile/profile_photo.jpg")
                .downloadUrl
                .await()
            Result.success(downloadUrl.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Temporary Upload Operations (for image processing before final upload)
    suspend fun uploadTempFile(fileBytes: ByteArray, fileExtension: String = "jpg"): Result<Pair<String, String>> {
        return try {
            val tempFileName = "${UUID.randomUUID()}.$fileExtension"
            val tempRef = storage.reference
                .child("$TEMP_UPLOADS_PATH/$tempFileName")
            
            val uploadTask = tempRef.putBytes(fileBytes).await()
            val downloadUrl = tempRef.downloadUrl.await()
            
            // Return both the file path and download URL
            Result.success(Pair(tempFileName, downloadUrl.toString()))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun deleteTempFile(tempFileName: String): Result<Unit> {
        return try {
            storage.reference
                .child("$TEMP_UPLOADS_PATH/$tempFileName")
                .delete()
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Move temp file to final location
    suspend fun moveTempFileToMemorial(
        tempFileName: String, 
        memorialId: String, 
        finalFileName: String? = null
    ): Result<String> {
        return try {
            // Download the temp file
            val tempRef = storage.reference.child("$TEMP_UPLOADS_PATH/$tempFileName")
            val bytes = tempRef.getBytes(10 * 1024 * 1024).await() // 10MB max
            
            // Upload to final location
            val actualFinalFileName = finalFileName ?: tempFileName
            val result = uploadMemorialPhotoFromBytes(memorialId, bytes, actualFinalFileName)
            
            // Delete temp file
            deleteTempFile(tempFileName)
            
            result
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Upload with progress tracking (returns UploadTask for progress monitoring)
    fun uploadMemorialPhotoWithProgress(
        memorialId: String,
        photoUri: Uri,
        fileName: String? = null
    ): UploadTask {
        val actualFileName = fileName ?: "${UUID.randomUUID()}.jpg"
        val photoRef = storage.reference
            .child("$MEMORIALS_PATH/$memorialId/$actualFileName")
        
        return photoRef.putFile(photoUri)
    }
    
    // Download file to local storage
    suspend fun downloadMemorialPhoto(
        memorialId: String,
        fileName: String,
        localFile: File
    ): Result<Unit> {
        return try {
            storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$fileName")
                .getFile(localFile)
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Get file metadata
    suspend fun getFileMetadata(memorialId: String, fileName: String): Result<Map<String, Any?>> {
        return try {
            val metadata = storage.reference
                .child("$MEMORIALS_PATH/$memorialId/$fileName")
                .metadata
                .await()
                
            val metadataMap = mapOf(
                "bucket" to metadata.bucket,
                "generation" to metadata.generation,
                "metadataGeneration" to metadata.metadataGeneration,
                "path" to metadata.path,
                "name" to metadata.name,
                "sizeBytes" to metadata.sizeBytes,
                "timeCreated" to metadata.creationTimeMillis,
                "updated" to metadata.updatedTimeMillis,
                "contentType" to metadata.contentType,
                "contentLanguage" to metadata.contentLanguage,
                "contentEncoding" to metadata.contentEncoding,
                "md5Hash" to metadata.md5Hash
            )
            
            Result.success(metadataMap)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}