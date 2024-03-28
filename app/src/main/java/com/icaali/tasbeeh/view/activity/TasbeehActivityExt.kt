package com.icaali.tasbeeh.view.activity

import com.icaali.tasbeeh.preference.ThemesPreference
import com.icaali.tasbeeh.utils.Analytic
import com.icaali.tasbeeh.view.dialog.SurveyDialog
import com.icaali.tasbeeh.view.theme.Theme
import com.icaali.tasbeeh.view.theme.ThemeType

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
    setOnPositiveListener { inputSurveyTheme(Analytic.LIKE_THEME_FULL_BACKGROUND) }
    setOnNegativeListener { inputSurveyTheme(Analytic.DISLIKE_THEME_FULL_BACKGROUND) }
}

fun TasbeehActivity.inputSurveyTheme(log: String) {
    logClick(log)
    themesPreference.hasThemeSurvey = false
}
