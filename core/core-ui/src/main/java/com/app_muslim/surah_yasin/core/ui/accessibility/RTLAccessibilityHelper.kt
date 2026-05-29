package com.app_muslim.surah_yasin.core.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RTL Accessibility Helper for Islamic Prayer Platform
 * 
 * Comprehensive Right-to-Left language support with accessibility considerations
 * for Arabic, Urdu, Persian, and other RTL languages used in Islamic contexts.
 * 
 * Features:
 * - Automatic RTL layout detection and adjustment
 * - Arabic text accessibility with proper directionality
 * - Islamic language switching with accessibility preservation
 * - Cultural reading patterns for memorial content
 * - Bi-directional text support for mixed content
 */
@Singleton
class RTLAccessibilityHelper @Inject constructor(
    private val context: Context
) {

    companion object {
        // RTL languages supported by Islamic platform
        private val RTL_LANGUAGES = setOf(
            "ar", "fa", "ur", "he", "ckb", "dv", "ps", "sd", "ug", "yi"
        )
        
        // Islamic languages with special requirements
        private val ISLAMIC_RTL_LANGUAGES = mapOf(
            "ar" to IslamicLanguageInfo("Arabic", "العربية", true),
            "fa" to IslamicLanguageInfo("Persian", "فارسی", true),
            "ur" to IslamicLanguageInfo("Urdu", "اردو", true),
            "ps" to IslamicLanguageInfo("Pashto", "پښتو", true),
            "sd" to IslamicLanguageInfo("Sindhi", "سنڌي", true),
            "ckb" to IslamicLanguageInfo("Central Kurdish", "سۆرانی", true)
        )
        
        // Bi-directional text patterns in Islamic content
        private val BIDI_MARKERS = mapOf(
            "LRM" to '\u200E', // Left-to-Right Mark
            "RLM" to '\u200F', // Right-to-Left Mark
            "LRE" to '\u202A', // Left-to-Right Embedding
            "RLE" to '\u202B', // Right-to-Left Embedding
            "PDF" to '\u202C', // Pop Directional Formatting
            "LRO" to '\u202D', // Left-to-Right Override
            "RLO" to '\u202E'  // Right-to-Left Override
        )
    }

    /**
     * Check if current locale uses RTL layout
     */
    fun isRTLLayout(): Boolean {
        return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == 
               ViewCompat.LAYOUT_DIRECTION_RTL
    }

    /**
     * Check if given language is RTL
     */
    fun isRTLLanguage(languageCode: String): Boolean {
        return RTL_LANGUAGES.contains(languageCode.lowercase())
    }

    /**
     * Get Islamic language information
     */
    fun getIslamicLanguageInfo(languageCode: String): IslamicLanguageInfo? {
        return ISLAMIC_RTL_LANGUAGES[languageCode.lowercase()]
    }

    /**
     * Configure view for comprehensive RTL accessibility
     */
    fun configureRTLAccessibility(
        view: View,
        contentType: RTLContentType = RTLContentType.GENERAL,
        forceDirection: Boolean = false
    ) {
        // Set layout direction
        val direction = if (forceDirection || isRTLLayout()) {
            ViewCompat.LAYOUT_DIRECTION_RTL
        } else {
            ViewCompat.LAYOUT_DIRECTION_LTR
        }
        
        ViewCompat.setLayoutDirection(view, direction)
        
        // Configure specific content types
        when (contentType) {
            RTLContentType.ARABIC_PRAYER -> configureArabicPrayerRTL(view)
            RTLContentType.MEMORIAL_TEXT -> configureMemorialTextRTL(view)
            RTLContentType.NAVIGATION -> configureNavigationRTL(view)
            RTLContentType.FORM_INPUT -> configureFormInputRTL(view)
            RTLContentType.MIXED_CONTENT -> configureMixedContentRTL(view)
            RTLContentType.GENERAL -> configureGeneralRTL(view)
        }
        
        // Set accessibility properties
        configureRTLAccessibilityProperties(view, contentType)
    }

    /**
     * Configure TextView for RTL Arabic content with accessibility
     */
    fun configureArabicTextView(
        textView: TextView,
        arabicText: String,
        transliteration: String? = null,
        translation: String? = null
    ) {
        // Set RTL direction
        ViewCompat.setLayoutDirection(textView, ViewCompat.LAYOUT_DIRECTION_RTL)
        
        // Configure text properties for Arabic
        textView.textDirection = View.TEXT_DIRECTION_RTL
        textView.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        
        // Set Arabic text
        textView.text = arabicText
        
        // Create comprehensive accessibility description
        val accessibilityDescription = buildArabicAccessibilityDescription(
            arabicText, transliteration, translation
        )
        textView.contentDescription = accessibilityDescription
        
        // Configure text layout for accessibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.breakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
            textView.hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }
        
        // Set language for screen readers
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setTextLocale(Locale("ar"))
        }
    }

    /**
     * Configure navigation elements for RTL accessibility
     */
    fun configureRTLNavigation(viewGroup: ViewGroup) {
        ViewCompat.setLayoutDirection(viewGroup, 
            if (isRTLLayout()) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )
        
        // Adjust navigation order for RTL
        if (isRTLLayout()) {
            reverseNavigationOrder(viewGroup)
        }
        
        // Configure breadcrumb navigation
        configureRTLBreadcrumbs(viewGroup)
    }

    /**
     * Handle mixed LTR/RTL content with proper accessibility
     */
    fun configureMixedDirectionalContent(
        textView: TextView,
        content: MixedDirectionalContent
    ) {
        val processedText = StringBuilder()
        
        content.segments.forEach { segment ->
            when (segment.direction) {
                TextDirection.LTR -> {
                    processedText.append(BIDI_MARKERS["LRM"])
                    processedText.append(segment.text)
                    processedText.append(BIDI_MARKERS["LRM"])
                }
                TextDirection.RTL -> {
                    processedText.append(BIDI_MARKERS["RLM"])
                    processedText.append(segment.text)
                    processedText.append(BIDI_MARKERS["RLM"])
                }
                TextDirection.AUTO -> {
                    // Let system determine direction
                    processedText.append(segment.text)
                }
            }
            
            if (segment != content.segments.last()) {
                processedText.append(" ")
            }
        }
        
        textView.text = processedText.toString()
        
        // Set mixed content accessibility description
        val accessibilityDesc = content.segments.joinToString(", ") { segment ->
            "${segment.direction.name.lowercase()}: ${segment.text}"
        }
        textView.contentDescription = "Mixed text content: $accessibilityDesc"
    }

    /**
     * Configure form inputs for RTL accessibility
     */
    fun configureRTLFormInput(
        view: View,
        inputType: RTLInputType = RTLInputType.TEXT
    ) {
        ViewCompat.setLayoutDirection(view, 
            if (isRTLLayout()) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )
        
        when (inputType) {
            RTLInputType.ARABIC_TEXT -> {
                if (view is TextView) {
                    view.textDirection = View.TEXT_DIRECTION_RTL
                    view.gravity = Gravity.END
                }
            }
            RTLInputType.NAME_FIELD -> {
                if (view is TextView) {
                    // Names might be Arabic or English
                    view.textDirection = View.TEXT_DIRECTION_FIRST_STRONG
                    view.gravity = if (isRTLLayout()) Gravity.END else Gravity.START
                }
            }
            RTLInputType.EMAIL_PHONE -> {
                if (view is TextView) {
                    // Always LTR for email/phone
                    view.textDirection = View.TEXT_DIRECTION_LTR
                    view.gravity = Gravity.START
                }
            }
            RTLInputType.TEXT -> {
                if (view is TextView) {
                    view.textDirection = View.TEXT_DIRECTION_FIRST_STRONG
                    view.gravity = if (isRTLLayout()) Gravity.END else Gravity.START
                }
            }
        }
    }

    /**
     * Get RTL-aware accessibility announcement
     */
    fun getRTLAccessibilityAnnouncement(
        content: String,
        isArabicContent: Boolean = false,
        includeDirectionality: Boolean = true
    ): String {
        val directionPrefix = if (includeDirectionality) {
            if (isArabicContent || isRTLLayout()) {
                "Right-to-left content: "
            } else {
                "Left-to-right content: "
            }
        } else ""
        
        return "$directionPrefix$content"
    }

    /**
     * Configure RTL layout for ViewGroup accessibility
     */
    fun configureRTLLayout(
        viewGroup: ViewGroup,
        preserveChildOrder: Boolean = false
    ) {
        val direction = if (isRTLLayout()) {
            ViewCompat.LAYOUT_DIRECTION_RTL
        } else {
            ViewCompat.LAYOUT_DIRECTION_LTR
        }
        
        ViewCompat.setLayoutDirection(viewGroup, direction)
        
        // Apply direction to all children
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            ViewCompat.setLayoutDirection(child, direction)
        }
        
        // Adjust layout parameters for RTL
        adjustLayoutParamsForRTL(viewGroup)
        
        if (!preserveChildOrder && isRTLLayout()) {
            reverseChildOrder(viewGroup)
        }
    }

    /**
     * Get cultural reading pattern for content
     */
    fun getCulturalReadingPattern(contentType: CulturalContentType): ReadingPattern {
        return when (contentType) {
            CulturalContentType.QURANIC_VERSE -> ReadingPattern(
                startPosition = ReadingStartPosition.TOP_RIGHT,
                direction = TextDirection.RTL,
                flowPattern = FlowPattern.VERTICAL_THEN_HORIZONTAL,
                culturalNote = "Quranic text follows traditional Arabic reading pattern"
            )
            CulturalContentType.MEMORIAL_DETAILS -> ReadingPattern(
                startPosition = if (isRTLLayout()) ReadingStartPosition.TOP_RIGHT else ReadingStartPosition.TOP_LEFT,
                direction = if (isRTLLayout()) TextDirection.RTL else TextDirection.LTR,
                flowPattern = FlowPattern.HORIZONTAL_THEN_VERTICAL,
                culturalNote = "Memorial information follows cultural reading preferences"
            )
            CulturalContentType.PRAYER_INSTRUCTIONS -> ReadingPattern(
                startPosition = ReadingStartPosition.TOP_CENTER,
                direction = TextDirection.RTL,
                flowPattern = FlowPattern.CENTERED_FLOW,
                culturalNote = "Prayer instructions centered for universal access"
            )
            CulturalContentType.COMMUNITY_LIST -> ReadingPattern(
                startPosition = if (isRTLLayout()) ReadingStartPosition.TOP_RIGHT else ReadingStartPosition.TOP_LEFT,
                direction = if (isRTLLayout()) TextDirection.RTL else TextDirection.LTR,
                flowPattern = FlowPattern.LIST_VERTICAL,
                culturalNote = "Community content follows local reading preferences"
            )
        }
    }

    // Private helper methods
    
    private fun configureArabicPrayerRTL(view: View) {
        if (view is TextView) {
            view.textDirection = View.TEXT_DIRECTION_RTL
            view.gravity = Gravity.CENTER
            view.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }
    
    private fun configureMemorialTextRTL(view: View) {
        if (view is TextView) {
            view.textDirection = View.TEXT_DIRECTION_FIRST_STRONG
            view.gravity = if (isRTLLayout()) Gravity.END else Gravity.START
        }
    }
    
    private fun configureNavigationRTL(view: View) {
        // Navigation should follow system direction
        ViewCompat.setLayoutDirection(view, 
            if (isRTLLayout()) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )
    }
    
    private fun configureFormInputRTL(view: View) {
        configureRTLFormInput(view, RTLInputType.TEXT)
    }
    
    private fun configureMixedContentRTL(view: View) {
        if (view is TextView) {
            view.textDirection = View.TEXT_DIRECTION_FIRST_STRONG
        }
    }
    
    private fun configureGeneralRTL(view: View) {
        // General content follows system direction
        ViewCompat.setLayoutDirection(view, 
            if (isRTLLayout()) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )
    }
    
    private fun configureRTLAccessibilityProperties(view: View, contentType: RTLContentType) {
        // Set content description with directional information
        val currentDesc = view.contentDescription?.toString() ?: ""
        val directionalDesc = when (contentType) {
            RTLContentType.ARABIC_PRAYER -> "Arabic prayer text, right-to-left. $currentDesc"
            RTLContentType.MEMORIAL_TEXT -> "Memorial text. $currentDesc"
            RTLContentType.NAVIGATION -> "Navigation element. $currentDesc"
            RTLContentType.MIXED_CONTENT -> "Mixed directional content. $currentDesc"
            else -> currentDesc
        }
        
        if (directionalDesc != currentDesc) {
            view.contentDescription = directionalDesc
        }
    }
    
    private fun buildArabicAccessibilityDescription(
        arabicText: String,
        transliteration: String?,
        translation: String?
    ): String {
        val description = StringBuilder()
        description.append("Arabic text: $arabicText")
        
        transliteration?.let {
            description.append(". Pronunciation: $it")
        }
        
        translation?.let {
            description.append(". Meaning: $it")
        }
        
        return description.toString()
    }
    
    private fun reverseNavigationOrder(viewGroup: ViewGroup) {
        val children = mutableListOf<View>()
        for (i in 0 until viewGroup.childCount) {
            children.add(viewGroup.getChildAt(i))
        }
        
        viewGroup.removeAllViews()
        children.reversed().forEach { child ->
            viewGroup.addView(child)
        }
    }
    
    private fun configureRTLBreadcrumbs(viewGroup: ViewGroup) {
        // Configure breadcrumb separators for RTL
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView && child.text.contains(">")) {
                child.text = if (isRTLLayout()) {
                    child.text.toString().replace(">", "<")
                } else {
                    child.text.toString().replace("<", ">")
                }
            }
        }
    }
    
    private fun adjustLayoutParamsForRTL(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            val layoutParams = child.layoutParams
            
            if (layoutParams is RelativeLayout.LayoutParams) {
                adjustRelativeLayoutParamsForRTL(layoutParams)
            } else if (layoutParams is LinearLayout.LayoutParams) {
                adjustLinearLayoutParamsForRTL(layoutParams)
            }
        }
    }
    
    private fun adjustRelativeLayoutParamsForRTL(params: RelativeLayout.LayoutParams) {
        if (isRTLLayout()) {
            // Swap left/right rules
            val rules = params.rules.clone()
            params.addRule(RelativeLayout.ALIGN_PARENT_END, rules[RelativeLayout.ALIGN_PARENT_START])
            params.addRule(RelativeLayout.ALIGN_PARENT_START, rules[RelativeLayout.ALIGN_PARENT_END])
        }
    }
    
    private fun adjustLinearLayoutParamsForRTL(params: LinearLayout.LayoutParams) {
        // LinearLayout handles RTL automatically in most cases
        // Additional adjustments can be made here if needed
    }
    
    private fun reverseChildOrder(viewGroup: ViewGroup) {
        val children = mutableListOf<View>()
        for (i in 0 until viewGroup.childCount) {
            children.add(viewGroup.getChildAt(i))
        }
        
        viewGroup.removeAllViews()
        children.reversed().forEach { child ->
            viewGroup.addView(child)
        }
    }
}

