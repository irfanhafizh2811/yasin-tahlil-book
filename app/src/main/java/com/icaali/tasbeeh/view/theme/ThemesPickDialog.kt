package com.icaali.tasbeeh.view.theme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import kotlinx.android.synthetic.main.dialog_bottom_themes_picker.*

class ThemesPickDialog(context: Context) : BottomSheetDialog(context) {

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
    ): ThemesPickDialog {
        adapter.themes = themes
        adapter.type = type
        adapter.notifyDataSetChanged()
        return this
    }

    fun setOnPositiveListener(onPositiveListener: (Theme) -> Unit): ThemesPickDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

}