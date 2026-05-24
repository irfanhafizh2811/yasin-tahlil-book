package com.app_muslim.surah_yasin.core.common.model

/**
 * Islamic regions for cultural preferences
 */
enum class IslamicRegion(val displayName: String) {
    NOT_SPECIFIED("Not Specified"),
    SOUTHEAST_ASIA("Southeast Asia"),
    MIDDLE_EAST("Middle East"),
    NORTH_AFRICA("North Africa"),
    SUB_SAHARAN_AFRICA("Sub-Saharan Africa"),
    SOUTH_ASIA("South Asia"),
    CENTRAL_ASIA("Central Asia"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    OCEANIA("Oceania")
}

/**
 * Islamic schools of thought
 */
enum class SchoolOfThought(val displayName: String, val arabicName: String) {
    NOT_SPECIFIED("Not Specified", "غير محدد"),
    HANAFI("Hanafi", "حنفي"),
    MALIKI("Maliki", "مالكي"),
    SHAFI("Shafi'i", "شافعي"),
    HANBALI("Hanbali", "حنبلي"),
    JAFARI("Ja'fari", "جعفري"),
    OTHER("Other", "أخرى")
}

/**
 * Cultural preferences for Islamic users
 */
data class CulturalPreferences(
    val region: IslamicRegion = IslamicRegion.NOT_SPECIFIED,
    val country: String = "",
    val primaryLanguage: String = "en",
    val secondaryLanguages: List<String> = emptyList(),
    val schoolOfThought: SchoolOfThought = SchoolOfThought.NOT_SPECIFIED,
    val showArabicText: Boolean = true,
    val showTransliteration: Boolean = true
) {
    
    fun getSupportedLanguages(): List<String> {
        return when (region) {
            IslamicRegion.SOUTHEAST_ASIA -> listOf("id", "ms", "th", "tl", "en")
            IslamicRegion.MIDDLE_EAST -> listOf("ar", "tr", "fa", "en")
            IslamicRegion.SOUTH_ASIA -> listOf("ur", "hi", "bn", "en")
            IslamicRegion.NORTH_AFRICA -> listOf("ar", "fr", "en")
            IslamicRegion.SUB_SAHARAN_AFRICA -> listOf("sw", "ha", "fr", "en")
            IslamicRegion.CENTRAL_ASIA -> listOf("uz", "kk", "ky", "ru", "en")
            IslamicRegion.EUROPE -> listOf("en", "fr", "de", "tr")
            IslamicRegion.NORTH_AMERICA -> listOf("en", "es", "fr")
            IslamicRegion.SOUTH_AMERICA -> listOf("es", "pt", "en")
            IslamicRegion.OCEANIA -> listOf("en", "id", "ms")
            IslamicRegion.NOT_SPECIFIED -> listOf("en", "ar")
        }
    }
}

/**
 * Extension functions for region-specific functionality
 */
fun IslamicRegion.getDefaultLanguage(): String {
    return when (this) {
        IslamicRegion.SOUTHEAST_ASIA -> "id"
        IslamicRegion.MIDDLE_EAST -> "ar"
        IslamicRegion.SOUTH_ASIA -> "ur"
        IslamicRegion.NORTH_AFRICA -> "ar"
        IslamicRegion.SUB_SAHARAN_AFRICA -> "sw"
        IslamicRegion.CENTRAL_ASIA -> "uz"
        IslamicRegion.EUROPE -> "en"
        IslamicRegion.NORTH_AMERICA -> "en"
        IslamicRegion.SOUTH_AMERICA -> "es"
        IslamicRegion.OCEANIA -> "en"
        IslamicRegion.NOT_SPECIFIED -> "en"
    }
}

fun SchoolOfThought.isSpecified(): Boolean {
    return this != SchoolOfThought.NOT_SPECIFIED
}