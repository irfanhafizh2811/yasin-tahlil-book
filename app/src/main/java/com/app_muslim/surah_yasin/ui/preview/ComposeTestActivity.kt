package com.app_muslim.surah_yasin.ui.preview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.ui.memorial.MemorialDetailScreen
import com.app_muslim.surah_yasin.ui.memorial.ArabicTextDisplay
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme as IslamicTheme // Alias for backward compatibility
import com.app_muslim.surah_yasin.core.ui.components.TahlilBottomNavigationPreview
import com.app_muslim.surah_yasin.core.ui.islamic.IslamicCard
import com.app_muslim.surah_yasin.core.ui.islamic.IslamicPrayerCard
import com.app_muslim.surah_yasin.core.ui.islamic.CompletePrayerDisplay
import com.app_muslim.surah_yasin.core.ui.islamic.ArabicPrayerText
import com.app_muslim.surah_yasin.feature.auth.ui.LoginScreenPreview
import com.app_muslim.surah_yasin.feature.auth.ui.RegisterScreenPreview
import com.app_muslim.surah_yasin.feature.auth.ui.AuthScreenPreview
import com.app_muslim.surah_yasin.feature.auth.ui.AuthFlow
import com.app_muslim.surah_yasin.feature.auth.model.AuthUiState
// Preview imports commented out due to compilation issues - will use basic components instead
// import com.app_muslim.surah_yasin.feature.auth.ui.AuthScreenPreview
// import com.app_muslim.surah_yasin.feature.auth.ui.CulturalSetupScreenPreview
// import com.app_muslim.surah_yasin.feature.auth.ui.ForgotPasswordScreenPreview
// import com.app_muslim.surah_yasin.feature.memorial.ui.CreateMemorialScreenPreview
// import com.app_muslim.surah_yasin.feature.memorial.ui.list.MemorialListScreenPreview
// import com.app_muslim.surah_yasin.feature.memorial.ui.edit.EditMemorialScreenPreview
// import com.app_muslim.surah_yasin.feature.memorial.ui.prayer.CommunityPrayerScreenPreview

/**
 * Test Activity for previewing Compose screens on actual device
 * 
 * To use this activity:
 * 1. Add to AndroidManifest.xml:
 *    <activity
 *        android:name=".ui.preview.ComposeTestActivity"
 *        android:exported="true"
 *        android:theme="@style/Theme.MaterialComponents.DayNight.NoActionBar">
 *        <intent-filter>
 *            <action android:name="android.intent.action.MAIN" />
 *            <category android:name="android.intent.category.LAUNCHER" />
 *        </intent-filter>
 *    </activity>
 * 
 * 2. Build and install: ./gradlew assembleDebug && adb install app/build/outputs/apk/debug/app-debug.apk
 * 3. Launch on your phone to test screens
 */
@OptIn(ExperimentalMaterial3Api::class)
class ComposeTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TahlilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComposeTestApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeTestApp() {
    var selectedScreen by remember { mutableStateOf("menu") }
    
    when (selectedScreen) {
        "menu" -> ScreenSelectionMenu(
            onScreenSelected = { selectedScreen = it }
        )
        "memorial_empty" -> TestScreenWrapper(
            title = "Memorial - Empty",
            onBack = { selectedScreen = "menu" }
        ) {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }
        
        "memorial_loading" -> TestScreenWrapper(
            title = "Memorial - Loading",
            onBack = { selectedScreen = "menu" }
        ) {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = true,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }
        
        "memorial_data" -> TestScreenWrapper(
            title = "Memorial - With Data",
            onBack = { selectedScreen = "menu" }
        ) {
            MemorialScreen(
                memorials = getSampleMemorials(),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }
        
        "memorial_detail" -> TestScreenWrapper(
            title = "Memorial Detail",
            onBack = { selectedScreen = "menu" }
        ) {
            MemorialDetailScreen(
                memorial = getSampleMemorials().first(),
                onPrayerIncrement = {},
                onNavigateBack = { selectedScreen = "menu" }
            )
        }
        
        "arabic_text" -> TestScreenWrapper(
            title = "Arabic Text",
            onBack = { selectedScreen = "menu" }
        ) {
            Column {
                ArabicTextDisplay(
                    text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem"
                )
                ArabicTextDisplay(
                    text = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
                    transliteration = "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad"
                )
                ArabicTextDisplay(
                    text = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Rabbana aatina fi'd-dunya hasanatan wa fi'l-aakhirati hasanatan wa qina 'adhaab an-naar"
                )
            }
        }
        
        "bottom_navigation" -> TestScreenWrapper(
            title = "Bottom Navigation",
            onBack = { selectedScreen = "menu" }
        ) {
            TahlilBottomNavigationPreview()
        }
        
        "islamic_cards" -> TestScreenWrapper(
            title = "Islamic Cards",
            onBack = { selectedScreen = "menu" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IslamicCard(
                    title = "Memorial Prayer",
                    subtitle = "Tahlil for the Deceased",
                    onClick = {}
                )
                IslamicPrayerCard(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful",
                    prayerCount = 45,
                    onClick = {}
                )
            }
        }
        
        "login_screen" -> TestScreenWrapper(
            title = "Login Screen",
            onBack = { selectedScreen = "menu" }
        ) {
            LoginScreenPreview()
        }
        
        "register_screen" -> TestScreenWrapper(
            title = "Register Screen", 
            onBack = { selectedScreen = "menu" }
        ) {
            RegisterScreenPreview()
        }
        
        "auth_screen_sign_in" -> TestScreenWrapper(
            title = "Auth - Sign In",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.SIGN_IN,
                uiState = AuthUiState(),
                hasData = false
            )
        }
        
        "auth_screen_sign_up" -> TestScreenWrapper(
            title = "Auth - Sign Up",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.SIGN_UP,
                uiState = AuthUiState(),
                hasData = false
            )
        }
        
