package com.app_muslim.surah_yasin.feature.memorial.prayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel.MemorialPrayerViewModel
import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*

/**
 * Main screen for Memorial Prayer Sessions
 * Provides interface for starting, managing, and tracking prayer sessions
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        TopAppBar(
            title = { Text("Memorial Prayer Session") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    // TODO: Add back icon
                    Text("←")
                }
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
                PrayerSessionCard(
                    session = state.session,
                    progress = state.progress,
                    onIncrementPrayer = { viewModel.handleEvent(MemorialPrayerEvent.IncrementPrayer) },
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
        
        // Recent Sessions
        if (uiState.recentSessions.isNotEmpty()) {
            RecentSessionsCard(
                sessions = uiState.recentSessions,
                onSessionClick = { /* TODO: Navigate to session details */ }
            )
        }
        
        // Statistics
        uiState.statistics?.let { stats ->
            StatisticsCard(statistics = stats)
        }
    }
    
    // Handle error messages
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show snackbar or dialog
        }
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