package com.quran.surah_almulk.data.model.surah

import androidx.annotation.StringRes
import com.quran.surah_almulk.R

class SurahAlWaqiah : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.AL_WAQIAH

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 96

    override val arabic: String
        get() = "الْوَاقِعَةُ"

    override val numberSurah: Int
        get() = 56

    override val sourceJson: String
        get() = "surah_al_waqiah.json"

    override val mean: Int
        @StringRes
        get() = R.string.label_surah_mean_al_waqiah
}