package com.quran.surah_yasin.deps

import com.quran.surah_yasin.vm.DhikrViewModel
import com.quran.surah_yasin.vm.MainViewModel
import com.quran.surah_yasin.vm.OnBoardViewModel
import com.quran.surah_yasin.vm.SurahViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get())  }
    viewModel { OnBoardViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SurahViewModel(get()) }
}