package com.icaali.tasbeeh.view.theme

object ThemeFactory {

    val themes = mutableListOf<Theme>(
        ThemeDefault(),
        ThemeFlowers(),
        ThemeLeaves(),
        ThemeBlackWhite(),
        ThemeYellow(),
        ThemeKaaba(),
        ThemeRamadhan(),
        ThemeEidAlFitr()
    )

    fun generate(themeType: ThemeType): Theme? = themes.find {
        themeType == it.themeType()
    }

}