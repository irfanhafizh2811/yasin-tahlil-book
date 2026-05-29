package com.app_muslim.surah_yasin.core.ui.validation

// Import removed to fix compilation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Islamic Prayer Traditions Validator
 * 
 * Comprehensive validation system for Islamic prayer traditions, ensuring that
 * prayer practices, sequences, texts, and customs align with authentic Islamic
 * teachings across different schools of thought and regional practices.
 * 
 * Validates:
 * - Prayer text authenticity and accuracy
 * - Prayer sequence and ritual correctness
 * - School of thought compatibility
 * - Regional prayer customs
 * - Timing and contextual appropriateness
 * - Community prayer protocols
 */
@Singleton
class IslamicPrayerTraditionsValidator @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        // Authentic Islamic prayers with verification sources
        val AUTHENTICATED_PRAYERS = mapOf(
            PrayerType.TAHLIL to AuthenticatedPrayer(
                arabicText = "لا إله إلا الله",
                transliteration = "La ilaha illa Allah",
                englishTranslation = "There is no deity except Allah",
                sources = listOf("Quran 47:19", "Sahih al-Bukhari"),
                schools = SchoolOfThought.values().toList(),
                contexts = listOf(PrayerContext.MEMORIAL, PrayerContext.DHIKR, PrayerContext.DAILY),
                isUniversal = true
            ),
            
            PrayerType.YASIN to AuthenticatedPrayer(
                arabicText = "سورة يس",
                transliteration = "Surah Ya-Sin",
                englishTranslation = "Chapter Ya-Sin (Quran 36:1-83)",
                sources = listOf("Quran Chapter 36"),
                schools = SchoolOfThought.values().toList(),
                contexts = listOf(PrayerContext.MEMORIAL, PrayerContext.RECITATION),
                isUniversal = true,
                specialNotes = "Complete Surah Ya-Sin recitation for deceased"
            ),
            
            PrayerType.FATIHAH to AuthenticatedPrayer(
                arabicText = "الحمد لله رب العالمين",
                transliteration = "Alhamdulillahi rabbil alameen",
                englishTranslation = "Praise be to Allah, Lord of all the worlds",
                sources = listOf("Quran 1:1-7", "Sahih Muslim"),
                schools = SchoolOfThought.values().toList(),
                contexts = listOf(PrayerContext.MEMORIAL, PrayerContext.DAILY, PrayerContext.FORMAL_PRAYER),
                isUniversal = true,
                specialNotes = "Essential opening of formal prayers"
            ),
            
            PrayerType.ISTIGHFAR to AuthenticatedPrayer(
                arabicText = "أستغفر الله العظيم الذي لا إله إلا هو الحي القيوم وأتوب إليه",
                transliteration = "Astaghfirullaha al-azeem alladhi la ilaha illa huwa al-hayyu al-qayyumu wa atubu ilayh",
                englishTranslation = "I seek forgiveness from Allah the Great, who there is none worthy of worship but Him, the Living, the Sustainer, and I repent to Him",
                sources = listOf("Sunan Abu Dawood", "Sunan at-Tirmidhi"),
                schools = SchoolOfThought.values().toList(),
                contexts = listOf(PrayerContext.REPENTANCE, PrayerContext.DHIKR),
                isUniversal = true
            ),
            
            PrayerType.SALAWAT to AuthenticatedPrayer(
                arabicText = "اللهم صل على محمد وعلى آل محمد كما صليت على إبراهيم وعلى آل إبراهيم",
                transliteration = "Allahumma salli ala Muhammad wa ala ali Muhammad kama sallayta ala Ibrahim wa ala ali Ibrahim",
                englishTranslation = "O Allah, send prayers upon Muhammad and the family of Muhammad as You sent prayers upon Ibrahim and the family of Ibrahim",
                sources = listOf("Sahih al-Bukhari", "Sahih Muslim"),
                schools = SchoolOfThought.values().toList(),
                contexts = listOf(PrayerContext.BLESSING, PrayerContext.DHIKR, PrayerContext.FORMAL_PRAYER),
                isUniversal = true
            )
        )
        
        // School-specific prayer variations and preferences
        val SCHOOL_PREFERENCES = mapOf(
            SchoolOfThought.HANAFI to SchoolPreferences(
                preferredPrayerOrder = listOf(PrayerType.FATIHAH, PrayerType.SALAWAT, PrayerType.TAHLIL),
                specificPhrases = listOf("آمين" to "Ameen (said quietly)"),
                regionCompatibility = listOf(IslamicRegion.SOUTH_ASIA, IslamicRegion.CENTRAL_ASIA),
                memorialCustoms = "40-day memorial with community recitation"
            ),
            
            SchoolOfThought.SHAFI to SchoolPreferences(
                preferredPrayerOrder = listOf(PrayerType.FATIHAH, PrayerType.YASIN, PrayerType.SALAWAT),
                specificPhrases = listOf("آمين" to "Ameen (said aloud)"),
                regionCompatibility = listOf(IslamicRegion.SOUTHEAST_ASIA, IslamicRegion.MIDDLE_EAST),
                memorialCustoms = "7 and 40-day memorial gatherings"
            ),
            
            SchoolOfThought.MALIKI to SchoolPreferences(
                preferredPrayerOrder = listOf(PrayerType.FATIHAH, PrayerType.ISTIGHFAR, PrayerType.TAHLIL),
                specificPhrases = listOf("بسم الله" to "Bismillah before recitation"),
                regionCompatibility = listOf(IslamicRegion.NORTH_AFRICA, IslamicRegion.SUB_SAHARAN_AFRICA),
                memorialCustoms = "Community prayer with food distribution"
            ),
            
            SchoolOfThought.HANBALI to SchoolPreferences(
                preferredPrayerOrder = listOf(PrayerType.FATIHAH, PrayerType.TAHLIL, PrayerType.ISTIGHFAR),
                specificPhrases = listOf("الله أكبر" to "Allahu Akbar during transitions"),
                regionCompatibility = listOf(IslamicRegion.MIDDLE_EAST),
                memorialCustoms = "Simple memorial without elaborate gatherings"
            ),
            
            SchoolOfThought.JAFARI to SchoolPreferences(
                preferredPrayerOrder = listOf(PrayerType.FATIHAH, PrayerType.SALAWAT, PrayerType.YASIN),
                specificPhrases = listOf("وعلى أهل بيته" to "And upon his household"),
                regionCompatibility = listOf(IslamicRegion.MIDDLE_EAST, IslamicRegion.SOUTH_ASIA),
                memorialCustoms = "Majlis with extended recitation sessions"
            )
        )
        
        // Regional prayer customs and variations
        val REGIONAL_TRADITIONS = mapOf(
            IslamicRegion.MIDDLE_EAST to RegionalTradition(
                commonPrayers = listOf(PrayerType.FATIHAH, PrayerType.TAHLIL, PrayerType.SALAWAT),
                memorialDuration = listOf(3, 7, 40),
                communityGathering = CommunityGatheringStyle.GENDER_SEGREGATED,
                languageUsage = LanguageUsage.ARABIC_PRIMARILY,
                specialCustoms = "Quran recitation by qualified reciter"
            ),
            
            IslamicRegion.SOUTHEAST_ASIA to RegionalTradition(
                commonPrayers = listOf(PrayerType.YASIN, PrayerType.FATIHAH, PrayerType.TAHLIL),
                memorialDuration = listOf(3, 7, 14, 40, 100, 1000),
                communityGathering = CommunityGatheringStyle.MIXED_WITH_RESPECT,
                languageUsage = LanguageUsage.BILINGUAL,
                specialCustoms = "Tahlil with local language explanations"
            ),
            
            IslamicRegion.SOUTH_ASIA to RegionalTradition(
                commonPrayers = listOf(PrayerType.FATIHAH, PrayerType.SALAWAT, PrayerType.ISTIGHFAR),
                memorialDuration = listOf(3, 10, 40, 365),
                communityGathering = CommunityGatheringStyle.FAMILY_FOCUSED,
                languageUsage = LanguageUsage.MULTILINGUAL,
                specialCustoms = "Khatm al-Quran and food distribution (sadqa)"
            ),
            
            IslamicRegion.NORTH_AFRICA to RegionalTradition(
                commonPrayers = listOf(PrayerType.FATIHAH, PrayerType.TAHLIL, PrayerType.ISTIGHFAR),
                memorialDuration = listOf(3, 7, 40),
                communityGathering = CommunityGatheringStyle.COMMUNITY_WIDE,
                languageUsage = LanguageUsage.ARABIC_WITH_LOCAL,
                specialCustoms = "Group dhikr with traditional melodies"
            )
        )
    }

    /**
     * Validate Islamic prayer tradition implementation
     */
    suspend fun validatePrayerTradition(
        prayerImplementation: PrayerImplementation
    ): PrayerValidationResult {
        val violations = mutableListOf<PrayerViolation>()
        val warnings = mutableListOf<PrayerWarning>()
        val recommendations = mutableListOf<PrayerRecommendation>()

        // 1. Validate prayer authenticity
        validatePrayerAuthenticity(prayerImplementation, violations, warnings)

        // 2. Validate school of thought compatibility
        validateSchoolCompatibility(prayerImplementation, violations, warnings, recommendations)

        // 3. Validate regional customs
        validateRegionalCustoms(prayerImplementation, warnings, recommendations)

        // 4. Validate prayer sequence and structure
        validatePrayerSequence(prayerImplementation, warnings, recommendations)

        // 5. Validate contextual appropriateness
        validateContextualAppropriateness(prayerImplementation, violations, warnings)

        // 6. Validate community prayer protocols
        validateCommunityProtocols(prayerImplementation, warnings, recommendations)

        return PrayerValidationResult(
            isValid = violations.isEmpty(),
            violations = violations,
            warnings = warnings,
            recommendations = recommendations,
            authenticityScore = calculateAuthenticityScore(prayerImplementation),
            schoolCompatibility = checkSchoolCompatibility(prayerImplementation),
            regionalAppropriateness = checkRegionalAppropriateness(prayerImplementation)
        )
    }

    /**
     * Get prayer recommendations for specific context
     */
    fun getPrayerRecommendations(
        context: PrayerContext,
        school: SchoolOfThought,
        region: IslamicRegion,
        duration: PrayerDuration = PrayerDuration.STANDARD
    ): PrayerRecommendations {
        val schoolPrefs = SCHOOL_PREFERENCES[school]
        val regionalTradition = REGIONAL_TRADITIONS[region]
        
        val recommendedPrayers = when (context) {
            PrayerContext.MEMORIAL -> {
                val basePrayers = listOf(PrayerType.FATIHAH, PrayerType.YASIN, PrayerType.TAHLIL)
                when (duration) {
                    PrayerDuration.SHORT -> basePrayers.take(1)
                    PrayerDuration.STANDARD -> basePrayers.take(2)
                    PrayerDuration.EXTENDED -> basePrayers + listOf(PrayerType.ISTIGHFAR, PrayerType.SALAWAT)
                }
            }
            PrayerContext.DHIKR -> {
                listOf(PrayerType.TAHLIL, PrayerType.ISTIGHFAR, PrayerType.SALAWAT)
            }
            PrayerContext.DAILY -> {
                listOf(PrayerType.ISTIGHFAR, PrayerType.SALAWAT)
            }
            else -> schoolPrefs?.preferredPrayerOrder ?: listOf(PrayerType.FATIHAH)
        }

        return PrayerRecommendations(
            recommendedPrayers = recommendedPrayers.mapNotNull { type ->
                AUTHENTICATED_PRAYERS[type]?.let { prayer ->
                    RecommendedPrayer(
                        type = type,
                        arabicText = prayer.arabicText,
                        transliteration = prayer.transliteration,
                        translation = prayer.englishTranslation,
                        source = prayer.sources.first(),
                        schoolNotes = schoolPrefs?.specificPhrases?.firstOrNull()?.second,
                        regionalNotes = regionalTradition?.specialCustoms
                    )
                }
            },
            prayerSequence = recommendedPrayers,
            estimatedDuration = calculatePrayerDuration(recommendedPrayers, duration),
            schoolSpecificNotes = schoolPrefs?.specificPhrases?.map { "${it.first}: ${it.second}" } ?: emptyList(),
            regionalCustoms = regionalTradition?.specialCustoms,
            communityGuidelines = regionalTradition?.communityGathering?.name?.replace("_", " ")
        )
    }

    /**
     * Validate specific prayer text against authenticated sources
     */
    suspend fun validatePrayerText(
        prayerType: PrayerType,
        providedText: String,
        language: String = "arabic"
    ): PrayerTextValidationResult {
        val authenticPrayer = AUTHENTICATED_PRAYERS[prayerType]
            ?: return PrayerTextValidationResult.error("Prayer type not recognized")

        val issues = mutableListOf<TextValidationIssue>()
        val suggestions = mutableListOf<String>()

        when (language.lowercase()) {
            "arabic" -> {
                validateArabicPrayerText(providedText, authenticPrayer.arabicText, issues, suggestions)
            }
            "transliteration" -> {
                validateTransliterationText(providedText, authenticPrayer.transliteration, issues, suggestions)
            }
            else -> {
                // For other languages, check if translation maintains meaning
                validateTranslationText(providedText, authenticPrayer.englishTranslation, language, issues, suggestions)
            }
        }

        val accuracyScore = calculateTextAccuracy(providedText, authenticPrayer, language)

        return PrayerTextValidationResult(
            isAccurate = issues.none { it.severity == ValidationSeverity.CRITICAL },
            accuracyScore = accuracyScore,
            issues = issues,
            suggestions = suggestions,
            authenticSource = authenticPrayer.sources.first(),
            schoolCompatibility = authenticPrayer.schools
        )
    }

    /**
     * Validate prayer authenticity against Islamic sources
     */
    private fun validatePrayerAuthenticity(
        implementation: PrayerImplementation,
        violations: MutableList<PrayerViolation>,
        warnings: MutableList<PrayerWarning>
    ) {
        implementation.prayers.forEach { prayer ->
            val authenticPrayer = AUTHENTICATED_PRAYERS[prayer.type]
            
            if (authenticPrayer == null) {
                violations.add(
                    PrayerViolation(
                        type = ViolationType.UNRECOGNIZED_PRAYER,
                        message = "Prayer type '${prayer.type}' is not recognized in Islamic tradition",
                        severity = ViolationSeverity.CRITICAL,
                        affectedPrayer = prayer.type
                    )
                )
                return@forEach
            }

            // Check text accuracy
            if (prayer.arabicText.isNotEmpty()) {
                val similarity = calculateTextSimilarity(prayer.arabicText, authenticPrayer.arabicText)
                if (similarity < 0.8) {
                    warnings.add(
                        PrayerWarning(
                            category = WarningCategory.TEXT_ACCURACY,
                            message = "Prayer text may differ from authentic sources (${(similarity * 100).toInt()}% match)",
                            affectedPrayer = prayer.type,
                            suggestion = "Verify with source: ${authenticPrayer.sources.first()}"
                        )
                    )
                }
            }
        }
    }

    /**
     * Validate school of thought compatibility
     */
    private fun validateSchoolCompatibility(
        implementation: PrayerImplementation,
        violations: MutableList<PrayerViolation>,
        warnings: MutableList<PrayerWarning>,
        recommendations: MutableList<PrayerRecommendation>
    ) {
        val schoolPrefs = SCHOOL_PREFERENCES[implementation.schoolOfThought] ?: return

        // Check prayer order preference
        val implementedOrder = implementation.prayers.map { it.type }
        val preferredOrder = schoolPrefs.preferredPrayerOrder

        if (implementedOrder.isNotEmpty() && preferredOrder.isNotEmpty()) {
            val hasPreferredPrayer = implementedOrder.any { it in preferredOrder }
            if (!hasPreferredPrayer) {
                recommendations.add(
                    PrayerRecommendation(
                        category = RecommendationCategory.SCHOOL_PREFERENCE,
                        suggestion = "Consider including prayers preferred in ${implementation.schoolOfThought} school: ${preferredOrder.take(2).joinToString(", ")}",
                        priority = RecommendationPriority.MEDIUM
                    )
                )
            }
        }

        // Check region compatibility
        if (implementation.region !in schoolPrefs.regionCompatibility) {
            warnings.add(
                PrayerWarning(
                    category = WarningCategory.REGIONAL_COMPATIBILITY,
                    message = "${implementation.schoolOfThought} school practices may not be common in ${implementation.region}",
                    suggestion = "Consider regional variations in prayer customs"
                )
            )
        }
    }

    /**
     * Validate regional customs
     */
    private fun validateRegionalCustoms(
        implementation: PrayerImplementation,
        warnings: MutableList<PrayerWarning>,
        recommendations: MutableList<PrayerRecommendation>
    ) {
        val regionalTradition = REGIONAL_TRADITIONS[implementation.region] ?: return

        // Check common prayers for region
        val implementedTypes = implementation.prayers.map { it.type }.toSet()
        val commonPrayers = regionalTradition.commonPrayers.toSet()
        val intersection = implementedTypes.intersect(commonPrayers)

        if (intersection.isEmpty() && implementedTypes.isNotEmpty()) {
            recommendations.add(
                PrayerRecommendation(
                    category = RecommendationCategory.REGIONAL_CUSTOM,
                    suggestion = "Consider including prayers common in ${implementation.region}: ${regionalTradition.commonPrayers.take(2).joinToString(", ")}",
                    priority = RecommendationPriority.LOW
                )
            )
        }

        // Check memorial duration
        if (implementation.context == PrayerContext.MEMORIAL) {
            implementation.memorialDuration?.let { duration ->
                if (duration !in regionalTradition.memorialDuration) {
                    warnings.add(
                        PrayerWarning(
                            category = WarningCategory.MEMORIAL_CUSTOM,
                            message = "Memorial duration of $duration days is not traditional in ${implementation.region}",
                            suggestion = "Traditional durations: ${regionalTradition.memorialDuration.joinToString(", ")} days"
                        )
                    )
                }
            }
        }
    }

    /**
     * Validate prayer sequence and structure
     */
    private fun validatePrayerSequence(
        implementation: PrayerImplementation,
        warnings: MutableList<PrayerWarning>,
        recommendations: MutableList<PrayerRecommendation>
    ) {
        val prayers = implementation.prayers

        if (prayers.isEmpty()) {
            warnings.add(
                PrayerWarning(
                    category = WarningCategory.SEQUENCE_STRUCTURE,
                    message = "No prayers specified in implementation",
                    suggestion = "Include at least Al-Fatihah for any Islamic prayer session"
                )
            )
            return
        }

        // Check if Fatihah is included (recommended for most contexts)
        val hasFatihah = prayers.any { it.type == PrayerType.FATIHAH }
        if (!hasFatihah && implementation.context in listOf(PrayerContext.MEMORIAL, PrayerContext.FORMAL_PRAYER)) {
            recommendations.add(
                PrayerRecommendation(
                    category = RecommendationCategory.PRAYER_INCLUSION,
                    suggestion = "Consider starting with Al-Fatihah (The Opening) as it is the foundation of Islamic prayer",
                    priority = RecommendationPriority.HIGH
                )
            )
        }

        // Check prayer order logic
        if (prayers.size > 1) {
            validatePrayerOrderLogic(prayers, recommendations)
        }
    }

    /**
     * Validate prayer order logic
     */
    private fun validatePrayerOrderLogic(
        prayers: List<ImplementedPrayer>,
        recommendations: MutableList<PrayerRecommendation>
    ) {
        // Fatihah should typically come first
        val fatihaIndex = prayers.indexOfFirst { it.type == PrayerType.FATIHAH }
        if (fatihaIndex > 0) {
            recommendations.add(
                PrayerRecommendation(
                    category = RecommendationCategory.PRAYER_ORDER,
                    suggestion = "Consider placing Al-Fatihah at the beginning of the prayer sequence",
                    priority = RecommendationPriority.MEDIUM
                )
            )
        }

        // Salawat (blessings on Prophet) should come before ending
        val salawatIndex = prayers.indexOfFirst { it.type == PrayerType.SALAWAT }
        val tahlilIndex = prayers.indexOfFirst { it.type == PrayerType.TAHLIL }
        
        if (salawatIndex >= 0 && tahlilIndex >= 0 && salawatIndex > tahlilIndex) {
            recommendations.add(
                PrayerRecommendation(
                    category = RecommendationCategory.PRAYER_ORDER,
                    suggestion = "Consider placing Salawat before Tahlil in the sequence",
                    priority = RecommendationPriority.LOW
                )
            )
        }
    }

    /**
     * Validate contextual appropriateness
     */
    private fun validateContextualAppropriateness(
        implementation: PrayerImplementation,
        violations: MutableList<PrayerViolation>,
        warnings: MutableList<PrayerWarning>
    ) {
        implementation.prayers.forEach { prayer ->
            val authenticPrayer = AUTHENTICATED_PRAYERS[prayer.type] ?: return@forEach
            
            if (implementation.context !in authenticPrayer.contexts) {
                warnings.add(
                    PrayerWarning(
                        category = WarningCategory.CONTEXTUAL_APPROPRIATENESS,
                        message = "${prayer.type} is not typically used in ${implementation.context} context",
                        affectedPrayer = prayer.type,
                        suggestion = "Verify appropriateness for this context"
                    )
                )
            }
        }
    }

    /**
     * Validate community prayer protocols
     */
    private fun validateCommunityProtocols(
        implementation: PrayerImplementation,
        warnings: MutableList<PrayerWarning>,
        recommendations: MutableList<PrayerRecommendation>
    ) {
        if (!implementation.isCommunityPrayer) return

        val regionalTradition = REGIONAL_TRADITIONS[implementation.region]
        
        regionalTradition?.let { tradition ->
            when (tradition.communityGathering) {
                CommunityGatheringStyle.GENDER_SEGREGATED -> {
                    if (implementation.isMixedGender) {
                        warnings.add(
                            PrayerWarning(
                                category = WarningCategory.COMMUNITY_PROTOCOL,
                                message = "Mixed-gender community prayers may not align with ${implementation.region} customs",
                                suggestion = "Consider separate spaces for different genders"
                            )
                        )
                    }
                }
                CommunityGatheringStyle.FAMILY_FOCUSED -> {
                    recommendations.add(
                        PrayerRecommendation(
                            category = RecommendationCategory.COMMUNITY_APPROACH,
                            suggestion = "In ${implementation.region}, family-focused prayer gatherings are preferred",
                            priority = RecommendationPriority.LOW
                        )
                    )
                }
                else -> { /* Other styles are more flexible */ }
            }
        }
    }

    /**
     * Calculate prayer authenticity score
     */
    private fun calculateAuthenticityScore(implementation: PrayerImplementation): Double {
        if (implementation.prayers.isEmpty()) return 0.0

        var totalScore = 0.0
        var prayerCount = 0

        implementation.prayers.forEach { prayer ->
            val authenticPrayer = AUTHENTICATED_PRAYERS[prayer.type]
            if (authenticPrayer != null) {
                val textSimilarity = calculateTextSimilarity(prayer.arabicText, authenticPrayer.arabicText)
                val contextMatch = if (implementation.context in authenticPrayer.contexts) 1.0 else 0.5
                val schoolMatch = if (implementation.schoolOfThought in authenticPrayer.schools) 1.0 else 0.8
                
                val prayerScore = (textSimilarity + contextMatch + schoolMatch) / 3
                totalScore += prayerScore
                prayerCount++
            }
        }

        return if (prayerCount > 0) totalScore / prayerCount else 0.0
    }

    /**
     * Check school compatibility
     */
    private fun checkSchoolCompatibility(implementation: PrayerImplementation): SchoolCompatibilityResult {
        val schoolPrefs = SCHOOL_PREFERENCES[implementation.schoolOfThought]
            ?: return SchoolCompatibilityResult.UNKNOWN

        val implementedTypes = implementation.prayers.map { it.type }
        val preferredTypes = schoolPrefs.preferredPrayerOrder
        
        val matchScore = implementedTypes.intersect(preferredTypes.toSet()).size.toDouble() / 
                        maxOf(implementedTypes.size, preferredTypes.size, 1)

        return when {
            matchScore >= 0.7 -> SchoolCompatibilityResult.HIGHLY_COMPATIBLE
            matchScore >= 0.4 -> SchoolCompatibilityResult.MODERATELY_COMPATIBLE
            matchScore > 0 -> SchoolCompatibilityResult.PARTIALLY_COMPATIBLE
            else -> SchoolCompatibilityResult.NOT_COMPATIBLE
        }
    }

    /**
     * Check regional appropriateness
     */
    private fun checkRegionalAppropriateness(implementation: PrayerImplementation): RegionalAppropriatenessResult {
        val regionalTradition = REGIONAL_TRADITIONS[implementation.region]
            ?: return RegionalAppropriatenessResult.UNKNOWN

        val implementedTypes = implementation.prayers.map { it.type }
        val commonTypes = regionalTradition.commonPrayers
        
        val matchScore = implementedTypes.intersect(commonTypes.toSet()).size.toDouble() / 
                        maxOf(implementedTypes.size, commonTypes.size, 1)

        return when {
            matchScore >= 0.6 -> RegionalAppropriatenessResult.HIGHLY_APPROPRIATE
            matchScore >= 0.3 -> RegionalAppropriatenessResult.MODERATELY_APPROPRIATE
            matchScore > 0 -> RegionalAppropriatenessResult.PARTIALLY_APPROPRIATE
            else -> RegionalAppropriatenessResult.NOT_APPROPRIATE
        }
    }

    /**
     * Supporting validation methods
     */
    private fun validateArabicPrayerText(
        providedText: String,
        authenticText: String,
        issues: MutableList<TextValidationIssue>,
        suggestions: MutableList<String>
    ) {
        val similarity = calculateTextSimilarity(providedText, authenticText)
        when {
            similarity < 0.6 -> {
                issues.add(
                    TextValidationIssue(
                        severity = ValidationSeverity.CRITICAL,
                        message = "Arabic text significantly differs from authentic source",
                        suggestion = "Please verify with authentic Islamic sources"
                    )
                )
            }
            similarity < 0.8 -> {
                issues.add(
                    TextValidationIssue(
                        severity = ValidationSeverity.MODERATE,
                        message = "Arabic text has some differences from authentic source",
                        suggestion = "Consider reviewing against original sources"
                    )
                )
            }
            similarity < 0.95 -> {
                suggestions.add("Minor variations detected - this may be due to diacritical marks or formatting")
            }
        }
    }

    private fun validateTransliterationText(
        providedText: String,
        authenticText: String,
        issues: MutableList<TextValidationIssue>,
        suggestions: MutableList<String>
    ) {
        val similarity = calculateTextSimilarity(providedText.lowercase(), authenticText.lowercase())
        if (similarity < 0.7) {
            issues.add(
                TextValidationIssue(
                    severity = ValidationSeverity.MODERATE,
                    message = "Transliteration differs from standard academic transliteration",
                    suggestion = "Consider using standard transliteration: $authenticText"
                )
            )
        }
    }

    private fun validateTranslationText(
        providedText: String,
        authenticTranslation: String,
        language: String,
        issues: MutableList<TextValidationIssue>,
        suggestions: MutableList<String>
    ) {
        // Basic translation validation - in practice would use more sophisticated NLP
        val providedWords = providedText.lowercase().split(" ").toSet()
        val authenticWords = authenticTranslation.lowercase().split(" ").toSet()
        val commonWords = providedWords.intersect(authenticWords)
        
        val similarity = commonWords.size.toDouble() / maxOf(providedWords.size, authenticWords.size, 1)
        
        if (similarity < 0.4) {
            issues.add(
                TextValidationIssue(
                    severity = ValidationSeverity.HIGH,
                    message = "Translation may not preserve the original meaning",
                    suggestion = "Consider reviewing translation with Islamic scholar"
                )
            )
        }
    }

    private fun calculateTextSimilarity(text1: String, text2: String): Double {
        // Simple Jaccard similarity - in practice would use more sophisticated algorithms
        val words1 = text1.split(" ").toSet()
        val words2 = text2.split(" ").toSet()
        val intersection = words1.intersect(words2).size
        val union = words1.union(words2).size
        return if (union == 0) 1.0 else intersection.toDouble() / union.toDouble()
    }

    private fun calculatePrayerDuration(prayers: List<PrayerType>, duration: PrayerDuration): String {
        val baseDuration = when (duration) {
            PrayerDuration.SHORT -> 5
            PrayerDuration.STANDARD -> 15
            PrayerDuration.EXTENDED -> 30
        }
        
        val totalMinutes = baseDuration + (prayers.size * 2)
        return "$totalMinutes minutes"
    }

    private fun calculateTextAccuracy(
        providedText: String,
        authenticPrayer: AuthenticatedPrayer,
        language: String
    ): Double {
        return when (language.lowercase()) {
            "arabic" -> calculateTextSimilarity(providedText, authenticPrayer.arabicText)
            "transliteration" -> calculateTextSimilarity(providedText.lowercase(), authenticPrayer.transliteration.lowercase())
            else -> calculateTextSimilarity(providedText.lowercase(), authenticPrayer.englishTranslation.lowercase())
        }
    }
}

