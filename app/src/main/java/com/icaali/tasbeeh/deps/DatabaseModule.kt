package com.icaali.tasbeeh.deps

import androidx.room.Room
import com.icaali.tasbeeh.database.DhikrRoomDatabase
import com.icaali.tasbeeh.repository.DhikrRepository
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
    single { DhikrRepository(get<DhikrRoomDatabase>().dhikrDao()) }
}