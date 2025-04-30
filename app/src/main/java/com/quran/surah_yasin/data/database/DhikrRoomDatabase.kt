package com.quran.surah_yasin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quran.surah_yasin.data.database.dao.DhikrDao
import com.quran.surah_yasin.data.database.entity.TasbeehEntity

@Database(
    entities = [TasbeehEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DhikrRoomDatabase : RoomDatabase() {

    abstract fun dhikrDao(): DhikrDao

}