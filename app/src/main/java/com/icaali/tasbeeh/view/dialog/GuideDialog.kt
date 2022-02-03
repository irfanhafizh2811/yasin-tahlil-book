package com.icaali.tasbeeh.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.preference.GuidePreference

open class GuideDialog(context: Context, val guidePref: GuidePreference)
    : Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen) {

    enum class Screen {
        MAIN, TASBEEH
    }

    //----------------------   Access Protected   ----------------------
    protected var onNextListener: (() -> Unit)? = null
    //---------------------- End Access Protected ----------------------

    //----------------------   Access Public   ----------------------
    var onTapTargetListener: (() -> Unit)? = null
    //---------------------- End Access Public ----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundTarget))
            )
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
    }

    override fun show() {
        super.show()
    }

    protected fun onCompleted(screen: Screen) {
        when (screen) {
            Screen.MAIN -> {
                guidePref.hasShownAddDhikr = true
                guidePref.skipMainGuide = true
            }
            Screen.TASBEEH -> {
                guidePref.hasShownDhikrTarget = true
                guidePref.hasShownPickTheme = true
                guidePref.hasShownVibrateSound = true
                guidePref.skipTasbeehGuide = true
            }
        }
        dismiss()
    }
}