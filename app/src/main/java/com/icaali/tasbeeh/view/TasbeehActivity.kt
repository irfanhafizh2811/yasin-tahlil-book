package com.icaali.tasbeeh.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.common.TextUtils
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.InterstitialPreference
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
    private val confirmationDialog by lazy { ConfirmationDialog(this) }

    companion object {
        const val THROTTLE_FIRST = 100L
        const val TYPE_EXTRA = "TYPE_EXTRA"
        const val TASBEEH_LATIN_EXTRA = "TASBEEH_LATIN_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent?.getStringExtra(TYPE_EXTRA) ?: TextUtils.BLANK
        setContentView(R.layout.activity_tasbeeh)
        ivBack?.setOnClickListener { finish() }
        fabReset?.setOnClickListener {
            ViewAnimator.animate(fabReset)
                .pulse()
                .start()
            confirmationDialog.apply {
                setOnDismissListener { loadAdMobInterstitial() }
                setOnPositiveListener { reset() }
            }.show()
        }
        tvDzikir?.text = intent?.getStringExtra(TASBEEH_LATIN_EXTRA)
        ivDzikir?.setImageDrawable(getDzikirImage())
        initTasbeeh()
        disposable.add(
            RxView.clicks(fabCount)
                .throttleFirst(
                    THROTTLE_FIRST,
                    TimeUnit.MILLISECONDS,
                    AndroidSchedulers.mainThread()
                )
                .subscribe {
                    ViewAnimator.animate(fabCount)
                        .pulse()
                        .duration(THROTTLE_FIRST)
                        .start()
                    count()
                }
        )
        loadBanner(adViewContainer)
    }

    private fun initTasbeeh() {
        with(counterPreference) {
            val value = when (type) {
                Tasbeeh.SUBHANALLAH -> subhanallah
                Tasbeeh.ALHAMDULILLAH -> alhamdulillah
                Tasbeeh.LAILAHAILALLAH -> lailahailallah
                Tasbeeh.ALLAHU_AKBAR -> allahukkbar
                Tasbeeh.ASTAGHFIRULLAH -> astaghfirullah
                else -> 0
            }
            setTextCounter(value)
        }
    }

    private fun getDzikirImage(): Drawable? {
        return when (type) {
            Tasbeeh.SUBHANALLAH -> getDrawableCompat(R.drawable.ic_subhanallah)
            Tasbeeh.ALHAMDULILLAH -> getDrawableCompat(R.drawable.ic_alhamdulillah)
            Tasbeeh.ALLAHU_AKBAR -> getDrawableCompat(R.drawable.ic_allahu_akbar)
            Tasbeeh.ASTAGHFIRULLAH -> getDrawableCompat(R.drawable.ic_astagfirllah)
            Tasbeeh.LAILAHAILALLAH -> getDrawableCompat(R.drawable.ic_laailaahaillallah)
            else -> getDrawableCompat(R.drawable.ic_subhanallah)
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
            Tasbeeh.ASTAGHFIRULLAH -> {
                counterPreference.astaghfirullah = 0
            }
        }
        setTextCounter(0)
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
                Tasbeeh.ASTAGHFIRULLAH -> {
                    astaghfirullah += 1
                    astaghfirullah
                }
                else -> 0
            }
            setTextCounter(count)
        }
    }

    private fun setTextCounter(counter: Int) {
        val textCounter = counter.toString()
        tvHintCounter?.text = when (textCounter.length == 1 &&
                textCounter.contains("1")) {
            false -> getString(R.string.label_text_counter_hint)
            else -> {
                val text = getString(R.string.label_text_counter_hint)
                textCounter.forEachIndexed { index, c ->
                    if (c.toInt() == 1)
                        text.replaceRange(index, index, 1.toString())
                }
                text
            }
        }
        tvCounter?.text = when {
            counter <= 0 -> TextUtils.BLANK
            else -> counter.toString()
        }
    }

    override fun onBackPressed() {
        when {
            mInterstitialAd.isLoaded -> {
                loadAdMobInterstitial()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}