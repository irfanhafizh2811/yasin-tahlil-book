package com.app_muslim.surah_yasin.core.ui.validation

/**
 * Shared validation types and enums for the Islamic platform
 */

// Content and validation types
enum class ContentType {
    MEMORIAL_MESSAGE,
    PRAYER_TEXT,
    COMMUNITY_POST,
    GENERAL
}

enum class ValidationSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

// Regional and cultural types
enum class IslamicRegion {
    MIDDLE_EAST,
    SOUTH_ASIA,
    SOUTHEAST_ASIA,
    NORTH_AFRICA,
    WEST_AFRICA,
    EUROPE,
    AMERICA,
    SUB_SAHARAN_AFRICA,
    NORTH_AMERICA,
    CENTRAL_ASIA
}

enum class SchoolOfThought {
    HANAFI,
    SHAFII,
    MALIKI,
    HANBALI,
    SHIA_JAFARI,
    GENERAL_SUNNI,
    UNIVERSAL,
    SHAFI,
    JAFARI
}

// Gender and interaction types
enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN
}

enum class InteractionContext {
    MEMORIAL_CONDOLENCE,
    PRAYER_SUPPORT,
    COMMUNITY_HELP,
    GENERAL
}

// Risk and priority types
enum class RiskLevel {
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN
}

enum class RecommendationPriority {
    LOW,
    MEDIUM,
    HIGH
}

// Violation and warning types
enum class ViolationType {
    INAPPROPRIATE_GENDER_INTERACTION,
    DISRESPECTFUL_COMMUNICATION,
    DIVISIVE_LANGUAGE,
    HARASSMENT,
    INCORRECT_PRAYER_TEXT,
    INAPPROPRIATE_SEQUENCE,
    CULTURAL_INAPPROPRIATENESS,
    SCHOOL_INCOMPATIBILITY,
    UNRECOGNIZED_PRAYER
}

enum class ViolationSeverity {
    HIGH,
    MODERATE,
    LOW,
    CRITICAL
}

enum class WarningCategory {
    ISLAMIC_CONTENT,
    CULTURAL_SENSITIVITY,
    COMMUNITY_ENGAGEMENT,
    RESPECTFUL_LANGUAGE,
    CONTEXTUAL_APPROPRIATENESS,
    SCHOOL_PREFERENCE,
    REGIONAL_CUSTOM,
    TEXT_ACCURACY,
    SEQUENCE_ORDER,
    REGIONAL_COMPATIBILITY,
    MEMORIAL_CUSTOM,
    SEQUENCE_STRUCTURE,
    PRAYER_INCLUSION,
    PRAYER_ORDER,
    COMMUNITY_PROTOCOL,
    COMMUNITY_APPROACH
}

// Issue types
enum class IssueType {
    DISRESPECTFUL_LANGUAGE,
    PRIVACY_VIOLATION,
    DIGNITY_VIOLATION,
    INAPPROPRIATE_DETAILS
}

enum class IssueSeverity {
    CRITICAL,
    HIGH,
    MODERATE,
    LOW
}

// Flag and moderation types
enum class FlagCategory {
    CULTURAL_REVIEW,
    FACT_CHECK,
    HUMAN_REVIEW,
    SPAM_CHECK,
    AUTHOR_RISK
}

enum class ModerationSeverity {
    CRITICAL,
    HIGH,
    MODERATE,
    LOW
}

enum class ModerationAction {
    IMMEDIATE_REMOVAL,
    REQUIRE_REVISION,
    REMOVE_AND_WARNING,
    TEMPORARY_SUSPENSION,
    IMMEDIATE_BAN,
    IMMEDIATE_REPORT,
    CONTENT_REMOVAL,
    FACT_CHECK_FLAG
}

// Prohibition categories
enum class ProhibitionCategory {
    RELIGIOUS_VIOLATION,
    INAPPROPRIATE_MEMORIAL,
    HARASSMENT_ABUSE,
    HATE_SPEECH,
    SPAM_COMMERCIAL,
    PRIVACY_VIOLATION,
    MISINFORMATION,
    EXPLICIT_CONTENT,
    HARASSMENT,
    INAPPROPRIATE_SEXUAL,
    VIOLENCE_INCITEMENT
}

// Prayer and cultural types
enum class PrayerType {
    TAHLIL,
    FATIHAH,
    YASIN,
    ISTIGHFAR,
    SALAWAT,
    DUA_KHATM,
    DUA_GENERAL,
    DHIKR_GENERAL
}

enum class PrayerContext {
    MEMORIAL_SERVICE,
    COMMUNITY_GATHERING,
    INDIVIDUAL_PRAYER,
    FAMILY_PRAYER,
    SPECIAL_OCCASION,
    DAILY_DHIKR,
    MEMORIAL,
    DHIKR,
    DAILY,
    RECITATION,
    FORMAL_PRAYER,
    REPENTANCE,
    BLESSING
}

