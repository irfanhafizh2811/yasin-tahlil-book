package com.app_muslim.surah_yasin.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app_muslim.surah_yasin.data.model.theme.Theme
import com.app_muslim.surah_yasin.data.model.theme.ThemeType
import com.app_muslim.surah_yasin.databinding.DialogBottomThemesPickerBinding
import com.app_muslim.surah_yasin.view.adapter.ThemeAdapter

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