package com.app_muslim.surah_yasin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.MainActivity
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.ui.memorial.MemorialDetailScreen
import com.app_muslim.surah_yasin.ui.memorial.ArabicTextDisplay
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MemorialPerformanceBenchmark {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val benchmarkRule = BenchmarkRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val largeMemorialList = (1..1000).map { index ->
        Memorial(
            id = index.toString(),
            title = "Memorial $index",
            deceasedName = "Person $index",
            createdAt = System.currentTimeMillis() - (index * 1000),
            totalPrayers = index * 5
        )
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun benchmark_memorialListRender() {
        benchmarkRule.measureRepeated {
            composeTestRule.setContent {
                MemorialScreen(
                    memorials = largeMemorialList,
                    isLoading = false,
                    onCreateMemorial = {},
                    onMemorialClick = {},
                    onPrayForMemorial = {}
                )
            }

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun benchmark_memorialListScroll() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = largeMemorialList,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        benchmarkRule.measureRepeated {
            // Scroll to bottom
            composeTestRule
                .onNodeWithText("Memorial 1000")
                .performScrollTo()

            // Scroll back to top
            composeTestRule
                .onNodeWithText("Memorial 1")
                .performScrollTo()
        }
    }

    @Test
    fun benchmark_prayerCounterIncrement() {
        val testMemorial = Memorial(
            id = "1",
            title = "Benchmark Memorial",
            deceasedName = "Benchmark Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 0
        )

        composeTestRule.setContent {
            MemorialDetailScreen(
                memorial = testMemorial,
                onPrayerIncrement = {},
                onNavigateBack = {}
            )
        }

        benchmarkRule.measureRepeated {
            repeat(100) {
                composeTestRule
                    .onNodeWithContentDescription("Pray for ${testMemorial.deceasedName}")
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun benchmark_searchPerformance() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = largeMemorialList,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        benchmarkRule.measureRepeated {
            // Open search
            composeTestRule
                .onNodeWithContentDescription("Search memorials")
                .performClick()

            // Type search query
            composeTestRule
                .onNodeWithText("Search memorials...")
                .performTextInput("Memorial 500")

            composeTestRule.waitForIdle()

            // Clear search
            composeTestRule
                .onNodeWithContentDescription("Clear search")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun benchmark_navigationPerformance() {
        benchmarkRule.measureRepeated {
            // Navigate to Community tab
            composeTestRule
                .onNodeWithText("Community")
                .performClick()

            composeTestRule.waitForIdle()

            // Navigate to Tasbeeh tab  
            composeTestRule
                .onNodeWithText("Tasbeeh")
                .performClick()

            composeTestRule.waitForIdle()

            // Navigate back to Memorials
            composeTestRule
                .onNodeWithText("Memorials")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun benchmark_memorialCreationFlow() {
        benchmarkRule.measureRepeated {
            // Open create memorial
            composeTestRule
                .onNodeWithContentDescription("Create memorial")
                .performClick()

            composeTestRule.waitForIdle()

            // Fill form
            composeTestRule
                .onNodeWithText("Memorial title")
                .performTextInput("Benchmark Memorial")

            composeTestRule
                .onNodeWithText("Deceased person's name")
                .performTextInput("Benchmark Person")

            composeTestRule.waitForIdle()

            // Navigate back without creating
            composeTestRule
                .onNodeWithContentDescription("Navigate back")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun benchmark_themeSwitch() {
        benchmarkRule.measureRepeated {
            // Open settings
            composeTestRule
                .onNodeWithText("Profile")
                .performClick()

            composeTestRule
                .onNodeWithText("Settings")
                .performClick()

            // Toggle dark mode
            composeTestRule
                .onNodeWithText("Dark mode")
                .performClick()

            composeTestRule.waitForIdle()

            // Toggle back to light mode
            composeTestRule
                .onNodeWithText("Dark mode")
                .performClick()

            composeTestRule.waitForIdle()

            // Navigate back
            composeTestRule
                .onNodeWithContentDescription("Navigate back")
                .performClick()

            composeTestRule
                .onNodeWithContentDescription("Navigate back")
                .performClick()
        }
    }

    @Test
    fun benchmark_arabicTextRendering() {
        val arabicTexts = listOf(
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِين",
            "الرَّحْمَنِ الرَّحِيم",
            "مَالِكِ يَوْمِ الدِّين",
            "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِين"
        )

        benchmarkRule.measureRepeated {
            arabicTexts.forEach { arabicText ->
                composeTestRule.setContent {
                    ArabicTextDisplay(
                        text = arabicText,
                        transliteration = "Transliteration"
                    )
                }

                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun benchmark_firebaseDataLoad() {
        benchmarkRule.measureRepeated {
            // Trigger data refresh from Firebase
            composeTestRule
                .onNodeWithContentDescription("Refresh memorials")
                .performClick()

            // Wait for network operation to complete
            composeTestRule.waitForIdle()
        }
    }
}