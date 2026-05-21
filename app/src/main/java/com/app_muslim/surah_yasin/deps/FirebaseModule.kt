package com.app_muslim.surah_yasin.deps

import com.app_muslim.surah_yasin.remote.CoreRemoteConfig
import com.app_muslim.surah_yasin.remote.InterstitialRemoteConfig
import com.app_muslim.surah_yasin.remote.SourceAppsRemoteConfig
import com.app_muslim.surah_yasin.services.FirebaseAuthService
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.services.StorageService
import com.app_muslim.surah_yasin.services.MessagingService
import com.app_muslim.surah_yasin.data.preference.AuthPreference
import com.app_muslim.surah_yasin.data.preference.CorePreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    // Existing Remote Config Services
    @Provides
    @Singleton
    fun provideCoreRemoteConfig(): CoreRemoteConfig = CoreRemoteConfig()
    
    @Provides
    @Singleton
    fun provideInterstitialRemoteConfig(coreRemoteConfig: CoreRemoteConfig): InterstitialRemoteConfig {
        return InterstitialRemoteConfig(coreRemoteConfig.remoteConfig)
    }
    
    @Provides
    @Singleton
    fun provideSourceAppsRemoteConfig(coreRemoteConfig: CoreRemoteConfig): SourceAppsRemoteConfig {
        return SourceAppsRemoteConfig(coreRemoteConfig.remoteConfig)
    }
    
    // Firebase SDK Instances
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}