package com.icaali.tasbeeh.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dhirk_table")
data class Dhikr(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "arabic") val arabic: String,
    @ColumnInfo(name = "latin") val latin: String,
    @ColumnInfo(name = "count") val count: Int
)