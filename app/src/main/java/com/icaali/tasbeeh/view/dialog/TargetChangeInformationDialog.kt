package com.icaali.tasbeeh.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.icaali.tasbeeh.databinding.DialogTargetChangeInformationBinding
import com.icaali.tasbeeh.preference.SettingPreference

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