/**
 * Supporting data classes and enums
 */
data class AuthenticatedPrayer(
    val arabicText: String,
    val transliteration: String,
    val englishTranslation: String,
    val sources: List<String>,
    val schools: List<SchoolOfThought>,
    val contexts: List<PrayerContext>,
    val isUniversal: Boolean,
    val specialNotes: String? = null
)

data class SchoolPreferences(
    val preferredPrayerOrder: List<PrayerType>,
    val specificPhrases: List<Pair<String, String>>,
    val regionCompatibility: List<IslamicRegion>,
    val memorialCustoms: String
)

data class RegionalTradition(
    val commonPrayers: List<PrayerType>,
    val memorialDuration: List<Int>,
    val communityGathering: CommunityGatheringStyle,
    val languageUsage: LanguageUsage,
    val specialCustoms: String
)

data class PrayerImplementation(
    val prayers: List<ImplementedPrayer>,
    val context: PrayerContext,
    val schoolOfThought: SchoolOfThought,
    val region: IslamicRegion,
    val isCommunityPrayer: Boolean = false,
    val isMixedGender: Boolean = false,
    val memorialDuration: Int? = null
)

data class ImplementedPrayer(
    val type: PrayerType,
    val arabicText: String,
    val transliteration: String = "",
    val translation: String = "",
    val language: String = "arabic"
)

