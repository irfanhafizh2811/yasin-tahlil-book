package com.icaali.tasbeeh.view.theme

object ThemeFactory {

    val themes = mutableListOf<Theme>(
        ThemeDefault(),
        ThemeFlowers(),
        ThemeLeaves(),
        ThemeBlackWhite()
    )

    fun generate(themeType: ThemeType): Theme? = themes.find {
        themeType == it.type
    }

}