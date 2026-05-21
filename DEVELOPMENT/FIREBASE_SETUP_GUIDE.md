# 🔥 Firebase Setup & Configuration Guide
## Tahlil Platform - Modern Architecture Implementation

### 📋 Setup Overview

This guide provides step-by-step instructions for setting up the complete Firebase ecosystem for the Tahlil memorial prayer platform. All configurations follow Islamic privacy principles and modern security standards.

---

## 🚀 Initial Firebase Project Setup

### 1. Create Firebase Projects

```bash
# Install Firebase CLI
npm install -g firebase-tools@latest

# Login to Firebase
firebase login

# Create development project
firebase projects:create tahlil-dev --display-name "Tahlil Development"

# Create staging project  
firebase projects:create tahlil-staging --display-name "Tahlil Staging"

# Create production project
firebase projects:create tahlil-production --display-name "Tahlil Production"
```

### 2. Initialize Firebase in Project

```bash
# Navigate to project directory
cd /path/to/tahlil-project

# Initialize Firebase
firebase init

# Select the following services:
# ✅ Firestore: Configure security rules and indexes
# ✅ Functions: Configure Firebase Functions
# ✅ Hosting: Configure files for Firebase Hosting
# ✅ Storage: Configure security rules for Cloud Storage
# ✅ Emulators: Set up local emulators
# ✅ Remote Config: Configure Remote Config
```

### 3. Project Configuration Files

#### firebase.json
```json
{
  "firestore": {
    "rules": "firestore.rules",
    "indexes": "firestore.indexes.json"
  },
  "functions": [
    {
      "source": "functions",
      "codebase": "default",
      "ignore": [
        "node_modules",
        ".git",
        "firebase-debug.log",
        "firebase-debug.*.log"
      ],
      "predeploy": ["npm --prefix \"$RESOURCE_DIR\" run lint"]
    }
  ],
  "hosting": {
    "public": "web/dist",
    "ignore": ["firebase.json", "**/.*", "**/node_modules/**"],
    "rewrites": [
      {
        "source": "**",
        "destination": "/index.html"
      }
    ]
  },
  "storage": {
    "rules": "storage.rules"
  },
  "emulators": {
    "auth": {
      "port": 9099
    },
    "functions": {
      "port": 5001
    },
    "firestore": {
      "port": 8080
    },
    "hosting": {
      "port": 5000
    },
    "storage": {
      "port": 9199
    },
    "ui": {
      "enabled": true,
      "port": 4000
    },
    "singleProjectMode": true
  },
  "remoteconfig": {
    "template": "remoteconfig.template.json"
  }
}
```

---

## 🔐 Authentication Configuration

### 1. Enable Authentication Providers

```bash
# Enable providers via Firebase Console or CLI
firebase auth:import providers.json --project tahlil-dev
```

#### providers.json
```json
{
  "providers": [
    {
      "providerId": "password",
      "enabled": true,
      "config": {
        "passwordPolicy": {
          "minLength": 8,
          "requireUppercase": true,
          "requireLowercase": true,
          "requireNumbers": true,
          "requireSymbols": true
        }
      }
    },
    {
      "providerId": "google.com",
      "enabled": true,
      "config": {
        "clientId": "YOUR_GOOGLE_CLIENT_ID"
      }
    },
    {
      "providerId": "apple.com",
      "enabled": true
    },
    {
      "providerId": "phone",
      "enabled": true,
      "config": {
        "testPhoneNumbers": {
          "+1555000000": "123456"
        }
      }
    },
    {
      "providerId": "anonymous",
      "enabled": true
    }
  ]
}
```

### 2. Configure Authentication Settings

```typescript
// auth.config.ts
export const authConfig = {
  development: {
    persistenceEnabled: true,
    tenantId: null,
    customDomain: null
  },
  production: {
    persistenceEnabled: true,
    tenantId: 'tahlil-prod',
    customDomain: 'auth.tahlil.app'
  },
  security: {
    emailVerificationRequired: true,
    phoneVerificationEnabled: true,
    multiFactorEnabled: false, // Enable for admin accounts
    sessionTimeout: '1h',
    refreshTokenTimeout: '7d'
  }
};
```

