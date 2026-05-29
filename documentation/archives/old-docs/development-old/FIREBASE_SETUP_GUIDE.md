# 🔥 Firebase Setup & Configuration Guide
## Complete Firebase Ecosystem Implementation for Tahlil Platform

### 📋 Overview

This comprehensive guide provides step-by-step instructions for setting up the complete Firebase ecosystem with the latest 2026 technologies for the Tahlil global memorial prayer platform.

---

## 🚀 Prerequisites & Environment Setup

### 📱 **Development Environment Requirements**
```bash
# Required Software (2026 Latest Versions)
Node.js: v20.12.0+
npm: v10.5.0+
Firebase CLI: v13.7.0+
React Native CLI: v13.6.0+
Expo CLI: v6.3.0+
TypeScript: v5.3.0+
Git: v2.44.0+

# Platform-specific Requirements
macOS: Xcode 15.3+ (iOS development)
Windows/macOS/Linux: Android Studio 2023.2.1+ (Android development)
```

### 🔧 **Initial Setup Commands**
```bash
# Install Firebase CLI globally
npm install -g firebase-tools@latest

# Install Expo CLI globally  
npm install -g @expo/cli@latest

# Install React Native CLI
npm install -g @react-native-community/cli@latest

# Verify installations
firebase --version    # Should show v13.7.0+
expo --version        # Should show v6.3.0+
npx react-native --version  # Should show v13.6.0+
```

---

## 🔥 Firebase Project Setup

### 1️⃣ **Create Firebase Project**
```bash
# Login to Firebase
firebase login

# Initialize new Firebase project
firebase projects:create tahlil-global-platform

# Set project as default
firebase use tahlil-global-platform
```

### 2️⃣ **Firebase Project Configuration**
```bash
# Initialize Firebase in your project directory
cd /Users/projek/Documents/Project/tasbeeh
firebase init

# Select the following services:
☑️ Firestore: Configure security rules and indexes
☑️ Functions: Configure Cloud Functions
☑️ Hosting: Configure files for Firebase Hosting
☑️ Storage: Configure security rules for Cloud Storage
☑️ Emulators: Set up local emulators
☑️ Remote Config: Configure Remote Config
```

### 3️⃣ **Firebase Configuration Files Setup**
```typescript
// firebase.config.ts - Main Firebase configuration
import { initializeApp, getApps } from 'firebase/app';
import { getAuth, connectAuthEmulator } from 'firebase/auth';
import { getFirestore, connectFirestoreEmulator } from 'firebase/firestore';
import { getStorage, connectStorageEmulator } from 'firebase/storage';
import { getFunctions, connectFunctionsEmulator } from 'firebase/functions';
import { getAnalytics } from 'firebase/analytics';
import { getMessaging, getToken } from 'firebase/messaging';
import { getRemoteConfig } from 'firebase/remote-config';
import { getPerformance } from 'firebase/performance';
import { initializeAppCheck, ReCaptchaV3Provider } from 'firebase/app-check';

// Environment-based configuration
const firebaseConfig = {
  apiKey: process.env.EXPO_PUBLIC_FIREBASE_API_KEY,
  authDomain: process.env.EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.EXPO_PUBLIC_FIREBASE_PROJECT_ID,
  storageBucket: process.env.EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.EXPO_PUBLIC_FIREBASE_APP_ID,
  measurementId: process.env.EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID
};

// Initialize Firebase (prevent multiple initialization)
let app;
if (getApps().length === 0) {
  app = initializeApp(firebaseConfig);
} else {
  app = getApps()[0];
}

// Initialize Firebase services
export const auth = getAuth(app);
export const firestore = getFirestore(app);
export const storage = getStorage(app);
export const functions = getFunctions(app);
export const analytics = getAnalytics(app);
export const messaging = getMessaging(app);
export const remoteConfig = getRemoteConfig(app);
export const performance = getPerformance(app);

// Initialize App Check for production
if (process.env.EXPO_PUBLIC_ENV === 'production') {
  initializeAppCheck(app, {
    provider: new ReCaptchaV3Provider(process.env.EXPO_PUBLIC_RECAPTCHA_SITE_KEY!),
    isTokenAutoRefreshEnabled: true
  });
}

// Connect to emulators in development
if (__DEV__ && process.env.EXPO_PUBLIC_USE_EMULATORS === 'true') {
  try {
    connectAuthEmulator(auth, 'http://localhost:9099');
    connectFirestoreEmulator(firestore, 'localhost', 8080);
    connectStorageEmulator(storage, 'localhost', 9199);
    connectFunctionsEmulator(functions, 'localhost', 5001);
  } catch (error) {
    console.warn('Firebase emulators already connected');
  }
}

export default app;
```

