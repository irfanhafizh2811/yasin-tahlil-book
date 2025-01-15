package com.icaali.almulk.deps

import com.icaali.almulk.data.preference.CorePreference
import com.icaali.almulk.data.preference.CounterPreference
import com.icaali.almulk.data.preference.GuidePreference
import com.icaali.almulk.data.preference.InterstitialPreference
import com.icaali.almulk.data.preference.LanguagePreference
import com.icaali.almulk.data.preference.SettingPreference
import com.icaali.almulk.data.preference.SurahPreference
import com.icaali.almulk.data.preference.ThemesPreference
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