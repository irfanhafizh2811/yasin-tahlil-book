package com.app_muslim.surah_yasin.data.preference

import com.app_muslim.surah_yasin.utils.FontSize

class SettingPreference(val corePreference: CorePreference) {

    companion object {
        private const val VIBRATION = "vibration"
        private const val SOUND = "sound"
        private const val SHOW_POPUP_AGAIN = "show_popup_again"
        private const val NOTIFICATION = "notification"
        private const val TIME_NOTIFICATION = "time_notification"
        private const val FONT_SIZE = "font_size"
        private const val HAS_RATING = "has_rating"
        private const val SHOW_QURAN_LATIN = "show_quran"
        private const val SHOW_QURAN_TRANSLATION = "show_translation"
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

    var notification: Boolean
        set(value) = corePreference.setBoolean(NOTIFICATION, value)
        get() = corePreference.getBoolean(NOTIFICATION, true)

    var noHasSubmitRating: Boolean
        set(value) = corePreference.setBoolean(HAS_RATING, value)
        get() = corePreference.getBoolean(HAS_RATING, true)

    var timeNotification: Long
        set(value) = corePreference.setLong(TIME_NOTIFICATION, value)
        get() = corePreference.getLong(TIME_NOTIFICATION, 0)

    var fontSize: FontSize
        set(value) = corePreference.setString(FONT_SIZE, value.name)
        get() = FontSize.valueOf(corePreference.getString(FONT_SIZE, FontSize.REGULAR.name))

    var showQuranLatin: Boolean
        set(value) = corePreference.setBoolean(SHOW_QURAN_LATIN, value)
        get() = corePreference.getBoolean(SHOW_QURAN_LATIN, true)

    var showQuranTranslation: Boolean
        set(value) = corePreference.setBoolean(SHOW_QURAN_TRANSLATION, value)
        get() = corePreference.getBoolean(SHOW_QURAN_TRANSLATION, true)
}