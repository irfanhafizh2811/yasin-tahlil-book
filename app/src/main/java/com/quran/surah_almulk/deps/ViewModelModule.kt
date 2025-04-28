package com.quran.surah_almulk.deps

import com.quran.surah_almulk.vm.DhikrViewModel
import com.quran.surah_almulk.vm.MainViewModel
import com.quran.surah_almulk.vm.OnBoardViewModel
import com.quran.surah_almulk.vm.SurahViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get())  }
    viewModel { OnBoardViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SurahViewModel(get()) }
}