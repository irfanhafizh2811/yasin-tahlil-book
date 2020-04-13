package com.dzikir.tasbeeh.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class InterstitialRemoteConfig(firebaseRemoteConfig: FirebaseRemoteConfig) {

    companion object {
        const val MAX_EVENT_COUNT = "interstitial_max_event_count"
        const val NEW_PAGE_COUNT = "interstitial_new_page_count"
    }

    var maxEventCount = firebaseRemoteConfig.getLong(MAX_EVENT_COUNT)
        .toInt()

    var newPageCount = firebaseRemoteConfig.getLong(NEW_PAGE_COUNT)
        .toInt()

}