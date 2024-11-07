package com.icaali.almulk.deps

import com.icaali.almulk.remote.CoreRemoteConfig
import com.icaali.almulk.remote.InterstitialRemoteConfig
import com.icaali.almulk.remote.SourceAppsRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    single { SourceAppsRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}