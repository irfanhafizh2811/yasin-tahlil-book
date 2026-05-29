package com.app_muslim.surah_yasin.feature.community.di

import com.app_muslim.surah_yasin.feature.community.repository.CommunityPrayerRepository
import com.app_muslim.surah_yasin.feature.community.repository.CommunityPrayerFirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommunityModule {

    @Provides
    @Singleton
    fun provideCommunityPrayerRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        messaging: FirebaseMessaging
    ): CommunityPrayerRepository {
        return CommunityPrayerFirebaseRepository(firestore, auth, messaging)
    }
}