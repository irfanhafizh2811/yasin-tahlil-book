package com.icaali.tasbeeh.extension.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import org.jetbrains.anko.alert

fun Context.showAlert(
    @StringRes messageRes: Int,
    @StringRes btnLabelRes: Int = android.R.string.ok,
    onClicked: (DialogInterface) -> Unit = { it.dismiss() }
) {
    alert(messageRes) { positiveButton(btnLabelRes, onClicked) }.show()
}

fun Context.showAlert(
    message: String,
    @StringRes btnLabelRes: Int = android.R.string.ok,
    onClicked: (DialogInterface) -> Unit = { it.dismiss() }
) {
    alert(message) { positiveButton(btnLabelRes, onClicked) }.show()
}

fun Context.showAlert(
    message: String,
    @StringRes btnPostive: Int = android.R.string.ok,
    @StringRes btnNegative: Int = android.R.string.cancel,
    onClickedPositive: (DialogInterface) -> Unit = { it.dismiss() },
    onClickedNegative: (DialogInterface) -> Unit = { it.dismiss() }
) {
    alert(message) {
        positiveButton(btnPostive, onClickedPositive)
        negativeButton(btnNegative, onClickedNegative)
    }.show()
}