# 🔥 Modern Firebase Architecture - Tahlil Global Platform
## Comprehensive Firebase Ecosystem Integration with Latest Technologies

### 📋 Overview

Complete modern serverless architecture leveraging the full Firebase ecosystem with cutting-edge technologies for the global Islamic memorial prayer platform "Tahlil".

---

## 🏗️ Architecture Stack (2026 Latest Technologies)

### 📱 **Frontend Architecture**
```typescript
// React Native 0.76.x with Expo 52
├── React Native 0.76.5 (New Architecture/Fabric)
├── Expo SDK 52 (Latest EAS Build & Updates)
├── TypeScript 5.3+ (Latest with decorators)
├── React Native Reanimated 3.8+ (Shared Element Transitions)
├── React Native Gesture Handler 2.15+
├── Expo Router v4 (File-based routing)
└── React Native MMKV (Ultra-fast storage)

// State Management
├── Zustand 4.5+ (Lightweight state management)
├── TanStack Query v5 (Server state management)
├── React Hook Form 7.52+ (Form state)
└── Jotai 2.6+ (Atomic state for complex UI)
```

### 🔥 **Firebase Ecosystem (Complete Integration)**
```typescript
// Firebase v10+ with Modular SDK
├── Firebase Auth (Multi-provider authentication)
├── Cloud Firestore (NoSQL database with offline support)
├── Cloud Storage (Media and document storage)
├── Cloud Functions v2 (Node.js 20 runtime)
├── Firebase Analytics (User behavior tracking)
├── Cloud Messaging (Push notifications)
├── Remote Config (Feature flags & A/B testing)
├── App Distribution (Beta testing)
├── Performance Monitoring (App performance insights)
├── Crashlytics (Crash reporting)
├── App Check (Anti-abuse protection)
├── Authentication (Email, Google, Apple, Phone)
└── Extensions (Pre-built solutions)
```

### 🎨 **UI/UX Technology Stack**
```typescript
// Modern UI Framework
├── NativeBase 4.0+ / Gluestack UI (Design System)
├── React Native Elements 4.0+
├── React Native Paper 5.12+ (Material Design 3)
├── Lottie React Native 6.5+ (Animations)
├── React Native Skia 1.0+ (2D Graphics)
├── React Native SVG 15.0+ (Vector graphics)
└── Expo Linear Gradient (Beautiful gradients)

// Typography & Internationalization
├── React Native Super Grid 5.0+
├── React Native RTL Support (Arabic/Urdu)
├── i18next + react-i18next 23.8+
├── Islamic Calendar integration
└── Hijri Date calculations
```

### ⚡ **Performance & Optimization**
```typescript
// Performance Stack
├── React Native Performance Monitor
├── Flipper Integration (Development debugging)
├── Expo Development Build (Custom dev clients)
├── EAS Updates (Over-the-air updates)
├── React Native Hermes Engine (JavaScript optimization)
├── Metro bundler optimization
└── Image optimization with Sharp
```

---

## 🔥 Firebase Services Architecture

### 🔐 **Authentication Architecture**
```typescript
// Multi-Provider Authentication Setup
interface AuthProviders {
  email: {
    provider: 'password'
    features: ['email_verification', 'password_reset']
    customClaims: ['islamic_school', 'country', 'language']
  }
  
  social: {
    google: 'google.com'
    apple: 'apple.com' // iOS only
    facebook: 'facebook.com' // Optional
  }
  
  phone: {
    provider: 'phone'
    regions: ['global'] // International phone support
    verification: 'sms'
  }
  
  anonymous: {
    provider: 'anonymous'
    upgradeFlow: true // Convert anonymous to permanent
  }
}

// Custom Claims for Islamic App
interface IslamicCustomClaims {
  islamic_school: 'sunni' | 'shia' | 'other'
  prayer_calculation_method: string
  preferred_language: string
  country_code: string
  timezone: string
  memorial_privacy_level: 'public' | 'private' | 'family_only'
}
```

