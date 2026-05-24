package com.app_muslim.surah_yasin.feature.profile.repository

import android.net.Uri
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    
    suspend fun getUserProfile(userId: String): Result<ProfileData?>
    
    fun getUserProfileFlow(userId: String): Flow<ProfileData?>
    
    suspend fun saveUserProfile(profileData: ProfileData): Result<Unit>
    
    suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): Result<String>
    
    suspend fun deleteProfilePhoto(userId: String, photoUrl: String): Result<Unit>
    
    suspend fun updateProfileField(userId: String, field: String, value: Any): Result<Unit>
    
    suspend fun syncWithFirestore(userId: String): Result<Unit>
    
    suspend fun getProfileCompleteness(userId: String): Result<Int>
    
    suspend fun validateProfileData(profileData: ProfileData): Result<Unit>
}