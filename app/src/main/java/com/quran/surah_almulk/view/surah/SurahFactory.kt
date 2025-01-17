package com.quran.surah_almulk.view.surah

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