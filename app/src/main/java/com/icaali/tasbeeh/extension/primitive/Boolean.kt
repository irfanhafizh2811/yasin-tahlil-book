package com.icaali.tasbeeh.extension.primitive

fun Boolean.switchOnOff(): String = when (this) {
    true -> "ON"
    else -> "OFF"
}