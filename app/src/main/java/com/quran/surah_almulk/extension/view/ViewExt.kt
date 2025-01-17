package com.quran.surah_almulk.extension.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import com.quran.surah_almulk.extension.context.getCurrentActivity
import com.google.android.material.snackbar.Snackbar
import com.quran.surah_almulk.extension.activty.getContentView

/**
 * Created by irfanbrader on 9/7/17.
 */

/**
 * Extension to simplify getString without accessing resources
 */
fun View.getString(@StringRes stringResId: Int): String =
    this.context.resources.getString(stringResId)

/**
 * Extension method to provide show keyboard for [View].
 */
fun View.showKeyboard() {
    val imm: InputMethodManager by lazy { this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * Extension method to provide hide keyboard for [View].
 */
fun View.hideKeyboard() {
    val imm: InputMethodManager by lazy { this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
inline fun View.showSnackbar(
    snackbarText: String,
    timeLength: Int = Snackbar.LENGTH_LONG,
    listener: Snackbar.() -> Unit = {}
) =
    Snackbar.make(this, snackbarText, timeLength).also {
        it.listener()
        it.showCompat(this.context)
    }


inline fun View.showSnackbar(
    @StringRes snackbarTextRes: Int,
    timeLength: Int = Snackbar.LENGTH_LONG,
    listener: Snackbar.() -> Unit = {}
) =
    Snackbar.make(this, snackbarTextRes, timeLength).also {
        it.listener()
        it.showCompat(this.context)
    }

fun Snackbar.showCompat(context: Context) {
    val curActivity = context.getCurrentActivity()
    if (!this.isShownOrQueued) {
        curActivity?.getContentView()?.viewTreeObserver?.let {
            it.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    this@showCompat.show()
                    it.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }
}

fun View.goneIf(condition: Boolean) {
    if (condition) this.visibility = View.GONE else this.visibility = View.VISIBLE
}

fun View.invisibleIf(condition: Boolean) {
    if (condition) {
        this.visibility = View.INVISIBLE
        this.isEnabled = false
    } else {
        this.visibility = View.VISIBLE
        this.isEnabled = true
    }
}

fun View.isViewVisible(): Boolean = this.visibility == View.VISIBLE

fun View.scheduleStartPostponedTransition(activity: Activity) {
    this.viewTreeObserver.addOnPreDrawListener(
        object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                this@scheduleStartPostponedTransition.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition(activity)
                return true
            }
        })
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun View.getDimension(@DimenRes dimenResId: Int): Float =
    this.context.resources.getDimension(dimenResId)