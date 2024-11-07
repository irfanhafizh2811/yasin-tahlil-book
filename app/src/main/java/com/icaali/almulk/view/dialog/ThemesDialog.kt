package com.icaali.almulk.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.almulk.databinding.DialogBottomThemesPickerBinding
import com.icaali.almulk.view.adapter.ThemeAdapter
import com.icaali.almulk.view.theme.Theme
import com.icaali.almulk.view.theme.ThemeType

class ThemesDialog(context: Context) : BottomSheetDialog(context) {

    private var binding: DialogBottomThemesPickerBinding
    private var onPositiveListener: ((Theme) -> Unit)? = null
    private val adapter by lazy {
        ThemeAdapter {
            dismiss()
            onPositiveListener?.invoke(it)
        }
    }

    init {
        binding = DialogBottomThemesPickerBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            ivClose.setOnClickListener { dismiss() }
            rvThemes.adapter = adapter
        }
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