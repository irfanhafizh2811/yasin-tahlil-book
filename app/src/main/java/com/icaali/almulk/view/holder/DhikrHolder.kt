package com.icaali.almulk.view.holder

import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.icaali.almulk.R
import com.icaali.almulk.databinding.ItemDhikrBinding
import com.icaali.almulk.model.Dhikr
import com.icaali.almulk.utils.FontSize

class DhikrHolder(
    private val binding: ItemDhikrBinding,
) : RecyclerView.ViewHolder(binding.root) {

    val typeUnit = TypedValue.COMPLEX_UNIT_PX
    var surah: String = ""

    companion object {
        const val ALFATIHAH = "Al-Fatihah"
    }

    fun bind(dhikr: Dhikr, fontSize: FontSize): DhikrHolder = with(binding) {
        val (surah, prayer, meanInd, _, count) = dhikr
        this@DhikrHolder.surah = surah
        onUITaawudz()
        tvSurah.text = surah
        tvArabic.text = prayer
        tvLatin.text = meanInd
        tvCount.apply {
            text = count.toString().plus("x")
            isVisible = count > 0
        }
        updateSize(fontSize)
        return this@DhikrHolder
    }

    private fun onUITaawudz() = with(binding) {
        clTaawudz.isVisible = surah.contains(ALFATIHAH)
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
        tvSurah.setTextSize(typeUnit, res.getDimension(R.dimen.font_title_size_small))
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_small))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_small))
        tvCount.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_small))
        if (!surah.contains(ALFATIHAH)) return@with
        tvTaawudz.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_small))
        tvTaawudzLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_small))
    }

    private fun updateSizeRegular() = with(binding) {
        val res = root.context.resources
        tvSurah.setTextSize(typeUnit, res.getDimension(R.dimen.font_title_size_regular))
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_regular))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_regular))
        tvCount.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_regular))
        if (!surah.contains(ALFATIHAH)) return@with
        tvTaawudz.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_regular))
        tvTaawudzLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_regular))
    }

    private fun updateSizeLarge() = with(binding) {
        val res = root.context.resources
        tvSurah.setTextSize(typeUnit, res.getDimension(R.dimen.font_title_size_large))
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_large))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_large))
        tvCount.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_large))
        if (!surah.contains(ALFATIHAH)) return@with
        tvTaawudz.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_large))
        tvTaawudzLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_large))
    }

    private fun updateSizeHuge() = with(binding) {
        val res = root.context.resources
        tvSurah.setTextSize(typeUnit, res.getDimension(R.dimen.font_title_size_huge))
        tvArabic.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_huge))
        tvLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_huge))
        tvCount.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_huge))
        if (!surah.contains(ALFATIHAH)) return@with
        tvTaawudz.setTextSize(typeUnit, res.getDimension(R.dimen.font_arabic_size_huge))
        tvTaawudzLatin.setTextSize(typeUnit, res.getDimension(R.dimen.font_mean_size_huge))
    }
}