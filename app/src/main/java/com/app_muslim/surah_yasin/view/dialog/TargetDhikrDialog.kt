package com.app_muslim.surah_yasin.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.app_muslim.surah_yasin.databinding.DialogBottomTargetDhikrBinding
import com.app_muslim.surah_yasin.view.adapter.TargetDhikrAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class TargetDhikrDialog(
    context: Context,
    private val onPositiveListener: (Int) -> Unit
) : BottomSheetDialog(context) {

    companion object {
        private const val DELAY = 400L
    }

    private var binding: DialogBottomTargetDhikrBinding =
        DialogBottomTargetDhikrBinding.inflate(LayoutInflater.from(context))
    private val compositeDisposable = CompositeDisposable()
    private val adapter by lazy {
        TargetDhikrAdapter {
            binding.etTargetDhikr.setText(it.toString())
        }
    }

    init {
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            ivClose.setOnClickListener { dismiss() }
            rvTargetDhikr.adapter = adapter
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnSave.setOnClickListener {
                val targetCount = etTargetDhikr.text?.toString().orEmpty()
                if (targetCount.isNotEmpty()) {
                    onPositiveListener.invoke(targetCount.toInt())
                    dismiss()
                }
            }

            compositeDisposable.addAll(
                RxTextView.afterTextChangeEvents(etTargetDhikr)
                    .debounce(DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { e -> e.printStackTrace() }
                    .subscribe {
                        val targetCount = it?.editable()?.toString().orEmpty()
                        adapter.selectedTarget(
                            if (targetCount.isEmpty()) 0
                            else targetCount.toInt()
                        )
                    }
            )
        }
    }

    fun show(targetCount: Int) = with(binding) {
        if (targetCount != 0) {
            etTargetDhikr.setText(targetCount.toString())
            adapter.selectedTarget(targetCount)
        }
        show()
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.dispose()
        super.onDetachedFromWindow()
    }
}