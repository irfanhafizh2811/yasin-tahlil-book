package com.quran.surah_yasin.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dhirk_table")
data class TasbeehEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "arabic") val arabic: String,
    @ColumnInfo(name = "latin") var latin: String,
    @ColumnInfo(name = "count") var count: Int
)