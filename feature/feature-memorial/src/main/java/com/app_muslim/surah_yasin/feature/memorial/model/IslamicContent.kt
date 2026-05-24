package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class IslamicContentValidation(
    val id: String = "",
    val contentText: String = "",
    val contentType: ContentType = ContentType.MEMORIAL_DESCRIPTION,
    val language: String = "en",
    val isValidated: Boolean = false,
    val validationStatus: ValidationStatus = ValidationStatus.PENDING,
    val validatedBy: String? = null,
    val validatedAt: Date? = null,
    val validationNotes: String = "",
    val suggestedCorrections: List<ContentSuggestion> = emptyList(),
    val culturalSensitivityScore: Float = 0.0f,
    val islamicComplianceScore: Float = 0.0f,
    val inappropriateFlags: List<InappropriateFlag> = emptyList(),
    val approvedContent: String? = null,
    val rejectionReason: String? = null,
    val createdAt: Date = Date(),
    val lastReviewed: Date? = null
) : Parcelable

@Parcelize
data class ContentSuggestion(
    val originalText: String = "",
    val suggestedText: String = "",
    val reason: String = "",
    val severity: SuggestionSeverity = SuggestionSeverity.LOW,
    val category: SuggestionCategory = SuggestionCategory.LANGUAGE_IMPROVEMENT
) : Parcelable

@Parcelize
data class InappropriateFlag(
    val flagType: FlagType = FlagType.CULTURAL_INSENSITIVITY,
    val description: String = "",
    val severity: FlagSeverity = FlagSeverity.MEDIUM,
    val startPosition: Int = 0,
    val endPosition: Int = 0,
    val flaggedText: String = "",
    val explanation: String = ""
) : Parcelable

@Parcelize
data class IslamicScholarReview(
    val id: String = "",
    val contentId: String = "",
    val scholarId: String = "",
    val scholarName: String = "",
    val scholarQualifications: List<ScholarQualification> = emptyList(),
    val reviewStatus: ReviewStatus = ReviewStatus.UNDER_REVIEW,
    val islamicRuling: IslamicRuling = IslamicRuling.PERMISSIBLE,
    val detailedReview: String = "",
    val arabicReview: String = "",
    val recommendations: List<String> = emptyList(),
    val references: List<IslamicReference> = emptyList(),
    val reviewedAt: Date = Date(),
    val approvalLevel: ApprovalLevel = ApprovalLevel.CONDITIONAL
) : Parcelable

@Parcelize
data class ScholarQualification(
    val title: String = "",
    val institution: String = "",
    val specialization: String = "",
    val yearObtained: Int = 0,
    val isVerified: Boolean = false
) : Parcelable

@Parcelize
data class IslamicReference(
    val source: ReferenceSource = ReferenceSource.QURAN,
    val citation: String = "",
    val arabicText: String = "",
    val translation: String = "",
    val chapter: String? = null,
    val verse: String? = null,
    val hadithNumber: String? = null,
    val authenticity: AuthenticityLevel = AuthenticityLevel.AUTHENTIC
) : Parcelable

@Parcelize
data class CulturalContext(
    val region: IslamicRegion = IslamicRegion.GLOBAL,
    val schoolOfThought: SchoolOfThought = SchoolOfThought.SUNNI,
    val culturalTraditions: List<String> = emptyList(),
    val localCustoms: List<String> = emptyList(),
    val sensitiveTopics: List<String> = emptyList(),
    val appropriateLanguage: List<String> = emptyList(),
    val languagePreferences: LanguagePreferences = LanguagePreferences()
) : Parcelable

@Parcelize
data class LanguagePreferences(
    val primaryLanguage: String = "en",
    val secondaryLanguages: List<String> = emptyList(),
    val includeArabic: Boolean = true,
    val transliterationStyle: TransliterationStyle = TransliterationStyle.SIMPLIFIED,
    val formalityLevel: FormalityLevel = FormalityLevel.RESPECTFUL
) : Parcelable

@Parcelize
data class ContentModerationResult(
    val contentId: String = "",
    val isApproved: Boolean = false,
    val moderationLevel: ModerationLevel = ModerationLevel.AUTOMATED,
    val automaticFlags: List<AutomaticFlag> = emptyList(),
    val humanReviewRequired: Boolean = false,
    val confidenceScore: Float = 0.0f,
    val processedAt: Date = Date(),
    val moderatorId: String? = null,
    val finalDecision: ModerationDecision = ModerationDecision.PENDING
) : Parcelable

@Parcelize
data class AutomaticFlag(
    val detectionType: DetectionType = DetectionType.KEYWORD_FILTER,
    val confidence: Float = 0.0f,
    val flaggedContent: String = "",
    val reason: String = "",
    val suggestedAction: ContentSuggestedAction = ContentSuggestedAction.REVIEW_REQUIRED
) : Parcelable

enum class ContentType(val displayName: String) {
    MEMORIAL_DESCRIPTION("Memorial Description"),
    PRAYER_TEXT("Prayer Text"),
    FAMILY_MESSAGE("Family Message"),
    COMMUNITY_ANNOUNCEMENT("Community Announcement"),
    DHIKR_CONTENT("Dhikr Content"),
    DUA_SUPPLICATION("Dua Supplication"),
    QURAN_REFERENCE("Quran Reference"),
    HADITH_REFERENCE("Hadith Reference"),
    BIOGRAPHICAL_INFO("Biographical Information"),
    MEMORIAL_TITLE("Memorial Title"),
    CUSTOM_PRAYER("Custom Prayer"),
    COMMUNITY_MESSAGE("Community Message")
}