---

## 🗄️ Firestore Database Setup

### 1. Security Rules Configuration

#### firestore.rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isOwner(userId) {
      return request.auth.uid == userId;
    }
    
    function isMemorialParticipant(memorialId) {
      return request.auth.uid in get(/databases/$(database)/documents/memorials/$(memorialId)).data.participants;
    }
    
    // User documents
    match /users/{userId} {
      allow read, write: if isAuthenticated() && isOwner(userId);
    }
    
    // Memorial documents
    match /memorials/{memorialId} {
      allow read: if isAuthenticated() && (
        resource.data.privacyLevel == 'community' ||
        resource.data.createdBy == request.auth.uid ||
        (resource.data.privacyLevel == 'family' && 
         request.auth.uid in resource.data.participants)
      );
      
      allow create: if isAuthenticated() && 
        request.resource.data.createdBy == request.auth.uid &&
        request.resource.data.keys().hasAll(['fullName', 'privacyLevel', 'createdBy']);
        
      allow update: if isAuthenticated() && 
        resource.data.createdBy == request.auth.uid;
        
      allow delete: if isAuthenticated() && 
        resource.data.createdBy == request.auth.uid;
        
      // Prayer sub-collection
      match /prayers/{prayerId} {
        allow read: if isAuthenticated();
        allow write: if isAuthenticated() && 
          request.resource.data.userId == request.auth.uid;
      }
    }
    
    // Community documents
    match /communities/{communityId} {
      allow read: if isAuthenticated();
      allow create: if isAuthenticated() && 
        request.resource.data.createdBy == request.auth.uid;
      allow update: if isAuthenticated() && 
        request.auth.uid in resource.data.admins;
      allow delete: if isAuthenticated() && 
        resource.data.createdBy == request.auth.uid;
    }
    
    // Analytics (read-only for users, write-only for functions)
    match /analytics/{document} {
      allow read: if isAuthenticated();
      allow write: if false;
    }
    
    // Security logs (admin only)
    match /security_logs/{logId} {
      allow read, write: if false; // Function access only
    }
  }
}
```

### 2. Database Indexes

#### firestore.indexes.json
```json
{
  "indexes": [
    {
      "collectionGroup": "memorials",
      "queryScope": "COLLECTION",
      "fields": [
        {
          "fieldPath": "createdBy",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "createdAt",
          "order": "DESCENDING"
        }
      ]
    },
    {
      "collectionGroup": "memorials",
      "queryScope": "COLLECTION",
      "fields": [
        {
          "fieldPath": "privacyLevel",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "updatedAt",
          "order": "DESCENDING"
        }
      ]
    },
    {
      "collectionGroup": "prayers",
      "queryScope": "COLLECTION_GROUP",
      "fields": [
        {
          "fieldPath": "userId",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "createdAt",
          "order": "DESCENDING"
        }
      ]
    },
    {
      "collectionGroup": "prayers",
      "queryScope": "COLLECTION_GROUP",
      "fields": [
        {
          "fieldPath": "prayerType",
          "order": "ASCENDING"
        },
        {
          "fieldPath": "completedAt",
          "order": "DESCENDING"
        }
      ]
    }
  ],
  "fieldOverrides": []
}
```

### 3. Initial Data Seed

```typescript
// scripts/seedData.ts
import { initializeApp } from 'firebase/app';
import { getFirestore, collection, addDoc, serverTimestamp } from 'firebase/firestore';

