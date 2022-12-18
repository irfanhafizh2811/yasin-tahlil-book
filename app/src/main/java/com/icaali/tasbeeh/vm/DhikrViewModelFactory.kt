package com.icaali.tasbeeh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.repository.TasbeehRepository

class DhikrViewModelFactory(private val repository: TasbeehRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Tasbeeh::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DhikrViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}