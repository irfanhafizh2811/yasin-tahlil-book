package com.icaali.tasbeeh.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.icaali.tasbeeh.database.dao.DhikrDao
import com.icaali.tasbeeh.database.table.Dhikr

@Database(
    entities = [Dhikr::class],
    version = 1,
    exportSchema = false
)
abstract class DhikrRoomDatabase : RoomDatabase() {

    abstract fun dhikrDao(): DhikrDao

}