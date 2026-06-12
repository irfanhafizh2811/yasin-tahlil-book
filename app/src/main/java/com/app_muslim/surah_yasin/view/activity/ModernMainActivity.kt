package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.app_muslim.surah_yasin.feature.community.navigation.addCommunityNavigation
import com.app_muslim.surah_yasin.feature.community.navigation.CommunityRoutes
import com.app_muslim.surah_yasin.feature.memorial.navigation.memorialNavGraph
import com.app_muslim.surah_yasin.feature.memorial.navigation.navigateToMemorialList
import com.app_muslim.surah_yasin.feature.profile.navigation.profileScreen
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.ui.memorial.MemorialScreen
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial
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
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    
    // Show splash screen while checking authentication
    if (uiState.isLoading) {
        SplashScreen()
        return
    }
    
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
                // Navigate to main app after authentication
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        )
        
        // Main App Graph
        composable("main") {
            MainAppContent(navController)
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "🕌",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Text(
                text = "Tahlil",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Text(
                text = "Global Islamic Memorial Prayer Platform",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun MainAppContent(mainNavController: NavController) {
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
            // Tasbeeh Counter Screen (Main Feature)
            composable("tasbeeh") {
                TasbeehScreen()
            }
            
            // Memorial Feature Navigation Graph
            composable("memorial") {
                MemorialMainScreen(
                    onNavigateToCreate = {
                        bottomNavController.navigate("memorial_create")
                    },
                    onNavigateToList = {
                        bottomNavController.navigate("memorial_list")
                    },
                    onNavigateToDetail = { memorialId ->
                        bottomNavController.navigate("memorial_detail/$memorialId")
                    }
                )
            }
            
            // Memorial creation and management screens
            memorialNavGraph(
                onNavigateBack = { bottomNavController.popBackStack() },
                onMemorialCreated = { memorialId ->
                    bottomNavController.navigate("memorial") {
                        popUpTo("memorial") { inclusive = true }
                    }
                },
                onNavigateToDetail = { memorialId ->
                    bottomNavController.navigate("memorial_detail/$memorialId")
                },
                onNavigateToEdit = { memorialId ->
                    bottomNavController.navigate("edit_memorial/$memorialId")
                },
                onNavigateToCreate = {
                    bottomNavController.navigate("create_memorial")
                }
            )
            
            // Community Feature Navigation Graph
            composable("community") {
                CommunityMainScreen(
                    onNavigateToHome = {
                        bottomNavController.navigate(CommunityRoutes.COMMUNITY_HOME)
                    },
                    onNavigateToLeaderboard = {
                        bottomNavController.navigate(CommunityRoutes.PRAYER_LEADERBOARD)
                    },
                    onNavigateToDiscovery = {
                        bottomNavController.navigate(CommunityRoutes.MEMORIAL_DISCOVERY)
                    }
                )
            }
            
            // Add community navigation screens
            addCommunityNavigation(navController = bottomNavController)
            
            // Profile Screen
            composable("profile") {
                ProfileMainScreen(
                    onSignOut = {
                        // Handle sign out - navigate back to auth
                        mainNavController.navigateToAuth()
                    },
                    onNavigateToSettings = {
                        bottomNavController.navigate("profile_settings")
                    }
                )
            }
            
            // Profile navigation screens
            profileScreen(
                onNavigateBack = { bottomNavController.popBackStack() }
            )
        }
    }
}

@Composable
fun TahlilBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("tasbeeh", "Tasbeeh", Icons.Default.FavoriteBorder),
        BottomNavItem("memorial", "Memorial", Icons.Default.Place),
        BottomNavItem("community", "Community", Icons.Default.Groups),
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

// ================================================================================================
// MAIN FEATURE SCREENS
// ================================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Tasbeeh Counter") }
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "📿",
                    style = MaterialTheme.typography.displayLarge
                )
                
                Text(
                    text = "Tasbeeh Counter",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Text(
                    text = "Digital prayer beads for Islamic dhikr",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                var count by remember { mutableStateOf(0) }
                
                Card {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.displayMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { count++ },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("+1")
                        }
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(onClick = { count = 0 }) {
                        Text("Reset")
                    }
                    
                    Button(onClick = { /* Save prayer session */ }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialMainScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Memorial Prayers") }
        )
        
        // Sample memorial data for display
        val sampleMemorials = remember {
            listOf(
                Memorial(
                    id = "1",
                    title = "Loving Memory of Grandfather",
                    deceasedName = "Ahmad bin Abdullah",
                    totalPrayers = 245,
                    description = "A wonderful grandfather who taught us about Islam"
                ),
                Memorial(
                    id = "2", 
                    title = "In Memory of Our Beloved Mother",
                    deceasedName = "Fatimah bint Hassan",
                    totalPrayers = 523,
                    description = "The most caring mother"
                )
            )
        }
        
        MemorialScreen(
            memorials = sampleMemorials,
            isLoading = false,
            onCreateMemorial = onNavigateToCreate,
            onMemorialClick = { memorial -> onNavigateToDetail(memorial.id) },
            onPrayForMemorial = { memorial ->
                // Handle prayer increment
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityMainScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToDiscovery: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Global Community") }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CommunityFeatureCard(
                    title = "Community Home",
                    description = "Join global prayer sessions",
                    icon = Icons.Default.Home,
                    onClick = onNavigateToHome
                )
            }
            
            item {
                CommunityFeatureCard(
                    title = "Prayer Leaderboard", 
                    description = "See top prayer contributors",
                    icon = Icons.Default.Leaderboard,
                    onClick = onNavigateToLeaderboard
                )
            }
            
            item {
                CommunityFeatureCard(
                    title = "Memorial Discovery",
                    description = "Discover memorials from around the world",
                    icon = Icons.Default.Search,
                    onClick = onNavigateToDiscovery
                )
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Global Prayer Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("1.2M", "Total Prayers")
                            StatItem("45K", "Active Users")
                            StatItem("180", "Countries")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMainScreen(
    onSignOut: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Profile") }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Profile Header
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "👤",
                            style = MaterialTheme.typography.displayMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Islamic User",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Text(
                            text = "Member since 2024",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                ProfileMenuItem(
                    title = "Prayer Statistics",
                    description = "View your prayer history",
                    icon = Icons.Default.Analytics,
                    onClick = { }
                )
            }
            
            item {
                ProfileMenuItem(
                    title = "Settings",
                    description = "App preferences and configuration",
                    icon = Icons.Default.Settings,
                    onClick = onNavigateToSettings
                )
            }
            
            item {
                ProfileMenuItem(
                    title = "Help & Support",
                    description = "Get help and contact support",
                    icon = Icons.Default.Help,
                    onClick = { }
                )
            }
            
            item {
                ProfileMenuItem(
                    title = "About",
                    description = "App information and credits",
                    icon = Icons.Default.Info,
                    onClick = { }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedButton(
                    onClick = {
                        authViewModel.handleAuthEvent(
                            com.app_muslim.surah_yasin.feature.auth.model.AuthEvent.SignOut
                        )
                        onSignOut()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out")
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}