export async function seedInitialData() {
  const firestore = getFirestore();
  
  // Seed Islamic prayer texts
  const prayerTexts = [
    {
      id: 'tahlil',
      arabicText: 'لَا إِلَٰهَ إِلَّا ٱللَّٰهُ',
      transliteration: 'La ilaha illa Allah',
      translations: {
        en: 'There is no god but Allah',
        id: 'Tiada tuhan selain Allah',
        ar: 'لَا إِلَٰهَ إِلَّا ٱللَّٰهُ',
        ur: 'اللہ کے سوا کوئی معبود نہیں'
      }
    },
    {
      id: 'istighfar',
      arabicText: 'أَسْتَغْفِرُ ٱللَّٰهَ ٱلْعَظِيمَ',
      transliteration: 'Astaghfirullaha al-azeem',
      translations: {
        en: 'I seek forgiveness from Allah, the Magnificent',
        id: 'Aku memohon ampun kepada Allah Yang Maha Agung',
        ar: 'أَسْتَغْفِرُ ٱللَّٰهَ ٱلْعَظِيمَ',
        ur: 'میں اللہ تعالیٰ سے بخشش مانگتا ہوں'
      }
    }
  ];
  
  // Add prayer texts
  for (const prayer of prayerTexts) {
    await addDoc(collection(firestore, 'prayer_texts'), {
      ...prayer,
      createdAt: serverTimestamp(),
      verifiedBy: 'admin',
      scholarApproved: true
    });
  }
  
  // Seed cultural regions
  const culturalRegions = [
    {
      code: 'middle_east',
      name: 'Middle East',
      countries: ['SA', 'AE', 'EG', 'JO', 'LB', 'SY'],
      languages: ['ar', 'en'],
      traditions: ['hanafi', 'shafii', 'maliki', 'hanbali']
    },
    {
      code: 'south_asia',
      name: 'South Asia',
      countries: ['PK', 'IN', 'BD', 'AF'],
      languages: ['ur', 'en', 'ar'],
      traditions: ['hanafi', 'shafii', 'deobandi', 'barelvi']
    },
    {
      code: 'southeast_asia',
      name: 'Southeast Asia', 
      countries: ['ID', 'MY', 'SG', 'BN', 'TH', 'PH'],
      languages: ['id', 'ms', 'en', 'ar'],
      traditions: ['shafii', 'hanafi']
    }
  ];
  
  // Add cultural regions
  for (const region of culturalRegions) {
    await addDoc(collection(firestore, 'cultural_regions'), {
      ...region,
      createdAt: serverTimestamp()
    });
  }
  
  console.log('Initial data seeded successfully');
}
```

---

## 📁 Cloud Storage Configuration

### 1. Storage Security Rules

#### storage.rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isOwner(userId) {
      return request.auth.uid == userId;
    }
    
    function isValidImageFile() {
      return request.resource.contentType.matches('image/.*') &&
             request.resource.size < 10 * 1024 * 1024; // 10MB limit
    }
    
    // User profile photos
    match /users/{userId}/profile/{filename} {
      allow read: if isAuthenticated();
      allow write: if isAuthenticated() && 
                   isOwner(userId) && 
                   isValidImageFile();
    }
    
    // Memorial photos with privacy controls
    match /memorials/{memorialId}/{filename} {
      allow read: if isAuthenticated() && (
        resource.metadata.privacy == 'community' ||
        resource.metadata.createdBy == request.auth.uid ||
        request.auth.uid in resource.metadata.participants.split(',')
      );
      
      allow write: if isAuthenticated() && isValidImageFile();
      allow delete: if isAuthenticated() && 
                    resource.metadata.createdBy == request.auth.uid;
    }
    
    // Public Islamic resources (read-only)
    match /public/islamic-resources/{allPaths=**} {
      allow read: if isAuthenticated();
      allow write: if false; // Admin only through functions
    }
    
    // Temporary uploads (auto-expire after 24 hours)
    match /temp/{userId}/{filename} {
      allow read, write: if isAuthenticated() && 
                         isOwner(userId) && 
                         isValidImageFile();
    }
  }
}
```

### 2. Storage Buckets Setup

