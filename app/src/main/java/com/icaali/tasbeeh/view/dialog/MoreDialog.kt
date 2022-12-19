package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.preference.SettingPreference
import kotlinx.android.synthetic.main.dialog_bottom_more.*

class MoreDialog(context: Context, private val settingPreference: SettingPreference) :
    BottomSheetDialog(context) {

    enum class Menu {
        LANGUAGE,
        RATING_AND_REVIEW,
        SHARE,
        INSTAGRAM
    }

    private var onSelectedListener: ((Menu) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_more, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 33) {
            llLanguage?.gone()
        }
        llPlaystoreRate?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.RATING_AND_REVIEW)
        }
        llInstagram?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.INSTAGRAM)
        }
        llLanguage?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.LANGUAGE)
        }
        llShare?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.SHARE)
        }

        ivClose?.setOnClickListener { dismiss() }

        swNotification?.run {
            isChecked = settingPreference.notification
            setOnCheckedChangeListener { _, isChecked ->
                settingPreference.notification = isChecked
                setTextSwitch(tvLabelSwitchNotification, isChecked)
            }
        }

        setTextSwitch(tvLabelSwitchNotification, settingPreference.notification)
    }

    fun setOnSelectedListener(onSelectedListener: (Menu) -> Unit): MoreDialog {
        this.onSelectedListener = onSelectedListener
        return this
    }

    private fun setTextSwitch(view: TextView, isChecked: Boolean) {
        view.text = if (isChecked) context.getString(R.string.label_on)
        else context.getString(R.string.label_off)
    }
}