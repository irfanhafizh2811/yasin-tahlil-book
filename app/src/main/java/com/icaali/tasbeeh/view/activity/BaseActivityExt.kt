package com.icaali.tasbeeh.view.activity

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.icaali.tasbeeh.preference.GuidePreference
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun BaseActivity.observeGuide(
    secondDelay: Long,
    guidePref: GuidePreference,
    onGuideListener: () -> Unit
): Disposable = Completable.complete()
    .delay(secondDelay, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
    .doOnComplete {
        onGuideListener.invoke()
    }.subscribe()

fun BaseActivity.logAnalytic(event: String, key: String, value: String) {
    val bundle = Bundle()
    bundle.putString(key, value)
    firebaseAnalytics?.logEvent(event, bundle)
}

fun BaseActivity.logSelectContent(value: String) {
    logAnalytic(
        FirebaseAnalytics.Event.SELECT_CONTENT,
        FirebaseAnalytics.Param.CONTENT,
        value
    )
}

fun BaseActivity.logCount(value: String) {
    logAnalytic(
        FirebaseAnalytics.Event.SELECT_ITEM,
        FirebaseAnalytics.Param.ITEM_NAME,
        value
    )
}

fun BaseActivity.logTheme(value: String) {
    logAnalytic(
        FirebaseAnalytics.Event.VIEW_PROMOTION,
        FirebaseAnalytics.Param.CREATIVE_NAME,
        value
    )
}

fun BaseActivity.logAdd(value: String) {
    logAnalytic(
        FirebaseAnalytics.Event.ADD_TO_CART,
        FirebaseAnalytics.Param.VALUE,
        value
    )
}