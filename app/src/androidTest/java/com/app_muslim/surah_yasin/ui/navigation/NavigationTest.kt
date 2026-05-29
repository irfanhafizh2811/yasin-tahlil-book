package com.app_muslim.surah_yasin.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.MainActivity
import com.app_muslim.surah_yasin.ui.navigation.TahlilBottomNavigation
import com.app_muslim.surah_yasin.ui.navigation.TahlilNavHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun bottomNavigation_allTabsAreDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TahlilBottomNavigation(navController = navController)
        }

        composeTestRule
            .onNodeWithText("Memorials")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Community")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Tasbeeh")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_tapChangesSelectedTab() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TahlilBottomNavigation(navController = navController)
        }

        // Default tab should be selected (Memorials)
        composeTestRule
            .onNodeWithText("Memorials")
            .assertIsSelected()

        // Tap Community tab
        composeTestRule
            .onNodeWithText("Community")
            .performClick()

        composeTestRule
            .onNodeWithText("Community")
            .assertIsSelected()

        composeTestRule
            .onNodeWithText("Memorials")
            .assertIsNotSelected()
    }

    @Test
    fun navigation_memorialsToCreateMemorial() {
        composeTestRule.onRoot().performClick()

        // Start from memorials screen
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .performClick()

        // Should navigate to create memorial screen
        composeTestRule
            .onNodeWithText("Create Memorial")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_memorialDetailNavigatesBack() {
        // Simulate navigation to memorial detail
        composeTestRule.onRoot().performClick()

        // Click on a memorial (assuming one exists)
        composeTestRule
            .onAllNodesWithContentDescription("Memorial card")
            .onFirst()
            .performClick()

        // Should show memorial detail screen
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .assertIsDisplayed()

        // Click back button
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()

        // Should return to memorials list
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_deepLinking_worksCorrectly() {
        // Test deep linking to specific memorial
        composeTestRule.setContent {
            val navController = rememberNavController()
            TahlilNavHost(
                navController = navController,
                startDestination = "memorial_detail/test-memorial-id"
            )
        }

        // Should directly show memorial detail
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_handlesAuthenticationFlow() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TahlilNavHost(
                navController = navController,
                startDestination = "auth"
            )
        }

        // Should show authentication screen
        composeTestRule
            .onNodeWithText("Sign In")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Create Account")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_preservesState_betweenTabs() {
        composeTestRule.onRoot().performClick()

        // Navigate to Community tab
        composeTestRule
            .onNodeWithText("Community")
            .performClick()

        // Perform some action that changes state
        composeTestRule
            .onNodeWithContentDescription("Refresh community")
            .performClick()

        // Navigate back to Memorials
        composeTestRule
            .onNodeWithText("Memorials")
            .performClick()

        // Navigate back to Community
        composeTestRule
            .onNodeWithText("Community")
            .performClick()

        // State should be preserved (refreshed state should still be there)
        // This would require specific UI indicators to verify
    }

    @Test
    fun navigation_backButton_handledCorrectly() {
        composeTestRule.onRoot().performClick()

        // Navigate to create memorial
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .performClick()

        // Press system back button
        composeTestRule.activity.onBackPressed()

        // Should return to memorials screen
        composeTestRule
            .onNodeWithContentDescription("Create memorial")
            .assertIsDisplayed()
    }

    private fun SemanticsNodeInteraction.assertIsSelected(): SemanticsNodeInteraction {
        return assertIsSelected()
    }

    private fun SemanticsNodeInteraction.assertIsNotSelected(): SemanticsNodeInteraction {
        return assertIsNotSelected()
    }
}