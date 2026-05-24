package com.app_muslim.surah_yasin.feature.memorial.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app_muslim.surah_yasin.feature.memorial.ui.CreateMemorialScreen
import com.app_muslim.surah_yasin.feature.memorial.ui.list.MemorialListScreen
import com.app_muslim.surah_yasin.feature.memorial.ui.edit.EditMemorialScreen

const val CREATE_MEMORIAL_ROUTE = "create_memorial"
const val MEMORIAL_LIST_ROUTE = "memorial_list"
const val EDIT_MEMORIAL_ROUTE = "edit_memorial"
const val MEMORIAL_DETAIL_ROUTE = "memorial_detail"

fun NavController.navigateToCreateMemorial(navOptions: NavOptions? = null) {
    this.navigate(CREATE_MEMORIAL_ROUTE, navOptions)
}

fun NavController.navigateToMemorialList(navOptions: NavOptions? = null) {
    this.navigate(MEMORIAL_LIST_ROUTE, navOptions)
}

fun NavController.navigateToEditMemorial(memorialId: String, navOptions: NavOptions? = null) {
    this.navigate("$EDIT_MEMORIAL_ROUTE/$memorialId", navOptions)
}

fun NavController.navigateToMemorialDetail(memorialId: String, navOptions: NavOptions? = null) {
    this.navigate("$MEMORIAL_DETAIL_ROUTE/$memorialId", navOptions)
}

fun NavGraphBuilder.memorialNavGraph(
    onNavigateBack: () -> Unit,
    onMemorialCreated: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    composable(route = CREATE_MEMORIAL_ROUTE) {
        CreateMemorialScreen(
            onNavigateBack = onNavigateBack,
            onMemorialCreated = onMemorialCreated
        )
    }
    
    composable(route = MEMORIAL_LIST_ROUTE) {
        MemorialListScreen(
            onNavigateToCreate = onNavigateToCreate,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateBack = onNavigateBack
        )
    }
    
    composable(
        route = "$EDIT_MEMORIAL_ROUTE/{memorialId}",
        arguments = listOf(navArgument("memorialId") { type = NavType.StringType })
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        EditMemorialScreen(
            memorialId = memorialId,
            onNavigateBack = onNavigateBack,
            onMemorialUpdated = onNavigateBack
        )
    }
    
    composable(
        route = "$MEMORIAL_DETAIL_ROUTE/{memorialId}",
        arguments = listOf(navArgument("memorialId") { type = NavType.StringType })
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        // Memorial detail screen would be implemented here
        // For now, navigate back as a placeholder
        onNavigateBack()
    }
}