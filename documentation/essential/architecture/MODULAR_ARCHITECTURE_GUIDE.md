# 🏗️ Modular Architecture Guide - Tahlil Platform
## Single Activity + Jetpack Compose + Navigation Component

### 📋 Overview

Complete guide for implementing modular architecture in the Tahlil Android application using Single Activity pattern with Jetpack Compose and Navigation Component for bottom navigation.

---

## 🎯 Architecture Principles

### Single Activity Architecture Benefits
```kotlin
// Why Single Activity?
✅ Consistent Navigation (Bottom Navigation remains constant)
✅ Shared Element Transitions (Seamless between Compose screens) 
✅ Simplified State Management (Single Activity lifecycle)
✅ Better Performance (No Activity recreation overhead)
✅ Modern Android Development (Google recommended pattern)
✅ Material Design 3 Implementation (Coherent UI/UX)
```

### Modular Design Benefits
```kotlin
// Why Modular Architecture?
✅ Feature Isolation (Independent development and testing)
✅ Build Performance (Parallel module compilation)
✅ Team Scalability (Multiple developers, minimal conflicts)
✅ Code Reusability (Shared components across features)
✅ Dependency Management (Clear separation of concerns)
✅ Testing Strategy (Module-specific test coverage)
```

---

## 🏢 Module Structure

### Root Project Structure
```kotlin
app/
├── :app                         # Main application module (Single Activity Host)
│   ├── MainActivity.kt          # Single Activity with Navigation setup
│   ├── MainNavigation.kt        # Bottom Navigation + NavHost configuration
│   ├── TahlilApplication.kt     # Application class with Hilt
│   ├── di/                      # App-level Hilt modules
│   └── theme/                   # Material 3 theme configuration
│
├── :core                        # Core shared modules
│   ├── :core-ui                 # Compose Design System
│   │   ├── components/          # Reusable Compose components
│   │   │   ├── IslamicCard.kt
│   │   │   ├── PrayerButton.kt
│   │   │   ├── ArabicText.kt
│   │   │   └── LoadingIndicator.kt
│   │   ├── theme/               # Material 3 + Islamic theming
│   │   │   ├── Color.kt
│   │   │   ├── Typography.kt
│   │   │   ├── Theme.kt
│   │   │   └── IslamicColors.kt
│   │   └── islamic/             # Islamic-specific UI components
│   │       ├── QiblaCompass.kt
│   │       ├── HijriDatePicker.kt
│   │       └── PrayerTimeCard.kt
│   │
│   ├── :core-data               # Repository + Data sources
│   │   ├── repository/          # Repository pattern implementation
│   │   │   ├── TasbeehRepository.kt
│   │   │   ├── MemorialRepository.kt
│   │   │   └── UserRepository.kt
│   │   ├── database/            # Room database
│   │   │   ├── TahlilDatabase.kt
│   │   │   ├── entities/
│   │   │   └── dao/
│   │   └── firebase/            # Firebase data sources
│   │       ├── FirestoreDataSource.kt
│   │       └── StorageDataSource.kt
│   │
│   ├── :core-firebase           # Firebase services
│   │   ├── auth/                # Authentication service
│   │   │   └── AuthService.kt
│   │   ├── firestore/           # Firestore operations
│   │   │   ├── MemorialService.kt
│   │   │   └── CommunityService.kt
│   │   ├── storage/             # Cloud Storage
│   │   │   └── PhotoService.kt
│   │   └── messaging/           # FCM integration
│   │       └── NotificationService.kt
│   │
│   └── :core-common             # Shared utilities
│       ├── utils/               # Extension functions
│       │   ├── StringExtensions.kt
│       │   ├── DateExtensions.kt
│       │   └── ComposeExtensions.kt
│       ├── models/              # Data models
│       │   ├── Memorial.kt
│       │   ├── Prayer.kt
│       │   └── User.kt
│       └── constants/           # App constants
│           ├── IslamicConstants.kt
│           └── NavigationConstants.kt
│
├── :feature                     # Feature modules
│   ├── :feature-tasbeeh         # Prayer counter (existing functionality)
│   │   ├── presentation/        # Compose UI + ViewModels
│   │   │   ├── TasbeehScreen.kt
│   │   │   ├── TasbeehViewModel.kt
│   │   │   └── CounterComposables.kt
│   │   ├── domain/              # Use cases
│   │   │   ├── GetPrayerCountUseCase.kt
│   │   │   └── SavePrayerSessionUseCase.kt
│   │   └── data/                # Feature-specific data
│   │       └── TasbeehLocalDataSource.kt
│   │
│   ├── :feature-memorial        # Firebase memorial system
│   │   ├── presentation/        # Memorial Compose screens
│   │   │   ├── CreateMemorialScreen.kt
│   │   │   ├── MemorialListScreen.kt
│   │   │   ├── MemorialDetailScreen.kt
│   │   │   └── MemorialViewModel.kt
│   │   ├── domain/              # Memorial business logic
│   │   │   ├── CreateMemorialUseCase.kt
│   │   │   ├── GetMemorialListUseCase.kt
│   │   │   └── ShareMemorialUseCase.kt
│   │   └── data/                # Firestore integration
│   │       ├── MemorialRemoteDataSource.kt
│   │       └── MemorialRepositoryImpl.kt
│   │
│   ├── :feature-community       # Social features
│   │   ├── presentation/        # Community Compose screens
│   │   │   ├── CommunityScreen.kt
│   │   │   ├── GlobalStatsScreen.kt
│   │   │   └── CommunityViewModel.kt
│   │   └── data/                # Community data sources
│   │       └── CommunityDataSource.kt
│   │
│   └── :feature-auth            # Authentication
│       ├── presentation/        # Auth Compose screens
│       │   ├── LoginScreen.kt
│       │   ├── RegisterScreen.kt
│       │   └── AuthViewModel.kt
│       └── data/                # Firebase Auth integration
│           └── AuthDataSource.kt
│
└── :shared                      # Shared resources
    ├── :shared-preferences      # Settings & preferences
    │   ├── UserPreferences.kt
    │   └── AppSettings.kt
    ├── :shared-analytics        # Analytics & tracking
    │   └── AnalyticsManager.kt
    └── :shared-resources        # Strings, assets, fonts
        ├── fonts/               # font_lpmq_isep_misbah for Arabic content (Noto Naskh, font_lpmq_isep_misbah)
        ├── values/              # Multi-language strings
        └── drawable/            # Islamic assets and icons
```

