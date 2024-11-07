package com.icaali.almulk.model

import com.google.gson.annotations.SerializedName

data class Surah(
    @SerializedName("surah_name")
    val surahName: String,
    @SerializedName("surah_arabic")
    val surahArabic: String,
    @SerializedName("surah")
    val surah: List<Verse>
)