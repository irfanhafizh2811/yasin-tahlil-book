package com.app_muslim.surah_yasin.extension.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.extension.common.clazz
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.app_muslim.surah_yasin.view.activity.BaseActivity

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

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
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

            override fun onAdOpened() {
                Log.d(clazz<AdView>().name, "onAdOpened")
            }

            override fun onAdClicked() {
                Log.d(clazz<AdView>().name, "onAdClicked")
            }

            override fun onAdClosed() {
                Log.d(clazz<AdView>().name, "onAdClosed")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun BaseActivity.loadAd(context: Context, onAdLoaded: (AdManagerInterstitialAd) -> Unit) {
    try {
        AdManagerInterstitialAd.load(
            context,
            context.getString(R.string.id_unit_interstitial),
            AdManagerAdRequest.Builder().build(),
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: AdManagerInterstitialAd) {
                    super.onAdLoaded(p0)
                    onAdLoaded.invoke(p0)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun BaseActivity.loadAdMobTest(context: Context, onAdLoaded: (AdManagerInterstitialAd) -> Unit) {
    try {
        AdManagerInterstitialAd.load(
            context,
            context.getString(R.string.id_unit_interstitial_sample_test),
            AdManagerAdRequest.Builder().build(),
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: AdManagerInterstitialAd) {
                    super.onAdLoaded(p0)
                    onAdLoaded.invoke(p0)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}