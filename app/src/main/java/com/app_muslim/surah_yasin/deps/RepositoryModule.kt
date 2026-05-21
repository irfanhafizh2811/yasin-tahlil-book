package com.app_muslim.surah_yasin.deps

import com.app_muslim.surah_yasin.data.repository.MemorialRepository
import com.app_muslim.surah_yasin.data.repository.MemorialRepositoryImpl
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.services.StorageService
import com.app_muslim.surah_yasin.utils.CulturalValidator
import com.app_muslim.surah_yasin.utils.IslamicDateCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCulturalValidator(): CulturalValidator {
        return CulturalValidator()
    }

    @Provides
    @Singleton
    fun provideIslamicDateCalculator(): IslamicDateCalculator {
        return IslamicDateCalculator()
    }

    @Provides
    @Singleton
    fun provideMemorialRepository(
        firestoreService: FirestoreService,
        storageService: StorageService,
        culturalValidator: CulturalValidator,
        islamicDateCalculator: IslamicDateCalculator
    ): MemorialRepository {
        return MemorialRepositoryImpl(
            firestoreService,
            storageService, 
            culturalValidator,
            islamicDateCalculator
        )
    }
}