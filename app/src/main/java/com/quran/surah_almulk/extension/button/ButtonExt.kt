package com.quran.surah_almulk.extension.button

import android.graphics.drawable.Drawable
import android.widget.Button
import androidx.core.content.ContextCompat
import com.quran.surah_almulk.extension.context.getColorCompat
import com.quran.surah_almulk.extension.context.getDrawableCompat

fun Button.selected(backgroundDrawable: Drawable? = null, textColor: Int = android.R.color.white) {
    backgroundDrawable?.let {
        this.background = ContextCompat.getDrawable(this.context, android.R.drawable.btn_default)
    }
    this.setTextColor(context.getColorCompat(textColor))
}

fun Button.unselected(
    backgroundDrawable: Drawable? = null,
    textColor: Int = android.R.color.white
) {
    backgroundDrawable?.let {
        this.background = ContextCompat.getDrawable(this.context, android.R.drawable.btn_default)
    }
    this.setTextColor(context.getColorCompat(textColor))
}

fun Button.enableButton(
    backgroundDrawable: Drawable? = null,
    textColor: Int = android.R.color.white
) {
    this.isEnabled = true
    backgroundDrawable?.let {
        this.background = ContextCompat.getDrawable(this.context, android.R.drawable.btn_default)
    }
    this.setTextColor(context.getColorCompat(textColor))
}

fun Button.disableButton(
    backgroundDrawable: Drawable? = null,
    textColor: Int = android.R.color.white
) {
    this.isEnabled = false
    backgroundDrawable?.let {
        this.background = context.getDrawableCompat(android.R.drawable.btn_default)
    }
    this.setTextColor(context.getColorCompat(textColor))
}