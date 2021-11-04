package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.view.adapter.TargetDhikrAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.dialog_bottom_target_dhikr.*
import java.util.concurrent.TimeUnit

class TargetDhikrDialog(
    context: Context,
    private val onPositiveListener: (Int) -> Unit
) : BottomSheetDialog(context) {

    companion object {
        private const val DELAY = 400L
    }

    private val adapter by lazy {
        TargetDhikrAdapter {
            etTargetDhikr?.setText(it.toString())
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_target_dhikr, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivClose?.setOnClickListener { dismiss() }
        rvTargetDhikr?.adapter = adapter
        btnCancel?.setOnClickListener {
            dismiss()
        }
        btnSave?.setOnClickListener {
            val targetCount = etTargetDhikr?.text?.toString().orEmpty()
            onPositiveListener.invoke(targetCount.toInt())
            dismiss()
        }

        RxTextView.afterTextChangeEvents(etTargetDhikr)
            .debounce(DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val targetCount = it?.editable()?.toString().orEmpty()
                adapter.selectedTarget(
                    if (targetCount.isEmpty()) 0
                    else targetCount.toInt()
                )
            }
    }

    fun show(targetCount: Int) {
        if (targetCount != 0) {
            etTargetDhikr?.setText(targetCount.toString())
            adapter.selectedTarget(targetCount)
        }
        show()
    }
}