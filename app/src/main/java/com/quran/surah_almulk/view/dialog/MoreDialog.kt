package com.quran.surah_almulk.view.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quran.surah_almulk.R
import com.quran.surah_almulk.databinding.DialogBottomMoreBinding
import com.quran.surah_almulk.extension.view.gone
import com.quran.surah_almulk.data.preference.SettingPreference

class MoreDialog(context: Context, private val settingPreference: SettingPreference) :
    BottomSheetDialog(context) {

    enum class Menu {
        LANGUAGE,
        RATING_AND_REVIEW,
        SHARE,
        INSTAGRAM
    }

    private lateinit var binding: DialogBottomMoreBinding
    private var onSelectedListener: ((Menu) -> Unit)? = null

    init {
        binding = DialogBottomMoreBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding){

            if (Build.VERSION.SDK_INT < 33) {
                llLanguage?.gone()
            }
            llPlaystoreRate?.setOnClickListener {
                dismiss()
                onSelectedListener?.invoke(Menu.RATING_AND_REVIEW)
            }
            llInstagram.setOnClickListener {
                dismiss()
                onSelectedListener?.invoke(Menu.INSTAGRAM)
            }
            llLanguage.setOnClickListener {
                dismiss()
                onSelectedListener?.invoke(Menu.LANGUAGE)
            }
            llShare.setOnClickListener {
                dismiss()
                onSelectedListener?.invoke(Menu.SHARE)
            }
            ivClose.setOnClickListener { dismiss() }
            swNotification.run {
                isChecked = settingPreference.notification
                setOnCheckedChangeListener { _, isChecked ->
                    settingPreference.notification = isChecked
                    setTextSwitch(tvLabelSwitchNotification, isChecked)
                }
            }
            setTextSwitch(tvLabelSwitchNotification, settingPreference.notification)
        }
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