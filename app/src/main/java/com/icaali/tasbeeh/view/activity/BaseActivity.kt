package com.icaali.tasbeeh.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.icaali.tasbeeh.BuildConfig
import com.icaali.tasbeeh.extension.ads.loadAd
import com.icaali.tasbeeh.extension.ads.loadAdMob
import com.icaali.tasbeeh.extension.ads.loadAdMobTest
import com.icaali.tasbeeh.preference.GuidePreference
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val DHIKR_SECOND_DELAY = 2L
    }

    //----------------------   Access Protected   ----------------------
    protected val guidePref: GuidePreference by inject()
    protected lateinit var requestConfiguration: RequestConfiguration
    protected var mInterstitialAd: AdManagerInterstitialAd? = null
    protected val mAdView: AdView by lazy { AdView(this) }
    protected val mDisposable = CompositeDisposable()
    //---------------------- End Access Protected ----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestConfiguration = when (isTestAdmob()) {
            true -> {
                val deviceId = "040A8B4F3C09E831C2C1A355CBBCA3ED"
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
        MobileAds.initialize(this)
    }

    protected fun isTestAdmob(): Boolean {
        return (BuildConfig.BUILD_TYPE == "debug" && BuildConfig.FLAVOR == "production")
    }

    protected fun isProductionRelease(): Boolean {
        return (BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "production")
    }

    fun loadAdMobInterstitial() {
        when (isTestAdmob()) {
            true -> {
                loadAdMobTest(this@BaseActivity) {
                    mInterstitialAd = it
                    mInterstitialAd?.show(this)
                }
            }
            else -> {
                if (isProductionRelease())
                    loadAd(this@BaseActivity) {
                        mInterstitialAd = it
                        mInterstitialAd?.fullScreenContentCallback = object :
                            FullScreenContentCallback() {
                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                mInterstitialAd = null
                            }
                        }
                        mInterstitialAd?.show(this)
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

    override fun onDestroy() {
        mDisposable.dispose()
        super.onDestroy()
    }

}