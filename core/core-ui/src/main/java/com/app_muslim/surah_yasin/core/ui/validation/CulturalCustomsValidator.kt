package com.app_muslim.surah_yasin.core.ui.validation

// Import removed to fix compilation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cultural Customs Validator for Global Islamic Practices
 * 
 * Comprehensive validation system for Islamic cultural customs across 20+ countries
 * and regions. Ensures memorial practices, prayer traditions, and social interactions
 * respect diverse Islamic cultures while maintaining religious authenticity.
 * 
 * Covers:
 * - Regional Islamic practices and customs
 * - Memorial traditions and death observances  
 * - Prayer customs and community practices
 * - Gender-appropriate interactions
 * - Family values and social customs
 * - Holiday and calendar observances
 */
@Singleton
class CulturalCustomsValidator @Inject constructor() {

    companion object {
        // Regional cultural data with specific customs
        val REGIONAL_CUSTOMS = mapOf(
            IslamicRegion.MIDDLE_EAST to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 40, 100, 365), // 3 days, 1 week, 40 days, 100 days, 1 year
                prayerPhotoPolicy = PhotoPolicy.RESTRICTED, // Conservative approach
                communityMemorialSharing = SharingPolicy.FAMILY_ONLY,
                womenParticipationLevel = ParticipationLevel.FAMILY_SEGREGATED,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "رحمة الله عليها", 
                    "إنا لله وإنا إليه راجعون",
                    "اللهم اغفر له واكرمه", "اللهم ارزقه الجنة"
                ),
                culturalSensitivities = listOf(
                    "Avoid showing deceased photo in mixed-gender gatherings",
                    "Memorial duration follows traditional 40-day observance",
                    "Community prayers led by male imam for mixed gatherings"
                )
            ),
            
            IslamicRegion.SOUTHEAST_ASIA to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 14, 35, 100, 365, 1000), // Extended observance
                prayerPhotoPolicy = PhotoPolicy.MODERATE,
                communityMemorialSharing = SharingPolicy.COMMUNITY_WIDE,
                womenParticipationLevel = ParticipationLevel.EQUAL_PARTICIPATION,
                acceptableMemorialPhrases = listOf(
                    "Alfatihah", "Selamat jalan", "Husnul khatimah",
                    "رحمة الله عليه", "إنا لله وإنا إليه راجعون"
                ),
                culturalSensitivities = listOf(
                    "Memorial photos commonly displayed and shared",
                    "Extended 1000-day memorial observances accepted",
                    "Women actively participate in community memorial prayers",
                    "Local language memorial messages widely accepted"
                )
            ),
            
            IslamicRegion.SOUTH_ASIA to RegionalCustoms(
                memorialDurationDays = listOf(3, 10, 40, 365), // Traditional subcontinental practices
                prayerPhotoPolicy = PhotoPolicy.MODERATE,
                communityMemorialSharing = SharingPolicy.EXTENDED_FAMILY,
                womenParticipationLevel = ParticipationLevel.FAMILY_SEGREGATED,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "آمين", "دعا کریں", "مغفرت کی دعا",
                    "अल्लाह मग़फ़िरत करे", "ফাতিহা পড়ুন"
                ),
                culturalSensitivities = listOf(
                    "Memorial prayers often include Urdu/Hindi terminology",
                    "Extended family participation expected in memorial prayers",
                    "Regional language integration in Islamic practices accepted",
                    "Memorial food distribution (sadqa) is important custom"
                )
            ),
            
            IslamicRegion.NORTH_AFRICA to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 40), // Traditional Maghreb practices
                prayerPhotoPolicy = PhotoPolicy.MODERATE,
                communityMemorialSharing = SharingPolicy.COMMUNITY_WIDE,
                womenParticipationLevel = ParticipationLevel.EQUAL_PARTICIPATION,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "الله يرحمو", "ربي يكرمو",
                    "إنا لله وإنا إليه راجعون", "الله يجعلها في ميزان حسناته"
                ),
                culturalSensitivities = listOf(
                    "Maghrebi Arabic dialect variations accepted",
                    "Community-wide memorial participation common",
                    "Memorial gatherings often include traditional foods",
                    "French/Arabic bilingual memorial messages accepted"
                )
            ),
            
            IslamicRegion.SUB_SAHARAN_AFRICA to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 40, 365), // African Islamic traditions
                prayerPhotoPolicy = PhotoPolicy.LIBERAL,
                communityMemorialSharing = SharingPolicy.COMMUNITY_WIDE,
                womenParticipationLevel = ParticipationLevel.EQUAL_PARTICIPATION,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "Allah ya ji kansa", "Inna lillahi",
                    "May Allah grant him Jannah", "Allah ya bar mu rahama"
                ),
                culturalSensitivities = listOf(
                    "Local language memorial prayers widely accepted",
                    "Community drumming and singing may accompany prayers",
                    "Memorial photos and displays commonly shared",
                    "Interfaith family considerations may apply"
                )
            ),
            
            IslamicRegion.EUROPE to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 40), // Diaspora practices
                prayerPhotoPolicy = PhotoPolicy.LIBERAL,
                communityMemorialSharing = SharingPolicy.COMMUNITY_WIDE,
                womenParticipationLevel = ParticipationLevel.EQUAL_PARTICIPATION,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "May Allah have mercy", "Qu'Allah lui pardonne",
                    "Allah hab rachmie", "إنا لله وإنا إليه راجعون"
                ),
                culturalSensitivities = listOf(
                    "Multilingual memorial messages needed",
                    "Integration with non-Muslim family members",
                    "Digital memorial platforms widely accepted",
                    "Interfaith sensitivity required for mixed families"
                )
            ),
            
            IslamicRegion.NORTH_AMERICA to RegionalCustoms(
                memorialDurationDays = listOf(3, 7, 40, 365), // American Muslim practices
                prayerPhotoPolicy = PhotoPolicy.LIBERAL,
                communityMemorialSharing = SharingPolicy.PUBLIC_WITH_PRIVACY,
                womenParticipationLevel = ParticipationLevel.EQUAL_PARTICIPATION,
                acceptableMemorialPhrases = listOf(
                    "رحمة الله عليه", "May Allah grant him paradise", "Inna lillahi wa inna ilayhi raji'un",
                    "Please remember him in your prayers", "May he rest in Jannah"
                ),
                culturalSensitivities = listOf(
                    "English-Arabic bilingual content expected",
                    "Social media memorial sharing widely practiced",
                    "Interfaith and multicultural considerations",
                    "Gender-equal participation in community prayers"
                )
            )
        )
        
        // Gender interaction guidelines by region
        val GENDER_INTERACTION_GUIDELINES = mapOf(
            IslamicRegion.MIDDLE_EAST to GenderGuidelines(
                mixedGenderMemorials = false,
                maleLeadershipRequired = true,
                separateSpaces = true,
                coveringRequirements = CoveringLevel.CONSERVATIVE
            ),
            IslamicRegion.SOUTHEAST_ASIA to GenderGuidelines(
                mixedGenderMemorials = true,
                maleLeadershipRequired = false,
                separateSpaces = false,
                coveringRequirements = CoveringLevel.MODERATE
            ),
            IslamicRegion.EUROPE to GenderGuidelines(
                mixedGenderMemorials = true,
                maleLeadershipRequired = false,
                separateSpaces = false,
                coveringRequirements = CoveringLevel.LIBERAL
            )
        )
    }

    /**
     * Validate cultural customs for memorial content
     */
    fun validateMemorialCustoms(
        content: MemorialCustomsContent,
        targetRegions: List<IslamicRegion>
    ): CulturalValidationResult {
        val violations = mutableListOf<CulturalViolation>()
        val warnings = mutableListOf<CulturalWarning>()
        val recommendations = mutableListOf<CulturalRecommendation>()

        targetRegions.forEach { region ->
            val customs = REGIONAL_CUSTOMS[region] ?: return@forEach
            
            // Validate memorial duration
            validateMemorialDuration(content.memorialDurationDays, customs, region, violations, warnings)
            
            // Validate photo usage
            validatePhotoUsage(content.hasPhoto, content.isPubliclyShared, customs, region, violations, warnings)
            
            // Validate community sharing
            validateCommunitySharing(content.sharingLevel, customs, region, violations, warnings)
            
            // Validate memorial phrases
            validateMemorialPhrases(content.memorialPhrases, customs, region, warnings, recommendations)
            
            // Validate gender considerations
            validateGenderConsiderations(content.genderMix, region, violations, warnings)
        }

        return CulturalValidationResult(
            isValid = violations.isEmpty(),
            violations = violations,
            warnings = warnings,
            recommendations = recommendations,
            culturalScore = calculateCulturalScore(violations, warnings, recommendations),
            approvedRegions = targetRegions.filter { region -> 
                !violations.any { it.affectedRegion == region }
            }
        )
    }

    /**
     * Validate prayer traditions across cultures
     */
    fun validatePrayerTraditions(
        prayerContent: PrayerTraditionContent,
        targetRegions: List<IslamicRegion>
    ): CulturalValidationResult {
        val violations = mutableListOf<CulturalViolation>()
        val warnings = mutableListOf<CulturalWarning>()
        val recommendations = mutableListOf<CulturalRecommendation>()

        targetRegions.forEach { region ->
            val customs = REGIONAL_CUSTOMS[region] ?: return@forEach
            
            // Validate prayer language usage
            validatePrayerLanguage(prayerContent.languages, region, warnings, recommendations)
            
            // Validate community prayer setup
            validateCommunityPrayerSetup(prayerContent.communitySetup, region, violations, warnings)
            
            // Validate prayer timing customs
            validatePrayerTiming(prayerContent.timingCustoms, region, warnings, recommendations)
            
            // Validate audio/visual elements
            validateAudioVisualElements(prayerContent.audioVisual, region, warnings, recommendations)
        }

        return CulturalValidationResult(
            isValid = violations.isEmpty(),
            violations = violations,
            warnings = warnings,
            recommendations = recommendations,
            culturalScore = calculateCulturalScore(violations, warnings, recommendations),
            approvedRegions = targetRegions.filter { region -> 
                !violations.any { it.affectedRegion == region }
            }
        )
    }

    /**
     * Get cultural recommendations for region
     */
    fun getCulturalRecommendations(
        region: IslamicRegion,
        contentType: CulturalContentType
    ): RegionalRecommendations {
        val customs = REGIONAL_CUSTOMS[region] ?: return RegionalRecommendations.empty()
        
        return when (contentType) {
            CulturalContentType.MEMORIAL -> RegionalRecommendations(
                preferredLanguages = getPreferredLanguages(region),
                acceptableMemorialDurations = customs.memorialDurationDays,
                recommendedPhrases = customs.acceptableMemorialPhrases.take(3),
                culturalNotes = customs.culturalSensitivities,
                genderGuidelines = GENDER_INTERACTION_GUIDELINES[region]?.let { 
                    "Mixed gender participation: ${if (it.mixedGenderMemorials) "Accepted" else "Not recommended"}"
                },
                photoGuidelines = when (customs.prayerPhotoPolicy) {
                    PhotoPolicy.LIBERAL -> "Memorial photos widely accepted and encouraged"
                    PhotoPolicy.MODERATE -> "Memorial photos accepted with family approval"
                    PhotoPolicy.RESTRICTED -> "Memorial photos should be used cautiously"
                    PhotoPolicy.FORBIDDEN -> "Memorial photos not recommended"
                }
            )
            CulturalContentType.PRAYER -> RegionalRecommendations(
                preferredLanguages = getPreferredLanguages(region),
                recommendedPhrases = customs.acceptableMemorialPhrases,
                culturalNotes = customs.culturalSensitivities,
                genderGuidelines = GENDER_INTERACTION_GUIDELINES[region]?.let { guidelines ->
                    buildString {
                        append("Gender considerations: ")
                        if (guidelines.mixedGenderMemorials) append("Mixed participation accepted. ")
                        if (guidelines.maleLeadershipRequired) append("Male leadership preferred. ")
                        if (guidelines.separateSpaces) append("Separate spaces recommended.")
                    }
                }
            )
        }
    }

    /**
     * Validate memorial duration against regional customs
     */
    private fun validateMemorialDuration(
        durationDays: Int?,
        customs: RegionalCustoms,
        region: IslamicRegion,
        violations: MutableList<CulturalViolation>,
        warnings: MutableList<CulturalWarning>
    ) {
        durationDays?.let { duration ->
            if (duration !in customs.memorialDurationDays) {
                val acceptableDurations = customs.memorialDurationDays.joinToString(", ")
                warnings.add(
                    CulturalWarning(
                        region = region,
                        category = CulturalCategory.MEMORIAL_DURATION,
                        message = "Memorial duration of $duration days is not traditional in $region. Common durations: $acceptableDurations days",
                        severity = WarningSeverity.MODERATE
                    )
                )
            }
        }
    }

    /**
     * Validate photo usage policies
     */
    private fun validatePhotoUsage(
        hasPhoto: Boolean,
        isPubliclyShared: Boolean,
        customs: RegionalCustoms,
        region: IslamicRegion,
        violations: MutableList<CulturalViolation>,
        warnings: MutableList<CulturalWarning>
    ) {
        if (hasPhoto) {
            when (customs.prayerPhotoPolicy) {
                PhotoPolicy.FORBIDDEN -> {
                    violations.add(
                        CulturalViolation(
                            affectedRegion = region,
                            category = CulturalCategory.PHOTO_USAGE,
                            message = "Memorial photos are culturally inappropriate in $region",
                            severity = ViolationSeverity.HIGH
                        )
                    )
                }
                PhotoPolicy.RESTRICTED -> {
                    if (isPubliclyShared) {
                        warnings.add(
                            CulturalWarning(
                                region = region,
                                category = CulturalCategory.PHOTO_USAGE,
                                message = "Public sharing of memorial photos may be culturally sensitive in $region",
                                severity = WarningSeverity.HIGH
                            )
                        )
                    }
                }
                PhotoPolicy.MODERATE, PhotoPolicy.LIBERAL -> {
                    // Photos acceptable
                }
            }
        }
    }

    /**
     * Validate community sharing practices
     */
    private fun validateCommunitySharing(
        sharingLevel: SharingLevel,
        customs: RegionalCustoms,
        region: IslamicRegion,
        violations: MutableList<CulturalViolation>,
        warnings: MutableList<CulturalWarning>
    ) {
        val isAppropriate = when (customs.communityMemorialSharing) {
            SharingPolicy.FAMILY_ONLY -> sharingLevel in listOf(SharingLevel.PRIVATE, SharingLevel.FAMILY)
            SharingPolicy.EXTENDED_FAMILY -> sharingLevel in listOf(SharingLevel.PRIVATE, SharingLevel.FAMILY, SharingLevel.EXTENDED_FAMILY)
            SharingPolicy.COMMUNITY_WIDE -> sharingLevel != SharingLevel.PUBLIC
            SharingPolicy.PUBLIC_WITH_PRIVACY -> true // All levels acceptable
        }

        if (!isAppropriate) {
            warnings.add(
                CulturalWarning(
                    region = region,
                    category = CulturalCategory.SHARING_LEVEL,
                    message = "Sharing level '${sharingLevel.name}' may exceed cultural norms in $region where '${customs.communityMemorialSharing.name}' is preferred",
                    severity = WarningSeverity.MODERATE
                )
            )
        }
    }

    /**
     * Validate memorial phrases for cultural appropriateness
     */
    private fun validateMemorialPhrases(
        phrases: List<String>,
        customs: RegionalCustoms,
        region: IslamicRegion,
        warnings: MutableList<CulturalWarning>,
        recommendations: MutableList<CulturalRecommendation>
    ) {
        phrases.forEach { phrase ->
            val isRecognized = customs.acceptableMemorialPhrases.any { acceptablePhrase ->
                phrase.contains(acceptablePhrase) || acceptablePhrase.contains(phrase)
            }
            
            if (!isRecognized && phrase.length > 5) {
                recommendations.add(
                    CulturalRecommendation(
                        region = region,
                        category = CulturalCategory.MEMORIAL_PHRASES,
                        suggestion = "Consider using regionally appropriate phrases like: ${customs.acceptableMemorialPhrases.take(2).joinToString(", ")}",
                        priority = RecommendationPriority.MEDIUM
                    )
                )
            }
        }
    }

    /**
     * Validate gender considerations
     */
    private fun validateGenderConsiderations(
        genderMix: GenderMixLevel,
        region: IslamicRegion,
        violations: MutableList<CulturalViolation>,
        warnings: MutableList<CulturalWarning>
    ) {
        val guidelines = GENDER_INTERACTION_GUIDELINES[region] ?: return
        
        if (genderMix == GenderMixLevel.MIXED && !guidelines.mixedGenderMemorials) {
            warnings.add(
                CulturalWarning(
                    region = region,
                    category = CulturalCategory.GENDER_INTERACTION,
                    message = "Mixed-gender memorial gatherings may not align with cultural preferences in $region",
                    severity = WarningSeverity.HIGH
                )
            )
        }
    }

    /**
     * Validate prayer language usage
     */
    private fun validatePrayerLanguage(
        languages: List<String>,
        region: IslamicRegion,
        warnings: MutableList<CulturalWarning>,
        recommendations: MutableList<CulturalRecommendation>
    ) {
        val preferredLanguages = getPreferredLanguages(region)
        val hasPreferredLanguage = languages.any { it in preferredLanguages }
        
        if (!hasPreferredLanguage) {
            recommendations.add(
                CulturalRecommendation(
                    region = region,
                    category = CulturalCategory.LANGUAGE_USAGE,
                    suggestion = "Consider including ${preferredLanguages.first()} translation for better regional accessibility",
                    priority = RecommendationPriority.MEDIUM
                )
            )
        }
    }

    /**
     * Validate community prayer setup
     */
    private fun validateCommunityPrayerSetup(
        setup: CommunityPrayerSetup,
        region: IslamicRegion,
        violations: MutableList<CulturalViolation>,
        warnings: MutableList<CulturalWarning>
    ) {
        val guidelines = GENDER_INTERACTION_GUIDELINES[region] ?: return
        
        if (setup.isMixedGender && guidelines.separateSpaces) {
            warnings.add(
                CulturalWarning(
                    region = region,
                    category = CulturalCategory.COMMUNITY_PRAYER,
                    message = "Consider separate prayer spaces for different genders as per $region customs",
                    severity = WarningSeverity.MODERATE
                )
            )
        }
        
        if (setup.femaleLeadership && guidelines.maleLeadershipRequired) {
            warnings.add(
                CulturalWarning(
                    region = region,
                    category = CulturalCategory.PRAYER_LEADERSHIP,
                    message = "Female prayer leadership for mixed groups may not align with $region customs",
                    severity = WarningSeverity.HIGH
                )
            )
        }
    }

    /**
     * Additional validation methods for prayer timing, audio/visual elements
     */
    private fun validatePrayerTiming(
        timingCustoms: PrayerTimingCustoms,
        region: IslamicRegion,
        warnings: MutableList<CulturalWarning>,
        recommendations: MutableList<CulturalRecommendation>
    ) {
        // Implementation for prayer timing validation
        if (timingCustoms.followsLocalTiming && region in listOf(IslamicRegion.NORTH_AMERICA, IslamicRegion.EUROPE)) {
            recommendations.add(
                CulturalRecommendation(
                    region = region,
                    category = CulturalCategory.PRAYER_TIMING,
                    suggestion = "Consider mosque consultation for accurate local prayer times",
                    priority = RecommendationPriority.LOW
                )
            )
        }
    }

    private fun validateAudioVisualElements(
        audioVisual: AudioVisualElements,
        region: IslamicRegion,
        warnings: MutableList<CulturalWarning>,
        recommendations: MutableList<CulturalRecommendation>
    ) {
        // Implementation for audio/visual validation
        if (audioVisual.hasMusic && region == IslamicRegion.MIDDLE_EAST) {
            warnings.add(
                CulturalWarning(
                    region = region,
                    category = CulturalCategory.AUDIO_VISUAL,
                    message = "Musical elements in prayer context may be culturally sensitive in $region",
                    severity = WarningSeverity.MODERATE
                )
            )
        }
    }

    /**
     * Calculate cultural appropriateness score
     */
    private fun calculateCulturalScore(
        violations: List<CulturalViolation>,
        warnings: List<CulturalWarning>,
        recommendations: List<CulturalRecommendation>
    ): Double {
        var score = 100.0
        
        violations.forEach { violation ->
            score -= when (violation.severity) {
                ViolationSeverity.CRITICAL -> 25.0
                ViolationSeverity.HIGH -> 15.0
                ViolationSeverity.MODERATE -> 10.0
                ViolationSeverity.LOW -> 5.0
            }
        }
        
        warnings.forEach { warning ->
            score -= when (warning.severity) {
                WarningSeverity.HIGH -> 8.0
                WarningSeverity.MODERATE -> 5.0
                WarningSeverity.LOW -> 2.0
            }
        }
        
        return score.coerceAtLeast(0.0)
    }

    /**
     * Get preferred languages for region
     */
    private fun getPreferredLanguages(region: IslamicRegion): List<String> {
        return when (region) {
            IslamicRegion.MIDDLE_EAST -> listOf("Arabic", "Persian", "Turkish")
            IslamicRegion.NORTH_AFRICA -> listOf("Arabic", "French", "Berber")
            IslamicRegion.SOUTH_ASIA -> listOf("Urdu", "Hindi", "Bengali", "Arabic")
            IslamicRegion.SOUTHEAST_ASIA -> listOf("Malay", "Indonesian", "Arabic")
            IslamicRegion.SUB_SAHARAN_AFRICA -> listOf("Arabic", "Hausa", "Swahili", "English")
            IslamicRegion.EUROPE -> listOf("Arabic", "Turkish", "Albanian", "Bosnian")
            IslamicRegion.NORTH_AMERICA -> listOf("English", "Arabic", "Urdu")
            IslamicRegion.CENTRAL_ASIA -> listOf("Arabic", "Uzbek", "Kazakh", "Tajik")
            else -> listOf("Arabic", "English")
        }
    }
}

