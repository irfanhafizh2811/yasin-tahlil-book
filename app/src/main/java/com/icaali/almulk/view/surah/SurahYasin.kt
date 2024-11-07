package com.icaali.almulk.view.surah

import androidx.annotation.StringRes
import com.icaali.almulk.R

class SurahYasin : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.YASIN

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 83

    override val arabic: String
        get() = "يٰسۤ"

    override val numberSurah: Int
        get() = 83

    override val sourceJson: String
        get() = "surah_yasin.json"

    override val mean: Int
        @StringRes
        get() = R.string.label_surah_mean_ar_yasin
}