package com.quran.almulk.deps

import com.quran.almulk.remote.CoreRemoteConfig
import com.quran.almulk.remote.InterstitialRemoteConfig
import com.quran.almulk.remote.SourceAppsRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    single { SourceAppsRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}