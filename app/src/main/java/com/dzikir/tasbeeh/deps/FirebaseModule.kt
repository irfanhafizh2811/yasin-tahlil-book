package com.dzikir.tasbeeh.deps

import com.dzikir.tasbeeh.remote.CoreRemoteConfig
import com.dzikir.tasbeeh.remote.InterstitialRemoteConfig
import org.koin.dsl.module

val firebaseModule = module {
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
}