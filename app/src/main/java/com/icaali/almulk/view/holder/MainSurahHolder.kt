package com.icaali.almulk.view.holder

import androidx.recyclerview.widget.RecyclerView
import com.icaali.almulk.databinding.ItemMainSurahBinding
import com.icaali.almulk.utils.TextUtils
import com.icaali.almulk.view.surah.SurahInterface

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