        "auth_screen_social" -> TestScreenWrapper(
            title = "Auth - Social Sign In",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.SOCIAL_AUTH,
                uiState = AuthUiState()
            )
        }
        
        "auth_screen_phone" -> TestScreenWrapper(
            title = "Auth - Phone Verification",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.PHONE_AUTH,
                uiState = AuthUiState(),
                hasData = true
            )
        }
        
        "auth_screen_biometric" -> TestScreenWrapper(
            title = "Auth - Biometric",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.BIOMETRIC_AUTH,
                uiState = AuthUiState()
            )
        }
        
        "auth_screen_loading" -> TestScreenWrapper(
            title = "Auth - Loading State",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.SIGN_IN,
                uiState = AuthUiState(isLoading = true),
                hasData = true
            )
        }
        
        "auth_screen_error" -> TestScreenWrapper(
            title = "Auth - Error State",
            onBack = { selectedScreen = "menu" }
        ) {
            AuthScreenPreview(
                authFlow = AuthFlow.SIGN_IN,
                uiState = AuthUiState(errorMessage = "Invalid email or password. Please check your credentials and try again.")
            )
        }
        
        "cultural_setup" -> TestScreenWrapper(
            title = "Cultural Setup",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Cultural Setup", "Islamic cultural preferences and regional settings")
        }
        
        "forgot_password" -> TestScreenWrapper(
            title = "Forgot Password",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Forgot Password", "Password recovery flow with email verification")
        }
        
        "create_memorial" -> TestScreenWrapper(
            title = "Create Memorial",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Create Memorial", "Memorial creation form with Islamic traditions")
        }
        
        "memorial_list" -> TestScreenWrapper(
            title = "Memorial List",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Memorial List", "List of all memorials with filtering and sorting")
        }
        
        "edit_memorial" -> TestScreenWrapper(
            title = "Edit Memorial",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Edit Memorial", "Memorial editing functionality with validation")
        }
        
        "community_prayer" -> TestScreenWrapper(
            title = "Community Prayer",
            onBack = { selectedScreen = "menu" }
        ) {
            PreviewPlaceholder("Community Prayer", "Community prayer participation and tracking")
        }
        
        "arabic_components" -> TestScreenWrapper(
            title = "Arabic Components",
            onBack = { selectedScreen = "menu" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ArabicPrayerText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم"
                )
                CompletePrayerDisplay(
                    arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
                    transliteration = "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad",
                    translation = "O Allah, send prayers upon Muhammad and the family of Muhammad"
                )
            }
        }
    }
}

