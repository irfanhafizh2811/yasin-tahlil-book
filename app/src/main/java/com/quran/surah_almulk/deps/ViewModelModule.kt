package com.quran.surah_almulk.deps

import com.quran.surah_almulk.vm.DhikrViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get())  }
}