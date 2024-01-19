package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.utils.TextUtils
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.extension.text.charCountingListener
import kotlinx.android.synthetic.main.dialog_bottom_add_custom_dhikr.*

class AddCustomDialog(context: Context) : BottomSheetDialog(context) {

    var dhikr: Tasbeeh? = null
    private var onPositiveListener: ((Tasbeeh) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_add_custom_dhikr, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnCancel?.apply {
            setOnClickListener { dismiss() }
            text = context.getString(R.string.label_cancel)
        }
        btnSave?.apply {
            text = context.getString(R.string.label_save)
            setOnClickListener {
                if (edtAddDhikr.text.toString().isNotBlank()) {
                    val latinDhikr = edtAddDhikr.text.toString()
                    val tasbeeh = when {
                        null == dhikr ->
                            Tasbeeh(
                                id = latinDhikr,
                                arabic = "",
                                latin = latinDhikr,
                                count = 0
                            )
                        else -> dhikr
                    }
                    tasbeeh?.let { onPositiveListener?.invoke(it) }
                    dismiss()
                }
            }
        }
        ivClose?.setOnClickListener { dismiss() }
        edtAddDhikr.charCountingListener { tvCounterChar.setText("$it/300") }
    }

    fun setOnPositiveListener(onPositiveListener: (Tasbeeh) -> Unit): BottomSheetDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

    override fun show() {
        super.show()
        edtAddDhikr.setText("")
    }

}