package com.app_muslim.surah_yasin.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app_muslim.surah_yasin.feature.auth.ui.*
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought

const val AUTH_GRAPH_ROUTE = "auth"
const val LOGIN_ROUTE = "auth/login"
const val REGISTER_ROUTE = "auth/register"
const val FORGOT_PASSWORD_ROUTE = "auth/forgot_password"
const val CULTURAL_SETUP_ROUTE = "auth/cultural_setup"

fun NavController.navigateToAuth() {
    navigate(AUTH_GRAPH_ROUTE) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToLogin() {
    navigate(LOGIN_ROUTE) {
        popUpTo(AUTH_GRAPH_ROUTE)
        launchSingleTop = true
    }
}

fun NavController.navigateToRegister() {
    navigate(REGISTER_ROUTE) {
        launchSingleTop = true
    }
}

fun NavController.navigateToForgotPassword() {
    navigate(FORGOT_PASSWORD_ROUTE) {
        launchSingleTop = true
    }
}

fun NavController.navigateToMainApp() {
    navigate("main") {
        popUpTo(AUTH_GRAPH_ROUTE) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavController.navigateToCulturalSetup() {
    navigate(CULTURAL_SETUP_ROUTE) {
        popUpTo(AUTH_GRAPH_ROUTE)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onAuthComplete: (IslamicRegion, SchoolOfThought, String) -> Unit
) {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_GRAPH_ROUTE
    ) {
        composable(LOGIN_ROUTE) {
            LoginScreen(
                onNavigateToRegister = { navController.navigateToRegister() },
                onNavigateToForgotPassword = { navController.navigateToForgotPassword() },
                onLoginSuccess = { navController.navigateToCulturalSetup() }
            )
        }

        composable(REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigateToLogin() },
                onRegisterSuccess = { navController.navigateToCulturalSetup() }
            )
        }

        composable(FORGOT_PASSWORD_ROUTE) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(CULTURAL_SETUP_ROUTE) {
            CulturalSetupScreen(
                onSetupComplete = { region, school, language ->
                    onAuthComplete(region, school, language)
                    navController.navigateToMainApp()
                }
            )
        }
    }
}