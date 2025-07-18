package com.app_muslim.surah_yasin.data.preference

import com.app_muslim.surah_yasin.extension.context.getSystemLocaleCode

class LanguagePreference(val corePreference: CorePreference) {

    companion object {
        private const val LANGUAGE_PREF = "language_pref"
    }

    var language: Language
        set(value) = corePreference.setString(LANGUAGE_PREF, value.localeCode)
        get() = Language.fromLocaleCode(corePreference.getString(LANGUAGE_PREF,
            getSystemLocaleCode()))
}

enum class Language(val localeCode: String) {
    SAUDI_ARABIA("ar-SA"),
    INDONESIA("id-ID"),
    RUSSIAN("ru-RU"),
    MALAYSIA("ms-MY"),
    TURKEY("tr-TR"),
    ENGLISH("en");

    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        fun fromLocaleCode(code: String): Language {
            return entries.find { it.localeCode == code } ?: ENGLISH
        }
    }
}