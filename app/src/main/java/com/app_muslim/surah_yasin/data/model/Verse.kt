package com.app_muslim.surah_yasin.data.model

import com.app_muslim.surah_yasin.utils.TextUtils
import com.google.gson.annotations.SerializedName

data class Verse(
    @SerializedName("verse_arabic") val arabic: String = TextUtils.BLANK,
    @SerializedName("verse_latin") val latin: String = TextUtils.BLANK,
    @SerializedName("mean_ind") val meanInd: String = TextUtils.BLANK,
    @SerializedName("mean_eng") val meanEng: String = TextUtils.BLANK,
    @SerializedName("mean_tur") val meanTur: String = TextUtils.BLANK,
    @SerializedName("mean_may") val meanMay: String = TextUtils.BLANK,
    @SerializedName("mean_rus") val meanRus: String = TextUtils.BLANK
)