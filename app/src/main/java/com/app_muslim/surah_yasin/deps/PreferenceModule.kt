package com.app_muslim.surah_yasin.deps

import android.content.Context
import com.app_muslim.surah_yasin.data.preference.*
import com.app_muslim.surah_yasin.data.repository.UserRepository
import com.app_muslim.surah_yasin.data.repository.UserRepositoryImpl
import com.app_muslim.surah_yasin.remote.InterstitialRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun provideCorePreference(@ApplicationContext context: Context): CorePreference {
        return CorePreference.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAuthPreference(corePreference: CorePreference): AuthPreference {
        return AuthPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideUserPreference(corePreference: CorePreference): UserPreference {
        return UserPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideInterstitialPreference(
        corePreference: CorePreference,
        interstitialRemoteConfig: InterstitialRemoteConfig
    ): InterstitialPreference {
        return InterstitialPreference(corePreference, interstitialRemoteConfig)
    }

    @Provides
    @Singleton
    fun provideCounterPreference(corePreference: CorePreference): CounterPreference {
        return CounterPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideThemesPreference(corePreference: CorePreference): ThemesPreference {
        return ThemesPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideSettingPreference(corePreference: CorePreference): SettingPreference {
        return SettingPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideGuidePreference(corePreference: CorePreference): GuidePreference {
        return GuidePreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideSurahPreference(corePreference: CorePreference): SurahPreference {
        return SurahPreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideLanguagePreference(corePreference: CorePreference): LanguagePreference {
        return LanguagePreference(corePreference)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userPreference: UserPreference): UserRepository {
        return UserRepositoryImpl(userPreference)
    }
}