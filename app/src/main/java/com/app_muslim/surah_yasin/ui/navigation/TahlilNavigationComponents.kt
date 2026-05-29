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
import androidx.navigation.compose.rememberNavController
import com.app_muslim.surah_yasin.ui.navigation.BottomNavItem

@Composable
fun TahlilNavigationRail(
    navController: NavController = rememberNavController(),
    selectedTab: String = "memorials"
) {
    NavigationRail(
        modifier = Modifier.semantics { contentDescription = "Navigation rail" }
    ) {
        NavigationRailItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Memorials") },
            label = { Text("Memorials") },
            selected = selectedTab == "memorials",
            onClick = { 
                navController.navigate("memorials") 
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Community") },
            label = { Text("Community") },
            selected = selectedTab == "community",
            onClick = { 
                navController.navigate("community") 
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Tasbeeh") },
            label = { Text("Tasbeeh") },
            selected = selectedTab == "tasbeeh",
            onClick = { 
                navController.navigate("tasbeeh") 
            }
        )
        NavigationRailItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == "profile",
            onClick = { 
                navController.navigate("profile") 
            }
        )
    }
}

@Composable
fun TahlilBottomNavigation(
    navController: NavController,
    selectedTab: String = "memorials"
) {
    val navItems = listOf(
        BottomNavItem("memorials", "Memorials", Icons.Default.Favorite),
        BottomNavItem("community", "Community", Icons.Default.Home),
        BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.List),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    NavigationBar(
        modifier = Modifier.semantics { contentDescription = "Bottom navigation" }
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedTab == item.route,
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

// BottomNavItem is defined in TahlilNavigation.kt to avoid duplication