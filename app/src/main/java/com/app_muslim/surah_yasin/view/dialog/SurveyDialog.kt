package com.app_muslim.surah_yasin.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app_muslim.surah_yasin.databinding.DialogBottomSurveyBinding
import com.app_muslim.surah_yasin.extension.context.getDrawableCompat

class SurveyDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var binding: DialogBottomSurveyBinding
    private var onPositiveListener: (() -> Unit)? = null
    private var onNegativeListener: (() -> Unit)? = null

    init {
        binding = DialogBottomSurveyBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            btnNegative.setOnClickListener {
                onNegativeListener?.invoke()
                dismiss()
            }
            btnPositive.setOnClickListener {
                onPositiveListener?.invoke()
                dismiss()
            }
            ivClose.setOnClickListener { dismiss() }
        }
    }

    fun setText(
        titleText: String = "",
        messageText: String = "",
        positiveText: String = "",
        negativeText: String = "",
        @DrawableRes positiveIcon: Int? = null
    ): SurveyDialog {
        with(binding) {
            if (messageText.isNotBlank()) tvTitle.text = titleText
            if (messageText.isNotBlank()) tvMessage.text = messageText
            if (positiveText.isNotBlank()) btnPositive.text = positiveText
            if (negativeText.isNotBlank()) btnNegative.text = negativeText
            positiveIcon?.let {
                btnPositive.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    context.getDrawableCompat(it), null, null, null
                )
            }
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