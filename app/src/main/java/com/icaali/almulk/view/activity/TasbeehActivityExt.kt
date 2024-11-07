package com.icaali.almulk.view.activity

import com.icaali.almulk.preference.ThemesPreference
import com.icaali.almulk.utils.ActionSurvey
import com.icaali.almulk.view.dialog.SurveyDialog
import com.icaali.almulk.view.theme.Theme
import com.icaali.almulk.view.theme.ThemeType

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
