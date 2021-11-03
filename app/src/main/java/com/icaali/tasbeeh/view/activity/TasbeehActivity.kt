package com.icaali.tasbeeh.view.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.common.TextUtils
import com.icaali.tasbeeh.extension.activty.openPlaystore
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.InterstitialPreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.preference.ThemesPreference
import com.icaali.tasbeeh.view.Tasbeeh
import com.icaali.tasbeeh.view.dialog.ConfirmationDialog
import com.icaali.tasbeeh.view.dialog.MoreDialog
import com.icaali.tasbeeh.view.dialog.MoreTasbeehDialog
import com.icaali.tasbeeh.view.theme.Theme
import com.icaali.tasbeeh.view.theme.ThemeFactory
import com.icaali.tasbeeh.view.theme.ThemeType
import com.icaali.tasbeeh.view.dialog.ThemesDialog
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_tasbeeh.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColor
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class TasbeehActivity : BaseActivity() {

    private val counterPreference by inject<CounterPreference>()
    private val interstitialPreference by inject<InterstitialPreference>()
    private val themesPreference by inject<ThemesPreference>()
    private val settingPreference by inject<SettingPreference>()
    private val disposable = CompositeDisposable()
    private var type = TextUtils.BLANK
    private var theme: Theme? = null
    private val tvCounters by lazy {
        listOf(tvCounter1, tvCounter2, tvCounter3, tvCounter4, tvCounter4, tvCounter5)
    }

    private val confirmationDialog by lazy { ConfirmationDialog(this) }
    private val themesPickDialog by lazy { ThemesDialog(this) }
    private val moreDialog by lazy { MoreTasbeehDialog(this, settingPreference) }
    private val vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    companion object {
        const val THROTTLE_FIRST = 100L
        const val TYPE_EXTRA = "TYPE_EXTRA"
        const val TASBEEH_LATIN_EXTRA = "TASBEEH_LATIN_EXTRA"

        const val VIBRATE_TARGET_DURATION = 2000L
        const val VIBRATE_CLICK_DURATION = 500L

        const val MAX_VOLUME = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent?.getStringExtra(TYPE_EXTRA) ?: TextUtils.BLANK
        setContentView(R.layout.activity_tasbeeh)
        selectedTheme()
        setupDialog()
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
            tvTargetCounter?.text = target.toString()
            setTextCounter(value)
        }
    }

    private fun setupDialog() {
        llThemes?.setOnClickListener {
            themesPickDialog.apply {
                setItemThemes(ThemeFactory.themes, theme?.type ?: ThemeType.DEFAULT)
                setOnPositiveListener { themeSelected ->
                    themesPreference.type = themeSelected.type
                    selectedTheme()
                }
                setOnDismissListener { loadAdMobInterstitial() }
            }.show()
        }

        llMore?.setOnClickListener {
            moreDialog.apply {
                showButtonDelete(false)
                setDeleteClickListener {

                }
                setOnDismissListener {
                    loadAdMobInterstitial()
                }
            }.show()
        }
    }

    private fun selectedTheme() {
        theme = ThemeFactory.generate(themesPreference.type)
        theme?.run {
            ivDzikir?.setImageDrawable(getDzikirImage())

            clRootLayout?.backgroundDrawable = getDrawableCompat(backgroundScreenImageRes)
            ivSkin?.setImageDrawable(getDrawableCompat(backgroundDigitalImageRes))
            fabCount?.setImageDrawable(getDrawableCompat(counterImageRes))
            fabReset?.setImageDrawable(getDrawableCompat(resetImageRes))
            ivCounterSkinBox?.setImageDrawable(getDrawableCompat(outputImageRes))
            tvHintCounter?.textColor = getColorCompat(outputHintColorRes)

            ivBack?.setImageDrawable(getDrawableCompat(R.drawable.ic_arrow_back, tintColorAccent))
            ivMore?.setImageDrawable(getDrawableCompat(R.drawable.ic_more_new, tintColorAccent))
            ivThemes?.setImageDrawable(getDrawableCompat(R.drawable.ic_theme, tintColorAccent))
            tvDzikir?.textColor = getColorCompat(tintColorAccent)

            ivTargetCounter?.setImageDrawable(getDrawableCompat(backgroundTargetCounterImageRes))
            tvTargetCounter?.textColor = getColorCompat(outputHintColorRes)
        }
    }

    private fun getDzikirImage(): Drawable? {
        return when (type) {
            Tasbeeh.SUBHANALLAH -> getDrawableCompat(
                R.drawable.ic_subhanallah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            Tasbeeh.ALHAMDULILLAH -> getDrawableCompat(
                R.drawable.ic_alhamdulillah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            Tasbeeh.ALLAHU_AKBAR -> getDrawableCompat(
                R.drawable.ic_allahu_akbar,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            Tasbeeh.ASTAGHFIRULLAH -> getDrawableCompat(
                R.drawable.ic_astagfirllah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            Tasbeeh.LAILAHAILALLAH -> getDrawableCompat(
                R.drawable.ic_laailaahaillallah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            else -> getDrawableCompat(
                R.drawable.ic_subhanallah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
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

            vibrate(
                try {
                    if (count % counterPreference.target == 0)
                        VIBRATE_TARGET_DURATION
                    else
                        VIBRATE_CLICK_DURATION
                } catch (e: ArithmeticException) {
                    VIBRATE_CLICK_DURATION
                }
            )
            clickSound(count)
            setTextCounter(count)
        }
    }

    private fun setTextCounter(counter: Int) {
        var textCounter = counter.toString()
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

        tvCounters.forEachIndexed { index, textView ->
            when {
                index < textCounter.length -> {
                    textView.text = textCounter.reversed()[index].toString()
                    textView.visible()
                }
                else ->
                    textView.gone()
            }
        }
    }

    private fun vibrate(duration: Long) {
        if (settingPreference.vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                );
            } else {
                vibrator.vibrate(duration);
            }
        }
    }

    private fun clickSound(count: Int) {
        if (settingPreference.sound) {
            val mp = MediaPlayer.create(
                this@TasbeehActivity,
                try {
                    if (count % counterPreference.target == 0)
                        R.raw.target
                    else
                        R.raw.sound_click
                } catch (e: ArithmeticException) {
                    R.raw.sound_click
                }
            )
            mp.start()
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