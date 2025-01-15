package com.icaali.almulk.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.icaali.almulk.data.database.dao.DhikrDao
import com.icaali.almulk.data.database.entity.TasbeehEntity

@Database(
    entities = [TasbeehEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DhikrRoomDatabase : RoomDatabase() {

    abstract fun dhikrDao(): DhikrDao

}