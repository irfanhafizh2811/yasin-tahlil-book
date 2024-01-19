package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.utils.TextUtils
import kotlinx.android.synthetic.main.dialog_bottom_confirmation.*

class SurveyDialog(context: Context) : BottomSheetDialog(context) {

    private var onPositiveListener: (() -> Unit)? = null
    private var onNegativeListener: (() -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_survey, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnNegative?.setOnClickListener {
            onNegativeListener?.invoke()
            dismiss()
        }
        btnPositive?.setOnClickListener {
            onPositiveListener?.invoke()
            dismiss()
        }
        ivClose?.setOnClickListener { dismiss() }
    }

    fun setText(
        titleText: String = "",
        messageText: String = "",
        positiveText: String = "",
        negativeText: String = "",
        @DrawableRes positiveIcon: Int? = null
    ): SurveyDialog {
        if (messageText.isNotBlank()) tvTitle?.text = titleText
        if (messageText.isNotBlank()) tvMessage?.text = messageText
        if (positiveText.isNotBlank()) btnPositive?.text = positiveText
        if (negativeText.isNotBlank()) btnNegative?.text = negativeText
        positiveIcon?.let {
            btnPositive.setCompoundDrawablesRelativeWithIntrinsicBounds(
                context.getDrawableCompat(it), null, null, null
            )
        }
        return this
    }

    fun setOnPositiveListener(onPositiveListener: () -> Unit): SurveyDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

    fun setOnNegativeListener(onNegativeListener: () -> Unit): SurveyDialog {
        this.onNegativeListener = onNegativeListener
        return this
    }
}