package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.view.goneIf
import com.icaali.tasbeeh.preference.SettingPreference
import kotlinx.android.synthetic.main.dialog_bottom_more_tasbeeh.*

class MoreTasbeehDialog(context: Context, private val settingPreference: SettingPreference) :
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
            }
        }

        setTextSwitch(tvLabelSwitchVibration, settingPreference.vibrate)

        swSound.run {
            isChecked = settingPreference.sound
            setOnCheckedChangeListener { _, isChecked ->
                settingPreference.sound = isChecked
                setTextSwitch(tvLabelSwitchSound, isChecked)
            }
        }

        setTextSwitch(tvLabelSwitchSound, settingPreference.sound)

        llDeleteTasbeeh.setOnClickListener {
            onClickDeleteTasbeeh?.invoke()
        }
        ivClose?.setOnClickListener { dismiss() }
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