package com.quran.almulk.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.quran.almulk.databinding.DialogGuideTasbeehBinding
import com.quran.almulk.extension.view.gone
import com.quran.almulk.extension.view.visible
import com.quran.almulk.data.preference.GuidePreference

class GuideTasbeehDialog(context: Context, guidePref: GuidePreference) : GuideDialog(
    context,
    guidePref
) {

    private lateinit var binding: DialogGuideTasbeehBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogGuideTasbeehBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        with(binding) {
            btnNext.setOnClickListener {
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
            btnSkip.setOnClickListener {
                onCompleted(Screen.TASBEEH)
            }
        }
    }

    private fun showTheme() = with(binding) {
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

    private fun showVibrateSound() = with(binding) {
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

    private fun showTarget() = with(binding) {
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