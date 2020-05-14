package com.icaali.tasbeeh.view.theme

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.view.getString
import kotlinx.android.synthetic.main.item_theme.view.*
import org.jetbrains.anko.textColor

class ThemeAdapter(val onItemClickListener: (Theme) -> Unit) :
    RecyclerView.Adapter<ThemeAdapter.ThemeVH>() {

    var themes = mutableListOf<Theme>()
    var type = ThemeType.DEFAULT

    class ThemeVH(
        val onItemClickListener: (Theme) -> Unit,
        val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(theme: Theme, type: ThemeType) {
            with(view) {
                ivTheme.setImageDrawable(context.getDrawableCompat(theme.iconImageRes))
                tvTheme.text = getString(theme.textStringRes)
                when (theme.type) {
                    type -> selected(this)
                    else -> unselected(this)
                }
                cvTheme.setOnClickListener {
                    onItemClickListener.invoke(theme)
                }
            }
        }

        private fun selected(view: View) {
            with(view) {
                cvTheme.setCardBackgroundColor(
                    context.getColorCompat(R.color.themeSelected)
                )
                tvTheme.run {
                    typeface = Typeface.DEFAULT_BOLD
                    textColor = context.getColorCompat(R.color.themeSelected)
                }
            }
        }

        private fun unselected(view: View) {
            with(view) {
                cvTheme.setCardBackgroundColor(
                    context.getColorCompat(R.color.themeUnselected)
                )
                tvTheme.run {
                    typeface = Typeface.DEFAULT
                    textColor = context.getColorCompat(R.color.themeUnselected)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeVH =
        ThemeVH(
            onItemClickListener,
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_theme,
                parent, false
            )
        )

    override fun getItemCount(): Int = themes.size

    override fun onBindViewHolder(holder: ThemeVH, position: Int) {
        holder.bind(themes[position], type)
    }

}