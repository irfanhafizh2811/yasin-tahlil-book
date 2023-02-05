package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.view.theme.Theme
import com.icaali.tasbeeh.view.adapter.ThemeAdapter
import com.icaali.tasbeeh.view.theme.ThemeType
import kotlinx.android.synthetic.main.dialog_bottom_themes_picker.*

class ThemesDialog(context: Context) : BottomSheetDialog(context) {

    private var onPositiveListener: ((Theme) -> Unit)? = null
    private val adapter by lazy {
        ThemeAdapter {
            dismiss()
            onPositiveListener?.invoke(it)
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_themes_picker, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivClose?.setOnClickListener { dismiss() }
        rvThemes?.adapter = adapter
    }

    fun setItemThemes(
        themes: MutableList<Theme>,
        type: ThemeType
    ): ThemesDialog {
        adapter.themes = themes
        adapter.type = type
        adapter.notifyItemRangeChanged(0, adapter.themes.size)
        return this
    }

    fun setOnPositiveListener(onPositiveListener: (Theme) -> Unit): ThemesDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

}