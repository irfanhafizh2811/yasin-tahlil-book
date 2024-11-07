package com.icaali.almulk.view.theme

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

abstract class BaseTheme : Theme {

    private var visibleNewBadge: Boolean = false

    override fun themeType(): ThemeType = ThemeType.DEFAULT

    @DrawableRes
    override fun textStringRes(): Int = 0

    @DrawableRes
    override fun iconImageRes(): Int = 0

    @DrawableRes
    override fun outputImageRes(): Int = 0

    @ColorRes
    override fun outputHintColorRes(): Int = 0

    @DrawableRes
    override fun resetImageRes(): Int = 0

    @DrawableRes
    override fun counterImageRes(): Int = 0

    @DrawableRes
    override fun backgroundDigitalImageRes(): Int = 0

    @ColorRes
    override fun tintColorAccent(): Int = 0

    @DrawableRes
    override fun backgroundScreenImageRes(): Int = 0

    @DrawableRes
    override fun backgroundTargetCounterImageRes(): Int = 0

    override fun isVisibleNewBadge(): Boolean = visibleNewBadge

    override fun setVisibleNewBad(isVisible: Boolean) {
        this.visibleNewBadge = isVisible
    }
}