---

## 🗄️ Cloud Firestore Setup

### 1️⃣ **Firestore Security Rules**
```javascript
// firestore.rules
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    // User document security
    match /users/{userId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == userId;
      
      // Allow reading public profile info for community features
      allow read: if request.auth != null && 
        resource.data.privacy.profileVisibility == 'community';
    }
    
    // Memorial document security with privacy levels
    match /memorials/{memorialId} {
      // Read access based on privacy level
      allow read: if request.auth != null && (
        resource.data.privacyLevel == 'community' ||
        resource.data.createdBy == request.auth.uid ||
        (resource.data.privacyLevel == 'family' && 
         request.auth.uid in resource.data.familyMembers)
      );
      
      // Write access only for creator
      allow write: if request.auth != null && 
        resource.data.createdBy == request.auth.uid;
        
      // Create access for authenticated users
      allow create: if request.auth != null && 
        request.resource.data.createdBy == request.auth.uid &&
        validateMemorialData(request.resource.data);
    }
    
    // Memorial prayers sub-collection
    match /memorials/{memorialId}/prayers/{prayerId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && 
        request.resource.data.userId == request.auth.uid &&
        validatePrayerData(request.resource.data);
      allow update: if request.auth != null && 
        resource.data.userId == request.auth.uid;
    }
    
    // Community collections
    match /communities/{communityId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        request.auth.uid in resource.data.admins;
      allow create: if request.auth != null;
    }
    
    // Islamic content (read-only for users, admin write)
    match /islamic_content/{document=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        request.auth.token.admin == true;
    }
    
    // Analytics (Cloud Functions only)
    match /analytics/{document=**} {
      allow read: if request.auth != null && 
        request.auth.token.admin == true;
      allow write: if false; // Only Cloud Functions can write
    }
    
    // Validation functions
    function validateMemorialData(data) {
      return data.keys().hasAll(['fullName', 'createdBy', 'privacyLevel']) &&
             data.fullName.size() > 0 &&
             data.privacyLevel in ['private', 'family', 'community'];
    }
    
    function validatePrayerData(data) {
      return data.keys().hasAll(['userId', 'prayerType', 'count']) &&
             data.prayerType in ['tahlil', 'yasin', 'fatihah', 'dua'] &&
             data.count is int && data.count > 0;
    }
  }
}
```

### 2️⃣ **Firestore Indexes Configuration**
```json
// firestore.indexes.json
{
  "indexes": [
    {
      "collectionGroup": "memorials",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "privacyLevel", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "memorials",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "createdBy", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "prayers",
      "queryScope": "COLLECTION_GROUP",
      "fields": [
        { "fieldPath": "userId", "order": "ASCENDING" },
        { "fieldPath": "completedAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "prayers",
      "queryScope": "COLLECTION_GROUP",
      "fields": [
        { "fieldPath": "prayerType", "order": "ASCENDING" },
        { "fieldPath": "completedAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "communities",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "region", "order": "ASCENDING" },
        { "fieldPath": "memberCount", "order": "DESCENDING" }
      ]
    }
  ],
  "fieldOverrides": []
}
```

---

## 🔐 Firebase Authentication Setup