```typescript
// storage.config.ts
export const storageConfig = {
  buckets: {
    main: 'tahlil-production.appspot.com',
    cdn: 'cdn.tahlil.app',
    backup: 'tahlil-backup.appspot.com'
  },
  regions: {
    primary: 'asia-southeast1',
    secondary: 'us-central1'
  },
  policies: {
    lifecycle: {
      rules: [
        {
          condition: {
            age: 90,
            matchesStorageClass: 'STANDARD'
          },
          action: {
            type: 'SetStorageClass',
            storageClass: 'COLDLINE'
          }
        },
        {
          condition: {
            age: 365,
            matchesStorageClass: 'COLDLINE'
          },
          action: {
            type: 'SetStorageClass', 
            storageClass: 'ARCHIVE'
          }
        }
      ]
    },
    cors: [
      {
        origin: ['https://tahlil.app', 'https://admin.tahlil.app'],
        method: ['GET', 'POST', 'PUT', 'DELETE'],
        responseHeader: ['Content-Type', 'x-goog-resumable'],
        maxAgeSeconds: 3600
      }
    ]
  }
};
```

---

## ☁️ Cloud Functions Setup

### 1. Functions Configuration

#### functions/package.json
```json
{
  "name": "tahlil-functions",
  "description": "Cloud Functions for Tahlil Platform",
  "scripts": {
    "lint": "eslint --ext .js,.ts .",
    "build": "tsc",
    "build:watch": "tsc --watch",
    "serve": "npm run build && firebase emulators:start --only functions",
    "shell": "npm run build && firebase functions:shell",
    "start": "npm run shell",
    "deploy": "firebase deploy --only functions",
    "logs": "firebase functions:log"
  },
  "engines": {
    "node": "20"
  },
  "main": "lib/index.js",
  "dependencies": {
    "firebase-admin": "^12.0.0",
    "firebase-functions": "^5.0.0",
    "@google-cloud/storage": "^7.7.0",
    "sharp": "^0.33.0",
    "nodemailer": "^6.9.0",
    "cors": "^2.8.5",
    "express": "^4.18.2",
    "helmet": "^7.1.0",
    "zod": "^3.22.0"
  },
  "devDependencies": {
    "@types/cors": "^2.8.17",
    "@types/express": "^4.17.21",
    "@types/node": "^20.10.0",
    "@types/nodemailer": "^6.4.14",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "eslint": "^8.0.0",
    "eslint-config-google": "^0.14.0",
    "firebase-functions-test": "^3.1.0",
    "typescript": "^5.3.0"
  },
  "private": true
}
```

### 2. Core Functions Implementation

