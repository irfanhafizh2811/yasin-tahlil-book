package com.app_muslim.surah_yasin.core.ui.accessibility

import android.accessibilityservice.AccessibilityService
import kotlin.math.pow
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Accessibility Manager for Tahlil Islamic Memorial Platform
 * 
 * Comprehensive accessibility framework implementing WCAG 2.1 AA standards
 * with Islamic cultural sensitivity and multilingual support.
 * 
 * Features:
 * - Screen reader optimization for Arabic content
 * - RTL language accessibility support
 * - Cultural context preservation in assistive technologies
 * - Voice navigation for prayer interactions
 * - Tactile feedback for memorial prayers
 */
@Singleton
class AccessibilityManager @Inject constructor(
    private val context: Context,
    private val accessibilityService: AccessibilityManager
) {
    
    private val _accessibilityState = MutableStateFlow(AccessibilityState())
    val accessibilityState: StateFlow<AccessibilityState> = _accessibilityState.asStateFlow()
    
    companion object {
        // WCAG 2.1 AA Compliance Constants
        private const val MIN_CONTRAST_RATIO_NORMAL = 4.5f
        private const val MIN_CONTRAST_RATIO_LARGE = 3.0f
        private const val MIN_TOUCH_TARGET_SIZE_DP = 48
        private const val FONT_SCALE_MAX = 2.0f
        
        // Islamic Accessibility Constants
        private const val ARABIC_TEXT_LINE_HEIGHT_MULTIPLIER = 1.8f
        private const val PRAYER_COUNTER_FEEDBACK_DURATION = 300L
        private const val MEMORIAL_ANNOUNCEMENT_DELAY = 1500L
        
        // Cultural Accessibility Preferences
        private val ISLAMIC_COLOR_MEANINGS = mapOf(
            "green" to "Paradise and Islamic tradition",
            "gold" to "Enlightenment and divine guidance", 
            "white" to "Purity and spiritual cleanliness",
            "black" to "Kaaba and sacred Islamic spaces"
        )
    }
    
    /**
     * Initialize accessibility monitoring and configuration
     */
    init {
        updateAccessibilityState()
        setupAccessibilityListeners()
    }
    
    /**
     * Check if any assistive technologies are enabled
     */
    fun isAccessibilityEnabled(): Boolean {
        return accessibilityService.isEnabled ||
                isTalkBackEnabled() ||
                isSelectToSpeakEnabled() ||
                isSwitchControlEnabled() ||
                isVoiceAccessEnabled()
    }
    
    /**
     * Check if TalkBack (Android screen reader) is enabled
     */
    fun isTalkBackEnabled(): Boolean {
        return accessibilityService.isEnabled &&
                accessibilityService.getEnabledAccessibilityServiceList(
                    android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_SPOKEN
                ).isNotEmpty()
    }
    
    /**
     * Check if high contrast mode is enabled
     */
    fun isHighContrastEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Settings.Secure.getInt(
                    context.contentResolver,
                    "high_text_contrast_enabled", 0
                ) == 1
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
    
    /**
     * Check if large text/font scaling is enabled
     */
    fun isLargeFontEnabled(): Boolean {
        val fontScale = context.resources.configuration.fontScale
        return fontScale > 1.3f
    }
    
    /**
     * Get current font scale factor
     */
    fun getFontScaleMultiplier(): Float {
        val fontScale = context.resources.configuration.fontScale
        return minOf(fontScale, FONT_SCALE_MAX)
    }
    
    /**
     * Check if RTL layout is enabled
     */
    fun isRTLEnabled(): Boolean {
        return context.resources.configuration.layoutDirection == 
               Configuration.SCREENLAYOUT_LAYOUTDIR_RTL
    }
    
    /**
     * Get accessibility announcement for prayer actions with Islamic context
     */
    fun getPrayerAnnouncementText(
        prayerType: PrayerType,
        count: Int,
        isArabic: Boolean = false
    ): String {
        return when (prayerType) {
            PrayerType.TAHLIL -> {
                if (isArabic) {
                    "لا إله إلا الله - تم إكمال التهليل رقم $count"
                } else {
                    "Tahlil prayer completed: $count. La ilaha illa Allah - There is no god except Allah"
                }
            }
            PrayerType.FATIHAH -> {
                if (isArabic) {
                    "سورة الفاتحة - تم إكمال القراءة رقم $count"
                } else {
                    "Al-Fatihah completed: $count. The Opening chapter of the Holy Quran"
                }
            }
            PrayerType.YASIN -> {
                if (isArabic) {
                    "سورة يس - تم إكمال القراءة رقم $count"
                } else {
                    "Surah Ya-Sin completed: $count. Heart of the Quran recitation"
                }
            }
            PrayerType.ISTIGHFAR -> {
                if (isArabic) {
                    "استغفر الله - تم إكمال الاستغفار رقم $count"
                } else {
                    "Istighfar completed: $count. Seeking Allah's forgiveness"
                }
            }
            PrayerType.SALAWAT -> {
                if (isArabic) {
                    "الصلاة على النبي - تم إكمال الصلاة رقم $count"
                } else {
                    "Salawat completed: $count. Blessings upon Prophet Muhammad (peace be upon him)"
                }
            }
        }
    }
    
    /**
     * Get memorial announcement with cultural sensitivity
     */
    fun getMemorialAnnouncementText(
        deceasedName: String,
        relationship: String,
        isArabic: Boolean = false
    ): String {
        return if (isArabic) {
            "تذكار للمرحوم $deceasedName، $relationship. رحمة الله عليه وأسكنه فسيح جناته"
        } else {
            "Memorial for $deceasedName, your $relationship. May Allah have mercy on their soul and grant them Paradise"
        }
    }
    
    /**
     * Get accessible description for Arabic text with pronunciation guide
     */
    fun getArabicTextDescription(
        arabicText: String,
        transliteration: String,
        translation: String
    ): String {
        return "Arabic text: $arabicText. Pronunciation: $transliteration. Meaning: $translation"
    }
    
    /**
     * Get cultural color meaning for accessibility
     */
    fun getCulturalColorMeaning(colorName: String): String {
        return ISLAMIC_COLOR_MEANINGS[colorName.lowercase()] 
            ?: "Traditional Islamic design color"
    }
    
    /**
     * Calculate and validate color contrast ratio for WCAG compliance
     */
    fun validateColorContrast(
        foregroundColor: Int,
        backgroundColor: Int,
        isLargeText: Boolean = false
    ): AccessibilityColorResult {
        val contrastRatio = calculateContrastRatio(foregroundColor, backgroundColor)
        val requiredRatio = if (isLargeText) MIN_CONTRAST_RATIO_LARGE else MIN_CONTRAST_RATIO_NORMAL
        
        return AccessibilityColorResult(
            contrastRatio = contrastRatio,
            isCompliant = contrastRatio >= requiredRatio,
            requiredRatio = requiredRatio,
            recommendation = if (contrastRatio < requiredRatio) {
                "Increase contrast to meet WCAG 2.1 AA standards. Current: ${"%.1f".format(contrastRatio)}, Required: ${"%.1f".format(requiredRatio)}"
            } else null
        )
    }
    
    /**
     * Get recommended touch target size based on accessibility settings
     */
    fun getRecommendedTouchTargetSize(): Int {
        val density = context.resources.displayMetrics.density
        val baseSize = MIN_TOUCH_TARGET_SIZE_DP * density
        val fontScale = getFontScaleMultiplier()
        return (baseSize * fontScale).toInt()
    }
    
    /**
     * Get RTL-aware content direction for accessibility
     */
    fun getContentDirection(): ContentDirection {
        return if (isRTLEnabled()) {
            ContentDirection.RTL
        } else {
            ContentDirection.LTR
        }
    }
    
    /**
     * Generate skip navigation links for prayer interface
     */
    fun getSkipNavigationOptions(currentScreen: AccessibilityScreen): List<SkipNavigationLink> {
        return when (currentScreen) {
            AccessibilityScreen.HOME -> listOf(
                SkipNavigationLink("Skip to prayer center", "prayer_center"),
                SkipNavigationLink("Skip to memorial list", "memorial_list"),
                SkipNavigationLink("Skip to community prayers", "community_section")
            )
            AccessibilityScreen.PRAYER -> listOf(
                SkipNavigationLink("Skip to prayer counter", "prayer_counter"),
                SkipNavigationLink("Skip to prayer text", "prayer_text"),
                SkipNavigationLink("Skip to prayer settings", "prayer_settings")
            )
            AccessibilityScreen.MEMORIAL -> listOf(
                SkipNavigationLink("Skip to memorial details", "memorial_details"),
                SkipNavigationLink("Skip to prayer options", "prayer_options"),
                SkipNavigationLink("Skip to community prayers", "community_prayers")
            )
            AccessibilityScreen.COMMUNITY -> listOf(
                SkipNavigationLink("Skip to active sessions", "active_sessions"),
                SkipNavigationLink("Skip to global statistics", "global_stats"),
                SkipNavigationLink("Skip to join community", "join_community")
            )
        }
    }
    
    /**
     * Get accessibility landmark for screen reader navigation
     */
    fun getAccessibilityLandmark(elementType: AccessibilityElementType): String {
        return when (elementType) {
            AccessibilityElementType.MAIN_CONTENT -> "main"
            AccessibilityElementType.NAVIGATION -> "navigation"
            AccessibilityElementType.PRAYER_SECTION -> "region"
            AccessibilityElementType.MEMORIAL_CARD -> "article"
            AccessibilityElementType.FORM_SECTION -> "form"
            AccessibilityElementType.PRAYER_COUNTER -> "application"
            AccessibilityElementType.COMMUNITY_STATS -> "complementary"
        }
    }
    
    // Private helper methods
    
    private fun updateAccessibilityState() {
        _accessibilityState.value = AccessibilityState(
            isAccessibilityEnabled = isAccessibilityEnabled(),
            isTalkBackEnabled = isTalkBackEnabled(),
            isHighContrastEnabled = isHighContrastEnabled(),
            isLargeFontEnabled = isLargeFontEnabled(),
            isRTLEnabled = isRTLEnabled(),
            fontScaleMultiplier = getFontScaleMultiplier(),
            touchTargetSize = getRecommendedTouchTargetSize(),
            contentDirection = getContentDirection()
        )
    }
    
    private fun setupAccessibilityListeners() {
        accessibilityService.addAccessibilityStateChangeListener { enabled ->
            updateAccessibilityState()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            accessibilityService.addTouchExplorationStateChangeListener { enabled ->
                updateAccessibilityState()
            }
        }
    }
    
    private fun isSelectToSpeakEnabled(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            enabledServices?.contains("selecttospeak") == true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isSwitchControlEnabled(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            enabledServices?.contains("switchcontrol") == true ||
            enabledServices?.contains("switch_access") == true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isVoiceAccessEnabled(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            enabledServices?.contains("voice_access") == true ||
            enabledServices?.contains("voiceaccess") == true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun calculateContrastRatio(foreground: Int, background: Int): Float {
        val foregroundLuminance = calculateLuminance(foreground)
        val backgroundLuminance = calculateLuminance(background)
        
        val lighter = maxOf(foregroundLuminance, backgroundLuminance)
        val darker = minOf(foregroundLuminance, backgroundLuminance)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    private fun calculateLuminance(color: Int): Float {
        val red = android.graphics.Color.red(color) / 255f
        val green = android.graphics.Color.green(color) / 255f
        val blue = android.graphics.Color.blue(color) / 255f
        
        val r = if (red <= 0.03928f) red / 12.92f else ((red + 0.055f) / 1.055f).pow(2.4f)
        val g = if (green <= 0.03928f) green / 12.92f else ((green + 0.055f) / 1.055f).pow(2.4f)
        val b = if (blue <= 0.03928f) blue / 12.92f else ((blue + 0.055f) / 1.055f).pow(2.4f)
        
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }
}

/**
 * Current accessibility state of the application
 */
data class AccessibilityState(
    val isAccessibilityEnabled: Boolean = false,
    val isTalkBackEnabled: Boolean = false,
    val isHighContrastEnabled: Boolean = false,
    val isLargeFontEnabled: Boolean = false,
    val isRTLEnabled: Boolean = false,
    val fontScaleMultiplier: Float = 1.0f,
    val touchTargetSize: Int = 144, // 48dp * 3 density
    val contentDirection: ContentDirection = ContentDirection.LTR
)

/**
 * Result of color contrast validation
 */
data class AccessibilityColorResult(
    val contrastRatio: Float,
    val isCompliant: Boolean,
    val requiredRatio: Float,
    val recommendation: String?
)

/**
 * Skip navigation link for keyboard users
 */
data class SkipNavigationLink(
    val text: String,
    val targetId: String
)

/**
 * Content direction for RTL/LTR support
 */
enum class ContentDirection {
    LTR, RTL
}

/**
 * Screen types for accessibility navigation
 */
enum class AccessibilityScreen {
    HOME,
    PRAYER,
    MEMORIAL,
    COMMUNITY
}

/**
 * Element types for accessibility landmarks
 */
enum class AccessibilityElementType {
    MAIN_CONTENT,
    NAVIGATION,
    PRAYER_SECTION,
    MEMORIAL_CARD,
    FORM_SECTION,
    PRAYER_COUNTER,
    COMMUNITY_STATS
}

/**
 * Prayer types for accessibility announcements
 */
enum class PrayerType {
    TAHLIL,
    FATIHAH,
    YASIN,
    ISTIGHFAR,
    SALAWAT
}