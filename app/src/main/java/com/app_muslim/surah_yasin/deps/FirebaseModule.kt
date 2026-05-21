package com.app_muslim.surah_yasin.deps

import com.app_muslim.surah_yasin.remote.CoreRemoteConfig
import com.app_muslim.surah_yasin.remote.InterstitialRemoteConfig
import com.app_muslim.surah_yasin.remote.SourceAppsRemoteConfig
import com.app_muslim.surah_yasin.services.FirebaseAuthService
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.services.StorageService
import com.app_muslim.surah_yasin.services.MessagingService
import com.app_muslim.surah_yasin.data.preference.AuthPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.dsl.module

val firebaseModule = module {
    
    // Existing Remote Config Services
    single { CoreRemoteConfig() }
    single { InterstitialRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    single { SourceAppsRemoteConfig(get<CoreRemoteConfig>().remoteConfig) }
    
    // Complete Firebase Ecosystem (2026 Latest)
    // Firebase SDK Instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseFunctions.getInstance() }
    single { FirebaseMessaging.getInstance() }
    
    // Firebase Services
    single { FirebaseAuthService(get()) }
    single { FirestoreService(get()) }
    single { StorageService(get()) }
    single { MessagingService(get()) }
    
    // Authentication Preferences
    single { AuthPreference(get()) }
    
    // Note: Add Repository Layer when you create them:
    // single<MemorialRepository> { MemorialRepositoryImpl(get(), get()) }
}