#### functions/src/index.ts
```typescript
import { initializeApp } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { onDocumentCreated, onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { onCall, HttpsError } from 'firebase-functions/v2/https';
import { onSchedule } from 'firebase-functions/v2/scheduler';
import { defineString, defineInt } from 'firebase-functions/params';

// Initialize Firebase Admin
initializeApp();
const firestore = getFirestore();

// Environment configuration
const REGION = defineString('REGION', 'asia-southeast1');
const MAX_INSTANCES = defineInt('MAX_INSTANCES', 100);

// Memorial creation handler
export const onMemorialCreated = onDocumentCreated(
  {
    document: 'memorials/{memorialId}',
    region: REGION.value(),
    maxInstances: MAX_INSTANCES.value()
  },
  async (event) => {
    const memorial = event.data?.data();
    const memorialId = event.params.memorialId;
    
    if (!memorial) return;
    
    try {
      // Send creation notifications
      await sendMemorialCreationNotifications(memorial, memorialId);
      
      // Update community statistics
      await updateCommunityStatistics(memorial);
      
      // Initialize memorial analytics
      await initializeMemorialAnalytics(memorialId);
      
      console.log(`Memorial ${memorialId} processed successfully`);
    } catch (error) {
      console.error('Error processing memorial creation:', error);
      throw new Error('Failed to process memorial creation');
    }
  }
);

// Prayer completion handler
export const onPrayerCompleted = onDocumentCreated(
  {
    document: 'memorials/{memorialId}/prayers/{prayerId}',
    region: REGION.value()
  },
  async (event) => {
    const prayer = event.data?.data();
    const { memorialId, prayerId } = event.params;
    
    if (!prayer || !prayer.completedAt) return;
    
    try {
      // Update memorial prayer count
      await firestore.doc(`memorials/${memorialId}`).update({
        totalPrayers: admin.firestore.FieldValue.increment(prayer.count),
        lastPrayerAt: prayer.completedAt
      });
      
      // Update daily analytics
      await updateDailyPrayerAnalytics(prayer);
      
      // Check for milestones and send notifications
      await checkPrayerMilestones(memorialId, prayer);
      
      console.log(`Prayer ${prayerId} processed successfully`);
    } catch (error) {
      console.error('Error processing prayer completion:', error);
    }
  }
);

// Daily analytics aggregation
export const aggregateDailyAnalytics = onSchedule(
  {
    schedule: 'every day 02:00',
    timeZone: 'Asia/Jakarta',
    region: REGION.value()
  },
  async (event) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayStr = today.toISOString().split('T')[0];
    
    try {
      // Aggregate prayer statistics
      const prayerStats = await aggregatePrayerStatistics(today);
      
      // Aggregate user activity
      const userStats = await aggregateUserActivity(today);
      
      // Aggregate memorial statistics
      const memorialStats = await aggregateMemorialStatistics(today);
      
      // Save to analytics collection
      await firestore.doc(`analytics/${todayStr}`).set({
        date: todayStr,
        global: {
          totalPrayers: prayerStats.total,
          activeUsers: userStats.active,
          memorialsCreated: memorialStats.created,
          sessionsCompleted: prayerStats.completed
        },
        byCountry: prayerStats.byCountry,
        byPrayerType: prayerStats.byType,
        updatedAt: admin.firestore.FieldValue.serverTimestamp()
      });
      
      console.log(`Daily analytics aggregated for ${todayStr}`);
    } catch (error) {
      console.error('Error aggregating daily analytics:', error);
    }
  }
);

// Islamic calendar service
export const getIslamicDate = onCall(
  {
    region: REGION.value(),
    cors: true
  },
  async (request) => {
    const { date, location } = request.data;
    
    try {
      const islamicDate = await calculateIslamicDate(date, location);
      const prayerTimes = await calculatePrayerTimes(date, location);
      
      return {
        islamicDate,
        prayerTimes,
        calculatedAt: new Date().toISOString()
      };
    } catch (error) {
      throw new HttpsError('internal', 'Failed to calculate Islamic date');
    }
  }
);

// Memorial sharing service
export const shareMemorial = onCall(
  {
    region: REGION.value(),
    cors: true
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError('unauthenticated', 'Must be authenticated');
    }
    
    const { memorialId, recipientEmails, message } = request.data;
    
    try {
      // Verify memorial access
      const memorial = await firestore.doc(`memorials/${memorialId}`).get();
      if (!memorial.exists) {
        throw new HttpsError('not-found', 'Memorial not found');
      }
      
      const memorialData = memorial.data();
      if (memorialData?.createdBy !== request.auth.uid) {
        throw new HttpsError('permission-denied', 'Not authorized to share this memorial');
      }
      
      // Generate secure share links
      const shareLinks = await generateShareLinks(memorialId, recipientEmails);
      
      // Send invitation emails
      await sendMemorialInvitations(memorialData, shareLinks, message);
      
      return {
        success: true,
        shareCount: recipientEmails.length,
        sharedAt: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error sharing memorial:', error);
      throw new HttpsError('internal', 'Failed to share memorial');
    }
  }
);

// Helper functions would be implemented here...
```

---

## 📱 Remote Config Setup

### 1. Remote Config Template

