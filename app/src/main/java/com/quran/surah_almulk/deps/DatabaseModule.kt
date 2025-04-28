package com.quran.surah_almulk.deps

import androidx.room.Room
import com.quran.surah_almulk.coroutine.DefaultDispatcherProvider
import com.quran.surah_almulk.coroutine.DispatcherProvider
import com.quran.surah_almulk.data.database.DhikrRoomDatabase
import com.quran.surah_almulk.data.repository.TasbeehRepository
import com.quran.surah_almulk.data.repository.UserRepository
import com.quran.surah_almulk.data.repository.UserRepositoryImpl
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
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { TasbeehRepository(get<DhikrRoomDatabase>().dhikrDao()) }
}