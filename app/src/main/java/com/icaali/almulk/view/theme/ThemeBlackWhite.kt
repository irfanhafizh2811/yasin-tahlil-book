package com.icaali.almulk.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.icaali.almulk.R

class ThemeBlackWhite : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.BLACK_WHITE

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_black_white

    @DrawableRes
    override fun iconImageRes(): Int = R.drawable.ic_picker_black_white

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_black_white

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputBlackWhite

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_black_white

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_black_white

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_black_white

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen_black_white

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.black

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_black_white

}