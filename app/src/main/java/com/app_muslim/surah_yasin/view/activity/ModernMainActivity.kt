package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app_muslim.surah_yasin.feature.auth.navigation.authGraph
import com.app_muslim.surah_yasin.feature.auth.navigation.navigateToAuth
import com.app_muslim.surah_yasin.feature.auth.viewmodel.AuthViewModel
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModernMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TahlilTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun TahlilTheme(content: @Composable () -> Unit) {
    // Material 3 theme with Islamic colors
    val islamicColorScheme = lightColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF1B4332), // Islamic Green
        onPrimary = androidx.compose.ui.graphics.Color.White,
        secondary = androidx.compose.ui.graphics.Color(0xFFD4AF37), // Islamic Gold
        onSecondary = androidx.compose.ui.graphics.Color.Black,
        tertiary = androidx.compose.ui.graphics.Color(0xFFF5F5DC), // Islamic Cream
        surface = androidx.compose.ui.graphics.Color.White,
        onSurface = androidx.compose.ui.graphics.Color.Black,
        background = androidx.compose.ui.graphics.Color.White,
        onBackground = androidx.compose.ui.graphics.Color.Black
    )
    
    MaterialTheme(
        colorScheme = islamicColorScheme,
        content = content
    )
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    
    // Determine start destination based on authentication state
    val startDestination = if (uiState.isAuthenticated) "main" else "auth"
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Graph
        authGraph(
            navController = navController,
            onAuthComplete = { region, school, language ->
                // Save user preferences here
                // For now, we'll just proceed to main app
            }
        )
        
        // Main App Graph
        composable("main") {
            MainAppContent(navController)
        }
    }
}

@Composable
fun MainAppContent(navController: NavController) {
    val bottomNavController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            TahlilBottomNavigation(navController = bottomNavController)
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "tasbeeh",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("tasbeeh") {
                TasbeehScreen()
            }
            composable("memorial") {
                MemorialScreen()
            }
            composable("community") {
                CommunityScreen()
            }
            composable("profile") {
                ProfileScreen(onSignOut = {
                    // Handle sign out - navigate back to auth
                    navController.navigateToAuth()
                })
            }
        }
    }
}

@Composable
fun TahlilBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.Favorite),
        BottomNavItem("memorial", "Memorial", Icons.Default.LocationOn),
        BottomNavItem("community", "Community", Icons.Default.Home),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { 
                it.route == item.route 
            } == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { 
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    ) 
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// Placeholder Compose screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen() {
    CenterAlignedTopAppBar(
        title = {
            Text("Tasbeeh Counter")
        }
    )
    // TODO: Implement actual Tasbeeh Compose UI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialScreen() {
    CenterAlignedTopAppBar(
        title = {
            Text("Memorial Prayers")
        }
    )
    // TODO: Implement Memorial Compose UI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen() {
    CenterAlignedTopAppBar(
        title = {
            Text("Global Community")
        }
    )
    // TODO: Implement Community Compose UI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onSignOut: () -> Unit = {}) {
    val authViewModel: AuthViewModel = hiltViewModel()
    
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text("Profile")
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "👤",
                        fontSize = 64.sp
                    )
                    Text(
                        text = "User Profile",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Profile management coming soon...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            
            OutlinedButton(
                onClick = {
                    authViewModel.handleAuthEvent(com.app_muslim.surah_yasin.feature.auth.model.AuthEvent.SignOut)
                    onSignOut()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}