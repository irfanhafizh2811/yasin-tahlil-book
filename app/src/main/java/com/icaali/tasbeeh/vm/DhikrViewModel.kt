package com.icaali.tasbeeh.vm

import androidx.lifecycle.*
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.repository.TasbeehRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DhikrViewModel(private val repository: TasbeehRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val dhikrs: LiveData<List<Tasbeeh>> = repository.dhikrs.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(dhikr: Tasbeeh) = viewModelScope.launch {
        repository.insert(dhikr)
    }

    fun update(dhikr: Tasbeeh) = viewModelScope.launch {
        repository.update(dhikr)
    }

    fun delete(dhikr: Tasbeeh) = viewModelScope.launch {
        repository.delete(dhikr)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}