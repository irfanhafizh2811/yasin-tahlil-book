package com.app_muslim.surah_yasin.feature.memorial.prayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import com.app_muslim.surah_yasin.feature.memorial.prayer.ui.MemorialPrayerScreen

/**
 * Navigation routes for Memorial Prayer feature
 */
object MemorialPrayerRoutes {
    const val PRAYER_SCREEN = "memorial_prayer_screen"
    const val PRAYER_SCREEN_WITH_MEMORIAL = "memorial_prayer_screen/{memorialId}/{memorialName}?photoUrl={photoUrl}"
    
    fun prayerScreenRoute(
        memorialId: String,
        memorialName: String,
        photoUrl: String? = null
    ): String {
        val encodedName = memorialName.replace("/", "%2F")
        val encodedPhotoUrl = photoUrl?.replace("/", "%2F") ?: ""
        return "memorial_prayer_screen/$memorialId/$encodedName?photoUrl=$encodedPhotoUrl"
    }
}

/**
 * Memorial Prayer Navigation Graph
 */
@Composable
fun MemorialPrayerNavGraph(
    navController: NavHostController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToMemorialList: () -> Unit = {},
    startDestination: String = MemorialPrayerRoutes.PRAYER_SCREEN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        memorialPrayerGraph(
            onNavigateBack = onNavigateBack,
            onNavigateToMemorialList = onNavigateToMemorialList
        )
    }
}

/**
 * Extension function to add memorial prayer destinations to existing NavGraphBuilder
 */
fun NavGraphBuilder.memorialPrayerGraph(
    onNavigateBack: () -> Unit = {},
    onNavigateToMemorialList: () -> Unit = {}
) {
    // Default prayer screen (no memorial selected)
    composable(
        route = MemorialPrayerRoutes.PRAYER_SCREEN
    ) {
        MemorialPrayerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToMemorialList = onNavigateToMemorialList
        )
    }
    
    // Prayer screen with specific memorial
    composable(
        route = MemorialPrayerRoutes.PRAYER_SCREEN_WITH_MEMORIAL,
        arguments = listOf(
            navArgument("memorialId") { type = NavType.StringType },
            navArgument("memorialName") { type = NavType.StringType },
            navArgument("photoUrl") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        val memorialName = backStackEntry.arguments?.getString("memorialName") ?: ""
        val photoUrl = backStackEntry.arguments?.getString("photoUrl")?.takeIf { it.isNotEmpty() }
        
        MemorialPrayerScreen(
            memorialId = memorialId,
            memorialName = memorialName.replace("%2F", "/"),
            memorialPhotoUrl = photoUrl?.replace("%2F", "/"),
            onNavigateBack = onNavigateBack,
            onNavigateToMemorialList = onNavigateToMemorialList
        )
    }
}

/**
 * Navigation helper functions
 */
object MemorialPrayerNavigator {
    
    /**
     * Navigate to prayer screen with specific memorial
     */
    fun navigateToPrayerScreen(
        navController: NavController,
        memorialId: String,
        memorialName: String,
        memorialPhotoUrl: String? = null
    ) {
        val route = MemorialPrayerRoutes.prayerScreenRoute(
            memorialId = memorialId,
            memorialName = memorialName,
            photoUrl = memorialPhotoUrl
        )
        navController.navigate(route) {
            // Clear back stack if needed
            launchSingleTop = true
        }
    }
    
    /**
     * Navigate to default prayer screen
     */
    fun navigateToDefaultPrayerScreen(navController: NavController) {
        navController.navigate(MemorialPrayerRoutes.PRAYER_SCREEN) {
            launchSingleTop = true
        }
    }
    
    /**
     * Navigate back from prayer screen
     */
    fun navigateBack(navController: NavController): Boolean {
        return navController.popBackStack()
    }
}

/**
 * Deep link support for memorial prayers
 */
object MemorialPrayerDeepLinks {
    const val PRAYER_DEEP_LINK = "tahlil://memorial/prayer"
    const val PRAYER_WITH_MEMORIAL_DEEP_LINK = "tahlil://memorial/prayer/{memorialId}"
    
    fun createPrayerDeepLink(memorialId: String? = null): String {
        return if (memorialId != null) {
            "tahlil://memorial/prayer/$memorialId"
        } else {
            PRAYER_DEEP_LINK
        }
    }
}

/**
 * Memorial Prayer feature integration for main navigation
 * This should be called within a NavGraphBuilder context
 */
fun NavGraphBuilder.integrateMemorialPrayerNavigation(
    navController: NavHostController,
    route: String = "prayer"
) {
    // This would be called from the main navigation setup
    composable(route) {
        MemorialPrayerNavGraph(
            navController = navController,
            onNavigateToMemorialList = {
                // Navigate to memorial list - would be implemented in main nav
                navController.navigate("memorial_list")
            }
        )
    }
}