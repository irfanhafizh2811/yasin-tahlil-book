package com.quran.almulk.deps

import com.quran.almulk.data.preference.CorePreference
import com.quran.almulk.data.preference.CounterPreference
import com.quran.almulk.data.preference.GuidePreference
import com.quran.almulk.data.preference.InterstitialPreference
import com.quran.almulk.data.preference.LanguagePreference
import com.quran.almulk.data.preference.SettingPreference
import com.quran.almulk.data.preference.SurahPreference
import com.quran.almulk.data.preference.ThemesPreference
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