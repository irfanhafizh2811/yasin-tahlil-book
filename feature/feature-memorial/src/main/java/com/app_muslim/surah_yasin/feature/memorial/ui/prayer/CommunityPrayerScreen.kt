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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.ui.prayer.viewmodel.CommunityPrayerViewModel

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