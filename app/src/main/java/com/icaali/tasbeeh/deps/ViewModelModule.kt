package com.icaali.tasbeeh.deps

import com.icaali.tasbeeh.vm.DhikrViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get())  }
}