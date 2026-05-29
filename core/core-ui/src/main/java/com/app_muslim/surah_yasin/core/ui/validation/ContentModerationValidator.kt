package com.app_muslim.surah_yasin.core.ui.validation

// Import removed to fix compilation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Content Moderation Validator for Islamic Platform
 * 
 * Comprehensive content moderation system ensuring no inappropriate content
 * or interactions that violate Islamic principles, community standards,
 * or platform guidelines. Provides multi-layered validation including:
 * 
 * - Islamic content appropriateness
 * - Community interaction guidelines
 * - Cultural sensitivity enforcement
 * - Anti-abuse and harassment protection
 * - Memorial content respectfulness
 * - Language and behavior monitoring
 */
@Singleton
class ContentModerationValidator @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        // Prohibited content categories with Islamic context
        val PROHIBITED_CONTENT = mapOf(
            ProhibitionCategory.RELIGIOUS_VIOLATION to ProhibitedContentRule(
                keywords = listOf(
                    // Shirk (associating partners with Allah)
                    "Allah has partners", "multiple gods", "trinity", "son of god",
                    // Blasphemy
                    "Allah is not", "Prophet is not", "Islam is false",
                    // Sectarian hatred
                    "kafir", "infidel dogs", "destroy mosque", "burn quran"
                ),
                severity = ModerationSeverity.CRITICAL,
                action = ModerationAction.IMMEDIATE_REMOVAL,
                description = "Content that violates core Islamic beliefs or promotes blasphemy"
            ),
            
            ProhibitionCategory.INAPPROPRIATE_MEMORIAL to ProhibitedContentRule(
                keywords = listOf(
                    "curse", "damn", "hell bound", "deserved to die",
                    "glad they're dead", "burning in hell", "evil person",
                    "hated them", "good riddance"
                ),
                severity = ModerationSeverity.HIGH,
                action = ModerationAction.REMOVE_AND_WARNING,
                description = "Memorial content that lacks respect for the deceased"
            ),
            
            ProhibitionCategory.HATE_SPEECH to ProhibitedContentRule(
                keywords = listOf(
                    // Racial/ethnic hatred
                    "dirty arab", "terrorist muslim", "sand people",
                    // Gender-based hatred  
                    "women are inferior", "stupid females", "weak men",
                    // Sectarian hatred
                    "sunni dogs", "shia heretics", "sufi deviants"
                ),
                severity = ModerationSeverity.CRITICAL,
                action = ModerationAction.IMMEDIATE_BAN,
                description = "Hate speech targeting individuals or groups"
            ),
            
            ProhibitionCategory.HARASSMENT to ProhibitedContentRule(
                keywords = listOf(
                    "kill yourself", "you deserve pain", "i hate you",
                    "you're worthless", "go die", "nobody likes you",
                    "ugly", "disgusting", "piece of trash"
                ),
                severity = ModerationSeverity.HIGH,
                action = ModerationAction.TEMPORARY_SUSPENSION,
                description = "Personal harassment or bullying behavior"
            ),
            
            ProhibitionCategory.INAPPROPRIATE_SEXUAL to ProhibitedContentRule(
                keywords = listOf(
                    "sexual", "nude", "porn", "erotic", "intimate",
                    "bedroom", "sexual pleasure", "arousing",
                    "seductive", "provocative"
                ),
                severity = ModerationSeverity.HIGH,
                action = ModerationAction.REMOVE_AND_WARNING,
                description = "Sexually inappropriate content in Islamic context"
            ),
            
            ProhibitionCategory.VIOLENCE_INCITEMENT to ProhibitedContentRule(
                keywords = listOf(
                    "kill all", "destroy them", "violence is answer",
                    "hurt them", "make them suffer", "revenge",
                    "bomb", "terrorist attack", "jihad against"
                ),
                severity = ModerationSeverity.CRITICAL,
                action = ModerationAction.IMMEDIATE_REPORT,
                description = "Content that incites violence or promotes terrorism"
            ),
            
            ProhibitionCategory.SPAM_COMMERCIAL to ProhibitedContentRule(
                keywords = listOf(
                    "buy now", "click here", "special offer", "discount",
                    "call this number", "visit website", "make money",
                    "get rich quick", "amazing deal"
                ),
                severity = ModerationSeverity.MODERATE,
                action = ModerationAction.CONTENT_REMOVAL,
                description = "Commercial spam or promotional content"
            ),
            
            ProhibitionCategory.MISINFORMATION to ProhibitedContentRule(
                keywords = listOf(
                    "fake hadith", "prophet never said", "quran is wrong",
                    "scholars lie", "islam teaches hatred", "false prophet",
                    "covid hoax", "vaccine contains", "conspiracy"
                ),
                severity = ModerationSeverity.MODERATE,
                action = ModerationAction.FACT_CHECK_FLAG,
                description = "Potentially false or misleading information"
            )
        )
        
        // Positive Islamic content indicators (boost confidence)
        val POSITIVE_INDICATORS = listOf(
            // Islamic greetings and expressions
            "assalamu alaikum", "wa alaikum assalam", "barakallahu feeki",
            "jazakallahu khairan", "may allah bless", "insha allah",
            "masha allah", "subhanallah", "alhamdulillah",
            // Memorial blessing phrases
            "may allah have mercy", "rest in jannah", "allah yarhamhu",
            "may he find peace", "allah forgive him", "grant her paradise",
            // Community support
            "pray for family", "support each other", "islamic brotherhood",
            "help the community", "charity", "sadaqah"
        )
        
        // Cultural sensitivity indicators by region
        val CULTURAL_SENSITIVITIES = mapOf(
            IslamicRegion.MIDDLE_EAST to CulturalSensitivity(
                avoidTerms = listOf("arab spring", "tribal", "backward"),
                preferredTerms = listOf("traditional", "authentic", "classical"),
                contextualWarnings = listOf("Avoid political references", "Respect traditional customs")
            ),
            IslamicRegion.SOUTHEAST_ASIA to CulturalSensitivity(
                avoidTerms = listOf("primitive", "underdeveloped", "exotic"),
                preferredTerms = listOf("diverse", "multicultural", "harmonious"),
                contextualWarnings = listOf("Respect local Islamic adaptations", "Honor cultural diversity")
            ),
            IslamicRegion.SOUTH_ASIA to CulturalSensitivity(
                avoidTerms = listOf("caste system", "poverty-stricken", "illiterate"),
                preferredTerms = listOf("rich heritage", "scholarly tradition", "diverse"),
                contextualWarnings = listOf("Avoid generalizations", "Respect linguistic diversity")
            )
        )
    }

    /**
     * Comprehensive content moderation check
     */
    suspend fun moderateContent(
        content: String,
        contentType: ContentType,
        authorId: String,
        targetRegions: List<IslamicRegion> = emptyList()
    ): ContentModerationResult {
        val violations = mutableListOf<ContentViolation>()
        val warnings = mutableListOf<ContentWarning>()
        val flags = mutableListOf<ContentFlag>()

        // 1. Prohibited content detection
        detectProhibitedContent(content, contentType, violations)

        // 2. Islamic appropriateness check
        validateIslamicAppropriateness(content, contentType, violations, warnings)

        // 3. Cultural sensitivity validation
        validateCulturalSensitivity(content, targetRegions, warnings, flags)

        // 4. Contextual appropriateness (memorial vs general)
        validateContextualAppropriateness(content, contentType, warnings)

        // 5. Author behavior pattern analysis
        val authorPattern = analyzeAuthorBehavior(authorId)
        if (authorPattern.riskLevel == RiskLevel.HIGH) {
            flags.add(
                ContentFlag(
                    category = FlagCategory.AUTHOR_RISK,
                    message = "Author has history of policy violations",
                    confidence = 0.8
                )
            )
        }

        // 6. Positive content recognition
        val positiveScore = calculatePositiveContentScore(content)

        return ContentModerationResult(
            isApproved = violations.isEmpty() && 
                        violations.none { it.severity == ModerationSeverity.CRITICAL },
            autoApproved = violations.isEmpty() && warnings.isEmpty() && positiveScore > 0.7,
            requiresHumanReview = violations.any { it.severity == ModerationSeverity.MODERATE } ||
                                 warnings.size > 2 || flags.isNotEmpty(),
            violations = violations,
            warnings = warnings,
            flags = flags,
            positiveScore = positiveScore,
            recommendedActions = generateRecommendedActions(violations, warnings),
            moderationTags = generateModerationTags(content, contentType, violations, warnings)
        )
    }

    /**
     * Real-time content filtering for live interactions
     */
    fun filterContentRealtime(
        content: String,
        contentType: ContentType
    ): RealtimeFilterResult {
        val criticalViolations = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        // Check for critical violations only (fast check)
        PROHIBITED_CONTENT.forEach { (category, rule) ->
            if (rule.severity == ModerationSeverity.CRITICAL) {
                rule.keywords.forEach { keyword ->
                    if (content.contains(keyword, ignoreCase = true)) {
                        criticalViolations.add("Contains prohibited content: $category")
                    }
                }
            }
        }

        // Generate suggestions for improvement
        if (criticalViolations.isEmpty()) {
            generateContentSuggestions(content, contentType, suggestions)
        }

        return RealtimeFilterResult(
            isBlocked = criticalViolations.isNotEmpty(),
            violations = criticalViolations,
            suggestions = suggestions,
            filteredContent = if (criticalViolations.isEmpty()) content else "[Content Blocked]"
        )
    }

    /**
     * Memorial-specific content validation
     */
    suspend fun validateMemorialContent(
        memorialText: String,
        deceasedName: String,
        familyMembers: List<String>,
        authorId: String
    ): MemorialValidationResult {
        val issues = mutableListOf<MemorialIssue>()
        val recommendations = mutableListOf<String>()

        // 1. Respectful language check
        validateMemorialRespect(memorialText, issues)

        // 2. Islamic memorial phrases validation
        validateIslamicMemorialPhrases(memorialText, recommendations)

        // 3. Family appropriateness check
        validateFamilyAppropriateness(memorialText, familyMembers, issues)

        // 4. Privacy and dignity check
        validatePrivacyAndDignity(memorialText, issues)

        return MemorialValidationResult(
            isAppropriate = issues.none { it.severity == IssueSeverity.CRITICAL },
            issues = issues,
            recommendations = recommendations,
            suggestedImprovements = generateMemorialImprovements(memorialText, issues),
            appropriatenessScore = calculateMemorialAppropriatenessScore(memorialText, issues)
        )
    }

    /**
     * Community interaction guidelines validation
     */
    fun validateCommunityInteraction(
        interaction: CommunityInteraction
    ): InteractionValidationResult {
        val violations = mutableListOf<InteractionViolation>()
        val guidelines = mutableListOf<String>()

        // 1. Gender-appropriate interaction
        validateGenderAppropriateInteraction(interaction, violations, guidelines)

        // 2. Respectful communication
        validateRespectfulCommunication(interaction.message, violations)

        // 3. Islamic etiquette compliance
        validateIslamicEtiquette(interaction, violations, guidelines)

        // 4. Community harmony promotion
        validateCommunityHarmony(interaction.message, guidelines)

        return InteractionValidationResult(
            isAppropriate = violations.none { it.severity == ViolationSeverity.HIGH },
            violations = violations,
            guidelines = guidelines,
            improvementSuggestions = generateInteractionImprovements(interaction, violations)
        )
    }

    /**
     * Detect prohibited content using pattern matching
     */
    private fun detectProhibitedContent(
        content: String,
        contentType: ContentType,
        violations: MutableList<ContentViolation>
    ) {
        val lowercaseContent = content.lowercase()

        PROHIBITED_CONTENT.forEach { (category, rule) ->
            rule.keywords.forEach { keyword ->
                if (lowercaseContent.contains(keyword)) {
                    violations.add(
                        ContentViolation(
                            category = category,
                            message = "Content contains prohibited material: ${rule.description}",
                            severity = rule.severity,
                            action = rule.action,
                            matchedKeyword = keyword,
                            position = content.indexOf(keyword, ignoreCase = true)
                        )
                    )
                }
            }
        }
    }

    /**
     * Validate Islamic appropriateness of content
     */
    private fun validateIslamicAppropriateness(
        content: String,
        contentType: ContentType,
        violations: MutableList<ContentViolation>,
        warnings: MutableList<ContentWarning>
    ) {
        when (contentType) {
            ContentType.MEMORIAL_MESSAGE -> {
                validateMemorialIslamicContent(content, violations, warnings)
            }
            ContentType.PRAYER_TEXT -> {
                validatePrayerIslamicContent(content, violations, warnings)
            }
            ContentType.COMMUNITY_POST -> {
                validateCommunityIslamicContent(content, warnings)
            }
            ContentType.GENERAL -> {
                validateGeneralIslamicContent(content, warnings)
            }
        }
    }

    /**
     * Validate memorial Islamic content
     */
    private fun validateMemorialIslamicContent(
        content: String,
        violations: MutableList<ContentViolation>,
        warnings: MutableList<ContentWarning>
    ) {
        // Check for appropriate Islamic memorial expressions
        val islamicMemorialPhrases = listOf(
            "رحمة الله عليه", "رحمة الله عليها", 
            "إنا لله وإنا إليه راجعون",
            "may allah have mercy", "rest in jannah"
        )

        val hasIslamicElements = islamicMemorialPhrases.any { phrase ->
            content.contains(phrase, ignoreCase = true)
        }

        if (!hasIslamicElements && content.length > 20) {
            warnings.add(
                ContentWarning(
                    category = WarningCategory.ISLAMIC_CONTENT,
                    message = "Memorial message could include Islamic remembrance phrases",
                    suggestion = "Consider adding 'رحمة الله عليه' or 'may Allah have mercy on him'"
                )
            )
        }

        // Check for non-Islamic memorial expressions that might be inappropriate
        val nonIslamicMemorialTerms = listOf(
            "rest in peace", "gone to heaven", "angel now", "spirit lives on"
        )

        nonIslamicMemorialTerms.forEach { term ->
            if (content.contains(term, ignoreCase = true)) {
                warnings.add(
                    ContentWarning(
                        category = WarningCategory.CULTURAL_SENSITIVITY,
                        message = "Consider using Islamic terminology instead of '$term'",
                        suggestion = "Islamic alternative: 'May Allah grant him Jannah' or 'في رحمة الله'"
                    )
                )
            }
        }
    }

    /**
     * Validate prayer Islamic content
     */
    private fun validatePrayerIslamicContent(
        content: String,
        violations: MutableList<ContentViolation>,
        warnings: MutableList<ContentWarning>
    ) {
        // Ensure prayer content contains appropriate Islamic elements
        val islamicPrayerElements = listOf("الله", "allah", "muhammad", "محمد", "prayer", "dua")
        
        val hasIslamicPrayerElement = islamicPrayerElements.any { element ->
            content.contains(element, ignoreCase = true)
        }

        if (!hasIslamicPrayerElement) {
            violations.add(
                ContentViolation(
                    category = ProhibitionCategory.RELIGIOUS_VIOLATION,
                    message = "Prayer content must contain Islamic elements",
                    severity = ModerationSeverity.MODERATE,
                    action = ModerationAction.REQUIRE_REVISION
                )
            )
        }
    }

    /**
     * Validate community Islamic content
     */
    private fun validateCommunityIslamicContent(
        content: String,
        warnings: MutableList<ContentWarning>
    ) {
        // Check for positive Islamic community expressions
        val positiveTerms = POSITIVE_INDICATORS.count { term ->
            content.contains(term, ignoreCase = true)
        }

        if (positiveTerms == 0 && content.length > 50) {
            warnings.add(
                ContentWarning(
                    category = WarningCategory.COMMUNITY_ENGAGEMENT,
                    message = "Consider adding Islamic greetings or positive expressions",
                    suggestion = "Examples: 'Assalamu Alaikum', 'JazakAllahu Khair', 'May Allah bless you'"
                )
            )
        }
    }

    /**
     * Validate general Islamic content appropriateness
     */
    private fun validateGeneralIslamicContent(
        content: String,
        warnings: MutableList<ContentWarning>
    ) {
        // Basic appropriateness check for general content
        val respectfulLanguage = !content.contains(Regex("\\b(stupid|idiot|dumb|crazy)\\b", RegexOption.IGNORE_CASE))
        
        if (!respectfulLanguage) {
            warnings.add(
                ContentWarning(
                    category = WarningCategory.RESPECTFUL_LANGUAGE,
                    message = "Consider using more respectful language in Islamic community",
                    suggestion = "Islamic communities value kind and respectful communication"
                )
            )
        }
    }

    /**
     * Validate cultural sensitivity for target regions
     */
    private fun validateCulturalSensitivity(
        content: String,
        targetRegions: List<IslamicRegion>,
        warnings: MutableList<ContentWarning>,
        flags: MutableList<ContentFlag>
    ) {
        targetRegions.forEach { region ->
            val sensitivity = CULTURAL_SENSITIVITIES[region] ?: return@forEach

            // Check for terms to avoid
            sensitivity.avoidTerms.forEach { term ->
                if (content.contains(term, ignoreCase = true)) {
                    warnings.add(
                        ContentWarning(
                            category = WarningCategory.CULTURAL_SENSITIVITY,
                            message = "Term '$term' may be culturally insensitive in $region",
                            suggestion = "Consider using: ${sensitivity.preferredTerms.random()}"
                        )
                    )
                }
            }

            // Add cultural context flags
            if (content.length > 100) {
                flags.add(
                    ContentFlag(
                        category = FlagCategory.CULTURAL_REVIEW,
                        message = "Long-form content should be reviewed for cultural appropriateness in $region",
                        confidence = 0.6
                    )
                )
            }
        }
    }

    /**
     * Validate contextual appropriateness of content
     */
    private fun validateContextualAppropriateness(
        content: String,
        contentType: ContentType,
        warnings: MutableList<ContentWarning>
    ) {
        when (contentType) {
            ContentType.MEMORIAL_MESSAGE -> {
                if (content.contains(Regex("\\b(happy|exciting|celebration)\\b", RegexOption.IGNORE_CASE))) {
                    warnings.add(
                        ContentWarning(
                            category = WarningCategory.CONTEXTUAL_APPROPRIATENESS,
                            message = "Memorial messages should maintain somber and respectful tone",
                            suggestion = "Focus on remembrance, prayers, and condolences"
                        )
                    )
                }
            }
            ContentType.PRAYER_TEXT -> {
                if (content.contains(Regex("\\b(joke|funny|laugh)\\b", RegexOption.IGNORE_CASE))) {
                    warnings.add(
                        ContentWarning(
                            category = WarningCategory.CONTEXTUAL_APPROPRIATENESS,
                            message = "Prayer context should maintain serious and reverent tone",
                            suggestion = "Focus on worship, supplication, and remembrance of Allah"
                        )
                    )
                }
            }
            else -> { /* Other content types are more flexible */ }
        }
    }

    /**
     * Memorial-specific validation methods
     */
    private fun validateMemorialRespect(
        memorialText: String,
        issues: MutableList<MemorialIssue>
    ) {
        val disrespectfulTerms = listOf(
            "hated", "evil", "bad person", "deserved it", "glad", "finally"
        )

        disrespectfulTerms.forEach { term ->
            if (memorialText.contains(term, ignoreCase = true)) {
                issues.add(
                    MemorialIssue(
                        type = IssueType.DISRESPECTFUL_LANGUAGE,
                        message = "Memorial contains potentially disrespectful language: '$term'",
                        severity = IssueSeverity.HIGH,
                        suggestion = "Focus on positive memories and prayers for the deceased"
                    )
                )
            }
        }
    }

    private fun validateIslamicMemorialPhrases(
        memorialText: String,
        recommendations: MutableList<String>
    ) {
        val commonIslamicPhrases = mapOf(
            "إنا لله وإنا إليه راجعون" to "Inna lillahi wa inna ilayhi raji'un (To Allah we belong and to Him we return)",
            "رحمة الله عليه" to "May Allah have mercy on him",
            "اللهم اغفر له" to "O Allah, forgive him",
            "جعله الله من أهل الجنة" to "May Allah make him among the people of Paradise"
        )

        val hasIslamicPhrase = commonIslamicPhrases.keys.any { phrase ->
            memorialText.contains(phrase) || 
            memorialText.contains(commonIslamicPhrases[phrase]!!, ignoreCase = true)
        }

        if (!hasIslamicPhrase) {
            recommendations.add(
                "Consider including Islamic memorial phrases like:\n" +
                commonIslamicPhrases.entries.take(2).joinToString("\n") { 
                    "• ${it.key} (${it.value})" 
                }
            )
        }
    }

    private fun validateFamilyAppropriateness(
        memorialText: String,
        familyMembers: List<String>,
        issues: MutableList<MemorialIssue>
    ) {
        // Check for overly personal details that might be inappropriate
        val personalTerms = listOf(
            "marriage problems", "financial issues", "addiction", 
            "mental illness", "family secrets", "private matters"
        )

        personalTerms.forEach { term ->
            if (memorialText.contains(term, ignoreCase = true)) {
                issues.add(
                    MemorialIssue(
                        type = IssueType.PRIVACY_VIOLATION,
                        message = "Memorial contains private family information",
                        severity = IssueSeverity.MODERATE,
                        suggestion = "Focus on public positive memories and Islamic remembrance"
                    )
                )
            }
        }
    }

    private fun validatePrivacyAndDignity(
        memorialText: String,
        issues: MutableList<MemorialIssue>
    ) {
        // Check for inappropriate personal details
        val inappropriateDetails = listOf(
            "cause of death", "how he died", "illness details",
            "last words", "suffering", "pain"
        )

        inappropriateDetails.forEach { detail ->
            if (memorialText.contains(detail, ignoreCase = true)) {
                issues.add(
                    MemorialIssue(
                        type = IssueType.DIGNITY_VIOLATION,
                        message = "Consider avoiding specific details about death or illness",
                        severity = IssueSeverity.MODERATE,
                        suggestion = "Focus on life achievements and positive memories"
                    )
                )
            }
        }
    }

    /**
     * Community interaction validation methods
     */
    private fun validateGenderAppropriateInteraction(
        interaction: CommunityInteraction,
        violations: MutableList<InteractionViolation>,
        guidelines: MutableList<String>
    ) {
        if (interaction.fromGender != interaction.toGender && 
            interaction.fromGender != Gender.UNKNOWN && 
            interaction.toGender != Gender.UNKNOWN) {
            
            val inappropriateTerms = listOf(
                "beautiful", "handsome", "attractive", "pretty", "cute",
                "love", "romantic", "date", "meet me", "alone"
            )

            inappropriateTerms.forEach { term ->
                if (interaction.message.contains(term, ignoreCase = true)) {
                    violations.add(
                        InteractionViolation(
                            type = ViolationType.INAPPROPRIATE_GENDER_INTERACTION,
                            message = "Cross-gender interaction contains inappropriate language: '$term'",
                            severity = ViolationSeverity.MODERATE,
                            suggestion = "Maintain respectful, Islamic guidelines for cross-gender communication"
                        )
                    )
                }
            }

            guidelines.add("Islamic guidelines encourage respectful, purposeful communication between genders")
        }
    }

    private fun validateRespectfulCommunication(
        message: String,
        violations: MutableList<InteractionViolation>
    ) {
        val disrespectfulTerms = listOf(
            "shut up", "you're wrong", "stupid", "idiot", "ignorant",
            "don't talk", "mind your business", "go away"
        )

        disrespectfulTerms.forEach { term ->
            if (message.contains(term, ignoreCase = true)) {
                violations.add(
                    InteractionViolation(
                        type = ViolationType.DISRESPECTFUL_COMMUNICATION,
                        message = "Message contains disrespectful language: '$term'",
                        severity = ViolationSeverity.MODERATE,
                        suggestion = "Use kind and respectful language as encouraged in Islamic teachings"
                    )
                )
            }
        }
    }

    private fun validateIslamicEtiquette(
        interaction: CommunityInteraction,
        violations: MutableList<InteractionViolation>,
        guidelines: MutableList<String>
    ) {
        val message = interaction.message

        // Check for Islamic greetings
        val hasIslamicGreeting = listOf(
            "assalamu alaikum", "wa alaikum assalam", "peace be upon you"
        ).any { greeting -> message.contains(greeting, ignoreCase = true) }

        if (!hasIslamicGreeting && interaction.isFirstContact) {
            guidelines.add("Consider starting with Islamic greeting: 'Assalamu Alaikum'")
        }

        // Check for gratitude expressions
        val hasGratitude = listOf(
            "jazakallahu khair", "barakallahu feek", "may allah reward",
            "thank you", "grateful"
        ).any { gratitude -> message.contains(gratitude, ignoreCase = true) }

        if (!hasGratitude && message.length > 50) {
            guidelines.add("Islamic interactions often include expressions of gratitude to Allah and others")
        }
    }

    private fun validateCommunityHarmony(
        message: String,
        guidelines: MutableList<String>
    ) {
        val divisiveTerms = listOf(
            "us vs them", "they are wrong", "only we are right",
            "other muslims", "fake muslims", "better muslims"
        )

        val hasDivisiveLanguage = divisiveTerms.any { term ->
            message.contains(term, ignoreCase = true)
        }

        if (hasDivisiveLanguage) {
            guidelines.add("Promote Islamic unity and avoid divisive language that separates the Muslim community")
        }

        val positiveTerms = listOf(
            "ummah", "brothers and sisters", "unity", "support",
            "help each other", "islamic community"
        )

        val hasPositiveLanguage = positiveTerms.any { term ->
            message.contains(term, ignoreCase = true)
        }

        if (!hasPositiveLanguage && message.length > 100) {
            guidelines.add("Encourage Islamic brotherhood and community support in your messages")
        }
    }

    /**
     * Helper methods for analysis and scoring
     */
    private suspend fun analyzeAuthorBehavior(authorId: String): AuthorBehaviorPattern {
        try {
            // Get author's recent content history from Firestore
            val recentContent = firestore.collection("moderation_history")
                .whereEqualTo("authorId", authorId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            val violations = recentContent.documents.count { doc ->
                val data = doc.data ?: return@count false
                (data["violations"] as? List<*>)?.isNotEmpty() == true
            }

            val totalPosts = recentContent.documents.size
            val violationRate = if (totalPosts > 0) violations.toDouble() / totalPosts else 0.0

            return AuthorBehaviorPattern(
                authorId = authorId,
                recentPostCount = totalPosts,
                violationCount = violations,
                violationRate = violationRate,
                riskLevel = when {
                    violationRate > 0.3 -> RiskLevel.HIGH
                    violationRate > 0.1 -> RiskLevel.MEDIUM
                    else -> RiskLevel.LOW
                },
                lastActivity = Date()
            )

        } catch (e: Exception) {
            // Return default pattern if analysis fails
            return AuthorBehaviorPattern(
                authorId = authorId,
                recentPostCount = 0,
                violationCount = 0,
                violationRate = 0.0,
                riskLevel = RiskLevel.UNKNOWN,
                lastActivity = Date()
            )
        }
    }

    private fun calculatePositiveContentScore(content: String): Double {
        val positiveMatches = POSITIVE_INDICATORS.count { indicator ->
            content.contains(indicator, ignoreCase = true)
        }
        
        val contentLength = content.split(" ").size
        val baseScore = positiveMatches.toDouble() / maxOf(contentLength / 10, 1)
        
        return baseScore.coerceIn(0.0, 1.0)
    }

    private fun calculateMemorialAppropriatenessScore(
        memorialText: String,
        issues: List<MemorialIssue>
    ): Double {
        var score = 1.0
        
        issues.forEach { issue ->
            score -= when (issue.severity) {
                IssueSeverity.CRITICAL -> 0.4
                IssueSeverity.HIGH -> 0.2
                IssueSeverity.MODERATE -> 0.1
                IssueSeverity.LOW -> 0.05
            }
        }
        
        return score.coerceAtLeast(0.0)
    }

    private fun generateRecommendedActions(
        violations: List<ContentViolation>,
        warnings: List<ContentWarning>
    ): List<String> {
        val actions = mutableListOf<String>()
        
        violations.forEach { violation ->
            when (violation.action) {
                ModerationAction.IMMEDIATE_REMOVAL -> actions.add("Remove content immediately")
                ModerationAction.REQUIRE_REVISION -> actions.add("Request content revision")
                ModerationAction.REMOVE_AND_WARNING -> actions.add("Remove and warn user")
                ModerationAction.TEMPORARY_SUSPENSION -> actions.add("Temporarily suspend user")
                ModerationAction.IMMEDIATE_BAN -> actions.add("Permanently ban user")
                ModerationAction.IMMEDIATE_REPORT -> actions.add("Report to authorities")
                ModerationAction.CONTENT_REMOVAL -> actions.add("Remove content")
                ModerationAction.FACT_CHECK_FLAG -> actions.add("Flag for fact-checking")
            }
        }
        
        if (warnings.isNotEmpty()) {
            actions.add("Provide educational guidance to user")
        }
        
        return actions.distinct()
    }

    private fun generateModerationTags(
        content: String,
        contentType: ContentType,
        violations: List<ContentViolation>,
        warnings: List<ContentWarning>
    ): List<String> {
        val tags = mutableListOf<String>()
        
        tags.add("content_type:${contentType.name.lowercase()}")
        
        violations.forEach { violation ->
            tags.add("violation:${violation.category.name.lowercase()}")
        }
        
        warnings.forEach { warning ->
            tags.add("warning:${warning.category.name.lowercase()}")
        }
        
        val positiveScore = calculatePositiveContentScore(content)
        if (positiveScore > 0.5) tags.add("positive_content")
        
        return tags
    }

    private fun generateContentSuggestions(
        content: String,
        contentType: ContentType,
        suggestions: MutableList<String>
    ) {
        when (contentType) {
            ContentType.MEMORIAL_MESSAGE -> {
                if (!content.contains("allah", ignoreCase = true)) {
                    suggestions.add("Consider including Islamic phrases like 'May Allah have mercy'")
                }
            }
            ContentType.COMMUNITY_POST -> {
                if (!content.contains("assalamu alaikum", ignoreCase = true)) {
                    suggestions.add("Consider starting with 'Assalamu Alaikum'")
                }
            }
            else -> { /* No specific suggestions for other types */ }
        }
    }

    private fun generateMemorialImprovements(
        memorialText: String,
        issues: List<MemorialIssue>
    ): List<String> {
        val improvements = mutableListOf<String>()
        
        issues.forEach { issue ->
            issue.suggestion?.let { improvements.add(it) }
        }
        
        if (improvements.isEmpty()) {
            improvements.add("Memorial message is appropriate as written")
        }
        
        return improvements
    }

    private fun generateInteractionImprovements(
        interaction: CommunityInteraction,
        violations: List<InteractionViolation>
    ): List<String> {
        val improvements = mutableListOf<String>()
        
        violations.forEach { violation ->
            violation.suggestion?.let { improvements.add(it) }
        }
        
        if (improvements.isEmpty()) {
            improvements.add("Interaction follows Islamic community guidelines")
        }
        
        return improvements
    }
}