/**
 * Supporting data classes and enums
 */
data class RegionalCustoms(
    val memorialDurationDays: List<Int>,
    val prayerPhotoPolicy: PhotoPolicy,
    val communityMemorialSharing: SharingPolicy,
    val womenParticipationLevel: ParticipationLevel,
    val acceptableMemorialPhrases: List<String>,
    val culturalSensitivities: List<String>
)

data class GenderGuidelines(
    val mixedGenderMemorials: Boolean,
    val maleLeadershipRequired: Boolean,
    val separateSpaces: Boolean,
    val coveringRequirements: CoveringLevel
)

data class MemorialCustomsContent(
    val memorialDurationDays: Int?,
    val hasPhoto: Boolean,
    val isPubliclyShared: Boolean,
    val sharingLevel: SharingLevel,
    val memorialPhrases: List<String>,
    val genderMix: GenderMixLevel
)

data class PrayerTraditionContent(
    val languages: List<String>,
    val communitySetup: CommunityPrayerSetup,
    val timingCustoms: PrayerTimingCustoms,
    val audioVisual: AudioVisualElements
)

data class CommunityPrayerSetup(
    val isMixedGender: Boolean,
    val femaleLeadership: Boolean,
    val separateSpaces: Boolean
)

data class PrayerTimingCustoms(
    val followsLocalTiming: Boolean,
    val adjustsForRegion: Boolean
)

