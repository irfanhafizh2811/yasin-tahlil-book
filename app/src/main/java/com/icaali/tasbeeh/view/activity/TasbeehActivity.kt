package com.icaali.tasbeeh.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.utils.TextUtils
import com.icaali.tasbeeh.extension.activty.isCustomType
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.preference.ThemesPreference
import com.icaali.tasbeeh.view.dialog.*
import com.icaali.tasbeeh.view.theme.Theme
import com.icaali.tasbeeh.view.theme.ThemeFactory
import com.icaali.tasbeeh.view.theme.ThemeType
import com.icaali.tasbeeh.vm.DhikrViewModel
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_tasbeeh.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import com.google.android.gms.ads.MobileAds
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.extension.activty.hasPermissions
import com.icaali.tasbeeh.utils.Analytic
import com.icaali.tasbeeh.utils.TasbeehConst

class TasbeehActivity : BaseActivity() {

    companion object {
        const val MAX_VOLUME = 15F
        const val THROTTLE_FIRST = 100L
        const val TYPE_EXTRA = "TYPE_EXTRA"
        const val TASBEEH_LATIN_EXTRA = "TASBEEH_LATIN_EXTRA"
        const val TASBEEH_DHIKR_EXTRA = "TASBEEH_DHIKR_EXTRA"
        const val VIBRATE_TARGET_DURATION = 2000L
        const val VIBRATE_CLICK_DURATION = 500L
    }

    //----------------------------------   Dependency Inject   ----------------------------------
    private val counterPreference by inject<CounterPreference>()
    private val themesPreference by inject<ThemesPreference>()
    private val settingPreference by inject<SettingPreference>()
    private val dhikrViewModel: DhikrViewModel by viewModel()
    //---------------------------------- End Dependency Inject ----------------------------------

    //------------------------------------   Section Lazy   ------------------------------------
    private val tvCounters by lazy {
        listOf(tvCounter1, tvCounter2, tvCounter3, tvCounter4, tvCounter5)
    }
    private val vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    private val confirmationDialog by lazy { ConfirmationDialog(this) }
    private val themesPickDialog by lazy { ThemesDialog(this) }
    private val moreDialog by lazy { MoreTasbeehDialog(this, settingPreference, firebaseAnalytics) }
    private val targetChangeInformationDialog by lazy {
        TargetChangeInformationDialog(
            this,
            settingPreference
        )
    }
    private val targetDhikrDialog by lazy {
        TargetDhikrDialog(this) {
            counterPreference.target = it
            tvTargetCounter?.text = it.toString()
            logClick(Analytic.TARGET_DHIKR.plus(it.toString()))
            if (settingPreference.showPopupAgain)
                targetChangeInformationDialog.show()
        }
    }
    private val guideTasbeehDialog by lazy { GuideTasbeehDialog(this, guidePref) }
    //------------------------------------ Section Lazy ------------------------------------

    private var dhikr = Tasbeeh(TextUtils.BLANK, TextUtils.BLANK, TextUtils.BLANK, 0)
    private var theme: Theme? = null
    internal var type = TextUtils.BLANK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent?.getStringExtra(TYPE_EXTRA) ?: TextUtils.BLANK
        setContentView(R.layout.activity_tasbeeh)
        setViewTypeCustom()
        selectedTheme()
        setupDialog()
        ivBack?.setOnClickListener { finish() }

