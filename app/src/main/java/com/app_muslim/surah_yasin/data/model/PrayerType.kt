package com.app_muslim.surah_yasin.data.model

/**
 * Represents different types of Islamic prayers and remembrance
 */
enum class PrayerType(
    val displayName: String,
    val arabicName: String,
    val defaultCount: Int,
    val description: String,
    val category: PrayerCategory
) {
    // Memorial-focused prayers removed individual counters
    
    // Dhikr (Remembrance)
    LA_ILAHA_ILLA_ALLAH(
        "La ilaha illa Allah",
        "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ",
        100,
        "There is no god except Allah",
        PrayerCategory.DHIKR
    ),
    
    ASTAGHFIRULLAH(
        "Astaghfirullah",
        "أَسْتَغْفِرُ ٱللَّٰهَ",
        100,
        "I seek forgiveness from Allah",
        PrayerCategory.DHIKR
    ),
    
    LA_HAWLA_WALA_QUWWATA(
        "La hawla wa la quwwata illa billah",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِٱللَّٰهِ",
        100,
        "There is no power except with Allah",
        PrayerCategory.DHIKR
    ),
    
    // Salawat (Blessings on Prophet)
    SALAWAT_IBRAHIM(
        "Salawat Ibrahimiyyah",
        "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
        100,
        "O Allah, send blessings upon Muhammad",
        PrayerCategory.SALAWAT
    ),
    
    // Dua (Supplication)
    RABBANA_ATINA(
        "Rabbana atina fi'd-dunya hasanatan",
        "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً",
        7,
        "Our Lord, give us good in this world and good in the next world",
        PrayerCategory.DUA
    ),
    
    DUA_YUNUS(
        "Dua Yunus",
        "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        100,
        "There is no deity except You; glory be to You. Indeed, I have been of the wrongdoers",
        PrayerCategory.DUA
    ),
    
    // Memorial Prayers
    TAHLIL_MAYIT(
        "Tahlil for Deceased",
        "لَا إِلَٰهَ إِلَّا ٱللَّٰهُ مُحَمَّدٌ رَسُولُ ٱللَّٰهِ",
        1000,
        "Memorial prayer for the deceased",
        PrayerCategory.MEMORIAL
    ),
    
    YASIN_READING(
        "Surah Yasin",
        "سُورَة يس",
        1,
        "Reading of Surah Yasin for the deceased",
        PrayerCategory.MEMORIAL
    ),
    
    FATIHAH_MEMORIAL(
        "Al-Fatihah for Deceased",
        "ٱلْفَاتِحَة",
        7,
        "Opening chapter for the deceased",
        PrayerCategory.MEMORIAL
    ),
    
    // 99 Names of Allah
    ASMA_UL_HUSNA(
        "Asma ul-Husna",
        "ٱلْأَسْمَاءُ ٱلْحُسْنَىٰ",
        99,
        "The 99 Beautiful Names of Allah",
        PrayerCategory.NAMES_OF_ALLAH
    ),
    
    // Custom/Other
    CUSTOM_DHIKR(
        "Custom Dhikr",
        "ذِكْر مُخَصَّص",
        1,
        "User-defined remembrance",
        PrayerCategory.CUSTOM
    );

    /**
     * Get recommended counts for different levels
     */
    fun getRecommendedCounts(): List<Int> = when (this) {
        LA_ILAHA_ILLA_ALLAH, ASTAGHFIRULLAH, LA_HAWLA_WALA_QUWWATA -> listOf(100, 300, 500, 1000)
        SALAWAT_IBRAHIM -> listOf(100, 300, 1000)
        TAHLIL_MAYIT -> listOf(1000, 3000, 7000)
        YASIN_READING -> listOf(1, 3, 7, 41)
        FATIHAH_MEMORIAL -> listOf(7, 21, 71)
        ASMA_UL_HUSNA -> listOf(99, 297, 999)
        else -> listOf(defaultCount, defaultCount * 3, defaultCount * 10)
    }

    /**
     * Check if this prayer is appropriate for memorial occasions
     */
    fun isMemorialPrayer(): Boolean = category == PrayerCategory.MEMORIAL

    /**
     * Get the typical session duration in minutes
     */
    fun getEstimatedDurationMinutes(count: Int): Int = when (this) {
        YASIN_READING -> 20 * count // 20 minutes per reading
        FATIHAH_MEMORIAL -> 2 * count // 2 minutes per Fatihah
        TAHLIL_MAYIT -> (count / 50) // ~50 tahlil per minute
        ASMA_UL_HUSNA -> 15 * (count / 99) // ~15 minutes for full names
        else -> maxOf(1, count / 60) // ~60 dhikr per minute for others
    }
}

enum class PrayerCategory(val displayName: String, val arabicName: String) {
    DHIKR("Dhikr", "ذِكْر"),
    SALAWAT("Salawat", "صَلَوَات"),
    DUA("Dua", "دُعَاء"),
    MEMORIAL("Memorial Prayers", "صَلَاة الْمَيِّت"),
    NAMES_OF_ALLAH("Names of Allah", "أَسْمَاء ٱللَّٰه"),
    CUSTOM("Custom", "مُخَصَّص")
}