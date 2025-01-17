package com.quran.surah_almulk.view.holder

import androidx.recyclerview.widget.RecyclerView
import com.quran.surah_almulk.databinding.ItemMainSurahBinding
import com.quran.surah_almulk.utils.TextUtils
import com.quran.surah_almulk.view.surah.SurahInterface

class MainSurahHolder(
    val binding: ItemMainSurahBinding,
    val onClickListener: (SurahInterface) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(surahInterface: SurahInterface) = with(binding) {
        tvArabic.text = surahInterface.arabic
        tvEndOfAyah.text = surahInterface.numberSurah.toString()
        tvSurah.text =
            surahInterface.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        tvVerses.text =
            "${surahInterface.typeSurah.name.uppercase()} • ${surahInterface.verses} Verse"
        clParent.setOnClickListener { onClickListener.invoke(surahInterface) }
    }
}