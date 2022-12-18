package com.icaali.tasbeeh.extension.activty

import com.icaali.tasbeeh.utils.TasbeehConst
import com.icaali.tasbeeh.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)