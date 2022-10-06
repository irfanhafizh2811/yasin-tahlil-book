package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.common.TextUtils
import kotlinx.android.synthetic.main.dialog_bottom_confirmation.*

class ConfirmationDialog(context: Context) : BottomSheetDialog(context) {

    private var onPositiveListener: (() -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_confirmation, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnNegative?.setOnClickListener { dismiss() }
        btnPositive?.setOnClickListener {
            onPositiveListener?.invoke()
            dismiss()
        }
        ivClose?.setOnClickListener { dismiss() }
    }

    fun setText(
        messageText: String = TextUtils.BLANK,
        positiveText: String = TextUtils.BLANK,
        negativeText: String = TextUtils.BLANK
    ): ConfirmationDialog {
        if (messageText.isNotBlank()) tvMessage?.text = messageText
        if (positiveText.isNotBlank()) btnPositive?.text = positiveText
        if (negativeText.isNotBlank()) btnNegative?.text = negativeText
        return this
    }

    fun setOnPositiveListener(onPositiveListener: () -> Unit): ConfirmationDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

}