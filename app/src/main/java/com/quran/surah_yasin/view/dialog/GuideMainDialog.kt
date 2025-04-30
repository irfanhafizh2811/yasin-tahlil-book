package com.quran.surah_yasin.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.quran.surah_yasin.databinding.DialogGuideMainBinding
import com.quran.surah_yasin.data.preference.GuidePreference

class GuideMainDialog(context: Context, guidePref: GuidePreference) : GuideDialog(
    context,
    guidePref
) {

    private lateinit var binding: DialogGuideMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogGuideMainBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        with(binding) {
            cvAddDhikr.setOnClickListener {
                guidePref.hasShownAddDhikr = true
                onTapTargetListener?.invoke()
                onCompleted(Screen.MAIN)
            }
            btnNext.setOnClickListener {
                onNextListener?.invoke()
                onCompleted(Screen.MAIN)
            }
            btnSkip.setOnClickListener {
                onCompleted(Screen.MAIN)
            }
        }
    }

    override fun show() {
        super.show()
    }

    fun isShowingAll(): Boolean {
        return guidePref.skipMainGuide || (guidePref.hasShownAddDhikr)
    }
}