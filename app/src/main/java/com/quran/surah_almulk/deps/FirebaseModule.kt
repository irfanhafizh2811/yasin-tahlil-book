package com.quran.surah_almulk.deps

import com.quran.surah_almulk.remote.CoreRemoteConfig
import com.quran.surah_almulk.remote.InterstitialRemoteConfig
import com.quran.surah_almulk.remote.SourceAppsRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    single { SourceAppsRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}