/**
 * Supporting data classes and enums
 */
data class ProhibitedContentRule(
    val keywords: List<String>,
    val severity: ModerationSeverity,
    val action: ModerationAction,
    val description: String
)

data class CulturalSensitivity(
    val avoidTerms: List<String>,
    val preferredTerms: List<String>,
    val contextualWarnings: List<String>
)

data class ContentModerationResult(
    val isApproved: Boolean,
    val autoApproved: Boolean,
    val requiresHumanReview: Boolean,
    val violations: List<ContentViolation>,
    val warnings: List<ContentWarning>,
    val flags: List<ContentFlag>,
    val positiveScore: Double,
    val recommendedActions: List<String>,
    val moderationTags: List<String>
)

data class RealtimeFilterResult(
    val isBlocked: Boolean,
    val violations: List<String>,
    val suggestions: List<String>,
    val filteredContent: String
)

data class MemorialValidationResult(
    val isAppropriate: Boolean,
    val issues: List<MemorialIssue>,
    val recommendations: List<String>,
    val suggestedImprovements: List<String>,
    val appropriatenessScore: Double
)

data class InteractionValidationResult(
    val isAppropriate: Boolean,
    val violations: List<InteractionViolation>,
    val guidelines: List<String>,
    val improvementSuggestions: List<String>
)

data class ContentViolation(
    val category: ProhibitionCategory,
    val message: String,
    val severity: ModerationSeverity,
    val action: ModerationAction,
    val matchedKeyword: String? = null,
    val position: Int = -1
)

data class ContentWarning(
    val category: WarningCategory,
    val message: String,
    val suggestion: String? = null
)

data class ContentFlag(
    val category: FlagCategory,
    val message: String,
    val confidence: Double
)

data class MemorialIssue(
    val type: IssueType,
    val message: String,
    val severity: IssueSeverity,
    val suggestion: String? = null
)

data class InteractionViolation(
    val type: ViolationType,
    val message: String,
    val severity: ViolationSeverity,
    val suggestion: String? = null
)

data class CommunityInteraction(
    val fromUserId: String,
    val toUserId: String,
    val fromGender: Gender,
    val toGender: Gender,
    val message: String,
    val isFirstContact: Boolean = false,
    val context: InteractionContext = InteractionContext.GENERAL
)

data class AuthorBehaviorPattern(
    val authorId: String,
    val recentPostCount: Int,
    val violationCount: Int,
    val violationRate: Double,
    val riskLevel: RiskLevel,
    val lastActivity: Date
)

// Enums are imported from CulturalValidationTypes