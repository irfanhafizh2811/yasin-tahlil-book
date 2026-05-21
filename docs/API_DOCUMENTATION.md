# 📡 API Documentation

## Overview

Tahlil uses Firebase services as the primary backend API, providing real-time, scalable, and secure Islamic memorial and prayer platform capabilities.

## 🔐 Authentication APIs

### Firebase Authentication

#### Sign In with Email
```kotlin
suspend fun signInWithEmail(
    email: String, 
    password: String
): AuthResult

// Usage
try {
    val result = firebaseAuthService.signInWithEmail(
        email = "user@example.com",
        password = "securePassword"
    )
    // Handle successful authentication
    val user = result.user
} catch (e: FirebaseAuthException) {
    // Handle authentication error
}
```

#### Sign In with Google
```kotlin
suspend fun signInWithGoogle(googleIdToken: String): AuthResult

// Usage  
val result = firebaseAuthService.signInWithGoogle(googleIdToken)
```

#### Sign In with Phone
```kotlin
suspend fun signInWithPhoneNumber(
    phoneNumber: String,
    verificationCode: String
): AuthResult

// Usage
val result = firebaseAuthService.signInWithPhoneNumber(
    phoneNumber = "+1234567890",
    verificationCode = "123456"
)
```

#### Anonymous Sign In
```kotlin
suspend fun signInAnonymously(): AuthResult

// Usage - for guest access to public memorials
val result = firebaseAuthService.signInAnonymously()
```

#### Get Current User
```kotlin
fun getCurrentUser(): FirebaseUser?

// Usage
val currentUser = firebaseAuthService.getCurrentUser()
if (currentUser != null) {
    // User is signed in
    val userId = currentUser.uid
    val email = currentUser.email
}
```

#### Sign Out
```kotlin
suspend fun signOut(): Result<Unit>

// Usage
firebaseAuthService.signOut()
```

## 🗄️ Firestore APIs

### Memorial Management

#### Create Memorial
```kotlin
suspend fun createMemorial(memorial: Memorial): Result<String>

// Usage
val memorial = Memorial(
    name = "Abdullah Rahman",
    arabicName = "عبد الله الرحمن",
    description = "A beloved father and community member",
    privacy = MemorialPrivacy.FAMILY,
    createdBy = currentUser.uid,
    familyMembers = listOf("family1", "family2")
)

try {
    val memorialId = firestoreService.createMemorial(memorial)
    Log.d("Memorial", "Created with ID: $memorialId")
} catch (e: Exception) {
    Log.e("Memorial", "Failed to create: ${e.message}")
}
```

#### Get User Memorials
```kotlin
fun getMemorials(userId: String): Flow<List<Memorial>>

// Usage
firestoreService.getMemorials(currentUser.uid)
    .collect { memorialList ->
        // Update UI with memorial list
        adapter.submitList(memorialList)
    }
```

#### Get Community Memorials
```kotlin
fun getCommunityMemorials(
    region: IslamicRegion? = null,
    limit: Int = 20
): Flow<List<Memorial>>

// Usage  
firestoreService.getCommunityMemorials(
    region = IslamicRegion.SOUTHEAST_ASIA,
    limit = 50
).collect { communityMemorials ->
    // Display community memorials
}
```

#### Update Memorial
```kotlin
suspend fun updateMemorial(memorial: Memorial): Result<Unit>

// Usage
val updatedMemorial = existingMemorial.copy(
    description = "Updated description"
)
firestoreService.updateMemorial(updatedMemorial)
```

#### Delete Memorial
```kotlin
suspend fun deleteMemorial(memorialId: String): Result<Unit>

// Usage
firestoreService.deleteMemorial("memorial_123")
```

#### Get Memorial by ID
```kotlin
suspend fun getMemorial(memorialId: String): Result<Memorial>

// Usage
val result = firestoreService.getMemorial("memorial_123")
result.onSuccess { memorial ->
    // Use memorial data
}.onFailure { error ->
    // Handle error
}
```

### Prayer Session Management

#### Start Prayer Session
```kotlin
suspend fun startPrayerSession(
    memorialId: String,
    prayerType: PrayerType,
    userId: String
): Result<String>

// Usage
val sessionId = firestoreService.startPrayerSession(
    memorialId = "memorial_123",
    prayerType = PrayerType.TAHLIL,
    userId = currentUser.uid
)
```

