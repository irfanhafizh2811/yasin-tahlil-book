package com.quran.surah_almulk.extension.activty

import com.quran.surah_almulk.utils.TasbeehConst
import com.quran.surah_almulk.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)