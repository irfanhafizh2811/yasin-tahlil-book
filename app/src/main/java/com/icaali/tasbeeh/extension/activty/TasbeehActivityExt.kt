package com.icaali.tasbeeh.extension.activty

import com.icaali.tasbeeh.view.Tasbeeh
import com.icaali.tasbeeh.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(Tasbeeh.CUSTOM, true)