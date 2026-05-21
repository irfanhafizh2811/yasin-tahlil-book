# 📋 Coding Guidelines & Standards

## Overview

This document outlines coding standards and best practices for the Tahlil project. All contributors must follow these guidelines to ensure code quality, maintainability, and cultural authenticity.

## 🎯 Core Principles

### 1. **Islamic Authenticity First**
- All Islamic content must be verified by scholars
- Arabic text must be properly encoded (UTF-8)
- Cultural sensitivity in naming and implementation
- Privacy compliance with Islamic family values

### 2. **Zero Breaking Changes**
- Preserve existing functionality for 240M+ users
- Additive architecture only
- Backward compatibility required
- Gradual feature rollout

### 3. **Performance & Security**
- Sub-3 second app startup
- Secure data handling
- Privacy-first design
- Offline-first for core features

## 🔤 Naming Conventions

### 1. **Kotlin Classes**

```kotlin
// ✅ CORRECT - PascalCase for classes
class MemorialRepository
class FirebaseAuthService  
class IslamicThemeManager

// ✅ CORRECT - Interface naming
interface MemorialRepository
interface PrayerService
interface CulturalValidator

// ❌ INCORRECT
class memorialRepository
class firebase_auth_service
class IMemorialRepository
```

### 2. **Variables & Functions**

```kotlin
// ✅ CORRECT - camelCase
val currentPrayerCount: Int
val memorialId: String
fun createMemorial(name: String)
fun validateIslamicContent(text: String)

// ❌ INCORRECT  
val CurrentPrayerCount: Int
val memorial_id: String
fun CreateMemorial(name: String)
```

### 3. **Constants**

```kotlin
// ✅ CORRECT - SCREAMING_SNAKE_CASE
const val MEMORIAL_EXPIRATION_DAYS = 40
const val MAX_PHOTO_SIZE_MB = 5
const val DEFAULT_PRAYER_COUNT = 0

// Islamic-specific constants
const val TAHLIL_PRAYER_STEPS = 7
const val YASIN_VERSE_COUNT = 83
const val ISLAMIC_HIJRI_OFFSET = 622
```

### 4. **Islamic Content Naming**

```kotlin
// ✅ CORRECT - Respectful Islamic naming
enum class PrayerType {
    TAHLIL,           // Memorial prayer
    YASIN,            // Surah Yasin
    FATIHAH,          // The Opening
    DHIKR_MORNING,    // Morning remembrance
    DHIKR_EVENING     // Evening remembrance
}

enum class IslamicRegion {
    MIDDLE_EAST,
    SOUTH_ASIA, 
    SOUTHEAST_ASIA,
    CENTRAL_ASIA,
    NORTH_AFRICA,
    // ... others
}

// ❌ INCORRECT - Avoid abbreviations for sacred content
enum class PrayerType {
    THL,    // Too abbreviated
    YSN,    // Not respectful
    FTH     // Unclear
}
```

## 🏗️ Architecture Guidelines

### 1. **Repository Pattern**

