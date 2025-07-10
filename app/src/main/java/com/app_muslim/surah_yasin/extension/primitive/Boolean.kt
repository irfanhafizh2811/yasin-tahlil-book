package com.app_muslim.surah_yasin.extension.primitive

fun Boolean.switchOnOff(): String = when (this) {
    true -> "ON"
    else -> "OFF"
}