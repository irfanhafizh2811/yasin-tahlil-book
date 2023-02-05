package com.icaali.tasbeeh.view.activity

import com.icaali.tasbeeh.preference.ThemesPreference
import com.icaali.tasbeeh.view.theme.Theme
import com.icaali.tasbeeh.view.theme.ThemeType

fun TasbeehActivity.updateNewThemePref(theme: Theme, themesPreference: ThemesPreference) {
    when (theme.themeType()) {
        ThemeType.YELLOW -> themesPreference.hasNewContentYellow = false
        ThemeType.KAABA -> themesPreference.hasNewContentKaaba = false
        ThemeType.RAMADHAN -> themesPreference.hasNewContentRamadhan = false
        ThemeType.EID_AL_FITR -> themesPreference.hasNewContentEidAlFitr = false
    }
}