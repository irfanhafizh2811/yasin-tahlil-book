package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.*
import com.app_muslim.surah_yasin.data.database.entity.TasbeehEntity
import com.app_muslim.surah_yasin.data.repository.TasbeehRepository
import kotlinx.coroutines.launch

class DhikrViewModel(private val repository: TasbeehRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val dhikrs: LiveData<List<TasbeehEntity>> = repository.dhikrs.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(dhikr: TasbeehEntity) = viewModelScope.launch {
        repository.insert(dhikr)
    }

    fun update(dhikr: TasbeehEntity) = viewModelScope.launch {
        repository.update(dhikr)
    }

    fun delete(dhikr: TasbeehEntity) = viewModelScope.launch {
        repository.delete(dhikr)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}