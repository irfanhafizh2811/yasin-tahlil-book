package com.app_muslim.surah_yasin.feature.memorial.prayer.di

import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepositoryStub
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Memorial Prayer feature dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MemorialPrayerModule {

    /**
     * Bind the stub repository implementation
     * This will be replaced with proper Firebase implementation later
     */
    @Binds
    @Singleton
    abstract fun bindMemorialPrayerRepository(
        repository: MemorialPrayerRepositoryStub
    ): MemorialPrayerRepository
}