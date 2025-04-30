package com.quran.surah_yasin.data.preference

import com.quran.surah_yasin.utils.TextUtils

class SurahPreference(val corePreference: CorePreference) {

    companion object {
        const val ANY_READ_SURAH = "any_read_surah"
        const val LAST_READ_SURAH = "last_read_surah"
        const val LAST_READ_VERSE = "last_read_verse"
    }

    var lastReadSurah: String
        set(value) = corePreference.setString(LAST_READ_SURAH, value)
        get() = corePreference.getString(
            LAST_READ_SURAH,
            TextUtils.BLANK
        )

    var lastReadVerse: Int
        set(value) = corePreference.setInt(LAST_READ_VERSE, value)
        get() = corePreference.getInt(
            LAST_READ_VERSE, 0
        )

    var anyReadSurah: Boolean
        set(value) = corePreference.setBoolean(ANY_READ_SURAH, value)
        get() = corePreference.getBoolean(
            ANY_READ_SURAH, lastReadSurah != TextUtils.BLANK
        )
}