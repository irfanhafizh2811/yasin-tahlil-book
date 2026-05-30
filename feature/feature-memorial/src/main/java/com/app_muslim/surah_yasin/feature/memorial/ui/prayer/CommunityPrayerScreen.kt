package com.app_muslim.surah_yasin.feature.memorial.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.ui.prayer.viewmodel.CommunityPrayerViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPrayerScreen(
    memorialId: String,
    onNavigateBack: () -> Unit,
    onNavigateToSession: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityPrayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // val communityStats by viewModel.communityStats.collectAsStateWithLifecycle() // Comment out temporarily

    LaunchedEffect(memorialId) {
        viewModel.loadCommunityData(memorialId)
    }

    Scaffold(
        topBar = {
            CommunityPrayerTopBar(
                onNavigateBack = onNavigateBack,
                onRefresh = { viewModel.refreshCommunityData() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCommunityPrayerSession() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Start Community Prayer"
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                uiState.error?.let { errorMessage ->
                    ErrorContent(
                        error = errorMessage,
                        onRetry = { viewModel.loadCommunityData(memorialId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            else -> {
                // Temporarily simplified to fix compilation
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Community Prayer Feature",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(Under Development)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Temporarily comment out dialog to fix compilation
        // Show create group dialog
        // if (uiState.showCreateGroupDialog) {
        //     CreatePrayerGroupDialog(
        //         onDismiss = { viewModel.hideCreateGroupDialog() },
        //         onConfirm = { groupName, prayerType, scheduledTime ->
        //             viewModel.createPrayerGroup(groupName, prayerType, scheduledTime)
        //         }
        //     )
        // }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityPrayerTopBar(
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Community Prayer")
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Failed to load community data",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

// Preview Data Providers
class CommunityPrayerUiStateProvider : PreviewParameterProvider<CommunityPrayerUiState> {
    override val values: Sequence<CommunityPrayerUiState> = sequenceOf(
        // Loading state
        CommunityPrayerUiState(isLoading = true),
        
        // Error state
        CommunityPrayerUiState(
            error = "Unable to connect to the community prayer service. Please check your internet connection."
        ),
        
        // Empty community state
        CommunityPrayerUiState(
            isLoading = false,
            error = null,
            communityStats = CommunityStats(
                totalParticipants = 0,
                activePrayerSessions = 0,
                completedPrayers = 0
            )
        ),
        
        // Active community state
        CommunityPrayerUiState(
            isLoading = false,
            error = null,
            communityStats = CommunityStats(
                totalParticipants = 247,
                activePrayerSessions = 12,
                completedPrayers = 1564
            ),
            showCreateGroupDialog = false
        ),
        
        // Show create group dialog state
        CommunityPrayerUiState(
            isLoading = false,
            error = null,
            communityStats = CommunityStats(
                totalParticipants = 89,
                activePrayerSessions = 5,
                completedPrayers = 456
            ),
            showCreateGroupDialog = true
        )
    )
}

// Mock data classes for previews
data class CommunityPrayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val communityStats: CommunityStats? = null,
    val showCreateGroupDialog: Boolean = false
)

data class CommunityStats(
    val totalParticipants: Int = 0,
    val activePrayerSessions: Int = 0,
    val completedPrayers: Int = 0
)

// Mock preview components
@Composable
fun CommunityPrayerScreenPreview(
    uiState: CommunityPrayerUiState = CommunityPrayerUiState()
) {
    CommunityPrayerContent(
        communityStats = CommunityStats(
            totalParticipants = uiState.communityStats?.totalParticipants ?: 150,
            activePrayerSessions = uiState.communityStats?.activePrayerSessions ?: 8,
            completedPrayers = uiState.communityStats?.completedPrayers ?: 892
        ),
        onStartPrayerSession = { }
    )
}

// Enhanced content components for active development preview
@Composable
fun CommunityPrayerContent(
    communityStats: CommunityStats,
    onStartPrayerSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Community Stats Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "بسم الله الرحمن الرحيم",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Community Prayer for the Deceased",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "Participants",
                value = communityStats.totalParticipants.toString(),
                icon = Icons.Default.People
            )
            StatCard(
                title = "Active Sessions",
                value = communityStats.activePrayerSessions.toString(),
                icon = Icons.Default.GroupWork
            )
            StatCard(
                title = "Completed Prayers",
                value = communityStats.completedPrayers.toString(),
                icon = Icons.Default.Favorite
            )
        }
        
        // Prayer Types Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Available Prayer Types",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                PrayerTypeItem(
                    title = "Surah Yasin",
                    description = "Recite Surah Yasin for the soul of the deceased",
                    participants = 45
                )
                PrayerTypeItem(
                    title = "Tahlil",
                    description = "Islamic remembrance and supplication",
                    participants = 32
                )
                PrayerTypeItem(
                    title = "Dua",
                    description = "Personal prayers for the departed soul",
                    participants = 28
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.size(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun PrayerTypeItem(
    title: String,
    description: String,
    participants: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = "$participants praying",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// Preview Functions
@Preview(name = "Community Prayer Screen - Under Development")
@Composable
fun PreviewCommunityPrayerScreenDevelopment() {
    TahlilTheme {
        Surface {
            CommunityPrayerScreenPreview()
        }
    }
}

@Preview(name = "Community Prayer Screen - Loading")
@Composable
fun PreviewCommunityPrayerScreenLoading() {
    TahlilTheme {
        Surface {
            CommunityPrayerScreenPreview(
                uiState = CommunityPrayerUiState(isLoading = true)
            )
        }
    }
}

@Preview(name = "Community Prayer Screen - Error")
@Composable
fun PreviewCommunityPrayerScreenError() {
    TahlilTheme {
        Surface {
            CommunityPrayerScreenPreview(
                uiState = CommunityPrayerUiState(
                    error = "Unable to connect to the community prayer service. Please check your internet connection."
                )
            )
        }
    }
}

@Preview(name = "Community Prayer Screen - Dynamic States", group = "Dynamic")
@Composable
fun PreviewCommunityPrayerScreenDynamic(
    @PreviewParameter(CommunityPrayerUiStateProvider::class) uiState: CommunityPrayerUiState
) {
    TahlilTheme {
        Surface {
            CommunityPrayerScreenPreview(uiState = uiState)
        }
    }
}

@Preview(name = "Community Prayer Screen - Dark Theme")
@Composable
fun PreviewCommunityPrayerScreenDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            CommunityPrayerScreenPreview(
                uiState = CommunityPrayerUiState(
                    communityStats = CommunityStats(
                        totalParticipants = 150,
                        activePrayerSessions = 8,
                        completedPrayers = 892
                    )
                )
            )
        }
    }
}

@Preview(name = "Community Prayer Content - Active")
@Composable
fun PreviewCommunityPrayerContentActive() {
    TahlilTheme {
        Surface {
            CommunityPrayerContent(
                communityStats = CommunityStats(
                    totalParticipants = 247,
                    activePrayerSessions = 12,
                    completedPrayers = 1564
                ),
                onStartPrayerSession = { }
            )
        }
    }
}

@Preview(name = "Community Prayer Top Bar")
@Composable
fun PreviewCommunityPrayerTopBar() {
    TahlilTheme {
        Surface {
            CommunityPrayerTopBar(
                onNavigateBack = { },
                onRefresh = { }
            )
        }
    }
}

@Preview(name = "Stat Cards")
@Composable
fun PreviewStatCards() {
    TahlilTheme {
        Surface {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Participants",
                    value = "247",
                    icon = Icons.Default.People
                )
                StatCard(
                    title = "Sessions",
                    value = "12",
                    icon = Icons.Default.GroupWork
                )
                StatCard(
                    title = "Prayers",
                    value = "1.5K",
                    icon = Icons.Default.Favorite
                )
            }
        }
    }
}

@Preview(name = "Prayer Type Items")
@Composable
fun PreviewPrayerTypeItems() {
    TahlilTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrayerTypeItem(
                    title = "Surah Yasin",
                    description = "Recite Surah Yasin for the soul of the deceased",
                    participants = 45
                )
                PrayerTypeItem(
                    title = "Tahlil",
                    description = "Islamic remembrance and supplication",
                    participants = 32
                )
                PrayerTypeItem(
                    title = "Dua",
                    description = "Personal prayers for the departed soul",
                    participants = 28
                )
            }
        }
    }
}

@Preview(name = "Loading Content")
@Composable
fun PreviewLoadingContentCommunity() {
    TahlilTheme {
        Surface {
            LoadingContent(modifier = Modifier.size(200.dp))
        }
    }
}

@Preview(name = "Error Content")
@Composable
fun PreviewErrorContentCommunity() {
    TahlilTheme {
        Surface {
            ErrorContent(
                error = "Network connection timeout. The community prayer service is temporarily unavailable.",
                onRetry = { }
            )
        }
    }
}