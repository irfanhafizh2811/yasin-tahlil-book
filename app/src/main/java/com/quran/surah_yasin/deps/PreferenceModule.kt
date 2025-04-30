package com.quran.surah_yasin.deps

import com.quran.surah_yasin.data.preference.CorePreference
import com.quran.surah_yasin.data.preference.CounterPreference
import com.quran.surah_yasin.data.preference.GuidePreference
import com.quran.surah_yasin.data.preference.InterstitialPreference
import com.quran.surah_yasin.data.preference.LanguagePreference
import com.quran.surah_yasin.data.preference.SettingPreference
import com.quran.surah_yasin.data.preference.SurahPreference
import com.quran.surah_yasin.data.preference.ThemesPreference
import com.quran.surah_yasin.data.preference.UserPreference
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