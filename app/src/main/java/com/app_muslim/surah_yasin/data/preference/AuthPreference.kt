package com.app_muslim.surah_yasin.data.preference

import com.app_muslim.surah_yasin.data.model.IslamicRegion
import com.app_muslim.surah_yasin.data.model.SchoolOfThought
import com.app_muslim.surah_yasin.utils.TextUtils

class AuthPreference(private val corePreference: CorePreference) {

    companion object {
        const val USER_ID = "AUTH_USER_ID"
        const val IS_AUTHENTICATED = "AUTH_IS_AUTHENTICATED"
        const val DISPLAY_NAME = "AUTH_DISPLAY_NAME"
        const val EMAIL = "AUTH_EMAIL"
        const val PHONE_NUMBER = "AUTH_PHONE_NUMBER"
        const val PHOTO_URL = "AUTH_PHOTO_URL"
        const val IS_ANONYMOUS = "AUTH_IS_ANONYMOUS"
        const val AUTH_PROVIDER = "AUTH_PROVIDER"
        const val LAST_LOGIN_TIME = "AUTH_LAST_LOGIN_TIME"
        
        // Cultural preferences
        const val CULTURAL_SETUP_COMPLETED = "CULTURAL_SETUP_COMPLETED"
        const val ISLAMIC_REGION = "ISLAMIC_REGION"
        const val PRIMARY_LANGUAGE = "PRIMARY_LANGUAGE"
        const val SCHOOL_OF_THOUGHT = "SCHOOL_OF_THOUGHT"
        const val SHOW_ARABIC_TEXT = "SHOW_ARABIC_TEXT"
        const val SHOW_TRANSLITERATION = "SHOW_TRANSLITERATION"
        const val ARABIC_FONT_SIZE = "ARABIC_FONT_SIZE"
        const val TRANSLATION_FONT_SIZE = "TRANSLATION_FONT_SIZE"
        
        // Profile completion
        const val PROFILE_COMPLETENESS = "PROFILE_COMPLETENESS"
        const val IS_PROFILE_VERIFIED = "IS_PROFILE_VERIFIED"
    }

    // Authentication state
    var userId: String
        set(value) = corePreference.setString(USER_ID, value)
        get() = corePreference.getString(USER_ID, TextUtils.BLANK)

    var isAuthenticated: Boolean
        set(value) = corePreference.setBoolean(IS_AUTHENTICATED, value)
        get() = corePreference.getBoolean(IS_AUTHENTICATED, false)

    var displayName: String
        set(value) = corePreference.setString(DISPLAY_NAME, value)
        get() = corePreference.getString(DISPLAY_NAME, TextUtils.BLANK)

    var email: String
        set(value) = corePreference.setString(EMAIL, value)
        get() = corePreference.getString(EMAIL, TextUtils.BLANK)

    var phoneNumber: String
        set(value) = corePreference.setString(PHONE_NUMBER, value)
        get() = corePreference.getString(PHONE_NUMBER, TextUtils.BLANK)

    var photoUrl: String
        set(value) = corePreference.setString(PHOTO_URL, value)
        get() = corePreference.getString(PHOTO_URL, TextUtils.BLANK)

    var isAnonymous: Boolean
        set(value) = corePreference.setBoolean(IS_ANONYMOUS, value)
        get() = corePreference.getBoolean(IS_ANONYMOUS, false)

    var authProvider: String
        set(value) = corePreference.setString(AUTH_PROVIDER, value)
        get() = corePreference.getString(AUTH_PROVIDER, "email")

    var lastLoginTime: Long
        set(value) = corePreference.setLong(LAST_LOGIN_TIME, value)
        get() = corePreference.getLong(LAST_LOGIN_TIME, 0L)

    // Cultural setup
    var culturalSetupCompleted: Boolean
        set(value) = corePreference.setBoolean(CULTURAL_SETUP_COMPLETED, value)
        get() = corePreference.getBoolean(CULTURAL_SETUP_COMPLETED, false)

    var islamicRegion: IslamicRegion
        set(value) = corePreference.setString(ISLAMIC_REGION, value.name)
        get() {
            val regionName = corePreference.getString(ISLAMIC_REGION, IslamicRegion.NOT_SPECIFIED.name)
            return try {
                IslamicRegion.valueOf(regionName)
            } catch (e: IllegalArgumentException) {
                IslamicRegion.NOT_SPECIFIED
            }
        }

