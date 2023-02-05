package com.icaali.tasbeeh.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.icaali.tasbeeh.R

class ThemeKaaba : BaseTheme() {

    override fun themeType(): ThemeType = ThemeType.KAABA

    @StringRes
    override fun textStringRes (): Int= R.string.label_picker_kaaba

    @DrawableRes
    override fun iconImageRes (): Int= R.drawable.ic_picker_kaaba

    @DrawableRes
    override fun outputImageRes (): Int= R.drawable.bg_output_kaaba

    @ColorRes
    override fun outputHintColorRes (): Int= R.color.textHintOutputKaaba

    @DrawableRes
    override fun resetImageRes (): Int= R.drawable.ic_reset_kaaba

    @DrawableRes
    override fun counterImageRes (): Int= R.drawable.ic_counter_kaaba

    @DrawableRes
    override fun backgroundDigitalImageRes (): Int= R.drawable.bg_tasbeeh_digital_transparent

    @DrawableRes
    override fun backgroundScreenImageRes (): Int= R.drawable.bg_counter_screen_kaaba

    @ColorRes
    override fun tintColorAccent (): Int= android.R.color.black

    @DrawableRes
    override fun backgroundTargetCounterImageRes (): Int = R.drawable.bg_target_counter_kaaba
}