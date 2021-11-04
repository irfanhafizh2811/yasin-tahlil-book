package com.icaali.tasbeeh.preference

class SettingPreference(val corePreference: CorePreference) {

    companion object {
        private const val VIBRATION = "vibration"
        private const val SOUND = "sound"
        private const val SHOW_POPUP_AGAIN = "show_popup_again"
    }

    var vibrate: Boolean
        set(value) = corePreference.setBoolean(VIBRATION, value)
        get() = corePreference.getBoolean(VIBRATION, true)

    var sound: Boolean
        set(value) = corePreference.setBoolean(SOUND, value)
        get() = corePreference.getBoolean(SOUND, true)

    var showPopupAgain: Boolean
        set(value) = corePreference.setBoolean(SHOW_POPUP_AGAIN, value)
        get() = corePreference.getBoolean(SHOW_POPUP_AGAIN, true)

}