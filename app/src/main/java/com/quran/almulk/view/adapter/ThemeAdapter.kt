package com.quran.almulk.view.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.quran.almulk.R
import com.quran.almulk.databinding.ItemThemeBinding
import com.quran.almulk.extension.context.getColorCompat
import com.quran.almulk.extension.context.getDrawableCompat
import com.quran.almulk.view.theme.Theme
import com.quran.almulk.view.theme.ThemeType

class ThemeAdapter(val onItemClickListener: (Theme) -> Unit) :
    RecyclerView.Adapter<ThemeAdapter.ThemeVH>() {

    private lateinit var binding: ItemThemeBinding
    var themes = mutableListOf<Theme>()
    var type = ThemeType.DEFAULT

    class ThemeVH(
        private val onItemClickListener: (Theme) -> Unit,
        private val binding: ItemThemeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: Theme, type: ThemeType) {
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