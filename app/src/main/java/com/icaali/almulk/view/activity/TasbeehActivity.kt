package com.icaali.almulk.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.view.isVisible
import com.asliri.viewanimator.ViewAnimator
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewManagerFactory
import com.icaali.almulk.R
import com.icaali.almulk.data.database.entity.TasbeehEntity
import com.icaali.almulk.databinding.ActivityTasbeehBinding
import com.icaali.almulk.extension.activty.hasPermissions
import com.icaali.almulk.extension.activty.isCustomType
import com.icaali.almulk.extension.context.getColorCompat
import com.icaali.almulk.extension.context.getDrawableCompat
import com.icaali.almulk.extension.view.gone
import com.icaali.almulk.extension.view.visible
import com.icaali.almulk.data.preference.CounterPreference
import com.icaali.almulk.data.preference.SettingPreference
import com.icaali.almulk.data.preference.ThemesPreference
import com.icaali.almulk.utils.TasbeehConst
import com.icaali.almulk.view.dialog.*
import com.icaali.almulk.view.theme.*
import com.icaali.almulk.vm.DhikrViewModel
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TasbeehActivity : BaseActivity() {

    private lateinit var binding: ActivityTasbeehBinding

    //----------------------------------   Dependency Inject   ----------------------------------
    private val counterPreference by inject<CounterPreference>()
    private val settingPreference by inject<SettingPreference>()
    private val dhikrViewModel: DhikrViewModel by viewModel()
    //---------------------------------- End Dependency Inject ----------------------------------

    //------------------------------------   Section Lazy   ------------------------------------
    private val reviewManager by lazy {
        ReviewManagerFactory.create(this)
    }
    private val tvCounters by lazy {
        listOf(
            binding.tvCounter1,
            binding.tvCounter2,
            binding.tvCounter3,
            binding.tvCounter4,
            binding.tvCounter5
        )
    }
    private val vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    private val confirmationDialog by lazy { ConfirmationDialog(this) }
    private val themesPickDialog by lazy { ThemesDialog(this) }
    private val moreDialog by lazy { MoreTasbeehDialog(this, settingPreference) }
    private val targetChangeInformationDialog by lazy {
        TargetChangeInformationDialog(
            this,
            settingPreference
        )
    }
    private val targetDhikrDialog by lazy {
        TargetDhikrDialog(this) {
            counterPreference.target = it
            binding.tvTargetCounter.text = it.toString()
            if (settingPreference.showPopupAgain)
                targetChangeInformationDialog.show()
        }
    }
    private val guideTasbeehDialog by lazy { GuideTasbeehDialog(this, guidePref) }
    //------------------------------------ Section Lazy ------------------------------------

    private var dhikr = TasbeehEntity("", "", "", 0)
    private var theme: Theme? = null

    internal var type = ""
    internal val themesPreference by inject<ThemesPreference>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent?.getStringExtra(TYPE_EXTRA) ?: ""
        binding = ActivityTasbeehBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewTypeCustom()
        selectedTheme()
        setupDialog()
        with(binding) {
            ivBack.setOnClickListener { finish() }
            fabReset.setOnClickListener {
                ViewAnimator.animate(fabReset)
                    .pulse()
                    .start()
                confirmationDialog.apply {
                    setOnPositiveListener { reset() }
                }.show()
            }
            tvDzikir.text = intent?.getStringExtra(TASBEEH_LATIN_EXTRA)
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
        }
        MobileAds.openAdInspector(this) {
            // Error will be non-null if ad inspector closed due to an error.
        }
    }

    private fun setViewTypeCustom() {
        if (isCustomType()) {
            dhikrViewModel.dhikrs.observe(this) { listTasbeeh ->
                if (listTasbeeh.isEmpty()) return@observe
                val id = intent.getStringExtra(TASBEEH_DHIKR_EXTRA_ID)
                listTasbeeh.find { it.id == id }?.let { tasbeeh ->
                    dhikr = tasbeeh
                }
            }
        }
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
            binding.tvTargetCounter.text = target.toString()
            setTextCounter(value)
        }
    }

    private fun setupDialog() {
        binding.clTargetCounter.setOnClickListener { showTargetDialog() }
        binding.llThemes.setOnClickListener { showThemeDialog() }
        binding.llMore.setOnClickListener { showMoreDialog() }
    }

    private fun showTargetDialog() {
        targetDhikrDialog.show(counterPreference.target)
    }

    private fun showThemeDialog(isGuide: Boolean = false) {
        val themes = ThemeFactory.themes.apply {
            setVisibleBadgeNewThemes(themesPreference)
        }
        themesPickDialog.apply {
            setItemThemes(themes, theme?.themeType() ?: ThemeType.DEFAULT)
            setOnPositiveListener { themeSelected ->
                themesPreference.type = themeSelected.themeType()
                selectedTheme()
            }
            setOnDismissListener {
                binding.cvNewTheme.isVisible = themesPreference.anyNewContent()
                if (isGuide) guideTasbeehDialog.show()
                else loadAdMobInterstitial()
            }
        }.show()
    }

    override fun onResume() {
        binding.cvNewTheme.isVisible = themesPreference.anyNewContent()
        super.onResume()
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

    private fun selectedTheme() = with(binding) {
        theme = ThemeFactory.generate(themesPreference.type)
        theme?.run {
            if (themesPreference.anyNewContent()) updateNewThemePref(this, themesPreference)
            if (!isCustomType()) ivDzikir.setImageDrawable(getDzikirImage())
            clRootLayout.setBackgroundResource(backgroundScreenImageRes())
            ivSkin.setImageDrawable(getDrawableCompat(backgroundDigitalImageRes()))
            fabCount.setImageDrawable(getDrawableCompat(counterImageRes()))
            fabReset.setImageDrawable(getDrawableCompat(resetImageRes()))
            ivCounterSkinBox.setImageDrawable(getDrawableCompat(outputImageRes()))
            tvHintCounter.setTextColor(getColorCompat(outputHintColorRes()))

            ivBack.setImageDrawable(getDrawableCompat(R.drawable.ic_arrow_back, tintColorAccent()))
            ivMore.setImageDrawable(getDrawableCompat(R.drawable.ic_more_new, tintColorAccent()))
            ivThemes.setImageDrawable(getDrawableCompat(R.drawable.ic_theme, tintColorAccent()))
            tvDzikir.setTextColor(getColorCompat(tintColorAccent()))

            ivTargetCounter.setImageDrawable(getDrawableCompat(backgroundTargetCounterImageRes()))
            tvTargetCounter.setTextColor(getColorCompat(outputHintColorRes()))
        }
    }

    private fun getDzikirImage(): Drawable? {
        return when (type) {
            TasbeehConst.SUBHANALLAH -> getDrawableCompat(
                R.drawable.ic_subhanallah,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
            )

            TasbeehConst.ALHAMDULILLAH -> getDrawableCompat(
                R.drawable.ic_alhamdulillah,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
            )

            TasbeehConst.ALLAHU_AKBAR -> getDrawableCompat(
                R.drawable.ic_allahu_akbar,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
            )

            TasbeehConst.ASTAGHFIRULLAH -> getDrawableCompat(
                R.drawable.ic_astagfirllah,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
            )

            TasbeehConst.LAILAHAILALLAH -> getDrawableCompat(
                R.drawable.ic_laailaahaillallah,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
            )

            else -> getDrawableCompat(
                R.drawable.ic_subhanallah,
                theme?.tintColorAccent() ?: R.color.textHintOutputDefault
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
        requestRatingReviewPlaystore()
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

    private fun setTextCounter(counter: Int) = with(binding) {
        val textCounter = counter.toString()
        tvHintCounter.text = when (textCounter.length == 1 &&
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

    private fun hasInputSurveyTheme(): Boolean = listOf(
        ThemeType.KAABA,
        ThemeType.EID_AL_FITR
    ).any { it == themesPreference.type } && themesPreference.hasThemeSurvey

    override fun finish() {
        if (hasInputSurveyTheme()) createThemeSurvey().apply {
            setOnDismissListener { super.finish() }
        }.show()
        else super.finish()
    }

    private fun requestRatingReviewPlaystore() {
        if (settingPreference.noHasSubmitRating) {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        settingPreference.noHasSubmitRating = false
                    }
                } else {
                    task.exception?.let { it.printStackTrace() }
                }
            }
        }
    }

    companion object {
        const val MAX_VOLUME = 15F
        const val THROTTLE_FIRST = 100L
        const val TYPE_EXTRA = "TYPE_EXTRA"
        const val TASBEEH_LATIN_EXTRA = "TASBEEH_LATIN_EXTRA"
        const val TASBEEH_DHIKR_EXTRA_ID = "TASBEEH_DHIKR_EXTRA_ID"
        const val VIBRATE_TARGET_DURATION = 2000L
        const val VIBRATE_CLICK_DURATION = 500L
    }
}