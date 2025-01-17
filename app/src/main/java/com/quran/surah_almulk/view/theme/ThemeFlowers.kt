package com.quran.surah_almulk.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.quran.surah_almulk.R

class ThemeFlowers : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.FLOWERS

    @StringRes
    override fun textStringRes(): Int = R.string.label_picker_flowers

    @DrawableRes
    override fun iconImageRes(): Int = R.drawable.ic_picker_flower

    @DrawableRes
    override fun outputImageRes(): Int = R.drawable.bg_output_flowers

    @ColorRes
    override fun outputHintColorRes(): Int = R.color.textHintOutputFlowers

    @DrawableRes
    override fun resetImageRes(): Int = R.drawable.ic_reset_flowers

    @DrawableRes
    override fun counterImageRes(): Int = R.drawable.ic_counter_flowers

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = R.drawable.bg_tasbeeh_digital_flowers

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = R.drawable.bg_counter_screen_flowers

    @ColorRes
    override fun tintColorAccent(): Int = android.R.color.white

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = R.drawable.bg_target_counter_flowers
    
}