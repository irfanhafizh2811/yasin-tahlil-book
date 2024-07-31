package com.icaali.tasbeeh.view.dialog

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.databinding.DialogLanguageBinding
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.LanguagePreference
import org.jetbrains.anko.textColor
import java.util.Locale

class LanguageDialog(
    context: Context,
    val languagePreference: LanguagePreference,
    val localManager: LocaleManager,
) : BottomSheetDialog(context) {

    private lateinit var binding: DialogLanguageBinding

    init {
        binding = DialogLanguageBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            val language = languagePreference.language
            when {
                language.contains(LanguagePreference.LANGUAGE_ENGLISH) -> {
                    languagePreference.language = LanguagePreference.LANGUAGE_ENGLISH
                    localManager.applicationLocales =
                        LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_ENGLISH))
                }

                language.contains(LanguagePreference.LANGUAGE_INDONESIA) -> {
                    languagePreference.language = LanguagePreference.LANGUAGE_INDONESIA
                    localManager.applicationLocales =
                        LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_INDONESIA))
                }

                language.contains(LanguagePreference.LANGUAGE_TURKEY) -> {
                    languagePreference.language = LanguagePreference.LANGUAGE_TURKEY
                    localManager.applicationLocales =
                        LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_TURKEY))
                }

                language.contains(LanguagePreference.LANGUAGE_SAUDI_ARABIA) -> {
                    languagePreference.language = LanguagePreference.LANGUAGE_SAUDI_ARABIA
                    localManager.applicationLocales =
                        LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_SAUDI_ARABIA))
                }
            }
            dismiss()
        }
    }

    fun updateView(languageActive: String) = with(binding) {
        tvIndonesia.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tvEnglish.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tvTurkey.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tvSaudiArabia.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        ivActiveIndonesia.gone()
        ivActiveEnglish.gone()
        ivActiveTurkey.gone()
        ivActiveSaudiArabia.gone()
        when {
            languageActive.contains(LanguagePreference.LANGUAGE_INDONESIA) -> {
                tvIndonesia.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                ivActiveIndonesia.visible()
            }

            languageActive.contains(LanguagePreference.LANGUAGE_RUSSIAN) -> {
                tvRussian.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                ivActiveRussian.visible()
            }

            languageActive.contains(LanguagePreference.LANGUAGE_TURKEY) -> {
                tvTurkey.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                ivActiveTurkey.visible()
            }

            languageActive.contains(LanguagePreference.LANGUAGE_SAUDI_ARABIA) -> {
                tvSaudiArabia.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                ivActiveSaudiArabia.visible()
            }

            languageActive.contains(LanguagePreference.LANGUAGE_ENGLISH) -> {
                tvEnglish.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                ivActiveEnglish.visible()
            }
        }
    }
}