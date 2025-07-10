package com.app_muslim.surah_yasin.view.activity

import com.app_muslim.surah_yasin.data.model.theme.ThemeType
import com.app_muslim.surah_yasin.data.preference.ThemesPreference
import com.app_muslim.surah_yasin.utils.ActionSurvey
import com.app_muslim.surah_yasin.view.dialog.SurveyDialog

fun TasbeehActivity.updateNewThemePref(theme: com.app_muslim.surah_yasin.data.model.theme.Theme, themesPreference: ThemesPreference) {
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
