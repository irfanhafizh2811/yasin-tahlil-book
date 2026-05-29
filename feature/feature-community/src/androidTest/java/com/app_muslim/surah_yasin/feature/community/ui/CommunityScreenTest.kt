package com.app_muslim.surah_yasin.feature.community.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.feature.community.ui.CommunityMemorial
import com.app_muslim.surah_yasin.feature.community.ui.PrayerLeaderboard
import com.app_muslim.surah_yasin.feature.community.ui.GlobalPrayerStatistics
import com.app_muslim.surah_yasin.feature.community.ui.LeaderboardFilter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommunityScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun communityScreen_displaysGlobalStatistics() {
        val testStats = GlobalPrayerStatistics(
            totalPrayers = 1_250_000,
            activePrayers = 856,
            recentMemorials = 245
        )

        composeTestRule.setContent {
            CommunityScreen(
                globalStats = testStats,
                recentMemorials = emptyList(),
                leaderboard = emptyList(),
                isLoading = false,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithText("1.25M")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Total Prayers")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("856")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Active Now")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("245")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("New Memorials")
            .assertIsDisplayed()
    }

    @Test
    fun communityScreen_displaysRecentMemorials() {
        val testMemorials = listOf(
            CommunityMemorial(
                id = "1",
                title = "John's Memorial",
                deceasedName = "John Doe",
                totalPrayers = 150,
                recentPrayers = 25,
                familyName = "Doe Family"
            ),
            CommunityMemorial(
                id = "2",
                title = "Mary's Memorial", 
                deceasedName = "Mary Smith",
                totalPrayers = 89,
                recentPrayers = 12,
                familyName = "Smith Family"
            )
        )

        composeTestRule.setContent {
            CommunityScreen(
                globalStats = GlobalPrayerStatistics(),
                recentMemorials = testMemorials,
                leaderboard = emptyList(),
                isLoading = false,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithText("John's Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mary's Memorial")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("150 prayers")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("89 prayers")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Doe Family")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Smith Family")
            .assertIsDisplayed()
    }

    @Test
    fun communityScreen_displaysLeaderboard() {
        val testLeaderboard = listOf(
            PrayerLeaderboard(
                userId = "1",
                displayName = "Ahmed Ali",
                totalPrayers = 2500,
                rank = 1,
                country = "Saudi Arabia"
            ),
            PrayerLeaderboard(
                userId = "2",
                displayName = "Fatima Hassan",
                totalPrayers = 2100,
                rank = 2,
                country = "Egypt"
            ),
            PrayerLeaderboard(
                userId = "3",
                displayName = "Muhammad Khan",
                totalPrayers = 1875,
                rank = 3,
                country = "Pakistan"
            )
        )

        composeTestRule.setContent {
            CommunityScreen(
                globalStats = GlobalPrayerStatistics(),
                recentMemorials = emptyList(),
                leaderboard = testLeaderboard,
                isLoading = false,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithText("Ahmed Ali")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Fatima Hassan")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Muhammad Khan")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("2500 prayers")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("#1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("🇸🇦")
            .assertIsDisplayed()
    }

    @Test
    fun memorialCard_handlesPrayerJoin() {
        val testMemorial = CommunityMemorial(
            id = "1",
            title = "Community Memorial",
            deceasedName = "Test Person",
            totalPrayers = 50,
            recentPrayers = 5,
            familyName = "Test Family"
        )

        var joinedMemorial: CommunityMemorial? = null

        composeTestRule.setContent {
            CommunityMemorialCard(
                memorial = testMemorial,
                onMemorialClick = {},
                onJoinPrayer = { memorial: CommunityMemorial -> joinedMemorial = memorial }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Join prayer for ${testMemorial.deceasedName}")
            .performClick()

        assert(joinedMemorial == testMemorial)
    }

    @Test
    fun communityScreen_showsLoadingState() {
        composeTestRule.setContent {
            CommunityScreen(
                globalStats = GlobalPrayerStatistics(),
                recentMemorials = emptyList(),
                leaderboard = emptyList(),
                isLoading = true,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Loading community data")
            .assertIsDisplayed()
    }

    @Test
    fun communityScreen_showsEmptyStates() {
        composeTestRule.setContent {
            CommunityScreen(
                globalStats = GlobalPrayerStatistics(),
                recentMemorials = emptyList(),
                leaderboard = emptyList(),
                isLoading = false,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithText("No recent memorials")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Leaderboard updating...")
            .assertIsDisplayed()
    }

    @Test
    fun prayerJoin_showsConfirmationDialog() {
        val testMemorial = CommunityMemorial(
            id = "1",
            title = "Join Prayer Memorial",
            deceasedName = "Test Person",
            totalPrayers = 100,
            recentPrayers = 10,
            familyName = "Test Family"
        )

        composeTestRule.setContent {
            CommunityMemorialCard(
                memorial = testMemorial,
                onMemorialClick = {},
                onJoinPrayer = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Join prayer for ${testMemorial.deceasedName}")
            .performClick()

        // Should show confirmation dialog
        composeTestRule
            .onNodeWithText("Join Community Prayer")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("You are about to join a prayer for ${testMemorial.deceasedName}")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Join Prayer")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun leaderboard_supportsFiltering() {
        val testLeaderboard = listOf(
            PrayerLeaderboard("1", "Local User", 1500, 1, "Indonesia"),
            PrayerLeaderboard("2", "Global User", 2000, 2, "Malaysia"),
            PrayerLeaderboard("3", "Another Local", 1200, 3, "Indonesia")
        )

        composeTestRule.setContent {
            LeaderboardSection(
                leaderboard = testLeaderboard,
                currentFilter = LeaderboardFilter.LOCAL
            )
        }

        // Should show only Indonesian users when filtered by local
        composeTestRule
            .onNodeWithText("Local User")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Another Local")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Global User")
            .assertDoesNotExist()

        // Tap global filter
        composeTestRule
            .onNodeWithText("Global")
            .performClick()

        // Should show all users
        composeTestRule
            .onNodeWithText("Global User")
            .assertIsDisplayed()
    }

    @Test
    fun refreshGesture_triggersDataReload() {
        var refreshCalled = false

        composeTestRule.setContent {
            CommunityScreen(
                globalStats = GlobalPrayerStatistics(),
                recentMemorials = emptyList(),
                leaderboard = emptyList(),
                isLoading = false,
                onMemorialClick = {},
                onJoinPrayer = {},
                onRefresh = { refreshCalled = true }
            )
        }

        // Perform pull-to-refresh gesture
        composeTestRule
            .onNodeWithTag("CommunityScrollContent")
            .performTouchInput {
                swipeDown(
                    startY = 100f,
                    endY = 500f
                )
            }

        assert(refreshCalled)
    }

    // Test data classes are imported from the UI module
}