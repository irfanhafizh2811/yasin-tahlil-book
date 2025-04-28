package com.quran.surah_almulk.data.model.theme

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