#### Complete Prayer Session
```kotlin
suspend fun completePrayerSession(
    sessionId: String,
    completed: Boolean = true
): Result<Unit>

// Usage
firestoreService.completePrayerSession(
    sessionId = "session_456",
    completed = true
)
```

#### Get User Prayer Sessions
```kotlin
fun getUserPrayerSessions(userId: String): Flow<List<PrayerSession>>

// Usage
firestoreService.getUserPrayerSessions(currentUser.uid)
    .collect { sessions ->
        // Update prayer history UI
    }
```

#### Get Memorial Prayer Sessions
```kotlin
fun getMemorialPrayerSessions(memorialId: String): Flow<List<PrayerSession>>

// Usage
firestoreService.getMemorialPrayerSessions("memorial_123")
    .collect { sessions ->
        // Show memorial prayer activity
    }
```

### User Profile Management

#### Create User Profile
```kotlin
suspend fun createUserProfile(userProfile: UserProfile): Result<Unit>

// Usage
val profile = UserProfile(
    userId = currentUser.uid,
    displayName = "Ahmad Abdullah",
    email = currentUser.email,
    islamicRegion = IslamicRegion.MIDDLE_EAST,
    schoolOfThought = SchoolOfThought.SUNNI_HANAFI,
    preferredLanguage = "ar",
    culturalPreferences = CulturalPreferences(
        showArabicText = true,
        enableRTL = true,
        prayerNotifications = true
    )
)

firestoreService.createUserProfile(profile)
```

#### Get User Profile
```kotlin
suspend fun getUserProfile(userId: String): Result<UserProfile>

// Usage
val result = firestoreService.getUserProfile(currentUser.uid)
result.onSuccess { profile ->
    // Use profile data for customization
}
```

#### Update User Profile
```kotlin
suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit>

// Usage
val updatedProfile = currentProfile.copy(
    preferredLanguage = "en",
    culturalPreferences = currentProfile.culturalPreferences.copy(
        enableRTL = false
    )
)
firestoreService.updateUserProfile(updatedProfile)
```

### Global Statistics

#### Get Global Prayer Stats
```kotlin
fun getGlobalPrayerStats(): Flow<GlobalPrayerStats>

// Usage
firestoreService.getGlobalPrayerStats()
    .collect { stats ->
        // Display global community participation
        binding.textTotalPrayers.text = stats.totalPrayersCompleted.toString()
        binding.textActiveMemorials.text = stats.activeMemorials.toString()
    }
```

#### Get Regional Stats
```kotlin
fun getRegionalStats(region: IslamicRegion): Flow<RegionalStats>

// Usage
firestoreService.getRegionalStats(IslamicRegion.SOUTH_ASIA)
    .collect { stats ->
        // Show regional community activity
    }
```

## 📁 Storage APIs

### Memorial Photo Management

#### Upload Memorial Photo
```kotlin
suspend fun uploadMemorialPhoto(
    memorialId: String,
    imageUri: Uri,
    compressionQuality: Float = 0.8f
): Result<String>

// Usage
try {
    val imageUri = // ... get image from gallery or camera
    val photoUrl = storageService.uploadMemorialPhoto(
        memorialId = "memorial_123",
        imageUri = imageUri,
        compressionQuality = 0.8f
    )
    // Update memorial with photo URL
} catch (e: Exception) {
    // Handle upload error
}
```

#### Get Memorial Photos
```kotlin
suspend fun getMemorialPhotos(memorialId: String): Result<List<String>>

// Usage
val result = storageService.getMemorialPhotos("memorial_123")
result.onSuccess { photoUrls ->
    // Load photos in gallery
    photoUrls.forEach { url ->
        // Load with Glide/Picasso
    }
}
```

#### Delete Memorial Photo
```kotlin
suspend fun deleteMemorialPhoto(
    memorialId: String,
    photoUrl: String
): Result<Unit>

// Usage
storageService.deleteMemorialPhoto("memorial_123", photoUrl)
```

### User Profile Photo

#### Upload Profile Photo
```kotlin
suspend fun uploadProfilePhoto(
    userId: String,
    imageUri: Uri
): Result<String>

// Usage
val photoUrl = storageService.uploadProfilePhoto(
    userId = currentUser.uid,
    imageUri = selectedImageUri
)
```

