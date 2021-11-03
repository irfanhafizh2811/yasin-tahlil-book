package com.icaali.tasbeeh.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.icaali.tasbeeh.R

class ThemeDefault : BaseTheme() {

    override var type: ThemeType
        get() = ThemeType.DEFAULT
        set(value) {}

    override var textStringRes: Int
        @StringRes
        get() = R.string.label_picker_default
        set(value) {}

    override var iconImageRes: Int
        @DrawableRes
        get() = R.drawable.ic_picker_default
        set(value) {}

    override var outputImageRes: Int
        @DrawableRes
        get() = R.drawable.bg_output_default
        set(value) {}

    override var outputHintColorRes: Int
        @ColorRes
        get() = R.color.textHintOutputDefault
        set(value) {}

    override var resetImageRes: Int
        @DrawableRes
        get() = R.drawable.ic_reset_default
        set(value) {}

    override var counterImageRes: Int
        @DrawableRes
        get() = R.drawable.ic_counter_default
        set(value) {}

    override var backgroundDigitalImageRes: Int
        @DrawableRes
        get() = R.drawable.bg_tasbeeh_digital_default
        set(value) {}

    override var backgroundScreenImageRes: Int
        @DrawableRes
        get() = R.drawable.bg_counter_screen
        set(value) {}

    override var tintColorAccent: Int
        @ColorRes
        get() = android.R.color.white
        set(value) {}

    override var backgroundTargetCounterImageRes: Int
        get() = R.drawable.bg_target_counter_default
        set(value) {}

}