package com.app_muslim.surah_yasin.ui.memorial

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.MainActivity
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MemorialScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun memorialScreen_displaysEmptyState_whenNoMemorials() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        composeTestRule
            .onNodeWithText("No memorials yet")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Create your first memorial")
            .assertIsDisplayed()
    }

    @Test
    fun memorialScreen_displaysMemorials_whenMemorialsExist() {
        val testMemorials = listOf(
            Memorial(
                id = "1",
                title = "John's Memorial",
                deceasedName = "John Doe",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 15
            ),
            Memorial(
                id = "2", 
                title = "Mary's Memorial",
                deceasedName = "Mary Smith",
                createdAt = System.currentTimeMillis(),
                totalPrayers = 8
            )
        )

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        composeTestRule
            .onNodeWithText("John's Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mary's Memorial") 
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("15 prayers")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("8 prayers")
            .assertIsDisplayed()
    }

    @Test
    fun memorialScreen_showsLoadingState() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = true,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Loading memorials")
            .assertIsDisplayed()
    }

    @Test
    fun memorialCard_clickTriggersCallback() {
        val testMemorial = Memorial(
            id = "1",
            title = "Test Memorial",
            deceasedName = "Test Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 5
        )

        var clickedMemorial: Memorial? = null

        composeTestRule.setContent {
            MemorialCard(
                memorial = testMemorial,
                onMemorialClick = { clickedMemorial = it },
                onPrayClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Test Memorial")
            .performClick()

        assert(clickedMemorial == testMemorial)
    }

    @Test
    fun prayButton_clickTriggersCallback() {
        val testMemorial = Memorial(
            id = "1",
            title = "Test Memorial",
            deceasedName = "Test Person",
            createdAt = System.currentTimeMillis(),
            totalPrayers = 5
        )

        var prayedForMemorial: Memorial? = null

        composeTestRule.setContent {
            MemorialCard(
                memorial = testMemorial,
                onMemorialClick = {},
                onPrayClick = { prayedForMemorial = it }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Pray for ${testMemorial.deceasedName}")
            .performClick()

        assert(prayedForMemorial == testMemorial)
    }

    @Test
    fun createMemorialFab_isDisplayed() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun createMemorialFab_clickTriggersCallback() {
        var createClicked = false

        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = false,
                onCreateMemorial = { createClicked = true },
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .performClick()

        assert(createClicked)
    }

    @Test
    fun memorialScreen_supportsScroll_withManyMemorials() {
        val manyMemorials = (1..20).map { index ->
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
                memorials = manyMemorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Test that first item is visible
        composeTestRule
            .onNodeWithText("Memorial 1")
            .assertIsDisplayed()

        // Scroll to bottom
        composeTestRule
            .onNodeWithText("Memorial 20")
            .performScrollTo()
            .assertIsDisplayed()
    }
}