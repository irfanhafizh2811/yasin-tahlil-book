package com.app_muslim.surah_yasin.data.model

import java.util.Date
import java.util.Locale

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePhotoUrl: String? = null,
    val culturalPreferences: CulturalPreferences,
    val notificationPreferences: NotificationPreferences,
    val privacySettings: PrivacySettings,
    val prayerPreferences: PrayerPreferences,
    val accountSettings: AccountSettings,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastLoginAt: Date? = null,
    val isVerified: Boolean = false,
    val profileCompleteness: Int = 0 // Percentage of profile completion
) {
    
    fun calculateProfileCompleteness(): Int {
        var completeness = 0
        if (displayName.isNotBlank()) completeness += 20
        if (!profilePhotoUrl.isNullOrBlank()) completeness += 15
        if (culturalPreferences.region != IslamicRegion.NOT_SPECIFIED) completeness += 15
        if (culturalPreferences.primaryLanguage.isNotBlank()) completeness += 10
        if (prayerPreferences.defaultPrayerType != null) completeness += 10
        if (isVerified) completeness += 20
        if (!email.isNullOrBlank()) completeness += 10
        
        return completeness.coerceIn(0, 100)
    }
    
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "profilePhotoUrl" to profilePhotoUrl,
            "culturalPreferences" to culturalPreferences.toMap(),
            "notificationPreferences" to notificationPreferences.toMap(),
            "privacySettings" to privacySettings.toMap(),
            "prayerPreferences" to prayerPreferences.toMap(),
            "accountSettings" to accountSettings.toMap(),
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "lastLoginAt" to lastLoginAt,
            "isVerified" to isVerified,
            "profileCompleteness" to calculateProfileCompleteness()
        )
    }
}

data class CulturalPreferences(
    val region: IslamicRegion = IslamicRegion.NOT_SPECIFIED,
    val country: String = "",
    val primaryLanguage: String = "en", // ISO language code
    val secondaryLanguages: List<String> = emptyList(),
    val preferredCalendar: CalendarType = CalendarType.GREGORIAN,
    val culturalTraditions: List<String> = emptyList(), // e.g., "40_day_memorial", "3_day_memorial"
    val schoolOfThought: SchoolOfThought = SchoolOfThought.NOT_SPECIFIED,
    val showArabicText: Boolean = true,
    val showTransliteration: Boolean = true,
    val arabicFontSize: FontSize = FontSize.MEDIUM,
    val translationFontSize: FontSize = FontSize.MEDIUM
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "region" to region.name,
            "country" to country,
            "primaryLanguage" to primaryLanguage,
            "secondaryLanguages" to secondaryLanguages,
            "preferredCalendar" to preferredCalendar.name,
            "culturalTraditions" to culturalTraditions,
            "schoolOfThought" to schoolOfThought.name,
            "showArabicText" to showArabicText,
            "showTransliteration" to showTransliteration,
            "arabicFontSize" to arabicFontSize.name,
            "translationFontSize" to translationFontSize.name
        )
    }
}

data class NotificationPreferences(
    val memorialReminders: Boolean = true,
    val fridayNightReminders: Boolean = true,
    val prayerSessionReminders: Boolean = true,
    val communityUpdates: Boolean = false,
    val familyInvitations: Boolean = true,
    val prayerMilestones: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00", // 24-hour format
    val quietHoursEnd: String = "07:00"
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "memorialReminders" to memorialReminders,
            "fridayNightReminders" to fridayNightReminders,
            "prayerSessionReminders" to prayerSessionReminders,
            "communityUpdates" to communityUpdates,
            "familyInvitations" to familyInvitations,
            "prayerMilestones" to prayerMilestones,
            "soundEnabled" to soundEnabled,
            "vibrationEnabled" to vibrationEnabled,
            "quietHoursEnabled" to quietHoursEnabled,
            "quietHoursStart" to quietHoursStart,
            "quietHoursEnd" to quietHoursEnd
        )
    }
}

data class PrivacySettings(
    val profileVisibility: ProfileVisibility = ProfileVisibility.COMMUNITY,
    val prayerStatsVisibility: StatsVisibility = StatsVisibility.FRIENDS_ONLY,
    val memorialListVisibility: StatsVisibility = StatsVisibility.PRIVATE,
    val allowFamilyInvitations: Boolean = true,
    val allowCommunityInteraction: Boolean = true,
    val shareAnonymousStats: Boolean = true,
    val dataProcessingConsent: Boolean = false,
    val marketingConsent: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "profileVisibility" to profileVisibility.name,
            "prayerStatsVisibility" to prayerStatsVisibility.name,
            "memorialListVisibility" to memorialListVisibility.name,
            "allowFamilyInvitations" to allowFamilyInvitations,
            "allowCommunityInteraction" to allowCommunityInteraction,
            "shareAnonymousStats" to shareAnonymousStats,
            "dataProcessingConsent" to dataProcessingConsent,
            "marketingConsent" to marketingConsent
        )
    }
}

