package com.quran.surah_yasin.data.model

import com.google.gson.annotations.SerializedName

data class DeveloperApp(
    @SerializedName("applications")
    val applications: List<Application>
)