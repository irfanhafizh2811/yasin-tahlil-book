package com.icaali.almulk.extension.activty

import com.icaali.almulk.utils.TasbeehConst
import com.icaali.almulk.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)