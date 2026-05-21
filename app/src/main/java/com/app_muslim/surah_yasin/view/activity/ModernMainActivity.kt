package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentDestinationAsState
import androidx.navigation.compose.rememberNavController
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
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            TahlilBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
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
                ProfileScreen()
            }
        }
    }
}

@Composable
fun TahlilBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.Favorite),
        BottomNavItem("memorial", "Memorial", Icons.Default.LocationOn),
        BottomNavItem("community", "Community", Icons.Default.People),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
    
    val currentDestination by navController.currentDestinationAsState()
    
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
fun ProfileScreen() {
    CenterAlignedTopAppBar(
        title = {
            Text("Profile")
        }
    )
    // TODO: Implement Profile Compose UI
}