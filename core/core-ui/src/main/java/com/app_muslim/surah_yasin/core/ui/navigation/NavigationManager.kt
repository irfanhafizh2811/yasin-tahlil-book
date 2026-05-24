package com.app_muslim.surah_yasin.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app_muslim.surah_yasin.core.firebase.SessionManager
import com.app_muslim.surah_yasin.core.firebase.SessionState
import javax.inject.Inject
import javax.inject.Singleton

// Navigation routes
object NavigationRoutes {
    const val AUTH_GRAPH = "auth"
    const val MAIN_APP = "main"
    const val PROFILE = "profile"
    const val MEMORIAL_CREATE = "memorial_create"
    const val MEMORIAL_LIST = "memorial_list"
    const val TASBEEH = "tasbeeh"
    
    // Guest mode restricted routes
    val GUEST_RESTRICTED_ROUTES = setOf(
        MEMORIAL_CREATE,
        PROFILE
    )
    
    // Email verification required routes
    val EMAIL_VERIFICATION_REQUIRED_ROUTES = setOf(
        MEMORIAL_CREATE,
        PROFILE
    )
}

@Singleton
class NavigationManager @Inject constructor(
    private val sessionManager: SessionManager
) {
    
    fun canAccessRoute(route: String, sessionState: SessionState): NavigationAccessResult {
        return when {
            // Always allow auth routes
            route.startsWith(NavigationRoutes.AUTH_GRAPH) -> NavigationAccessResult.Allowed
            
            // Check if user is authenticated
            !sessionState.isAuthenticated -> NavigationAccessResult.RequiresAuth
            
            // Check guest restrictions
            sessionState.isGuest && route in NavigationRoutes.GUEST_RESTRICTED_ROUTES -> {
                NavigationAccessResult.GuestRestricted
            }
            
            // Check email verification for non-guest users
            !sessionState.isGuest && 
            !sessionState.isEmailVerified && 
            route in NavigationRoutes.EMAIL_VERIFICATION_REQUIRED_ROUTES -> {
                NavigationAccessResult.RequiresEmailVerification
            }
            
            else -> NavigationAccessResult.Allowed
        }
    }
    
    fun shouldShowGuestModeDialog(route: String, sessionState: SessionState): Boolean {
        return sessionState.isGuest && route in NavigationRoutes.GUEST_RESTRICTED_ROUTES
    }
    
    fun shouldShowEmailVerificationDialog(route: String, sessionState: SessionState): Boolean {
        return !sessionState.isGuest && 
               !sessionState.isEmailVerified && 
               route in NavigationRoutes.EMAIL_VERIFICATION_REQUIRED_ROUTES
    }
}

sealed class NavigationAccessResult {
    object Allowed : NavigationAccessResult()
    object RequiresAuth : NavigationAccessResult()
    object GuestRestricted : NavigationAccessResult()
    object RequiresEmailVerification : NavigationAccessResult()
}

// Navigation integration will be implemented in app module
// to avoid circular dependencies between core and feature modules