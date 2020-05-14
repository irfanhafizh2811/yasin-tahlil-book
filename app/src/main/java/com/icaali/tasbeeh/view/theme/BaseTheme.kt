package com.icaali.tasbeeh.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

abstract class BaseTheme : Theme {

    override var type: ThemeType
        get() = ThemeType.DEFAULT
        set(value) {}

    override var textStringRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var iconImageRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var outputImageRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var outputHintColorRes: Int
        @ColorRes
        get() = 0
        set(value) {}

    override var resetImageRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var counterImageRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var backgroundDigitalImageRes: Int
        @DrawableRes
        get() = 0
        set(value) {}

    override var tintColorAccent: Int
        @ColorRes
        get() = 0
        set(value) {}

}