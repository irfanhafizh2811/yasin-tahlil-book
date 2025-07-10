package com.app_muslim.surah_yasin.deps

import com.app_muslim.surah_yasin.data.preference.CorePreference
import com.app_muslim.surah_yasin.data.preference.CounterPreference
import com.app_muslim.surah_yasin.data.preference.GuidePreference
import com.app_muslim.surah_yasin.data.preference.InterstitialPreference
import com.app_muslim.surah_yasin.data.preference.LanguagePreference
import com.app_muslim.surah_yasin.data.preference.SettingPreference
import com.app_muslim.surah_yasin.data.preference.SurahPreference
import com.app_muslim.surah_yasin.data.preference.ThemesPreference
import com.app_muslim.surah_yasin.data.preference.UserPreference
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