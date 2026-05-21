package com.app_muslim.surah_yasin.utils

import java.util.*

/**
 * Utility class for Islamic date calculations and memorial periods
 */
class IslamicDateCalculator {

    companion object {
        // Traditional Islamic memorial periods
        const val THREE_DAY_PERIOD = 3
        const val SEVEN_DAY_PERIOD = 7
        const val FORTY_DAY_PERIOD = 40
        const val ONE_YEAR_PERIOD = 365
    }

    /**
     * Calculate the end date for a memorial based on Islamic tradition
     */
    fun calculateMemorialEndDate(startDate: Date, durationDays: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_YEAR, durationDays)
        return calendar.time
    }

    /**
     * Get the traditional memorial period recommendations
     */
    fun getTraditionalMemorialPeriods(): List<MemorialPeriod> {
        return listOf(
            MemorialPeriod(THREE_DAY_PERIOD, "3 Days", "Short-term remembrance period"),
            MemorialPeriod(SEVEN_DAY_PERIOD, "7 Days", "Week-long memorial period"),
            MemorialPeriod(FORTY_DAY_PERIOD, "40 Days", "Traditional Islamic memorial period"),
            MemorialPeriod(ONE_YEAR_PERIOD, "1 Year", "Annual remembrance")
        )
    }

    /**
     * Check if a memorial is within the traditional 40-day period
     */
    fun isWithinTraditionalPeriod(startDate: Date, currentDate: Date = Date()): Boolean {
        val daysDifference = getDaysDifference(startDate, currentDate)
        return daysDifference <= FORTY_DAY_PERIOD
    }

    /**
     * Calculate remaining days in memorial period
     */
    fun getRemainingDays(startDate: Date, durationDays: Int, currentDate: Date = Date()): Int {
        val endDate = calculateMemorialEndDate(startDate, durationDays)
        val remaining = getDaysDifference(currentDate, endDate)
        return maxOf(0, remaining)
    }

    /**
     * Get days elapsed since memorial started
     */
    fun getDaysElapsed(startDate: Date, currentDate: Date = Date()): Int {
        return getDaysDifference(startDate, currentDate)
    }

    /**
     * Check if memorial has expired
     */
    fun hasMemorialExpired(startDate: Date, durationDays: Int, currentDate: Date = Date()): Boolean {
        val endDate = calculateMemorialEndDate(startDate, durationDays)
        return currentDate.after(endDate)
    }

    /**
     * Get the progress percentage of memorial period
     */
    fun getMemorialProgress(startDate: Date, durationDays: Int, currentDate: Date = Date()): Float {
        val elapsed = getDaysElapsed(startDate, currentDate)
        return (elapsed.toFloat() / durationDays.toFloat() * 100f).coerceIn(0f, 100f)
    }

    /**
     * Get recommended prayer frequency based on memorial phase
     */
    fun getRecommendedPrayerFrequency(startDate: Date, currentDate: Date = Date()): PrayerFrequency {
        val daysElapsed = getDaysElapsed(startDate, currentDate)
        
        return when {
            daysElapsed <= 3 -> PrayerFrequency.MULTIPLE_DAILY // First 3 days
            daysElapsed <= 7 -> PrayerFrequency.DAILY // First week
            daysElapsed <= 40 -> PrayerFrequency.EVERY_FEW_DAYS // Traditional period
            else -> PrayerFrequency.WEEKLY // After 40 days
        }
    }

    /**
     * Calculate Islamic special dates (simplified)
     */
    fun getIslamicSpecialDates(year: Int): Map<String, Date> {
        // This is a simplified version - in practice, you'd use proper Hijri calendar
        // calculations or an Islamic calendar library
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        
        return mapOf(
            "Ramadan Start" to getEstimatedDate(calendar, Calendar.APRIL, 1),
            "Eid al-Fitr" to getEstimatedDate(calendar, Calendar.MAY, 1),
            "Eid al-Adha" to getEstimatedDate(calendar, Calendar.JULY, 10),
            "Ashura" to getEstimatedDate(calendar, Calendar.AUGUST, 20)
        )
    }

    private fun getDaysDifference(startDate: Date, endDate: Date): Int {
        val diffInMillis = endDate.time - startDate.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun getEstimatedDate(calendar: Calendar, month: Int, day: Int): Date {
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar.time
    }
}

/**
 * Data class representing a memorial period option
 */
data class MemorialPeriod(
    val days: Int,
    val displayName: String,
    val description: String
)

/**
 * Enum for recommended prayer frequencies
 */
enum class PrayerFrequency(val displayName: String, val description: String) {
    MULTIPLE_DAILY("Multiple Daily", "Several times throughout the day"),
    DAILY("Daily", "Once per day"),
    EVERY_FEW_DAYS("Every Few Days", "2-3 times per week"),
    WEEKLY("Weekly", "Once per week"),
    MONTHLY("Monthly", "Once per month")
}