package com.app_muslim.surah_yasin.core.data.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app_muslim.surah_yasin.core.data.dao.*
import com.app_muslim.surah_yasin.core.data.entity.*

/**
 * Main Tahlil application database
 * Includes all local storage entities for offline functionality
 */
@Database(
    entities = [
        PhotoEntity::class,
        PhotoProcessingQueueEntity::class,
        PhotoCacheEntity::class,
        MemorialPrayerSessionEntity::class
    ],
    version = 2, // Incremented from PhotoDatabase version
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class TahlilDatabase : RoomDatabase() {
    
    // Photo management DAOs
    abstract fun photoDao(): PhotoDao
    abstract fun photoProcessingQueueDao(): PhotoProcessingQueueDao
    abstract fun photoCacheDao(): PhotoCacheDao
    
    // Memorial prayer session DAO
    abstract fun memorialPrayerSessionDao(): MemorialPrayerSessionDao
    
    companion object {
        const val DATABASE_NAME = "tahlil_database"
        const val DATABASE_VERSION = 2
    }
}

/**
 * Database migration from version 1 (PhotoDatabase) to version 2 (TahlilDatabase)
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create memorial prayer sessions table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `memorial_prayer_sessions` (
                `id` TEXT NOT NULL,
                `memorialId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `prayerType` TEXT NOT NULL,
                `currentCount` INTEGER NOT NULL,
                `targetCount` INTEGER NOT NULL,
                `state` TEXT NOT NULL,
                `startTime` INTEGER,
                `endTime` INTEGER,
                `pausedDuration` INTEGER NOT NULL,
                `isCompleted` INTEGER NOT NULL,
                `memorialName` TEXT NOT NULL,
                `memorialPhotoUrl` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `isSynced` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`id`)
            )
        """)
        
        // Create indexes for better query performance
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_memorial_prayer_sessions_userId` ON `memorial_prayer_sessions` (`userId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_memorial_prayer_sessions_memorialId` ON `memorial_prayer_sessions` (`memorialId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_memorial_prayer_sessions_state` ON `memorial_prayer_sessions` (`state`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_memorial_prayer_sessions_createdAt` ON `memorial_prayer_sessions` (`createdAt`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_memorial_prayer_sessions_isCompleted` ON `memorial_prayer_sessions` (`isCompleted`)")
    }
}