#### remoteconfig.template.json
```json
{
  "conditions": [
    {
      "name": "iOS_users",
      "expression": "device.os == 'ios'"
    },
    {
      "name": "Android_users", 
      "expression": "device.os == 'android'"
    },
    {
      "name": "Beta_users",
      "expression": "user.inRandomPercentile(10)"
    }
  ],
  "parameters": {
    "feature_flags": {
      "defaultValue": {
        "stringValue": "{\"advanced_analytics\": false, \"social_sharing\": true, \"offline_mode\": true}"
      },
      "conditionalValues": {
        "Beta_users": {
          "stringValue": "{\"advanced_analytics\": true, \"social_sharing\": true, \"offline_mode\": true}"
        }
      },
      "description": "Feature flags for gradual rollout"
    },
    "prayer_reminder_times": {
      "defaultValue": {
        "stringValue": "[\"fajr\", \"maghrib\", \"isha\"]"
      },
      "description": "Default prayer reminder times"
    },
    "max_memorial_photos": {
      "defaultValue": {
        "stringValue": "5"
      },
      "conditionalValues": {
        "iOS_users": {
          "stringValue": "10"
        }
      },
      "description": "Maximum number of photos per memorial"
    },
    "cultural_themes": {
      "defaultValue": {
        "stringValue": "{\"middle_east\": {\"primary\": \"#1B5E20\", \"accent\": \"#FDD835\"}, \"south_asia\": {\"primary\": \"#2E7D32\", \"accent\": \"#FF8F00\"}, \"southeast_asia\": {\"primary\": \"#388E3C\", \"accent\": \"#FFC107\"}}"
      },
      "description": "Cultural color themes"
    },
    "api_endpoints": {
      "defaultValue": {
        "stringValue": "{\"prayer_times\": \"https://api.aladhan.com/v1/timings\", \"islamic_calendar\": \"https://api.aladhan.com/v1/islamicDateToday\"}"
      },
      "description": "External API endpoints"
    }
  },
  "parameterGroups": {
    "UI_Configuration": {
      "parameters": {
        "cultural_themes": {},
        "max_memorial_photos": {}
      }
    },
    "Feature_Management": {
      "parameters": {
        "feature_flags": {},
        "prayer_reminder_times": {}
      }
    }
  },
  "version": {
    "versionNumber": "1",
    "updateTime": "2024-01-01T00:00:00.000Z",
    "updateUser": {
      "email": "admin@tahlil.app"
    },
    "description": "Initial Remote Config setup for Tahlil platform"
  }
}
```

---

## 🔔 Cloud Messaging Setup

### 1. FCM Configuration

```typescript
// messaging.config.ts
export const messagingConfig = {
  webConfig: {
    apiKey: "your-api-key",
    authDomain: "tahlil-production.firebaseapp.com",
    projectId: "tahlil-production",
    storageBucket: "tahlil-production.appspot.com",
    messagingSenderId: "123456789",
    appId: "1:123456789:web:abcdef123456",
    vapidKey: "your-vapid-key"
  },
  
  notificationTypes: {
    memorial_invitation: {
      title: 'Memorial Invitation',
      icon: '/icons/memorial.png',
      badge: '/icons/badge.png',
      sound: 'gentle_chime.mp3'
    },
    prayer_reminder: {
      title: 'Prayer Time Reminder',
      icon: '/icons/prayer.png',
      badge: '/icons/badge.png',
      sound: 'prayer_call.mp3'
    },
    milestone_achieved: {
      title: 'Prayer Milestone Reached',
      icon: '/icons/achievement.png',
      badge: '/icons/badge.png',
      sound: 'celebration.mp3'
    }
  },
  
  topics: [
    'global_announcements',
    'regional_middle_east',
    'regional_south_asia', 
    'regional_southeast_asia',
    'prayer_reminders',
    'community_updates'
  ]
};

// Notification service implementation
export class NotificationService {
  private messaging = getMessaging();
  
  async initializeMessaging() {
    // Request permission
    const permission = await Notification.requestPermission();
    
    if (permission === 'granted') {
      // Get FCM token
      const token = await getToken(this.messaging, {
        vapidKey: messagingConfig.webConfig.vapidKey
      });
      
      // Save token to user profile
      await this.saveTokenToProfile(token);
      
      // Handle foreground messages
      onMessage(this.messaging, (payload) => {
        this.handleForegroundMessage(payload);
      });
    }
  }
  
  async saveTokenToProfile(token: string) {
    const user = auth.currentUser;
    if (user) {
      await updateDoc(doc(firestore, 'users', user.uid), {
        fcmToken: token,
        lastTokenUpdate: serverTimestamp()
      });
    }
  }
  
  async subscribeToTopic(topic: string) {
    // This would typically be done via Cloud Function
    await httpsCallable(functions, 'subscribeToTopic')({ topic });
  }
}
```