#### Delete Profile Photo
```kotlin
suspend fun deleteProfilePhoto(userId: String): Result<Unit>

// Usage
storageService.deleteProfilePhoto(currentUser.uid)
```

## 📱 Messaging APIs

### Push Notifications

#### Subscribe to Memorial Updates
```kotlin
suspend fun subscribeToMemorialUpdates(memorialId: String): Result<Unit>

// Usage
messagingService.subscribeToMemorialUpdates("memorial_123")
```

#### Unsubscribe from Memorial Updates
```kotlin
suspend fun unsubscribeFromMemorialUpdates(memorialId: String): Result<Unit>

// Usage
messagingService.unsubscribeFromMemorialUpdates("memorial_123")
```

#### Send Prayer Notification
```kotlin
suspend fun sendPrayerNotification(
    memorialId: String,
    prayerType: PrayerType,
    message: String
): Result<Unit>

// Usage - typically called by Cloud Functions
messagingService.sendPrayerNotification(
    memorialId = "memorial_123",
    prayerType = PrayerType.YASIN,
    message = "Join the evening Yasin prayer for Abdullah Rahman"
)
```

#### Update FCM Token
```kotlin
suspend fun updateFCMToken(token: String): Result<Unit>

// Usage
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    val token = task.result
    messagingService.updateFCMToken(token)
}
```

## 📊 Analytics APIs

### Event Tracking

#### Track Prayer Completion
```kotlin
fun trackPrayerCompletion(
    prayerType: PrayerType,
    memorialId: String,
    duration: Long
)

// Usage
analyticsService.trackPrayerCompletion(
    prayerType = PrayerType.TAHLIL,
    memorialId = "memorial_123",
    duration = 300_000L // 5 minutes
)
```

#### Track Memorial Creation
```kotlin
fun trackMemorialCreation(
    privacy: MemorialPrivacy,
    hasPhoto: Boolean,
    region: IslamicRegion
)

// Usage
analyticsService.trackMemorialCreation(
    privacy = MemorialPrivacy.FAMILY,
    hasPhoto = true,
    region = IslamicRegion.SOUTHEAST_ASIA
)
```

#### Track Cultural Settings
```kotlin
fun trackCulturalSettings(
    language: String,
    region: IslamicRegion,
    schoolOfThought: SchoolOfThought
)

// Usage
analyticsService.trackCulturalSettings(
    language = "ar",
    region = IslamicRegion.MIDDLE_EAST,
    schoolOfThought = SchoolOfThought.SUNNI_MALIKI
)
```

## 🔄 Real-time Listeners

### Memorial Real-time Updates
```kotlin
class MemorialRepository {
    fun observeMemorial(memorialId: String): Flow<Memorial?> {
        return callbackFlow {
            val listener = firestore.collection("memorials")
                .document(memorialId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val memorial = snapshot?.toObject<Memorial>()
                    trySend(memorial)
                }
            
            awaitClose { listener.remove() }
        }
    }
}
```

### Prayer Session Real-time Updates
```kotlin
fun observePrayerSessions(memorialId: String): Flow<List<PrayerSession>> {
    return callbackFlow {
        val listener = firestore.collection("prayer_sessions")
            .whereEqualTo("memorialId", memorialId)
            .orderBy("startedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val sessions = snapshot?.toObjects<PrayerSession>() ?: emptyList()
                trySend(sessions)
            }
        
        awaitClose { listener.remove() }
    }
}
```

## 🚨 Error Handling

### Custom Exception Types
```kotlin
sealed class TahlilApiException : Exception() {
    data class NetworkException(override val message: String) : TahlilApiException()
    data class AuthenticationException(override val message: String) : TahlilApiException()
    data class ValidationException(override val message: String) : TahlilApiException()
    data class PermissionException(override val message: String) : TahlilApiException()
    data class CulturalException(override val message: String) : TahlilApiException()
    data class RateLimitException(override val message: String) : TahlilApiException()
}
```

### Error Response Format
```kotlin
data class ApiErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, Any>? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Common error codes
object ErrorCodes {
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val PERMISSION_DENIED = "PERMISSION_DENIED"
    const val VALIDATION_FAILED = "VALIDATION_FAILED"
    const val CULTURAL_VALIDATION_FAILED = "CULTURAL_VALIDATION_FAILED"
    const val RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED"
    const val MEMORIAL_EXPIRED = "MEMORIAL_EXPIRED"
    const val NETWORK_ERROR = "NETWORK_ERROR"
}
```