data class PrayerValidationResult(
    val isValid: Boolean,
    val violations: List<PrayerViolation>,
    val warnings: List<PrayerWarning>,
    val recommendations: List<PrayerRecommendation>,
    val authenticityScore: Double,
    val schoolCompatibility: SchoolCompatibilityResult,
    val regionalAppropriateness: RegionalAppropriatenessResult
)

data class PrayerTextValidationResult(
    val isAccurate: Boolean,
    val accuracyScore: Double,
    val issues: List<TextValidationIssue>,
    val suggestions: List<String>,
    val authenticSource: String,
    val schoolCompatibility: List<SchoolOfThought>
) {
    companion object {
        fun error(message: String) = PrayerTextValidationResult(
            isAccurate = false,
            accuracyScore = 0.0,
            issues = listOf(TextValidationIssue(ValidationSeverity.CRITICAL, message)),
            suggestions = emptyList(),
            authenticSource = "Unknown",
            schoolCompatibility = emptyList()
        )
    }
}

data class PrayerRecommendations(
    val recommendedPrayers: List<RecommendedPrayer>,
    val prayerSequence: List<PrayerType>,
    val estimatedDuration: String,
    val schoolSpecificNotes: List<String>,
    val regionalCustoms: String?,
    val communityGuidelines: String?
)

data class RecommendedPrayer(
    val type: PrayerType,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val source: String,
    val schoolNotes: String?,
    val regionalNotes: String?
)

data class PrayerViolation(
    val type: ViolationType,
    val message: String,
    val severity: ViolationSeverity,
    val affectedPrayer: PrayerType? = null
)

data class PrayerWarning(
    val category: WarningCategory,
    val message: String,
    val affectedPrayer: PrayerType? = null,
    val suggestion: String? = null
)

data class PrayerRecommendation(
    val category: RecommendationCategory,
    val suggestion: String,
    val priority: RecommendationPriority
)

data class TextValidationIssue(
    val severity: ValidationSeverity,
    val message: String,
    val suggestion: String? = null
)

