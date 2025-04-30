package com.quran.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quran.surah_yasin.data.model.User
import com.quran.surah_yasin.data.model.gender.Gender
import com.quran.surah_yasin.data.repository.UserRepository
import com.quran.surah_yasin.extension.data.asLiveData
import kotlinx.coroutines.launch

class OnBoardViewModel(val userRepository: UserRepository) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user = _user.asLiveData()

    fun getUser() {
        viewModelScope.launch(dispatcher.io()) {
            _user.postValue(userRepository.getUser())
        }
    }

    fun setGender(gender: Gender) {
        viewModelScope.launch(dispatcher.io()) {
            userRepository.setGender(gender)
            userRepository.setInitialization(true)
        }
    }
}