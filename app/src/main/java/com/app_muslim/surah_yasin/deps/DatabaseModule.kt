package com.app_muslim.surah_yasin.deps

import android.content.Context
import androidx.room.Room
import com.app_muslim.surah_yasin.coroutine.DefaultDispatcherProvider
import com.app_muslim.surah_yasin.coroutine.DispatcherProvider
import com.app_muslim.surah_yasin.data.database.dao.SurahDao
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
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}