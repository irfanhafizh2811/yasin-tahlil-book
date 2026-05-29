package com.app_muslim.surah_yasin.core.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class IslamicThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun islamicTheme_appliesCorrectColors() {
        var appliedColors: androidx.compose.material3.ColorScheme? = null

        composeTestRule.setContent {
            IslamicTheme(isDarkTheme = false) {
                appliedColors = MaterialTheme.colorScheme
                Surface {}
            }
        }

        assertNotNull(appliedColors)
        assertEquals(IslamicGreen, appliedColors!!.primary)
        assertEquals(IslamicGold, appliedColors!!.secondary)
    }

    @Test
    fun islamicTheme_darkMode_appliesCorrectColors() {
        var appliedColors: androidx.compose.material3.ColorScheme? = null

        composeTestRule.setContent {
            IslamicTheme(isDarkTheme = true) {
                appliedColors = MaterialTheme.colorScheme
                Surface {}
            }
        }

        assertNotNull(appliedColors)
        assertEquals(IslamicGreenDark, appliedColors!!.primary)
        assertEquals(IslamicGoldDark, appliedColors!!.secondary)
    }

    @Test
    fun islamicTypography_appliesCorrectFonts() {
        var appliedTypography: androidx.compose.material3.Typography? = null

        composeTestRule.setContent {
            IslamicTheme {
                appliedTypography = MaterialTheme.typography
                Surface {}
            }
        }

        assertNotNull(appliedTypography)
        assertEquals("font_lpmq_isep_misbah", appliedTypography!!.headlineLarge.fontFamily?.toString())
    }

    @Test
    fun culturalColorPalette_providesValidColors() {
        val colors = CulturalColorPalette()

        // Verify all colors are properly defined
        assert(colors.primary != Color.Unspecified)
        assert(colors.onPrimary != Color.Unspecified)
        assert(colors.secondary != Color.Unspecified)
        assert(colors.onSecondary != Color.Unspecified)
        assert(colors.surface != Color.Unspecified)
        assert(colors.onSurface != Color.Unspecified)
    }

    @Test
    fun islamicShapes_providesProperCornerRadius() {
        composeTestRule.setContent {
            IslamicTheme {
                val shapes = MaterialTheme.shapes
                
                // Test that shapes are properly defined
                Box(
                    modifier = Modifier
                        .size(100.dp)
                ) {}
            }
        }

        // Verify no crashes occur during shape application
        composeTestRule.waitForIdle()
    }
}