data class AudioVisualElements(
    val hasMusic: Boolean,
    val hasChanting: Boolean,
    val hasVisualEffects: Boolean
)

data class RegionalRecommendations(
    val preferredLanguages: List<String> = emptyList(),
    val acceptableMemorialDurations: List<Int> = emptyList(),
    val recommendedPhrases: List<String> = emptyList(),
    val culturalNotes: List<String> = emptyList(),
    val genderGuidelines: String? = null,
    val photoGuidelines: String? = null
) {
    companion object {
        fun empty() = RegionalRecommendations()
    }
}

data class CulturalValidationResult(
    val isValid: Boolean,
    val violations: List<CulturalViolation>,
    val warnings: List<CulturalWarning>,
    val recommendations: List<CulturalRecommendation>,
    val culturalScore: Double,
    val approvedRegions: List<IslamicRegion>
)

data class CulturalViolation(
    val affectedRegion: IslamicRegion,
    val category: CulturalCategory,
    val message: String,
    val severity: ViolationSeverity
)

data class CulturalWarning(
    val region: IslamicRegion,
    val category: CulturalCategory,
    val message: String,
    val severity: WarningSeverity
)

data class CulturalRecommendation(
    val region: IslamicRegion,
    val category: CulturalCategory,
    val suggestion: String,
    val priority: RecommendationPriority
)

