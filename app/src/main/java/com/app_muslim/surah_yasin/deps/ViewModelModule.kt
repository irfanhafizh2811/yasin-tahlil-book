package com.app_muslim.surah_yasin.deps

import com.app_muslim.surah_yasin.vm.DhikrViewModel
import com.app_muslim.surah_yasin.vm.LanguageViewModel
import com.app_muslim.surah_yasin.vm.MainViewModel
import com.app_muslim.surah_yasin.vm.OnBoardViewModel
import com.app_muslim.surah_yasin.vm.SurahViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DhikrViewModel(get()) }
    viewModel { OnBoardViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SurahViewModel(get()) }
    viewModel { LanguageViewModel(get()) }
}