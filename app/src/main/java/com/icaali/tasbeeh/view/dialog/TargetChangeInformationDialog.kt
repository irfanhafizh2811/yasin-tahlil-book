package com.icaali.tasbeeh.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.preference.SettingPreference
import kotlinx.android.synthetic.main.dialog_bottom_more_tasbeeh.clContainer
import kotlinx.android.synthetic.main.dialog_target_change_information.*

class TargetChangeInformationDialog(
    context: Context,
    private val settingPreference: SettingPreference
) : Dialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_target_change_information, clContainer, false
        )
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnOk?.setOnClickListener {
            val showAgain = cbDontShowAgain?.isChecked ?: false
            settingPreference.showPopupAgain = !showAgain
            dismiss()
        }
    }
}