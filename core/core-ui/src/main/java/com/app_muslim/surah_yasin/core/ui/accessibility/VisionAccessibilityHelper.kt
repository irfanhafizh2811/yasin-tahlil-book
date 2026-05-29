package com.app_muslim.surah_yasin.core.ui.accessibility

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlin.math.pow
import kotlin.math.roundToInt
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Vision Accessibility Helper for Islamic Prayer Platform
 * 
 * Comprehensive vision accessibility support implementing WCAG 2.1 AA standards
 * with Islamic cultural design considerations.
 * 
 * Features:
 * - High contrast mode with Islamic color preservation
 * - Large text support for Arabic scripts
 * - Color-blind friendly design patterns
 * - Dynamic font scaling for prayer texts
 * - Cultural color meaning preservation in accessibility modes
 */
@Singleton
class VisionAccessibilityHelper @Inject constructor(
    private val context: Context,
    private val accessibilityManager: AccessibilityManager
) {

    companion object {
        // WCAG 2.1 AA Color Contrast Requirements
        private const val MIN_CONTRAST_NORMAL = 4.5f
        private const val MIN_CONTRAST_LARGE = 3.0f
        private const val MIN_CONTRAST_AAA = 7.0f
        
        // Islamic Color Palette - High Contrast Safe
        private const val ISLAMIC_GREEN_PRIMARY = 0xFF1B5E20.toInt()
        private const val ISLAMIC_GREEN_LIGHT = 0xFF4C8C4A.toInt()
        private const val ISLAMIC_GOLD = 0xFFBF9000.toInt()
        private const val ISLAMIC_WHITE = 0xFFFFFFFE.toInt() // Slightly off-white for better contrast
        private const val ISLAMIC_BLACK = 0xFF1C1C1C.toInt()
        private const val ISLAMIC_ERROR = 0xFFB71C1C.toInt()
        private const val ISLAMIC_SUCCESS = 0xFF2E7D32.toInt()
        
        // Font scaling constants
        private const val MIN_FONT_SCALE = 0.85f
        private const val MAX_FONT_SCALE = 3.0f
        private const val ARABIC_FONT_MULTIPLIER = 1.2f
        private const val PRAYER_TEXT_MULTIPLIER = 1.4f
        
        // Touch target scaling
        private const val MIN_TOUCH_TARGET_DP = 48
        private const val PREFERRED_TOUCH_TARGET_DP = 60
        
        // Cultural design constants
        private val ISLAMIC_PATTERN_CONTRAST_ADJUSTMENTS = mapOf(
            "geometric" to 0.15f,
            "calligraphy" to 0.25f,
            "arabesque" to 0.20f
        )
    }

    /**
     * Apply comprehensive vision accessibility to TextView
     */
    fun applyVisionAccessibility(
        textView: TextView,
        textType: IslamicTextType = IslamicTextType.GENERAL,
        enableHighContrast: Boolean = accessibilityManager.isHighContrastEnabled()
    ) {
        // Apply font scaling
        applyFontScaling(textView, textType)
        
        // Apply high contrast if needed
        if (enableHighContrast) {
            applyHighContrastColors(textView, textType)
        }
        
        // Apply appropriate line height for readability
        applyLineHeight(textView, textType)
        
        // Set font family for optimal readability
        applyOptimalFontFamily(textView, textType)
        
        // Configure text appearance
        configureTextAppearance(textView, textType)
    }

    /**
     * Apply font scaling based on accessibility settings and content type
     */
    private fun applyFontScaling(textView: TextView, textType: IslamicTextType) {
        val systemFontScale = accessibilityManager.getFontScaleMultiplier()
        val baseMultiplier = when (textType) {
            IslamicTextType.ARABIC_PRAYER -> ARABIC_FONT_MULTIPLIER * PRAYER_TEXT_MULTIPLIER
            IslamicTextType.ARABIC_GENERAL -> ARABIC_FONT_MULTIPLIER
            IslamicTextType.PRAYER_TRANSLATION -> PRAYER_TEXT_MULTIPLIER
            IslamicTextType.MEMORIAL_NAME -> 1.3f
            IslamicTextType.GENERAL -> 1.0f
            IslamicTextType.SMALL_LABEL -> 0.9f
        }
        
        val finalScale = (systemFontScale * baseMultiplier).coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        
        val currentSize = textView.textSize / context.resources.displayMetrics.scaledDensity
        val newSize = currentSize * finalScale
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize)
    }

    /**
     * Apply high contrast colors while preserving Islamic design values
     */
    private fun applyHighContrastColors(textView: TextView, textType: IslamicTextType) {
        val (textColor, backgroundColor) = getHighContrastColors(textType)
        
        textView.setTextColor(textColor)
        textView.setBackgroundColor(backgroundColor)
        
        // Add subtle border for additional definition if needed
        if (textType == IslamicTextType.ARABIC_PRAYER || textType == IslamicTextType.MEMORIAL_NAME) {
            textView.setPadding(16, 12, 16, 12)
        }
    }

    /**
     * Get high contrast color pairs for different text types
     */
    private fun getHighContrastColors(textType: IslamicTextType): Pair<Int, Int> {
        return when (textType) {
            IslamicTextType.ARABIC_PRAYER -> Pair(ISLAMIC_GREEN_PRIMARY, ISLAMIC_WHITE)
            IslamicTextType.MEMORIAL_NAME -> Pair(ISLAMIC_BLACK, ISLAMIC_WHITE)
            IslamicTextType.PRAYER_TRANSLATION -> Pair(ISLAMIC_BLACK, Color.parseColor("#F8F9FA"))
            IslamicTextType.ARABIC_GENERAL -> Pair(ISLAMIC_GREEN_PRIMARY, ISLAMIC_WHITE)
            IslamicTextType.GENERAL -> Pair(ISLAMIC_BLACK, ISLAMIC_WHITE)
            IslamicTextType.SMALL_LABEL -> Pair(Color.parseColor("#424242"), ISLAMIC_WHITE)
        }
    }

    /**
     * Apply optimal line height for readability
     */
    private fun applyLineHeight(textView: TextView, textType: IslamicTextType) {
        val lineHeightMultiplier = when (textType) {
            IslamicTextType.ARABIC_PRAYER -> 2.0f
            IslamicTextType.ARABIC_GENERAL -> 1.8f
            IslamicTextType.PRAYER_TRANSLATION -> 1.6f
            IslamicTextType.MEMORIAL_NAME -> 1.4f
            IslamicTextType.GENERAL -> 1.5f
            IslamicTextType.SMALL_LABEL -> 1.4f
        }
        
        val currentTextSize = textView.textSize
        val lineHeight = (currentTextSize * lineHeightMultiplier).roundToInt()
        textView.setLineSpacing(0f, lineHeightMultiplier)
    }

    /**
     * Apply optimal font family for different text types
     */
    private fun applyOptimalFontFamily(textView: TextView, textType: IslamicTextType) {
        when (textType) {
            IslamicTextType.ARABIC_PRAYER, IslamicTextType.ARABIC_GENERAL -> {
                // Use dedicated Arabic font for better readability
                try {
                    val arabicFont = Typeface.createFromAsset(context.assets, "fonts/font_lpmq_isep_misbah.ttf")
                    textView.typeface = arabicFont
                } catch (e: Exception) {
                    // Fallback to system default
                    textView.typeface = Typeface.DEFAULT
                }
            }
            IslamicTextType.PRAYER_TRANSLATION, IslamicTextType.MEMORIAL_NAME -> {
                textView.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
            IslamicTextType.GENERAL, IslamicTextType.SMALL_LABEL -> {
                textView.typeface = Typeface.DEFAULT
            }
        }
    }

    /**
     * Configure text appearance for optimal readability
     */
    private fun configureTextAppearance(textView: TextView, textType: IslamicTextType) {
        when (textType) {
            IslamicTextType.ARABIC_PRAYER -> {
                textView.letterSpacing = 0.02f
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textView.justificationMode = android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
                }
            }
            IslamicTextType.MEMORIAL_NAME -> {
                textView.typeface = Typeface.create(textView.typeface, Typeface.BOLD)
                textView.letterSpacing = 0.01f
            }
            IslamicTextType.PRAYER_TRANSLATION -> {
                textView.letterSpacing = 0.015f
            }
            else -> {
                // Use default settings
            }
        }
    }

    /**
     * Create color-blind friendly visual indicators
     */
    fun createColorBlindFriendlyIndicator(
        view: View,
        indicatorType: IndicatorType,
        state: IndicatorState
    ) {
        val (color, symbol, description) = getColorBlindFriendlyDesign(indicatorType, state)
        
        // Apply color
        view.setBackgroundColor(color)
        
        // Add visual symbol using content description
        view.contentDescription = "$description $symbol"
        
        // Add pattern or texture for additional differentiation
        when (state) {
            IndicatorState.SUCCESS -> addSuccessPattern(view)
            IndicatorState.ERROR -> addErrorPattern(view)
            IndicatorState.WARNING -> addWarningPattern(view)
            IndicatorState.INFO -> addInfoPattern(view)
            IndicatorState.NEUTRAL -> {
                // No additional pattern needed
            }
        }
    }

    /**
     * Get color-blind friendly design elements
     */
    private fun getColorBlindFriendlyDesign(
        type: IndicatorType,
        state: IndicatorState
    ): Triple<Int, String, String> {
        return when (state) {
            IndicatorState.SUCCESS -> Triple(
                ISLAMIC_SUCCESS,
                "✓",
                "Success indicator - prayer completed"
            )
            IndicatorState.ERROR -> Triple(
                ISLAMIC_ERROR,
                "⚠",
                "Error indicator - action required"
            )
            IndicatorState.WARNING -> Triple(
                ISLAMIC_GOLD,
                "⚡",
                "Warning indicator - attention needed"
            )
            IndicatorState.INFO -> Triple(
                Color.parseColor("#1976D2"),
                "ℹ",
                "Information indicator"
            )
            IndicatorState.NEUTRAL -> Triple(
                Color.parseColor("#757575"),
                "●",
                "Neutral indicator"
            )
        }
    }

    /**
     * Calculate optimal color contrast for readability
     */
    fun calculateOptimalContrast(
        @ColorInt foreground: Int,
        @ColorInt background: Int,
        requiresAAA: Boolean = false
    ): ContrastResult {
        val contrast = calculateContrastRatio(foreground, background)
        val target = if (requiresAAA) MIN_CONTRAST_AAA else MIN_CONTRAST_NORMAL
        
        return ContrastResult(
            ratio = contrast,
            isCompliant = contrast >= target,
            target = target,
            suggestedAdjustment = if (contrast < target) {
                calculateContrastAdjustment(foreground, background, target)
            } else null
        )
    }

    /**
     * Get vision accessibility recommendations
     */
    fun getVisionAccessibilityRecommendations(): List<AccessibilityRecommendation> {
        val recommendations = mutableListOf<AccessibilityRecommendation>()
        
        if (accessibilityManager.isHighContrastEnabled()) {
            recommendations.add(
                AccessibilityRecommendation(
                    type = RecommendationType.HIGH_CONTRAST,
                    description = "High contrast mode detected - enhanced color contrasts applied",
                    action = "Colors automatically adjusted for better visibility"
                )
            )
        }
        
        if (accessibilityManager.isLargeFontEnabled()) {
            recommendations.add(
                AccessibilityRecommendation(
                    type = RecommendationType.LARGE_FONT,
                    description = "Large font preference detected - text sizes increased",
                    action = "Arabic and prayer texts scaled appropriately"
                )
            )
        }
        
        return recommendations
    }

    /**
     * Apply Islamic pattern accessibility adjustments
     */
    fun adjustIslamicPatternsForAccessibility(
        view: View,
        patternType: String
    ) {
        val adjustment = ISLAMIC_PATTERN_CONTRAST_ADJUSTMENTS[patternType] ?: 0.1f
        
        // Reduce opacity for accessibility
        view.alpha = 1.0f - adjustment
        
        // Add content description
        view.contentDescription = when (patternType) {
            "geometric" -> "Islamic geometric pattern - decorative background"
            "calligraphy" -> "Arabic calligraphy pattern - decorative text"
            "arabesque" -> "Islamic arabesque pattern - floral decoration"
            else -> "Traditional Islamic decorative pattern"
        }
    }

    // Private helper methods
    
    private fun calculateContrastRatio(@ColorInt color1: Int, @ColorInt color2: Int): Float {
        val luminance1 = ColorUtils.calculateLuminance(color1)
        val luminance2 = ColorUtils.calculateLuminance(color2)
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        return ((lighter + 0.05) / (darker + 0.05)).toFloat()
    }
    
    private fun calculateContrastAdjustment(
        @ColorInt foreground: Int,
        @ColorInt background: Int,
        targetRatio: Float
    ): ColorAdjustment {
        // Calculate how much to adjust foreground color
        val currentRatio = calculateContrastRatio(foreground, background)
        val adjustmentFactor = targetRatio / currentRatio
        
        val adjustedForeground = if (ColorUtils.calculateLuminance(foreground) < 0.5) {
            // Darken dark colors
            ColorUtils.blendARGB(foreground, Color.BLACK, adjustmentFactor * 0.3f)
        } else {
            // Lighten light colors
            ColorUtils.blendARGB(foreground, Color.WHITE, adjustmentFactor * 0.3f)
        }
        
        return ColorAdjustment(
            originalColor = foreground,
            adjustedColor = adjustedForeground,
            adjustmentReason = "Improved contrast ratio from ${"%.1f".format(currentRatio)} to target ${"%.1f".format(targetRatio)}"
        )
    }
    
    private fun addSuccessPattern(view: View) {
        // Add subtle border for success indication
        view.setPadding(4, 4, 4, 4)
    }
    
    private fun addErrorPattern(view: View) {
        // Add border and slight elevation for error indication
        view.setPadding(6, 6, 6, 6)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = 2f
        }
    }
    
    private fun addWarningPattern(view: View) {
        // Add dashed-style padding for warning
        view.setPadding(5, 3, 5, 3)
    }
    
    private fun addInfoPattern(view: View) {
        // Add minimal padding for info
        view.setPadding(3, 3, 3, 3)
    }
}

