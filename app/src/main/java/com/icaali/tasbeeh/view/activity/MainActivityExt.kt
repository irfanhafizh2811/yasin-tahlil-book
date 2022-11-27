package com.icaali.tasbeeh.view.activity

import android.os.Build
import android.os.LocaleList
import com.icaali.tasbeeh.preference.LanguagePreference
import java.util.Locale

fun MainActivity.setLanguage() {
    if (Build.VERSION.SDK_INT >= 33) {
        when {
            languagePreference.language.contains(LanguagePreference.LANGUAGE_ENGLISH) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_ENGLISH))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_TURKEY) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_TURKEY))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_RUSSIAN) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_RUSSIAN))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_SAUDI_ARABIA) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_SAUDI_ARABIA))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_INDONESIA) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_INDONESIA))
            }
        }
    }
}