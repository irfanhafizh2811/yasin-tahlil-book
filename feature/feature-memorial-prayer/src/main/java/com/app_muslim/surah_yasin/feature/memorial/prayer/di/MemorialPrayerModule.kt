package com.app_muslim.surah_yasin.feature.memorial.prayer.di

import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerFirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for Memorial Prayer feature
 * Uses Firebase services from the main FirebaseModule to avoid duplicate bindings
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MemorialPrayerModule {
    
    @Binds
    @Singleton
    abstract fun bindMemorialPrayerRepository(
        memorialPrayerFirebaseRepository: MemorialPrayerFirebaseRepository
    ): MemorialPrayerRepository
}