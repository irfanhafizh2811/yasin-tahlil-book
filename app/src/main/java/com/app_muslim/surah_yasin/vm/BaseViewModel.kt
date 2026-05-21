package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app_muslim.surah_yasin.coroutine.DispatcherProvider
import com.app_muslim.surah_yasin.extension.data.asLiveData

open class BaseViewModel(
    protected val dispatcher: DispatcherProvider
) : ViewModel() {

    protected var _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState.asLiveData()

    protected fun setLoadingState(state: Boolean) {
        _loadingState.postValue(state)
    }

}