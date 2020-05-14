package com.icaali.tasbeeh.preference

import com.icaali.tasbeeh.view.theme.ThemeType

class ThemesPreference(val corePreference: CorePreference) {

    companion object {
        const val THEME_TYPE = "theme_type"
    }

    var type: ThemeType
        get() = when (corePreference.getString(THEME_TYPE, ThemeType.DEFAULT.name)) {
            ThemeType.DEFAULT.name -> ThemeType.DEFAULT
            ThemeType.FLOWERS.name -> ThemeType.FLOWERS
            ThemeType.LEAVES.name -> ThemeType.LEAVES
            ThemeType.BLACK_WHITE.name -> ThemeType.BLACK_WHITE
            else -> ThemeType.DEFAULT
        }
        set(value) {
             val typeText = when (value) {
                ThemeType.DEFAULT -> ThemeType.DEFAULT.name
                ThemeType.FLOWERS -> ThemeType.FLOWERS.name
                ThemeType.LEAVES -> ThemeType.LEAVES.name
                ThemeType.BLACK_WHITE -> ThemeType.BLACK_WHITE.name
             }
            corePreference.setString(THEME_TYPE, typeText)
        }

}