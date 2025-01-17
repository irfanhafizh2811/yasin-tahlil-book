package com.quran.surah_almulk.view.surah

interface SurahInterface {
    val surahQuran: SurahQuran
    val typeSurah: TypeSurah
    val verses: Int
    val arabic: String
    val numberSurah: Int
    val sourceJson: String
    val mean: Int
}