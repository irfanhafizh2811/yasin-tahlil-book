package com.quran.surah_almulk.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.quran.surah_almulk.R

class ThemeLeaves : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.LEAVES

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_leaves

    @DrawableRes
    override fun iconImageRes(): Int  = R.drawable.ic_picker_leaves

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_leaves

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputLeaves

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_leaves

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_leaves

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_leaves

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen_leaves

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.black

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_leaves

}