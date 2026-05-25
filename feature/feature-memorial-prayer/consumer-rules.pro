# Consumer ProGuard rules for :feature-memorial-prayer

# Keep all model classes for Firebase serialization
-keep class com.app_muslim.surah_yasin.feature.memorial.prayer.model.** { *; }

# Keep Room entities and DAOs
-keep class com.app_muslim.surah_yasin.feature.memorial.prayer.repository.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep Navigation classes
-keep class androidx.navigation.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }

# Prevent obfuscation of ViewModel classes
-keep class com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel.** { *; }

# Keep enum classes
-keepclassmembers enum com.app_muslim.surah_yasin.feature.memorial.prayer.model.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}