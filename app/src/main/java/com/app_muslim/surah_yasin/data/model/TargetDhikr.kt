package com.app_muslim.surah_yasin.data.model

import com.google.gson.annotations.SerializedName

data class TargetDhikr(
    @SerializedName("name") val name: String,
    @SerializedName("count") val count: Int,
    var isSelected: Boolean = false
)