---

## 🚀 Implementation Details

### 1. Single Activity Setup (MainActivity.kt)

```kotlin
// app/MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TahlilTheme {
                MainNavigation()
            }
        }
    }
}

// app/MainNavigation.kt
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            TahlilBottomNavigation(
                navController = navController,
                items = listOf(
                    BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.Favorite),
                    BottomNavItem("memorial", "Memorial", Icons.Default.LocationOn),
                    BottomNavItem("community", "Community", Icons.Default.People),
                    BottomNavItem("profile", "Profile", Icons.Default.Person)
                )
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "tasbeeh",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Feature module navigation graphs
            tasbeehNavigation(navController)
            memorialNavigation(navController)
            communityNavigation(navController)
            authNavigation(navController)
        }
    }
}
```

### 2. Bottom Navigation Component

```kotlin
// core-ui/components/TahlilBottomNavigation.kt
@Composable
fun TahlilBottomNavigation(
    navController: NavController,
    items: List<BottomNavItem>
) {
    val currentDestination by navController.currentDestinationAsState()
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { 
                it.route == item.route 
            } == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
```

### 3. Feature Module Navigation

```kotlin
// feature-tasbeeh/presentation/TasbeehNavigation.kt
fun NavGraphBuilder.tasbeehNavigation(navController: NavController) {
    composable("tasbeeh") {
        TasbeehScreen(navController = navController)
    }
    composable("tasbeeh/detail/{prayerId}") { backStackEntry ->
        val prayerId = backStackEntry.arguments?.getString("prayerId") ?: ""
        TasbeehDetailScreen(
            prayerId = prayerId,
            navController = navController
        )
    }
}

// feature-memorial/presentation/MemorialNavigation.kt
fun NavGraphBuilder.memorialNavigation(navController: NavController) {
    composable("memorial") {
        MemorialListScreen(navController = navController)
    }
    composable("memorial/create") {
        CreateMemorialScreen(navController = navController)
    }
    composable("memorial/detail/{memorialId}") { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        MemorialDetailScreen(
            memorialId = memorialId,
            navController = navController
        )
    }
}
```

