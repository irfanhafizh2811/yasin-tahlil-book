package com.quran.surah_almulk.view.theme

interface Theme {

    fun themeType(): ThemeType
    fun textStringRes(): Int
    fun iconImageRes(): Int
    fun outputImageRes(): Int
    fun outputHintColorRes(): Int
    fun resetImageRes(): Int
    fun counterImageRes(): Int
    fun backgroundDigitalImageRes(): Int
    fun backgroundScreenImageRes(): Int
    fun tintColorAccent(): Int
    fun backgroundTargetCounterImageRes(): Int
    fun isVisibleNewBadge(): Boolean
    fun setVisibleNewBad(isVisible: Boolean)

}