package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.icaali.tasbeeh.R
import kotlinx.android.synthetic.main.dialog_bottom_more.*

class MoreDialog(context: Context) : BottomSheetDialog(context) {

    enum class Menu {
        RATING_AND_REVIEW,
        APP_LINK
    }

    private var onSelectedListener: ((Menu) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_bottom_more, clContainer, false
        )
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        llPlaystoreRate?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.RATING_AND_REVIEW)
        }
        llLinkApp?.setOnClickListener {
            dismiss()
            onSelectedListener?.invoke(Menu.APP_LINK)
        }
        ivClose?.setOnClickListener { dismiss() }
    }

    fun setOnSelectedListener(onSelectedListener: (Menu) -> Unit): MoreDialog {
        this.onSelectedListener = onSelectedListener
        return this
    }

}