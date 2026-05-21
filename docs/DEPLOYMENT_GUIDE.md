# 🚀 Deployment Guide

## Overview

This guide covers the complete deployment process for Tahlil, from development environment setup to production release, including Firebase backend deployment and Android app distribution.

## 📋 Prerequisites

### Development Environment
```bash
# Required tools
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Node.js 18+ (for Firebase Functions)
- Firebase CLI (latest version)
- Git
- Google Cloud SDK (optional, for advanced monitoring)
```

### Firebase Setup
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Verify project access
firebase projects:list
```

### Android Environment
```bash
# Environment variables
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Verify setup
android --version
gradle --version
```

## 🔧 Environment Configuration

### 1. Development Environment

```bash
# Firebase project
firebase use surah-almulk --alias development

# Environment variables
export ENVIRONMENT=development
export FIREBASE_PROJECT_ID=surah-almulk
export ANDROID_BUILD_TYPE=debug
```

#### Development Firebase Configuration
```json
// firebase.dev.json
{
  "project_id": "surah-almulk",
  "environment": "development",
  "emulators": {
    "enabled": true,
    "ports": {
      "firestore": 8080,
      "auth": 9099,
      "storage": 9199,
      "functions": 5001
    }
  }
}
```

### 2. Staging Environment

```bash
# Firebase project
firebase use surah-almulk-staging --alias staging

# Environment variables
export ENVIRONMENT=staging
export FIREBASE_PROJECT_ID=surah-almulk-staging
export ANDROID_BUILD_TYPE=staging
```

### 3. Production Environment

```bash
# Firebase project  
firebase use surah-almulk --alias production

# Environment variables
export ENVIRONMENT=production
export FIREBASE_PROJECT_ID=surah-almulk
export ANDROID_BUILD_TYPE=release
```

## 🔥 Firebase Deployment

### 1. Security Rules Deployment

```bash
# Deploy Firestore security rules
firebase deploy --only firestore:rules --project surah-almulk

# Deploy Storage security rules
firebase deploy --only storage:rules --project surah-almulk

# Deploy both
firebase deploy --only firestore:rules,storage:rules --project surah-almulk
```

#### Validation Before Deployment
```bash
# Validate Firestore rules
firebase firestore:rules:get --project surah-almulk

# Test security rules with emulator
firebase emulators:start --only firestore
npm run test:security-rules
```

### 2. Cloud Functions Deployment

```bash
# Navigate to functions directory
cd functions

# Install dependencies
npm install

# Deploy all functions
firebase deploy --only functions --project surah-almulk

# Deploy specific function
firebase deploy --only functions:memorialExpiration --project surah-almulk
```

#### Functions Environment Configuration
```javascript
// functions/src/config.js
const config = {
  development: {
    memorialExpirationDays: 40,
    maxPhotosPerMemorial: 10,
    enableDebugLogs: true
  },
  staging: {
    memorialExpirationDays: 40,
    maxPhotosPerMemorial: 10,
    enableDebugLogs: false
  },
  production: {
    memorialExpirationDays: 40,
    maxPhotosPerMemorial: 5,
    enableDebugLogs: false
  }
}
```

### 3. Database Indexes Deployment

```bash
# Deploy Firestore indexes
firebase deploy --only firestore:indexes --project surah-almulk

# Check index status
firebase firestore:indexes --project surah-almulk
```

#### Critical Indexes for Performance
```json
// firestore.indexes.json
{
  "indexes": [
    {
      "collectionGroup": "memorials",
      "queryScope": "COLLECTION",
      "fields": [
        {"fieldPath": "privacy", "order": "ASCENDING"},
        {"fieldPath": "isActive", "order": "ASCENDING"},
        {"fieldPath": "createdAt", "order": "DESCENDING"}
      ]
    },
    {
      "collectionGroup": "prayer_sessions",
      "queryScope": "COLLECTION", 
      "fields": [
        {"fieldPath": "memorialId", "order": "ASCENDING"},
        {"fieldPath": "completed", "order": "ASCENDING"},
        {"fieldPath": "startedAt", "order": "DESCENDING"}
      ]
    }
  ]
}
```

### 4. Complete Firebase Deployment

```bash
# Full deployment (staging)
firebase deploy --project surah-almulk-staging

# Full deployment (production)
firebase deploy --project surah-almulk

# Deployment with confirmation
firebase deploy --project surah-almulk --confirm
```

## 📱 Android App Deployment

### 1. Build Configuration

#### Gradle Build Types
```kotlin
// app/build.gradle
android {
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
        }
        
        staging {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            buildConfigField("String", "ENVIRONMENT", "\"staging\"")
        }
        
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
        }
    }
}
```

#### Firebase Configuration per Environment
```
app/
├── src/
│   ├── debug/
│   │   └── google-services.json          # Development
│   ├── staging/
│   │   └── google-services.json          # Staging
│   └── release/
│       └── google-services.json          # Production
```

### 2. Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Staging build
./gradlew assembleStaging

# Release build
./gradlew assembleRelease

# Install debug on device
./gradlew installDebug

# Generate signed release APK
./gradlew bundleRelease
```