    var primaryLanguage: String
        set(value) = corePreference.setString(PRIMARY_LANGUAGE, value)
        get() = corePreference.getString(PRIMARY_LANGUAGE, "en")

    var schoolOfThought: SchoolOfThought
        set(value) = corePreference.setString(SCHOOL_OF_THOUGHT, value.name)
        get() {
            val schoolName = corePreference.getString(SCHOOL_OF_THOUGHT, SchoolOfThought.NOT_SPECIFIED.name)
            return try {
                SchoolOfThought.valueOf(schoolName)
            } catch (e: IllegalArgumentException) {
                SchoolOfThought.NOT_SPECIFIED
            }
        }

    var showArabicText: Boolean
        set(value) = corePreference.setBoolean(SHOW_ARABIC_TEXT, value)
        get() = corePreference.getBoolean(SHOW_ARABIC_TEXT, true)

    var showTransliteration: Boolean
        set(value) = corePreference.setBoolean(SHOW_TRANSLITERATION, value)
        get() = corePreference.getBoolean(SHOW_TRANSLITERATION, true)

    var arabicFontSize: Float
        set(value) = corePreference.setFloat(ARABIC_FONT_SIZE, value)
        get() = corePreference.getFloat(ARABIC_FONT_SIZE, 1.0f)

    var translationFontSize: Float
        set(value) = corePreference.setFloat(TRANSLATION_FONT_SIZE, value)
        get() = corePreference.getFloat(TRANSLATION_FONT_SIZE, 1.0f)

    // Profile state
    var profileCompleteness: Int
        set(value) = corePreference.setInt(PROFILE_COMPLETENESS, value)
        get() = corePreference.getInt(PROFILE_COMPLETENESS, 0)

    var isProfileVerified: Boolean
        set(value) = corePreference.setBoolean(IS_PROFILE_VERIFIED, value)
        get() = corePreference.getBoolean(IS_PROFILE_VERIFIED, false)

    /**
     * Clear all authentication data (for logout)
     */
    fun clearAuthData() {
        userId = TextUtils.BLANK
        isAuthenticated = false
        displayName = TextUtils.BLANK
        email = TextUtils.BLANK
        phoneNumber = TextUtils.BLANK
        photoUrl = TextUtils.BLANK
        isAnonymous = false
        authProvider = "email"
        lastLoginTime = 0L
    }

    /**
     * Clear cultural setup (for reset)
     */
    fun clearCulturalData() {
        culturalSetupCompleted = false
        islamicRegion = IslamicRegion.NOT_SPECIFIED
        schoolOfThought = SchoolOfThought.NOT_SPECIFIED
        // Keep language and display preferences
    }

    /**
     * Check if user needs cultural setup
     */
    fun needsCulturalSetup(): Boolean {
        return !culturalSetupCompleted || 
               islamicRegion == IslamicRegion.NOT_SPECIFIED ||
               schoolOfThought == SchoolOfThought.NOT_SPECIFIED
    }

    /**
     * Check if profile is complete
     */
    fun isProfileComplete(): Boolean {
        return culturalSetupCompleted && 
               displayName.isNotBlank() && 
               islamicRegion != IslamicRegion.NOT_SPECIFIED &&
               profileCompleteness >= 80
    }

    /**
     * Update profile from Firebase UserProfile
     */
    fun updateFromUserProfile(userProfile: com.app_muslim.surah_yasin.data.model.UserProfile) {
        displayName = userProfile.displayName
        email = userProfile.email ?: ""
        phoneNumber = userProfile.phoneNumber ?: ""
        photoUrl = userProfile.profilePhotoUrl ?: ""
        isProfileVerified = userProfile.isVerified
        profileCompleteness = userProfile.profileCompleteness
        
        // Update cultural preferences
        islamicRegion = userProfile.culturalPreferences.region
        primaryLanguage = userProfile.culturalPreferences.primaryLanguage
        schoolOfThought = userProfile.culturalPreferences.schoolOfThought
        showArabicText = userProfile.culturalPreferences.showArabicText
        showTransliteration = userProfile.culturalPreferences.showTransliteration
        arabicFontSize = userProfile.culturalPreferences.arabicFontSize.scaleFactor
        translationFontSize = userProfile.culturalPreferences.translationFontSize.scaleFactor
        
        culturalSetupCompleted = true
    }
}