package com.quran.surah_yasin.data.repository

import com.quran.surah_yasin.data.model.User
import com.quran.surah_yasin.data.model.gender.Gender
import com.quran.surah_yasin.data.preference.UserPreference

class UserRepositoryImpl(private val userPreference: UserPreference) : UserRepository {

    override fun getUser(): User {
        return User(
            hasInitialize = userPreference.initialization,
            name = userPreference.name,
            gender = userPreference.gender,
        )
    }

    override fun setInitialization(hasInitialization: Boolean) {
        userPreference.initialization = hasInitialization
    }

    override fun setName(name: String) {
        userPreference.name = name
    }

    override fun setGender(gender: Gender) {
        userPreference.gender = gender
    }
}