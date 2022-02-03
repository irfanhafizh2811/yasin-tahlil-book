package com.icaali.tasbeeh.view.dialog

import android.content.Context
import android.os.Bundle
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.GuidePreference
import kotlinx.android.synthetic.main.dialog_guide_tasbeeh.*

class GuideTasbeehDialog(context: Context, guidePref: GuidePreference) : GuideDialog(
    context,
    guidePref
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_guide_tasbeeh)
        btnNext?.setOnClickListener {
            when {
                !guidePref.hasShownPickTheme -> showTheme()
                guidePref.hasShownPickTheme && !guidePref.hasShownVibrateSound ->
                    showVibrateSound()
                guidePref.hasShownVibrateSound && !guidePref.hasShownDhikrTarget ->
                    showTarget()
                else -> dismiss()
            }
            onNextListener?.invoke()
        }
        btnSkip?.setOnClickListener {
            onCompleted(Screen.TASBEEH)
        }
    }

    private fun showTheme() {
        clThemeGuide.visible()
        clVibrateSoundGuide.gone()
        clTargetDhikrGuide.gone()
        clTargetDhikrGuideDesc.gone()
        guidePref.hasShownPickTheme = true
        ivThemesGuide.setOnClickListener {
            dismiss()
            onTapTargetListener?.invoke()
        }
    }

    private fun showVibrateSound() {
        clThemeGuide.gone()
        clVibrateSoundGuide.visible()
        clTargetDhikrGuide.gone()
        clTargetDhikrGuideDesc.gone()
        guidePref.hasShownVibrateSound = true
        llMoreVibrateSound.setOnClickListener {
            dismiss()
            onTapTargetListener?.invoke()
        }
    }

    private fun showTarget() {
        clThemeGuide.gone()
        clVibrateSoundGuide.gone()
        clTargetDhikrGuide.visible()
        clTargetDhikrGuideDesc.visible()
        guidePref.hasShownDhikrTarget = true
        clTargetDhikrGuide.setOnClickListener {
            dismiss()
            onTapTargetListener?.invoke()
        }
    }

    private fun showGuide() {
        if (!guidePref.hasShownPickTheme) showTheme()
        else if (!guidePref.hasShownVibrateSound) showVibrateSound()
        else if (!guidePref.hasShownDhikrTarget) showTarget()
    }

    override fun show() {
        super.show()
        showGuide()
    }

    fun isShowingAll(): Boolean {
        return guidePref.skipTasbeehGuide ||
                (guidePref.hasShownPickTheme &&
                        guidePref.hasShownVibrateSound &&
                        guidePref.hasShownDhikrTarget)
    }
}