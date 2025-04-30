package com.quran.surah_yasin.deps

import androidx.room.Room
import com.quran.surah_yasin.coroutine.DefaultDispatcherProvider
import com.quran.surah_yasin.coroutine.DispatcherProvider
import com.quran.surah_yasin.data.database.DhikrRoomDatabase
import com.quran.surah_yasin.data.repository.TasbeehRepository
import com.quran.surah_yasin.data.repository.UserRepository
import com.quran.surah_yasin.data.repository.UserRepositoryImpl
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