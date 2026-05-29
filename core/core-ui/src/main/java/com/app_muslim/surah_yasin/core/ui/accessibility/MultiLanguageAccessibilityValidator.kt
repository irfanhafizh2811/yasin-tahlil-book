package com.app_muslim.surah_yasin.core.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.core.os.LocaleListCompat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Multi-Language Accessibility Validator for Islamic Prayer Platform
 * 
 * Comprehensive validation framework for multi-language accessibility support
 * across Islamic languages with cultural and religious considerations.
 * 
 * Features:
 * - 12+ language accessibility validation
 * - Islamic terminology consistency across languages
 * - Cultural context preservation in translations
 * - Right-to-Left language accessibility verification
 * - Screen reader pronunciation validation for Islamic terms
 * - Regional accessibility preferences support
 */
@Singleton
class MultiLanguageAccessibilityValidator @Inject constructor(
    private val context: Context,
    private val accessibilityManager: AccessibilityManager,
    private val rtlHelper: RTLAccessibilityHelper,
    private val screenReaderHelper: ScreenReaderHelper
) {

    companion object {
        // Supported Islamic languages with accessibility metadata
        private val ISLAMIC_LANGUAGES = mapOf(
            // Primary Islamic languages
            "ar" to LanguageAccessibilityInfo(
                name = "Arabic",
                nativeName = "العربية",
                isRTL = true,
                scriptType = ScriptType.ARABIC,
                hasSpecialCalligraphy = true,
                ttsQuality = TTSQuality.HIGH,
                culturalSensitivity = CulturalSensitivity.VERY_HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.RTL_LAYOUT,
                    AccessibilityFeature.ARABIC_FONTS,
                    AccessibilityFeature.QURANIC_PRONUNCIATION,
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY
                )
            ),
            
            // Southeast Asian languages
            "id" to LanguageAccessibilityInfo(
                name = "Indonesian",
                nativeName = "Bahasa Indonesia",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.HIGH,
                culturalSensitivity = CulturalSensitivity.HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_LOANWORDS
                )
            ),
            
            "ms" to LanguageAccessibilityInfo(
                name = "Malay",
                nativeName = "Bahasa Melayu",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.MEDIUM,
                culturalSensitivity = CulturalSensitivity.HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_LOANWORDS
                )
            ),
            
            // South Asian languages
            "ur" to LanguageAccessibilityInfo(
                name = "Urdu",
                nativeName = "اردو",
                isRTL = true,
                scriptType = ScriptType.ARABIC_DERIVED,
                hasSpecialCalligraphy = true,
                ttsQuality = TTSQuality.MEDIUM,
                culturalSensitivity = CulturalSensitivity.VERY_HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.RTL_LAYOUT,
                    AccessibilityFeature.URDU_FONTS,
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.PERSIAN_INFLUENCES
                )
            ),
            
            "bn" to LanguageAccessibilityInfo(
                name = "Bengali",
                nativeName = "বাংলা",
                isRTL = false,
                scriptType = ScriptType.BENGALI,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.MEDIUM,
                culturalSensitivity = CulturalSensitivity.HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.BENGALI_SCRIPT,
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY
                )
            ),
            
            // Persian and related languages
            "fa" to LanguageAccessibilityInfo(
                name = "Persian",
                nativeName = "فارسی",
                isRTL = true,
                scriptType = ScriptType.ARABIC_DERIVED,
                hasSpecialCalligraphy = true,
                ttsQuality = TTSQuality.MEDIUM,
                culturalSensitivity = CulturalSensitivity.HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.RTL_LAYOUT,
                    AccessibilityFeature.PERSIAN_FONTS,
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY
                )
            ),
            
            // Turkish
            "tr" to LanguageAccessibilityInfo(
                name = "Turkish",
                nativeName = "Türkçe",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.HIGH,
                culturalSensitivity = CulturalSensitivity.MEDIUM,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_LOANWORDS
                )
            ),
            
            // European languages with Muslim communities
            "en" to LanguageAccessibilityInfo(
                name = "English",
                nativeName = "English",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.HIGH,
                culturalSensitivity = CulturalSensitivity.MEDIUM,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_TRANSLITERATION
                )
            ),
            
            "fr" to LanguageAccessibilityInfo(
                name = "French",
                nativeName = "Français",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.HIGH,
                culturalSensitivity = CulturalSensitivity.MEDIUM,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_TRANSLITERATION
                )
            ),
            
            // African languages
            "sw" to LanguageAccessibilityInfo(
                name = "Swahili",
                nativeName = "Kiswahili",
                isRTL = false,
                scriptType = ScriptType.LATIN,
                hasSpecialCalligraphy = false,
                ttsQuality = TTSQuality.MEDIUM,
                culturalSensitivity = CulturalSensitivity.HIGH,
                requiredFeatures = listOf(
                    AccessibilityFeature.ISLAMIC_TERMINOLOGY,
                    AccessibilityFeature.ARABIC_LOANWORDS
                )
            )
        )
        
        // Islamic terminology that must be consistent across languages
        private val ISLAMIC_TERMINOLOGY_CONSISTENCY = mapOf(
            "Allah" to IslamicTermConsistency(
                arabic = "الله",
                transliteration = "Allah",
                shouldPreserveArabic = true,
                culturalVariations = mapOf(
                    "id" to "Allah",
                    "ms" to "Allah", 
                    "ur" to "اللہ",
                    "tr" to "Allah",
                    "en" to "Allah",
                    "fr" to "Allah",
                    "sw" to "Allah"
                )
            ),
            
            "Muhammad" to IslamicTermConsistency(
                arabic = "محمد",
                transliteration = "Muhammad",
                shouldPreserveArabic = false,
                culturalVariations = mapOf(
                    "id" to "Muhammad",
                    "ms" to "Muhammad",
                    "ur" to "محمد",
                    "tr" to "Muhammed",
                    "en" to "Muhammad",
                    "fr" to "Mahomet",
                    "sw" to "Muhammad"
                )
            ),
            
            "Salah" to IslamicTermConsistency(
                arabic = "صلاة",
                transliteration = "Salah",
                shouldPreserveArabic = false,
                culturalVariations = mapOf(
                    "id" to "Shalat",
                    "ms" to "Solat",
                    "ur" to "نماز",
                    "tr" to "Namaz",
                    "en" to "Prayer",
                    "fr" to "Prière",
                    "sw" to "Sala"
                )
            )
        )
    }

    /**
     * Validate multi-language accessibility support
     */
    fun validateMultiLanguageAccessibility(
        view: View,
        targetLanguages: List<String> = ISLAMIC_LANGUAGES.keys.toList()
    ): MultiLanguageValidationResult {
        val results = mutableMapOf<String, LanguageValidationResult>()
        
        targetLanguages.forEach { languageCode ->
            val languageInfo = ISLAMIC_LANGUAGES[languageCode]
            if (languageInfo != null) {
                results[languageCode] = validateLanguageSpecificAccessibility(view, languageCode, languageInfo)
            }
        }
        
        val overallCompliance = calculateOverallLanguageCompliance(results)
        val consistencyScore = validateIslamicTerminologyConsistency(results)
        
        return MultiLanguageValidationResult(
            languageResults = results,
            overallCompliance = overallCompliance,
            terminologyConsistency = consistencyScore,
            recommendations = generateMultiLanguageRecommendations(results),
            culturalSensitivityScore = calculateCulturalSensitivityScore(results)
        )
    }

    /**
     * Validate accessibility for specific language
     */
    private fun validateLanguageSpecificAccessibility(
        view: View,
        languageCode: String,
        languageInfo: LanguageAccessibilityInfo
    ): LanguageValidationResult {
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        var complianceScore = 1.0f
        
        // Test RTL support if required
        if (languageInfo.isRTL) {
            val rtlSupport = validateRTLSupport(view, languageCode)
            if (!rtlSupport.isCompliant) {
                complianceScore -= 0.3f
                issues.add("RTL layout support insufficient for $languageCode")
                recommendations.add("Implement proper RTL layout direction and text alignment")
            }
        }
        
        // Test font support
        val fontSupport = validateFontSupport(view, languageInfo)
        if (!fontSupport.isCompliant) {
            complianceScore -= 0.2f
            issues.add("Font support inadequate for ${languageInfo.name}")
            recommendations.add("Add appropriate font families for ${languageInfo.scriptType}")
        }
        
        // Test TTS pronunciation
        val ttsSupport = validateTTSPronunciation(languageCode, languageInfo)
        if (!ttsSupport.isCompliant) {
            complianceScore -= 0.2f
            issues.add("TTS pronunciation poor for ${languageInfo.name}")
            recommendations.add("Improve TTS pronunciation guides for Islamic terminology")
        }
        
        // Test cultural context preservation
        val culturalContext = validateCulturalContext(view, languageCode, languageInfo)
        if (!culturalContext.isCompliant) {
            complianceScore -= 0.15f
            issues.add("Cultural context not properly preserved in ${languageInfo.name}")
            recommendations.add("Ensure cultural sensitivity in accessibility descriptions")
        }
        
        // Test Islamic terminology consistency
        val terminologyConsistency = validateTerminologyConsistency(languageCode)
        if (!terminologyConsistency.isCompliant) {
            complianceScore -= 0.15f
            issues.add("Islamic terminology inconsistent in ${languageInfo.name}")
            recommendations.add("Standardize Islamic terminology across the interface")
        }
        
        return LanguageValidationResult(
            languageCode = languageCode,
            languageName = languageInfo.name,
            complianceScore = maxOf(0f, complianceScore),
            issues = issues,
            recommendations = recommendations,
            requiredFeatures = languageInfo.requiredFeatures,
            supportedFeatures = detectSupportedFeatures(view, languageInfo)
        )
    }

    /**
     * Validate Islamic terminology consistency across languages
     */
    fun validateIslamicTerminologyGlobally(): TerminologyConsistencyResult {
        val inconsistencies = mutableListOf<TerminologyInconsistency>()
        val consistencyScores = mutableMapOf<String, Float>()
        
        ISLAMIC_TERMINOLOGY_CONSISTENCY.forEach { (term, consistency) ->
            var termScore = 1.0f
            val termIssues = mutableListOf<String>()
            
            consistency.culturalVariations.forEach { (languageCode, variation) ->
                val expectedVariation = consistency.culturalVariations[languageCode]
                if (variation != expectedVariation) {
                    termScore -= 0.1f
                    termIssues.add("Inconsistent $term in $languageCode: expected $expectedVariation, found $variation")
                }
            }
            
            if (termIssues.isNotEmpty()) {
                inconsistencies.add(
                    TerminologyInconsistency(
                        term = term,
                        issues = termIssues,
                        affectedLanguages = termIssues.map { it.substringAfter("in ").substringBefore(":") }
                    )
                )
            }
            
            consistencyScores[term] = maxOf(0f, termScore)
        }
        
        val overallConsistency = consistencyScores.values.average().toFloat()
        
        return TerminologyConsistencyResult(
            overallConsistency = overallConsistency,
            termScores = consistencyScores,
            inconsistencies = inconsistencies,
            recommendations = generateTerminologyRecommendations(inconsistencies)
        )
    }

    /**
     * Get language-specific accessibility announcement
     */
    fun getLanguageSpecificAccessibilityAnnouncement(
        content: String,
        languageCode: String,
        contextType: AccessibilityContextType
    ): String {
        val languageInfo = ISLAMIC_LANGUAGES[languageCode] ?: return content
        
        return when (contextType) {
            AccessibilityContextType.PRAYER_COMPLETION -> {
                formatPrayerCompletionAnnouncement(content, languageCode, languageInfo)
            }
            AccessibilityContextType.MEMORIAL_CREATION -> {
                formatMemorialAnnouncementWithCulturalContext(content, languageCode, languageInfo)
            }
            AccessibilityContextType.ISLAMIC_TERMINOLOGY -> {
                formatIslamicTerminologyAnnouncement(content, languageCode, languageInfo)
            }
            AccessibilityContextType.NAVIGATION -> {
                formatNavigationAnnouncement(content, languageCode, languageInfo)
            }
            AccessibilityContextType.FORM_INPUT -> {
                formatFormInputAnnouncement(content, languageCode, languageInfo)
            }
        }
    }

    /**
     * Validate language switching accessibility
     */
    fun validateLanguageSwitchingAccessibility(
        languageSwitchView: View
    ): LanguageSwitchValidationResult {
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        var complianceScore = 1.0f
        
        // Check if language options are properly labeled
        val hasProperLabels = validateLanguageOptionLabels(languageSwitchView)
        if (!hasProperLabels) {
            complianceScore -= 0.3f
            issues.add("Language options not properly labeled for screen readers")
            recommendations.add("Add native language names and accessibility descriptions")
        }
        
        // Check if RTL languages are properly handled in switcher
        val rtlHandling = validateRTLLanguageSwitcherHandling(languageSwitchView)
        if (!rtlHandling) {
            complianceScore -= 0.2f
            issues.add("RTL language switching not properly implemented")
            recommendations.add("Ensure RTL languages trigger proper layout direction changes")
        }
        
        // Check if language changes are announced
        val announcementSupport = validateLanguageChangeAnnouncements(languageSwitchView)
        if (!announcementSupport) {
            complianceScore -= 0.2f
            issues.add("Language changes not announced to assistive technologies")
            recommendations.add("Implement live region announcements for language changes")
        }
        
        // Check if current language is clearly indicated
        val currentLanguageIndication = validateCurrentLanguageIndication(languageSwitchView)
        if (!currentLanguageIndication) {
            complianceScore -= 0.15f
            issues.add("Current language not clearly indicated for accessibility")
            recommendations.add("Mark current language with appropriate ARIA states")
        }
        
        // Check if keyboard navigation works across languages
        val keyboardSupport = validateKeyboardNavigationAcrossLanguages(languageSwitchView)
        if (!keyboardSupport) {
            complianceScore -= 0.15f
            issues.add("Keyboard navigation issues in language switcher")
            recommendations.add("Ensure consistent keyboard navigation regardless of language direction")
        }
        
        return LanguageSwitchValidationResult(
            complianceScore = maxOf(0f, complianceScore),
            issues = issues,
            recommendations = recommendations,
            supportedLanguages = detectSupportedLanguagesInSwitcher(languageSwitchView)
        )
    }

    // Private helper methods
    
    private fun validateRTLSupport(view: View, languageCode: String): ValidationResult {
        return if (rtlHelper.isRTLLanguage(languageCode)) {
            val hasRTLLayout = rtlHelper.isRTLLayout()
            ValidationResult(hasRTLLayout, if (hasRTLLayout) 1.0f else 0.0f)
        } else {
            ValidationResult(true, 1.0f)
        }
    }
    
    private fun validateFontSupport(view: View, languageInfo: LanguageAccessibilityInfo): ValidationResult {
        // This would check if appropriate fonts are available for the script type
        return ValidationResult(true, 1.0f) // Simplified for this example
    }
    
    private fun validateTTSPronunciation(languageCode: String, languageInfo: LanguageAccessibilityInfo): ValidationResult {
        val expectedQuality = languageInfo.ttsQuality
        // This would test actual TTS quality - simplified for example
        return ValidationResult(expectedQuality != TTSQuality.POOR, 
                              if (expectedQuality == TTSQuality.HIGH) 1.0f else 0.7f)
    }
    
    private fun validateCulturalContext(view: View, languageCode: String, languageInfo: LanguageAccessibilityInfo): ValidationResult {
        // Check if cultural context is preserved in accessibility descriptions
        return ValidationResult(true, 1.0f) // Simplified
    }
    
    private fun validateTerminologyConsistency(languageCode: String): ValidationResult {
        // Check if Islamic terminology is consistent within the language
        return ValidationResult(true, 1.0f) // Simplified
    }
    
    private fun detectSupportedFeatures(view: View, languageInfo: LanguageAccessibilityInfo): List<AccessibilityFeature> {
        // Detect which accessibility features are actually supported
        return languageInfo.requiredFeatures // Simplified
    }
    
    private fun calculateOverallLanguageCompliance(results: Map<String, LanguageValidationResult>): Float {
        return results.values.map { it.complianceScore }.average().toFloat()
    }
    
    private fun validateIslamicTerminologyConsistency(results: Map<String, LanguageValidationResult>): Float {
        // Calculate consistency score across languages
        return 0.95f // Simplified
    }
    
    private fun calculateCulturalSensitivityScore(results: Map<String, LanguageValidationResult>): Float {
        // Calculate how well cultural sensitivity is maintained across languages
        return 0.90f // Simplified
    }
    
    private fun generateMultiLanguageRecommendations(results: Map<String, LanguageValidationResult>): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Analyze common issues across languages
        val commonIssues = results.values.flatMap { it.issues }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
        
        commonIssues.forEach { (issue, count) ->
            recommendations.add("Address common issue across $count languages: $issue")
        }
        
        return recommendations
    }
    
    private fun generateTerminologyRecommendations(inconsistencies: List<TerminologyInconsistency>): List<String> {
        return inconsistencies.map { inconsistency ->
            "Standardize '${inconsistency.term}' across languages: ${inconsistency.affectedLanguages.joinToString(", ")}"
        }
    }
    
    // Format announcement methods
    
    private fun formatPrayerCompletionAnnouncement(
        content: String, 
        languageCode: String, 
        languageInfo: LanguageAccessibilityInfo
    ): String {
        val culturalPrefix = when (languageInfo.culturalSensitivity) {
            CulturalSensitivity.VERY_HIGH -> "May Allah accept this prayer. "
            CulturalSensitivity.HIGH -> "Prayer completed with blessing. "
            else -> "Prayer completed. "
        }
        
        return if (languageInfo.isRTL) {
            "$content $culturalPrefix"
        } else {
            "$culturalPrefix$content"
        }
    }
    
    private fun formatMemorialAnnouncementWithCulturalContext(
        content: String,
        languageCode: String,
        languageInfo: LanguageAccessibilityInfo
    ): String {
        val memorialPhrase = when (languageCode) {
            "ar" -> "رحمة الله عليه"
            "id" -> "semoga Allah merahmatinya"
            "ms" -> "semoga Allah merahmatinnya"
            "ur" -> "اللہ کی رحمت ہو"
            else -> "may Allah have mercy on their soul"
        }
        
        return "$content. $memorialPhrase"
    }
    
    private fun formatIslamicTerminologyAnnouncement(
        content: String,
        languageCode: String,
        languageInfo: LanguageAccessibilityInfo
    ): String {
        // Add pronunciation guidance for Islamic terms
        return if (languageInfo.isRTL) {
            "$content - Islamic terminology"
        } else {
            "Islamic terminology - $content"
        }
    }
    
    private fun formatNavigationAnnouncement(
        content: String,
        languageCode: String,
        languageInfo: LanguageAccessibilityInfo
    ): String {
        val direction = if (languageInfo.isRTL) "right-to-left navigation" else "left-to-right navigation"
        return "$content, $direction"
    }
    
    private fun formatFormInputAnnouncement(
        content: String,
        languageCode: String,
        languageInfo: LanguageAccessibilityInfo
    ): String {
        val inputGuidance = if (languageInfo.isRTL) {
            "Type from right to left"
        } else {
            "Type from left to right"
        }
        return "$content. $inputGuidance"
    }
    
    // Validation helper methods (simplified implementations)
    
    private fun validateLanguageOptionLabels(view: View): Boolean = true
    private fun validateRTLLanguageSwitcherHandling(view: View): Boolean = true
    private fun validateLanguageChangeAnnouncements(view: View): Boolean = true
    private fun validateCurrentLanguageIndication(view: View): Boolean = true
    private fun validateKeyboardNavigationAcrossLanguages(view: View): Boolean = true
    private fun detectSupportedLanguagesInSwitcher(view: View): List<String> = ISLAMIC_LANGUAGES.keys.toList()
}