### 4. Hilt Dependency Injection Setup

```kotlin
// app/TahlilApplication.kt
@HiltAndroidApp
class TahlilApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Configure Firestore offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}

// app/di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
    
    @Provides
    @Singleton
    fun provideTahlilDatabase(@ApplicationContext context: Context): TahlilDatabase {
        return Room.databaseBuilder(
            context,
            TahlilDatabase::class.java,
            "tahlil_database"
        ).fallbackToDestructiveMigration().build()
    }
}

// core-firebase/di/FirebaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideAuthService(auth: FirebaseAuth): AuthService = AuthService(auth)
    
    @Provides
    @Singleton
    fun provideMemorialService(firestore: FirebaseFirestore): MemorialService = 
        MemorialService(firestore)
}
```

### 5. Repository Pattern Implementation

```kotlin
// core-data/repository/MemorialRepository.kt
interface MemorialRepository {
    suspend fun createMemorial(memorial: Memorial): Result<String>
    suspend fun getMemorialList(userId: String): Flow<List<Memorial>>
    suspend fun getMemorial(memorialId: String): Memorial?
    suspend fun updateMemorial(memorial: Memorial): Result<Unit>
    suspend fun deleteMemorial(memorialId: String): Result<Unit>
}

// feature-memorial/data/MemorialRepositoryImpl.kt
@Singleton
class MemorialRepositoryImpl @Inject constructor(
    private val remoteDataSource: MemorialRemoteDataSource,
    private val localDataSource: MemorialLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MemorialRepository {
    
    override suspend fun createMemorial(memorial: Memorial): Result<String> =
        withContext(ioDispatcher) {
            try {
                val memorialId = remoteDataSource.createMemorial(memorial)
                localDataSource.insertMemorial(memorial.copy(id = memorialId))
                Result.success(memorialId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun getMemorialList(userId: String): Flow<List<Memorial>> =
        flow {
            // Emit local data first
            emit(localDataSource.getMemorialList(userId))
            
            // Then sync with remote
            try {
                val remoteMemorials = remoteDataSource.getMemorialList(userId)
                localDataSource.updateMemorialList(remoteMemorials)
                emit(remoteMemorials)
            } catch (e: Exception) {
                // Continue with local data if remote fails
                emit(localDataSource.getMemorialList(userId))
            }
        }
}
```

---

## 🎨 Material Design 3 Theme Implementation

### Theme Configuration

```kotlin
// core-ui/theme/Color.kt
val IslamicGreen = Color(0xFF1B4332)
val IslamicGold = Color(0xFFD4AF37)
val IslamicCream = Color(0xFFF5F5DC)

val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    tertiary = IslamicCream,
    surface = Color.White,
    onSurface = Color.Black,
    background = Color.White,
    onBackground = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    tertiary = IslamicCream,
    surface = Color(0xFF1C1C1C),
    onSurface = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White
)

// core-ui/theme/Typography.kt
val IslamicTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default, // Replace with font_lpmq_isep_misbah font
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Replace with font_lpmq_isep_misbah
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

// core-ui/theme/Theme.kt
@Composable
fun TahlilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = IslamicTypography,
        content = content
    )
}
```

