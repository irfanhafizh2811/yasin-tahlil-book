package com.dzikir.tasbeeh.view

import android.os.Bundle
import com.dzikir.tasbeeh.R
import com.dzikir.tasbeeh.common.TextUtils
import com.dzikir.tasbeeh.preference.CounterPreference
import com.dzikir.tasbeeh.preference.InterstitialPreference
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_tasbeeh.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class TasbeehActivity : BaseActivity() {

    private val counterPreference by inject<CounterPreference>()
    private val interstitialPreference by inject<InterstitialPreference>()
    private val disposable = CompositeDisposable()
    private var type = TextUtils.BLANK

    companion object {
        const val THROTTLE_FIRST = 350L
        const val TYPE_EXTRA = "TYPE_EXTRA"
        const val TASBEEH_ARABIC_EXTRA = "TASBEEH_ARABIC_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent?.getStringExtra(TYPE_EXTRA) ?: TextUtils.BLANK
        setContentView(R.layout.activity_tasbeeh)
        ivBack?.setOnClickListener { finish() }
        fabReset?.setOnClickListener { reset() }
        tvDzikir?.text = intent?.getStringExtra(TASBEEH_ARABIC_EXTRA)
        initTasbeeh()
        disposable.add(
            RxView.clicks(fabCount)
                .throttleFirst(
                    THROTTLE_FIRST,
                    TimeUnit.MILLISECONDS,
                    AndroidSchedulers.mainThread()
                )
                .subscribe {
                    count()
                    interstitialPreference.countTasbeeh {
                        loadAdMobInterstitial()
                    }
                }
        )
        interstitialPreference.countPage {
            loadAdMobInterstitial()
        }
    }

    private fun initTasbeeh() {
        with(counterPreference) {
            val value = when (type) {
                Tasbeeh.SUBHANALLAH -> subhanallah
                Tasbeeh.ALHAMDULILLAH -> alhamdulillah
                Tasbeeh.LAILAHAILALLAH -> lailahailallah
                Tasbeeh.ALLAHU_AKBAR -> allahukkbar
                else -> 0
            }
            fmDzikir?.setValue(value, false)
        }
    }

    private fun reset() {
        when (type) {
            Tasbeeh.SUBHANALLAH -> {
                counterPreference.subhanallah = 0
            }
            Tasbeeh.ALHAMDULILLAH -> {
                counterPreference.alhamdulillah = 0
            }
            Tasbeeh.LAILAHAILALLAH -> {
                counterPreference.lailahailallah = 0
            }
            Tasbeeh.ALLAHU_AKBAR -> {
                counterPreference.allahukkbar = 0
            }
        }
        fmDzikir.setValue(0, true)
    }

    private fun count() {
        with(counterPreference) {
            val count = when (type) {
                Tasbeeh.SUBHANALLAH -> {
                    subhanallah += 1
                    subhanallah
                }
                Tasbeeh.ALHAMDULILLAH -> {
                    alhamdulillah += 1
                    alhamdulillah
                }
                Tasbeeh.LAILAHAILALLAH -> {
                    lailahailallah += 1
                    lailahailallah
                }
                Tasbeeh.ALLAHU_AKBAR -> {
                    allahukkbar += 1
                    allahukkbar
                }
                else -> 0
            }
            fmDzikir.setValue(count, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}