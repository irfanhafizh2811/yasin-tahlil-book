package com.app_muslim.surah_yasin.view.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.theme.ThemeType
import com.app_muslim.surah_yasin.databinding.ItemThemeBinding
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.context.getDrawableCompat

class ThemeAdapter(val onItemClickListener: (com.app_muslim.surah_yasin.data.model.theme.Theme) -> Unit) :
    RecyclerView.Adapter<ThemeAdapter.ThemeVH>() {

    private lateinit var binding: ItemThemeBinding
    var themes = mutableListOf<com.app_muslim.surah_yasin.data.model.theme.Theme>()
    var type = ThemeType.DEFAULT

    class ThemeVH(
        private val onItemClickListener: (com.app_muslim.surah_yasin.data.model.theme.Theme) -> Unit,
        private val binding: ItemThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: com.app_muslim.surah_yasin.data.model.theme.Theme, type: ThemeType) {
            with(binding) {
                ivTheme.setImageDrawable(root.context.getDrawableCompat(theme.iconImageRes()))
                tvTheme.text = root.context.getString(theme.textStringRes())
                when (theme.themeType()) {
                    type -> selected()
                    else -> unselected()
                }
                cvTheme.setOnClickListener {
                    onItemClickListener.invoke(theme)
                }
                cvNewTheme.isVisible = theme.isVisibleNewBadge()
            }
        }

        private fun selected() {
            with(binding) {
                cvTheme.setCardBackgroundColor(
                    root.context.getColorCompat(R.color.themeSelected)
                )
                tvTheme.run {
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(context.getColorCompat(R.color.themeSelected))
                }
            }
        }

        private fun unselected() {
            with(binding) {
                cvTheme.setCardBackgroundColor(
                    root.context.getColorCompat(R.color.themeUnselected)
                )
                tvTheme.run {
                    typeface = Typeface.DEFAULT
                    setTextColor(context.getColorCompat(R.color.themeUnselected))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeVH {
        binding = ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeVH(onItemClickListener, binding)
    }

    override fun getItemCount(): Int = themes.size

    override fun onBindViewHolder(holder: ThemeVH, position: Int) {
        holder.bind(themes[position], type)
    }

}