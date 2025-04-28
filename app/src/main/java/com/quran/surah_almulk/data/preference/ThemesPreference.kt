package com.quran.surah_almulk.data.preference

import com.quran.surah_almulk.data.model.theme.ThemeType

class ThemesPreference(val corePreference: CorePreference) {

    companion object {
        const val THEME_TYPE = "theme_type"
        const val THEME_NEW_CONTENT_YELLOW = "theme_new_content_yellow"
        const val THEME_NEW_CONTENT_KAABA = "theme_new_content_kaaba"
        const val THEME_NEW_CONTENT_RAMADHAN = "theme_new_content_ramadhan"
        const val THEME_NEW_CONTENT_EID_AL_FITR = "theme_new_content_eid_al_fitr"
        const val THEME_SURVEY = "theme_survey"
    }

    fun anyNewContent(): Boolean = listOf(
        hasNewContentYellow,
        hasNewContentKaaba,
        hasNewContentRamadhan,
        hasNewContentEidAlFitr
    ).any { it }

    var type: ThemeType
        get() = when (corePreference.getString(THEME_TYPE, ThemeType.DEFAULT.name)) {
            ThemeType.DEFAULT.name -> ThemeType.DEFAULT
            ThemeType.FLOWERS.name -> ThemeType.FLOWERS
            ThemeType.LEAVES.name -> ThemeType.LEAVES
            ThemeType.BLACK_WHITE.name -> ThemeType.BLACK_WHITE
            ThemeType.YELLOW.name -> ThemeType.YELLOW
            ThemeType.KAABA.name -> ThemeType.KAABA
            ThemeType.RAMADHAN.name -> ThemeType.RAMADHAN
            ThemeType.EID_AL_FITR.name -> ThemeType.EID_AL_FITR
            else -> ThemeType.RAMADHAN
        }
        set(value) {
            val typeText = when (value) {
                ThemeType.DEFAULT -> ThemeType.DEFAULT.name
                ThemeType.FLOWERS -> ThemeType.FLOWERS.name
                ThemeType.LEAVES -> ThemeType.LEAVES.name
                ThemeType.BLACK_WHITE -> ThemeType.BLACK_WHITE.name
                ThemeType.YELLOW -> ThemeType.YELLOW.name
                ThemeType.KAABA -> ThemeType.KAABA.name
                ThemeType.RAMADHAN -> ThemeType.RAMADHAN.name
                ThemeType.EID_AL_FITR -> ThemeType.EID_AL_FITR.name
            }
            corePreference.setString(THEME_TYPE, typeText)
        }

    var hasNewContentYellow: Boolean
        get() = corePreference.getBoolean(THEME_NEW_CONTENT_YELLOW, true)
        set(value) {
            corePreference.setBoolean(THEME_NEW_CONTENT_YELLOW, value)
        }

    var hasNewContentKaaba: Boolean
        get() = corePreference.getBoolean(THEME_NEW_CONTENT_KAABA, true)
        set(value) {
            corePreference.setBoolean(THEME_NEW_CONTENT_KAABA, value)
        }

    var hasNewContentRamadhan: Boolean
        get() = corePreference.getBoolean(THEME_NEW_CONTENT_RAMADHAN, true)
        set(value) {
            corePreference.setBoolean(THEME_NEW_CONTENT_RAMADHAN, value)
        }

    var hasNewContentEidAlFitr: Boolean
        get() = corePreference.getBoolean(THEME_NEW_CONTENT_EID_AL_FITR, true)
        set(value) {
            corePreference.setBoolean(THEME_NEW_CONTENT_EID_AL_FITR, value)
        }

    var hasThemeSurvey: Boolean
        get() = corePreference.getBoolean(THEME_SURVEY, true)
        set(value) {
            corePreference.setBoolean(THEME_SURVEY, value)
        }
}