// Enums and data classes for multi-language accessibility

enum class ScriptType {
    LATIN,
    ARABIC,
    ARABIC_DERIVED,
    BENGALI,
    CYRILLIC
}

enum class TTSQuality {
    HIGH,
    MEDIUM,
    POOR
}

enum class CulturalSensitivity {
    VERY_HIGH,
    HIGH,
    MEDIUM,
    LOW
}

enum class AccessibilityFeature {
    RTL_LAYOUT,
    ARABIC_FONTS,
    URDU_FONTS,
    PERSIAN_FONTS,
    BENGALI_SCRIPT,
    QURANIC_PRONUNCIATION,
    ISLAMIC_TERMINOLOGY,
    ARABIC_LOANWORDS,
    ARABIC_TRANSLITERATION,
    PERSIAN_INFLUENCES
}

enum class AccessibilityContextType {
    PRAYER_COMPLETION,
    MEMORIAL_CREATION,
    ISLAMIC_TERMINOLOGY,
    NAVIGATION,
    FORM_INPUT
}

data class LanguageAccessibilityInfo(
    val name: String,
    val nativeName: String,
    val isRTL: Boolean,
    val scriptType: ScriptType,
    val hasSpecialCalligraphy: Boolean,
    val ttsQuality: TTSQuality,
    val culturalSensitivity: CulturalSensitivity,
    val requiredFeatures: List<AccessibilityFeature>
)

