package com.quran.almulk.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.quran.almulk.extension.common.clazz
import com.quran.almulk.model.DeveloperApp

class SourceAppsRemoteConfig(firebaseRemoteConfig: FirebaseRemoteConfig) {

    companion object {
        const val SOURCE_DEVELOPER_APPS = "source_developer_apps"
    }

    var developerApp = Gson().fromJson(
        firebaseRemoteConfig.getString(SOURCE_DEVELOPER_APPS)
        , clazz<DeveloperApp>()
    )

}