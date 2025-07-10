package com.app_muslim.surah_yasin.data.model.surah

interface SurahInterface {
    val surahQuran: SurahQuran
    val typeSurah: TypeSurah
    val verses: Int
    val arabic: String
    val numberSurah: Int
    val sourceJson: String
    val mean: Int
}