### 🗄️ **Cloud Firestore Database Schema**
```typescript
// Firestore Collections Structure
interface FirestoreSchema {
  // User Management
  users: {
    [uid: string]: {
      profile: UserProfile
      preferences: IslamicPreferences
      privacy: PrivacySettings
      created_at: Timestamp
      updated_at: Timestamp
    }
  }
  
  // Memorial Prayers (Core Feature)
  memorials: {
    [memorial_id: string]: {
      deceased_name: string
      deceased_info: DeceasedInfo
      creator_uid: string
      prayer_text: PrayerText
      media: MediaAssets[]
      privacy_level: 'public' | 'private' | 'family_only'
      location: GeoPoint
      created_at: Timestamp
      expires_at: Timestamp // Auto-deletion after 40 days
      prayer_count: number
      participant_count: number
    }
  }
  
  // Prayer Participation
  memorial_prayers: {
    [prayer_id: string]: {
      memorial_id: string
      user_uid: string
      prayer_type: 'yasin' | 'tahlil' | 'dua'
      prayer_text: string
      completed_at: Timestamp
      location?: GeoPoint
    }
  }
  
  // Social Features
  memorial_shares: {
    [share_id: string]: {
      memorial_id: string
      user_uid: string
      platform: 'whatsapp' | 'telegram' | 'facebook' | 'twitter'
      shared_at: Timestamp
    }
  }
  
  // Prayer Counter (Tasbeeh)
  prayer_sessions: {
    [session_id: string]: {
      user_uid: string
      prayer_type: string
      count: number
      target_count?: number
      started_at: Timestamp
      completed_at?: Timestamp
      session_duration: number // seconds
    }
  }
  
  // Islamic Content
  islamic_content: {
    prayers: {
      [prayer_id: string]: {
        type: 'yasin' | 'tahlil' | 'dua' | 'dhikr'
        arabic_text: string
        transliteration: string
        translations: {
          [language: string]: string
        }
        audio_url?: string
        verified_by_scholar: boolean
      }
    }
    
    chapters: {
      [chapter_id: string]: {
        name: string
        arabic_name: string
        number: number
        verses: VerseReference[]
        themes: string[]
      }
    }
  }
  
  // App Analytics
  app_analytics: {
    daily_stats: {
      [date: string]: {
        total_prayers: number
        unique_users: number
        memorials_created: number
        shares_count: number
      }
    }
  }
}

// Firestore Security Rules
const securityRules = `
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Memorial access based on privacy level
    match /memorials/{memorialId} {
      allow read: if resource.data.privacy_level == 'public' ||
                     (request.auth != null && 
                      (resource.data.creator_uid == request.auth.uid ||
                       resource.data.privacy_level == 'family_only'));
      allow write: if request.auth != null && request.auth.uid == resource.data.creator_uid;
      allow create: if request.auth != null;
    }
    
    // Prayer participation
    match /memorial_prayers/{prayerId} {
      allow read, write: if request.auth != null && 
                           request.auth.uid == resource.data.user_uid;
      allow create: if request.auth != null;
    }
    
    // Islamic content (read-only for users)
    match /islamic_content/{document=**} {
      allow read: if true;
      allow write: if request.auth != null && 
                     request.auth.token.role == 'admin';
    }
  }
}
`;
```

