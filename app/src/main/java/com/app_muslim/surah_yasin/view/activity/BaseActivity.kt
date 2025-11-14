package com.app_muslim.surah_yasin.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.app_muslim.surah_yasin.BuildConfig
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.app.App
import com.app_muslim.surah_yasin.data.model.TimerEvent
import com.app_muslim.surah_yasin.data.preference.GuidePreference
import com.app_muslim.surah_yasin.extension.activty.hasPermissions
import com.app_muslim.surah_yasin.extension.ads.loadAd
import com.app_muslim.surah_yasin.extension.ads.loadAdMob
import com.app_muslim.surah_yasin.extension.view.gone
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    //---------------------- End Access Public ----------------------

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestConfiguration = when (isTestAdmob()) {
            true -> {
                RequestConfiguration.Builder()
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
        }
    }

    override fun onStart() {
        super.onStart()
        App.instance.eventBus.register(this)
    }

    override fun onPause() {
        super.onPause()
        App.instance.eventBus.unregister(this)
    }

    protected fun isTestAdmob(): Boolean {
        return (BuildConfig.BUILD_TYPE == "debug" && BuildConfig.FLAVOR == "production")
    }

    protected fun isProductionRelease(): Boolean {
        return (BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "production")
    }

    fun loadAdMobInterstitial() {
        if (isProductionRelease()) loadAd(this@BaseActivity) {
            mInterstitialAd = it
            mInterstitialAd?.fullScreenContentCallback = object :
                FullScreenContentCallback() {

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    mInterstitialAd = null
                }
            }
            mInterstitialAd?.show(this)
        }
    }

    protected fun loadBanner(adViewContainer: FrameLayout) = if (isProductionRelease()) {
        adViewContainer.removeAllViews()
        val adView = AdView(this@BaseActivity).apply {
            setAdSize(getAdBannerSize())
            adUnitId = getString(R.string.id_unit_banner)
        }
        adView.loadAdMob(this@BaseActivity, getAdBannerSize()) {}
        adViewContainer.addView(adView)
    } else {
        adViewContainer.gone()
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TimerEvent) {
        loadAdMobInterstitial()
    }
}