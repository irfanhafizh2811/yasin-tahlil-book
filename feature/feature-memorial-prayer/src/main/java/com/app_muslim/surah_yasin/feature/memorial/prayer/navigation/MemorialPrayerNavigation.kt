package com.app_muslim.surah_yasin.feature.memorial.prayer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.app_muslim.surah_yasin.feature.memorial.prayer.ui.MemorialPrayerScreen

/**
 * Navigation setup for Memorial Prayer feature
 * Handles routing and parameter passing for prayer sessions
 */

const val MEMORIAL_PRAYER_ROUTE = "memorial_prayer"
const val MEMORIAL_PRAYER_ROUTE_WITH_ARGS = "memorial_prayer/{memorialId}"

fun NavController.navigateToMemorialPrayer(memorialId: String) {
    navigate("memorial_prayer/$memorialId")
}

fun NavGraphBuilder.memorialPrayerScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = MEMORIAL_PRAYER_ROUTE_WITH_ARGS
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        
        MemorialPrayerScreen(
            memorialId = memorialId,
            onNavigateBack = onNavigateBack
        )
    }
}