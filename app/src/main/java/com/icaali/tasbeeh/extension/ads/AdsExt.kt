package com.icaali.tasbeeh.extension.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.common.clazz
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*

@SuppressLint("HardwareIds")
fun AdView.loadAdMob(
    context: Context,
    adSize: AdSize,
    onAdLoaded: () -> Unit
) =
    try {
        adUnitId = context.getString(R.string.id_unit_banner)
        val extras = Bundle()
        extras.putString("max_ad_content_rating", "G")
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(clazz<AdMobAdapter>(), extras)
            .build()
        setAdSize(adSize)
        loadAd(adRequest)
        adListener = object : AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                onAdLoaded.invoke()
                Log.d(clazz<AdView>().name, "onAdLoaded")
            }

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

@SuppressLint("HardwareIds")
fun AdView.loadAdMobTest(
    context: Context,
    adSize: AdSize,
    onAdLoaded: () -> Unit
) {
    try {
        adUnitId = context.getString(R.string.id_unit_banner_sample_test)
        val deviceId = "3200cbcb466bb599"
        val extras = Bundle()
        extras.putString("max_ad_content_rating", "G")
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(clazz<AdMobAdapter>(), extras)
            .build()
        setAdSize(adSize)
        loadAd(adRequest)
        adListener = object : AdListener() {
            override fun onAdLoaded() {
                onAdLoaded.invoke()
                Log.d(clazz<AdView>().name, "onAdLoaded")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d(clazz<AdView>().name, "onAdFailedToLoad : $errorCode")
            }

            override fun onAdOpened() {
                Log.d(clazz<AdView>().name, "onAdOpened")
            }

            override fun onAdClicked() {
                Log.d(clazz<AdView>().name, "onAdClicked")
            }

            override fun onAdLeftApplication() {
                Log.d(clazz<AdView>().name, "onAdLeftApplication")
            }

            override fun onAdClosed() {
                Log.d(clazz<AdView>().name, "onAdClosed")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun InterstitialAd.loadAd(context: Context, onAdLoaded: () -> Unit) {
    try {
        adUnitId = context.getString(R.string.id_unit_interstitial)
        val extras = Bundle()
        extras.putString("max_ad_content_rating", "G")
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(clazz<AdMobAdapter>(), extras)
            .build()
        if (!adRequest.isTestDevice(context)) {
            loadAd(adRequest)
            adListener = object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    onAdLoaded.invoke()
                    Log.d(clazz<AdView>().name, "onAdLoaded")
                    show()
                }

            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun InterstitialAd.loadAdMobTest(context: Context, onAdLoaded: () -> Unit) {
    try {
        adUnitId = context.getString(R.string.id_unit_interstitial_sample_test)
        val extras = Bundle()
        extras.putString("max_ad_content_rating", "G")
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(clazz<AdMobAdapter>(), extras)
            .build()
        loadAd(adRequest)
        adListener = object : AdListener() {
            override fun onAdLoaded() {
                onAdLoaded.invoke()
                show()
                Log.d(clazz<AdView>().name, "onAdLoaded")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d(clazz<AdView>().name, "onAdFailedToLoad : $errorCode")
            }

            override fun onAdOpened() {
                Log.d(clazz<AdView>().name, "onAdOpened")
            }

            override fun onAdClicked() {
                Log.d(clazz<AdView>().name, "onAdClicked")
            }

            override fun onAdLeftApplication() {
                Log.d(clazz<AdView>().name, "onAdLeftApplication")
            }

            override fun onAdClosed() {
                Log.d(clazz<AdView>().name, "onAdClosed")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}