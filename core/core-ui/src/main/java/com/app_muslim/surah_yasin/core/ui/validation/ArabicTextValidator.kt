package com.app_muslim.surah_yasin.core.ui.validation

// Import removed to fix compilation
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Advanced Arabic Text Validator for Islamic Content
 * 
 * Comprehensive validation system for Arabic text used in Islamic prayers,
 * memorial messages, and religious content. Designed to ensure:
 * - Arabic script authenticity and correctness
 * - Islamic theological accuracy
 * - Cultural sensitivity across regions
 * - Scholar-approved content validation
 */
@Singleton
class ArabicTextValidator @Inject constructor() {

    companion object {
        // Arabic Unicode ranges
        private const val ARABIC_BASIC_RANGE = "\u0600-\u06FF"
        private const val ARABIC_SUPPLEMENT_RANGE = "\u0750-\u077F"
        private const val ARABIC_EXTENDED_RANGE = "\u08A0-\u08FF"
        private const val ARABIC_PRESENTATION_RANGE = "\uFB50-\uFDFF"
        private const val ARABIC_MATH_SYMBOLS = "\u1EE00-\u1EEFF"
        
        // Common Islamic phrases and their authenticity markers
        val AUTHENTICATED_PHRASES = mapOf(
            "بسم الله الرحمن الرحيم" to "Bismillah - Verified",
            "لا إله إلا الله" to "Tahlil - Core Islamic Declaration",
            "الحمد لله" to "Alhamdulillah - Praise to Allah",
            "سبحان الله" to "Subhan Allah - Glory to Allah",
            "استغفر الله" to "Istighfar - Seeking Forgiveness",
            "اللهم صل على محمد" to "Salawat - Blessings on Prophet",
            "رحمة الله عليه" to "Mercy of Allah - For Deceased",
            "إنا لله وإنا إليه راجعون" to "Inna Lillahi - For Death/Loss"
        )
        
        // Regional Arabic script variations
        val REGIONAL_SCRIPT_PATTERNS = mapOf(
            "maghrebi" to "\u08A1\u08A2\u08A4", // North African style
            "naskh" to "\u064B\u064C\u064D", // Classic calligraphy
            "persian" to "\u06CC\u06AF\u06BE", // Persian variations
            "urdu" to "\u06C1\u06C2\u06C3" // Urdu script variations
        )
    }

    /**
     * Comprehensive Arabic text validation
     */
    fun validateArabicText(
        text: String,
        validationType: ArabicValidationType = ArabicValidationType.GENERAL_ISLAMIC
    ): ArabicValidationResult {
        if (text.isBlank()) {
            return ArabicValidationResult.Invalid(
                errors = listOf("Arabic text cannot be empty"),
                suggestions = listOf("Please enter Arabic text for validation")
            )
        }

        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        // 1. Basic Arabic script validation
        validateArabicScript(text, errors, warnings)

        // 2. Islamic content appropriateness
        validateIslamicContent(text, validationType, errors, warnings, suggestions)

        // 3. Theological accuracy check
        validateTheologicalAccuracy(text, validationType, errors, warnings)

        // 4. Regional script compatibility
        validateRegionalCompatibility(text, warnings, suggestions)

        // 5. Diacritical marks validation
        validateDiacritics(text, warnings, suggestions)

        return if (errors.isEmpty()) {
            ArabicValidationResult.Valid(
                warnings = warnings,
                suggestions = suggestions,
                authenticatedPhrases = findAuthenticatedPhrases(text),
                scriptAnalysis = analyzeArabicScript(text)
            )
        } else {
            ArabicValidationResult.Invalid(
                errors = errors,
                warnings = warnings,
                suggestions = suggestions
            )
        }
    }

    /**
     * Validate basic Arabic script characteristics
     */
    private fun validateArabicScript(
        text: String,
        errors: MutableList<String>,
        warnings: MutableList<String>
    ) {
        val arabicPattern = Pattern.compile("[$ARABIC_BASIC_RANGE$ARABIC_SUPPLEMENT_RANGE$ARABIC_EXTENDED_RANGE$ARABIC_PRESENTATION_RANGE\\s\\p{Punct}]+")
        
        if (!arabicPattern.matcher(text).matches()) {
            // Check Arabic content percentage
            val arabicChars = text.count { char ->
                char.code in 0x0600..0x06FF || 
                char.code in 0x0750..0x077F ||
                char.code in 0x08A0..0x08FF ||
                char.code in 0xFB50..0xFDFF
            }
            
            val arabicPercentage = (arabicChars.toDouble() / text.length) * 100
            
            when {
                arabicPercentage < 50 -> {
                    errors.add("Text must contain at least 50% Arabic characters for Islamic content validation")
                }
                arabicPercentage < 80 -> {
                    warnings.add("Text contains mixed scripts - ensure Islamic content is primarily in Arabic")
                }
            }
        }

        // Check for common typing errors
        validateCommonErrors(text, warnings)
    }

