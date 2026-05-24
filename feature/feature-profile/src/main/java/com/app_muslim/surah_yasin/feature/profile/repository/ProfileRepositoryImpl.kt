package com.app_muslim.surah_yasin.feature.profile.repository

import android.net.Uri
import android.util.Log
import com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authService: FirebaseAuthService
) : ProfileRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val PROFILE_PHOTOS_PATH = "users/%s/profile"
        private const val TAG = "ProfileRepository"
    }

    override suspend fun getUserProfile(userId: String): Result<ProfileData?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
                
            if (document.exists()) {
                val profileData = document.toObject<ProfileData>()
                Result.success(profileData)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            Result.failure(e)
        }
    }

    override fun getUserProfileFlow(userId: String): Flow<ProfileData?> = callbackFlow {
        val listener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to profile changes", error)
                    return@addSnapshotListener
                }
                
                val profileData = if (snapshot?.exists() == true) {
                    snapshot.toObject<ProfileData>()
                } else {
                    null
                }
                
                trySend(profileData)
            }
            
        awaitClose { listener.remove() }
    }

    override suspend fun saveUserProfile(profileData: ProfileData): Result<Unit> {
        return try {
            val userProfileMap = mapOf(
                "userId" to profileData.userId,
                "displayName" to profileData.displayName,
                "email" to profileData.email,
                "phoneNumber" to profileData.phoneNumber,
                "profilePhotoUrl" to profileData.profilePhotoUrl,
                "culturalPreferences" to mapOf(
                    "region" to profileData.culturalPreferences.region.name,
                    "country" to profileData.culturalPreferences.country,
                    "primaryLanguage" to profileData.culturalPreferences.primaryLanguage,
                    "secondaryLanguages" to profileData.culturalPreferences.secondaryLanguages,
                    "schoolOfThought" to profileData.culturalPreferences.schoolOfThought.name,
                    "showArabicText" to profileData.culturalPreferences.showArabicText,
                    "showTransliteration" to profileData.culturalPreferences.showTransliteration,
                    "arabicFontSize" to profileData.culturalPreferences.arabicFontSize,
                    "translationFontSize" to profileData.culturalPreferences.translationFontSize
                ),
                "notificationPreferences" to profileData.notificationPreferences,
                "privacySettings" to profileData.privacySettings,
                "prayerPreferences" to profileData.prayerPreferences,
                "accountSettings" to profileData.accountSettings,
                "isVerified" to profileData.isVerified,
                "profileCompleteness" to profileData.calculateCompleteness(),
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            firestore.collection(USERS_COLLECTION)
                .document(profileData.userId)
                .set(userProfileMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile", e)
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): Result<String> {
        return try {
            val fileName = "profile_${UUID.randomUUID()}.jpg"
            val photoRef = storage.reference
                .child(PROFILE_PHOTOS_PATH.format(userId))
                .child(fileName)

            val uploadTask = photoRef.putFile(photoUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            // Update profile with new photo URL
            updateProfileField(userId, "profilePhotoUrl", downloadUrl)

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile photo", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProfilePhoto(userId: String, photoUrl: String): Result<Unit> {
        return try {
            // Extract file name from URL and delete from storage
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.delete().await()

            // Remove photo URL from profile
            updateProfileField(userId, "profilePhotoUrl", "")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting profile photo", e)
            Result.failure(e)
        }
    }

    override suspend fun updateProfileField(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            val updates = mapOf(
                field to value,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile field: $field", e)
            Result.failure(e)
        }
    }

    override suspend fun syncWithFirestore(userId: String): Result<Unit> {
        return try {
            // This would typically sync local Room data with Firestore
            // For now, we'll just verify the connection
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing with Firestore", e)
            Result.failure(e)
        }
    }

    override suspend fun getProfileCompleteness(userId: String): Result<Int> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            val completeness = profile?.calculateCompleteness() ?: 0
            Result.success(completeness)
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating profile completeness", e)
            Result.failure(e)
        }
    }

    override suspend fun validateProfileData(profileData: ProfileData): Result<Unit> {
        return try {
            // Basic validation
            when {
                profileData.displayName.isBlank() -> 
                    throw IllegalArgumentException("Display name cannot be empty")
                profileData.displayName.length < 2 -> 
                    throw IllegalArgumentException("Display name must be at least 2 characters")
                profileData.email?.isNotBlank() == true && !android.util.Patterns.EMAIL_ADDRESS.matcher(profileData.email).matches() -> 
                    throw IllegalArgumentException("Invalid email format")
                profileData.phoneNumber?.isNotBlank() == true && !android.util.Patterns.PHONE.matcher(profileData.phoneNumber).matches() -> 
                    throw IllegalArgumentException("Invalid phone number format")
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Profile validation failed", e)
            Result.failure(e)
        }
    }
}