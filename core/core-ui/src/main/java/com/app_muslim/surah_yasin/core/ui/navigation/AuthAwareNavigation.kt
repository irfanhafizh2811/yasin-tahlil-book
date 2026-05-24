package com.app_muslim.surah_yasin.core.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app_muslim.surah_yasin.core.firebase.SessionState
import com.app_muslim.surah_yasin.core.firebase.canAccessFeature
import com.app_muslim.surah_yasin.core.firebase.getStatusMessage
import com.app_muslim.surah_yasin.core.firebase.requiresAttention
import com.app_muslim.surah_yasin.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthAwareAppBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    sessionState: SessionState,
    onShowSessionDialog: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = navigationIcon,
        actions = {
            // Session status indicator
            if (sessionState.requiresAttention()) {
                IconButton(onClick = onShowSessionDialog) {
                    Badge {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Account attention required",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // User profile/auth status
            IconButton(onClick = onShowSessionDialog) {
                Icon(
                    imageVector = when {
                        !sessionState.isAuthenticated -> Icons.Default.Login
                        sessionState.isGuest -> Icons.Default.PersonOutline
                        else -> Icons.Default.AccountCircle
                    },
                    contentDescription = "Account status"
                )
            }
            
            actions()
        }
    )
}

@Composable
fun AuthStatusCard(
    sessionState: SessionState,
    onSignIn: () -> Unit,
    onUpgradeAccount: () -> Unit,
    onVerifyEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !sessionState.isAuthenticated -> MaterialTheme.colorScheme.errorContainer
                sessionState.requiresAttention() -> MaterialTheme.colorScheme.warningContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = when {
                        !sessionState.isAuthenticated -> Icons.Default.Login
                        sessionState.isGuest -> Icons.Default.PersonOutline
                        sessionState.requiresAttention() -> Icons.Default.Warning
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = when {
                        !sessionState.isAuthenticated -> MaterialTheme.colorScheme.onErrorContainer
                        sessionState.requiresAttention() -> MaterialTheme.colorScheme.onWarningContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
                
                Text(
                    text = sessionState.getStatusMessage(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        !sessionState.isAuthenticated -> MaterialTheme.colorScheme.onErrorContainer
                        sessionState.requiresAttention() -> MaterialTheme.colorScheme.onWarningContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
            
            // Action button based on state
            when {
                !sessionState.isAuthenticated -> {
                    Button(
                        onClick = onSignIn,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign In")
                    }
                }
                sessionState.isGuest -> {
                    Button(
                        onClick = onUpgradeAccount,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Account")
                    }
                }
                !sessionState.isEmailVerified -> {
                    Button(
                        onClick = onVerifyEmail,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify Email")
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureAccessCard(
    featureName: String,
    description: String,
    sessionState: SessionState,
    navigationManager: NavigationManager,
    onAccessGranted: () -> Unit,
    onShowGuestDialog: () -> Unit,
    onShowEmailVerificationDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canAccess = sessionState.canAccessFeature(featureName.lowercase())
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (canAccess) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = featureName,
                style = MaterialTheme.typography.titleMedium,
                color = if (canAccess) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (canAccess) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )
            
            Button(
                onClick = {
                    if (canAccess) {
                        onAccessGranted()
                    } else {
                        when {
                            sessionState.isGuest -> onShowGuestDialog()
                            !sessionState.isEmailVerified -> onShowEmailVerificationDialog()
                            else -> onAccessGranted()
                        }
                    }
                },
                enabled = canAccess || sessionState.isAuthenticated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (canAccess) "Open $featureName" else when {
                        !sessionState.isAuthenticated -> "Sign In Required"
                        sessionState.isGuest -> "Account Required"
                        !sessionState.isEmailVerified -> "Email Verification Required"
                        else -> "Access $featureName"
                    }
                )
            }
        }
    }
}

@Composable
fun AuthAwareBottomNavigation(
    navController: NavHostController,
    sessionState: SessionState,
    navigationManager: NavigationManager,
    onShowGuestDialog: () -> Unit,
    onShowEmailVerificationDialog: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        // Community Prayer - Always accessible
        NavigationBarItem(
            icon = { Icon(Icons.Default.Circle, contentDescription = null) },
            label = { Text("Community") },
            selected = currentRoute == NavigationRoutes.COMMUNITY_PRAYER,
            onClick = {
                navController.navigate(NavigationRoutes.COMMUNITY_PRAYER) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        
        // Memorial List - Basic auth required
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Memorials") },
            selected = currentRoute == NavigationRoutes.MEMORIAL_LIST,
            onClick = {
                if (sessionState.isAuthenticated) {
                    navController.navigate(NavigationRoutes.MEMORIAL_LIST) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            enabled = sessionState.isAuthenticated
        )
        
        // Create Memorial - Restricted feature
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("Create") },
            selected = currentRoute == NavigationRoutes.MEMORIAL_CREATE,
            onClick = {
                val accessResult = navigationManager.canAccessRoute(
                    NavigationRoutes.MEMORIAL_CREATE, 
                    sessionState
                )
                when (accessResult) {
                    NavigationAccessResult.Allowed -> {
                        navController.navigate(NavigationRoutes.MEMORIAL_CREATE)
                    }
                    NavigationAccessResult.GuestRestricted -> onShowGuestDialog()
                    NavigationAccessResult.RequiresEmailVerification -> onShowEmailVerificationDialog()
                    else -> {}
                }
            }
        )
        
        // Profile - Restricted feature
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = currentRoute == NavigationRoutes.PROFILE,
            onClick = {
                val accessResult = navigationManager.canAccessRoute(
                    NavigationRoutes.PROFILE,
                    sessionState
                )
                when (accessResult) {
                    NavigationAccessResult.Allowed -> {
                        navController.navigate(NavigationRoutes.PROFILE)
                    }
                    NavigationAccessResult.GuestRestricted -> onShowGuestDialog()
                    NavigationAccessResult.RequiresEmailVerification -> onShowEmailVerificationDialog()
                    else -> {}
                }
            }
        )
    }
}