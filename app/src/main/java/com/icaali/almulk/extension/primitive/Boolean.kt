package com.icaali.almulk.extension.primitive

fun Boolean.switchOnOff(): String = when (this) {
    true -> "ON"
    else -> "OFF"
}