package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.data.preference.Language
import com.app_muslim.surah_yasin.data.preference.LanguagePreference
import com.app_muslim.surah_yasin.extension.data.asLiveData
import kotlinx.coroutines.launch

class LanguageViewModel(private val languagePreference: LanguagePreference) : BaseViewModel() {

    private val _language = MutableLiveData<Language>()
    val language = _language.asLiveData()

    fun setupLanguage(language: Language) {
        viewModelScope.launch(dispatcher.io()) {
            languagePreference.language = language
            _language.postValue(language)
        }
    }

    fun getLanguage() {
        viewModelScope.launch(dispatcher.io()) {
            _language.postValue(languagePreference.language)
        }
    }
}