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
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.LanguagePreference
import kotlinx.android.synthetic.main.dialog_language.*
import org.jetbrains.anko.textColor
import java.util.Locale

class LanguageDialog(
    context: Context,
    val languagePreference: LanguagePreference,
    val localManager: LocaleManager
) : BottomSheetDialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_language, clContainer, false
        )
        setContentView(view)
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

    fun updateView(languageActive: String) {
        tv_indonesia.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tv_english.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tv_turkey.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        tv_saudi_arabia.textColor = ContextCompat.getColor(context, R.color.colorBlack)
        iv_active_indonesia.gone()
        iv_active_english.gone()
        iv_active_turkey.gone()
        iv_active_saudi_arabia.gone()
        when {
            languageActive.contains(LanguagePreference.LANGUAGE_INDONESIA) -> {
                tv_indonesia.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                iv_active_indonesia.visible()
            }
            languageActive.contains(LanguagePreference.LANGUAGE_RUSSIAN) -> {
                tv_russian.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                iv_active_russian.visible()
            }
            languageActive.contains(LanguagePreference.LANGUAGE_TURKEY) -> {
                tv_turkey.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                iv_active_turkey.visible()
            }
            languageActive.contains(LanguagePreference.LANGUAGE_SAUDI_ARABIA) -> {
                tv_saudi_arabia.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                iv_active_saudi_arabia.visible()
            }
            languageActive.contains(LanguagePreference.LANGUAGE_ENGLISH) -> {
                tv_english.textColor = ContextCompat.getColor(context, R.color.colorAccent)
                iv_active_english.visible()
            }
        }
    }
}