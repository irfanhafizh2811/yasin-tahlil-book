package com.quran.surah_yasin.deps

import com.quran.surah_yasin.remote.CoreRemoteConfig
import com.quran.surah_yasin.remote.InterstitialRemoteConfig
import com.quran.surah_yasin.remote.SourceAppsRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    single { SourceAppsRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}