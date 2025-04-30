package com.quran.surah_yasin.extension.activty

import com.quran.surah_yasin.utils.TasbeehConst
import com.quran.surah_yasin.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)