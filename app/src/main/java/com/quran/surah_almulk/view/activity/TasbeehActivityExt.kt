package com.quran.surah_almulk.view.activity

import com.quran.surah_almulk.data.preference.ThemesPreference
import com.quran.surah_almulk.utils.ActionSurvey
import com.quran.surah_almulk.view.dialog.SurveyDialog
import com.quran.surah_almulk.view.theme.Theme
import com.quran.surah_almulk.view.theme.ThemeType

fun TasbeehActivity.updateNewThemePref(theme: Theme, themesPreference: ThemesPreference) {
    when (theme.themeType()) {
        ThemeType.YELLOW -> themesPreference.hasNewContentYellow = false
        ThemeType.KAABA -> themesPreference.hasNewContentKaaba = false
        ThemeType.RAMADHAN -> themesPreference.hasNewContentRamadhan = false
        ThemeType.EID_AL_FITR -> themesPreference.hasNewContentEidAlFitr = false
        else -> {}
    }
}

fun TasbeehActivity.createThemeSurvey() = SurveyDialog(this).apply {
    setTitle("Your Opinion")
    setText(
        titleText = "Your Opinion",
        messageText = "Do you like this theme?",
        positiveText = "Like",
        negativeText = "Dislike"
    )
    setOnPositiveListener { inputSurveyTheme(ActionSurvey.LIKE_THEME_FULL_BACKGROUND) }
    setOnNegativeListener { inputSurveyTheme(ActionSurvey.DISLIKE_THEME_FULL_BACKGROUND) }
}

fun TasbeehActivity.inputSurveyTheme(log: String) {
    themesPreference.hasThemeSurvey = false
}