---

## 📊 Analytics & Performance Setup

### 1. Firebase Analytics Configuration

```typescript
// analytics.config.ts
import { getAnalytics, logEvent, setUserProperties } from 'firebase/analytics';

export const analyticsConfig = {
  measurementId: 'G-XXXXXXXXXX',
  cookieFlags: 'SameSite=None;Secure',
  customParameters: {
    custom_map: {
      memorial_type: 'memorial_category',
      prayer_count: 'prayers_completed',
      cultural_region: 'user_region'
    }
  }
};

export class AnalyticsService {
  private analytics = getAnalytics();
  
  // Islamic-compliant analytics (privacy-first)
  async trackEvent(eventName: string, parameters: any) {
    // Filter out sensitive information
    const sanitizedParams = this.sanitizeParameters(parameters);
    
    logEvent(this.analytics, eventName, sanitizedParams);
  }
  
  async setUserProperties(properties: any) {
    // Only set non-sensitive user properties
    const allowedProperties = {
      cultural_region: properties.culturalRegion,
      preferred_language: properties.language,
      user_type: properties.userType
    };
    
    setUserProperties(this.analytics, allowedProperties);
  }
  
  // Track memorial-specific events
  async trackMemorialCreated(memorial: any) {
    await this.trackEvent('memorial_created', {
      memorial_type: memorial.relationship,
      privacy_level: memorial.privacyLevel,
      has_photo: !!memorial.photoURL,
      cultural_region: memorial.culturalRegion
    });
  }
  
  async trackPrayerCompleted(prayer: any) {
    await this.trackEvent('prayer_completed', {
      prayer_type: prayer.prayerType,
      prayer_count: prayer.count,
      session_duration: prayer.sessionDuration,
      completion_rate: (prayer.count / prayer.targetCount) * 100
    });
  }
  
  private sanitizeParameters(params: any) {
    // Remove any PII or sensitive information
    const { email, name, phone, ...sanitized } = params;
    return sanitized;
  }
}
```

### 2. Performance Monitoring

```typescript
// performance.config.ts
import { getPerformance, trace } from 'firebase/performance';

export class PerformanceService {
  private performance = getPerformance();
  
  async trackPageLoad(pageName: string) {
    const pageTrace = trace(this.performance, `page_load_${pageName}`);
    pageTrace.start();
    
    return () => pageTrace.stop();
  }
  
  async trackMemorialLoad(memorialId: string) {
    const memorialTrace = trace(this.performance, 'memorial_load');
    memorialTrace.putAttribute('memorial_id', memorialId);
    memorialTrace.start();
    
    return () => memorialTrace.stop();
  }
  
  async trackPrayerSession() {
    const prayerTrace = trace(this.performance, 'prayer_session');
    prayerTrace.start();
    
    return () => prayerTrace.stop();
  }
}
```

---

## 🚀 Deployment Configuration

### 1. Environment-Specific Deployment