// Essential enums for CulturalCustomsValidator
enum class IslamicRegion {
    MIDDLE_EAST,
    NORTH_AFRICA,
    SOUTHEAST_ASIA,
    SOUTH_ASIA,
    CENTRAL_ASIA,
    EUROPE,
    NORTH_AMERICA,
    SUB_SAHARAN_AFRICA,
    EAST_ASIA,
    OCEANIA,
    LATIN_AMERICA
}

enum class CulturalCategory {
    MEMORIAL_PRACTICES,
    PRAYER_CUSTOMS,
    FAMILY_VALUES,
    COMMUNITY_INTERACTION,
    GENDER_GUIDELINES,
    RELIGIOUS_OBSERVANCE
}

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class CoveringLevel {
    CONSERVATIVE,
    MODERATE,
    LIBERAL
}

enum class SharingLevel {
    PRIVATE,
    FAMILY,
    EXTENDED_FAMILY,
    COMMUNITY,
    PUBLIC
}

enum class GenderMixLevel {
    MALE_ONLY,
    FEMALE_ONLY,
    SEGREGATED,
    MIXED
}

enum class CulturalContentType {
    MEMORIAL,
    PRAYER
}

enum class ViolationSeverity {
    MINOR,
    MODERATE,
    MAJOR,
    CRITICAL
}

enum class WarningCategory {
    CULTURAL_SENSITIVITY,
    RELIGIOUS_APPROPRIATENESS,
    COMMUNITY_GUIDELINES,
    FAMILY_VALUES
}

enum class WarningSeverity {
    LOW,
    MEDIUM,
    HIGH
}

enum class PhotoPolicy {
    PROHIBITED,
    RESTRICTED,
    MODERATE,
    ALLOWED
}

enum class SharingPolicy {
    PRIVATE_ONLY,
    FAMILY_ONLY,
    COMMUNITY_WIDE,
    PUBLIC
}

enum class ParticipationLevel {
    INDIVIDUAL,
    FAMILY,
    COMMUNITY,
    REGIONAL
}

