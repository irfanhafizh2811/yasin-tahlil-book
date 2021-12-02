package com.icaali.tasbeeh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.icaali.tasbeeh.database.table.Dhikr
import com.icaali.tasbeeh.repository.DhikrRepository

class DhikrViewModelFactory(private val repository: DhikrRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Dhikr::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DhikrViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}