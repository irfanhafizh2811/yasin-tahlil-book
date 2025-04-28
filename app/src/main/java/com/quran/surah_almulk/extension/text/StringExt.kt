package com.quran.surah_almulk.extension.text

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") {
        it.lowercase().replaceFirstChar { char -> char.uppercaseChar() }
    }
