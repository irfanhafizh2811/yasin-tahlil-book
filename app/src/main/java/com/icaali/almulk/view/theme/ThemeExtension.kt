package com.icaali.almulk.view.theme

import com.icaali.almulk.data.preference.ThemesPreference

fun MutableList<Theme>.setVisibleBadgeNewThemes(themesPreference: ThemesPreference) {
    forEach {
        when (it.themeType()) {
            ThemeType.YELLOW -> {
                it.setVisibleNewBad(themesPreference.hasNewContentYellow)
            }
            ThemeType.KAABA -> {
                it.setVisibleNewBad(themesPreference.hasNewContentKaaba)
            }
            ThemeType.RAMADHAN -> {
                it.setVisibleNewBad(themesPreference.hasNewContentRamadhan)
            }
            ThemeType.EID_AL_FITR -> {
                it.setVisibleNewBad(themesPreference.hasNewContentEidAlFitr)
            }
            else -> {}
        }
    }
}