---

## 🧪 Testing Strategy

### Module-Specific Testing

```kotlin
// feature-tasbeeh/src/test/TasbeehViewModelTest.kt
@HiltAndroidTest
class TasbeehViewModelTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Test
    fun `when incrementing counter, state updates correctly`() = runTest {
        // Test implementation
    }
}

// core-ui/src/test/TahlilBottomNavigationTest.kt
@Test
fun tahlilBottomNavigation_showsAllTabs() {
    composeTestRule.setContent {
        TahlilTheme {
            TahlilBottomNavigation(
                navController = rememberNavController(),
                items = testBottomNavItems
            )
        }
    }
    
    composeTestRule.onNodeWithText("Tasbeeh").assertIsDisplayed()
    composeTestRule.onNodeWithText("Memorial").assertIsDisplayed()
    composeTestRule.onNodeWithText("Community").assertIsDisplayed()
    composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
}
```

---

## 📱 Module Build Configuration

### App Module build.gradle

```kotlin
// app/build.gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'dagger.hilt.android.plugin'
    id 'kotlin-kapt'
    id 'com.google.gms.google-services'
}

android {
    compileSdk 35
    
    defaultConfig {
        applicationId "com.app_muslim.surah_yasin"
        minSdk 24
        targetSdk 35
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation project(':core:core-ui')
    implementation project(':core:core-data')
    implementation project(':core:core-firebase')
    implementation project(':core:core-common')
    implementation project(':feature:feature-tasbeeh')
    implementation project(':feature:feature-memorial')
    implementation project(':feature:feature-community')
    implementation project(':feature:feature-auth')
    implementation project(':shared:shared-preferences')
    implementation project(':shared:shared-analytics')
    
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose:1.8.2'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    
    // Hilt
    implementation 'com.google.dagger:hilt-android:2.50'
    kapt 'com.google.dagger:hilt-compiler:2.50'
    implementation 'androidx.hilt:hilt-navigation-compose:1.1.0'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:33.1.2')
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-crashlytics'
}
```

### Core Module build.gradle

```kotlin
// core/core-ui/build.gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'dagger.hilt.android.plugin'
    id 'kotlin-kapt'
}

android {
    compileSdk 35
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation project(':core:core-common')
    
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    
    // Hilt
    implementation 'com.google.dagger:hilt-android:2.50'
    kapt 'com.google.dagger:hilt-compiler:2.50'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
```

---

## ✅ Migration Checklist

### Phase 1: Foundation Setup
- [ ] Create modular project structure
- [ ] Setup Single Activity with MainActivity
- [ ] Implement Bottom Navigation with Navigation Component
- [ ] Configure Jetpack Compose BOM and Material Design 3
- [ ] Setup Hilt DI to replace Koin
- [ ] Create core modules (:core-ui, :core-data, :core-firebase, :core-common)

### Phase 2: Feature Module Migration
- [ ] Migrate existing Tasbeeh functionality to :feature-tasbeeh
- [ ] Create :feature-memorial for new Firebase features
- [ ] Implement :feature-auth for authentication
- [ ] Setup :feature-community for social features
- [ ] Migrate existing UI to Jetpack Compose screens

### Phase 3: Integration & Testing
- [ ] Integrate all feature modules with Single Activity navigation
- [ ] Implement Repository pattern across all data sources
- [ ] Setup comprehensive testing for each module
- [ ] Performance optimization and memory management
- [ ] Final integration testing and UI/UX validation

### Phase 4: Production Readiness
- [ ] ProGuard/R8 configuration for modular architecture
- [ ] App Bundle configuration for dynamic feature delivery
- [ ] Baseline Profiles for startup optimization
- [ ] Final security review and penetration testing
- [ ] Documentation and team training completion

---

This modular architecture provides a scalable, maintainable foundation for the Tahlil platform while leveraging modern Android development practices and ensuring optimal performance and user experience.