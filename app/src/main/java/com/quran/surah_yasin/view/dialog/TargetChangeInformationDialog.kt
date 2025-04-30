package com.quran.surah_yasin.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.quran.surah_yasin.databinding.DialogTargetChangeInformationBinding
import com.quran.surah_yasin.data.preference.SettingPreference

class TargetChangeInformationDialog(
    context: Context,
    private val settingPreference: SettingPreference
) : Dialog(context) {

    private lateinit var binding: DialogTargetChangeInformationBinding

    init {
        binding = DialogTargetChangeInformationBinding.inflate(LayoutInflater.from(context))
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding){
            btnOk.setOnClickListener {
                val showAgain = cbDontShowAgain.isChecked
                settingPreference.showPopupAgain = !showAgain
                dismiss()
            }
        }
    }
}