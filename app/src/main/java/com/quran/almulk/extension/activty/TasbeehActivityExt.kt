package com.quran.almulk.extension.activty

import com.quran.almulk.utils.TasbeehConst
import com.quran.almulk.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)