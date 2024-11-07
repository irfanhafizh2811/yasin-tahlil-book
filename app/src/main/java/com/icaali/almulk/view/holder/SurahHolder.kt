package com.icaali.almulk.view.holder

import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.icaali.almulk.R
import com.icaali.almulk.databinding.ItemSurahVerseBinding
import com.icaali.almulk.extension.text.numberArabic
import com.icaali.almulk.model.Verse
import com.icaali.almulk.utils.FontSize
import com.icaali.almulk.utils.TextUtils
import com.icaali.almulk.view.surah.SurahInterface

class SurahHolder(
    val binding: ItemSurahVerseBinding,
    val surahInterface: SurahInterface
) : RecyclerView.ViewHolder(binding.root) {

    var posItem: Int = 0
    val typeUnit = TypedValue.COMPLEX_UNIT_PX
    var surahName: String = TextUtils.BLANK
    private var isFirstAyah = posItem <= 0

    fun bind(verse: Verse, fontSize: FontSize, posItem: Int): SurahHolder = with(binding) {
        this@SurahHolder.posItem = posItem
        isFirstAyah = posItem == 0
        val (arabic, latin, meanIndo) = verse
        this@SurahHolder.surahName =
            surahInterface.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        tvArabic.text = arabic
        tvLatin.text = latin
        tvMean.text = meanIndo
        tvEndOfAyah.text = (posItem + 1).toString().numberArabic()
        onUITaawudz()
        updateSize(fontSize)
        return this@SurahHolder
    }

    private fun onUITaawudz() = with(binding) {
        cvTaawudz.isVisible = isFirstAyah
        if (!isFirstAyah) return@with
        tvSurah.text = surahName
        tvSurahMean.text = root.context.getString(surahInterface.mean)
        tvInfo.text = "${surahInterface.typeSurah.name.uppercase()} • ${surahInterface.verses}"
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