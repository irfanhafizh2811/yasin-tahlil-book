package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.gender.Gender
import com.app_muslim.surah_yasin.data.repository.UserRepository
import com.app_muslim.surah_yasin.extension.data.asLiveData
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.app_muslim.surah_yasin.coroutine.DispatcherProvider

@HiltViewModel
class OnBoardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: DispatcherProvider
) : BaseViewModel(dispatcher) {

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