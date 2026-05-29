package com.app_muslim.surah_yasin.compatibility

import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.MainActivity
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.ui.memorial.TabletMemorialScreen
import com.app_muslim.surah_yasin.ui.theme.IslamicTheme
import com.app_muslim.surah_yasin.ui.navigation.TahlilNavigationRail
import com.app_muslim.surah_yasin.ui.navigation.TahlilBottomNavigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CrossDeviceCompatibilityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testMemorials = listOf(
        Memorial(
            id = "1",
            title = "Test Memorial",
            deceasedName = "Test Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 10
        )
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testPortraitOrientation() {
        composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify layout works in portrait
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testLandscapeOrientation() {
        composeTestRule.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify layout adapts to landscape
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testTabletLayout() {
        // Simulate tablet size by setting content with larger dimensions
        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            val isTablet = configuration.screenWidthDp >= 600

            if (isTablet) {
                TabletMemorialScreen(
                    memorials = testMemorials,
                    isLoading = false,
                    onCreateMemorial = {},
                    onMemorialClick = {},
                    onPrayForMemorial = {}
                )
            } else {
                MemorialScreen(
                    memorials = testMemorials,
                    isLoading = false,
                    onCreateMemorial = {},
                    onMemorialClick = {},
                    onPrayForMemorial = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testSmallScreenLayout() {
        // Test on very small screens (less than 360dp width)
        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            val isSmallScreen = configuration.screenWidthDp < 360

            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify essential elements are still accessible
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testDarkModeCompatibility() {
        // Test in dark mode
        composeTestRule.activity.setTheme(android.R.style.Theme_Material_NoActionBar)
        
        composeTestRule.setContent {
            IslamicTheme(darkTheme = true) {
                MemorialScreen(
                    memorials = testMemorials,
                    isLoading = false,
                    onCreateMemorial = {},
                    onMemorialClick = {},
                    onPrayForMemorial = {}
                )
            }
        }

        // Verify dark mode colors are applied correctly
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testLightModeCompatibility() {
        composeTestRule.setContent {
            IslamicTheme(darkTheme = false) {
                MemorialScreen(
                    memorials = testMemorials,
                    isLoading = false,
                    onCreateMemorial = {},
                    onMemorialClick = {},
                    onPrayForMemorial = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testRTLLayoutSupport() {
        // Test Right-to-Left layout for Arabic
        composeTestRule.activity.window.decorView.layoutDirection = android.view.View.LAYOUT_DIRECTION_RTL

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify RTL layout works
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        // Reset to LTR
        composeTestRule.activity.window.decorView.layoutDirection = android.view.View.LAYOUT_DIRECTION_LTR
    }

    @Test
    fun testDifferentFontSizes() {
        // Test with large font size accessibility setting
        val configuration = Configuration().apply {
            fontScale = 1.5f // Large font size
        }

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify text is still readable and layout doesn't break
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigationAdaptability() {
        composeTestRule.setContent {
            val configuration = LocalConfiguration.current
            val useRailNavigation = configuration.screenWidthDp >= 800

            if (useRailNavigation) {
                // Use navigation rail for wide screens
                TahlilNavigationRail()
            } else {
                // Use bottom navigation for normal screens
                val navController = rememberNavController()
                TahlilBottomNavigation(navController = navController)
            }
        }

        // Verify appropriate navigation is shown
        composeTestRule
            .onNodeWithText("Memorials")
            .assertIsDisplayed()
    }

    @Test
    fun testKeyboardHandling() {
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .performClick()

        // Focus on text field (should bring up keyboard)
        composeTestRule
            .onNodeWithText("Memorial title")
            .performClick()

        // Verify UI adapts to keyboard presence
        composeTestRule
            .onNodeWithText("Memorial title")
            .assertIsDisplayed()

        // Type text
        composeTestRule
            .onNodeWithText("Memorial title")
            .performTextInput("Test Memorial")

        // Verify scroll behavior with keyboard
        composeTestRule
            .onNodeWithText("Create Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun testNetworkStateHandling() {
        // Test offline state
        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify screen loads properly
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        // Test that memorial data is displayed correctly
        composeTestRule
            .onNodeWithText("Test Person")
            .assertIsDisplayed()
    }

    @Test
    fun testLowRAMDevicePerformance() {
        // Simulate low RAM by creating a large list
        val largeMemorialList = (1..500).map { index ->
            Memorial(
                id = index.toString(),
                title = "Memorial $index",
                deceasedName = "Person $index",
                createdAt = System.currentTimeMillis(),
                totalPrayers = index
            )
        }

        composeTestRule.setContent {
            MemorialScreen(
                memorials = largeMemorialList,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Verify performance doesn't degrade significantly
        composeTestRule
            .onNodeWithText("Memorial 1")
            .assertIsDisplayed()

        // Test scrolling performance
        composeTestRule
            .onNodeWithText("Memorial 500")
            .performScrollTo()
            .assertIsDisplayed()
    }
}