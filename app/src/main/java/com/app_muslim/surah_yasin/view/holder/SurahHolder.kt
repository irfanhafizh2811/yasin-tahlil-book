package com.app_muslim.surah_yasin.view.holder

import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.Verse
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.data.preference.Language
import com.app_muslim.surah_yasin.databinding.ItemSurahVerseBinding
import com.app_muslim.surah_yasin.extension.text.numberArabic
import com.app_muslim.surah_yasin.utils.FontSize
import com.app_muslim.surah_yasin.utils.TextUtils

class SurahHolder(
    val binding: ItemSurahVerseBinding,
    val surahInterface: SurahInterface
) : RecyclerView.ViewHolder(binding.root) {

    var posItem: Int = 0
    val typeUnit = TypedValue.COMPLEX_UNIT_PX
    var surahName: String = TextUtils.BLANK
    private var isFirstAyah = posItem <= 0
    var visibleLatinQuran = true
    var visibleTranslationQuran = true
    var language: Language = Language.INDONESIA

    fun bind(
        verse: Verse,
        fontSize: FontSize,
        posItem: Int,
        visibleLatinQuran: Boolean,
        visibleTranslationQuran: Boolean,
        language: Language
    ): SurahHolder = with(binding) {
        this@SurahHolder.language = language
        this@SurahHolder.posItem = posItem
        this@SurahHolder.visibleLatinQuran = visibleLatinQuran
        this@SurahHolder.visibleTranslationQuran = visibleTranslationQuran
        isFirstAyah = posItem == 0
        val typeSurah = surahInterface.typeSurah.name
        val verseCount = surahInterface.numberSurah.toString()
        cvSurah.isVisible = isFirstAyah
        tvSurahMean.text = surahInterface.surahQuran.name
        tvSurahArabic.text = surahInterface.arabic
        tvInfo.text = "$typeSurah • $verseCount Verse"
        val (arabic, latin) = verse
        this@SurahHolder.surahName =
            surahInterface.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        tvArabic.text = arabic
        tvLatin.text = latin
        tvMean.text = getMeanTranslate(verse)
        tvEndOfAyah.text = (posItem + 1).toString().numberArabic()
        tvLatin.isVisible = visibleLatinQuran && language != Language.SAUDI_ARABIA
        tvMean.isVisible = visibleTranslationQuran && language != Language.SAUDI_ARABIA
        updateSize(fontSize)
        return this@SurahHolder
    }

    private fun getMeanTranslate(verse: Verse): String {
        return when (language) {
            Language.ENGLISH -> verse.meanEng
            Language.INDONESIA -> verse.meanInd
            Language.MALAYSIA -> verse.meanMay
            Language.TURKEY -> verse.meanTur
            Language.RUSSIAN -> verse.meanRus
            else -> verse.meanEng
        }
    }

    private fun updateSize(fontSize: FontSize) {
        when (fontSize) {
            FontSize.SMALL -> updateSizeSmall()
            FontSize.REGULAR -> updateSizeRegular()
            FontSize.LARGE -> updateSizeLarge()
            FontSize.HUGE -> updateSizeHuge()
        }
    }

    private fun updateSizeSmall() = with(binding) {
        val res = root.context.resources
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_small))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_small))
        tvMean.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_small))
        if (!isFirstAyah) return@with
    }

    private fun updateSizeRegular() = with(binding) {
        val res = root.context.resources
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_regular))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_regular))
        tvMean.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_regular))
        if (!isFirstAyah) return@with
    }

    private fun updateSizeLarge() = with(binding) {
        val res = root.context.resources
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_large))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_large))
        tvMean.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_large))
        if (!isFirstAyah) return@with
    }

    private fun updateSizeHuge() = with(binding) {
        val res = root.context.resources
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_huge))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_huge))
        tvMean.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_huge))
        if (!isFirstAyah) return@with
    }
}