package com.icaali.tasbeeh.deps

import com.icaali.tasbeeh.preference.CorePreference
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.InterstitialPreference
import com.icaali.tasbeeh.preference.ThemesPreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val prefModule = module {
    single { CorePreference.getInstance(androidContext()) }
    single { InterstitialPreference(get(), get()) }
    single { CounterPreference(get()) }
    single { ThemesPreference(get()) }
}