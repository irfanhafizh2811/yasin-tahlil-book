package com.app_muslim.surah_yasin.view.holder

import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.Verse
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.databinding.ItemSurahVerseBinding
import com.app_muslim.surah_yasin.extension.text.numberArabic
import com.app_muslim.surah_yasin.utils.FontSize
import com.app_muslim.surah_yasin.utils.TextUtils

class SurahHolder(
    val binding: ItemSurahVerseBinding,
    val surahInterface: SurahInterface
) : RecyclerView.ViewHolder(binding.root) {

    private var user: User = User()
    private var surahName: String = TextUtils.BLANK
    private var surahNameArab: String = TextUtils.BLANK
    private var posItem: Int = 0
    private val typeUnit = TypedValue.COMPLEX_UNIT_PX
    private var isFirstAyah = posItem <= 0
    private var visibleLatinQuran = true
    private var visibleTranslationQuran = true

    fun bind(
        surahInterface: SurahInterface,
        user: User,
        verse: Verse,
        fontSize: FontSize,
        posItem: Int,
        visibleLatinQuran: Boolean,
        visibleTranslationQuran: Boolean
    ): SurahHolder = with(binding) {
        this@SurahHolder.posItem = posItem
        this@SurahHolder.visibleLatinQuran = visibleLatinQuran
        this@SurahHolder.visibleTranslationQuran = visibleTranslationQuran
        this@SurahHolder.user = user
        isFirstAyah = posItem == 0
        val (arabic, latin, meanIndo) = verse
        this@SurahHolder.surahName =
            surahInterface.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        tvSurahArabic.text = surahInterface.arabic
        tvArabic.text = arabic
        tvLatin.text = latin
        tvMean.text = meanIndo
        tvEndOfAyah.text = (posItem + 1).toString().numberArabic()
        tvLatin.isVisible = visibleLatinQuran
        tvMean.isVisible = visibleTranslationQuran
        onUITaawudz()
        updateSize(fontSize)
        return this@SurahHolder
    }

    private fun onUITaawudz() = with(binding) {
        cvSurah.isVisible = isFirstAyah
        if (!isFirstAyah) return@with
        tvSurah.text = surahName
        tvSurahMean.text = root.context.getString(surahInterface.mean)
        tvInfo.text = "${surahInterface.typeSurah.name.uppercase()} • ${surahInterface.verses} Verse"
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