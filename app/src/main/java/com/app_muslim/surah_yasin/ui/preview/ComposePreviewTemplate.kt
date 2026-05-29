package com.app_muslim.surah_yasin.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.ui.theme.IslamicTheme

/**
 * Template for adding Jetpack Compose Previews
 * 
 * Copy this template to any Compose screen file and customize:
 * 1. Change YourScreenName to actual screen name
 * 2. Add actual parameters for your screen
 * 3. Create sample data for preview
 * 4. Add different preview states (loading, empty, error, etc.)
 */

// ============================================================================
// TEMPLATE SCREEN (Replace with your actual screen)
// ============================================================================

@Composable
fun YourScreenName(
    title: String,
    isLoading: Boolean = false,
    onButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onButtonClick) {
                Text("Action Button")
            }
        }
    }
}

// ============================================================================
// PREVIEW DATA PROVIDERS (Optional - for complex data)
// ============================================================================

class SampleDataProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "Sample Title 1",
        "Very Long Sample Title That Might Wrap",
        "عنوان باللغة العربية", // Arabic title
        ""
    )
}

// ============================================================================
// BASIC PREVIEWS
// ============================================================================

@Preview(name = "Screen - Default State")
@Composable
fun PreviewYourScreenDefault() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Sample Title",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

@Preview(name = "Screen - Loading State")
@Composable
fun PreviewYourScreenLoading() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Loading...",
                isLoading = true,
                onButtonClick = {}
            )
        }
    }
}

@Preview(name = "Screen - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewYourScreenDark() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Dark Theme",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Screen - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewYourScreenLandscape() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Landscape Mode",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

@Preview(
    name = "Screen - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewYourScreenTablet() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Tablet View",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

// ============================================================================
// SMALL SCREEN PREVIEW
// ============================================================================

@Preview(
    name = "Screen - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewYourScreenSmallPhone() {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = "Small Phone",
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Screen - Various Titles")
@Composable
fun PreviewYourScreenDynamic(
    @PreviewParameter(SampleDataProvider::class) title: String
) {
    IslamicTheme {
        Surface {
            YourScreenName(
                title = title,
                isLoading = false,
                onButtonClick = {}
            )
        }
    }
}

/**
 * ============================================================================
 * CHECKLIST FOR ADDING PREVIEWS TO YOUR SCREENS:
 * ============================================================================
 * 
 * □ Add preview imports:
 *   - import androidx.compose.ui.tooling.preview.Preview
 *   - import androidx.compose.ui.tooling.preview.PreviewParameter
 *   - import androidx.compose.ui.tooling.preview.PreviewParameterProvider
 * 
 * □ Wrap your screen in IslamicTheme and Surface:
 *   IslamicTheme { Surface { YourScreen(...) } }
 * 
 * □ Create preview for different states:
 *   - Default/Normal state
 *   - Loading state
 *   - Empty state
 *   - Error state
 *   - Dark theme
 * 
 * □ Create device-specific previews:
 *   - Phone portrait/landscape
 *   - Tablet
 *   - Small screen devices
 * 
 * □ Add sample data:
 *   - Use realistic Islamic names/content
 *   - Test with Arabic text
 *   - Test edge cases (long text, empty text)
 * 
 * □ For phone testing:
 *   - Build APK: ./gradlew assembleDebug
 *   - Use ComposeTestActivity (see below)
 *   - Test on actual device for real performance
 */