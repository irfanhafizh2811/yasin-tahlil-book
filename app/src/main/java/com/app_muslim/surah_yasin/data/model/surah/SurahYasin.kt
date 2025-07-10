package com.app_muslim.surah_yasin.data.model.surah

import androidx.annotation.StringRes
import com.app_muslim.surah_yasin.R

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