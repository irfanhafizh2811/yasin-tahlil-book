# 🏗️ Architecture Documentation

## Overview

Tahlil follows a **Hybrid Architecture Strategy** that preserves the existing Android application while seamlessly integrating Firebase ecosystem for new memorial and community features.

## 📐 Architecture Principles

### 1. **Zero Disruption Strategy**
- Preserve all existing functionality for 240M+ users
- Additive architecture - new features don't break existing ones
- Gradual feature rollout with backward compatibility

### 2. **Hybrid Data Strategy**
```kotlin
// LOCAL: Fast, offline-first features
Room Database (SQLite)
├── Tasbeeh counters
├── User preferences  
├── Downloaded Quran text
├── Theme settings
└── App configuration

// CLOUD: Community and sync features
Firebase Firestore
├── User profiles
├── Memorial prayers
├── Community statistics
├── Prayer sessions
└── Global content
```

### 3. **Scalable Service Layer**
Clean separation between local and cloud services with dependency injection

## 🎯 Architectural Layers

### 1. Presentation Layer (View + ViewModel)

```
📱 Activities/Fragments
├── 🏠 MainActivity (Dashboard)
├── 📿 TasbeehActivity (Counter)
├── 📖 SurahActivity (Reading)
├── 🕌 MemorialActivity (New)
└── 👤 ProfileActivity (Enhanced)
     ↕️
🧠 ViewModels (MVVM)
├── MainViewModel
├── TasbeehViewModel  
├── SurahViewModel
├── MemorialViewModel (New)
└── ProfileViewModel (Enhanced)
```

### 2. Domain Layer (Business Logic)

```kotlin
// Repository Pattern
interface TasbeehRepository {
    // Local operations (preserved)
    suspend fun getTasbeehCount(): Flow<Int>
    suspend fun updateCount(count: Int)
    
    // Cloud operations (new)
    suspend fun syncWithCloud(): Result<Unit>
    suspend fun getGlobalStats(): Flow<GlobalStats>
}

interface MemorialRepository {
    // Pure cloud operations
    suspend fun createMemorial(memorial: Memorial): Result<String>
    suspend fun getMemorials(): Flow<List<Memorial>>
    suspend fun joinPrayer(memorialId: String): Result<Unit>
}
```

### 3. Data Layer (Local + Remote)

#### Local Data Sources
```kotlin
// Room Database (Existing + Enhanced)
@Database(
    entities = [
        TasbeehEntity::class,      // Existing
        SurahEntity::class,        // Existing  
        UserProfileEntity::class,  // Enhanced
        OfflineMemorialEntity::class // New (offline cache)
    ],
    version = 2
)
abstract class DhikrRoomDatabase : RoomDatabase()
```

#### Remote Data Sources
```kotlin
// Firebase Services
class FirebaseAuthService {
    // Multi-provider authentication
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithPhone(phoneNumber: String): AuthResult
}

class FirestoreService {
    // Memorial operations
    suspend fun createMemorial(memorial: Memorial): String
    suspend fun getMemorials(userId: String): Flow<List<Memorial>>
    suspend fun joinPrayer(memorialId: String, userId: String): Unit
}

class StorageService {
    // Photo management
    suspend fun uploadMemorialPhoto(memorialId: String, imageUri: Uri): String
    suspend fun getMemorialPhotos(memorialId: String): List<String>
}
```

## 🔄 Data Flow Patterns

### 1. Offline-First Pattern (Existing Features)

```
User Action → ViewModel → Repository → Room → UI Update
                    ↓
              Background Sync (Optional)
                    ↓
              Firebase (Community Features)
```

### 2. Real-time Pattern (New Features)

```
User Action → ViewModel → Repository → Firestore → Real-time Updates
                                         ↓
                                   All Connected Users
```

### 3. Hybrid Sync Pattern

```
Local Change → Room Database → Background Worker → Firebase Sync
     ↕️              ↕️                ↕️               ↕️
UI Update ← Repository ← Success/Failure ← Cloud Response
```

## 🏛️ Dependency Injection (Koin)

### Module Structure

