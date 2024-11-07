package com.icaali.almulk.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class CoreRemoteConfig {

    var remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    companion object {
        const val MINIMUM_FETCH_INTERVAL_IN_SECONDS = 720L
    }

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_IN_SECONDS)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

}