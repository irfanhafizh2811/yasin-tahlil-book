package com.app_muslim.surah_yasin.core.data.database

import androidx.room.*
import com.app_muslim.surah_yasin.core.data.dao.*
import com.app_muslim.surah_yasin.core.data.entity.*

@Database(
    entities = [
        PhotoEntity::class,
        PhotoProcessingQueueEntity::class,
        PhotoCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class PhotoDatabase : RoomDatabase() {
    
    abstract fun photoDao(): PhotoDao
    abstract fun photoProcessingQueueDao(): PhotoProcessingQueueDao
    abstract fun photoCacheDao(): PhotoCacheDao
    
    companion object {
        const val DATABASE_NAME = "photo_database"
    }
}