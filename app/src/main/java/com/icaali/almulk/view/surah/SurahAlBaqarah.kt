package com.icaali.almulk.view.surah

import androidx.annotation.StringRes
import com.icaali.almulk.R

class SurahAlBaqarah : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.AL_BAQARAH

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 286

    override val arabic: String
        get() = "الْبَقَرَة"

    override val numberSurah: Int
        get() = 2

    override val sourceJson: String
        get() = "surah_al_baqarah.json"

    override val mean: Int
        @StringRes
        get() = R.string.label_surah_mean_al_baqarah
}