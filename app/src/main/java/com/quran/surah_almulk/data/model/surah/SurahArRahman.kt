package com.quran.surah_almulk.data.model.surah

import androidx.annotation.StringRes
import com.quran.surah_almulk.R

class SurahArRahman : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.AR_RAHMAN

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 78

    override val arabic: String
        get() = "اَلرَّحْمٰنُ"

    override val numberSurah: Int
        get() = 55

    override val sourceJson: String
        get() = "surah_ar_rahman.json"

    override val mean: Int
        @StringRes
        get() = R.string.label_surah_mean_ar_rahman
}