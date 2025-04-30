package com.quran.surah_yasin.data.model.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.quran.surah_yasin.R

class ThemeEidAlFitr : BaseTheme() {

    override fun themeType(): ThemeType =
        ThemeType.EID_AL_FITR

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_eid_al_fitr

    @DrawableRes
    override fun iconImageRes(): Int = R.drawable.ic_picker_eid_al_fitr

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_eid_al_fitr

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputEidAlFitr

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_eid_al_fitr

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_eid_al_fitr

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_transparent

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen_eid_al_fitr

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.white

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_eid_al_fitr

}