data class PrayerPreferences(
    val defaultPrayerType: PrayerType? = null,
    val defaultTargetCount: Map<PrayerType, Int> = emptyMap(),
    val enableHapticFeedback: Boolean = true,
    val enableSoundEffects: Boolean = true,
    val autoSaveProgress: Boolean = true,
    val sessionReminderInterval: Int = 0, // minutes, 0 = disabled
    val preferredPrayerTimes: List<String> = emptyList(), // e.g., ["20:00", "21:30"]
    val enableProgressSharing: Boolean = false,
    val trackDetailedStats: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "defaultPrayerType" to defaultPrayerType?.name,
            "defaultTargetCount" to defaultTargetCount.mapKeys { it.key.name },
            "enableHapticFeedback" to enableHapticFeedback,
            "enableSoundEffects" to enableSoundEffects,
            "autoSaveProgress" to autoSaveProgress,
            "sessionReminderInterval" to sessionReminderInterval,
            "preferredPrayerTimes" to preferredPrayerTimes,
            "enableProgressSharing" to enableProgressSharing,
            "trackDetailedStats" to trackDetailedStats
        )
    }
}

data class AccountSettings(
    val theme: AppTheme = AppTheme.AUTO,
    val language: String = "en",
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "24h", // "12h" or "24h"
    val enableAnalytics: Boolean = true,
    val enableCrashReporting: Boolean = true,
    val autoBackup: Boolean = true,
    val twoFactorEnabled: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "theme" to theme.name,
            "language" to language,
            "dateFormat" to dateFormat,
            "timeFormat" to timeFormat,
            "enableAnalytics" to enableAnalytics,
            "enableCrashReporting" to enableCrashReporting,
            "autoBackup" to autoBackup,
            "twoFactorEnabled" to twoFactorEnabled
        )
    }
}

enum class IslamicRegion(val displayName: String) {
    NOT_SPECIFIED("Not Specified"),
    SOUTHEAST_ASIA("Southeast Asia"),
    MIDDLE_EAST("Middle East"),
    NORTH_AFRICA("North Africa"),
    SUB_SAHARAN_AFRICA("Sub-Saharan Africa"),
    SOUTH_ASIA("South Asia"),
    CENTRAL_ASIA("Central Asia"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    OCEANIA("Oceania")
}

enum class CalendarType {
    GREGORIAN, HIJRI, BOTH
}

enum class SchoolOfThought(val displayName: String, val arabicName: String) {
    NOT_SPECIFIED("Not Specified", "غير محدد"),
    HANAFI("Hanafi", "حنفي"),
    MALIKI("Maliki", "مالكي"),
    SHAFI("Shafi'i", "شافعي"),
    HANBALI("Hanbali", "حنبلي"),
    JAFARI("Ja'fari", "جعفري"),
    OTHER("Other", "أخرى")
}

enum class FontSize(val displayName: String, val scaleFactor: Float) {
    SMALL("Small", 0.8f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.2f),
    EXTRA_LARGE("Extra Large", 1.5f)
}

enum class ProfileVisibility {
    PRIVATE, FRIENDS_ONLY, COMMUNITY, PUBLIC
}

enum class StatsVisibility {
    PRIVATE, FRIENDS_ONLY, COMMUNITY
}

enum class AppTheme {
    LIGHT, DARK, AUTO
}

// Extension functions
fun UserProfile.isProfileComplete(): Boolean {
    return calculateProfileCompleteness() >= 80
}

fun UserProfile.needsLanguageSetup(): Boolean {
    return culturalPreferences.primaryLanguage == "en" && 
           culturalPreferences.region == IslamicRegion.NOT_SPECIFIED
}

fun UserProfile.getPreferredLanguages(): List<String> {
    val languages = mutableListOf(culturalPreferences.primaryLanguage)
    languages.addAll(culturalPreferences.secondaryLanguages)
    return languages.distinct()
}

fun UserProfile.shouldShowArabic(): Boolean {
    return culturalPreferences.showArabicText
}

fun UserProfile.shouldShowTransliteration(): Boolean {
    return culturalPreferences.showTransliteration
}

fun CulturalPreferences.getSupportedLanguages(): List<String> {
    return when (region) {
        IslamicRegion.SOUTHEAST_ASIA -> listOf("id", "ms", "th", "tl", "en")
        IslamicRegion.MIDDLE_EAST -> listOf("ar", "tr", "fa", "en")
        IslamicRegion.SOUTH_ASIA -> listOf("ur", "hi", "bn", "en")
        IslamicRegion.NORTH_AFRICA -> listOf("ar", "fr", "en")
        IslamicRegion.SUB_SAHARAN_AFRICA -> listOf("sw", "ha", "fr", "en")
        IslamicRegion.EUROPE -> listOf("en", "fr", "de", "tr")
        IslamicRegion.NORTH_AMERICA -> listOf("en", "es", "fr")
        else -> listOf("en", "ar")
    }
}