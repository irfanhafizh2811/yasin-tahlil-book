package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.common.TextUtils
import com.icaali.tasbeeh.database.table.Dhikr
import com.icaali.tasbeeh.extension.text.charCountingListener
import kotlinx.android.synthetic.main.dialog_bottom_add_custom_dhikr.*

class AddCustomDialog(context: Context) : BottomSheetDialog(context) {

    var dhikr: Dhikr? = null
    private var onPositiveListener: ((Dhikr) -> Unit)? = null

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
                val latinDhikr = edtAddDhikr.text.toString()
                val dhikr = when {
                    null == dhikr ->
                        Dhikr(
                            id = latinDhikr,
                            arabic = TextUtils.BLANK,
                            latin = latinDhikr,
                            count = 0
                        )
                    else -> dhikr
                }
                dhikr?.let { onPositiveListener?.invoke(it) }
                dismiss()
            }
        }
        ivClose?.setOnClickListener { dismiss() }
        edtAddDhikr.charCountingListener { tvCounterChar.setText("$it/300") }
    }

    fun setOnPositiveListener(onPositiveListener: (Dhikr) -> Unit): BottomSheetDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

    override fun show() {
        super.show()
        edtAddDhikr.setText(TextUtils.BLANK)
    }

}