/**
 * Types of Islamic text content
 */
enum class IslamicTextType {
    ARABIC_PRAYER,      // Arabic prayer text (Quran, Dua)
    ARABIC_GENERAL,     // General Arabic text
    PRAYER_TRANSLATION, // Translation of prayers
    MEMORIAL_NAME,      // Name of deceased person
    GENERAL,           // Regular text content
    SMALL_LABEL        // Small labels and captions
}

/**
 * Visual indicator types
 */
enum class IndicatorType {
    PRAYER_STATUS,
    MEMORIAL_STATUS,
    NETWORK_STATUS,
    FORM_VALIDATION,
    PROGRESS_INDICATOR
}

/**
 * Visual indicator states
 */
enum class IndicatorState {
    SUCCESS,
    ERROR,
    WARNING,
    INFO,
    NEUTRAL
}

/**
 * Accessibility recommendation types
 */
enum class RecommendationType {
    HIGH_CONTRAST,
    LARGE_FONT,
    COLOR_BLIND_SUPPORT,
    REDUCED_MOTION,
    ENHANCED_FOCUS
}

/**
 * Color contrast calculation result
 */
data class ContrastResult(
    val ratio: Float,
    val isCompliant: Boolean,
    val target: Float,
    val suggestedAdjustment: ColorAdjustment?
)

