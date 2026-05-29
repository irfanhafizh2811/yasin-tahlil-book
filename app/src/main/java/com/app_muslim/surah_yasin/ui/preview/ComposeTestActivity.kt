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
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.ui.memorial.MemorialDetailScreen
import com.app_muslim.surah_yasin.ui.memorial.ArabicTextDisplay
import com.app_muslim.surah_yasin.ui.theme.IslamicTheme

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
            IslamicTheme {
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
        )
    )
}