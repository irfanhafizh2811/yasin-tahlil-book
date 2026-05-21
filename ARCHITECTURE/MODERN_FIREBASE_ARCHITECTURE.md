# Modern Firebase Architecture for Tahlil Platform

## Overview
This document outlines the complete modern architecture for the Tahlil (Buku Yasin and Tahlil Online) platform using Firebase ecosystem with latest technologies and best practices for 2026.

## Architecture Philosophy
- **Serverless-First**: Leveraging Firebase's serverless capabilities
- **Real-time**: Live memorial updates and prayer synchronization
- **Scalable**: Supporting 1.8B potential Muslim users globally
- **Secure**: Islamic-compliant privacy and security standards
- **Offline-First**: Prayer continuity without internet dependency

## Technology Stack

### Frontend (React Native + Expo 52)
```
├── React Native 0.76.x (latest)
├── Expo SDK 52.0.x
├── TypeScript 5.6.x
├── React Query v5 (TanStack Query)
├── Zustand 5.x (State Management)
├── React Navigation v7
├── React Native Reanimated 4.x
├── React Native Paper 5.x (Material Design 3)
├── React Hook Form 7.x
├── Zod 3.x (Schema Validation)
└── React Native MMKV (Local Storage)
```

### Backend (Firebase Ecosystem)
```
Firebase Services:
├── Firebase Auth (Multi-provider authentication)
├── Cloud Firestore (NoSQL real-time database)
├── Cloud Storage (Media and document storage)
├── Cloud Functions v2 (Node.js 20 runtime)
├── Firebase Hosting (Web dashboard)
├── Cloud Messaging (Push notifications)
├── Remote Config (Feature flags)
├── Analytics + Crashlytics
├── App Distribution (Beta testing)
├── Security Rules (Fine-grained access control)
├── Extensions (Third-party integrations)
└── Emulator Suite (Local development)
```

### Development & DevOps
```
├── Firebase CLI 13.x
├── GitHub Actions (CI/CD)
├── ESLint 9.x + Prettier
├── Jest 29.x + React Native Testing Library
├── Detox (E2E testing)
├── Flipper (Debugging)
├── Sentry (Error monitoring)
└── Firebase Emulator Suite
```

## Firebase Services Architecture

### 1. Authentication (Firebase Auth)
```typescript
// Multi-provider authentication strategy
const authProviders = {
  email: EmailAuthProvider,
  google: GoogleAuthProvider,
  apple: OAuthProvider, // Apple Sign-In
  phone: PhoneAuthProvider,
  anonymous: signInAnonymously
}

// Islamic-compliant user profiles
interface UserProfile {
  uid: string;
  email?: string;
  displayName: string;
  photoURL?: string;
  phoneNumber?: string;
  preferredLanguage: 'ar' | 'en' | 'id' | 'ur' | 'tr' | 'ms';
  timezone: string;
  isVerified: boolean;
  createdAt: Timestamp;
  lastActiveAt: Timestamp;
  privacySettings: PrivacySettings;
  religiousPreferences: ReligiousPreferences;
}
```

### 2. Database Architecture (Cloud Firestore)

#### Collection Structure
```
/users/{userId}
  - profile: UserProfile
  - settings: UserSettings
  - prayers: SubCollection<Prayer>
  - memorials: SubCollection<Memorial>

/memorials/{memorialId}
  - metadata: MemorialMetadata
  - prayers: SubCollection<Prayer>
  - participants: SubCollection<Participant>
  - media: SubCollection<MediaItem>

/communities/{communityId}
  - info: CommunityInfo
  - members: SubCollection<Member>
  - events: SubCollection<Event>

/notifications/{userId}
  - messages: SubCollection<NotificationMessage>

/analytics/{date}
  - daily: DailyAnalytics
  - user_activity: SubCollection<UserActivity>
```

#### Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Memorials are readable by authenticated users, writable by creator
    match /memorials/{memorialId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        (resource == null || resource.data.createdBy == request.auth.uid);
    }
    
    // Community access based on membership
    match /communities/{communityId} {
      allow read: if request.auth != null && 
        request.auth.uid in resource.data.members;
      allow write: if request.auth != null && 
        request.auth.uid in resource.data.admins;
    }
  }
}
```

### 3. Cloud Functions (Node.js 20)

#### Function Architecture
```typescript
// functions/src/index.ts
import { onDocumentCreated, onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { onCall, HttpsError } from 'firebase-functions/v2/https';
import { onSchedule } from 'firebase-functions/v2/scheduler';

// Memorial creation notification
export const onMemorialCreated = onDocumentCreated(
  'memorials/{memorialId}',
  async (event) => {
    const memorial = event.data?.data();
    await sendMemorialNotifications(memorial);
    await updateCommunityActivity(memorial.communityId);
  }
);

// Daily prayer reminders
export const sendDailyPrayerReminders = onSchedule(
  'every day 05:00',
  async (event) => {
    await sendPrayerReminders();
  }
);

// Islamic date calculations
export const calculateIslamicDates = onCall(
  { region: 'asia-southeast1' },
  async (request) => {
    return await getIslamicCalendar(request.data.location);
  }
);
```

### 4. Storage Architecture (Cloud Storage)

#### Storage Structure
```
/users/{userId}/
  ├── profile/
  │   ├── avatar.jpg
  │   └── cover.jpg
  ├── memorials/
  │   ├── {memorialId}/
  │   │   ├── photos/
  │   │   ├── documents/
  │   │   └── audio/
  └── temp/

/public/
  ├── islamic-resources/
  │   ├── quran-audio/
  │   ├── prayer-guides/
  │   └── cultural-assets/
  └── templates/
      ├── memorial-templates/
      └── prayer-templates/
```

#### Storage Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Users can upload to their own directories
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Public resources are readable by all authenticated users
    match /public/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if false; // Only admin uploads
    }
  }
}
```

## Mobile App Architecture (React Native)

### 1. Project Structure
```
src/
├── components/
│   ├── ui/           # Reusable UI components
│   ├── forms/        # Form components
│   └── islamic/      # Islamic-specific components
├── screens/
│   ├── auth/         # Authentication screens
│   ├── memorial/     # Memorial management
│   ├── prayer/       # Prayer screens
│   └── community/    # Community features
├── navigation/
│   ├── AppNavigator.tsx
│   └── AuthNavigator.tsx
├── services/
│   ├── firebase/     # Firebase service wrappers
│   ├── islamic/      # Islamic calendar, prayer times
│   └── offline/      # Offline data sync
├── stores/           # Zustand stores
├── hooks/            # Custom React hooks
├── utils/            # Utility functions
├── types/            # TypeScript definitions
└── constants/        # App constants
```

### 2. State Management (Zustand)
```typescript
// stores/authStore.ts
interface AuthState {
  user: User | null;
  isLoading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  updateProfile: (data: Partial<UserProfile>) => Promise<void>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isLoading: false,
  signIn: async (email, password) => {
    set({ isLoading: true });
    try {
      const result = await signInWithEmailAndPassword(auth, email, password);
      set({ user: result.user, isLoading: false });
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },
  // ... other methods
}));
```

### 3. Offline-First Architecture
```typescript
// services/offline/OfflineManager.ts
export class OfflineManager {
  private mmkv = new MMKV();
  
  async enableOfflinePersistence() {
    await enableNetwork(firestore);
    enableIndexedDbPersistence(firestore);
  }
  
  async cachePrayerData(prayers: Prayer[]) {
    this.mmkv.set('cached_prayers', JSON.stringify(prayers));
  }
  
  getCachedPrayerData(): Prayer[] {
    const cached = this.mmkv.getString('cached_prayers');
    return cached ? JSON.parse(cached) : [];
  }
}
```

## Real-time Features

### 1. Live Memorial Updates
```typescript
// hooks/useMemorialUpdates.ts
export const useMemorialUpdates = (memorialId: string) => {
  const [prayers, setPrayers] = useState<Prayer[]>([]);
  
  useEffect(() => {
    const unsubscribe = onSnapshot(
      collection(firestore, `memorials/${memorialId}/prayers`),
      (snapshot) => {
        const updates = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        } as Prayer));
        setPrayers(updates);
      }
    );
    
    return unsubscribe;
  }, [memorialId]);
  
  return prayers;
};
```

### 2. Push Notifications
```typescript
// services/notifications/NotificationService.ts
export class NotificationService {
  async initializeNotifications() {
    const messaging = getMessaging();
    const token = await getToken(messaging);
    
    // Save token to user profile
    await updateDoc(doc(firestore, 'users', auth.currentUser!.uid), {
      fcmToken: token
    });
    
    // Handle foreground messages
    onMessage(messaging, (payload) => {
      showInAppNotification(payload);
    });
  }
  
  async sendMemorialInvitation(memorialId: string, userIds: string[]) {
    await httpsCallable(functions, 'sendMemorialInvitation')({
      memorialId,
      userIds
    });
  }
}
```

## Security & Privacy

### 1. Data Encryption
```typescript
// services/security/EncryptionService.ts
export class EncryptionService {
  async encryptSensitiveData(data: string): Promise<string> {
    // Client-side encryption for sensitive prayer content
    const encrypted = await crypto.subtle.encrypt(
      { name: 'AES-GCM', iv: new Uint8Array(12) },
      await this.getEncryptionKey(),
      new TextEncoder().encode(data)
    );
    return btoa(String.fromCharCode(...new Uint8Array(encrypted)));
  }
  
  private async getEncryptionKey(): Promise<CryptoKey> {
    // Derive key from user authentication
    const keyMaterial = await crypto.subtle.importKey(
      'raw',
      new TextEncoder().encode(auth.currentUser!.uid),
      { name: 'PBKDF2' },
      false,
      ['deriveKey']
    );
    
    return crypto.subtle.deriveKey(
      { name: 'PBKDF2', salt: new Uint8Array(16), iterations: 100000, hash: 'SHA-256' },
      keyMaterial,
      { name: 'AES-GCM', length: 256 },
      false,
      ['encrypt', 'decrypt']
    );
  }
}
```

### 2. Islamic Privacy Compliance
```typescript
// services/privacy/IslamicPrivacyService.ts
export class IslamicPrivacyService {
  async applyIslamicPrivacyRules(userProfile: UserProfile): Promise<UserProfile> {
    // Implement Islamic guidelines for digital privacy
    const filtered = { ...userProfile };
    
    // Remove or anonymize data based on Islamic privacy principles
    if (filtered.gender === 'female' && !filtered.allowGenderDisplay) {
      delete filtered.gender;
    }
    
    // Apply hijab principles to profile photos
    if (filtered.photoURL && !filtered.allowPhotoDisplay) {
      filtered.photoURL = await this.getDefaultIslamicAvatar(filtered.gender);
    }
    
    return filtered;
  }
}
```

## Performance Optimization

### 1. Bundle Optimization
```javascript
// metro.config.js
const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

// Tree shaking for Firebase
config.resolver.alias = {
  'firebase/auth': 'firebase/auth/dist/index.esm.js',
  'firebase/firestore': 'firebase/firestore/dist/index.esm.js',
  'firebase/storage': 'firebase/storage/dist/index.esm.js',
};

// Hermes optimizations
config.transformer.minifierConfig = {
  mangle: { keep_fnames: true },
  output: { ascii_only: true, quote_style: 3, wrap_iife: true },
  compress: { drop_console: true },
};

module.exports = config;
```

### 2. Image Optimization
```typescript
// components/islamic/OptimizedImage.tsx
export const OptimizedImage: React.FC<OptimizedImageProps> = ({ 
  source, 
  placeholder,
  ...props 
}) => {
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  
  return (
    <View>
      {isLoading && <IslamicPlaceholder />}
      <Image
        source={source}
        onLoad={() => setIsLoading(false)}
        onError={() => setHasError(true)}
        resizeMode="cover"
        {...props}
      />
      {hasError && <IslamicFallbackImage />}
    </View>
  );
};
```

## Islamic Features Integration

### 1. Prayer Time Calculations
```typescript
// services/islamic/PrayerTimeService.ts
export class PrayerTimeService {
  async getCurrentPrayerTimes(location: GeoLocation): Promise<PrayerTimes> {
    const coordinates = new Coordinates(location.latitude, location.longitude);
    const params = CalculationMethod.MuslimWorldLeague();
    const date = new Date();
    
    const prayerTimes = new PrayerTimes(coordinates, date, params);
    
    return {
      fajr: prayerTimes.fajr,
      sunrise: prayerTimes.sunrise,
      dhuhr: prayerTimes.dhuhr,
      asr: prayerTimes.asr,
      maghrib: prayerTimes.maghrib,
      isha: prayerTimes.isha
    };
  }
}
```

### 2. Islamic Calendar Integration
```typescript
// services/islamic/IslamicCalendarService.ts
export class IslamicCalendarService {
  async getIslamicDate(gregorianDate: Date): Promise<IslamicDate> {
    // Convert Gregorian to Hijri date
    const hijriDate = gregorianToHijri(gregorianDate);
    
    return {
      day: hijriDate.day,
      month: hijriDate.month,
      year: hijriDate.year,
      monthName: this.getArabicMonthName(hijriDate.month),
      weekDay: this.getArabicWeekDay(gregorianDate.getDay())
    };
  }
  
  async getIslamicHolidays(year: number): Promise<IslamicHoliday[]> {
    // Calculate Islamic holidays for the given Hijri year
    return [
      { name: 'Eid al-Fitr', date: this.calculateEidAlFitr(year) },
      { name: 'Eid al-Adha', date: this.calculateEidAlAdha(year) },
      // ... other holidays
    ];
  }
}
```

## Deployment Strategy

### 1. Firebase Project Setup
```bash
# Install Firebase CLI
npm install -g firebase-tools@latest

# Initialize Firebase project
firebase init

# Select services:
# ✓ Firestore
# ✓ Functions
# ✓ Hosting
# ✓ Storage
# ✓ Emulators

# Deploy to staging
firebase deploy --project tahlil-staging

# Deploy to production
firebase deploy --project tahlil-production
```

### 2. Environment Configuration
```typescript
// config/firebase.config.ts
const firebaseConfig = {
  development: {
    projectId: 'tahlil-dev',
    // Use emulators
    useEmulators: true
  },
  staging: {
    projectId: 'tahlil-staging',
    // Staging configuration
  },
  production: {
    projectId: 'tahlil-production',
    // Production configuration
  }
};

export const getFirebaseConfig = () => {
  const env = process.env.NODE_ENV || 'development';
  return firebaseConfig[env];
};
```

## Monitoring & Analytics

### 1. Firebase Analytics
```typescript
// services/analytics/AnalyticsService.ts
export class AnalyticsService {
  async trackMemorialCreated(memorialId: string, type: string) {
    logEvent(analytics, 'memorial_created', {
      memorial_id: memorialId,
      memorial_type: type,
      timestamp: serverTimestamp()
    });
  }
  
  async trackPrayerCompleted(prayerType: string, duration: number) {
    logEvent(analytics, 'prayer_completed', {
      prayer_type: prayerType,
      duration_seconds: duration,
      completion_time: new Date().toISOString()
    });
  }
}
```

### 2. Performance Monitoring
```typescript
// services/monitoring/PerformanceService.ts
export class PerformanceService {
  async trackScreenLoad(screenName: string) {
    const trace = trace(performance, `screen_load_${screenName}`);
    trace.start();
    
    // Track loading completion
    return () => trace.stop();
  }
  
  async trackNetworkRequest(url: string) {
    const trace = trace(performance, 'network_request');
    trace.putAttribute('url', url);
    trace.start();
    return () => trace.stop();
  }
}
```

## Testing Strategy

### 1. Firebase Emulator Testing
```typescript
// __tests__/firebase/firestore.test.ts
import { initializeTestEnvironment } from '@firebase/rules-unit-testing';

describe('Firestore Security Rules', () => {
  let testEnv: RulesTestEnvironment;
  
  beforeAll(async () => {
    testEnv = await initializeTestEnvironment({
      projectId: 'tahlil-test',
      firestore: {
        rules: readFileSync('firestore.rules', 'utf8'),
      },
    });
  });
  
  test('Users can only read their own data', async () => {
    const alice = testEnv.authenticatedContext('alice');
    const bob = testEnv.authenticatedContext('bob');
    
    await assertFails(bob.firestore().doc('users/alice').get());
    await assertSucceeds(alice.firestore().doc('users/alice').get());
  });
});
```

### 2. React Native Testing
```typescript
// __tests__/screens/MemorialScreen.test.tsx
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { MemorialScreen } from '../src/screens/memorial/MemorialScreen';

describe('MemorialScreen', () => {
  test('creates new memorial successfully', async () => {
    const { getByText, getByTestId } = render(<MemorialScreen />);
    
    fireEvent.press(getByText('Create Memorial'));
    fireEvent.changeText(getByTestId('memorial-name-input'), 'Test Memorial');
    fireEvent.press(getByText('Save'));
    
    await waitFor(() => {
      expect(getByText('Memorial created successfully')).toBeTruthy();
    });
  });
});
```

## Migration Strategy

### 1. Data Migration from Current System
```typescript
// migration/DataMigrationService.ts
export class DataMigrationService {
  async migrateFromSQLiteToFirestore() {
    // Migrate existing prayer data
    const sqliteData = await this.readSQLiteData();
    
    const batch = writeBatch(firestore);
    
    sqliteData.forEach(record => {
      const docRef = doc(collection(firestore, 'prayers'));
      batch.set(docRef, {
        ...record,
        migratedAt: serverTimestamp(),
        version: '2.0'
      });
    });
    
    await batch.commit();
  }
}
```

### 2. Gradual Feature Rollout
```typescript
// services/features/FeatureToggleService.ts
export class FeatureToggleService {
  async isFeatureEnabled(featureName: string, userId: string): Promise<boolean> {
    const config = await fetchAndActivate(remoteConfig);
    const featureFlags = JSON.parse(getValue(remoteConfig, 'feature_flags').asString());
    
    return featureFlags[featureName]?.enabled === true;
  }
}
```

## Success Metrics

### 1. Technical KPIs
- App startup time: < 3 seconds
- Offline functionality: 100% prayer features available
- Real-time updates: < 1 second latency
- Crash rate: < 0.1%
- Bundle size: < 50MB

### 2. Islamic Compliance Metrics
- Cultural sensitivity score: 95%+
- Scholar approval rate: 100%
- Privacy compliance: GDPR + Islamic standards
- Accessibility: WCAG 2.1 AA compliant

### 3. User Engagement
- Daily active users: Track growth
- Memorial participation rate: > 80%
- Prayer completion rate: > 90%
- Community engagement: Track interactions

---

This modern Firebase architecture provides a scalable, secure, and culturally-appropriate foundation for the global Tahlil platform, leveraging the latest technologies while maintaining Islamic principles and values.