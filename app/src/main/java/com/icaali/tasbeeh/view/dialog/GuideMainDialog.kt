package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.preference.GuidePreference
import kotlinx.android.synthetic.main.dialog_guide_main.*

class GuideMainDialog(context: Context, guidePref: GuidePreference) : GuideDialog(
    context,
    guidePref
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_guide_main)
        cvAddDhikr?.setOnClickListener {
            guidePref.hasShownAddDhikr = true
            onTapTargetListener?.invoke()
            onCompleted(Screen.MAIN)
        }
        btnNext?.setOnClickListener {
            onNextListener?.invoke()
            onCompleted(Screen.MAIN)
        }
        btnSkip?.setOnClickListener {
            onCompleted(Screen.MAIN)
        }
    }

    override fun show() {
        super.show()
    }

    fun isShowingAll(): Boolean {
        return guidePref.skipMainGuide || (guidePref.hasShownAddDhikr)
    }
}