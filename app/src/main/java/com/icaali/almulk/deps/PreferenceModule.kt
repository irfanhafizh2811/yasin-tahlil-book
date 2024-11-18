package com.icaali.almulk.deps

import com.icaali.almulk.preference.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val prefModule = module {
    single { CorePreference.getInstance(androidContext()) }
    single { InterstitialPreference(get(), get()) }
    single { CounterPreference(get()) }
    single { ThemesPreference(get()) }
    single { SettingPreference(get()) }
    single { GuidePreference(get()) }
    single { SurahPreference(get()) }
    single { LanguagePreference(get()) }
}