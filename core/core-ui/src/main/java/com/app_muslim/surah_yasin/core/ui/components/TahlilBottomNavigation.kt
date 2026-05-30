package com.app_muslim.surah_yasin.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@Composable
fun TahlilBottomNavigation(
    navController: NavController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val items = listOf(
        BottomNavItem("memorial", "Memorial", Icons.Default.LocationOn),
        BottomNavItem("community", "Community", Icons.Default.Home),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
    
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    
    NavigationBar(
        modifier = modifier,
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
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    unselectedTextColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// ============================================================================
// PREVIEW-FRIENDLY VERSION
// ============================================================================

@Composable
fun TahlilBottomNavigationPreview(
    selectedTab: String = "memorial",
    onTabSelected: (String) -> Unit = {}
) {
    val items = listOf(
        BottomNavItem("memorial", "Memorial", Icons.Default.LocationOn),
        BottomNavItem("community", "Community", Icons.Default.Home),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = item.route == selectedTab
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.route) },
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
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    unselectedTextColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class BottomNavigationStateProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "memorial",
        "community", 
        "profile"
    )
}

// ============================================================================
// BASIC PREVIEWS
// ============================================================================

@Preview(name = "Bottom Navigation - Memorial Selected")
@Composable
fun PreviewTahlilBottomNavigationMemorial() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "memorial")
        }
    }
}

@Preview(name = "Bottom Navigation - Community Selected")
@Composable
fun PreviewTahlilBottomNavigationCommunity() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "community")
        }
    }
}

@Preview(name = "Bottom Navigation - Profile Selected")
@Composable
fun PreviewTahlilBottomNavigationProfile() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "profile")
        }
    }
}

@Preview(name = "Bottom Navigation - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTahlilBottomNavigationDark() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "memorial")
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Bottom Navigation - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewTahlilBottomNavigationLandscape() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "community")
        }
    }
}

@Preview(
    name = "Bottom Navigation - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewTahlilBottomNavigationTablet() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "memorial")
        }
    }
}

@Preview(
    name = "Bottom Navigation - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewTahlilBottomNavigationSmallPhone() {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = "profile")
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Bottom Navigation - Various States")
@Composable
fun PreviewTahlilBottomNavigationDynamic(
    @PreviewParameter(BottomNavigationStateProvider::class) selectedTab: String
) {
    TahlilTheme {
        Surface {
            TahlilBottomNavigationPreview(selectedTab = selectedTab)
        }
    }
}