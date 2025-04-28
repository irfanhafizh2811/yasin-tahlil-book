package com.quran.surah_almulk.deps

import com.quran.surah_almulk.data.preference.CorePreference
import com.quran.surah_almulk.data.preference.CounterPreference
import com.quran.surah_almulk.data.preference.GuidePreference
import com.quran.surah_almulk.data.preference.InterstitialPreference
import com.quran.surah_almulk.data.preference.LanguagePreference
import com.quran.surah_almulk.data.preference.SettingPreference
import com.quran.surah_almulk.data.preference.SurahPreference
import com.quran.surah_almulk.data.preference.ThemesPreference
import com.quran.surah_almulk.data.preference.UserPreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val prefModule = module {
    single { CorePreference.getInstance(androidContext()) }
    single { UserPreference(get()) }
    single { InterstitialPreference(get(), get()) }
    single { CounterPreference(get()) }
    single { ThemesPreference(get()) }
    single { SettingPreference(get()) }
    single { GuidePreference(get()) }
    single { SurahPreference(get()) }
    single { LanguagePreference(get()) }
}