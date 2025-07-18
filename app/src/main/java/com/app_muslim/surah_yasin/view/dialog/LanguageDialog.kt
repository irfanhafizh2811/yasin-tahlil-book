package com.app_muslim.surah_yasin.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.preference.Language
import com.app_muslim.surah_yasin.databinding.DialogLanguageBinding
import com.app_muslim.surah_yasin.extension.context.getColorCompat

class LanguageDialog(
    private val context: Context,
    private var language: Language
) : BottomSheetDialog(context) {

    private lateinit var binding: DialogLanguageBinding
    private var onListenerSelected: ((Language) -> Unit)? = null
    private val listLL by lazy {
        listOf(
            binding.llIndonesia,
            binding.llMalaysia,
            binding.llEnglish,
            binding.llSaudiArabia,
            binding.llTurkey,
            binding.llRussian
        )
    }
    private val radioButtons by lazy {
        listOf(
            binding.rbIndonesia,
            binding.rbMalaysia,
            binding.rbEnglish,
            binding.rbSaudiArabia,
            binding.rbTurkey,
            binding.rbRussian
        )
    }
    private val textViews by lazy {
        listOf(
            binding.tvEnglish,
            binding.tvMalaysia,
            binding.tvIndonesia,
            binding.tvSaudiArabia,
            binding.tvTurkey,
            binding.tvRussian
        )
    }

    init {
        binding = DialogLanguageBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }

    fun selectedLanguage(onListenerSelected: (Language) -> Unit): LanguageDialog {
        this.onListenerSelected = onListenerSelected
        return this
    }


    private fun setupView() = with(binding) {
        ivClose.setOnClickListener { dismiss() }
        btnNegative.setOnClickListener { dismiss() }
        btnPositive.setOnClickListener {
            onListenerSelected?.invoke(language)
            dismiss()
        }

        // Setup initial selection
        when (language) {
            Language.ENGLISH -> {
                selected(tvEnglish, rbEnglish)
                rbEnglish.isChecked = true
            }

            Language.INDONESIA -> {
                selected(tvIndonesia, rbIndonesia)
                rbIndonesia.isChecked = true
            }

            Language.MALAYSIA -> {
                selected(tvMalaysia, rbMalaysia)
                rbMalaysia.isChecked = true
            }

            Language.TURKEY -> {
                selected(tvTurkey, rbTurkey)
                rbTurkey.isChecked = true
            }

            Language.SAUDI_ARABIA -> {
                selected(tvSaudiArabia, rbSaudiArabia)
                rbSaudiArabia.isChecked = true
            }

            Language.RUSSIAN -> {
                selected(tvRussian, rbRussian)
                rbRussian.isChecked = true
            }
        }

        // Set checked change listeners
        listLL.forEach { linearLayout ->
            linearLayout.setOnClickListener {
                when (linearLayout.id) {
                    R.id.llIndonesia -> {
                        language = Language.INDONESIA
                        selected(tvIndonesia, rbIndonesia)
                    }

                    R.id.llMalaysia -> {
                        language = Language.MALAYSIA
                        selected(tvMalaysia, rbMalaysia)
                    }

                    R.id.llEnglish -> {
                        language = Language.ENGLISH
                        selected(tvEnglish, rbEnglish)
                    }

                    R.id.llTurkey -> {
                        language = Language.TURKEY
                        selected(tvTurkey, rbTurkey)
                    }

                    R.id.llSaudiArabia -> {
                        language = Language.SAUDI_ARABIA
                        selected(tvSaudiArabia, rbSaudiArabia)
                    }

                    R.id.llRussian -> {
                        language = Language.RUSSIAN
                        selected(tvRussian, rbRussian)
                    }
                }
            }
        }
        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                when (radioButton.id) {
                    R.id.rb_indonesia -> {
                        language = Language.INDONESIA
                        selected(tvIndonesia, rbIndonesia)
                    }

                    R.id.rb_malaysia -> {
                        language = Language.MALAYSIA
                        selected(tvMalaysia, rbMalaysia)
                    }

                    R.id.rb_english -> {
                        language = Language.ENGLISH
                        selected(tvEnglish, rbEnglish)
                    }

                    R.id.rb_turkey -> {
                        language = Language.TURKEY
                        selected(tvTurkey, rbTurkey)
                    }

                    R.id.rb_saudi_arabia -> {
                        language = Language.SAUDI_ARABIA
                        selected(tvSaudiArabia, rbSaudiArabia)
                    }

                    R.id.rb_russian -> {
                        language = Language.RUSSIAN
                        selected(tvRussian, rbRussian)
                    }
                }
            }
        }
    }

    private fun selected(selectedText: TextView, selectedRb: RadioButton) {
        textViews.forEach {
            it.setTextColor(context.getColorCompat(R.color.colorBlack))
        }
        radioButtons.forEach { it.isChecked = false }
        selectedRb.isChecked = true
        selectedText.setTextColor(context.getColorCompat(R.color.colorAccent))
    }
}