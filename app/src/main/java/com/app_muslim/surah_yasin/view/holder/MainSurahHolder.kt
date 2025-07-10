package com.app_muslim.surah_yasin.view.holder

import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.gender.Gender
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.databinding.ItemMainSurahBinding
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.image.setTint
import com.app_muslim.surah_yasin.utils.TextUtils

class MainSurahHolder(
    val binding: ItemMainSurahBinding,
    val onClickListener: (SurahInterface) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, surahInterface: SurahInterface) = with(binding) {
        tvArabic.text = surahInterface.arabic
        tvEndOfAyah.text = surahInterface.numberSurah.toString()
        tvSurah.text =
            surahInterface.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        tvVerses.text =
            "${surahInterface.typeSurah.name.uppercase()} • ${surahInterface.verses} Verse"
        clParent.setOnClickListener { onClickListener.invoke(surahInterface) }
        val tintColor = when (user.gender) {
            Gender.MALE -> R.color.colorAccentMale
            else -> R.color.colorAccentFemale
        }
        ivEndOfAyah.setTint(tintColor)
        when (user.gender) {
            Gender.MALE -> {
                tvArabic.setTextColor(getColorCompat(R.color.colorAccentMale))
            }

            Gender.FEMALE -> {
                tvArabic.setTextColor(getColorCompat(R.color.colorAccentFemale))
            }
        }
    }
}