### ☁️ **Cloud Functions v2 Architecture**
```typescript
// Cloud Functions with Node.js 20
import { onCall, onRequest, HttpsError } from 'firebase-functions/v2/https';
import { onDocumentCreated, onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { onSchedule } from 'firebase-functions/v2/scheduler';

// Memorial Auto-Expiration (40-day Islamic tradition)
export const cleanupExpiredMemorials = onSchedule(
  { schedule: '0 0 * * *', timeZone: 'UTC' },
  async (event) => {
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - 40);
    
    const expiredMemorials = await admin.firestore()
      .collection('memorials')
      .where('expires_at', '<=', cutoffDate)
      .get();
    
    const batch = admin.firestore().batch();
    expiredMemorials.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
  }
);

// Memorial Creation with Islamic Validation
export const createMemorial = onCall(
  { cors: true },
  async (request) => {
    const { data, auth } = request;
    
    if (!auth) {
      throw new HttpsError('unauthenticated', 'Must be authenticated');
    }
    
    // Islamic content validation
    const isValidIslamicContent = await validateIslamicContent(data.prayer_text);
    if (!isValidIslamicContent) {
      throw new HttpsError('invalid-argument', 'Content must comply with Islamic guidelines');
    }
    
    const memorial = {
      ...data,
      creator_uid: auth.uid,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
      expires_at: new Date(Date.now() + 40 * 24 * 60 * 60 * 1000), // 40 days
      prayer_count: 0,
      participant_count: 0
    };
    
    const docRef = await admin.firestore().collection('memorials').add(memorial);
    return { memorial_id: docRef.id };
  }
);

// Push Notification for Memorial Participation
export const sendMemorialNotification = onDocumentCreated(
  'memorial_prayers/{prayerId}',
  async (event) => {
    const prayerData = event.data?.data();
    if (!prayerData) return;
    
    const memorial = await admin.firestore()
      .collection('memorials')
      .doc(prayerData.memorial_id)
      .get();
    
    if (memorial.exists) {
      const memorialData = memorial.data();
      const creatorId = memorialData?.creator_uid;
      
      if (creatorId && creatorId !== prayerData.user_uid) {
        await admin.messaging().send({
          token: await getUserFCMToken(creatorId),
          notification: {
            title: 'Someone prayed for your memorial',
            body: `A prayer was offered for ${memorialData?.deceased_name}`
          },
          data: {
            memorial_id: prayerData.memorial_id,
            type: 'memorial_prayer'
          }
        });
      }
    }
  }
);

// Daily Analytics Aggregation
export const aggregateDailyStats = onSchedule(
  { schedule: '0 1 * * *', timeZone: 'UTC' },
  async (event) => {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    
    const stats = await calculateDailyStats(yesterday);
    
    await admin.firestore()
      .collection('app_analytics')
      .doc('daily_stats')
      .update({
        [yesterday.toISOString().split('T')[0]]: stats
      });
  }
);

// Islamic Content Validation Function
async function validateIslamicContent(text: string): Promise<boolean> {
  // Integration with Islamic content validation API
  // or local validation rules
  return true; // Placeholder
}
```

### 📱 **Cloud Messaging & Remote Config**
```typescript
// Push Notification Categories
interface NotificationCategories {
  memorial_created: {
    title: 'Memorial Created Successfully'
    body: 'Your memorial for {deceased_name} is now live'
    action: 'view_memorial'
  }
  
  prayer_received: {
    title: 'Prayer Received'
    body: '{user_name} prayed for {deceased_name}'
    action: 'view_prayers'
  }
  
  daily_reminder: {
    title: 'Daily Islamic Reminder'
    body: 'Time for your daily dhikr and remembrance'
    action: 'open_counter'
  }
  
  memorial_expiring: {
    title: 'Memorial Expiring Soon'
    body: 'Memorial for {deceased_name} expires in 3 days'
    action: 'extend_memorial'
  }
}

// Remote Config for Feature Flags
interface RemoteConfigFlags {
  // Feature toggles
  enable_social_sharing: boolean
  enable_memorial_extensions: boolean
  enable_audio_prayers: boolean
  
  // Islamic settings
  default_prayer_calculation_method: string
  memorial_duration_days: number
  max_memorial_per_user: number
  
  // UI configuration
  theme_colors: {
    primary: string
    secondary: string
    islamic_green: string
  }
  
  // Analytics
  analytics_sampling_rate: number
  crashlytics_collection_enabled: boolean
}
```

---

## 📱 React Native Application Architecture

