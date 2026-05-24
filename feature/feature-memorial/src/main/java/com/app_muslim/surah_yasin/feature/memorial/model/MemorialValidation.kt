package com.app_muslim.surah_yasin.feature.memorial.model

import java.util.*

data class MemorialValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)

enum class ValidationError(val message: String) {
    DECEASED_NAME_EMPTY("Deceased name is required"),
    DECEASED_NAME_TOO_SHORT("Deceased name must be at least 2 characters"),
    DECEASED_NAME_TOO_LONG("Deceased name must be less than 100 characters"),
    MEMORIAL_MESSAGE_TOO_LONG("Memorial message must be less than 1000 characters"),
    INVALID_DATE_OF_DEATH("Date of death cannot be in the future"),
    DATE_TOO_OLD("Date of death cannot be more than 100 years ago"),
    ARABIC_TEXT_INVALID("Arabic text contains invalid characters"),
    FAMILY_MEMBERS_LIMIT("Maximum 20 family members allowed"),
    INAPPROPRIATE_CONTENT("Content may contain inappropriate material"),
    NETWORK_ERROR("Please check your internet connection"),
    PERMISSION_DENIED("You don't have permission to create this memorial"),
    STORAGE_ERROR("Failed to upload memorial photo")
}

object MemorialValidator {
    
    fun validateMemorial(memorial: MemorialData): MemorialValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        // Validate deceased name
        when {
            memorial.deceasedName.isBlank() -> errors.add(ValidationError.DECEASED_NAME_EMPTY)
            memorial.deceasedName.length < 2 -> errors.add(ValidationError.DECEASED_NAME_TOO_SHORT)
            memorial.deceasedName.length > 100 -> errors.add(ValidationError.DECEASED_NAME_TOO_LONG)
        }
        
        // Validate memorial message
        if (memorial.memorialMessage.length > 1000) {
            errors.add(ValidationError.MEMORIAL_MESSAGE_TOO_LONG)
        }
        
        // Validate date of death
        val now = Date()
        val oneHundredYearsAgo = Calendar.getInstance().apply {
            time = now
            add(Calendar.YEAR, -100)
        }.time
        
        when {
            memorial.dateOfDeath.after(now) -> errors.add(ValidationError.INVALID_DATE_OF_DEATH)
            memorial.dateOfDeath.before(oneHundredYearsAgo) -> errors.add(ValidationError.DATE_TOO_OLD)
        }
        
        // Validate Arabic text if provided
        memorial.deceasedNameArabic?.let { arabicName ->
            if (!isValidArabicText(arabicName)) {
                errors.add(ValidationError.ARABIC_TEXT_INVALID)
            }
        }
        
        memorial.memorialMessageArabic?.let { arabicMessage ->
            if (!isValidArabicText(arabicMessage)) {
                errors.add(ValidationError.ARABIC_TEXT_INVALID)
            }
        }
        
        // Validate family members limit
        if (memorial.familyMembers.size > 20) {
            errors.add(ValidationError.FAMILY_MEMBERS_LIMIT)
        }
        
        // Content appropriateness check (basic implementation)
        if (containsInappropriateContent(memorial.memorialMessage) || 
            containsInappropriateContent(memorial.deceasedName)) {
            errors.add(ValidationError.INAPPROPRIATE_CONTENT)
        }
        
        return MemorialValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun isValidArabicText(text: String): Boolean {
        // Arabic Unicode range: U+0600 to U+06FF
        // Arabic Supplement: U+0750 to U+077F
        // Arabic Extended-A: U+08A0 to U+08FF
        val arabicRegex = Regex("^[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\s\\p{P}\\p{N}]*\$")
        return text.matches(arabicRegex)
    }
    
    private fun containsInappropriateContent(text: String): Boolean {
        // Basic implementation - in production, this should use ML Kit or similar service
        val inappropriateWords = listOf<String>(
            // Add inappropriate words in multiple languages
            // This is a simplified implementation
        )
        
        val lowerText = text.lowercase()
        return inappropriateWords.any { word: String -> lowerText.contains(word, ignoreCase = true) }
    }
    
    fun getIslamicDateValidation(dateOfDeath: Date): Boolean {
        // Ensure date respects Islamic calendar considerations
        val calendar = Calendar.getInstance()
        calendar.time = dateOfDeath
        
        // Basic validation - date should not be on a Friday prayer time
        // In production, this should integrate with proper Islamic calendar
        return true
    }
}

object HijriCalendarConverter {
    
    fun gregorianToHijri(gregorianDate: Date): HijriDate {
        // Simplified conversion - in production, use proper Hijri calendar library
        val calendar = Calendar.getInstance()
        calendar.time = gregorianDate
        
        val gregorianYear = calendar.get(Calendar.YEAR)
        val gregorianMonth = calendar.get(Calendar.MONTH) + 1
        val gregorianDay = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Approximate conversion (actual conversion requires complex algorithms)
        val hijriYear = ((gregorianYear - 622) * 33 / 32) + 1
        val hijriMonth = gregorianMonth
        val hijriDay = gregorianDay
        
        val monthNames = arrayOf(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )
        
        return HijriDate(
            year = hijriYear,
            month = hijriMonth,
            day = hijriDay,
            monthName = if (hijriMonth in 1..12) monthNames[hijriMonth - 1] else "",
            yearName = "${hijriYear}H"
        )
    }
    
    fun hijriToGregorian(hijriDate: HijriDate): Date {
        // Simplified conversion - in production, use proper Hijri calendar library
        val gregorianYear = ((hijriDate.year * 32 / 33) + 622)
        
        val calendar = Calendar.getInstance()
        calendar.set(gregorianYear, hijriDate.month - 1, hijriDate.day)
        
        return calendar.time
    }
}