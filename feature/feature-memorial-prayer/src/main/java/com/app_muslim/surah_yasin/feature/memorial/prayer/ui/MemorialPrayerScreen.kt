package com.app_muslim.surah_yasin.feature.memorial.prayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel.MemorialPrayerViewModel
import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*

/**
 * Main screen for Memorial Prayer Sessions with Firebase integration
 * Real-time prayer tracking with celebrations and statistics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialPrayerScreen(
    memorialId: String,
    onNavigateBack: () -> Unit,
    viewModel: MemorialPrayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    
    // Celebration states
    var showCompletionCelebration by remember { mutableStateOf(false) }
    var showMilestoneCelebration by remember { mutableStateOf(false) }
    var milestonePercentage by remember { mutableStateOf(0) }
    
    // Monitor session completion
    LaunchedEffect(uiState.memorialPrayerState) {
        when (val state = uiState.memorialPrayerState) {
            is MemorialPrayerState.Completed -> {
                showCompletionCelebration = true
            }
            is MemorialPrayerState.InProgress -> {
                val percentage = (state.progress.currentCount.toFloat() / state.progress.targetCount.toFloat() * 100).toInt()
                val milestones = listOf(25, 50, 75)
                if (milestones.contains(percentage) && percentage != milestonePercentage) {
                    milestonePercentage = percentage
                    showMilestoneCelebration = true
                }
            }
            else -> {}
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with real-time sync indicator
            TopAppBar(
                title = { 
                    Column {
                        Text("Memorial Prayer Session")
                        if (uiState.isLoading) {
                            Text(
                                "Syncing with Firebase...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                actions = {
                    // Global prayer stats indicator
                    GlobalPrayerStatsChip(viewModel)
                }
            )
        
        when (val state = uiState.memorialPrayerState) {
            is MemorialPrayerState.Idle -> {
                PrayerSelectionCard(
                    onStartSession = { prayerType, targetCount ->
                        viewModel.handleEvent(
                            MemorialPrayerEvent.StartSession(memorialId, prayerType, targetCount)
                        )
                    }
                )
            }
            
            is MemorialPrayerState.InProgress -> {
                // Use enhanced animated prayer counter
                com.app_muslim.surah_yasin.feature.memorial.prayer.ui.components.AnimatedPrayerCounter(
                    currentCount = state.progress.currentCount,
                    targetCount = state.progress.targetCount,
                    onIncrement = { viewModel.handleEvent(MemorialPrayerEvent.IncrementPrayer) }
                )
                
                // Session controls
                PrayerSessionControls(
                    session = state.session,
                    onPauseSession = { viewModel.handleEvent(MemorialPrayerEvent.PauseSession) },
                    onCompleteSession = { viewModel.handleEvent(MemorialPrayerEvent.CompleteSession) }
                )
            }
            
            is MemorialPrayerState.Paused -> {
                PrayerSessionCard(
                    session = state.session,
                    progress = state.progress,
                    isPaused = true,
                    onIncrementPrayer = { viewModel.handleEvent(MemorialPrayerEvent.IncrementPrayer) },
                    onResumeSession = { viewModel.handleEvent(MemorialPrayerEvent.ResumeSession) },
                    onCompleteSession = { viewModel.handleEvent(MemorialPrayerEvent.CompleteSession) }
                )
            }
            
            is MemorialPrayerState.Completed -> {
                CompletedSessionCard(
                    session = state.session,
                    onStartNewSession = {
                        // Reset to idle state for new session
                    }
                )
            }
            
            is MemorialPrayerState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is MemorialPrayerState.Error -> {
                ErrorCard(
                    message = state.message,
                    onRetry = { /* TODO: Implement retry */ }
                )
            }
        }
        
        // Recent Sessions with real-time updates
        if (uiState.recentSessions.isNotEmpty()) {
            RecentSessionsCard(
                sessions = uiState.recentSessions,
                onSessionClick = { /* TODO: Navigate to session details */ }
            )
        }
        
        // Statistics with real-time Firebase data
        uiState.statistics?.let { stats ->
            FirebaseStatsCard(
                statistics = stats,
                memorialId = memorialId,
                viewModel = viewModel
            )
        }
    }
    
    // Milestone celebration overlay
    com.app_muslim.surah_yasin.feature.memorial.prayer.ui.components.MilestoneCelebration(
        isVisible = showMilestoneCelebration,
        milestonePercentage = milestonePercentage,
        prayerType = currentSession?.prayerType?.displayName ?: "Prayer",
        onDismiss = { showMilestoneCelebration = false }
    )
}

