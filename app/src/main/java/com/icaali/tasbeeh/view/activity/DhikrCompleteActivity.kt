package com.icaali.tasbeeh.view.activity

import android.os.Bundle
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import kotlinx.android.synthetic.main.activity_dhikr_complete.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import java.util.Random

class DhikrCompleteActivity : BaseActivity() {

    companion object {
        const val DHIKR_INTENT_EXTRA = "DHIKR_INTENT_EXTRA"
        val PRAYERS = arrayListOf(
            "Semoga kita selalu dalam lindungan Allah, diampuni dosa-dosa kita dan dilancarkan segala urusan",
            "Semoga hari ini, kita mendapatkan rezeki yang berlimpah, serta mendapatkan keberkahanNya",
            "Jauhkanlah kami dari gangguan setan serta diberikan Ketenangan dan Kedamaian pada jiwa kami"
        )
    }

    private var isEvening = false
    private val randomIndex = Random().nextInt((PRAYERS.size - 1) - 0 + 1) + 0
    private val pray = PRAYERS[randomIndex]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEvening = intent.getBooleanExtra(DHIKR_INTENT_EXTRA, false)
        setContentView(R.layout.activity_dhikr_complete)
        onUIView()
        loadBanner(adViewContainer)
        trackCompleteAnalytic()
    }

    private fun onUIView() {
        window.setBackgroundDrawable(
            if (!isEvening) getDrawableCompat(R.drawable.bg_activtiy_pray_morning)
            else getDrawableCompat(R.drawable.bg_activtiy_pray_evening)
        )
        onUIPrayer()
        ivBack?.setOnClickListener { finish() }
        tvTitle?.text = if (!isEvening) getString(R.string.label_prayer_morning)
        else getString(R.string.label_prayer_evening)
        btnAmin?.setOnClickListener {
            loadAdMobInterstitial()
            finishAffinity()
            startActivity<MainActivity>()
        }
        ivPrayer?.setImageDrawable(
            if (!isEvening) getDrawableCompat(R.drawable.ic_footer_pray_morning)
            else getDrawableCompat(R.drawable.ic_footer_pray_evening)
        )
        tvPrayer?.text = pray
    }

    private fun onUIPrayer() = with(tvPrayer) {
        text = pray
        textColor = if (!isEvening) getColorCompat(R.color.colorAccentMorningDhikr)
        else getColorCompat(R.color.colorAccentEveningDhikr)
    }

    private fun trackCompleteAnalytic() {
        if (isEvening) logSelectContent(R.string.analytic_evening_pray)
        if (!isEvening) logSelectContent(R.string.analytic_morning_pray)
    }
}