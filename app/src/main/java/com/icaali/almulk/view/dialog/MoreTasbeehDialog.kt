package com.icaali.almulk.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.almulk.R
import com.icaali.almulk.databinding.DialogBottomMoreTasbeehBinding
import com.icaali.almulk.extension.view.goneIf
import com.icaali.almulk.preference.SettingPreference

class MoreTasbeehDialog(
    context: Context,
    private val settingPreference: SettingPreference,
) :
    BottomSheetDialog(context) {

    private lateinit var binding: DialogBottomMoreTasbeehBinding
    private var onClickDeleteTasbeeh: (() -> Unit)? = null

    init {
        binding = DialogBottomMoreTasbeehBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {

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

    fun showButtonDelete(show: Boolean) = with(binding) {
        llDeleteTasbeeh.goneIf(!show)
    }

}