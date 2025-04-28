package com.quran.surah_almulk.data.model

import com.google.gson.annotations.SerializedName

data class DeveloperApp(
    @SerializedName("applications")
    val applications: List<Application>
)