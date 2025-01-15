package com.icaali.almulk.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.almulk.R
import com.icaali.almulk.data.database.entity.TasbeehEntity
import com.icaali.almulk.databinding.DialogBottomAddCustomDhikrBinding
import com.icaali.almulk.extension.text.charCountingListener

class AddCustomDialog(context: Context) : BottomSheetDialog(context) {

    private lateinit var binding: DialogBottomAddCustomDhikrBinding
    var dhikr: TasbeehEntity? = null
    private var onPositiveListener: ((TasbeehEntity) -> Unit)? = null

    init {
        binding = DialogBottomAddCustomDhikrBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            btnCancel.apply {
                setOnClickListener { dismiss() }
                text = context.getString(R.string.label_cancel)
            }
            btnSave.apply {
                text = context.getString(R.string.label_save)
                setOnClickListener {
                    if (edtAddDhikr.text.toString().isNotBlank()) {
                        val latinDhikr = edtAddDhikr.text.toString()
                        val tasbeehEntity = when {
                            null == dhikr ->
                                TasbeehEntity(
                                    id = latinDhikr,
                                    arabic = "",
                                    latin = latinDhikr,
                                    count = 0
                                )

                            else -> dhikr
                        }
                        tasbeehEntity?.let { onPositiveListener?.invoke(it) }
                        dismiss()
                    }
                }
            }
            ivClose.setOnClickListener { dismiss() }
            edtAddDhikr.charCountingListener { tvCounterChar.text = "$it/300" }
        }
    }

    fun setOnPositiveListener(onPositiveListener: (TasbeehEntity) -> Unit): BottomSheetDialog {
        this.onPositiveListener = onPositiveListener
        return this
    }

    override fun show() {
        super.show()
        binding.edtAddDhikr.setText("")
    }

}