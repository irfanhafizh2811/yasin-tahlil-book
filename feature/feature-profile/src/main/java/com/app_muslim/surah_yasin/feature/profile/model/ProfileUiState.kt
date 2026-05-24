package com.app_muslim.surah_yasin.feature.profile.model

import android.net.Uri
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditing: Boolean = false,
    val profileData: ProfileData? = null,
    val isProfileSaved: Boolean = false,
    val showPhotoSelector: Boolean = false,
    val photoUploadProgress: Float? = null
)

data class ProfileData(
    val userId: String = "",
    val displayName: String = "",
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePhotoUrl: String? = null,
    val profilePhotoUri: Uri? = null, // For local photo selection
    val culturalPreferences: CulturalPreferencesData = CulturalPreferencesData(),
    val notificationPreferences: NotificationPreferencesData = NotificationPreferencesData(),
    val privacySettings: PrivacySettingsData = PrivacySettingsData(),
    val prayerPreferences: PrayerPreferencesData = PrayerPreferencesData(),
    val accountSettings: AccountSettingsData = AccountSettingsData(),
    val isVerified: Boolean = false,
    val profileCompleteness: Int = 0
) {
    
    fun calculateCompleteness(): Int {
        var completeness = 0
        if (displayName.isNotBlank()) completeness += 20
        if (!profilePhotoUrl.isNullOrBlank() || profilePhotoUri != null) completeness += 15
        if (culturalPreferences.region != IslamicRegion.NOT_SPECIFIED) completeness += 15
        if (culturalPreferences.primaryLanguage.isNotBlank()) completeness += 10
        if (prayerPreferences.defaultPrayerType.isNotBlank()) completeness += 10
        if (isVerified) completeness += 20
        if (!email.isNullOrBlank()) completeness += 10
        
        return completeness.coerceIn(0, 100)
    }
}

data class CulturalPreferencesData(
    val region: IslamicRegion = IslamicRegion.NOT_SPECIFIED,
    val country: String = "",
    val primaryLanguage: String = "en",
    val secondaryLanguages: List<String> = emptyList(),
    val schoolOfThought: SchoolOfThought = SchoolOfThought.NOT_SPECIFIED,
    val showArabicText: Boolean = true,
    val showTransliteration: Boolean = true,
    val arabicFontSize: String = "MEDIUM",
    val translationFontSize: String = "MEDIUM"
)

data class NotificationPreferencesData(
    val memorialReminders: Boolean = true,
    val fridayNightReminders: Boolean = true,
    val prayerSessionReminders: Boolean = true,
    val communityUpdates: Boolean = false,
    val familyInvitations: Boolean = true,
    val prayerMilestones: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00"
)

data class PrivacySettingsData(
    val profileVisibility: String = "COMMUNITY", // PRIVATE, FRIENDS_ONLY, COMMUNITY, PUBLIC
    val prayerStatsVisibility: String = "FRIENDS_ONLY", // PRIVATE, FRIENDS_ONLY, COMMUNITY
    val memorialListVisibility: String = "PRIVATE", // PRIVATE, FRIENDS_ONLY, COMMUNITY
    val allowFamilyInvitations: Boolean = true,
    val allowCommunityInteraction: Boolean = true,
    val shareAnonymousStats: Boolean = true,
    val dataProcessingConsent: Boolean = false,
    val marketingConsent: Boolean = false
)

data class PrayerPreferencesData(
    val defaultPrayerType: String = "",
    val enableHapticFeedback: Boolean = true,
    val enableSoundEffects: Boolean = true,
    val autoSaveProgress: Boolean = true,
    val sessionReminderInterval: Int = 0, // minutes, 0 = disabled
    val preferredPrayerTimes: List<String> = emptyList(),
    val enableProgressSharing: Boolean = false,
    val trackDetailedStats: Boolean = true
)

data class AccountSettingsData(
    val theme: String = "AUTO", // LIGHT, DARK, AUTO
    val language: String = "en",
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "24h", // 12h or 24h
    val enableAnalytics: Boolean = true,
    val enableCrashReporting: Boolean = true,
    val autoBackup: Boolean = true,
    val twoFactorEnabled: Boolean = false
)

sealed class ProfileEvent {
    object StartEditing : ProfileEvent()
    object SaveProfile : ProfileEvent()
    object CancelEditing : ProfileEvent()
    data class UpdateDisplayName(val name: String) : ProfileEvent()
    data class UpdateEmail(val email: String) : ProfileEvent()
    data class UpdatePhoneNumber(val phoneNumber: String) : ProfileEvent()
    data class UpdateCulturalRegion(val region: IslamicRegion) : ProfileEvent()
    data class UpdateSchoolOfThought(val school: SchoolOfThought) : ProfileEvent()
    data class UpdateLanguage(val language: String) : ProfileEvent()
    data class UpdateArabicTextVisibility(val show: Boolean) : ProfileEvent()
    data class UpdateTransliterationVisibility(val show: Boolean) : ProfileEvent()
    data class UpdatePrivacySetting(val setting: String, val value: Any) : ProfileEvent()
    data class UpdateNotificationSetting(val setting: String, val value: Any) : ProfileEvent()
    data class UpdatePrayerPreference(val setting: String, val value: Any) : ProfileEvent()
    data class UpdateAccountSetting(val setting: String, val value: Any) : ProfileEvent()
    data class SelectProfilePhoto(val uri: Uri) : ProfileEvent()
    object RemoveProfilePhoto : ProfileEvent()
    object ShowPhotoSelector : ProfileEvent()
    object HidePhotoSelector : ProfileEvent()
    object ClearError : ProfileEvent()
}

data class ProfileFormState(
    val displayNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val isValid: Boolean = true
)