@Composable
fun ScreenSelectionMenu(
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎨 Compose Screen Tester",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Select a screen to test:",
            style = MaterialTheme.typography.titleMedium
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getTestScreens()) { screen ->
                Card(
                    onClick = { onScreenSelected(screen.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = screen.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "📱 Tips for Testing:",
            style = MaterialTheme.typography.titleMedium
        )
        
        Text(
            text = "• Rotate device to test landscape\n• Try dark/light theme in device settings\n• Test with different font sizes\n• Check RTL languages (Arabic)\n• Test touch interactions",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreenWrapper(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun PreviewPlaceholder(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚧",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "This screen preview is available in the full component implementation.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ============================================================================
// SAMPLE DATA
// ============================================================================

fun getSampleMemorials(): List<Memorial> {
    return listOf(
        Memorial(
            id = "1",
            title = "Loving Memory of Grandfather",
            deceasedName = "Ahmad bin Abdullah",
            totalPrayers = 45,
            description = "A wonderful grandfather who taught us about Islam and life"
        ),
        Memorial(
            id = "2", 
            title = "In Memory of Our Beloved Mother",
            deceasedName = "Fatimah bint Hassan",
            totalPrayers = 123,
            description = "The most caring mother and devoted Muslim woman"
        ),
        Memorial(
            id = "3",
            title = "Remembering Uncle Yusuf",
            deceasedName = "Yusuf ibn Omar",
            totalPrayers = 67,
            description = "A generous and kind uncle who helped many families"
        ),
        Memorial(
            id = "4",
            title = "للجدة الحبيبة خديجة", // Arabic title
            deceasedName = "Khadijah bint Ali",
            totalPrayers = 234,
            description = "جدة محبوبة علمتنا القرآن والدين" // Arabic description
        ),
        Memorial(
            id = "5",
            title = "Memory of Brother Hassan",
            deceasedName = "Hassan ibn Muhammad",
            totalPrayers = 89,
            description = "A brother who always helped others and loved Allah"
        )
    )
}

data class TestScreen(
    val id: String,
    val title: String,
    val description: String
)

fun getTestScreens(): List<TestScreen> {
    return listOf(
        TestScreen(
            id = "memorial_empty",
            title = "Memorial Screen - Empty",
            description = "Test empty state with no memorials"
        ),
        TestScreen(
            id = "memorial_loading", 
            title = "Memorial Screen - Loading",
            description = "Test loading state with spinner"
        ),
        TestScreen(
            id = "memorial_data",
            title = "Memorial Screen - With Data",
            description = "Test screen with multiple memorial cards"
        ),
        TestScreen(
            id = "memorial_detail",
            title = "Memorial Detail Screen", 
            description = "Test individual memorial detail view"
        ),
        TestScreen(
            id = "arabic_text",
            title = "Arabic Text Display",
            description = "Test Arabic text rendering and RTL layout"
        ),
        TestScreen(
            id = "bottom_navigation",
            title = "Bottom Navigation",
            description = "Test bottom navigation with different selected states"
        ),
        TestScreen(
            id = "islamic_cards",
            title = "Islamic UI Components",
            description = "Test Islamic cards with Arabic text and prayer counts"
        ),
        TestScreen(
            id = "login_screen",
            title = "Login Screen",
            description = "Test authentication login screen with form validation"
        ),
        TestScreen(
            id = "register_screen",
            title = "Register Screen",
            description = "Test user registration screen with cultural setup"
        ),
        TestScreen(
            id = "auth_screen_sign_in",
            title = "Auth - Sign In Flow",
            description = "Test enhanced sign in flow with Islamic theme"
        ),
        TestScreen(
            id = "auth_screen_sign_up", 
            title = "Auth - Sign Up Flow",
            description = "Test enhanced sign up flow with validation"
        ),
        TestScreen(
            id = "auth_screen_social",
            title = "Auth - Social Sign In",
            description = "Test social authentication options (Google, Apple, etc.)"
        ),
        TestScreen(
            id = "auth_screen_phone",
            title = "Auth - Phone Verification",
            description = "Test phone number authentication and verification"
        ),
        TestScreen(
            id = "auth_screen_biometric",
            title = "Auth - Biometric Authentication",
            description = "Test biometric authentication (fingerprint, face ID)"
        ),
        TestScreen(
            id = "auth_screen_loading",
            title = "Auth - Loading State",
            description = "Test authentication loading and processing states"
        ),
        TestScreen(
            id = "auth_screen_error",
            title = "Auth - Error State",
            description = "Test authentication error handling and display"
        ),
        TestScreen(
            id = "cultural_setup",
            title = "Cultural Setup Screen",
            description = "Test Islamic cultural preferences and setup"
        ),
        TestScreen(
            id = "forgot_password",
            title = "Forgot Password Screen",
            description = "Test password recovery flow and validation"
        ),
        TestScreen(
            id = "create_memorial",
            title = "Create Memorial Screen",
            description = "Test memorial creation form with validation"
        ),
        TestScreen(
            id = "memorial_list",
            title = "Memorial List Screen",
            description = "Test memorial list with different states"
        ),
        TestScreen(
            id = "edit_memorial",
            title = "Edit Memorial Screen",
            description = "Test memorial editing functionality"
        ),
        TestScreen(
            id = "community_prayer",
            title = "Community Prayer Screen",
            description = "Test community prayer participation features"
        ),
        TestScreen(
            id = "arabic_components",
            title = "Advanced Arabic Components",
            description = "Test Arabic prayer text and complete prayer displays"
        )
    )
}