### 3. Code Signing

#### Debug Keystore (Auto-generated)
```bash
# Location: ~/.android/debug.keystore
# Alias: androiddebugkey
# Password: android
```

#### Production Keystore
```bash
# Generate production keystore
keytool -genkey -v -keystore tahlil-release-key.keystore \
  -alias tahlil-release \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# Store in secure location
# Add to app/build.gradle signing configuration
```

#### Signing Configuration
```kotlin
// app/build.gradle
android {
    signingConfigs {
        debug {
            storeFile file('../debug-key.jks')
            storePassword 'android'
            keyAlias 'androiddebugkey'
            keyPassword 'android'
        }
        
        release {
            storeFile file('../release-key.jks')
            storePassword System.getenv("RELEASE_STORE_PASSWORD")
            keyAlias System.getenv("RELEASE_KEY_ALIAS")
            keyPassword System.getenv("RELEASE_KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### 4. ProGuard Configuration

```bash
# app/proguard-rules.pro

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Koin DI
-keep class org.koin.** { *; }
-keep class com.app_muslim.surah_yasin.di.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Arabic fonts and Islamic content
-keep class com.app_muslim.surah_yasin.data.model.** { *; }
-keep class com.app_muslim.surah_yasin.utils.Islamic** { *; }
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml
name: Deploy Tahlil

on:
  push:
    branches: [master, staging]
  pull_request:
    branches: [master]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          
      - name: Setup Firebase CLI
        run: npm install -g firebase-tools
        
      - name: Authenticate with Firebase
        env:
          FIREBASE_TOKEN: ${{ secrets.FIREBASE_TOKEN }}
        run: firebase use surah-almulk
        
      - name: Start Firebase Emulators
        run: firebase emulators:start --only firestore,auth,storage &
        
      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest
        
      - name: Run Security Rules Tests
        working-directory: ./functions
        run: |
          npm install
          npm run test:security
          
      - name: Cultural Validation Tests
        run: ./gradlew testCulturalValidation

  deploy-firebase:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/master'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Install Firebase CLI
        run: npm install -g firebase-tools
        
      - name: Deploy to Firebase
        env:
          FIREBASE_TOKEN: ${{ secrets.FIREBASE_TOKEN }}
        run: |
          firebase use surah-almulk
          firebase deploy --only firestore:rules,storage:rules,functions
          
  build-android:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          
      - name: Decode Keystore
        env:
          ENCODED_STRING: ${{ secrets.KEYSTORE_BASE64 }}
        run: echo $ENCODED_STRING | base64 -d > app/tahlil-release-key.keystore
        
      - name: Build Release APK
        env:
          RELEASE_STORE_PASSWORD: ${{ secrets.RELEASE_STORE_PASSWORD }}
          RELEASE_KEY_ALIAS: ${{ secrets.RELEASE_KEY_ALIAS }}
          RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
        run: ./gradlew assembleRelease
        
      - name: Upload APK to Artifacts
        uses: actions/upload-artifact@v3
        with:
          name: release-apk
          path: app/build/outputs/apk/release/app-release.apk
          
      - name: Deploy to Play Store (Internal Testing)
        if: github.ref == 'refs/heads/master'
        env:
          PLAY_STORE_JSON_KEY: ${{ secrets.PLAY_STORE_JSON_KEY }}
        run: |
          # Upload to Play Console Internal Testing
          # Use Gradle Play Publisher plugin
          ./gradlew publishReleaseBundle
```

### Environment Secrets Configuration

```bash
# GitHub Secrets to configure
FIREBASE_TOKEN=your_firebase_ci_token
KEYSTORE_BASE64=base64_encoded_keystore
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
PLAY_STORE_JSON_KEY=play_store_service_account_json
```

## 📦 Play Store Deployment

### 1. Play Store Setup

```bash
# Generate signed App Bundle (preferred format)
./gradlew bundleRelease

# Verify bundle
bundletool validate --bundle=app/build/outputs/bundle/release/app-release.aab
```

### 2. Release Configuration

#### Version Management
```kotlin
// app/build.gradle
android {
    defaultConfig {
        versionCode 1000040 // Format: MAJOR(1)MINOR(00)PATCH(00)BUILD(40)
        versionName "1.0.0"
    }
}
```

#### Release Tracks
- **Internal Testing**: QA team and beta testers
- **Closed Testing**: Selected user groups
- **Open Testing**: Public beta
- **Production**: Full release

### 3. Store Listing Optimization

#### Localized Store Listings
```
store-listing/
├── ar/                 # Arabic
├── en-US/             # English (US)
├── id/                # Indonesian
├── tr/                # Turkish
├── ru/                # Russian
└── ms/                # Malay
```

#### Screenshots and Assets
```
assets/
├── screenshots/
│   ├── phone/         # 5.5" phones
│   ├── seveninch/     # 7" tablets
│   └── teninch/       # 10" tablets
├── feature-graphic.png
├── icon-512.png
└── promo-video.mp4
```

## 🔍 Monitoring & Analytics

### 1. Firebase Performance Monitoring

```kotlin
// Enable in app/build.gradle
dependencies {
    implementation 'com.google.firebase:firebase-perf:20.4.1'
}

