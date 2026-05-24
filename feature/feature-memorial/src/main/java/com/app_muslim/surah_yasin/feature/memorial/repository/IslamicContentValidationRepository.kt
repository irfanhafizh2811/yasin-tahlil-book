package com.app_muslim.surah_yasin.feature.memorial.repository

import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IslamicContentValidationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val validationCollection = firestore.collection("islamicContentValidation")
    private val scholarReviewCollection = firestore.collection("scholarReviews")
    private val moderationResultsCollection = firestore.collection("moderationResults")
    
    suspend fun submitContentForValidation(request: IslamicContentValidationRequest): String {
        val validationId = firestore.collection("temp").document().id
        
        val validation = IslamicContentValidation(
            id = validationId,
            contentText = request.contentText,
            contentType = request.contentType,
            language = request.language,
            createdAt = Date(),
            validationStatus = ValidationStatus.PENDING
        )
        
        validationCollection.document(validationId).set(validation).await()
        
        // Trigger automatic moderation
        performAutomaticModeration(validationId, request)
        
        return validationId
    }
    
    suspend fun getValidationResult(validationId: String): IslamicContentValidation? {
        return try {
            val document = validationCollection.document(validationId).get().await()
            document.toObject(IslamicContentValidation::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun validateMemorialContent(
        title: String,
        description: String,
        region: IslamicRegion,
        schoolOfThought: SchoolOfThought
    ): ContentModerationResult {
        val combinedContent = "$title\n$description"
        
        // Perform comprehensive validation
        val automaticFlags = performContentAnalysis(combinedContent)
        val isApproved = automaticFlags.none { it.suggestedAction == ContentSuggestedAction.REJECTION_RECOMMENDED }
        val needsHumanReview = automaticFlags.any { 
            it.suggestedAction == ContentSuggestedAction.REVIEW_REQUIRED || 
            it.suggestedAction == ContentSuggestedAction.SCHOLAR_CONSULTATION 
        }
        
        val result = ContentModerationResult(
            contentId = UUID.randomUUID().toString(),
            isApproved = isApproved,
            moderationLevel = if (needsHumanReview) ModerationLevel.HUMAN_ASSISTED else ModerationLevel.AUTOMATED,
            automaticFlags = automaticFlags,
            humanReviewRequired = needsHumanReview,
            confidenceScore = calculateConfidenceScore(automaticFlags),
            processedAt = Date(),
            finalDecision = if (isApproved) ModerationDecision.APPROVED else ModerationDecision.NEEDS_REVISION
        )
        
        // Save moderation result
        moderationResultsCollection.document(result.contentId).set(result).await()
        
        return result
    }
    
    suspend fun validatePrayerContent(
        prayerText: String,
        prayerType: PrayerType,
        language: String
    ): IslamicContentValidation {
        val validationId = UUID.randomUUID().toString()
        
        // Enhanced validation for prayer content
        val flags = performPrayerContentAnalysis(prayerText, prayerType, language)
        val suggestions = generatePrayerSuggestions(prayerText, prayerType)
        
        val validation = IslamicContentValidation(
            id = validationId,
            contentText = prayerText,
            contentType = ContentType.PRAYER_TEXT,
            language = language,
            isValidated = flags.isEmpty(),
            validationStatus = if (flags.isEmpty()) ValidationStatus.APPROVED else ValidationStatus.NEEDS_REVISION,
            inappropriateFlags = flags,
            suggestedCorrections = suggestions,
            islamicComplianceScore = calculateIslamicComplianceScore(prayerText, prayerType),
            culturalSensitivityScore = calculateCulturalSensitivityScore(prayerText),
            createdAt = Date(),
            lastReviewed = Date()
        )
        
        validationCollection.document(validationId).set(validation).await()
        return validation
    }
    
    suspend fun requestScholarReview(
        validationId: String,
        scholarId: String,
        urgencyLevel: UrgencyLevel = UrgencyLevel.NORMAL
    ): String {
        val reviewId = UUID.randomUUID().toString()
        
        val review = IslamicScholarReview(
            id = reviewId,
            contentId = validationId,
            scholarId = scholarId,
            reviewStatus = ReviewStatus.SUBMITTED,
            reviewedAt = Date()
        )
        
        scholarReviewCollection.document(reviewId).set(review).await()
        
        // Update validation status
        validationCollection.document(validationId)
            .update("validationStatus", ValidationStatus.SCHOLAR_REVIEW_REQUIRED)
            .await()
        
        return reviewId
    }
    
    fun getValidationHistory(userId: String): Flow<List<IslamicContentValidation>> = flow {
        try {
            val snapshot = validationCollection
                .whereEqualTo("validatedBy", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            
            val validations = snapshot.documents.mapNotNull { document ->
                document.toObject(IslamicContentValidation::class.java)
            }
            emit(validations)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    fun getPendingScholarReviews(scholarId: String): Flow<List<IslamicScholarReview>> = flow {
        try {
            val snapshot = scholarReviewCollection
                .whereEqualTo("scholarId", scholarId)
                .whereEqualTo("reviewStatus", ReviewStatus.UNDER_REVIEW)
                .orderBy("reviewedAt", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val reviews = snapshot.documents.mapNotNull { document ->
                document.toObject(IslamicScholarReview::class.java)
            }
            emit(reviews)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    suspend fun updateValidationStatus(
        validationId: String,
        status: ValidationStatus,
        approvedContent: String? = null,
        rejectionReason: String? = null
    ) {
        val updates = mutableMapOf<String, Any>(
            "validationStatus" to status,
            "lastReviewed" to Date()
        )
        
        if (status == ValidationStatus.APPROVED) {
            updates["isValidated"] = true
            updates["validatedAt"] = Date()
            if (approvedContent != null) {
                updates["approvedContent"] = approvedContent
            }
        } else if (status == ValidationStatus.REJECTED && rejectionReason != null) {
            updates["rejectionReason"] = rejectionReason
        }
        
        validationCollection.document(validationId).update(updates).await()
    }
    
    suspend fun submitScholarReview(
        reviewId: String,
        ruling: IslamicRuling,
        detailedReview: String,
        arabicReview: String = "",
        recommendations: List<String> = emptyList(),
        references: List<IslamicReference> = emptyList(),
        approvalLevel: ApprovalLevel = ApprovalLevel.CONDITIONAL
    ) {
        val updates = mapOf(
            "reviewStatus" to ReviewStatus.COMPLETED,
            "islamicRuling" to ruling,
            "detailedReview" to detailedReview,
            "arabicReview" to arabicReview,
            "recommendations" to recommendations,
            "references" to references,
            "approvalLevel" to approvalLevel,
            "reviewedAt" to Date()
        )
        
        scholarReviewCollection.document(reviewId).update(updates).await()
        
        // Update corresponding validation
        val review = scholarReviewCollection.document(reviewId).get().await()
            .toObject(IslamicScholarReview::class.java)
        
        if (review != null) {
            val finalStatus = when (ruling) {
                IslamicRuling.PERMISSIBLE, IslamicRuling.RECOMMENDED, IslamicRuling.OBLIGATORY -> 
                    ValidationStatus.APPROVED
                IslamicRuling.FORBIDDEN -> 
                    ValidationStatus.REJECTED
                else -> 
                    ValidationStatus.APPROVED_WITH_CONDITIONS
            }
            
            updateValidationStatus(review.contentId, finalStatus)
        }
    }
    
    private suspend fun performAutomaticModeration(
        validationId: String,
        request: IslamicContentValidationRequest
    ) {
        val automaticFlags = performContentAnalysis(request.contentText)
        val moderationResult = ContentModerationResult(
            contentId = validationId,
            isApproved = automaticFlags.isEmpty(),
            moderationLevel = ModerationLevel.AUTOMATED,
            automaticFlags = automaticFlags,
            humanReviewRequired = automaticFlags.isNotEmpty(),
            confidenceScore = calculateConfidenceScore(automaticFlags),
            processedAt = Date(),
            finalDecision = if (automaticFlags.isEmpty()) ModerationDecision.APPROVED else ModerationDecision.NEEDS_REVISION
        )
        
        moderationResultsCollection.document(validationId).set(moderationResult).await()
        
        // Update validation status based on moderation
        val newStatus = when {
            automaticFlags.isEmpty() -> ValidationStatus.APPROVED
            automaticFlags.any { it.suggestedAction == ContentSuggestedAction.SCHOLAR_CONSULTATION } -> 
                ValidationStatus.SCHOLAR_REVIEW_REQUIRED
            else -> ValidationStatus.NEEDS_REVISION
        }
        
        updateValidationStatus(validationId, newStatus)
    }
    
    private fun performContentAnalysis(content: String): List<AutomaticFlag> {
        val flags = mutableListOf<AutomaticFlag>()
        val lowerContent = content.lowercase()
        
        // Inappropriate language detection
        val inappropriateWords = listOf("inappropriate", "offensive", "disrespectful") // Simplified for example
        inappropriateWords.forEach { word ->
            if (lowerContent.contains(word)) {
                flags.add(AutomaticFlag(
                    detectionType = DetectionType.KEYWORD_FILTER,
                    confidence = 0.8f,
                    flaggedContent = word,
                    reason = "Potentially inappropriate language detected",
                    suggestedAction = ContentSuggestedAction.REVIEW_REQUIRED
                ))
            }
        }
        
        // Cultural sensitivity checks
        if (lowerContent.contains("sect") || lowerContent.contains("denomination")) {
            flags.add(AutomaticFlag(
                detectionType = DetectionType.CULTURAL_AI,
                confidence = 0.6f,
                flaggedContent = "sectarian references",
                reason = "Potential sectarian content detected",
                suggestedAction = ContentSuggestedAction.REVIEW_REQUIRED
            ))
        }
        
        // Length validation
        if (content.length > 2000) {
            flags.add(AutomaticFlag(
                detectionType = DetectionType.CONTEXTUAL_ANALYSIS,
                confidence = 1.0f,
                flaggedContent = "content length",
                reason = "Content exceeds recommended length",
                suggestedAction = ContentSuggestedAction.REVIEW_REQUIRED
            ))
        }
        
        return flags
    }
    
    private fun performPrayerContentAnalysis(
        prayerText: String,
        prayerType: PrayerType,
        language: String
    ): List<InappropriateFlag> {
        val flags = mutableListOf<InappropriateFlag>()
        
        // Validate prayer structure based on type
        when (prayerType) {
            PrayerType.FATIHAH -> {
                if (!prayerText.contains("bismillah", ignoreCase = true) && language == "ar") {
                    flags.add(InappropriateFlag(
                        flagType = FlagType.THEOLOGICAL_ERROR,
                        description = "Al-Fatihah should begin with Bismillah",
                        severity = FlagSeverity.MEDIUM,
                        explanation = "Traditional structure not followed"
                    ))
                }
            }
            PrayerType.TAHLIL -> {
                if (!prayerText.contains("la ilaha illa", ignoreCase = true)) {
                    flags.add(InappropriateFlag(
                        flagType = FlagType.THEOLOGICAL_ERROR,
                        description = "Tahlil should contain the shahada phrase",
                        severity = FlagSeverity.HIGH,
                        explanation = "Essential dhikr content missing"
                    ))
                }
            }
            else -> {
                // General prayer validation
            }
        }
        
        return flags
    }
    
    private fun generatePrayerSuggestions(prayerText: String, prayerType: PrayerType): List<ContentSuggestion> {
        val suggestions = mutableListOf<ContentSuggestion>()
        
        // Add standard suggestions based on prayer type
        when (prayerType) {
            PrayerType.DUA -> {
                if (!prayerText.startsWith("Allahumma", ignoreCase = true)) {
                    suggestions.add(ContentSuggestion(
                        originalText = prayerText.take(20),
                        suggestedText = "Allahumma ${prayerText}",
                        reason = "Duas traditionally begin with 'Allahumma'",
                        severity = SuggestionSeverity.LOW,
                        category = SuggestionCategory.ISLAMIC_COMPLIANCE
                    ))
                }
            }
            else -> {
                // Other prayer types
            }
        }
        
        return suggestions
    }
    
    private fun calculateConfidenceScore(flags: List<AutomaticFlag>): Float {
        if (flags.isEmpty()) return 1.0f
        return flags.map { 1.0f - it.confidence }.average().toFloat()
    }
    
    private fun calculateIslamicComplianceScore(prayerText: String, prayerType: PrayerType): Float {
        var score = 0.8f // Base score
        
        // Adjust based on prayer type compliance
        when (prayerType) {
            PrayerType.FATIHAH, PrayerType.TAHLIL -> {
                if (prayerText.contains("bismillah", ignoreCase = true)) score += 0.1f
                if (prayerText.contains("alhamdulillah", ignoreCase = true)) score += 0.1f
            }
            PrayerType.DUA -> {
                if (prayerText.contains("allahumma", ignoreCase = true)) score += 0.1f
                if (prayerText.contains("amin", ignoreCase = true)) score += 0.1f
            }
            else -> {
                // Default scoring
            }
        }
        
        return minOf(1.0f, score)
    }
    
    private fun calculateCulturalSensitivityScore(content: String): Float {
        var score = 0.9f // Base score
        val lowerContent = content.lowercase()
        
        // Deduct for potential issues
        if (lowerContent.contains("sect") || lowerContent.contains("wrong")) score -= 0.2f
        if (lowerContent.contains("better") || lowerContent.contains("superior")) score -= 0.1f
        
        return maxOf(0.0f, score)
    }
}