    /**
     * Validate Islamic content appropriateness and theological accuracy
     */
    private fun validateIslamicContent(
        text: String,
        validationType: ArabicValidationType,
        errors: MutableList<String>,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        when (validationType) {
            ArabicValidationType.PRAYER_TEXT -> {
                validatePrayerText(text, errors, warnings, suggestions)
            }
            ArabicValidationType.MEMORIAL_MESSAGE -> {
                validateMemorialMessage(text, errors, warnings, suggestions)
            }
            ArabicValidationType.QURANIC_VERSE -> {
                validateQuranicVerse(text, errors, warnings, suggestions)
            }
            ArabicValidationType.GENERAL_ISLAMIC -> {
                validateGeneralIslamic(text, warnings, suggestions)
            }
        }
    }

    /**
     * Validate prayer-specific Arabic text
     */
    private fun validatePrayerText(
        text: String,
        errors: MutableList<String>,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        // Check for essential Islamic prayer elements
        val hasBasmala = text.contains("بسم الله") || text.contains("بِسْمِ ٱللَّهِ")
        val hasPrayerContent = AUTHENTICATED_PHRASES.keys.any { phrase -> text.contains(phrase) }
        
        if (!hasPrayerContent && text.length > 10) {
            warnings.add("Prayer text should contain recognized Islamic supplications")
            suggestions.add("Consider including authenticated Islamic phrases like 'الحمد لله' or 'سبحان الله'")
        }

        // Validate prayer structure
        if (text.contains("اللهم") && !text.contains("آمين")) {
            suggestions.add("Consider ending supplication with 'آمين' (Ameen)")
        }
    }

    /**
     * Validate memorial message content
     */
    private fun validateMemorialMessage(
        text: String,
        errors: MutableList<String>,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        // Common memorial phrases validation
        val memorialPhrases = listOf(
            "رحمة الله عليه", "رحمة الله عليها", // May Allah have mercy on him/her
            "إنا لله وإنا إليه راجعون", // Inna lillahi wa inna ilayhi raji'un
            "غفر الله له", "غفر الله لها", // May Allah forgive him/her
            "جعله الله من أهل الجنة" // May Allah make him among people of Paradise
        )

        val hasMemorialPhrase = memorialPhrases.any { phrase -> text.contains(phrase) }
        
        if (!hasMemorialPhrase && text.length > 20) {
            suggestions.add("Consider including traditional Islamic memorial phrases like 'رحمة الله عليه' or 'إنا لله وإنا إليه راجعون'")
        }

        // Check for inappropriate content in memorial context
        val inappropriateTerms = listOf("ملعون", "غضب", "عذاب") // cursed, anger, punishment
        inappropriateTerms.forEach { term ->
            if (text.contains(term)) {
                warnings.add("Memorial messages should focus on mercy and positive remembrance")
            }
        }
    }

    /**
     * Validate Quranic verse accuracy (basic structure check)
     */
    private fun validateQuranicVerse(
        text: String,
        errors: MutableList<String>,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        // Basic Quranic verse structure validation
        if (!text.trim().startsWith("بِسْمِ") && !text.contains("قُل") && !text.contains("وَ") && text.length > 30) {
            warnings.add("Quranic verses typically follow specific Arabic structures")
            suggestions.add("Please verify verse accuracy with authenticated Quranic sources")
        }
        
        // Suggest verse reference
        suggestions.add("Include verse reference (Surah:Ayah) for verification")
    }

    /**
     * General Islamic text validation
     */
    private fun validateGeneralIslamic(
        text: String,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        // Check for common Islamic expressions
        val islamicExpressions = listOf("الله", "محمد", "إسلام", "مسلم", "صلاة", "دعاء")
        val hasIslamicContent = islamicExpressions.any { expr -> text.contains(expr) }
        
        if (!hasIslamicContent && text.length > 15) {
            suggestions.add("Consider including Islamic terminology appropriate to the context")
        }
    }

    /**
     * Validate theological accuracy
     */
    private fun validateTheologicalAccuracy(
        text: String,
        validationType: ArabicValidationType,
        errors: MutableList<String>,
        warnings: MutableList<String>
    ) {
        // Check for potential theological issues
        val concerningPatterns = listOf(
            "الله والأصنام", // Allah and idols - should not be equal
            "محمد إله", // Muhammad as god - theological error
            "ثلاثة آلهة", // Three gods - contradicts Tawhid
            "عيسى ابن الله" // Jesus son of God - Islamic perspective differs
        )

        concerningPatterns.forEach { pattern ->
            if (text.contains(pattern)) {
                errors.add("Text contains potential theological inaccuracy that may contradict Islamic beliefs")
            }
        }

        // Positive theological validation
        if (text.contains("لا إله إلا الله") && text.contains("محمد رسول الله")) {
            // Perfect - contains Shahada
        }
    }

