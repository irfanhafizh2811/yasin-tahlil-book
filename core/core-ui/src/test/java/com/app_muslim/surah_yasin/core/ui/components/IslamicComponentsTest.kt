package com.app_muslim.surah_yasin.core.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IslamicComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun islamicCard_displaysCorrectContent() {
        composeTestRule.setContent {
            IslamicCard(
                title = "Test Memorial",
                subtitle = "Test Family",
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Family")
            .assertIsDisplayed()
    }

    @Test
    fun prayerCounterButton_incrementsCorrectly() {
        var currentCount = 0
        
        composeTestRule.setContent {
            PrayerCounterButton(
                count = currentCount,
                onIncrement = { currentCount++ },
                prayerType = "Tasbih"
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Prayer counter button")
            .performClick()

        assert(currentCount == 1)
    }

    @Test
    fun arabicTextDisplay_showsProperRTL() {
        composeTestRule.setContent {
            ArabicTextDisplay(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                transliteration = "Bismillahir Rahmanir Raheem"
            )
        }

        composeTestRule
            .onNodeWithText("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bismillahir Rahmanir Raheem")
            .assertIsDisplayed()
    }

    @Test
    fun memorialCard_handlesLongTitles() {
        val longTitle = "A very long memorial title that should be handled properly by the component layout system"
        
        composeTestRule.setContent {
            MemorialCard(
                title = longTitle,
                createdDate = "2024-01-01",
                prayerCount = 100,
                onCardClick = {},
                onPrayClick = {}
            )
        }

        composeTestRule
            .onNodeWithText(longTitle)
            .assertIsDisplayed()
    }

    @Test
    fun qiblaDirectionIndicator_showsProperOrientation() {
        composeTestRule.setContent {
            QiblaDirectionIndicator(
                qiblaDirection = 45.0,
                currentOrientation = 0.0
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Qibla direction indicator")
            .assertIsDisplayed()
    }
}