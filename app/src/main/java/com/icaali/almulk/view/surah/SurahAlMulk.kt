package com.icaali.almulk.view.surah

import androidx.annotation.StringRes
import com.icaali.almulk.R

class SurahAlMulk : SurahInterface {

    override val surahQuran: SurahQuran
        get() = SurahQuran.AL_MULK

    override val typeSurah: TypeSurah
        get() = TypeSurah.Mecca

    override val verses: Int
        get() = 30

    override val arabic: String
        get() = "الْمُلْك"

    override val numberSurah: Int
        get() = 67

    override val sourceJson: String
        get() = "surah_al_mulk.json"

    override val mean: Int
        @StringRes
        get() = R.string.label_surah_mean_al_mulk
}