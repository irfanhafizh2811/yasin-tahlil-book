package com.icaali.tasbeeh.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
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

    fun setOnPositiveListener(onPositiveListener: () -> Unit): ConfirmationDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

}