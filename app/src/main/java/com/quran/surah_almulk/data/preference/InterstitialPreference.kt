package com.quran.surah_almulk.data.preference

import com.quran.surah_almulk.remote.InterstitialRemoteConfig

class InterstitialPreference(
    val corePreference: CorePreference,
    val interstitialRemoteConfig: InterstitialRemoteConfig
) {

    companion object {
        const val EVENT_TASBEEH_COUNT = "interstitial_event_count"
        const val EVENT_PAGE_COUNT = "interstitial_event_count"
    }

    fun countTasbeeh(listenerEventInterstitial: () -> Unit) {
        val eventCount = corePreference.getInt(EVENT_TASBEEH_COUNT, 0)
        val maxEventCount = interstitialRemoteConfig.maxEventCount
        corePreference.setInt(EVENT_TASBEEH_COUNT, eventCount + 1)
        if (eventCount >= maxEventCount) {
            listenerEventInterstitial.invoke()
            corePreference.setInt(EVENT_TASBEEH_COUNT, 0)
        }
    }

    fun countPage(listenerEventInterstitial: () -> Unit) {
        val eventCount = corePreference.getInt(EVENT_PAGE_COUNT, 0)
        val maxEventCount = interstitialRemoteConfig.newPageCount
        corePreference.setInt(EVENT_PAGE_COUNT, eventCount + 1)
        if (eventCount >= maxEventCount) {
            listenerEventInterstitial.invoke()
            corePreference.setInt(EVENT_PAGE_COUNT, 0)
        }
    }

}