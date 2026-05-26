# Consumer proguard rules for feature-memorial-prayer

# Keep all classes in this feature module
-keep class com.app_muslim.surah_yasin.feature.memorial.prayer.** { *; }

# Hilt rules
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep class dagger.hilt.android.** { *; }

# Compose rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Firebase rules
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }