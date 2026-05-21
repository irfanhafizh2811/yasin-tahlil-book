package com.app_muslim.surah_yasin.deps

import android.content.Context
import androidx.room.Room
import com.app_muslim.surah_yasin.coroutine.DefaultDispatcherProvider
import com.app_muslim.surah_yasin.coroutine.DispatcherProvider
import com.app_muslim.surah_yasin.data.database.DhikrRoomDatabase
import com.app_muslim.surah_yasin.data.database.dao.DhikrDao
import com.app_muslim.surah_yasin.data.database.dao.SurahDao
import com.app_muslim.surah_yasin.data.repository.TasbeehRepository
import com.app_muslim.surah_yasin.data.repository.UserRepository
import com.app_muslim.surah_yasin.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DhikrRoomDatabase {
        return Room.databaseBuilder(
            context,
            DhikrRoomDatabase::class.java,
            "dhikr_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    @Singleton
    fun provideDhikrDao(database: DhikrRoomDatabase): DhikrDao {
        return database.dhikrDao()
    }

    @Provides
    @Singleton
    fun provideTasbeehRepository(dhikrDao: DhikrDao): TasbeehRepository {
        return TasbeehRepository(dhikrDao)
    }
}