### 1️⃣ **Authentication Configuration**
```typescript
// services/auth.service.ts
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signInWithPopup,
  GoogleAuthProvider,
  OAuthProvider,
  PhoneAuthProvider,
  RecaptchaVerifier,
  signInWithPhoneNumber,
  sendEmailVerification,
  updateProfile,
  User
} from 'firebase/auth';
import { doc, setDoc, getDoc } from 'firebase/firestore';
import { auth, firestore } from '../firebase.config';

export interface UserProfile {
  uid: string;
  email?: string;
  displayName: string;
  photoURL?: string;
  phoneNumber?: string;
  preferences: {
    language: 'ar' | 'en' | 'id' | 'ur' | 'tr' | 'ms' | 'fa';
    culturalRegion: string;
    prayerTraditions: string[];
    theme: 'light' | 'dark' | 'auto';
    islamicSchool: 'sunni' | 'shia' | 'other';
  };
  privacy: {
    profileVisibility: 'private' | 'family' | 'community';
    analyticsOptOut: boolean;
    dataSharing: boolean;
  };
  createdAt: Date;
  lastActiveAt: Date;
}

export class AuthService {
  // Email/Password Registration with Islamic Profile
  async registerWithEmail(
    email: string, 
    password: string, 
    profile: Partial<UserProfile>
  ): Promise<User> {
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      const user = userCredential.user;
      
      // Update user profile
      await updateProfile(user, {
        displayName: profile.displayName
      });
      
      // Create user document in Firestore
      const userProfile: UserProfile = {
        uid: user.uid,
        email: user.email || '',
        displayName: profile.displayName || '',
        preferences: {
          language: 'en',
          culturalRegion: 'global',
          prayerTraditions: ['sunni'],
          theme: 'light',
          islamicSchool: 'sunni',
          ...profile.preferences
        },
        privacy: {
          profileVisibility: 'private',
          analyticsOptOut: false,
          dataSharing: false,
          ...profile.privacy
        },
        createdAt: new Date(),
        lastActiveAt: new Date()
      };
      
      await setDoc(doc(firestore, 'users', user.uid), userProfile);
      
      // Send email verification
      await sendEmailVerification(user);
      
      return user;
    } catch (error) {
      throw new Error(`Registration failed: ${error}`);
    }
  }
  
  // Google Sign-In
  async signInWithGoogle(): Promise<User> {
    try {
      const provider = new GoogleAuthProvider();
      provider.addScope('profile');
      provider.addScope('email');
      
      const result = await signInWithPopup(auth, provider);
      await this.createOrUpdateUserProfile(result.user);
      
      return result.user;
    } catch (error) {
      throw new Error(`Google sign-in failed: ${error}`);
    }
  }
  
  // Apple Sign-In (iOS)
  async signInWithApple(): Promise<User> {
    try {
      const provider = new OAuthProvider('apple.com');
      provider.addScope('email');
      provider.addScope('name');
      
      const result = await signInWithPopup(auth, provider);
      await this.createOrUpdateUserProfile(result.user);
      
      return result.user;
    } catch (error) {
      throw new Error(`Apple sign-in failed: ${error}`);
    }
  }
  
  // Phone Authentication
  async setupPhoneAuth(phoneNumber: string, recaptchaContainer: string) {
    try {
      const recaptchaVerifier = new RecaptchaVerifier(auth, recaptchaContainer, {
        size: 'invisible',
        callback: () => {
          // reCAPTCHA solved
        }
      });
      
      const confirmationResult = await signInWithPhoneNumber(
        auth,
        phoneNumber,
        recaptchaVerifier
      );
      
      return confirmationResult;
    } catch (error) {
      throw new Error(`Phone authentication setup failed: ${error}`);
    }
  }
  
  // Create or update user profile in Firestore
  private async createOrUpdateUserProfile(user: User): Promise<void> {
    const userRef = doc(firestore, 'users', user.uid);
    const userDoc = await getDoc(userRef);
    
    if (!userDoc.exists()) {
      // Create new user profile
      const userProfile: UserProfile = {
        uid: user.uid,
        email: user.email || '',
        displayName: user.displayName || '',
        photoURL: user.photoURL || '',
        phoneNumber: user.phoneNumber || '',
        preferences: {
          language: 'en',
          culturalRegion: 'global',
          prayerTraditions: ['sunni'],
          theme: 'light',
          islamicSchool: 'sunni'
        },
        privacy: {
          profileVisibility: 'private',
          analyticsOptOut: false,
          dataSharing: false
        },
        createdAt: new Date(),
        lastActiveAt: new Date()
      };
      
      await setDoc(userRef, userProfile);
    } else {
      // Update last active time
      await setDoc(userRef, {
        lastActiveAt: new Date()
      }, { merge: true });
    }
  }
}

export const authService = new AuthService();
```

