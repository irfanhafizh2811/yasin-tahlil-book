package com.quran.surah_yasin.data.model.surah

object SurahFactory {

    val listSurah = mutableListOf(
        SurahAlBaqarah(),
        SurahArRahman(),
        SurahAlWaqiah(),
        SurahAlMulk(),
        SurahYasin()
    )

    fun generate(surahQuran: SurahQuran): SurahInterface = listSurah.find {
        surahQuran == it.surahQuran
    } ?: SurahAlMulk()
}