enum class PrayerDuration {
    SHORT,
    STANDARD,
    EXTENDED
}

// Cultural custom types
enum class PhotoPolicy {
    PROHIBITED,
    RESTRICTED,
    MODERATE,
    LIBERAL,
    ALLOWED
}

enum class SharingPolicy {
    PRIVATE_ONLY,
    FAMILY_ONLY,
    EXTENDED_FAMILY,
    COMMUNITY_WIDE,
    PUBLIC_WITH_PRIVACY,
    PUBLIC
}

enum class ParticipationLevel {
    INDIVIDUAL,
    FAMILY,
    COMMUNITY,
    REGIONAL,
    FAMILY_SEGREGATED,
    EQUAL_PARTICIPATION,
    EXTENDED_FAMILY,
    LIBERAL,
    PUBLIC_WITH_PRIVACY,
    FORBIDDEN,
    MODERATE
}

enum class CulturalViolationType {
    INAPPROPRIATE_PHOTO_SHARING,
    INVALID_MEMORIAL_DURATION,
    GENDER_INTERACTION_VIOLATION,
    FAMILY_CUSTOM_VIOLATION,
    RELIGIOUS_CUSTOM_VIOLATION,
    MEMORIAL_DURATION,
    PHOTO_USAGE,
    SHARING_LEVEL,
    MEMORIAL_PHRASES,
    GENDER_INTERACTION,
    LANGUAGE_USAGE,
    COMMUNITY_PRAYER,
    PRAYER_LEADERSHIP,
    PRAYER_TIMING,
    AUDIO_VISUAL
}

// Community and gathering types
enum class CommunityGatheringStyle {
    MIXED_GENDER,
    SEGREGATED,
    FAMILY_ONLY,
    MEN_ONLY,
    WOMEN_ONLY,
    GENDER_SEGREGATED,
    MIXED_WITH_RESPECT,
    FAMILY_FOCUSED,
    COMMUNITY_WIDE
}

enum class LanguageUsage {
    ARABIC_ONLY,
    ARABIC_WITH_TRANSLATION,
    LOCAL_LANGUAGE,
    MIXED_LANGUAGES,
    ARABIC_PRIMARILY,
    BILINGUAL,
    MULTILINGUAL,
    ARABIC_WITH_LOCAL
}

// Recommendation types
enum class RecommendationCategory {
    PRAYER_IMPROVEMENT,
    SEQUENCE_OPTIMIZATION,
    CULTURAL_ADAPTATION,
    SCHOOL_ALIGNMENT,
    SCHOOL_PREFERENCE,
    REGIONAL_CUSTOM,
    PRAYER_INCLUSION,
    PRAYER_ORDER,
    COMMUNITY_APPROACH
}

// Calendar and holiday types
enum class HijriMonth {
    MUHARRAM,
    SAFAR,
    RABIULAWAL,
    RABIULAKHIR,
    JUMADALAWAL,
    JUMADALAKHIR,
    RAJAB,
    SHAABAN,
    RAMADAN,
    SHAWWAL,
    DHULQADAH,
    DHULHIJJAH
}

enum class IslamicHoliday {
    RAMADAN,
    EID_FITR,
    EID_ADHA,
    ASHURA,
    MAWLID_NABAWI,
    LAYLAT_QADR,
    LAYLAT_BARA,
    HAJJ_SEASON
}

// Scholar validation types
enum class ScholarValidationStatus {
    APPROVED,
    PENDING_REVIEW,
    REQUIRES_MODIFICATION,
    REJECTED,
    CONDITIONAL_APPROVAL,
    PENDING,
    UNDER_REVIEW,
    REQUIRES_REVISION,
    INSUFFICIENT_REVIEWS,
    MIXED
}

enum class ScholarSpecialization {
    QURAN_TAFSIR,
    HADITH_STUDIES,
    FIQH_JURISPRUDENCE,
    ISLAMIC_HISTORY,
    ARABIC_LINGUISTICS,
    COMPARATIVE_RELIGION,
    ISLAMIC_PHILOSOPHY,
    MEMORIAL_PRACTICES,
    PRAYER_TRADITIONS,
    CULTURAL_STUDIES,
    GENERAL_ISLAMIC_STUDIES,
    ISLAMIC_JURISPRUDENCE,
    QURANIC_STUDIES,
    ARABIC_LANGUAGE,
    MEMORIAL_CONTENT,
    ISLAMIC_ETHICS
}

enum class ScholarValidationContentType {
    PRAYER_TEXT,
    MEMORIAL_MESSAGE,
    RELIGIOUS_GUIDANCE,
    CULTURAL_PRACTICE,
    QURANIC_VERSE,
    MEMORIAL_CONTENT,
    GENERAL_ISLAMIC
}

