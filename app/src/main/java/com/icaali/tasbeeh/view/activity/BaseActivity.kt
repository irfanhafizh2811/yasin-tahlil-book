package com.icaali.tasbeeh.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.icaali.tasbeeh.BuildConfig
import com.icaali.tasbeeh.utils.TextUtils
import com.icaali.tasbeeh.extension.activty.hasPermissions
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
    protected var mInterstitialAd: InterstitialAd? = null
    protected val mAdView: AdView by lazy { AdView(this) }
    protected val mDisposable = CompositeDisposable()
    //---------------------- End Access Protected ----------------------

    //----------------------   Access Public   ----------------------
    var firebaseAnalytics: FirebaseAnalytics? = null
    var localeManager: LocaleManager? = null
    //---------------------- End Access Public ----------------------

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            localeManager =
                getSystemService(Context.LOCALE_SERVICE) as LocaleManager
        }
        requestConfiguration = when (isTestAdmob()) {
            true -> {
                val deviceId = "F479D985133C6B3E3794DD9D1EF08219"
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(deviceId))
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE)
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
                    .build()
            }
            else -> {
                RequestConfiguration.Builder()
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE)
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
                    .build()
            }
        }
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(this) {

        }
        if (hasPermissions(
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WAKE_LOCK
                )
            )
        ) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            firebaseAnalytics?.setUserId(TextUtils.NA)
        }
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