```kotlin
// App Module (Enhanced)
val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { DhikrRoomDatabase.getDatabase(androidContext()) }
}

// Existing Modules (Preserved)
val tasbeehModule = module {
    factory { TasbeehRepository(get(), get()) }
    viewModel { TasbeehViewModel(get()) }
}

// New Firebase Module
val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    
    single<FirebaseAuthService> { FirebaseAuthServiceImpl(get()) }
    single<FirestoreService> { FirestoreServiceImpl(get()) }
    single<StorageService> { StorageServiceImpl(get()) }
}

// Memorial Module (New)
val memorialModule = module {
    single<MemorialRepository> { 
        MemorialRepositoryImpl(get(), get(), get()) 
    }
    viewModel { MemorialViewModel(get(), get()) }
}
```

## 📊 State Management

### 1. Local State (Room + SharedPreferences)

```kotlin
// Preserved existing state management
class CounterPreference(context: Context) {
    private val prefs = context.getSharedPreferences("counter", Context.MODE_PRIVATE)
    
    var currentCount: Int
        get() = prefs.getInt("current_count", 0)
        set(value) = prefs.edit().putInt("current_count", value).apply()
}

// Enhanced with cloud sync capabilities
class UserPreference(context: Context) {
    // Existing preferences (preserved)
    var selectedTheme: ThemeType
    var selectedLanguage: String
    
    // New cloud-enabled preferences
    var cloudSyncEnabled: Boolean
    var lastSyncTimestamp: Long
}
```

### 2. Cloud State (Firestore)

```kotlin
// User Profile Document
data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val islamicRegion: IslamicRegion,
    val schoolOfThought: SchoolOfThought,
    val preferredLanguage: String,
    val culturalPreferences: CulturalPreferences,
    val privacySettings: PrivacySettings,
    val createdAt: Timestamp,
    val lastActiveAt: Timestamp
)

// Memorial Document
data class Memorial(
    val id: String = "",
    val name: String,
    val arabicName: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val privacy: MemorialPrivacy,
    val createdBy: String,
    val familyMembers: List<String> = emptyList(),
    val createdAt: Timestamp,
    val expiresAt: Timestamp, // 40 days from creation
    val prayerStats: PrayerStats = PrayerStats(),
    val isActive: Boolean = true
)
```

## 🔐 Security Architecture

### 1. Firebase Security Rules

```javascript
// Firestore Rules - Memorial Privacy
match /memorials/{memorialId} {
  allow read: if request.auth != null && (
    // Creator access
    resource.data.createdBy == request.auth.uid ||
    // Privacy-based access
    (resource.data.privacy == 'private' && 
     resource.data.createdBy == request.auth.uid) ||
    (resource.data.privacy == 'family' && 
     request.auth.uid in resource.data.familyMembers) ||
    (resource.data.privacy == 'community')
  );
}
```

### 2. Client-Side Security

```kotlin
class SecurityManager {
    // Input validation
    fun validateMemorialInput(memorial: Memorial): ValidationResult
    
    // Data encryption for sensitive local data
    fun encryptSensitiveData(data: String): String
    
    // Privacy compliance checks
    fun checkPrivacyCompliance(operation: String): Boolean
}
```

## 📱 UI Architecture

### 1. Theme System (Enhanced)

```kotlin
// Existing theme system (preserved)
abstract class BaseTheme {
    abstract val primaryColor: Int
    abstract val backgroundColor: Int
    abstract val textColor: Int
}

// Enhanced with Islamic cultural themes
class IslamicThemeManager {
    // Cultural appropriateness validation
    fun validateCulturalTheme(theme: Theme, region: IslamicRegion): Boolean
    
    // RTL layout support
    fun applyRTLLayout(view: View, language: String)
    
    // Prayer time color schemes
    fun getPrayerTimeTheme(prayerTime: PrayerTime): Theme
}
```

### 2. Component Architecture

```kotlin
// Reusable Islamic UI Components
class IslamicCardView : CardView {
    // Islamic design patterns
    // Arabic typography support
    // RTL layout handling
}

class PrayerCounterView : View {
    // Enhanced counter with cloud sync
    // Real-time updates
    // Cultural animations
}

class MemorialPhotoView : ImageView {
    // Islamic-appropriate image handling
    // Privacy controls
    // Compression and upload
}
```

## ⚡ Performance Architecture

### 1. Lazy Loading Strategy

```kotlin
// Progressive data loading
class MemorialRepository {
    fun getMemorials(): Flow<PagingData<Memorial>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MemorialPagingSource() }
        ).flow
    }
}
```

