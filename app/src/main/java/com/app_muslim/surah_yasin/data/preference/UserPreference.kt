package com.app_muslim.surah_yasin.data.preference

import com.app_muslim.surah_yasin.data.model.gender.Gender
import com.app_muslim.surah_yasin.utils.TextUtils

class UserPreference(val corePreference: CorePreference) {

    companion object {
        const val INITIALIZATION = "USER_INITIALIZATION"
        const val NAME = "USER_NAME"
        const val GENDER = "USER_GENDER"
    }

    var initialization: Boolean
        set(value) = corePreference.setBoolean(INITIALIZATION, value)
        get() = corePreference.getBoolean(INITIALIZATION, false)

    var name: String
        set(value) = corePreference.setString(NAME, value)
        get() = corePreference.getString(NAME, TextUtils.BLANK)

    var gender: Gender
        set(value) = corePreference.setString(GENDER, value.name)
        get() = Gender.valueOf(corePreference.getString(GENDER, Gender.MALE.name))
}