// Prayer completion celebration overlay
com.app_muslim.surah_yasin.feature.memorial.prayer.ui.components.PrayerCompletionCelebration(
    isVisible = showCompletionCelebration,
    prayerType = currentSession?.prayerType?.displayName ?: "Prayer",
    totalPrayers = currentSession?.prayerCount ?: 0,
    onDismiss = { 
        showCompletionCelebration = false
        onNavigateBack()
    }
)

// Handle error messages
uiState.errorMessage?.let { error ->
    LaunchedEffect(error) {
        // TODO: Show snackbar or dialog
    }
}
}

@Composable
private fun GlobalPrayerStatsChip(viewModel: MemorialPrayerViewModel) {
    val globalStats by viewModel.globalPrayerStats.collectAsStateWithLifecycle()
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🌍",
                fontSize = 12.sp
            )
            Text(
                text = "${globalStats["completedSessions"] ?: 0}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun PrayerSessionControls(
    session: MemorialPrayerSession,
    onPauseSession: () -> Unit,
    onCompleteSession: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onPauseSession,
            modifier = Modifier.weight(1f)
        ) {
            Text("Pause Session")
        }
        
        Button(
            onClick = onCompleteSession,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("Complete", color = Color.White)
        }
    }
}

@Composable
private fun FirebaseStatsCard(
    statistics: MemorialPrayerStats,
    memorialId: String,
    viewModel: MemorialPrayerViewModel
) {
    // Real-time stats from Firebase
    val realtimeStats by viewModel.getPrayerStatisticsFlow(memorialId).collectAsStateWithLifecycle(
        initialValue = statistics
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Prayer Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Sessions",
                    value = realtimeStats.totalSessions.toString()
                )
                StatItem(
                    label = "Total Prayers", 
                    value = realtimeStats.totalPrayers.toString()
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Current Streak",
                    value = "${realtimeStats.currentStreak} days"
                )
                StatItem(
                    label = "Favorite Prayer",
                    value = realtimeStats.mostUsedPrayerType.displayName
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PrayerSelectionCard(
    onStartSession: (PrayerType, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Prayer Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            PrayerType.values().forEach { prayerType ->
                PrayerTypeButton(
                    prayerType = prayerType,
                    onClick = { onStartSession(prayerType, prayerType.defaultCount) }
                )
            }
        }
    }
}

@Composable
private fun PrayerTypeButton(
    prayerType: PrayerType,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = prayerType.arabicName,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${prayerType.displayName} (${prayerType.defaultCount}x)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PrayerSessionCard(
    session: MemorialPrayerSession,
    progress: PrayerProgress,
    isPaused: Boolean = false,
    onIncrementPrayer: () -> Unit = {},
    onPauseSession: () -> Unit = {},
    onResumeSession: () -> Unit = {},
    onCompleteSession: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = session.prayerType.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = session.prayerType.arabicName,
                style = MaterialTheme.typography.titleMedium
            )
            
            // Progress
            LinearProgressIndicator(
                progress = progress.percentage / 100f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "${progress.currentCount} / ${progress.targetCount}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onIncrementPrayer,
                    enabled = !isPaused,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Increment")
                }
                
                if (isPaused) {
                    Button(
                        onClick = onResumeSession,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Resume")
                    }
                } else {
                    OutlinedButton(
                        onClick = onPauseSession,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pause")
                    }
                }
                
                OutlinedButton(
                    onClick = onCompleteSession,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Complete")
                }
            }
        }
    }
}

@Composable
private fun CompletedSessionCard(
    session: MemorialPrayerSession,
    onStartNewSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Session Completed!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "${session.prayerType.displayName}: ${session.prayerCount} prayers",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Button(
                onClick = onStartNewSession,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start New Session")
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun RecentSessionsCard(
    sessions: List<MemorialPrayerSession>,
    onSessionClick: (MemorialPrayerSession) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Recent Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // TODO: Implement recent sessions list
        }
    }
}

@Composable
private fun StatisticsCard(
    statistics: MemorialPrayerStats
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text("Total Sessions: ${statistics.totalSessions}")
            Text("Total Prayers: ${statistics.totalPrayers}")
            Text("Current Streak: ${statistics.currentStreak} days")
        }
    }
}