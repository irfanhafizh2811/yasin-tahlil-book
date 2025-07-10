package com.app_muslim.surah_yasin.extension.activty

import com.app_muslim.surah_yasin.utils.TasbeehConst
import com.app_muslim.surah_yasin.view.activity.TasbeehActivity

fun TasbeehActivity.isCustomType(): Boolean = type.equals(TasbeehConst.CUSTOM, true)