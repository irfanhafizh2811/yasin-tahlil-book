package com.quran.surah_almulk.data.model.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.quran.surah_almulk.R

class ThemeYellow : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.YELLOW

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_yellow

    @DrawableRes
    override fun iconImageRes(): Int = R.drawable.ic_picker_yellow

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_yellow

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputYellow

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_yellow

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_yellow

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_yellow

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen_yellow

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.white

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_yellow
}