## 📝 Request/Response Models

### Memorial Model
```kotlin
data class Memorial(
    val id: String = "",
    val name: String,
    val arabicName: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val privacy: MemorialPrivacy = MemorialPrivacy.PRIVATE,
    val createdBy: String,
    val familyMembers: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp,
    val prayerStats: PrayerStats = PrayerStats(),
    val isActive: Boolean = true,
    val culturalSettings: CulturalSettings = CulturalSettings()
)

enum class MemorialPrivacy {
    PRIVATE,    // Creator only
    FAMILY,     // Family members only  
    COMMUNITY   // Public Islamic community
}
```

### Prayer Session Model
```kotlin
data class PrayerSession(
    val id: String = "",
    val userId: String,
    val memorialId: String,
    val prayerType: PrayerType,
    val startedAt: Timestamp = Timestamp.now(),
    val completedAt: Timestamp? = null,
    val completed: Boolean = false,
    val duration: Long = 0L,
    val notes: String? = null
)

enum class PrayerType {
    TAHLIL,         // Memorial prayer sequence
    YASIN,          // Surah Yasin recitation
    FATIHAH,        // Al-Fatihah
    DHIKR_MORNING,  // Morning remembrance
    DHIKR_EVENING,  // Evening remembrance
    CUSTOM          // User-defined prayer
}
```

### User Profile Model
```kotlin
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
    val notificationSettings: NotificationSettings,
    val createdAt: Timestamp = Timestamp.now(),
    val lastActiveAt: Timestamp = Timestamp.now(),
    val isVerified: Boolean = false,
    val communityStatus: CommunityStatus = CommunityStatus.MEMBER
)
```

## 🔄 Pagination

### Memorial Pagination
```kotlin
data class PaginatedResult<T>(
    val data: List<T>,
    val nextPageToken: String?,
    val hasNextPage: Boolean,
    val totalCount: Int?
)

// Usage
suspend fun getMemorialsWithPagination(
    userId: String,
    pageSize: Int = 20,
    pageToken: String? = null
): PaginatedResult<Memorial> {
    // Implementation with Firestore pagination
}
```

## 🌍 Internationalization

### Multi-language Support
```kotlin
data class LocalizedContent(
    val arabic: String,
    val english: String,
    val indonesian: String? = null,
    val turkish: String? = null,
    val russian: String? = null,
    val malay: String? = null
)

// Usage in API responses
data class PrayerContent(
    val type: PrayerType,
    val title: LocalizedContent,
    val content: LocalizedContent,
    val transliteration: String? = null,
    val audio: Map<String, String> = emptyMap() // language -> audio URL
)
```

## 📈 Rate Limiting

### API Rate Limits
```
User Operations:
- Memorial creation: 5 per day
- Photo upload: 20 per day  
- Prayer sessions: 100 per day
- Profile updates: 10 per day

Reading Operations:
- Memorial queries: 1000 per hour
- Prayer session queries: 500 per hour
- Global stats: 100 per hour

Community Operations:
- Family group creation: 2 per month
- Community posts: 10 per day
```

## 🔧 Development & Testing

### Firebase Emulator APIs
```kotlin
// For local development
class EmulatorConfig {
    fun setupEmulators() {
        FirebaseFirestore.getInstance().useEmulator("localhost", 8080)
        FirebaseAuth.getInstance().useEmulator("localhost", 9099)
        FirebaseStorage.getInstance().useEmulator("localhost", 9199)
    }
}
```

### Testing Utilities
```kotlin
class ApiTestHelper {
    fun createTestMemorial(): Memorial = Memorial(
        name = "Test Memorial",
        arabicName = "اختبار",
        createdBy = "test_user",
        privacy = MemorialPrivacy.PRIVATE
    )
    
    fun createTestUser(): UserProfile = UserProfile(
        userId = "test_user",
        displayName = "Test User",
        islamicRegion = IslamicRegion.MIDDLE_EAST,
        schoolOfThought = SchoolOfThought.SUNNI_HANAFI,
        preferredLanguage = "en"
    )
}
```

This API documentation provides comprehensive coverage of all Firebase services integration, ensuring developers can effectively implement Islamic memorial and prayer features while maintaining cultural authenticity and security standards.