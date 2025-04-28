package com.quran.surah_almulk.data.model

import com.quran.surah_almulk.data.model.gender.Gender

data class User(
    var hasInitialize: Boolean = false,
    var name: String = "",
    var gender: Gender = Gender.MALE,
)