#### .firebaserc
```json
{
  "projects": {
    "dev": "tahlil-dev",
    "staging": "tahlil-staging", 
    "prod": "tahlil-production"
  },
  "targets": {
    "tahlil-production": {
      "hosting": {
        "app": ["tahlil-app"],
        "admin": ["tahlil-admin"]
      }
    }
  },
  "etags": {},
  "dataconnectEmulatorConfig": {}
}
```

### 2. Deployment Scripts

#### package.json scripts
```json
{
  "scripts": {
    "dev:emulators": "firebase emulators:start",
    "dev:functions": "npm run build --prefix functions && firebase emulators:start --only functions",
    "build": "npm run build:functions && npm run build:web",
    "build:functions": "npm run build --prefix functions",
    "build:web": "npm run build --prefix web",
    "deploy:dev": "firebase deploy --project dev",
    "deploy:staging": "firebase deploy --project staging",
    "deploy:prod": "firebase deploy --project prod",
    "deploy:functions": "firebase deploy --only functions",
    "deploy:rules": "firebase deploy --only firestore:rules,storage:rules",
    "test:rules": "firebase emulators:exec --only firestore 'npm test' --project dev"
  }
}
```

### 3. CI/CD Pipeline

#### .github/workflows/firebase-deploy.yml
```yaml
name: Firebase Deploy

on:
  push:
    branches: [main, staging, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run tests
        run: npm test
        
      - name: Test Firebase Rules
        run: |
          npm install -g firebase-tools
          firebase emulators:exec --only firestore 'npm run test:rules'

  deploy-dev:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
        
      - name: Build project
        run: npm run build
        
      - name: Deploy to Development
        run: |
          npm install -g firebase-tools
          firebase deploy --project dev --token ${{ secrets.FIREBASE_TOKEN }}

  deploy-staging:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/staging'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Deploy to Staging
        run: |
          npm install -g firebase-tools
          firebase deploy --project staging --token ${{ secrets.FIREBASE_TOKEN }}

  deploy-prod:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment: production
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Deploy to Production
        run: |
          npm install -g firebase-tools
          firebase deploy --project prod --token ${{ secrets.FIREBASE_TOKEN }}
```

---

## ✅ Testing & Validation

### 1. Run Emulator Suite

```bash
# Start all emulators
firebase emulators:start

# Test security rules
firebase emulators:exec --only firestore 'npm run test:rules'

# Test functions locally
npm run serve --prefix functions

# Test with real data
npm run seed:emulators
```

### 2. Validation Checklist

```bash
# ✅ Authentication Configuration
□ Email/password authentication enabled
□ Google Sign-In configured with valid OAuth credentials
□ Phone authentication set up with test numbers
□ Custom claims for admin users configured
□ Password policy enforced (8+ chars, mixed case, symbols)

# ✅ Firestore Security Rules
□ User documents protected by UID matching
□ Memorial privacy levels enforced correctly
□ Prayer data accessible only to authenticated users
□ Community access controlled by membership
□ Admin-only collections properly secured

# ✅ Storage Security Rules
□ Profile photos protected by user ownership
□ Memorial photos respect privacy settings
□ File type validation enforced (images only)
□ File size limits applied (10MB max)
□ Public resources read-only for users

# ✅ Cloud Functions
□ Memorial creation triggers notifications
□ Prayer completion updates analytics
□ Daily aggregation runs on schedule
□ Error handling and logging implemented
□ Performance monitoring enabled

# ✅ Remote Config
□ Feature flags configured for gradual rollout
□ Cultural themes defined for all regions
□ API endpoints centrally managed
□ A/B testing parameters set up
□ Default values provided for all parameters

# ✅ Analytics & Monitoring
□ Privacy-compliant event tracking
□ Custom dimensions for Islamic features
□ Performance monitoring for key user flows
□ Crash reporting enabled
□ Real-time monitoring dashboards set up
```

---

This comprehensive Firebase setup guide provides everything needed to implement the modern serverless architecture for the Tahlil platform, ensuring scalability, security, and Islamic compliance from day one.