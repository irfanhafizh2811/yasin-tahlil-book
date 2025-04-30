package com.quran.surah_yasin.data.model.surah

class BaseSurahInterface : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.AL_MULK

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 30

    override val arabic: String
        get() = "الْمُلْك"

    override val numberSurah: Int
        get() = 0

    override val sourceJson: String
        get() = "surah_al_mulk.json"

    override val mean: Int
        get() = 0
}