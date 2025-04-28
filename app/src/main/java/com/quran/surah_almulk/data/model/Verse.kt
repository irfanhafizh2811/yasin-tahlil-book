package com.quran.surah_almulk.data.model

import com.google.gson.annotations.SerializedName

data class Verse(
    @SerializedName("verse_arabic")
    val arabic: String,
    @SerializedName("verse_latin")
    val latin: String,
    @SerializedName("mean_ind")
    val meanIndo: String
)