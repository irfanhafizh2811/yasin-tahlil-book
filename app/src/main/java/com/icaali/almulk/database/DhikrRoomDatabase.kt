package com.icaali.almulk.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.icaali.almulk.database.dao.DhikrDao
import com.icaali.almulk.database.table.Tasbeeh

@Database(
    entities = [Tasbeeh::class],
    version = 1,
    exportSchema = false
)
abstract class DhikrRoomDatabase : RoomDatabase() {

    abstract fun dhikrDao(): DhikrDao

}