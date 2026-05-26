package com.app_muslim.surah_yasin.feature.memorial.prayer.di

import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for Memorial Prayer feature
 * Provides all necessary dependencies for prayer session management
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MemorialPrayerModule {
    
    @Binds
    @Singleton
    abstract fun bindMemorialPrayerRepository(
        memorialPrayerRepositoryImpl: MemorialPrayerRepositoryImpl
    ): MemorialPrayerRepository
}