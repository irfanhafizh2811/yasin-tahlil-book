package com.icaali.almulk.view.activity

import com.icaali.almulk.data.preference.GuidePreference
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