```kotlin
// ✅ CORRECT - Clean repository interface
interface MemorialRepository {
    suspend fun createMemorial(memorial: Memorial): Result<String>
    suspend fun getMemorials(userId: String): Flow<List<Memorial>>
    suspend fun updateMemorial(memorial: Memorial): Result<Unit>
    suspend fun deleteMemorial(memorialId: String): Result<Unit>
    suspend fun joinPrayer(memorialId: String, userId: String): Result<Unit>
}

// ✅ CORRECT - Implementation with proper error handling
class MemorialRepositoryImpl(
    private val firestoreService: FirestoreService,
    private val storageService: StorageService,
    private val localCache: MemorialDao
) : MemorialRepository {
    
    override suspend fun createMemorial(memorial: Memorial): Result<String> {
        return try {
            // Validate Islamic content
            validateMemorialContent(memorial)
            
            // Create in Firestore
            val memorialId = firestoreService.createMemorial(memorial)
            
            // Cache locally for offline access
            localCache.insertMemorial(memorial.copy(id = memorialId))
            
            Result.success(memorialId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 2. **ViewModel Guidelines**

```kotlin
// ✅ CORRECT - Proper ViewModel structure
class MemorialViewModel(
    private val memorialRepository: MemorialRepository,
    private val authService: FirebaseAuthService
) : BaseViewModel() {
    
    private val _memorials = MutableLiveData<List<Memorial>>()
    val memorials: LiveData<List<Memorial>> = _memorials
    
    private val _uiState = MutableLiveData<MemorialUiState>()
    val uiState: LiveData<MemorialUiState> = _uiState
    
    // ✅ Clear function naming
    fun loadUserMemorials() {
        viewModelScope.launch {
            _uiState.value = MemorialUiState.Loading
            try {
                val currentUser = authService.getCurrentUser()
                if (currentUser != null) {
                    memorialRepository.getMemorials(currentUser.uid)
                        .collect { memorialList ->
                            _memorials.value = memorialList
                            _uiState.value = MemorialUiState.Success
                        }
                }
            } catch (e: Exception) {
                _uiState.value = MemorialUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// ✅ CORRECT - Sealed class for UI state
sealed class MemorialUiState {
    object Loading : MemorialUiState()
    object Success : MemorialUiState() 
    data class Error(val message: String) : MemorialUiState()
}
```

### 3. **Data Classes**

```kotlin
// ✅ CORRECT - Comprehensive data class with validation
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
    val expiresAt: Timestamp = calculateExpirationDate(),
    val prayerStats: PrayerStats = PrayerStats(),
    val isActive: Boolean = true,
    val culturalSettings: CulturalSettings = CulturalSettings()
) {
    // ✅ Validation functions
    fun isExpired(): Boolean = expiresAt.toDate().before(Date())
    
    fun canUserAccess(userId: String): Boolean {
        return when (privacy) {
            MemorialPrivacy.PRIVATE -> createdBy == userId
            MemorialPrivacy.FAMILY -> createdBy == userId || userId in familyMembers
            MemorialPrivacy.COMMUNITY -> true
        }
    }
    
    // ✅ Islamic business logic
    fun getRemainingDays(): Int {
        val now = Date()
        val expiry = expiresAt.toDate()
        val diffInMillis = expiry.time - now.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    companion object {
        // ✅ Islamic constants
        private const val MEMORIAL_DURATION_DAYS = 40L
        
        private fun calculateExpirationDate(): Timestamp {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, MEMORIAL_DURATION_DAYS.toInt())
            return Timestamp(calendar.time)
        }
    }
}
```

## 📱 UI/UX Guidelines

### 1. **Activity & Fragment Structure**

```kotlin
// ✅ CORRECT - Clean activity structure
class MemorialActivity : BaseActivity() {
    
    private lateinit var binding: ActivityMemorialBinding
    private val viewModel: MemorialViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
        setupEventListeners()
    }
    
    private fun setupUI() {
        // Islamic theming
        applyIslamicTheme()
        setupRTLSupport()
        configureArabicFonts()
    }
    
    private fun observeViewModel() {
        viewModel.memorials.observe(this) { memorials ->
            updateMemorialsList(memorials)
        }
        
        viewModel.uiState.observe(this) { state ->
            handleUiState(state)
        }
    }
    
    // ✅ Cultural theming
    private fun applyIslamicTheme() {
        val currentTheme = ThemeManager.getCurrentTheme()
        if (currentTheme.isIslamicTheme()) {
            // Apply Islamic color schemes
            // Configure prayer time backgrounds
            // Set cultural animations
        }
    }
}
```

### 2. **RecyclerView Adapters**

```kotlin
// ✅ CORRECT - Efficient adapter with ViewBinding
class MemorialAdapter(
    private val onMemorialClick: (Memorial) -> Unit,
    private val onJoinPrayer: (Memorial) -> Unit
) : RecyclerView.Adapter<MemorialAdapter.MemorialViewHolder>() {
    
    private val memorials = mutableListOf<Memorial>()
    
    inner class MemorialViewHolder(
        private val binding: ItemMemorialBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(memorial: Memorial) {
            with(binding) {
                // Basic information
                textMemorialName.text = memorial.name
                textArabicName.text = memorial.arabicName
                
                // Islamic content formatting
                formatIslamicText(memorial)
                setupPrayerProgress(memorial.prayerStats)
                configurePrivacyIndicator(memorial.privacy)
                
                // Click listeners
                root.setOnClickListener { onMemorialClick(memorial) }
                buttonJoinPrayer.setOnClickListener { onJoinPrayer(memorial) }
            }
        }
        
        private fun formatIslamicText(memorial: Memorial) {
            // Arabic text RTL support
            if (!memorial.arabicName.isNullOrBlank()) {
                binding.textArabicName.apply {
                    text = memorial.arabicName
                    textDirection = View.TEXT_DIRECTION_RTL
                    typeface = getArabicTypeface()
                }
            }
        }
    }
}
```

## 🔐 Security Guidelines

### 1. **Input Validation**

```kotlin
// ✅ CORRECT - Comprehensive validation
class MemorialValidator {
    
    fun validateMemorialInput(memorial: Memorial): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Basic validation
        if (memorial.name.isBlank()) {
            errors.add("Memorial name cannot be empty")
        }
        
        if (memorial.name.length > 100) {
            errors.add("Memorial name too long (max 100 characters)")
        }
        
        // Islamic content validation
        if (!memorial.arabicName.isNullOrBlank()) {
            if (!isValidArabicText(memorial.arabicName)) {
                errors.add("Arabic name contains invalid characters")
            }
        }
        
        // Cultural validation
        if (!isCulturallyAppropriate(memorial)) {
            errors.add("Content does not meet Islamic guidelines")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
    
    private fun isValidArabicText(text: String): Boolean {
        // Validate Arabic Unicode range (U+0600 to U+06FF)
        val arabicRegex = Regex("[\\u0600-\\u06FF\\s]+")
        return arabicRegex.matches(text)
    }
    
    private fun isCulturallyAppropriate(memorial: Memorial): Boolean {
        // Check against Islamic content guidelines
        // Validate memorial naming conventions
        // Ensure respectful descriptions
        return CulturalValidator.validate(memorial)
    }
}
```

### 2. **Data Sanitization**

```kotlin
// ✅ CORRECT - Secure data handling
class SecurityUtils {
    
    fun sanitizeUserInput(input: String): String {
        return input
            .trim()
            .replace(Regex("[<>\"'&]"), "") // Remove potentially harmful characters
            .take(1000) // Limit length
    }
    
    fun sanitizeArabicText(arabicText: String): String {
        // Preserve Arabic characters and common punctuation
        val allowedPattern = Regex("[\\u0600-\\u06FF\\s.,!?()\\-]+")
        return if (allowedPattern.matches(arabicText)) {
            arabicText.trim()
        } else {
            arabicText.filter { char ->
                char.code in 0x0600..0x06FF || char.isWhitespace() || char in ".,!?()-"
            }
        }
    }
}
```

## 🔄 Error Handling

### 1. **Result Pattern**

```kotlin
// ✅ CORRECT - Sealed Result class
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// ✅ CORRECT - Extension functions for Result
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}
```

### 2. **Exception Handling**

```kotlin
// ✅ CORRECT - Custom exceptions
sealed class TahlilException : Exception() {
    data class NetworkException(override val message: String) : TahlilException()
    data class ValidationException(override val message: String) : TahlilException()
    data class CulturalException(override val message: String) : TahlilException()
    data class AuthenticationException(override val message: String) : TahlilException()
}

// ✅ CORRECT - Error handling in repository
class MemorialRepositoryImpl : MemorialRepository {
    
    override suspend fun createMemorial(memorial: Memorial): Result<String> {
        return try {
            // Cultural validation first
            val validation = MemorialValidator().validateMemorialInput(memorial)
            if (validation is ValidationResult.Error) {
                return Result.Error(TahlilException.ValidationException(validation.message))
            }
            
            val memorialId = firestoreService.createMemorial(memorial)
            Result.Success(memorialId)
            
        } catch (e: FirebaseFirestoreException) {
            Result.Error(TahlilException.NetworkException("Failed to create memorial: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

## 📝 Documentation Guidelines

### 1. **KDoc Comments**

```kotlin
/**
 * Service for managing Islamic memorial prayers and community participation.
 * 
 * Handles creation, management, and participation in memorial prayers following
 * Islamic traditions and the 40-day memorial period.
 * 
 * @author Development Team
 * @since 2026
 * @see Memorial
 * @see PrayerSession
 */
class MemorialService {
    
    /**
     * Creates a new memorial with Islamic traditions and privacy controls.
     * 
     * The memorial will automatically expire after 40 days following Islamic tradition.
     * Content is validated for cultural appropriateness before creation.
     * 
     * @param memorial The memorial data to create
     * @return Result containing memorial ID on success, exception on failure
     * @throws CulturalException if content doesn't meet Islamic guidelines
     * @throws NetworkException if Firebase operation fails
     * 
     * @sample
     * ```kotlin
     * val memorial = Memorial(
     *     name = "Abdullah Rahman",
     *     arabicName = "عبد الله الرحمن",
     *     privacy = MemorialPrivacy.FAMILY
     * )
     * val result = memorialService.createMemorial(memorial)
     * ```
     */
    suspend fun createMemorial(memorial: Memorial): Result<String>
}
```

### 2. **TODO Comments**

```kotlin
// ✅ CORRECT - Clear TODO with context
// TODO: [ISLAMIC-123] Add Hijri calendar support for memorial dates
// TODO: [PERFORMANCE-456] Optimize Arabic font loading for RTL text
// TODO: [CULTURAL-789] Validate with Islamic scholars before release

// ❌ INCORRECT - Vague TODOs
// TODO: fix this
// TODO: make it better
```

## 🧪 Testing Guidelines

### 1. **Unit Tests**

```kotlin
// ✅ CORRECT - Comprehensive test class
class MemorialRepositoryTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var repository: MemorialRepository
    private lateinit var mockFirestoreService: FirestoreService
    private lateinit var mockStorageService: StorageService
    
    @Before
    fun setup() {
        mockFirestoreService = mockk()
        mockStorageService = mockk()
        repository = MemorialRepositoryImpl(mockFirestoreService, mockStorageService)
    }
    
    @Test
    fun `createMemorial with valid Islamic content returns success`() = runTest {
        // Given
        val memorial = createValidMemorial()
        every { mockFirestoreService.createMemorial(memorial) } returns "memorial123"
        
        // When
        val result = repository.createMemorial(memorial)
        
        // Then
        assert(result is Result.Success)
        assertEquals("memorial123", (result as Result.Success).data)
    }
    
    @Test
    fun `createMemorial with inappropriate content throws CulturalException`() = runTest {
        // Given
        val inappropriateMemorial = Memorial(
            name = "Inappropriate Name",
            description = "Content that violates Islamic guidelines"
        )
        
        // When & Then
        assertThrows<TahlilException.CulturalException> {
            repository.createMemorial(inappropriateMemorial)
        }
    }
    
    private fun createValidMemorial() = Memorial(
        name = "Test Memorial",
        arabicName = "اختبار",
        privacy = MemorialPrivacy.PRIVATE,
        createdBy = "user123"
    )
}
```

### 2. **Cultural Validation Tests**

```kotlin
// ✅ CORRECT - Cultural compliance testing
class IslamicValidationTest {
    
    private lateinit var validator: CulturalValidator
    
    @Before
    fun setup() {
        validator = CulturalValidator()
    }
    
    @Test
    fun `validateArabicText with proper Arabic script returns true`() {
        val validArabic = "بسم الله الرحمن الرحيم"
        assertTrue(validator.isValidArabicText(validArabic))
    }
    
    @Test
    fun `validateMemorialName with Islamic appropriate name passes`() {
        val memorial = Memorial(name = "Abdullah", arabicName = "عبد الله")
        assertTrue(validator.isCulturallyAppropriate(memorial))
    }
    
    @Test
    fun `memorial expiration follows 40-day Islamic tradition`() {
        val memorial = Memorial(name = "Test")
        val expectedDays = 40
        val actualDays = memorial.getRemainingDays()
        
        assertEquals(expectedDays, actualDays, 1) // Allow 1 day tolerance
    }
}
```

## 🚀 Performance Guidelines

### 1. **Memory Management**

```kotlin
// ✅ CORRECT - Efficient memory usage
class MemorialAdapter {
    
    // Use DiffUtil for efficient list updates
    private val diffCallback = object : DiffUtil.ItemCallback<Memorial>() {
        override fun areItemsTheSame(oldItem: Memorial, newItem: Memorial) = 
            oldItem.id == newItem.id
            
        override fun areContentsTheSame(oldItem: Memorial, newItem: Memorial) = 
            oldItem == newItem
    }
    
    private val differ = AsyncListDiffer(this, diffCallback)
    
    fun submitList(list: List<Memorial>) {
        differ.submitList(list)
    }
}

// ✅ CORRECT - Image loading with caching
class ImageLoader {
    fun loadMemorialPhoto(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_memorial_placeholder)
            .error(R.drawable.ic_memorial_error)
            .into(imageView)
    }
}
```

### 2. **Database Optimization**

```kotlin
// ✅ CORRECT - Optimized Room queries
@Dao
interface MemorialDao {
    
    @Query("SELECT * FROM memorials WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getActiveMemorials(userId: String): Flow<List<MemorialEntity>>
    
    @Query("SELECT * FROM memorials WHERE expiresAt > :currentTime")
    suspend fun getActiveMemorials(currentTime: Long): List<MemorialEntity>
    
    // Efficient batch operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorials(memorials: List<MemorialEntity>)
}
```

## ☪️ Islamic Content Guidelines

### 1. **Arabic Text Handling**

```kotlin
// ✅ CORRECT - Proper Arabic text processing
class ArabicTextProcessor {
    
    fun formatArabicText(text: String): String {
        return text
            .trim()
            .replace(Regex("\\s+"), " ") // Normalize spaces
            .let { normalizeArabicDiacritics(it) }
    }
    
    fun isValidArabicText(text: String): Boolean {
        // Arabic Unicode blocks
        val arabicRange = 0x0600..0x06FF
        val arabicPresentationA = 0xFB50..0xFDFF
        val arabicPresentationB = 0xFE70..0xFEFF
        
        return text.all { char ->
            char.code in arabicRange || 
            char.code in arabicPresentationA || 
            char.code in arabicPresentationB ||
            char.isWhitespace() ||
            char in ".,!?()-"
        }
    }
    
    private fun normalizeArabicDiacritics(text: String): String {
        // Normalize common Arabic diacritics
        return text
            .replace("ً", "ً") // Tanween Fath
            .replace("ٌ", "ٌ") // Tanween Damm
            .replace("ٍ", "ٍ") // Tanween Kasr
    }
}
```

### 2. **Prayer Content Validation**

```kotlin
// ✅ CORRECT - Islamic prayer validation
class PrayerValidator {
    
    private val authenticPrayerTexts = mapOf(
        PrayerType.FATIHAH to "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
        PrayerType.YASIN to "يٓسٓ وَٱلْقُرْءَانِ ٱلْحَكِيمِ",
        // ... other authentic texts
    )
    
    fun validatePrayerText(type: PrayerType, text: String): Boolean {
        val authenticText = authenticPrayerTexts[type]
        return authenticText?.let { text.contains(it) } ?: false
    }
    
    fun validatePrayerSequence(prayers: List<PrayerStep>): ValidationResult {
        // Validate Tahlil sequence follows Islamic tradition
        // Check prayer order and content
        return when {
            prayers.isEmpty() -> ValidationResult.Error("Prayer sequence cannot be empty")
            !followsIslamicOrder(prayers) -> ValidationResult.Error("Prayer order incorrect")
            else -> ValidationResult.Success
        }
    }
}
```

This comprehensive coding guidelines document ensures all team members follow consistent standards while maintaining Islamic authenticity and technical excellence.