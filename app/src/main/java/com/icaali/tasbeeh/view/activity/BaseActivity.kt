package com.icaali.tasbeeh.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.icaali.tasbeeh.BuildConfig
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.ads.loadAd
import com.icaali.tasbeeh.extension.ads.loadAdMob
import com.icaali.tasbeeh.extension.ads.loadAdMobTest

open class BaseActivity : AppCompatActivity() {

    protected lateinit var requestConfiguration: RequestConfiguration
    protected val mInterstitialAd: InterstitialAd by lazy { InterstitialAd(this) }
    protected val mAdView: AdView by lazy { AdView(this) }

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

    protected fun loadBanner(adViewContainer: FrameLayout) {
        adViewContainer.apply {
            removeAllViews()
            addView(mAdView)
            with(mAdView) {
                when (isTestAdmob()) {
                    true -> {
                        loadAdMobTest(this@BaseActivity, getAdBannerSize()) {}
                    }
                    else -> {
                        if (isProductionRelease())
                            loadAdMob(this@BaseActivity, getAdBannerSize()) {}
                    }
                }
            }
        }
    }

    private fun getAdBannerSize(): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return when (resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> AdSize.getPortraitAnchoredAdaptiveBannerAdSize(
                this,
                adWidth
            )
            Configuration.ORIENTATION_LANDSCAPE -> AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(
                this,
                adWidth
            )
            else -> AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                this,
                adWidth
            )
        }
    }

}