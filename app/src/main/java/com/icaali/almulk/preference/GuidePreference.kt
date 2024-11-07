package com.icaali.almulk.preference

class GuidePreference(val corePreference: CorePreference) {

    companion object {
        const val SKIP_MAIN_GUIDE = "SKIP_MAIN_GUIDE"
        const val HAS_SHOW_ADD_DHIKR = "HAS_SHOW_ADD_DHIKR"
        const val HAS_SHOW_PICK_THEME = "HAS_SHOW_CHANGE_THEME"

        const val SKIP_TASBEEH_GUIDE = "SKIP_TASBEEH_GUIDE"
        const val HAS_SHOW_VIBRATE_SOUND = "HAS_SHOW_VIBRATE_SOUND"
        const val HAS_SHOW_DHIKR_TARGET = "HAS_SHOW_DHIKR_TARGET"
    }

    var skipMainGuide: Boolean
        set(value) = corePreference.setBoolean(SKIP_MAIN_GUIDE, value)
        get() = corePreference.getBoolean(SKIP_MAIN_GUIDE, false)

    var skipTasbeehGuide: Boolean
        set(value) = corePreference.setBoolean(SKIP_TASBEEH_GUIDE, value)
        get() = corePreference.getBoolean(SKIP_TASBEEH_GUIDE, false)

    var hasShownAddDhikr: Boolean
        set(value) = corePreference.setBoolean(HAS_SHOW_ADD_DHIKR, value)
        get() = corePreference.getBoolean(HAS_SHOW_ADD_DHIKR, false)

    var hasShownPickTheme: Boolean
        set(value) = corePreference.setBoolean(HAS_SHOW_PICK_THEME, value)
        get() = corePreference.getBoolean(HAS_SHOW_PICK_THEME, false)

    var hasShownVibrateSound: Boolean
        set(value) = corePreference.setBoolean(HAS_SHOW_VIBRATE_SOUND, value)
        get() = corePreference.getBoolean(HAS_SHOW_VIBRATE_SOUND, false)

    var hasShownDhikrTarget: Boolean
        set(value) = corePreference.setBoolean(HAS_SHOW_DHIKR_TARGET, value)
        get() = corePreference.getBoolean(HAS_SHOW_DHIKR_TARGET, false)
}