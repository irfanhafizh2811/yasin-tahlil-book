package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.primitive.switchOnOff
import com.icaali.tasbeeh.extension.view.goneIf
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.utils.Analytic
import kotlinx.android.synthetic.main.dialog_bottom_more_tasbeeh.*

class MoreTasbeehDialog(
    context: Context,
    private val settingPreference: SettingPreference,
    private val analytic: FirebaseAnalytics?
) :
    BottomSheetDialog(context) {

    private var onClickDeleteTasbeeh: (() -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_more_tasbeeh, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swVibration.run {
            isChecked = settingPreference.vibrate
            setOnCheckedChangeListener { _, isChecked ->
                settingPreference.vibrate = isChecked
                setTextSwitch(tvLabelSwitchVibration, isChecked)
                log(Analytic.CLICK_VIBRATION, isChecked)
            }
        }
        setTextSwitch(tvLabelSwitchVibration, settingPreference.vibrate)
        swSound.run {
            isChecked = settingPreference.sound
            setOnCheckedChangeListener { _, isChecked ->
                settingPreference.sound = isChecked
                setTextSwitch(tvLabelSwitchSound, isChecked)
                log(Analytic.CLICK_SOUND, isChecked)
            }
        }

        setTextSwitch(tvLabelSwitchSound, settingPreference.sound)
        llDeleteTasbeeh.setOnClickListener {
            onClickDeleteTasbeeh?.invoke()
            log()
        }
        ivClose?.setOnClickListener { dismiss() }
    }

    private fun log(valueParam: String, isChecked: Boolean) {
        val event = FirebaseAnalytics.Event.VIEW_PROMOTION
        val keyParam = FirebaseAnalytics.Param.CREATIVE_NAME
        analytic?.logEvent(event, Bundle().apply {
            putString(keyParam, valueParam.plus(isChecked.switchOnOff()))
        })
    }

    private fun log() {
        val event = FirebaseAnalytics.Event.VIEW_PROMOTION
        val keyParam = FirebaseAnalytics.Param.CREATIVE_NAME
        analytic?.logEvent(event, Bundle().apply {
            putString(keyParam, Analytic.CLICK_DELETE)
        })
    }

    private fun setTextSwitch(view: TextView, isChecked: Boolean) {
        view.text = if (isChecked) {
            context.getString(R.string.label_on)
        } else {
            context.getString(R.string.label_off)
        }
    }

    fun setDeleteClickListener(onClickDeleteTasbeeh: () -> Unit) {
        this.onClickDeleteTasbeeh = onClickDeleteTasbeeh
    }

    fun showButtonDelete(show: Boolean) {
        llDeleteTasbeeh.goneIf(!show)
    }

}