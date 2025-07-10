package com.app_muslim.surah_yasin.data.repository

import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.gender.Gender

interface UserRepository {
    fun getUser(): User
    fun setInitialization(hasInitialization: Boolean)
    fun setName(name: String)
    fun setGender(gender: Gender)
}