enum class ValidationStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    NEEDS_REVISION,
    SCHOLAR_REVIEW_REQUIRED,
    CULTURAL_REVIEW_REQUIRED,
    APPROVED_WITH_CONDITIONS
}

enum class SuggestionSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class SuggestionCategory {
    LANGUAGE_IMPROVEMENT,
    CULTURAL_SENSITIVITY,
    ISLAMIC_COMPLIANCE,
    GRAMMATICAL_CORRECTION,
    TERMINOLOGY_ACCURACY,
    RESPECTFUL_LANGUAGE,
    GENDER_APPROPRIATE,
    SECTARIAN_NEUTRAL
}

enum class FlagType {
    CULTURAL_INSENSITIVITY,
    RELIGIOUS_INAPPROPRIATENESS,
    GENDER_INAPPROPRIATE,
    SECTARIAN_BIAS,
    OFFENSIVE_LANGUAGE,
    FACTUAL_INACCURACY,
    INAPPROPRIATE_IMAGERY_REFERENCE,
    PRIVACY_CONCERN,
    THEOLOGICAL_ERROR,
    DISRESPECTFUL_TONE
}

enum class FlagSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    BLOCKING
}

enum class ReviewStatus {
    SUBMITTED,
    UNDER_REVIEW,
    COMPLETED,
    REQUIRES_CLARIFICATION,
    ESCALATED,
    WITHDRAWN
}

enum class IslamicRuling {
    PERMISSIBLE, // Halal/Mubah
    RECOMMENDED, // Mustahabb
    OBLIGATORY, // Wajib
    DISCOURAGED, // Makruh
    FORBIDDEN, // Haram
    NEUTRAL, // No specific ruling
    REQUIRES_CONTEXT // Depends on circumstances
}

enum class ApprovalLevel {
    CONDITIONAL,
    FULL_APPROVAL,
    REGIONAL_APPROVAL,
    TEMPORARY_APPROVAL,
    REJECTED
}

enum class ReferenceSource {
    QURAN,
    HADITH_BUKHARI,
    HADITH_MUSLIM,
    HADITH_ABU_DAWOOD,
    HADITH_TIRMIDHI,
    HADITH_NASAI,
    HADITH_IBN_MAJAH,
    HADITH_AHMAD,
    SCHOLARLY_CONSENSUS,
    MADHAB_RULING,
    CONTEMPORARY_FATWA
}

enum class AuthenticityLevel {
    AUTHENTIC, // Sahih
    GOOD, // Hasan
    WEAK, // Dhaif
    FABRICATED, // Mawdu
    DISPUTED,
    CONSENSUS // Ijma
}

enum class IslamicRegion {
    GLOBAL,
    MIDDLE_EAST,
    SOUTH_ASIA,
    SOUTHEAST_ASIA,
    AFRICA,
    EUROPE,
    NORTH_AMERICA,
    CENTRAL_ASIA,
    CUSTOM
}

enum class SchoolOfThought {
    SUNNI,
    SHIA,
    HANAFI,
    MALIKI,
    SHAFII,
    HANBALI,
    JAFARI,
    ZAIDI,
    IBADI,
    NON_DENOMINATIONAL
}

enum class TransliterationStyle {
    SIMPLIFIED,
    ACADEMIC,
    REGIONAL,
    CUSTOM
}

enum class FormalityLevel {
    CASUAL,
    RESPECTFUL,
    FORMAL,
    HIGHLY_FORMAL,
    TRADITIONAL
}

enum class ModerationLevel {
    AUTOMATED,
    HUMAN_ASSISTED,
    FULL_HUMAN_REVIEW,
    EXPERT_REVIEW,
    COMMUNITY_REVIEW
}

enum class DetectionType {
    KEYWORD_FILTER,
    SENTIMENT_ANALYSIS,
    CULTURAL_AI,
    LANGUAGE_PATTERN,
    CONTEXTUAL_ANALYSIS,
    REFERENCE_VERIFICATION
}

enum class ModerationDecision {
    PENDING,
    APPROVED,
    REJECTED,
    NEEDS_REVISION,
    ESCALATED,
    DEFERRED
}

enum class ContentSuggestedAction {
    AUTO_APPROVE,
    REVIEW_REQUIRED,
    SCHOLAR_CONSULTATION,
    COMMUNITY_INPUT,
    REJECTION_RECOMMENDED,
    CONDITIONAL_APPROVAL
}

@Parcelize
data class IslamicContentValidationRequest(
    val contentText: String = "",
    val contentType: ContentType = ContentType.MEMORIAL_DESCRIPTION,
    val userRegion: IslamicRegion = IslamicRegion.GLOBAL,
    val userSchoolOfThought: SchoolOfThought = SchoolOfThought.SUNNI,
    val language: String = "en",
    val urgencyLevel: UrgencyLevel = UrgencyLevel.NORMAL,
    val requestedValidationType: ValidationType = ValidationType.STANDARD,
    val additionalContext: String = "",
    val submittedBy: String = "",
    val submittedAt: Date = Date()
) : Parcelable

enum class UrgencyLevel {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

enum class ValidationType {
    BASIC_FILTER,
    STANDARD,
    COMPREHENSIVE,
    SCHOLAR_REVIEW,
    EXPERT_PANEL
}