enum class ScholarDecision {
    APPROVE,
    REJECT,
    MODIFY,
    SEEK_SECOND_OPINION,
    APPROVED,
    REJECTED,
    MIXED
}

enum class ScholarConfidence {
    HIGH,
    MEDIUM,
    LOW
}

enum class ScholarConsensus {
    UNANIMOUS,
    MAJORITY,
    SPLIT,
    PENDING,
    APPROVED,
    REJECTED,
    MIXED,
    INSUFFICIENT_REVIEWS
}

enum class ScholarValidationPriority {
    URGENT,
    HIGH,
    NORMAL,
    LOW
}

// Translation validation types
enum class TranslationStatus {
    ACCURATE,
    NEEDS_IMPROVEMENT,
    INACCURATE,
    CULTURALLY_INAPPROPRIATE,
    PENDING
}

enum class TranslationContentType {
    PRAYER_TRANSLATION,
    MEMORIAL_TEXT,
    RELIGIOUS_INSTRUCTION,
    CULTURAL_GUIDANCE,
    GENERAL_CONTENT,
    PRAYER,
    MEMORIAL_MESSAGE,
    QURANIC_VERSE,
    GENERAL_ISLAMIC
}

enum class TranslationSpecialization {
    ARABIC_ENGLISH,
    ARABIC_INDONESIAN,
    ARABIC_URDU,
    ARABIC_TURKISH,
    ARABIC_MALAY,
    MULTILINGUAL
}

enum class TermCategory {
    RELIGIOUS_TERM,
    CULTURAL_REFERENCE,
    PRAYER_TERMINOLOGY,
    MEMORIAL_PHRASE,
    GENERAL_VOCABULARY,
    DIVINE_NAME,
    PROPHET_NAME,
    WORSHIP_TERM,
    CRITICAL_TERM
}

enum class IssueCategory {
    TRANSLATION_ACCURACY,
    CULTURAL_SENSITIVITY,
    RELIGIOUS_AUTHENTICITY,
    LINGUISTIC_APPROPRIATENESS,
    RELIGIOUS_APPROPRIATENESS,
    FLUENCY,
    CONSISTENCY,
    QURANIC_ACCURACY,
    TERMINOLOGY,
    CRITICAL_TERM
}

enum class SuggestionCategory {
    WORD_CHOICE,
    PHRASE_STRUCTURE,
    CULTURAL_ADAPTATION,
    RELIGIOUS_PRECISION,
    CONTENT_ENHANCEMENT,
    EXPERT_REVIEW,
    CULTURAL_PRESERVATION,
    TERMINOLOGY
}

enum class TranslationPriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    URGENT,
    NORMAL
}

enum class CulturalValidationContext {
    MEMORIAL_SERVICE,
    DAILY_PRAYER,
    COMMUNITY_GATHERING,
    EDUCATIONAL_CONTENT
}

// Compatibility and appropriateness types
enum class CompatibilityLevel {
    HIGHLY_COMPATIBLE,
    MODERATELY_COMPATIBLE,
    PARTIALLY_COMPATIBLE,
    NOT_COMPATIBLE,
    UNKNOWN
}

enum class AppropriatenessLevel {
    HIGHLY_APPROPRIATE,
    MODERATELY_APPROPRIATE,
    PARTIALLY_APPROPRIATE,
    NOT_APPROPRIATE,
    UNKNOWN
}

// Additional enum types for cultural validation
enum class CulturalCategory {
    MEMORIAL_PRACTICES,
    PRAYER_CUSTOMS,
    FAMILY_VALUES,
    COMMUNITY_INTERACTION,
    GENDER_GUIDELINES,
    RELIGIOUS_OBSERVANCE,
    SHARING_LEVEL,
    MEMORIAL_PHRASES,
    GENDER_INTERACTION,
    LANGUAGE_USAGE,
    COMMUNITY_PRAYER,
    PRAYER_LEADERSHIP,
    PRAYER_TIMING,
    AUDIO_VISUAL
}

enum class WarningSeverity {
    LOW,
    MEDIUM,
    MODERATE,
    HIGH
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

enum class CulturalContentType {
    MEMORIAL,
    PRAYER
}

enum class GenderMixLevel {
    MALE_ONLY,
    FEMALE_ONLY,
    SEGREGATED,
    MIXED_WITH_SUPERVISION,
    FULLY_MIXED
}

// Compatibility and appropriateness result types
enum class SchoolCompatibilityResult {
    HIGHLY_COMPATIBLE,
    MODERATELY_COMPATIBLE,
    PARTIALLY_COMPATIBLE,
    NOT_COMPATIBLE,
    UNKNOWN
}

enum class RegionalAppropriatenessResult {
    HIGHLY_APPROPRIATE,
    MODERATELY_APPROPRIATE,
    PARTIALLY_APPROPRIATE,
    NOT_APPROPRIATE,
    UNKNOWN
}