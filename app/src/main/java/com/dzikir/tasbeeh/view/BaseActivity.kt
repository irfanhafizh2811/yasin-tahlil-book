package com.dzikir.tasbeeh.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dzikir.tasbeeh.BuildConfig
import com.dzikir.tasbeeh.R
import com.dzikir.tasbeeh.extension.ads.loadAd
import com.dzikir.tasbeeh.extension.ads.loadAdMobTest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

open class BaseActivity : AppCompatActivity() {

    protected lateinit var requestConfiguration: RequestConfiguration
    private val mInterstitialAd: InterstitialAd by lazy { InterstitialAd(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestConfiguration = when (isTestAdmob()) {
            true -> {
                val deviceId = "3200cbcb466bb599"
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(deviceId))
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build()
            }
            else -> {
                RequestConfiguration.Builder()
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build()
            }

        }
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(this, getString(R.string.id_addmob))
    }

    protected fun isTestAdmob(): Boolean {
        return (BuildConfig.BUILD_TYPE == "debug" && BuildConfig.FLAVOR == "production")
    }

    protected fun isProductionRelease(): Boolean {
        return (BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "production")
    }

    fun loadAdMobInterstitial(){
        with(mInterstitialAd) {
            when (isTestAdmob()) {
                true -> {
                    loadAdMobTest(this@BaseActivity) {

                    }
                }
                else -> {
                    if (isProductionRelease())
                        loadAd(this@BaseActivity) { }
                }
            }
        }
    }

}