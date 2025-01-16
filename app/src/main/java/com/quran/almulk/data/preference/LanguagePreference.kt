package com.quran.almulk.data.preference

class LanguagePreference(val corePreference: CorePreference) {

    companion object {
        const val LANGUAGE_SAUDI_ARABIA = "ar-SA"
        const val LANGUAGE_INDONESIA = "id-ID"
        const val LANGUAGE_RUSSIAN = "ru-RU"
        const val LANGUAGE_TURKEY = "tr-TR"
        const val LANGUAGE_ENGLISH = "en"

        private const val LANGUAGE_PREF = "language_pref"
    }

    var language: String
        set(value) = corePreference.setString(LANGUAGE_PREF, value)
        get() = corePreference.getString(LANGUAGE_PREF, LANGUAGE_ENGLISH)
}