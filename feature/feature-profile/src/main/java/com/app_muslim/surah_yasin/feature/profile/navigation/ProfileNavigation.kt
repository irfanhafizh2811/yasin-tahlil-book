package com.app_muslim.surah_yasin.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.app_muslim.surah_yasin.feature.profile.ui.ProfileScreen

const val PROFILE_ROUTE = "profile"

fun NavController.navigateToProfile() {
    navigate(PROFILE_ROUTE) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.profileScreen(
    onNavigateBack: () -> Unit = {}
) {
    composable(route = PROFILE_ROUTE) {
        ProfileScreen(
            onNavigateBack = onNavigateBack
        )
    }
}