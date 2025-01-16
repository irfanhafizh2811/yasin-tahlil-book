package com.quran.almulk.deps

import com.quran.almulk.vm.DhikrViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get())  }
}