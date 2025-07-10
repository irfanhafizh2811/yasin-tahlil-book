package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.repository.UserRepository
import com.app_muslim.surah_yasin.extension.data.asLiveData
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user = _user.asLiveData()

    fun getUser() {
        viewModelScope.launch(dispatcher.io()) {
            _user.postValue(userRepository.getUser())
        }
    }
}