// Custom traces for Islamic features
class PerformanceTracker {
    fun trackPrayerSession(prayerType: PrayerType) {
        val trace = FirebasePerformance.getInstance()
            .newTrace("prayer_session_${prayerType.name.lowercase()}")
        trace.start()
        // ... prayer logic
        trace.stop()
    }
}
```

### 2. Crashlytics Configuration

```kotlin
// Enable crash reporting
dependencies {
    implementation 'com.google.firebase:firebase-crashlytics:18.4.3'
}

// Custom crash logging
class ErrorReporter {
    fun reportCulturalValidationError(error: String, memorial: Memorial) {
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("cultural_error", error)
            setCustomKey("memorial_id", memorial.id)
            setCustomKey("islamic_region", memorial.culturalSettings.region.name)
            recordException(CulturalValidationException(error))
        }
    }
}
```

### 3. Analytics Events

```kotlin
// Track Islamic app usage patterns
class IslamicAnalytics {
    fun trackMemorialCreation(memorial: Memorial) {
        Firebase.analytics.logEvent("memorial_created") {
            param("privacy_level", memorial.privacy.name)
            param("has_arabic_name", memorial.arabicName != null)
            param("islamic_region", getUserRegion())
        }
    }
    
    fun trackPrayerCompletion(session: PrayerSession) {
        Firebase.analytics.logEvent("prayer_completed") {
            param("prayer_type", session.prayerType.name)
            param("duration_minutes", session.duration / 60000)
            param("memorial_privacy", getMemorialPrivacy(session.memorialId))
        }
    }
}
```

## 🔒 Security Deployment

### 1. App Check Configuration

```kotlin
// Enable App Check for production
FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
    PlayIntegrityAppCheckProviderFactory.getInstance()
)
```

### 2. Network Security Configuration

```xml
<!-- app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">firebase.googleapis.com</domain>
        <domain includeSubdomains="true">firestore.googleapis.com</domain>
        <domain includeSubdomains="true">storage.googleapis.com</domain>
    </domain-config>
    
    <!-- Development only -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

## 📊 Health Checks & Rollback

### 1. Deployment Health Checks

```bash
#!/bin/bash
# deployment-health-check.sh

echo "Running Tahlil deployment health checks..."

# Check Firebase services
firebase projects:list | grep surah-almulk
if [ $? -ne 0 ]; then
    echo "❌ Firebase project access failed"
    exit 1
fi

# Check Firestore rules deployment
firebase firestore:rules:get --project surah-almulk > /dev/null
if [ $? -ne 0 ]; then
    echo "❌ Firestore rules check failed"
    exit 1
fi

# Check app installation
adb devices | grep device
if [ $? -ne 0 ]; then
    echo "⚠️  No Android device connected for testing"
fi

# Test app startup
adb shell am start -n com.app_muslim.surah_yasin/.view.activity.SplashActivity
sleep 5
adb shell dumpsys activity | grep "mResumedActivity.*SplashActivity"
if [ $? -eq 0 ]; then
    echo "✅ App startup successful"
else
    echo "❌ App startup failed"
    exit 1
fi

echo "✅ All health checks passed"
```

### 2. Rollback Procedures

```bash
# Firebase rollback
firebase functions:log --limit 50  # Check for errors
firebase rollback functions:memorialExpiration --project surah-almulk

# Android rollback
# Use Play Console to halt release and promote previous version

# Database rollback (if needed)
# Restore from backup with specific timestamp
```

## 📝 Deployment Checklist

### Pre-Deployment
- [ ] All tests passing (unit, integration, cultural validation)
- [ ] Security rules tested with emulators
- [ ] Performance benchmarks met
- [ ] Islamic content validated by scholars
- [ ] Localization complete for all target languages
- [ ] App signing configured correctly
- [ ] Environment-specific configurations set

### Firebase Deployment
- [ ] Security rules deployed and tested
- [ ] Cloud Functions deployed successfully
- [ ] Database indexes created
- [ ] Storage buckets configured with proper permissions
- [ ] Analytics and monitoring enabled

### Android Deployment
- [ ] APK/AAB built successfully
- [ ] Code signing verified
- [ ] ProGuard configuration tested
- [ ] App Bundle optimized
- [ ] Play Store metadata updated
- [ ] Screenshots and assets uploaded

### Post-Deployment
- [ ] Health checks executed successfully
- [ ] Error monitoring alerts configured
- [ ] Performance metrics baseline established
- [ ] User feedback channels monitored
- [ ] Islamic community validation ongoing

### Cultural Compliance
- [ ] Arabic text rendering verified across devices
- [ ] RTL layout support tested
- [ ] Islamic calendar integration working
- [ ] Prayer timing calculations accurate
- [ ] Cultural sensitivity guidelines followed
- [ ] Regional Islamic law compliance verified

This comprehensive deployment guide ensures a smooth, secure, and culturally-appropriate release process for the global Muslim community.