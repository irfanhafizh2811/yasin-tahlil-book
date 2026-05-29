package com.app_muslim.surah_yasin.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun TahlilNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "memorials"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("memorials") {
            // Memorial screen placeholder
            Text("Memorials Screen")
        }
        
        composable("community") {
            // Community screen placeholder
            Text("Community Screen")
        }
        
        composable("tasbeeh") {
            // Tasbeeh screen placeholder
            Text("Tasbeeh Screen")
        }
        
        composable("profile") {
            // Profile screen placeholder
            Text("Profile Screen")
        }
        
        composable("auth") {
            // Auth screen placeholder
            Text("Sign In Screen")
        }
        
        composable("memorial_detail/{memorialId}") { backStackEntry ->
            val memorialId = backStackEntry.arguments?.getString("memorialId") ?: ""
            Text("Memorial Detail for $memorialId")
        }
        
        composable("create_memorial") {
            Text("Create Memorial Screen")
        }
    }
}

@Composable
fun TahlilBottomNavigation(
    navController: NavController
) {
    val navItems = listOf(
        BottomNavItem("memorials", "Memorials", Icons.Default.Favorite),
        BottomNavItem("community", "Community", Icons.Default.Home),
        BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.List),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = false, // Should be implemented based on current route
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "${item.label} tab"
                }
            )
        }
    }
}

@Composable
fun TahlilNavigationRail() {
    NavigationRail {
        NavigationRailItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Memorials") },
            label = { Text("Memorials") },
            selected = false,
            onClick = { }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Community") },
            label = { Text("Community") },
            selected = false,
            onClick = { }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Tasbeeh") },
            label = { Text("Tasbeeh") },
            selected = false,
            onClick = { }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { }
        )
    }
}

// BottomNavItem moved to NavigationModels.kt