/**
 * Islamic language information for RTL support
 */
data class IslamicLanguageInfo(
    val englishName: String,
    val nativeName: String,
    val isRTL: Boolean,
    val hasSpecialCalligraphy: Boolean = false
)

/**
 * RTL content types for proper configuration
 */
enum class RTLContentType {
    ARABIC_PRAYER,
    MEMORIAL_TEXT,
    NAVIGATION,
    FORM_INPUT,
    MIXED_CONTENT,
    GENERAL
}

/**
 * RTL input field types
 */
enum class RTLInputType {
    ARABIC_TEXT,
    NAME_FIELD,
    EMAIL_PHONE,
    TEXT
}

/**
 * Text direction options
 */
enum class TextDirection {
    LTR,
    RTL,
    AUTO
}

/**
 * Cultural content types for reading patterns
 */
enum class CulturalContentType {
    QURANIC_VERSE,
    MEMORIAL_DETAILS,
    PRAYER_INSTRUCTIONS,
    COMMUNITY_LIST
}

/**
 * Reading start positions
 */
enum class ReadingStartPosition {
    TOP_LEFT,
    TOP_RIGHT,
    TOP_CENTER,
    CENTER
}

/**
 * Content flow patterns
 */
enum class FlowPattern {
    HORIZONTAL_THEN_VERTICAL,
    VERTICAL_THEN_HORIZONTAL,
    CENTERED_FLOW,
    LIST_VERTICAL
}

/**
 * Mixed directional content segment
 */
data class TextSegment(
    val text: String,
    val direction: TextDirection,
    val language: String? = null
)

/**
 * Mixed directional content container
 */
data class MixedDirectionalContent(
    val segments: List<TextSegment>
)

/**
 * Cultural reading pattern information
 */
data class ReadingPattern(
    val startPosition: ReadingStartPosition,
    val direction: TextDirection,
    val flowPattern: FlowPattern,
    val culturalNote: String
)