package com.quran.surah_almulk.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quran.surah_almulk.data.database.entity.TasbeehEntity
import com.quran.surah_almulk.data.repository.TasbeehRepository

class DhikrViewModelFactory(private val repository: TasbeehRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasbeehEntity::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DhikrViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}