---

## 📁 Cloud Storage Setup

### 1️⃣ **Storage Security Rules**
```javascript
// storage.rules
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {
    // User profile photos
    match /users/{userId}/profile/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                   request.auth.uid == userId &&
                   request.resource.size < 5 * 1024 * 1024 && // 5MB limit
                   request.resource.contentType.matches('image/.*');
    }
    
    // Memorial photos with privacy controls
    match /memorials/{memorialId}/{allPaths=**} {
      allow read: if request.auth != null && 
                  canAccessMemorial(memorialId);
      allow write: if request.auth != null &&
                   isMemorialOwner(memorialId) &&
                   request.resource.size < 10 * 1024 * 1024 && // 10MB limit
                   request.resource.contentType.matches('image/.*');
    }
    
    // Public Islamic assets (frames, icons, etc.)
    match /public/{allPaths=**} {
      allow read: if true; // Public access
      allow write: if request.auth != null && 
                   request.auth.token.admin == true;
    }
    
    // Helper functions (simplified)
    function canAccessMemorial(memorialId) {
      return request.auth != null; // Simplified for example
    }
    
    function isMemorialOwner(memorialId) {
      return request.auth != null; // Simplified for example
    }
  }
}
```

### 2️⃣ **Storage Service Implementation**
```typescript
// services/storage.service.ts
import {
  ref,
  uploadBytes,
  uploadBytesResumable,
  getDownloadURL,
  deleteObject,
  getMetadata,
  updateMetadata
} from 'firebase/storage';
import { storage } from '../firebase.config';

export class StorageService {
  // Upload memorial photo with Islamic frame
  async uploadMemorialPhoto(
    file: File | Blob,
    memorialId: string,
    userId: string,
    onProgress?: (progress: number) => void
  ): Promise<string> {
    try {
      // Validate file
      if (file.size > 10 * 1024 * 1024) {
        throw new Error('File size must be less than 10MB');
      }
      
      // Compress image before upload
      const compressedFile = await this.compressImage(file);
      
      const fileName = `${Date.now()}_${this.generateUUID()}.jpg`;
      const storageRef = ref(storage, `memorials/${memorialId}/${fileName}`);
      
      // Set metadata
      const metadata = {
        contentType: 'image/jpeg',
        customMetadata: {
          uploadedBy: userId,
          memorialId: memorialId,
          uploadedAt: new Date().toISOString()
        }
      };
      
      // Upload with progress tracking
      const uploadTask = uploadBytesResumable(storageRef, compressedFile, metadata);
      
      return new Promise((resolve, reject) => {
        uploadTask.on(
          'state_changed',
          (snapshot) => {
            const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
            if (onProgress) onProgress(progress);
          },
          (error) => reject(error),
          async () => {
            const downloadURL = await getDownloadURL(uploadTask.snapshot.ref);
            resolve(downloadURL);
          }
        );
      });
      
    } catch (error) {
      throw new Error(`Photo upload failed: ${error}`);
    }
  }
  
  // Compress image for optimal storage
  private async compressImage(file: File | Blob): Promise<Blob> {
    return new Promise((resolve) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d')!;
      const img = new Image();
      
      img.onload = () => {
        // Calculate optimal dimensions (max 1200px)
        const maxSize = 1200;
        const ratio = Math.min(maxSize / img.width, maxSize / img.height);
        
        canvas.width = img.width * ratio;
        canvas.height = img.height * ratio;
        
        // Draw and compress
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
        canvas.toBlob(resolve, 'image/jpeg', 0.8);
      };
      
      img.src = URL.createObjectURL(file);
    });
  }
  
  // Delete memorial photo
  async deleteMemorialPhoto(photoUrl: string): Promise<void> {
    try {
      const photoRef = ref(storage, photoUrl);
      await deleteObject(photoRef);
    } catch (error) {
      throw new Error(`Photo deletion failed: ${error}`);
    }
  }
  
  // Generate UUID for file names
  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
      const r = Math.random() * 16 | 0;
      const v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}

export const storageService = new StorageService();
```

