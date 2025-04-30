package com.quran.surah_yasin.data.model

import com.quran.surah_yasin.data.model.gender.Gender

data class User(
    var hasInitialize: Boolean = false,
    var name: String = "",
    var gender: Gender = Gender.MALE,
)