### 🎯 **Project Structure**
```typescript
src/
├── app/                          # Expo Router v4 (File-based routing)
│   ├── (auth)/                   # Authentication flow
│   ├── (tabs)/                   # Main app tabs
│   ├── memorial/                 # Memorial creation/viewing
│   └── _layout.tsx              # Root layout
├── components/                   # Reusable UI components
│   ├── ui/                      # Basic UI components
│   ├── islamic/                 # Islamic-specific components
│   └── forms/                   # Form components
├── services/                    # Firebase services
│   ├── auth.ts                  # Authentication service
│   ├── firestore.ts            # Firestore operations
│   ├── storage.ts               # Cloud Storage
│   └── messaging.ts             # FCM integration
├── hooks/                       # Custom React hooks
├── utils/                       # Utility functions
├── types/                       # TypeScript definitions
├── constants/                   # App constants
└── assets/                      # Static assets
```

### 🔥 **Firebase Integration Setup**
```typescript
// firebase.config.ts
import { initializeApp, getApps } from 'firebase/app';
import { getAuth, initializeAuth, getReactNativePersistence } from 'firebase/auth';
import { getFirestore, connectFirestoreEmulator, enableNetwork } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';
import { getFunctions, connectFunctionsEmulator } from 'firebase/functions';
import { getAnalytics } from 'firebase/analytics';
import { getMessaging } from 'firebase/messaging';
import { getRemoteConfig } from 'firebase/remote-config';
import AsyncStorage from '@react-native-async-storage/async-storage';

const firebaseConfig = {
  apiKey: process.env.EXPO_PUBLIC_FIREBASE_API_KEY,
  authDomain: process.env.EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.EXPO_PUBLIC_FIREBASE_PROJECT_ID,
  storageBucket: process.env.EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.EXPO_PUBLIC_FIREBASE_APP_ID,
  measurementId: process.env.EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID
};

// Initialize Firebase
let app;
if (getApps().length === 0) {
  app = initializeApp(firebaseConfig);
} else {
  app = getApps()[0];
}

// Initialize services with persistence
export const auth = initializeAuth(app, {
  persistence: getReactNativePersistence(AsyncStorage)
});

export const firestore = getFirestore(app);
export const storage = getStorage(app);
export const functions = getFunctions(app);
export const analytics = getAnalytics(app);
export const messaging = getMessaging(app);
export const remoteConfig = getRemoteConfig(app);

// Enable offline persistence
enableNetwork(firestore);

export default app;
```

### 🎨 **Modern UI Components with Islamic Design**
```typescript
// components/islamic/PrayerCard.tsx
import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import Animated, { FadeInDown } from 'react-native-reanimated';

interface PrayerCardProps {
  prayer: PrayerData;
  onPress: () => void;
}

export const PrayerCard: React.FC<PrayerCardProps> = ({ prayer, onPress }) => {
  return (
    <Animated.View 
      entering={FadeInDown.delay(200)}
      style={styles.container}
    >
      <LinearGradient
        colors={['#1B4332', '#2D5D47', '#40826D']}
        style={styles.gradient}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
      >
        <View style={styles.header}>
          <Text style={styles.arabicText}>{prayer.arabicText}</Text>
        </View>
        
        <View style={styles.content}>
          <Text style={styles.transliteration}>{prayer.transliteration}</Text>
          <Text style={styles.translation}>{prayer.translation}</Text>
        </View>
        
        <View style={styles.footer}>
          <Text style={styles.prayerCount}>
            {prayer.totalPrayers} prayers offered
          </Text>
        </View>
      </LinearGradient>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  container: {
    borderRadius: 16,
    marginHorizontal: 16,
    marginVertical: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 8,
  },
  gradient: {
    borderRadius: 16,
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 16,
  },
  arabicText: {
    fontFamily: 'AmiriQuran',
    fontSize: 24,
    color: '#FFFFFF',
    textAlign: 'center',
    lineHeight: 36,
  },
  content: {
    marginBottom: 16,
  },
  transliteration: {
    fontFamily: 'Inter-Medium',
    fontSize: 16,
    color: '#E8F5E8',
    textAlign: 'center',
    marginBottom: 8,
  },
  translation: {
    fontFamily: 'Inter-Regular',
    fontSize: 14,
    color: '#D4E9D4',
    textAlign: 'center',
    lineHeight: 20,
  },
  footer: {
    alignItems: 'center',
  },
  prayerCount: {
    fontFamily: 'Inter-Regular',
    fontSize: 12,
    color: '#B8D4B8',
  },
});
```

