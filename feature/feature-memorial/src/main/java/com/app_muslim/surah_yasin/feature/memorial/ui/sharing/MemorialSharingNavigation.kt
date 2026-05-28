package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Navigation component for Memorial Sharing System
 * Handles navigation between social sharing and family invitation screens
 */

// Navigation routes
const val SOCIAL_SHARING_ROUTE = "social_sharing/{memorialId}/{memorialName}/{deceasedName}"
const val FAMILY_INVITATION_ROUTE = "family_invitation/{memorialId}/{memorialName}"

/**
 * Add Memorial Sharing navigation routes to the nav graph
 */
fun NavGraphBuilder.memorialSharingNavigation(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    composable(
        route = SOCIAL_SHARING_ROUTE,
        arguments = listOf(
            navArgument("memorialId") { type = NavType.StringType },
            navArgument("memorialName") { type = NavType.StringType },
            navArgument("deceasedName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        val memorialName = backStackEntry.arguments?.getString("memorialName") ?: ""
        val deceasedName = backStackEntry.arguments?.getString("deceasedName") ?: ""
        
        SocialSharingScreen(
            memorialId = memorialId,
            memorialName = memorialName,
            deceasedName = deceasedName,
            onNavigateBack = onNavigateBack
        )
    }
    
    composable(
        route = FAMILY_INVITATION_ROUTE,
        arguments = listOf(
            navArgument("memorialId") { type = NavType.StringType },
            navArgument("memorialName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
        val memorialName = backStackEntry.arguments?.getString("memorialName") ?: ""
        
        FamilyInvitationScreen(
            memorialId = memorialId,
            memorialName = memorialName,
            onNavigateBack = onNavigateBack
        )
    }
}

/**
 * Navigation helpers for Memorial Sharing
 */
object MemorialSharingNavigation {
    
    fun navigateToSocialSharing(
        navController: NavController,
        memorialId: String,
        memorialName: String,
        deceasedName: String
    ) {
        val route = "social_sharing/$memorialId/${memorialName}/${deceasedName}"
        navController.navigate(route)
    }
    
    fun navigateToFamilyInvitation(
        navController: NavController,
        memorialId: String,
        memorialName: String
    ) {
        val route = "family_invitation/$memorialId/${memorialName}"
        navController.navigate(route)
    }
}