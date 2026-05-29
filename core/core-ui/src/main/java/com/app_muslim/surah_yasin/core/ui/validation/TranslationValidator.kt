package com.app_muslim.surah_yasin.core.ui.validation

// Import removed to fix compilation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Regional Translation Validator for Islamic Content
 * 
 * Comprehensive translation validation system working with regional Islamic experts
 * to ensure accurate, culturally appropriate translations of Islamic content across
 * 20+ languages and regional dialects. Validates:
 * 
 * - Prayer translations and transliterations
 * - Memorial messages and phrases  
 * - Islamic terminology accuracy
 * - Cultural context preservation
 * - Regional dialect appropriateness
 * - Religious authenticity maintenance
 */
@Singleton
class TranslationValidator @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val TRANSLATION_EXPERTS_COLLECTION = "translation_experts"
        private const val TRANSLATION_REVIEWS_COLLECTION = "translation_reviews"
        private const val VERIFIED_TRANSLATIONS_COLLECTION = "verified_translations"
        
        // Supported languages with regional variations
        val SUPPORTED_LANGUAGES = mapOf(
            "arabic" to LanguageInfo(
                name = "العربية",
                englishName = "Arabic",
                regions = listOf(IslamicRegion.MIDDLE_EAST, IslamicRegion.NORTH_AFRICA),
                dialects = listOf("Modern Standard", "Gulf", "Levantine", "Maghrebi", "Egyptian"),
                isSourceLanguage = true
            ),
            "english" to LanguageInfo(
                name = "English",
                englishName = "English",
                regions = listOf(IslamicRegion.NORTH_AMERICA, IslamicRegion.EUROPE, IslamicRegion.SUB_SAHARAN_AFRICA),
                dialects = listOf("American", "British", "South African"),
                isSourceLanguage = false
            ),
            "urdu" to LanguageInfo(
                name = "اردو",
                englishName = "Urdu",
                regions = listOf(IslamicRegion.SOUTH_ASIA),
                dialects = listOf("Pakistani", "Indian", "Bangladeshi"),
                isSourceLanguage = false
            ),
            "indonesian" to LanguageInfo(
                name = "Bahasa Indonesia",
                englishName = "Indonesian",
                regions = listOf(IslamicRegion.SOUTHEAST_ASIA),
                dialects = listOf("Standard", "Javanese-influenced"),
                isSourceLanguage = false
            ),
            "malay" to LanguageInfo(
                name = "Bahasa Melayu",
                englishName = "Malay",
                regions = listOf(IslamicRegion.SOUTHEAST_ASIA),
                dialects = listOf("Malaysian", "Brunei"),
                isSourceLanguage = false
            ),
            "turkish" to LanguageInfo(
                name = "Türkçe",
                englishName = "Turkish",
                regions = listOf(IslamicRegion.MIDDLE_EAST, IslamicRegion.EUROPE),
                dialects = listOf("Standard Turkish"),
                isSourceLanguage = false
            ),
            "persian" to LanguageInfo(
                name = "فارسی",
                englishName = "Persian/Farsi",
                regions = listOf(IslamicRegion.MIDDLE_EAST, IslamicRegion.CENTRAL_ASIA),
                dialects = listOf("Iranian", "Afghan", "Tajik"),
                isSourceLanguage = false
            ),
            "french" to LanguageInfo(
                name = "Français",
                englishName = "French",
                regions = listOf(IslamicRegion.NORTH_AFRICA, IslamicRegion.SUB_SAHARAN_AFRICA),
                dialects = listOf("French", "Maghrebi French"),
                isSourceLanguage = false
            ),
            "hausa" to LanguageInfo(
                name = "Hausa",
                englishName = "Hausa",
                regions = listOf(IslamicRegion.SUB_SAHARAN_AFRICA),
                dialects = listOf("Nigerian", "Nigerien"),
                isSourceLanguage = false
            ),
            "swahili" to LanguageInfo(
                name = "Kiswahili",
                englishName = "Swahili",
                regions = listOf(IslamicRegion.SUB_SAHARAN_AFRICA),
                dialects = listOf("Kenyan", "Tanzanian"),
                isSourceLanguage = false
            ),
            "bengali" to LanguageInfo(
                name = "বাংলা",
                englishName = "Bengali",
                regions = listOf(IslamicRegion.SOUTH_ASIA),
                dialects = listOf("Bangladeshi", "West Bengali"),
                isSourceLanguage = false
            ),
            "hindi" to LanguageInfo(
                name = "हिन्दी",
                englishName = "Hindi",
                regions = listOf(IslamicRegion.SOUTH_ASIA),
                dialects = listOf("Standard Hindi", "Urdu-influenced"),
                isSourceLanguage = false
            )
        )
        
        // Critical Islamic terms that require expert validation
        val CRITICAL_ISLAMIC_TERMS = mapOf(
            "Allah" to CriticalTerm(
                arabic = "الله",
                category = TermCategory.DIVINE_NAME,
                translations = mapOf(
                    "english" to listOf("Allah", "God"),
                    "urdu" to listOf("اللہ", "الله"),
                    "indonesian" to listOf("Allah"),
                    "turkish" to listOf("Allah", "Tanrı"),
                    "french" to listOf("Allah", "Dieu")
                ),
                validationRequired = true
            ),
            "Prophet Muhammad" to CriticalTerm(
                arabic = "النبي محمد",
                category = TermCategory.PROPHET_NAME,
                translations = mapOf(
                    "english" to listOf("Prophet Muhammad", "Prophet Mohammed (PBUH)"),
                    "urdu" to listOf("حضرت محمد صلی اللہ علیہ وسلم", "نبی کریم"),
                    "indonesian" to listOf("Nabi Muhammad SAW", "Rasulullah SAW"),
                    "turkish" to listOf("Hz. Muhammed", "Peygamber Efendimiz"),
                    "french" to listOf("Prophète Mahomet", "Le Messager d'Allah")
                ),
                validationRequired = true
            ),
            "prayer" to CriticalTerm(
                arabic = "صلاة",
                category = TermCategory.WORSHIP_TERM,
                translations = mapOf(
                    "english" to listOf("prayer", "salah", "namaz"),
                    "urdu" to listOf("نماز", "صلاۃ"),
                    "indonesian" to listOf("shalat", "sembahyang"),
                    "turkish" to listOf("namaz", "salat"),
                    "french" to listOf("prière", "salat")
                ),
                validationRequired = false
            )
        )
    }

    /**
     * Submit translation for expert validation
     */
    suspend fun submitTranslationForReview(
        request: TranslationValidationRequest
    ): TranslationValidationResponse {
        try {
            // Create translation review document
            val reviewDoc = firestore
                .collection(TRANSLATION_REVIEWS_COLLECTION)
                .document()

            val reviewData = mapOf(
                "id" to reviewDoc.id,
                "sourceText" to request.sourceText,
                "sourceLanguage" to request.sourceLanguage,
                "targetLanguage" to request.targetLanguage,
                "proposedTranslation" to request.proposedTranslation,
                "contentType" to request.contentType.name,
                "submittedBy" to request.submittedBy,
                "timestamp" to Date(),
                "status" to TranslationStatus.PENDING.name,
                "priority" to request.priority.name,
                "culturalContext" to mapOf("type" to request.culturalContext.name),
                "assignedExperts" to emptyList<String>()
            )

            reviewDoc.set(reviewData).await()

            // Assign appropriate translation experts
            val assignedExperts = assignTranslationExperts(request)
            reviewDoc.update("assignedExperts", assignedExperts).await()

            return TranslationValidationResponse.Success(
                reviewId = reviewDoc.id,
                assignedExperts = assignedExperts,
                estimatedReviewTime = calculateReviewTime(request)
            )

        } catch (e: Exception) {
            return TranslationValidationResponse.Error(
                message = "Failed to submit translation for review: ${e.message}"
            )
        }
    }

    /**
     * Validate translation quality and accuracy
     */
    suspend fun validateTranslationQuality(
        sourceText: String,
        translation: String,
        sourceLanguage: String,
        targetLanguage: String,
        contentType: TranslationContentType
    ): TranslationQualityResult {
        val issues = mutableListOf<TranslationIssue>()
        val suggestions = mutableListOf<TranslationSuggestion>()
        val score = mutableMapOf<String, Double>()

        // 1. Critical term validation
        validateCriticalTerms(sourceText, translation, sourceLanguage, targetLanguage, issues, suggestions)

        // 2. Religious appropriateness
        validateReligiousAppropriateness(translation, targetLanguage, contentType, issues, suggestions)

        // 3. Cultural context preservation
        validateCulturalContext(sourceText, translation, targetLanguage, issues, suggestions)

        // 4. Grammar and fluency check
        validateGrammarAndFluency(translation, targetLanguage, issues, suggestions)

        // 5. Consistency check
        validateConsistency(sourceText, translation, sourceLanguage, targetLanguage, issues, suggestions)

        // Calculate quality scores
        score["accuracy"] = calculateAccuracyScore(issues)
        score["fluency"] = calculateFluencyScore(issues)
        score["appropriateness"] = calculateAppropriatenessScore(issues)
        score["overall"] = (score["accuracy"]!! + score["fluency"]!! + score["appropriateness"]!!) / 3

        return TranslationQualityResult(
            overallScore = score["overall"]!!,
            accuracyScore = score["accuracy"]!!,
            fluencyScore = score["fluency"]!!,
            appropriatenessScore = score["appropriateness"]!!,
            issues = issues,
            suggestions = suggestions,
            isAcceptable = score["overall"]!! >= 0.7 && issues.none { it.severity == IssueSeverity.CRITICAL }
        )
    }

    /**
     * Get verified translations from database
     */
    suspend fun getVerifiedTranslation(
        sourceText: String,
        sourceLanguage: String,
        targetLanguage: String
    ): VerifiedTranslation? {
        try {
            val query = firestore
                .collection(VERIFIED_TRANSLATIONS_COLLECTION)
                .whereEqualTo("sourceText", sourceText)
                .whereEqualTo("sourceLanguage", sourceLanguage)
                .whereEqualTo("targetLanguage", targetLanguage)
                .whereEqualTo("isVerified", true)
                .limit(1)

            val result = query.get().await()
            
            if (result.isEmpty) return null
            
            val doc = result.documents.first()
            val data = doc.data!!
            
            return VerifiedTranslation(
                id = doc.id,
                sourceText = data["sourceText"] as String,
                translation = data["translation"] as String,
                sourceLanguage = data["sourceLanguage"] as String,
                targetLanguage = data["targetLanguage"] as String,
                verifiedBy = data["verifiedBy"] as String,
                verifiedAt = data["verifiedAt"] as Date,
                qualityScore = data["qualityScore"] as Double,
                expertNotes = data["expertNotes"] as? String ?: "",
                usageCount = (data["usageCount"] as Long).toInt()
            )

        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Get translation experts for language pair
     */
    suspend fun getTranslationExperts(
        sourceLanguage: String,
        targetLanguage: String,
        region: IslamicRegion? = null
    ): List<TranslationExpert> {
        try {
            var query = firestore.collection(TRANSLATION_EXPERTS_COLLECTION)
                .whereArrayContains("languagePairs", "$sourceLanguage-$targetLanguage")
                .whereEqualTo("isActive", true)
                .orderBy("expertiseLevel", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(5)

            val experts = query.get().await()
            
            return experts.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                TranslationExpert(
                    id = doc.id,
                    name = data["name"] as String,
                    languagePairs = data["languagePairs"] as List<String>,
                    specializations = (data["specializations"] as List<String>).map { 
                        TranslationSpecialization.valueOf(it) 
                    },
                    region = IslamicRegion.valueOf(data["primaryRegion"] as String),
                    yearsOfExperience = (data["yearsOfExperience"] as Long).toInt(),
                    completedTranslations = (data["completedTranslations"] as Long).toInt(),
                    averageScore = data["averageScore"] as Double,
                    responseTime = (data["responseTime"] as Long).toInt(),
                    isActive = data["isActive"] as Boolean
                )
            }

        } catch (e: Exception) {
            return emptyList()
        }
    }

    /**
     * Validate critical Islamic terms
     */
    private fun validateCriticalTerms(
        sourceText: String,
        translation: String,
        sourceLanguage: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        CRITICAL_ISLAMIC_TERMS.forEach { (termKey, termInfo) ->
            if (sourceText.contains(termInfo.arabic) || 
                (sourceLanguage == "english" && sourceText.contains(termKey, ignoreCase = true))) {
                
                val validTranslations = termInfo.translations[targetLanguage] ?: return@forEach
                val hasValidTranslation = validTranslations.any { validTranslation ->
                    translation.contains(validTranslation, ignoreCase = true)
                }
                
                if (!hasValidTranslation) {
                    val severity = if (termInfo.validationRequired) IssueSeverity.CRITICAL else IssueSeverity.MODERATE
                    
                    issues.add(
                        TranslationIssue(
                            category = IssueCategory.CRITICAL_TERM,
                            message = "Critical Islamic term '$termKey' may not be translated appropriately",
                            severity = severity,
                            position = sourceText.indexOf(termKey).takeIf { it >= 0 },
                            suggestion = "Consider using: ${validTranslations.joinToString(" or ")}"
                        )
                    )
                    
                    suggestions.add(
                        TranslationSuggestion(
                            category = SuggestionCategory.TERMINOLOGY,
                            suggestion = "For '$termKey', consider: ${validTranslations.first()}",
                            confidence = 0.9
                        )
                    )
                }
            }
        }
    }

    /**
     * Validate religious appropriateness of translation
     */
    private fun validateReligiousAppropriateness(
        translation: String,
        targetLanguage: String,
        contentType: TranslationContentType,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        when (contentType) {
            TranslationContentType.PRAYER -> {
                validatePrayerTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.MEMORIAL_MESSAGE -> {
                validateMemorialTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.QURANIC_VERSE -> {
                validateQuranicTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.GENERAL_ISLAMIC -> {
                validateGeneralIslamicTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.PRAYER_TRANSLATION -> {
                validatePrayerTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.MEMORIAL_TEXT -> {
                validateMemorialTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.RELIGIOUS_INSTRUCTION -> {
                validateGeneralIslamicTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.CULTURAL_GUIDANCE -> {
                validateGeneralIslamicTranslation(translation, targetLanguage, issues, suggestions)
            }
            TranslationContentType.GENERAL_CONTENT -> {
                validateGeneralIslamicTranslation(translation, targetLanguage, issues, suggestions)
            }
        }
    }

    /**
     * Validate prayer translation appropriateness
     */
    private fun validatePrayerTranslation(
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Check for essential prayer elements
        val prayerKeywords = when (targetLanguage) {
            "english" -> listOf("Allah", "God", "prayer", "blessing", "mercy")
            "urdu" -> listOf("اللہ", "دعا", "رحمت", "برکت")
            "indonesian" -> listOf("Allah", "doa", "rahmat", "berkah")
            "turkish" -> listOf("Allah", "dua", "rahmet", "bereket")
            else -> listOf("Allah", "prayer")
        }
        
        val hasPrayerElements = prayerKeywords.any { keyword ->
            translation.contains(keyword, ignoreCase = true)
        }
        
        if (!hasPrayerElements && translation.length > 10) {
            issues.add(
                TranslationIssue(
                    category = IssueCategory.RELIGIOUS_APPROPRIATENESS,
                    message = "Prayer translation may be missing essential Islamic elements",
                    severity = IssueSeverity.MODERATE,
                    suggestion = "Ensure translation includes appropriate Islamic terminology"
                )
            )
        }
    }

    /**
     * Validate memorial message translation
     */
    private fun validateMemorialTranslation(
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Common memorial phrases by language
        val memorialPhrases = when (targetLanguage) {
            "english" -> listOf("may Allah have mercy", "rest in peace", "may he rest in Jannah")
            "urdu" -> listOf("اللہ مغفرت کرے", "جنت میں جگہ دے", "رحمت کرے")
            "indonesian" -> listOf("semoga Allah merahmati", "masuk surga", "diampuni Allah")
            "turkish" -> listOf("Allah rahmet eylesin", "cennetlik olsun", "mağfiret buyursun")
            else -> listOf("mercy", "forgiveness", "paradise")
        }
        
        val hasMemorialElements = memorialPhrases.any { phrase ->
            translation.contains(phrase, ignoreCase = true)
        }
        
        if (!hasMemorialElements && translation.length > 15) {
            suggestions.add(
                TranslationSuggestion(
                    category = SuggestionCategory.CONTENT_ENHANCEMENT,
                    suggestion = "Consider adding appropriate memorial phrases like: ${memorialPhrases.first()}",
                    confidence = 0.7
                )
            )
        }
    }

    /**
     * Validate Quranic verse translation
     */
    private fun validateQuranicTranslation(
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Quranic translations require highest accuracy
        issues.add(
            TranslationIssue(
                category = IssueCategory.QURANIC_ACCURACY,
                message = "Quranic verse translations require expert scholar verification",
                severity = IssueSeverity.HIGH,
                suggestion = "Please submit to Islamic scholar for verification"
            )
        )
        
        suggestions.add(
            TranslationSuggestion(
                category = SuggestionCategory.EXPERT_REVIEW,
                suggestion = "Include verse reference (Surah:Ayah) for verification",
                confidence = 1.0
            )
        )
    }

    /**
     * Validate general Islamic content translation
     */
    private fun validateGeneralIslamicTranslation(
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Basic Islamic terminology validation
        val islamicTerms = when (targetLanguage) {
            "english" -> listOf("Islam", "Muslim", "Islamic", "Allah")
            "urdu" -> listOf("اسلام", "مسلمان", "اللہ")
            "indonesian" -> listOf("Islam", "Muslim", "Allah")
            "turkish" -> listOf("İslam", "Müslüman", "Allah")
            else -> listOf("Islam", "Muslim")
        }
        
        val hasIslamicTerms = islamicTerms.any { term ->
            translation.contains(term, ignoreCase = true)
        }
        
        if (!hasIslamicTerms && translation.length > 20) {
            suggestions.add(
                TranslationSuggestion(
                    category = SuggestionCategory.TERMINOLOGY,
                    suggestion = "Consider including appropriate Islamic terminology",
                    confidence = 0.6
                )
            )
        }
    }

    /**
     * Validate cultural context preservation
     */
    private fun validateCulturalContext(
        sourceText: String,
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Check for cultural references that might be lost in translation
        val culturalMarkers = listOf("insha'Allah", "masha'Allah", "subhan'Allah", "alhamdulillah")
        
        culturalMarkers.forEach { marker ->
            if (sourceText.contains(marker, ignoreCase = true) && 
                !translation.contains(marker, ignoreCase = true)) {
                
                suggestions.add(
                    TranslationSuggestion(
                        category = SuggestionCategory.CULTURAL_PRESERVATION,
                        suggestion = "Consider keeping '$marker' in original form for cultural authenticity",
                        confidence = 0.8
                    )
                )
            }
        }
    }

    /**
     * Validate grammar and fluency
     */
    private fun validateGrammarAndFluency(
        translation: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Basic fluency checks
        val sentences = translation.split(".", "!", "?").filter { it.isNotBlank() }
        
        sentences.forEach { sentence ->
            val words = sentence.trim().split(" ").filter { it.isNotBlank() }
            
            // Check sentence length (too short or too long may indicate issues)
            if (words.size < 3 && sentence.length > 10) {
                issues.add(
                    TranslationIssue(
                        category = IssueCategory.FLUENCY,
                        message = "Sentence may be too fragmented",
                        severity = IssueSeverity.LOW,
                        suggestion = "Consider combining short phrases for better flow"
                    )
                )
            } else if (words.size > 30) {
                issues.add(
                    TranslationIssue(
                        category = IssueCategory.FLUENCY,
                        message = "Sentence may be too long",
                        severity = IssueSeverity.LOW,
                        suggestion = "Consider breaking into shorter sentences"
                    )
                )
            }
        }
    }

    /**
     * Validate translation consistency
     */
    private fun validateConsistency(
        sourceText: String,
        translation: String,
        sourceLanguage: String,
        targetLanguage: String,
        issues: MutableList<TranslationIssue>,
        suggestions: MutableList<TranslationSuggestion>
    ) {
        // Check if similar length (significant length difference may indicate issues)
        val lengthRatio = translation.length.toDouble() / sourceText.length.toDouble()
        
        when {
            lengthRatio < 0.5 -> {
                issues.add(
                    TranslationIssue(
                        category = IssueCategory.CONSISTENCY,
                        message = "Translation seems significantly shorter than source",
                        severity = IssueSeverity.MODERATE,
                        suggestion = "Check if all content has been translated"
                    )
                )
            }
            lengthRatio > 2.0 -> {
                issues.add(
                    TranslationIssue(
                        category = IssueCategory.CONSISTENCY,
                        message = "Translation seems significantly longer than source", 
                        severity = IssueSeverity.MODERATE,
                        suggestion = "Check for unnecessary additions or repetition"
                    )
                )
            }
        }
    }

    /**
     * Assign translation experts to review request
     */
    private suspend fun assignTranslationExperts(request: TranslationValidationRequest): List<String> {
        val experts = getTranslationExperts(
            sourceLanguage = request.sourceLanguage,
            targetLanguage = request.targetLanguage,
            region = IslamicRegion.MIDDLE_EAST // TODO: Add region field to request or determine from context
        )
        
        return experts.take(2).map { it.id } // Assign top 2 experts
    }

    /**
     * Calculate review time based on complexity
     */
    private fun calculateReviewTime(request: TranslationValidationRequest): Int {
        var baseDays = when (request.priority) {
            TranslationPriority.URGENT -> 1
            TranslationPriority.HIGH -> 2
            TranslationPriority.CRITICAL -> 1
            TranslationPriority.MEDIUM -> 4
            TranslationPriority.NORMAL -> 5
            TranslationPriority.LOW -> 10
        }
        
        // Add complexity factors
        if (request.contentType == TranslationContentType.QURANIC_VERSE) baseDays += 2
        if (request.sourceText.length > 500) baseDays += 1
        
        return baseDays
    }

    /**
     * Calculate quality scores
     */
    private fun calculateAccuracyScore(issues: List<TranslationIssue>): Double {
        var score = 1.0
        issues.forEach { issue ->
            score -= when (issue.severity) {
                IssueSeverity.CRITICAL -> 0.3
                IssueSeverity.HIGH -> 0.2
                IssueSeverity.MODERATE -> 0.1
                IssueSeverity.LOW -> 0.05
            }
        }
        return score.coerceAtLeast(0.0)
    }

    private fun calculateFluencyScore(issues: List<TranslationIssue>): Double {
        var score = 1.0
        val fluencyIssues = issues.filter { 
            it.category == IssueCategory.FLUENCY || it.category == IssueCategory.CONSISTENCY 
        }
        fluencyIssues.forEach { issue ->
            score -= when (issue.severity) {
                IssueSeverity.CRITICAL -> 0.25
                IssueSeverity.HIGH -> 0.15
                IssueSeverity.MODERATE -> 0.08
                IssueSeverity.LOW -> 0.03
            }
        }
        return score.coerceAtLeast(0.0)
    }

    private fun calculateAppropriatenessScore(issues: List<TranslationIssue>): Double {
        var score = 1.0
        val appropriatenessIssues = issues.filter { 
            it.category == IssueCategory.RELIGIOUS_APPROPRIATENESS || 
            it.category == IssueCategory.CRITICAL_TERM 
        }
        appropriatenessIssues.forEach { issue ->
            score -= when (issue.severity) {
                IssueSeverity.CRITICAL -> 0.4
                IssueSeverity.HIGH -> 0.25
                IssueSeverity.MODERATE -> 0.15
                IssueSeverity.LOW -> 0.05
            }
        }
        return score.coerceAtLeast(0.0)
    }
}

/**
 * Supporting data classes and enums
 */
data class LanguageInfo(
    val name: String,
    val englishName: String,
    val regions: List<IslamicRegion>,
    val dialects: List<String>,
    val isSourceLanguage: Boolean
)

data class CriticalTerm(
    val arabic: String,
    val category: TermCategory,
    val translations: Map<String, List<String>>,
    val validationRequired: Boolean
)

data class TranslationValidationRequest(
    val sourceText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val proposedTranslation: String,
    val contentType: TranslationContentType,
    val submittedBy: String,
    val priority: TranslationPriority,
    val culturalContext: CulturalValidationContext
)

data class TranslationQualityResult(
    val overallScore: Double,
    val accuracyScore: Double,
    val fluencyScore: Double,
    val appropriatenessScore: Double,
    val issues: List<TranslationIssue>,
    val suggestions: List<TranslationSuggestion>,
    val isAcceptable: Boolean
)

data class TranslationIssue(
    val category: IssueCategory,
    val message: String,
    val severity: IssueSeverity,
    val position: Int? = null,
    val suggestion: String? = null
)

data class TranslationSuggestion(
    val category: SuggestionCategory,
    val suggestion: String,
    val confidence: Double
)

data class VerifiedTranslation(
    val id: String,
    val sourceText: String,
    val translation: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val verifiedBy: String,
    val verifiedAt: Date,
    val qualityScore: Double,
    val expertNotes: String,
    val usageCount: Int
)

data class TranslationExpert(
    val id: String,
    val name: String,
    val languagePairs: List<String>,
    val specializations: List<TranslationSpecialization>,
    val region: IslamicRegion,
    val yearsOfExperience: Int,
    val completedTranslations: Int,
    val averageScore: Double,
    val responseTime: Int,
    val isActive: Boolean
)

// Enums are imported from CulturalValidationTypes

sealed class TranslationValidationResponse {
    data class Success(
        val reviewId: String,
        val assignedExperts: List<String>,
        val estimatedReviewTime: Int
    ) : TranslationValidationResponse()
    
    data class Error(val message: String) : TranslationValidationResponse()
}