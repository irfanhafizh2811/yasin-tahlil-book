package com.app_muslim.surah_yasin.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemorialIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun endToEnd_createMemorial_andPrayForIt() {
        var createMemorialClicked = false
        var prayedForMemorial: Memorial? = null
        
        val testMemorials = listOf(
            Memorial(
                id = "1",
                title = "Test Memorial",
                deceasedName = "John Doe",
                totalPrayers = 0
            )
        )

        composeTestRule.setContent {
            MemorialScreen(
                memorials = testMemorials,
                isLoading = false,
                onCreateMemorial = { createMemorialClicked = true },
                onMemorialClick = {},
                onPrayForMemorial = { memorial -> prayedForMemorial = memorial }
            )
        }

        // Should display the memorial
        composeTestRule
            .onNodeWithText("Test Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("John Doe")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("0 prayers")
            .assertIsDisplayed()

        // Click pray button
        composeTestRule
            .onNodeWithContentDescription("Pray for John Doe")
            .performClick()

        // Verify prayer callback was triggered
        assert(prayedForMemorial?.id == "1")

        // Test create memorial FAB
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .performClick()

        assert(createMemorialClicked)
    }

    @Test
    fun endToEnd_searchMemorials() {
        val memorials = listOf(
            Memorial(id = "1", title = "John's Memorial", deceasedName = "John Smith", totalPrayers = 10),
            Memorial(id = "2", title = "Mary's Memorial", deceasedName = "Mary Johnson", totalPrayers = 5),
            Memorial(id = "3", title = "Ahmed's Memorial", deceasedName = "Ahmed Ali", totalPrayers = 15)
        )

        composeTestRule.setContent {
            MemorialScreen(
                memorials = memorials,
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // All memorials should be visible initially
        composeTestRule
            .onNodeWithText("John's Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mary's Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ahmed's Memorial")
            .assertIsDisplayed()

        // Search functionality exists
        composeTestRule
            .onNodeWithText("Search memorials...")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Search memorials")
            .assertIsDisplayed()
    }

    @Test
    fun endToEnd_emptyState() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Should show empty state
        composeTestRule
            .onNodeWithText("No memorials yet")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Create your first memorial")
            .assertIsDisplayed()

        // Create memorial button should still be available
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun endToEnd_loadingState() {
        composeTestRule.setContent {
            MemorialScreen(
                memorials = emptyList(),
                isLoading = true,
                onCreateMemorial = {},
                onMemorialClick = {},
                onPrayForMemorial = {}
            )
        }

        // Should show loading indicator
        composeTestRule
            .onNodeWithContentDescription("Loading memorials")
            .assertIsDisplayed()
    }

    @Test
    fun endToEnd_memorialInteraction() {
        var clickedMemorial: Memorial? = null
        
        val memorial = Memorial(
            id = "1",
            title = "Interactive Memorial",
            deceasedName = "Test Person",
            totalPrayers = 25
        )

        composeTestRule.setContent {
            MemorialScreen(
                memorials = listOf(memorial),
                isLoading = false,
                onCreateMemorial = {},
                onMemorialClick = { clickedMemorial = it },
                onPrayForMemorial = {}
            )
        }

        // Click on the memorial card
        composeTestRule
            .onNodeWithText("Interactive Memorial")
            .performClick()

        // Verify memorial click callback
        assert(clickedMemorial?.id == "1")
        assert(clickedMemorial?.title == "Interactive Memorial")
    }
}