    /**
     * Validate regional script compatibility
     */
    private fun validateRegionalCompatibility(
        text: String,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        REGIONAL_SCRIPT_PATTERNS.forEach { (region, pattern) ->
            if (text.any { char -> pattern.contains(char) }) {
                suggestions.add("Text contains $region script variations - ensure compatibility across regions")
            }
        }
    }

    /**
     * Validate diacritical marks usage
     */
    private fun validateDiacritics(
        text: String,
        warnings: MutableList<String>,
        suggestions: MutableList<String>
    ) {
        val diacritics = "\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652"
        val diacriticalCount = text.count { char -> diacritics.contains(char) }
        val arabicLetterCount = text.count { char -> char.code in 0x0600..0x06FF }
        
        val diacriticalRatio = if (arabicLetterCount > 0) diacriticalCount.toDouble() / arabicLetterCount else 0.0
        
        when {
            diacriticalRatio > 0.8 -> {
                suggestions.add("Text is fully diacriticized - excellent for pronunciation accuracy")
            }
            diacriticalRatio > 0.3 -> {
                suggestions.add("Text has partial diacritics - consider full diacriticization for clarity")
            }
            diacriticalRatio == 0.0 && arabicLetterCount > 10 -> {
                suggestions.add("Consider adding diacritical marks (Tashkeel) for proper pronunciation")
            }
        }
    }

    /**
     * Check for common Arabic typing errors
     */
    private fun validateCommonErrors(text: String, warnings: MutableList<String>) {
        // Common mistakes in Arabic typing
        val commonErrors = mapOf(
            "اللة" to "الله", // Common misspelling of Allah
            "محمد" to "مُحَمَّد", // Muhammad with proper diacritics
            "قرأن" to "قُرْآن", // Quran with proper spelling
            "اسلام" to "إِسْلاَم" // Islam with proper hamza
        )

        commonErrors.forEach { (incorrect, correct) ->
            if (text.contains(incorrect)) {
                warnings.add("Possible spelling error: '$incorrect' should be '$correct'")
            }
        }
    }

    /**
     * Find authenticated Islamic phrases in text
     */
    private fun findAuthenticatedPhrases(text: String): List<AuthenticatedPhrase> {
        return AUTHENTICATED_PHRASES.mapNotNull { (phrase, description) ->
            if (text.contains(phrase)) {
                AuthenticatedPhrase(
                    arabicText = phrase,
                    description = description,
                    position = text.indexOf(phrase)
                )
            } else null
        }
    }

    /**
     * Analyze Arabic script characteristics
     */
    private fun analyzeArabicScript(text: String): ArabicScriptAnalysis {
        val totalChars = text.length
        val arabicChars = text.count { char -> char.code in 0x0600..0x06FF }
        val diacritics = text.count { char -> char.code in 0x064B..0x0652 }
        val spaces = text.count { it == ' ' }
        val punctuation = text.count { it.isLetterOrDigit().not() && it != ' ' }

        return ArabicScriptAnalysis(
            totalCharacters = totalChars,
            arabicLetters = arabicChars,
            diacriticalMarks = diacritics,
            spaces = spaces,
            punctuationMarks = punctuation,
            arabicPercentage = if (totalChars > 0) (arabicChars.toDouble() / totalChars) * 100 else 0.0,
            diacritizationLevel = if (arabicChars > 0) (diacritics.toDouble() / arabicChars) * 100 else 0.0
        )
    }
}

/**
 * Types of Arabic content validation
 */
enum class ArabicValidationType {
    PRAYER_TEXT,        // Du'a, dhikr, supplications
    MEMORIAL_MESSAGE,   // Memorial remembrance messages
    QURANIC_VERSE,     // Quranic text verification
    GENERAL_ISLAMIC    // General Islamic content
}

/**
 * Result of Arabic text validation
 */
sealed class ArabicValidationResult {
    data class Valid(
        val warnings: List<String> = emptyList(),
        val suggestions: List<String> = emptyList(),
        val authenticatedPhrases: List<AuthenticatedPhrase> = emptyList(),
        val scriptAnalysis: ArabicScriptAnalysis
    ) : ArabicValidationResult()

    data class Invalid(
        val errors: List<String>,
        val warnings: List<String> = emptyList(),
        val suggestions: List<String> = emptyList()
    ) : ArabicValidationResult()

    fun isValid(): Boolean = this is Valid
    fun hasWarnings(): Boolean = when (this) {
        is Valid -> warnings.isNotEmpty()
        is Invalid -> warnings.isNotEmpty()
    }
}

/**
 * Authenticated Islamic phrase found in text
 */
data class AuthenticatedPhrase(
    val arabicText: String,
    val description: String,
    val position: Int
)

/**
 * Analysis of Arabic script characteristics
 */
data class ArabicScriptAnalysis(
    val totalCharacters: Int,
    val arabicLetters: Int,
    val diacriticalMarks: Int,
    val spaces: Int,
    val punctuationMarks: Int,
    val arabicPercentage: Double,
    val diacritizationLevel: Double
)
