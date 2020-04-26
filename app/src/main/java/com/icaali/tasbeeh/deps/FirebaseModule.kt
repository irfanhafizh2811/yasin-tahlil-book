package com.icaali.tasbeeh.deps

import com.icaali.tasbeeh.remote.CoreRemoteConfig
import com.icaali.tasbeeh.remote.InterstitialRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}