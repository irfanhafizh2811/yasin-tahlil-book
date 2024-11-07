package com.icaali.almulk.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.icaali.almulk.R

class ThemeDefault : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.DEFAULT

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_default

    @DrawableRes
    override fun iconImageRes(): Int = R.drawable.ic_picker_default

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_default

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputDefault

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_default

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_default

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_default

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.white

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_default

}