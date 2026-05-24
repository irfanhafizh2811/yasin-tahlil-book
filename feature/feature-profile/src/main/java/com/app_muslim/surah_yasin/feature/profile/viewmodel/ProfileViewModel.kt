package com.app_muslim.surah_yasin.feature.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought
import com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService
import com.app_muslim.surah_yasin.feature.profile.model.*
import com.app_muslim.surah_yasin.feature.profile.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadUserProfile()
    }

    fun handleProfileEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.StartEditing -> startEditing()
            ProfileEvent.SaveProfile -> saveProfile()
            ProfileEvent.CancelEditing -> cancelEditing()
            is ProfileEvent.UpdateDisplayName -> updateDisplayName(event.name)
            is ProfileEvent.UpdateEmail -> updateEmail(event.email)
            is ProfileEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phoneNumber)
            is ProfileEvent.UpdateCulturalRegion -> updateCulturalRegion(event.region)
            is ProfileEvent.UpdateSchoolOfThought -> updateSchoolOfThought(event.school)
            is ProfileEvent.UpdateLanguage -> updateLanguage(event.language)
            is ProfileEvent.UpdateArabicTextVisibility -> updateArabicTextVisibility(event.show)
            is ProfileEvent.UpdateTransliterationVisibility -> updateTransliterationVisibility(event.show)
            is ProfileEvent.UpdatePrivacySetting -> updatePrivacySetting(event.setting, event.value)
            is ProfileEvent.UpdateNotificationSetting -> updateNotificationSetting(event.setting, event.value)
            is ProfileEvent.UpdatePrayerPreference -> updatePrayerPreference(event.setting, event.value)
            is ProfileEvent.UpdateAccountSetting -> updateAccountSetting(event.setting, event.value)
            is ProfileEvent.SelectProfilePhoto -> selectProfilePhoto(event.uri)
            ProfileEvent.RemoveProfilePhoto -> removeProfilePhoto()
            ProfileEvent.ShowPhotoSelector -> showPhotoSelector()
            ProfileEvent.HidePhotoSelector -> hidePhotoSelector()
            ProfileEvent.ClearError -> clearError()
        }
    }

    private fun loadUserProfile() {
        val currentUserId = authService.currentUser?.uid
        if (currentUserId == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "User not authenticated"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            profileRepository.getUserProfile(currentUserId)
                .onSuccess { profile ->
                    val profileData = profile ?: createDefaultProfile(currentUserId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profileData = profileData
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to load user profile", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile: ${error.message}"
                    )
                }
        }
    }

    private fun createDefaultProfile(userId: String): ProfileData {
        val firebaseUser = authService.currentUser
        return ProfileData(
            userId = userId,
            displayName = firebaseUser?.displayName ?: "",
            email = firebaseUser?.email,
            phoneNumber = firebaseUser?.phoneNumber,
            profilePhotoUrl = firebaseUser?.photoUrl?.toString()
        )
    }

    private fun startEditing() {
        _uiState.value = _uiState.value.copy(isEditing = true)
        validateForm()
    }

    private fun cancelEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = false,
            showPhotoSelector = false
        )
        loadUserProfile() // Reload to discard changes
    }

    private fun saveProfile() {
        val currentProfile = _uiState.value.profileData ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Validate profile data first
            profileRepository.validateProfileData(currentProfile)
                .onSuccess {
                    // Save the profile
                    profileRepository.saveUserProfile(currentProfile)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isEditing = false,
                                isProfileSaved = true,
                                profileData = currentProfile.copy(
                                    profileCompleteness = currentProfile.calculateCompleteness()
                                )
                            )
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Failed to save profile", error)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Failed to save profile: ${error.message}"
                            )
                        }
                }
                .onFailure { error ->
                    Log.e(TAG, "Profile validation failed", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    private fun updateDisplayName(name: String) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(displayName = name)
        )
        validateForm()
    }

    private fun updateEmail(email: String) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(email = email.takeIf { it.isNotBlank() })
        )
        validateForm()
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(phoneNumber = phoneNumber.takeIf { it.isNotBlank() })
        )
        validateForm()
    }

    private fun updateCulturalRegion(region: IslamicRegion) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(
                culturalPreferences = currentProfile.culturalPreferences.copy(region = region)
            )
        )
    }

    private fun updateSchoolOfThought(school: SchoolOfThought) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(
                culturalPreferences = currentProfile.culturalPreferences.copy(schoolOfThought = school)
            )
        )
    }

    private fun updateLanguage(language: String) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(
                culturalPreferences = currentProfile.culturalPreferences.copy(primaryLanguage = language)
            )
        )
    }

    private fun updateArabicTextVisibility(show: Boolean) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(
                culturalPreferences = currentProfile.culturalPreferences.copy(showArabicText = show)
            )
        )
    }

    private fun updateTransliterationVisibility(show: Boolean) {
        val currentProfile = _uiState.value.profileData ?: return
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(
                culturalPreferences = currentProfile.culturalPreferences.copy(showTransliteration = show)
            )
        )
    }

    private fun updatePrivacySetting(setting: String, value: Any) {
        val currentProfile = _uiState.value.profileData ?: return
        val currentPrivacy = currentProfile.privacySettings
        
        val updatedPrivacy = when (setting) {
            "profileVisibility" -> currentPrivacy.copy(profileVisibility = value as String)
            "prayerStatsVisibility" -> currentPrivacy.copy(prayerStatsVisibility = value as String)
            "memorialListVisibility" -> currentPrivacy.copy(memorialListVisibility = value as String)
            "allowFamilyInvitations" -> currentPrivacy.copy(allowFamilyInvitations = value as Boolean)
            "allowCommunityInteraction" -> currentPrivacy.copy(allowCommunityInteraction = value as Boolean)
            "shareAnonymousStats" -> currentPrivacy.copy(shareAnonymousStats = value as Boolean)
            "dataProcessingConsent" -> currentPrivacy.copy(dataProcessingConsent = value as Boolean)
            "marketingConsent" -> currentPrivacy.copy(marketingConsent = value as Boolean)
            else -> currentPrivacy
        }
        
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(privacySettings = updatedPrivacy)
        )
    }

    private fun updateNotificationSetting(setting: String, value: Any) {
        val currentProfile = _uiState.value.profileData ?: return
        val currentNotifications = currentProfile.notificationPreferences
        
        val updatedNotifications = when (setting) {
            "memorialReminders" -> currentNotifications.copy(memorialReminders = value as Boolean)
            "fridayNightReminders" -> currentNotifications.copy(fridayNightReminders = value as Boolean)
            "prayerSessionReminders" -> currentNotifications.copy(prayerSessionReminders = value as Boolean)
            "communityUpdates" -> currentNotifications.copy(communityUpdates = value as Boolean)
            "familyInvitations" -> currentNotifications.copy(familyInvitations = value as Boolean)
            "prayerMilestones" -> currentNotifications.copy(prayerMilestones = value as Boolean)
            "soundEnabled" -> currentNotifications.copy(soundEnabled = value as Boolean)
            "vibrationEnabled" -> currentNotifications.copy(vibrationEnabled = value as Boolean)
            "quietHoursEnabled" -> currentNotifications.copy(quietHoursEnabled = value as Boolean)
            "quietHoursStart" -> currentNotifications.copy(quietHoursStart = value as String)
            "quietHoursEnd" -> currentNotifications.copy(quietHoursEnd = value as String)
            else -> currentNotifications
        }
        
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(notificationPreferences = updatedNotifications)
        )
    }

    private fun updatePrayerPreference(setting: String, value: Any) {
        val currentProfile = _uiState.value.profileData ?: return
        val currentPreferences = currentProfile.prayerPreferences
        
        val updatedPreferences = when (setting) {
            "defaultPrayerType" -> currentPreferences.copy(defaultPrayerType = value as String)
            "enableHapticFeedback" -> currentPreferences.copy(enableHapticFeedback = value as Boolean)
            "enableSoundEffects" -> currentPreferences.copy(enableSoundEffects = value as Boolean)
            "autoSaveProgress" -> currentPreferences.copy(autoSaveProgress = value as Boolean)
            "sessionReminderInterval" -> currentPreferences.copy(sessionReminderInterval = value as Int)
            "enableProgressSharing" -> currentPreferences.copy(enableProgressSharing = value as Boolean)
            "trackDetailedStats" -> currentPreferences.copy(trackDetailedStats = value as Boolean)
            else -> currentPreferences
        }
        
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(prayerPreferences = updatedPreferences)
        )
    }

    private fun updateAccountSetting(setting: String, value: Any) {
        val currentProfile = _uiState.value.profileData ?: return
        val currentSettings = currentProfile.accountSettings
        
        val updatedSettings = when (setting) {
            "theme" -> currentSettings.copy(theme = value as String)
            "language" -> currentSettings.copy(language = value as String)
            "dateFormat" -> currentSettings.copy(dateFormat = value as String)
            "timeFormat" -> currentSettings.copy(timeFormat = value as String)
            "enableAnalytics" -> currentSettings.copy(enableAnalytics = value as Boolean)
            "enableCrashReporting" -> currentSettings.copy(enableCrashReporting = value as Boolean)
            "autoBackup" -> currentSettings.copy(autoBackup = value as Boolean)
            "twoFactorEnabled" -> currentSettings.copy(twoFactorEnabled = value as Boolean)
            else -> currentSettings
        }
        
        _uiState.value = _uiState.value.copy(
            profileData = currentProfile.copy(accountSettings = updatedSettings)
        )
    }

    private fun selectProfilePhoto(uri: Uri) {
        val currentProfile = _uiState.value.profileData ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                showPhotoSelector = false
            )

            profileRepository.uploadProfilePhoto(currentProfile.userId, uri)
                .onSuccess { downloadUrl ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profileData = currentProfile.copy(
                            profilePhotoUrl = downloadUrl,
                            profilePhotoUri = uri
                        )
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to upload profile photo", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to upload photo: ${error.message}"
                    )
                }
        }
    }

    private fun removeProfilePhoto() {
        val currentProfile = _uiState.value.profileData ?: return
        val photoUrl = currentProfile.profilePhotoUrl ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            profileRepository.deleteProfilePhoto(currentProfile.userId, photoUrl)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profileData = currentProfile.copy(
                            profilePhotoUrl = null,
                            profilePhotoUri = null
                        )
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to delete profile photo", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete photo: ${error.message}"
                    )
                }
        }
    }

    private fun showPhotoSelector() {
        _uiState.value = _uiState.value.copy(showPhotoSelector = true)
    }

    private fun hidePhotoSelector() {
        _uiState.value = _uiState.value.copy(showPhotoSelector = false)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun validateForm() {
        val profileData = _uiState.value.profileData ?: return
        
        val displayNameError = when {
            profileData.displayName.isBlank() -> "Display name is required"
            profileData.displayName.length < 2 -> "Display name must be at least 2 characters"
            else -> null
        }
        
        val emailError = when {
            profileData.email?.isNotBlank() == true && 
            !android.util.Patterns.EMAIL_ADDRESS.matcher(profileData.email).matches() -> "Invalid email format"
            else -> null
        }
        
        val phoneNumberError = when {
            profileData.phoneNumber?.isNotBlank() == true && 
            !android.util.Patterns.PHONE.matcher(profileData.phoneNumber).matches() -> "Invalid phone number format"
            else -> null
        }
        
        _formState.value = ProfileFormState(
            displayNameError = displayNameError,
            emailError = emailError,
            phoneNumberError = phoneNumberError,
            isValid = displayNameError == null && emailError == null && phoneNumberError == null
        )
    }
}