package com.icaali.almulk.model

import com.google.gson.annotations.SerializedName

data class Dhikr(
    val surah: String,
    val prayer: String,
    @SerializedName("mean_ind")
    val meanInd: String,
    @SerializedName("mean_eng")
    val meanEng: String,
    val times: Int
)