---

## 🚀 Deployment & CI/CD Pipeline

### 📱 **EAS Build Configuration**
```json
// eas.json
{
  "cli": {
    "version": ">= 5.0.0"
  },
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal",
      "channel": "development",
      "env": {
        "EXPO_PUBLIC_ENV": "development"
      }
    },
    "staging": {
      "distribution": "internal",
      "channel": "staging",
      "env": {
        "EXPO_PUBLIC_ENV": "staging"
      }
    },
    "production": {
      "channel": "production",
      "env": {
        "EXPO_PUBLIC_ENV": "production"
      }
    }
  },
  "submit": {
    "production": {
      "ios": {
        "appleId": "your-apple-id@example.com",
        "ascAppId": "1234567890"
      },
      "android": {
        "serviceAccountKeyPath": "./google-service-account.json",
        "track": "internal"
      }
    }
  }
}
```

### 🔄 **GitHub Actions CI/CD**
```yaml
# .github/workflows/eas-build.yml
name: EAS Build and Deploy

on:
  push:
    branches: [main, staging]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout
      uses: actions/checkout@v4
      
    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '20'
        cache: 'npm'
        
    - name: Setup Expo
      uses: expo/expo-github-action@v8
      with:
        expo-version: latest
        token: ${{ secrets.EXPO_TOKEN }}
        
    - name: Install dependencies
      run: npm ci
      
    - name: Run tests
      run: npm run test
      
    - name: Type check
      run: npm run type-check
      
    - name: Lint
      run: npm run lint
      
    - name: Build for staging
      if: github.ref == 'refs/heads/staging'
      run: eas build --platform all --profile staging --non-interactive
      
    - name: Build for production
      if: github.ref == 'refs/heads/main'
      run: eas build --platform all --profile production --non-interactive
      
    - name: Deploy Firebase Functions
      if: github.ref == 'refs/heads/main'
      run: |
        npm install -g firebase-tools
        firebase deploy --only functions --token ${{ secrets.FIREBASE_TOKEN }}
```

---

## 🔒 Security & Performance

### 🛡️ **Security Implementation**
```typescript
// App Check integration for anti-abuse
import { initializeAppCheck, ReCaptchaV3Provider } from 'firebase/app-check';

initializeAppCheck(app, {
  provider: new ReCaptchaV3Provider('6LfHePAkAAAAAA...'), // reCAPTCHA v3 site key
  isTokenAutoRefreshEnabled: true
});

// Security rules for sensitive operations
const securityMiddleware = {
  // Rate limiting for memorial creation
  memorialCreationLimit: {
    maxPerDay: 5,
    maxPerHour: 2
  },
  
  // Content moderation
  contentFilters: {
    profanityCheck: true,
    islamicContentValidation: true,
    imageContentSafety: true
  },
  
  // Privacy protection
  dataEncryption: {
    personalData: true,
    prayerTexts: false, // Islamic texts should remain readable
    locationData: true
  }
};
```

### ⚡ **Performance Optimization**
```typescript
// Firestore optimization
const optimizationConfig = {
  // Offline persistence
  cacheSizeBytes: 100 * 1024 * 1024, // 100MB
  
  // Query optimization
  indexing: {
    memorials: ['privacy_level', 'created_at', 'expires_at'],
    prayers: ['user_uid', 'completed_at'],
    analytics: ['date', 'event_type']
  },
  
  // Image optimization
  imageProcessing: {
    maxWidth: 1200,
    maxHeight: 800,
    quality: 0.8,
    format: 'webp'
  },
  
  // Bundle optimization
  bundleSplitting: {
    enableHermes: true,
    enableProguard: true,
    enableR8: true
  }
};
```

---

This modern Firebase architecture provides a complete, scalable, and culturally-appropriate foundation for the global Islamic memorial prayer platform "Tahlil", leveraging the latest technologies and Firebase ecosystem capabilities.