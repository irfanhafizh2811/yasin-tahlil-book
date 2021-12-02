package com.icaali.tasbeeh.database.table

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "dhirk_table")
@Parcelize
data class Dhikr(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "arabic") val arabic: String,
    @ColumnInfo(name = "latin") var latin: String,
    @ColumnInfo(name = "count") var count: Int
) : Parcelable