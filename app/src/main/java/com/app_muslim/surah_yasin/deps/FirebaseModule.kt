package com.app_muslim.surah_yasin.deps

import android.content.Context
import android.content.SharedPreferences
import com.app_muslim.surah_yasin.remote.CoreRemoteConfig
import com.app_muslim.surah_yasin.remote.InterstitialRemoteConfig
import com.app_muslim.surah_yasin.remote.SourceAppsRemoteConfig
import com.app_muslim.surah_yasin.services.FirebaseAuthService
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.services.StorageService
import com.app_muslim.surah_yasin.services.MessagingService
import com.app_muslim.surah_yasin.data.preference.AuthPreference
import com.app_muslim.surah_yasin.data.preference.CorePreference
import com.app_muslim.surah_yasin.core.firebase.SessionManager
import com.app_muslim.surah_yasin.core.ui.navigation.NavigationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    
    // Session Management
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("tahlil_session_prefs", Context.MODE_PRIVATE)
    }
    
    @Provides
    @Singleton
    fun provideSessionManager(
        auth: FirebaseAuth,
        authService: com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService,
        sharedPreferences: SharedPreferences
    ): SessionManager {
        return SessionManager(auth, authService, sharedPreferences)
    }
    
    @Provides
    @Singleton
    fun provideNavigationManager(sessionManager: SessionManager): NavigationManager {
        return NavigationManager(sessionManager)
    }
    
    // Modern Firebase Auth Service
    @Provides
    @Singleton
    fun provideModernFirebaseAuthService(auth: FirebaseAuth): com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService {
        return com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService(auth)
    }
}