package com.app_muslim.surah_yasin.core.ui.islamic

/**
 * Main export file for Islamic typography components
 * 
 * This file provides easy access to all Islamic typography components
 * developed for the Tahlil platform with proper Arabic text support,
 * RTL formatting, and accessibility features.
 */

/**
 * Islamic Typography Components Export
 * 
 * Available components:
 * - ArabicPrayerText: Main Arabic text display with RTL support
 * - CompletePrayerDisplay: Full prayer card with Arabic, transliteration, translation
 * - BiDiText: Bidirectional text with automatic RTL detection
 * - MixedContentText: Mixed Arabic/Latin content
 * - MultiLanguageTranslation: Multi-language translation display
 * - PrayerContent: Data class for prayer content
 * - ScalableIslamicText: Accessibility-enhanced scalable text
 * - ZoomableTextDisplay: Text with zoom controls
 */

/**
 * Default prayer content for testing and examples
 */
object SamplePrayerContent {
    val BISMILLAH = PrayerContent(
        arabic = "بِسْمِ ٱللَّٰهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
        transliteration = "Bismillahi rahmani raheem",
        translations = mapOf(
            "en" to "In the name of Allah, the Most Gracious, the Most Merciful",
            "id" to "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang",
            "ms" to "Dengan nama Allah Yang Maha Pemurah lagi Maha Mengasihani",
            "tr" to "Rahman ve Rahim olan Allah'ın adıyla",
            "ur" to "اللہ کے نام سے جو نہایت مہربان رحم والا ہے"
        ),
        source = "Quran 1:1",
        category = "Opening"
    )
    
    val TAHLIL = PrayerContent(
        arabic = "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ",
        transliteration = "La ilaha illa Allah",
        translations = mapOf(
            "en" to "There is no god but Allah",
            "id" to "Tiada tuhan selain Allah",
            "ms" to "Tiada tuhan melainkan Allah",
            "tr" to "Allah'tan başka ilah yoktur",
            "ur" to "اللہ کے سوا کوئی معبود نہیں"
        ),
        source = "Islamic Declaration",
        category = "Tahlil"
    )
    
    val FATIHAH_EXCERPT = PrayerContent(
        arabic = "ٱلْحَمْدُ لِلَّٰهِ رَبِّ ٱلْعَالَمِينَ",
        transliteration = "Alhamdu lillahi rabbil alameen",
        translations = mapOf(
            "en" to "All praise is due to Allah, Lord of the worlds",
            "id" to "Segala puji bagi Allah, Tuhan semesta alam",
            "ms" to "Segala puji bagi Allah, Pemelihara alam semesta",
            "tr" to "Hamd, alemlerin Rabbi Allah'a mahsustur",
            "ur" to "تمام تعریفیں اللہ کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے"
        ),
        source = "Quran 1:2",
        category = "Al-Fatihah"
    )
}

/**
 * Typography preset configurations for different use cases
 */
object IslamicTypographyPresets {
    // Preset for prayer display screens
    val PRAYER_DISPLAY = TextSizeVariant.Medium
    
    // Preset for memorial cards
    val MEMORIAL_CARD = TextSizeVariant.Small
    
    // Preset for detailed prayer view
    val DETAILED_VIEW = TextSizeVariant.Large
    
    // Default language preferences
    val DEFAULT_LANGUAGES = listOf("en", "id", "ms", "ar", "tr", "ur")
    
    // Font scale factors for different contexts
    object ScaleFactors {
        const val COMPACT = 0.8f
        const val NORMAL = 1.0f
        const val COMFORTABLE = 1.2f
        const val LARGE = 1.5f
        const val EXTRA_LARGE = 2.0f
    }
}

/**
 * Utility functions for Islamic typography
 */
object IslamicTypographyUtils {
    /**
     * Check if a string contains Arabic text
     */
    fun String.isArabic(): Boolean = containsArabic()
    
    /**
     * Get appropriate text direction for content
     */
    fun String.getTextDirection() = getDominantDirection()
    
    /**
     * Extract Arabic text from mixed content
     */
    fun String.extractArabicText(): String {
        return this.filter { char ->
            char.code in 0x0600..0x06FF || // Arabic block
            char.code in 0x0750..0x077F || // Arabic Supplement
            char.code in 0x08A0..0x08FF    // Arabic Extended-A
        }
    }
    
    /**
     * Clean Arabic text for display
     */
    fun String.cleanArabicText(): String {
        return this.trim()
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .replace(Regex("[\\u200B\\u200C\\u200D]"), "") // Remove zero-width characters
    }
}

/**
 * Theme integration helpers
 */
object IslamicThemeHelpers {
    /**
     * Get appropriate text color based on content type
     */
    fun getTextColor(
        contentType: PrayerContentType,
        isDarkTheme: Boolean
    ): androidx.compose.ui.graphics.Color {
        return when (contentType) {
            PrayerContentType.ARABIC -> if (isDarkTheme) {
                androidx.compose.ui.graphics.Color(0xFFE8F5E8)
            } else {
                androidx.compose.ui.graphics.Color(0xFF1B5E20)
            }
            PrayerContentType.TRANSLITERATION -> if (isDarkTheme) {
                androidx.compose.ui.graphics.Color(0xFFB0BEC5)
            } else {
                androidx.compose.ui.graphics.Color(0xFF546E7A)
            }
            PrayerContentType.TRANSLATION -> if (isDarkTheme) {
                androidx.compose.ui.graphics.Color(0xFFE0E0E0)
            } else {
                androidx.compose.ui.graphics.Color(0xFF424242)
            }
        }
    }
}

/**
 * Prayer content types for styling
 */
enum class PrayerContentType {
    ARABIC, TRANSLITERATION, TRANSLATION
}