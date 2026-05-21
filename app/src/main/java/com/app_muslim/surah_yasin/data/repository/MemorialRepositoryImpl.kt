package com.app_muslim.surah_yasin.data.repository

import android.util.Log
import com.app_muslim.surah_yasin.data.database.dao.MemorialDao
import com.app_muslim.surah_yasin.data.model.*
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.services.StorageService
import com.app_muslim.surah_yasin.utils.CulturalValidator
import com.app_muslim.surah_yasin.utils.IslamicDateCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Implementation of MemorialRepository with Firebase backend and local caching.
 * 
 * Provides hybrid architecture with cloud synchronization and offline support
 * while maintaining Islamic cultural authenticity.
 */
class MemorialRepositoryImpl(
    private val firestoreService: FirestoreService,
    private val storageService: StorageService,
    private val memorialDao: MemorialDao,
    private val culturalValidator: CulturalValidator,
    private val islamicDateCalculator: IslamicDateCalculator
) : MemorialRepository {

    companion object {
        private const val TAG = "MemorialRepository"
        private const val MEMORIAL_CACHE_DURATION_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    // Memorial Management

    override suspend fun createMemorial(memorial: Memorial): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creating memorial: ${memorial.name}")
                
                // Cultural validation first
                val validationResult = culturalValidator.validateMemorial(memorial)
                if (!validationResult.isValid) {
                    return@withContext Result.failure(
                        TahlilException.CulturalException(
                            "Memorial validation failed: ${validationResult.errors.joinToString()}"
                        )
                    )
                }
                
                // Calculate Islamic expiration date (40 days)
                val expirationDate = islamicDateCalculator.calculateMemorialExpiration()
                val memorialWithExpiration = memorial.copy(
                    expiresAt = expirationDate,
                    createdAt = com.google.firebase.Timestamp.now()
                )
                
                // Create in Firestore
                val memorialId = firestoreService.createMemorial(memorialWithExpiration)
                Log.d(TAG, "Memorial created in Firestore with ID: $memorialId")
                
                // Cache locally for offline access
                val memorialEntity = memorialWithExpiration.copy(id = memorialId).toEntity()
                memorialDao.insertMemorial(memorialEntity)
                Log.d(TAG, "Memorial cached locally")
                
                Result.success(memorialId)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create memorial", e)
                Result.failure(
                    when (e) {
                        is com.google.firebase.FirebaseException -> 
                            TahlilException.NetworkException("Firebase error: ${e.message}")
                        is IllegalArgumentException -> 
                            TahlilException.ValidationException("Invalid memorial data: ${e.message}")
                        else -> e
                    }
                )
            }
        }
    }

    override fun getMemorials(userId: String): Flow<List<Memorial>> {
        return flow {
            try {
                // First emit cached data for immediate UI update
                val cachedMemorials = memorialDao.getMemorialsByUser(userId)
                if (cachedMemorials.isNotEmpty()) {
                    emit(cachedMemorials.map { it.toDomain() })
                }
                
                // Then fetch fresh data from Firestore
                firestoreService.getMemorials(userId)
                    .catch { error ->
                        Log.e(TAG, "Failed to fetch memorials from Firestore", error)
                        // Emit cached data on network error
                        emit(cachedMemorials.map { it.toDomain() })
                    }
                    .collect { firebaseMemorials ->
                        // Update cache with fresh data
                        withContext(Dispatchers.IO) {
                            val entities = firebaseMemorials.map { it.toEntity() }
                            memorialDao.insertMemorials(entities)
                        }
                        emit(firebaseMemorials)
                    }
                    
            } catch (e: Exception) {
                Log.e(TAG, "Error in getMemorials flow", e)
                // Fallback to cached data
                val cachedMemorials = memorialDao.getMemorialsByUser(userId)
                emit(cachedMemorials.map { it.toDomain() })
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getCommunityMemorials(
        region: IslamicRegion?,
        limit: Int
    ): Flow<List<Memorial>> {
        return firestoreService.getCommunityMemorials(region, limit)
            .catch { error ->
                Log.e(TAG, "Failed to fetch community memorials", error)
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun updateMemorial(memorial: Memorial): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Cultural validation
                val validationResult = culturalValidator.validateMemorial(memorial)
                if (!validationResult.isValid) {
                    return@withContext Result.failure(
                        TahlilException.CulturalException(validationResult.errors.first())
                    )
                }
                
                // Update in Firestore
                firestoreService.updateMemorial(memorial)
                
                // Update local cache
                memorialDao.updateMemorial(memorial.toEntity())
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update memorial", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteMemorial(memorialId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Delete from Firestore
                firestoreService.deleteMemorial(memorialId)
                
                // Delete from local cache
                memorialDao.deleteMemorial(memorialId)
                
                // Delete associated photos from storage
                storageService.deleteMemorialPhotos(memorialId)
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete memorial", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getMemorial(memorialId: String): Result<Memorial> {
        return withContext(Dispatchers.IO) {
            try {
                val memorial = firestoreService.getMemorial(memorialId)
                
                // Cache the memorial
                memorialDao.insertMemorial(memorial.toEntity())
                
                Result.success(memorial)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get memorial", e)
                
                // Fallback to cached version
                val cachedMemorial = memorialDao.getMemorialById(memorialId)
                if (cachedMemorial != null) {
                    Result.success(cachedMemorial.toDomain())
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    override fun observeMemorial(memorialId: String): Flow<Memorial?> {
        return firestoreService.observeMemorial(memorialId)
            .catch { error ->
                Log.e(TAG, "Error observing memorial", error)
                emit(null)
            }
            .flowOn(Dispatchers.IO)
    }

    // Prayer Session Management

    override suspend fun startPrayerSession(
        memorialId: String,
        prayerType: PrayerType,
        userId: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Verify memorial access
                val memorial = firestoreService.getMemorial(memorialId)
                if (!memorial.canUserAccess(userId)) {
                    return@withContext Result.failure(
                        TahlilException.PermissionException("No access to memorial")
                    )
                }
                
                // Check if memorial is still active (not expired)
                if (memorial.isExpired()) {
                    return@withContext Result.failure(
                        TahlilException.ValidationException("Memorial has expired")
                    )
                }
                
                val prayerSession = MemorialPrayer(
                    userId = userId,
                    memorialId = memorialId,
                    prayerType = prayerType,
                    startedAt = com.google.firebase.Timestamp.now(),
                    completed = false
                )
                
                val sessionId = firestoreService.startPrayerSession(prayerSession)
                Log.d(TAG, "Prayer session started: $sessionId")
                
                Result.success(sessionId)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start prayer session", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun completePrayerSession(
        sessionId: String,
        completed: Boolean,
        notes: String?
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val completedAt = if (completed) {
                    com.google.firebase.Timestamp.now()
                } else null
                
                firestoreService.completePrayerSession(sessionId, completed, completedAt, notes)
                Log.d(TAG, "Prayer session completed: $sessionId")
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to complete prayer session", e)
                Result.failure(e)
            }
        }
    }

    override fun getUserPrayerSessions(
        userId: String,
        limit: Int
    ): Flow<List<MemorialPrayer>> {
        return firestoreService.getUserPrayerSessions(userId, limit)
            .catch { error ->
                Log.e(TAG, "Failed to fetch user prayer sessions", error)
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getMemorialPrayerSessions(memorialId: String): Flow<List<MemorialPrayer>> {
        return firestoreService.getMemorialPrayerSessions(memorialId)
            .catch { error ->
                Log.e(TAG, "Failed to fetch memorial prayer sessions", error)
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }

    // Family & Community Management

    override suspend fun addFamilyMembers(
        memorialId: String,
        familyMemberIds: List<String>
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                firestoreService.addFamilyMembers(memorialId, familyMemberIds)
                Log.d(TAG, "Added family members to memorial: $memorialId")
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add family members", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun removeFamilyMembers(
        memorialId: String,
        familyMemberIds: List<String>
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                firestoreService.removeFamilyMembers(memorialId, familyMemberIds)
                Log.d(TAG, "Removed family members from memorial: $memorialId")
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove family members", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun updateMemorialPrivacy(
        memorialId: String,
        privacy: MemorialPrivacy
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                firestoreService.updateMemorialPrivacy(memorialId, privacy)
                
                // Update local cache
                memorialDao.updateMemorialPrivacy(memorialId, privacy.name)
                
                Log.d(TAG, "Updated memorial privacy: $memorialId to $privacy")
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update memorial privacy", e)
                Result.failure(e)
            }
        }
    }

    // Statistics & Analytics

    override fun getGlobalPrayerStats(): Flow<GlobalPrayerStats> {
        return firestoreService.getGlobalPrayerStats()
            .catch { error ->
                Log.e(TAG, "Failed to fetch global prayer stats", error)
                emit(GlobalPrayerStats())
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getRegionalStats(region: IslamicRegion): Flow<RegionalStats> {
        return firestoreService.getRegionalStats(region)
            .catch { error ->
                Log.e(TAG, "Failed to fetch regional stats", error)
                emit(RegionalStats(region = region))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getMemorialStats(memorialId: String): Result<MemorialStats> {
        return withContext(Dispatchers.IO) {
            try {
                val stats = firestoreService.getMemorialStats(memorialId)
                Result.success(stats)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get memorial stats", e)
                Result.failure(e)
            }
        }
    }

    // Offline Support

    override suspend fun syncWithCloud(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Get all local memorials that need syncing
                val unsyncedMemorials = memorialDao.getUnsyncedMemorials()
                
                for (memorial in unsyncedMemorials) {
                    try {
                        if (memorial.isLocalOnly) {
                            // Create in cloud
                            val cloudId = firestoreService.createMemorial(memorial.toDomain())
                            memorialDao.markAsSynced(memorial.id, cloudId)
                        } else {
                            // Update in cloud
                            firestoreService.updateMemorial(memorial.toDomain())
                            memorialDao.markAsSynced(memorial.id, memorial.id)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to sync memorial: ${memorial.id}", e)
                        // Continue with other memorials
                    }
                }
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync with cloud", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getCachedMemorials(userId: String): List<Memorial> {
        return withContext(Dispatchers.IO) {
            memorialDao.getMemorialsByUser(userId).map { it.toDomain() }
        }
    }

    override suspend fun cacheMemorial(memorial: Memorial) {
        withContext(Dispatchers.IO) {
            memorialDao.insertMemorial(memorial.toEntity())
        }
    }

    override suspend fun clearExpiredCache() {
        withContext(Dispatchers.IO) {
            val expirationTime = System.currentTimeMillis() - MEMORIAL_CACHE_DURATION_MS
            memorialDao.deleteExpiredCache(expirationTime)
        }
    }
}

// Extension functions for data conversion

private fun Memorial.toEntity(): MemorialEntity {
    return MemorialEntity(
        id = this.id,
        name = this.name,
        arabicName = this.arabicName,
        description = this.description,
        photoUrl = this.photoUrl,
        privacy = this.privacy.name,
        createdBy = this.createdBy,
        familyMembers = this.familyMembers.joinToString(","),
        createdAt = this.createdAt.toDate().time,
        expiresAt = this.expiresAt.toDate().time,
        isActive = this.isActive,
        isLocalOnly = false,
        isSynced = true,
        lastModified = System.currentTimeMillis()
    )
}

private fun MemorialEntity.toDomain(): Memorial {
    return Memorial(
        id = this.id,
        name = this.name,
        arabicName = this.arabicName,
        description = this.description,
        photoUrl = this.photoUrl,
        privacy = MemorialPrivacy.valueOf(this.privacy),
        createdBy = this.createdBy,
        familyMembers = if (this.familyMembers.isNotEmpty()) {
            this.familyMembers.split(",")
        } else {
            emptyList()
        },
        createdAt = com.google.firebase.Timestamp(Date(this.createdAt)),
        expiresAt = com.google.firebase.Timestamp(Date(this.expiresAt)),
        isActive = this.isActive
    )
}

/**
 * Custom exceptions for Tahlil application.
 */
sealed class TahlilException : Exception() {
    data class NetworkException(override val message: String) : TahlilException()
    data class ValidationException(override val message: String) : TahlilException()
    data class CulturalException(override val message: String) : TahlilException()
    data class PermissionException(override val message: String) : TahlilException()
    data class AuthenticationException(override val message: String) : TahlilException()
}