---

## ☁️ Cloud Functions Setup

### 1️⃣ **Functions Project Structure**
```bash
functions/
├── src/
│   ├── index.ts
│   ├── auth/
│   │   ├── onUserCreate.ts
│   │   └── onUserDelete.ts
│   ├── memorials/
│   │   ├── onMemorialCreate.ts
│   │   ├── cleanupExpiredMemorials.ts
│   │   └── validateMemorialContent.ts
│   ├── prayers/
│   │   ├── onPrayerCreate.ts
│   │   └── updatePrayerStats.ts
│   ├── notifications/
│   │   ├── sendPrayerNotification.ts
│   │   └── sendDailyReminder.ts
│   └── analytics/
│       ├── aggregateDailyStats.ts
│       └── generateReports.ts
├── package.json
└── tsconfig.json
```

### 2️⃣ **Main Functions Index**
```typescript
// functions/src/index.ts
import { onCall, onRequest } from 'firebase-functions/v2/https';
import { onDocumentCreated, onDocumentUpdated, onDocumentDeleted } from 'firebase-functions/v2/firestore';
import { onSchedule } from 'firebase-functions/v2/scheduler';
import { setGlobalOptions } from 'firebase-functions/v2';

// Set global options
setGlobalOptions({
  maxInstances: 100,
  region: 'us-central1',
  memory: '1GiB',
  timeoutSeconds: 60
});

// Import function modules
import { onUserCreate, onUserDelete } from './auth';
import { onMemorialCreate, cleanupExpiredMemorials, validateMemorialContent } from './memorials';
import { onPrayerCreate, updatePrayerStats } from './prayers';
import { sendPrayerNotification, sendDailyReminder } from './notifications';
import { aggregateDailyStats } from './analytics';

// Auth Functions
export { onUserCreate, onUserDelete };

// Memorial Functions
export { onMemorialCreate, validateMemorialContent };
export const scheduleCleanupExpiredMemorials = onSchedule(
  { schedule: '0 0 * * *', timeZone: 'UTC' },
  cleanupExpiredMemorials
);

// Prayer Functions
export { onPrayerCreate, updatePrayerStats };

// Notification Functions
export { sendPrayerNotification };
export const scheduleDailyReminders = onSchedule(
  { schedule: '0 8 * * *', timeZone: 'UTC' },
  sendDailyReminder
);

// Analytics Functions
export const scheduleAnalyticsAggregation = onSchedule(
  { schedule: '0 1 * * *', timeZone: 'UTC' },
  aggregateDailyStats
);
```

### 3️⃣ **Memorial Auto-Expiration Function**
```typescript
// functions/src/memorials/cleanupExpiredMemorials.ts
import { ScheduledFunction } from 'firebase-functions/v2/scheduler';
import { getFirestore, FieldValue } from 'firebase-admin/firestore';
import { getStorage } from 'firebase-admin/storage';

const firestore = getFirestore();
const storage = getStorage();

export const cleanupExpiredMemorials: ScheduledFunction = async (event) => {
  try {
    console.log('Starting memorial cleanup process...');
    
    // Calculate 40-day cutoff (Islamic tradition)
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - 40);
    
    // Find expired memorials
    const expiredMemorialsQuery = await firestore
      .collection('memorials')
      .where('createdAt', '<=', cutoffDate)
      .where('autoExpiration', '==', true)
      .get();
    
    const batch = firestore.batch();
    const photoCleanupPromises: Promise<void>[] = [];
    
    for (const doc of expiredMemorialsQuery.docs) {
      const memorialData = doc.data();
      
      // Schedule photo cleanup
      if (memorialData.photoURL) {
        photoCleanupPromises.push(
          cleanupMemorialPhoto(memorialData.photoURL)
        );
      }
      
      // Add to batch delete
      batch.delete(doc.ref);
      
      // Delete prayer sub-collection
      const prayersQuery = await doc.ref.collection('prayers').get();
      prayersQuery.docs.forEach(prayerDoc => {
        batch.delete(prayerDoc.ref);
      });
    }
    
    // Execute batch delete
    await batch.commit();
    
    // Cleanup photos
    await Promise.all(photoCleanupPromises);
    
    console.log(`Cleaned up ${expiredMemorialsQuery.size} expired memorials`);
    
    // Log analytics
    await firestore.collection('analytics').doc('cleanup').set({
      lastCleanupAt: FieldValue.serverTimestamp(),
      memorialsDeleted: expiredMemorialsQuery.size,
      cleanupDate: cutoffDate
    }, { merge: true });
    
  } catch (error) {
    console.error('Memorial cleanup failed:', error);
    throw error;
  }
};

async function cleanupMemorialPhoto(photoURL: string): Promise<void> {
  try {
    const bucket = storage.bucket();
    const fileName = photoURL.split('/').pop()?.split('?')[0];
    if (fileName) {
      await bucket.file(fileName).delete();
    }
  } catch (error) {
    console.error('Photo cleanup failed:', error);
  }
}
```

