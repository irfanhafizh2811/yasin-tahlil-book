package com.app_muslim.surah_yasin.utils

import com.app_muslim.surah_yasin.data.model.Memorial
import com.app_muslim.surah_yasin.data.model.IslamicRegion
import com.app_muslim.surah_yasin.data.model.SchoolOfThought

/**
 * Validator for ensuring cultural and Islamic appropriateness
 */
class CulturalValidator {

    /**
     * Validate memorial content against Islamic guidelines
     */
    fun validateMemorial(memorial: Memorial): ValidationResult {
        val issues = mutableListOf<String>()

        // Check memorial duration (Islamic tradition: typically 40 days)
        if (memorial.duration > 40) {
            issues.add("Memorial duration exceeds traditional 40-day period")
        }

        // Check name appropriateness (basic validation)
        if (memorial.name.isBlank()) {
            issues.add("Memorial name cannot be empty")
        }

        // Validate memorial description for appropriate content
        memorial.description?.let { description ->
            if (description.contains(Regex("\\b(forbidden|haram)\\b", RegexOption.IGNORE_CASE))) {
                issues.add("Memorial description contains inappropriate content")
            }
        }

        return if (issues.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(issues)
        }
    }

    /**
     * Validate prayer content and appropriateness
     */
    fun validatePrayerContent(arabicText: String, transliteration: String): ValidationResult {
        val issues = mutableListOf<String>()

        // Basic validation for Arabic text
        if (arabicText.isBlank()) {
            issues.add("Arabic text is required for prayers")
        }

        // Check for proper Arabic characters
        if (arabicText.isNotEmpty() && !arabicText.matches(Regex(".*[\\u0600-\\u06FF].*"))) {
            issues.add("Arabic text should contain Arabic characters")
        }

        return if (issues.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(issues)
        }
    }

    /**
     * Get culturally appropriate greeting based on region and school of thought
     */
    fun getAppropriateGreeting(region: IslamicRegion, schoolOfThought: SchoolOfThought): String {
        return when (region) {
            IslamicRegion.MIDDLE_EAST -> "السلام عليكم"
            IslamicRegion.SOUTH_ASIA -> when (schoolOfThought) {
                SchoolOfThought.HANAFI -> "آداب"
                else -> "السلام عليكم"
            }
            IslamicRegion.SOUTHEAST_ASIA -> "السلام عليكم"
            else -> "Peace be upon you"
        }
    }

    /**
     * Validate cultural preferences
     */
    fun validateCulturalSettings(
        region: IslamicRegion,
        schoolOfThought: SchoolOfThought,
        languages: List<String>
    ): ValidationResult {
        val issues = mutableListOf<String>()

        // Check if region and school of thought are compatible
        if (region != IslamicRegion.NOT_SPECIFIED && 
            schoolOfThought != SchoolOfThought.NOT_SPECIFIED) {
            
            val compatibleSchools = getCompatibleSchools(region)
            if (schoolOfThought !in compatibleSchools) {
                issues.add("Selected school of thought may not be common in the selected region")
            }
        }

        return if (issues.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(issues)
        }
    }

    private fun getCompatibleSchools(region: IslamicRegion): List<SchoolOfThought> {
        return when (region) {
            IslamicRegion.MIDDLE_EAST -> listOf(
                SchoolOfThought.HANBALI, 
                SchoolOfThought.SHAFI,
                SchoolOfThought.JAFARI
            )
            IslamicRegion.SOUTH_ASIA -> listOf(
                SchoolOfThought.HANAFI,
                SchoolOfThought.JAFARI
            )
            IslamicRegion.SOUTHEAST_ASIA -> listOf(
                SchoolOfThought.SHAFI
            )
            IslamicRegion.NORTH_AFRICA -> listOf(
                SchoolOfThought.MALIKI
            )
            else -> SchoolOfThought.values().toList()
        }
    }
}

/**
 * Result of cultural validation
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val issues: List<String>) : ValidationResult()
    
    fun isValid(): Boolean = this is Valid
}