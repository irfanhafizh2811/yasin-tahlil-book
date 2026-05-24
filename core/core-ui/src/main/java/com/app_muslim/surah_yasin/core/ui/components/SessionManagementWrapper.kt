package com.app_muslim.surah_yasin.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app_muslim.surah_yasin.core.firebase.TokenRefreshManager
import com.app_muslim.surah_yasin.core.ui.navigation.NavigationManagerViewModel

@Composable
fun SessionManagementWrapper(
    content: @Composable () -> Unit,
    viewModel: NavigationManagerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Initialize token refresh when session starts
    LaunchedEffect(uiState.sessionState.isAuthenticated) {
        if (uiState.sessionState.isAuthenticated) {
            // Start auto token refresh when user is authenticated
            // This would be handled by the TokenRefreshManager
        }
    }
    
    Box {
        // Main content
        content()
        
        // Session management dialogs
        GuestModeRestrictionDialog(
            isVisible = uiState.showGuestModeDialog,
            onDismiss = viewModel::dismissGuestModeDialog,
            onUpgradeAccount = { email, password ->
                viewModel.upgradeGuestAccount(email, password)
            },
            onSignInWithAccount = {
                viewModel.dismissGuestModeDialog()
                // Navigate to auth screen
            }
        )
        
        EmailVerificationDialog(
            isVisible = uiState.showEmailVerificationDialog,
            userEmail = "", // Will be passed from parent component
            onDismiss = viewModel::dismissEmailVerificationDialog,
            onSendVerification = viewModel::sendEmailVerification,
            onRefreshStatus = {
                // Check email verification status
                viewModel.dismissEmailVerificationDialog()
            }
        )
        
        SignOutConfirmationDialog(
            isVisible = uiState.showSignOutDialog,
            isSigningOut = uiState.isSigningOut,
            onConfirm = viewModel::signOut,
            onDismiss = viewModel::dismissSignOutDialog
        )
        
        // Show snackbar for sign out errors
        uiState.signOutError?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }
    }
}

@Composable
fun SessionStatusIndicator(
    sessionState: com.app_muslim.surah_yasin.core.firebase.SessionState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !sessionState.isAuthenticated -> MaterialTheme.colorScheme.errorContainer
                sessionState.isGuest -> MaterialTheme.colorScheme.warningContainer
                !sessionState.isEmailVerified -> MaterialTheme.colorScheme.warningContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when {
                            !sessionState.isAuthenticated -> MaterialTheme.colorScheme.error
                            sessionState.isGuest -> MaterialTheme.colorScheme.warning
                            !sessionState.isEmailVerified -> MaterialTheme.colorScheme.warning
                            else -> MaterialTheme.colorScheme.primary
                        },
                        shape = CircleShape
                    )
            )
            
            // Status text
            Text(
                text = when {
                    !sessionState.isAuthenticated -> "Not signed in"
                    sessionState.isGuest -> "Guest mode"
                    !sessionState.isEmailVerified -> "Email not verified"
                    else -> "Signed in"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    !sessionState.isAuthenticated -> MaterialTheme.colorScheme.onErrorContainer
                    sessionState.isGuest -> MaterialTheme.colorScheme.onWarningContainer
                    !sessionState.isEmailVerified -> MaterialTheme.colorScheme.onWarningContainer
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

// Extension for warning colors (since Material 3 doesn't have built-in warning)
val ColorScheme.warningContainer: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFFFE4B5)

val ColorScheme.onWarningContainer: androidx.compose.ui.graphics.Color  
    get() = androidx.compose.ui.graphics.Color(0xFF8B4513)

val ColorScheme.warning: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFFF9800)

@Composable
fun ProtectedFeatureButton(
    onClick: () -> Unit,
    sessionState: com.app_muslim.surah_yasin.core.firebase.SessionState,
    navigationManager: com.app_muslim.surah_yasin.core.ui.navigation.NavigationManager,
    route: String,
    onShowGuestDialog: () -> Unit,
    onShowEmailVerificationDialog: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = {
            val accessResult = navigationManager.canAccessRoute(route, sessionState)
            when (accessResult) {
                is com.app_muslim.surah_yasin.core.ui.navigation.NavigationAccessResult.Allowed -> onClick()
                is com.app_muslim.surah_yasin.core.ui.navigation.NavigationAccessResult.GuestRestricted -> onShowGuestDialog()
                is com.app_muslim.surah_yasin.core.ui.navigation.NavigationAccessResult.RequiresEmailVerification -> onShowEmailVerificationDialog()
                is com.app_muslim.surah_yasin.core.ui.navigation.NavigationAccessResult.RequiresAuth -> {
                    // Navigate to auth
                }
            }
        },
        enabled = enabled,
        modifier = modifier,
        content = content
    )
}