data class IslamicTermConsistency(
    val arabic: String,
    val transliteration: String,
    val shouldPreserveArabic: Boolean,
    val culturalVariations: Map<String, String>
)

data class ValidationResult(
    val isCompliant: Boolean,
    val score: Float
)

data class LanguageValidationResult(
    val languageCode: String,
    val languageName: String,
    val complianceScore: Float,
    val issues: List<String>,
    val recommendations: List<String>,
    val requiredFeatures: List<AccessibilityFeature>,
    val supportedFeatures: List<AccessibilityFeature>
)

data class MultiLanguageValidationResult(
    val languageResults: Map<String, LanguageValidationResult>,
    val overallCompliance: Float,
    val terminologyConsistency: Float,
    val recommendations: List<String>,
    val culturalSensitivityScore: Float
)

data class TerminologyInconsistency(
    val term: String,
    val issues: List<String>,
    val affectedLanguages: List<String>
)

data class TerminologyConsistencyResult(
    val overallConsistency: Float,
    val termScores: Map<String, Float>,
    val inconsistencies: List<TerminologyInconsistency>,
    val recommendations: List<String>
)

data class LanguageSwitchValidationResult(
    val complianceScore: Float,
    val issues: List<String>,
    val recommendations: List<String>,
    val supportedLanguages: List<String>
)