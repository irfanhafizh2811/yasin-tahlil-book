package com.app_muslim.surah_yasin.extension.common

import android.content.Context
import android.util.Patterns
import android.util.TypedValue
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by irfanbrader on 9/7/17.
 */
inline fun <reified T : Any> clazz() = T::class.java

inline fun <reified T : Any> tag() = T::class.java.simpleName

fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.convertPhoneFormat(countryCode: String): String {
    if (this.isNotEmpty()) {
        var result = this.trim().replace("\\s".toRegex(), "")
        if (result.startsWith('0', true)) {
            result = countryCode + result.substring(1)
        }
        return result
    }
    return ""
}

fun Long.convertToCurrency(currencyType: String = ""): String {
    val value = NumberFormat.getNumberInstance().format(this).replace(',', '.')
    return currencyType + value
}

fun Int.convertToCurrency(currencyType: String = ""): String {
    val value = NumberFormat.getNumberInstance().format(this).replace(',', '.')
    return currencyType + value
}

fun String.convertDate(inputFormat: String = "yyyy-MM-dd", outputFormat: String = "dd MMMM yyyy"): String {
    val dateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val requiredFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
    try {
        val date = dateFormat.parse(this)
        return requiredFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun Double.doubleToString(decimalPattern: String = "#.#"): String {
    return try {
        val decimalFormat = DecimalFormat(decimalPattern)
        decimalFormat.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        this.toString()
    }
}

fun Int.convertDpToPx(context: Context): Int {
    return Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics))
}

fun Float.convertDpToPx(context: Context): Int {
    return Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics))
}

public val String.extension: String
    get() = this.substringAfterLast('.', "")


fun String.textDefault(): String {
    return when (this.isBlank() || this.isEmpty()) {
        true -> "-"
        else -> this
    }
}