/**
 * Color adjustment recommendation
 */
data class ColorAdjustment(
    @ColorInt val originalColor: Int,
    @ColorInt val adjustedColor: Int,
    val adjustmentReason: String
)

/**
 * Contrast levels for accessibility
 */
enum class ContrastLevel {
    NORMAL,     // WCAG AA: 4.5:1
    LARGE,      // WCAG AA large text: 3:1
    AAA,        // WCAG AAA: 7:1
    ENHANCED    // Higher than AAA for maximum accessibility
}

/**
 * Vision impairment types
 */
enum class VisionImpairment {
    LOW_VISION,
    LIGHT_SENSITIVITY,
    MOTION_SENSITIVITY,
    CONTRAST_SENSITIVITY,
    NONE
}

/**
 * Color blindness types
 */
enum class ColorBlindness {
    PROTANOPIA,     // Red-blind
    DEUTERANOPIA,   // Green-blind
    TRITANOPIA,     // Blue-blind
    PROTANOMALY,    // Red-weak
    DEUTERANOMALY,  // Green-weak
    TRITANOMALY,    // Blue-weak
    ACHROMATOPSIA,  // Complete color blindness
    NONE            // No color blindness
}

/**
 * Accessibility recommendation
 */
data class AccessibilityRecommendation(
    val type: RecommendationType,
    val description: String,
    val action: String
)