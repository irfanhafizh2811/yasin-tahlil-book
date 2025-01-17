package com.quran.surah_almulk.deps

import androidx.room.Room
import com.quran.surah_almulk.data.database.DhikrRoomDatabase
import com.quran.surah_almulk.repository.TasbeehRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication().applicationContext,
            DhikrRoomDatabase::class.java,
            "dhikr_database"
        ).build()
    }
    single { TasbeehRepository(get<DhikrRoomDatabase>().dhikrDao()) }
}