package com.app_muslim.surah_yasin.feature.memorial.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app_muslim.surah_yasin.feature.memorial.ui.CreateMemorialScreen

const val CREATE_MEMORIAL_ROUTE = "create_memorial"
const val MEMORIAL_DETAIL_ROUTE = "memorial_detail"

fun NavController.navigateToCreateMemorial(navOptions: NavOptions? = null) {
    this.navigate(CREATE_MEMORIAL_ROUTE, navOptions)
}

fun NavController.navigateToMemorialDetail(memorialId: String, navOptions: NavOptions? = null) {
    this.navigate("$MEMORIAL_DETAIL_ROUTE/$memorialId", navOptions)
}

fun NavGraphBuilder.memorialNavGraph(
    onNavigateBack: () -> Unit,
    onMemorialCreated: (String) -> Unit
) {
    composable(route = CREATE_MEMORIAL_ROUTE) {
        CreateMemorialScreen(
            onNavigateBack = onNavigateBack,
            onMemorialCreated = onMemorialCreated
        )
    }
    
    composable(route = "$MEMORIAL_DETAIL_ROUTE/{memorialId}") { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        // Memorial detail screen would be implemented here
        // For now, navigate back as a placeholder
        onNavigateBack()
    }
}