---

## 📱 React Native Integration

### 1️⃣ **Project Setup with Expo 52**
```bash
# Create new Expo project with latest template
npx create-expo-app@latest TahlilApp --template

# Navigate to project directory
cd TahlilApp

# Install Firebase dependencies
npx expo install firebase
npx expo install @react-native-async-storage/async-storage
npx expo install expo-crypto
npx expo install expo-device
npx expo install expo-notifications

# Install UI and utility libraries
npx expo install react-native-paper
npx expo install react-native-vector-icons
npx expo install react-native-super-grid
npx expo install @shopify/react-native-skia

# Install state management
npm install zustand @tanstack/react-query

# Install development dependencies
npm install --save-dev @types/react @types/react-native typescript
```

### 2️⃣ **App Configuration**
```json
// app.json
{
  "expo": {
    "name": "Tahlil - Memorial Prayers",
    "slug": "tahlil-app",
    "version": "1.0.0",
    "orientation": "portrait",
    "icon": "./assets/icon.png",
    "userInterfaceStyle": "automatic",
    "splash": {
      "image": "./assets/splash.png",
      "resizeMode": "contain",
      "backgroundColor": "#1B5E20"
    },
    "assetBundlePatterns": [
      "**/*"
    ],
    "ios": {
      "supportsTablet": true,
      "bundleIdentifier": "com.tahlil.memorial.prayers",
      "googleServicesFile": "./GoogleService-Info.plist"
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/adaptive-icon.png",
        "backgroundColor": "#1B5E20"
      },
      "package": "com.tahlil.memorial.prayers",
      "googleServicesFile": "./google-services.json"
    },
    "web": {
      "favicon": "./assets/favicon.png"
    },
    "plugins": [
      "@react-native-firebase/app",
      "@react-native-firebase/auth",
      "@react-native-firebase/firestore",
      "@react-native-firebase/storage",
      "@react-native-firebase/messaging",
      "@react-native-firebase/analytics",
      [
        "expo-notifications",
        {
          "icon": "./assets/notification-icon.png",
          "color": "#1B5E20"
        }
      ]
    ],
    "extra": {
      "eas": {
        "projectId": "your-project-id"
      }
    }
  }
}
```

### 3️⃣ **Environment Configuration**
```bash
# .env.development
EXPO_PUBLIC_ENV=development
EXPO_PUBLIC_FIREBASE_API_KEY=your-dev-api-key
EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN=tahlil-dev.firebaseapp.com
EXPO_PUBLIC_FIREBASE_PROJECT_ID=tahlil-dev
EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET=tahlil-dev.appspot.com
EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
EXPO_PUBLIC_FIREBASE_APP_ID=your-app-id
EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID=your-measurement-id
EXPO_PUBLIC_USE_EMULATORS=true

# .env.production
EXPO_PUBLIC_ENV=production
EXPO_PUBLIC_FIREBASE_API_KEY=your-prod-api-key
EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN=tahlil-global.firebaseapp.com
EXPO_PUBLIC_FIREBASE_PROJECT_ID=tahlil-global
EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET=tahlil-global.appspot.com
EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
EXPO_PUBLIC_FIREBASE_APP_ID=your-app-id
EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID=your-measurement-id
EXPO_PUBLIC_USE_EMULATORS=false
```

