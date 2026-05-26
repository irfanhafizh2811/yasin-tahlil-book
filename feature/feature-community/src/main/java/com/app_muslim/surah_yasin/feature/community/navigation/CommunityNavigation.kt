package com.app_muslim.surah_yasin.feature.community.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app_muslim.surah_yasin.feature.community.ui.CommunityHomeScreen
import com.app_muslim.surah_yasin.feature.community.ui.PrayerLeaderboardScreen
import com.app_muslim.surah_yasin.feature.community.viewmodel.CommunityHomeViewModel
import com.app_muslim.surah_yasin.feature.community.viewmodel.PrayerLeaderboardViewModel

/**
 * Community Feature Navigation Setup
 * Defines routes and navigation for community prayer features
 */

// Navigation routes
object CommunityRoutes {
    const val COMMUNITY_HOME = "community_home"
    const val PRAYER_LEADERBOARD = "prayer_leaderboard"
    const val COMMUNITY_SESSION = "community_session/{sessionId}"
    const val CREATE_SESSION = "create_session"
    const val FAMILY_MEMORIAL_SHARING = "family_memorial_sharing"
    const val USER_ACHIEVEMENTS = "user_achievements/{userId}"
    
    // Route with arguments
    fun communitySessionRoute(sessionId: String) = "community_session/$sessionId"
    fun userAchievementsRoute(userId: String) = "user_achievements/$userId"
}

/**
 * Add community navigation to NavGraphBuilder
 */
fun NavGraphBuilder.addCommunityNavigation(
    navController: NavController
) {
    // Community Home Screen
    composable(
        route = CommunityRoutes.COMMUNITY_HOME
    ) {
        val viewModel: CommunityHomeViewModel = hiltViewModel()
        CommunityHomeScreen(
            onNavigateToSession = { sessionId ->
                navController.navigate(CommunityRoutes.communitySessionRoute(sessionId))
            },
            onNavigateToLeaderboard = {
                navController.navigate(CommunityRoutes.PRAYER_LEADERBOARD)
            },
            onNavigateToCreateSession = {
                navController.navigate(CommunityRoutes.CREATE_SESSION)
            },
            viewModel = viewModel
        )
    }
    
    // Prayer Leaderboard Screen
    composable(
        route = CommunityRoutes.PRAYER_LEADERBOARD
    ) {
        val viewModel: PrayerLeaderboardViewModel = hiltViewModel()
        PrayerLeaderboardScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToProfile = { userId ->
                navController.navigate(CommunityRoutes.userAchievementsRoute(userId))
            },
            viewModel = viewModel
        )
    }
    
    // Community Prayer Session Screen (placeholder for future implementation)
    composable(
        route = CommunityRoutes.COMMUNITY_SESSION,
        arguments = listOf(
            navArgument("sessionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
        CommunitySessionPlaceholder(
            sessionId = sessionId,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
    
    // Create Community Session Screen (placeholder for future implementation)
    composable(
        route = CommunityRoutes.CREATE_SESSION
    ) {
        CreateSessionPlaceholder(
            onNavigateBack = {
                navController.popBackStack()
            },
            onSessionCreated = { sessionId ->
                navController.navigate(CommunityRoutes.communitySessionRoute(sessionId)) {
                    popUpTo(CommunityRoutes.COMMUNITY_HOME) { inclusive = false }
                }
            }
        )
    }
    
    // Family Memorial Sharing Screen (placeholder for future implementation)
    composable(
        route = CommunityRoutes.FAMILY_MEMORIAL_SHARING
    ) {
        FamilyMemorialSharingPlaceholder(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
    
    // User Achievements Screen (placeholder for future implementation)
    composable(
        route = CommunityRoutes.USER_ACHIEVEMENTS,
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        UserAchievementsPlaceholder(
            userId = userId,
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}

// Placeholder composables for future implementation
@Composable
private fun CommunitySessionPlaceholder(
    sessionId: String,
    onNavigateBack: () -> Unit
) {
    // TODO: Implement Community Session Screen
    androidx.compose.material3.Text("Community Session: $sessionId")
}

@Composable
private fun CreateSessionPlaceholder(
    onNavigateBack: () -> Unit,
    onSessionCreated: (String) -> Unit
) {
    // TODO: Implement Create Session Screen
    androidx.compose.material3.Text("Create Session")
}

@Composable
private fun FamilyMemorialSharingPlaceholder(
    onNavigateBack: () -> Unit
) {
    // TODO: Implement Family Memorial Sharing Screen
    androidx.compose.material3.Text("Family Memorial Sharing")
}

@Composable
private fun UserAchievementsPlaceholder(
    userId: String,
    onNavigateBack: () -> Unit
) {
    // TODO: Implement User Achievements Screen
    androidx.compose.material3.Text("User Achievements: $userId")
}

/**
 * Navigation extension functions for easy access
 */
object CommunityNavigationActions {
    fun NavController.navigateToCommunityHome() {
        navigate(CommunityRoutes.COMMUNITY_HOME) {
            launchSingleTop = true
        }
    }
    
    fun NavController.navigateToPrayerLeaderboard() {
        navigate(CommunityRoutes.PRAYER_LEADERBOARD)
    }
    
    fun NavController.navigateToCommunitySession(sessionId: String) {
        navigate(CommunityRoutes.communitySessionRoute(sessionId))
    }
    
    fun NavController.navigateToCreateSession() {
        navigate(CommunityRoutes.CREATE_SESSION)
    }
    
    fun NavController.navigateToFamilyMemorialSharing() {
        navigate(CommunityRoutes.FAMILY_MEMORIAL_SHARING)
    }
    
    fun NavController.navigateToUserAchievements(userId: String) {
        navigate(CommunityRoutes.userAchievementsRoute(userId))
    }
}