        fabReset?.setOnClickListener {
            ViewAnimator.animate(fabReset)
                .pulse()
                .start()
            confirmationDialog.apply {
                setOnPositiveListener { reset() }
            }.show()
        }
        tvDzikir?.text = intent?.getStringExtra(TASBEEH_LATIN_EXTRA)
        initTasbeeh()
        mDisposable.add(
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
                    logCount(intent?.getStringExtra(TASBEEH_LATIN_EXTRA) ?: TextUtils.BLANK)
                }
        )
        mDisposable.addAll(observeGuide(DHIKR_SECOND_DELAY, guidePref) {
            if (!guideTasbeehDialog.isShowingAll()) guideTasbeehDialog.apply {
                onTapTargetListener = {
                    when {
                        guidePref.hasShownPickTheme && !guidePref.hasShownVibrateSound ->
                            showThemeDialog(true)
                        guidePref.hasShownVibrateSound && !guidePref.hasShownDhikrTarget ->
                            showMoreDialog(true)
                        else -> showTargetDialog()
                    }
                }
            }.show()
        })
        loadBanner(adViewContainer)
        MobileAds.openAdInspector(this) {
            // Error will be non-null if ad inspector closed due to an error.
        }
    }

    private fun setViewTypeCustom() {
        if (isCustomType())
            intent?.getParcelableExtra<Tasbeeh>(TASBEEH_DHIKR_EXTRA)?.let { dhikr = it }
    }

    private fun initTasbeeh() {
        with(counterPreference) {
            val value = when (type) {
                TasbeehConst.CUSTOM -> dhikr.count
                TasbeehConst.SUBHANALLAH -> subhanallah
                TasbeehConst.ALHAMDULILLAH -> alhamdulillah
                TasbeehConst.LAILAHAILALLAH -> lailahailallah
                TasbeehConst.ALLAHU_AKBAR -> allahukkbar
                TasbeehConst.ASTAGHFIRULLAH -> astaghfirullah
                else -> 0
            }
            tvTargetCounter?.text = target.toString()
            setTextCounter(value)
        }
    }

    private fun setupDialog() {
        clTargetCounter?.setOnClickListener { showTargetDialog() }
        llThemes?.setOnClickListener { showThemeDialog() }
        llMore?.setOnClickListener { showMoreDialog() }
    }

    private fun showTargetDialog() {
        targetDhikrDialog.show(counterPreference.target)
    }

    private fun showThemeDialog(isGuide: Boolean = false) {
        themesPickDialog.apply {
            setItemThemes(ThemeFactory.themes, theme?.type ?: ThemeType.DEFAULT)
            setOnPositiveListener { themeSelected ->
                logClick(themeSelected.type.name)
                themesPreference.type = themeSelected.type
                selectedTheme()
            }
            setOnDismissListener {
                if (isGuide) guideTasbeehDialog.show()
                else loadAdMobInterstitial()
            }
        }.show()
    }

    private fun showMoreDialog(isGuide: Boolean = false) {
        moreDialog.apply {
            showButtonDelete(isCustomType())
            setDeleteClickListener {
                dismiss()
                confirmationDialog.apply {
                    setTitle(R.string.label_delete)
                    setText(getString(R.string.label_message_delete_confirm))
                    setOnPositiveListener {
                        dhikrViewModel.delete(dhikr)
                        finish()
                    }
                }.show()
            }
            setOnDismissListener {
                if (isGuide) guideTasbeehDialog.show()
            }
        }.show()
    }

    private fun selectedTheme() {
        theme = ThemeFactory.generate(themesPreference.type)
        theme?.run {
            if (!isCustomType()) ivDzikir?.setImageDrawable(getDzikirImage())

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
            TasbeehConst.SUBHANALLAH -> getDrawableCompat(
                R.drawable.ic_subhanallah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            TasbeehConst.ALHAMDULILLAH -> getDrawableCompat(
                R.drawable.ic_alhamdulillah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            TasbeehConst.ALLAHU_AKBAR -> getDrawableCompat(
                R.drawable.ic_allahu_akbar,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            TasbeehConst.ASTAGHFIRULLAH -> getDrawableCompat(
                R.drawable.ic_astagfirllah,
                theme?.tintColorAccent ?: R.color.textHintOutputDefault
            )
            TasbeehConst.LAILAHAILALLAH -> getDrawableCompat(
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
            TasbeehConst.SUBHANALLAH -> {
                counterPreference.subhanallah = 0
            }
            TasbeehConst.ALHAMDULILLAH -> {
                counterPreference.alhamdulillah = 0
            }
            TasbeehConst.LAILAHAILALLAH -> {
                counterPreference.lailahailallah = 0
            }
            TasbeehConst.ALLAHU_AKBAR -> {
                counterPreference.allahukkbar = 0
            }
            TasbeehConst.ASTAGHFIRULLAH -> {
                counterPreference.astaghfirullah = 0
            }
        }
        setTextCounter(0)
        logClick(Analytic.CLICK_RESET_DHIKR)
    }

    private fun count() {
        with(counterPreference) {
            val count = when (type) {
                TasbeehConst.CUSTOM -> {
                    dhikr.count += 1
                    dhikr.count
                }
                TasbeehConst.SUBHANALLAH -> {
                    subhanallah += 1
                    subhanallah
                }
                TasbeehConst.ALHAMDULILLAH -> {
                    alhamdulillah += 1
                    alhamdulillah
                }
                TasbeehConst.LAILAHAILALLAH -> {
                    lailahailallah += 1
                    lailahailallah
                }
                TasbeehConst.ALLAHU_AKBAR -> {
                    allahukkbar += 1
                    allahukkbar
                }
                TasbeehConst.ASTAGHFIRULLAH -> {
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
        if (isCustomType()) {
            dhikr.count = counter
            dhikrViewModel.update(dhikr)
        }
    }

    @SuppressLint("MissingPermission")
    private fun vibrate(duration: Long) {
        if (settingPreference.vibrate) {
            if (hasPermissions(arrayOf(Manifest.permission.VIBRATE))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            duration,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(duration);
                }
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
            ).apply {
                setVolume(MAX_VOLUME, MAX_VOLUME)
                setOnCompletionListener {
                    it.release()
                }
            }
            mp.start()
        }
    }
}