---

## 🧪 Development Workflow

### 1️⃣ **Firebase Emulator Development**
```bash
# Start all Firebase emulators
firebase emulators:start

# Available emulators:
# ✔  firestore: http://localhost:8080
# ✔  functions: http://localhost:5001
# ✔  storage: http://localhost:9199
# ✔  ui: http://localhost:4000
# ✔  auth: http://localhost:9099

# Run React Native app with emulators
EXPO_PUBLIC_USE_EMULATORS=true npx expo start
```

### 2️⃣ **Testing Firebase Integration**
```typescript
// __tests__/firebase.test.ts
import { connectAuthEmulator, createUserWithEmailAndPassword } from 'firebase/auth';
import { connectFirestoreEmulator, doc, setDoc } from 'firebase/firestore';
import { auth, firestore } from '../firebase.config';

// Setup emulators for testing
beforeAll(async () => {
  if (!auth._delegate._config.emulator) {
    connectAuthEmulator(auth, 'http://localhost:9099');
  }
  if (!firestore._delegate._databaseId.projectId.includes('test')) {
    connectFirestoreEmulator(firestore, 'localhost', 8080);
  }
});

describe('Firebase Integration', () => {
  test('should create user and profile', async () => {
    const testEmail = 'test@example.com';
    const testPassword = 'TestPassword123!';
    
    // Create user
    const userCredential = await createUserWithEmailAndPassword(
      auth,
      testEmail,
      testPassword
    );
    
    // Create profile
    await setDoc(doc(firestore, 'users', userCredential.user.uid), {
      displayName: 'Test User',
      createdAt: new Date()
    });
    
    expect(userCredential.user).toBeDefined();
    expect(userCredential.user.email).toBe(testEmail);
  });
});
```

---

## 🚀 Deployment Pipeline

### 1️⃣ **GitHub Actions CI/CD**
```yaml
# .github/workflows/firebase-deploy.yml
name: Firebase Deploy

on:
  push:
    branches: [main, staging]
  pull_request:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout
      uses: actions/checkout@v4
      
    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '20'
        cache: 'npm'
        
    - name: Install dependencies
      run: |
        npm ci
        cd functions && npm ci
        
    - name: Run tests
      run: |
        npm run test
        cd functions && npm run test
        
    - name: Build Functions
      run: cd functions && npm run build
      
    - name: Setup Firebase CLI
      run: npm install -g firebase-tools
      
    - name: Deploy to Firebase
      run: |
        firebase deploy --only firestore:rules,storage:rules,functions
      env:
        FIREBASE_TOKEN: ${{ secrets.FIREBASE_TOKEN }}
```

### 2️⃣ **EAS Build Configuration**
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
      "channel": "development"
    },
    "staging": {
      "distribution": "internal",
      "channel": "staging"
    },
    "production": {
      "channel": "production"
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

---

## 📊 Monitoring & Analytics

### 1️⃣ **Firebase Analytics Setup**
```typescript
// services/analytics.service.ts
import { logEvent, setUserId, setUserProperties } from 'firebase/analytics';
import { analytics } from '../firebase.config';

export class AnalyticsService {
  // Log prayer session
  logPrayerSession(prayerType: string, count: number, duration: number) {
    logEvent(analytics, 'prayer_session_completed', {
      prayer_type: prayerType,
      prayer_count: count,
      session_duration: duration,
      timestamp: Date.now()
    });
  }
  
  // Log memorial creation
  logMemorialCreated(memorialId: string, privacyLevel: string) {
    logEvent(analytics, 'memorial_created', {
      memorial_id: memorialId,
      privacy_level: privacyLevel,
      timestamp: Date.now()
    });
  }
  
  // Set user properties
  setUserProperties(userId: string, properties: Record<string, string>) {
    setUserId(analytics, userId);
    setUserProperties(analytics, properties);
  }
}

export const analyticsService = new AnalyticsService();
```

---

This comprehensive Firebase setup guide provides everything needed to implement the complete Firebase ecosystem for the Tahlil memorial prayer platform with the latest 2026 technologies.
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