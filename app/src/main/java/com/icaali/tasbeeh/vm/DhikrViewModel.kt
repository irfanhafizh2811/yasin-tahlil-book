package com.icaali.tasbeeh.vm

import androidx.lifecycle.*
import com.icaali.tasbeeh.database.table.Dhikr
import com.icaali.tasbeeh.repository.DhikrRepository
import kotlinx.coroutines.launch

class DhikrViewModel(private val repository: DhikrRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val dhikrs: LiveData<List<Dhikr>> = repository.dhikrs.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(dhikr: Dhikr) = viewModelScope.launch {
        repository.insert(dhikr)
    }

    fun update(dhikr: Dhikr) = viewModelScope.launch {
        repository.update(dhikr)
    }

    fun delete(dhikr: Dhikr) = viewModelScope.launch {
        repository.delete(dhikr)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}