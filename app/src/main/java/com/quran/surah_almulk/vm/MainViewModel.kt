package com.quran.surah_almulk.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quran.surah_almulk.data.model.User
import com.quran.surah_almulk.data.repository.UserRepository
import com.quran.surah_almulk.extension.data.asLiveData
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