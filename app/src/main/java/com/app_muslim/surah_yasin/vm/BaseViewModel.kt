package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app_muslim.surah_yasin.coroutine.DispatcherProvider
import com.app_muslim.surah_yasin.extension.data.asLiveData
import org.koin.java.KoinJavaComponent.inject

open class BaseViewModel : ViewModel() {

    protected val dispatcher by inject<DispatcherProvider>(DispatcherProvider::class.java)

    protected var _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState.asLiveData()

    protected fun setLoadingState(state: Boolean) {
        _loadingState.postValue(state)
    }

}