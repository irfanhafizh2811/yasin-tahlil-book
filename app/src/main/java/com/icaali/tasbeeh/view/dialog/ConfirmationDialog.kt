package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.databinding.DialogBottomConfirmationBinding

class ConfirmationDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var binding: DialogBottomConfirmationBinding
    private var onPositiveListener: (() -> Unit)? = null

    init {
        binding = DialogBottomConfirmationBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            btnNegative.setOnClickListener { dismiss() }
            btnPositive.setOnClickListener {
                onPositiveListener?.invoke()
                dismiss()
            }
            ivClose.setOnClickListener { dismiss() }
        }
    }

    fun setText(
        messageText: String = "",
        positiveText: String = "",
        negativeText: String = ""
    ): ConfirmationDialog {
        with(binding) {
            if (messageText.isNotBlank()) tvMessage.text = messageText
            if (positiveText.isNotBlank()) btnPositive.text = positiveText
            if (negativeText.isNotBlank()) btnNegative.text = negativeText
        }
        return this
    }

    fun setOnPositiveListener(onPositiveListener: () -> Unit): ConfirmationDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

}