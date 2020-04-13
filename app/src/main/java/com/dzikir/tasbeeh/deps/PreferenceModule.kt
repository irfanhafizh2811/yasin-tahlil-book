package com.dzikir.tasbeeh.deps

import com.dzikir.tasbeeh.preference.CorePreference
import com.dzikir.tasbeeh.preference.CounterPreference
import com.dzikir.tasbeeh.preference.InterstitialPreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val prefModule = module {
    single { CorePreference.getInstance(androidContext()) }
    single { InterstitialPreference(get(), get()) }
    single { CounterPreference(get()) }
}