### 2. Caching Strategy

```kotlin
// Multi-level caching
class CacheManager {
    // Memory cache (immediate)
    private val memoryCache = LruCache<String, Any>(maxSize)
    
    // Disk cache (offline)
    private val diskCache = DiskLruCache.create()
    
    // Firestore cache (automatic)
    // Enabled via FirebaseFirestoreSettings
}
```

### 3. Background Processing

```kotlin
// WorkManager for background tasks
class SyncWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        // Sync local changes to cloud
        // Download community updates
        // Clean expired memorials
        return Result.success()
    }
}
```

## 🌍 Internationalization Architecture

### 1. Multi-Language Support

```kotlin
class LanguageManager {
    // RTL language detection
    fun isRTL(language: String): Boolean
    
    // Font selection for Arabic
    fun getArabicFont(scriptType: ArabicScript): Typeface
    
    // Cultural text formatting
    fun formatIslamicDate(date: Date, region: IslamicRegion): String
}
```

### 2. Content Localization

```
res/
├── values/ (Default - English)
├── values-ar/ (Arabic - RTL)
├── values-id/ (Indonesian)
├── values-tr/ (Turkish)
├── values-ru/ (Russian)
└── values-ms/ (Malay)

assets/
├── quran/
│   ├── arabic/
│   ├── translations/
│   └── audio/
└── prayers/
    ├── tahlil/
    ├── yasin/
    └── fatihah/
```

## 🔄 Migration Strategy

### Phase 1: Foundation (Current)
- Firebase integration
- Security rules
- Authentication system

### Phase 2: Core Features  
- Memorial creation
- Prayer sessions
- User profiles

### Phase 3: Community
- Real-time participation
- Global statistics
- Photo sharing

### Phase 4: Advanced
- AI content validation
- Advanced analytics
- Performance optimization

## 📈 Monitoring & Analytics

### 1. Performance Monitoring

```kotlin
class PerformanceTracker {
    // Firebase Performance
    fun trackStartupTime()
    fun trackMemorialCreation()
    fun trackPrayerSession()
    
    // Custom metrics
    fun trackIslamicFeatureUsage()
    fun trackCulturalPreferences()
}
```

### 2. Error Handling

```kotlin
class ErrorHandler {
    // Centralized error handling
    fun handleFirebaseError(error: FirebaseException)
    fun handleNetworkError(error: NetworkException)
    fun handleValidationError(error: ValidationException)
    
    // Cultural sensitivity in error messages
    fun localizeError(error: AppError, language: String): String
}
```

## 🧪 Testing Architecture

### 1. Testing Strategy

```kotlin
// Unit Tests
class MemorialRepositoryTest {
    @Test fun createMemorial_validInput_returnsSuccess()
    @Test fun getMemorials_withPrivacy_returnsFiltered()
}

// Integration Tests  
class FirebaseIntegrationTest {
    @Test fun authenticateUser_multiProvider_succeeds()
    @Test fun createMemorial_endToEnd_syncsCorrectly()
}

// Cultural Validation Tests
class IslamicValidationTest {
    @Test fun validateMemorial_culturalCompliance_passes()
    @Test fun validatePrayerText_scholarApproved_succeeds()
}
```

### 2. Mock Strategy

```kotlin
// Firebase Emulators for local testing
class TestConfiguration {
    fun setupFirebaseEmulators() {
        // Firestore emulator
        // Auth emulator  
        // Storage emulator
    }
}
```

## 🚀 Deployment Architecture

### 1. Environment Configuration

```
environments/
├── development/
│   ├── google-services-dev.json
│   └── firebase-dev.json
├── staging/
│   ├── google-services-staging.json
│   └── firebase-staging.json
└── production/
    ├── google-services.json
    └── firebase.json
```

### 2. CI/CD Pipeline

```yaml
# GitHub Actions
name: Tahlil CI/CD
on: [push, pull_request]

jobs:
  test:
    - Unit tests
    - Integration tests
    - Cultural validation tests
    
  security:
    - Security rule testing
    - Firebase emulator tests
    - Privacy compliance checks
    
  deploy:
    - Firebase rules deployment
    - Android APK generation
    - Play Store upload (staging)
```

This architecture ensures scalability, maintainability, and cultural authenticity while preserving the existing user experience for millions of users.