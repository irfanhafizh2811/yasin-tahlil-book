package com.quran.surah_yasin.data.model

import com.google.gson.annotations.SerializedName

data class Application(
    @SerializedName("name")
    val name: String,
    @SerializedName("package")
    val packageId: String,
    @SerializedName("image_url")
    val imageUrl: String
)