package com.app_muslim.surah_yasin.feature.memorial.prayer.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel.MemorialPrayerViewModel

/**
 * Main Memorial Prayer Screen
 * Provides focused prayer counter for memorial prayers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialPrayerScreen(
    memorialId: String? = null,
    memorialName: String? = null,
    memorialPhotoUrl: String? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToMemorialList: () -> Unit = {},
    viewModel: MemorialPrayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val prayerStats by viewModel.prayerStats.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    // Handle completion dialog
    if (uiState.showCompletionDialog) {
        PrayerCompletionDialog(
            session = currentSession,
            stats = prayerStats,
            onDismiss = viewModel::dismissCompletionDialog,
            onStartNewSession = {
                if (memorialId != null && memorialName != null) {
                    viewModel.startNewPrayerSession(
                        memorialId = memorialId,
                        memorialName = memorialName,
                        memorialPhotoUrl = memorialPhotoUrl,
                        prayerType = MemorialPrayerType.TAHLIL
                    )
                }
            }
        )
    }

    // Handle error display
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentSession?.memorialName ?: "Memorial Prayers",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (currentSession == null) {
                        IconButton(onClick = onNavigateToMemorialList) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Memorial List"
                            )
                        }
                    }
                    
                    IconButton(onClick = viewModel::refreshData) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                when {
                    currentSession != null -> {
                        // Active prayer session UI
                        ActivePrayerSessionContent(
                            session = currentSession!!,
                            onIncrement = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.incrementPrayerCount()
                            },
                            onPause = viewModel::pausePrayerSession,
                            onResume = viewModel::resumePrayerSession,
                            onComplete = viewModel::completePrayerSession,
                            onCancel = viewModel::cancelPrayerSession,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    uiState.canStartNewSession && memorialId != null && memorialName != null -> {
                        // Start new session UI
                        StartNewSessionContent(
                            memorialName = memorialName,
                            memorialPhotoUrl = memorialPhotoUrl,
                            stats = prayerStats,
                            recentSessions = uiState.recentSessions,
                            onStartSession = { prayerType, targetCount ->
                                viewModel.startNewPrayerSession(
                                    memorialId = memorialId,
                                    memorialName = memorialName,
                                    memorialPhotoUrl = memorialPhotoUrl,
                                    prayerType = prayerType,
                                    customTargetCount = targetCount
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    else -> {
                        // No memorial selected
                        EmptyStateContent(
                            onSelectMemorial = onNavigateToMemorialList,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Active prayer session content with counter
 */
@Composable
private fun ActivePrayerSessionContent(
    session: MemorialPrayerSession,
    onIncrement: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prayerText = MemorialPrayerTexts.getTextForPrayerType(session.prayerType)
    val progress by animateFloatAsState(
        targetValue = session.progress,
        animationSpec = tween(300),
        label = "progress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Memorial info and prayer text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Memorial photo
            if (session.memorialPhotoUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(session.memorialPhotoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Memorial photo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Memorial name
            Text(
                text = session.memorialName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prayer type
            Text(
                text = session.prayerType.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Prayer text
            PrayerTextDisplay(
                prayerText = prayerText,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Prayer counter
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress and count display
            Text(
                text = "${session.currentCount} / ${session.targetCount}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Circular progress counter
            Box(
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                
                // Background circle
                Canvas(
                    modifier = Modifier.size(240.dp)
                ) {
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.3f),
                        style = Stroke(width = 16.dp.toPx())
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Counter button
                Button(
                    onClick = onIncrement,
                    modifier = Modifier.size(180.dp),
                    shape = CircleShape,
                    enabled = session.state == PrayerSessionState.IN_PROGRESS,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap to count",
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = when (session.state) {
                                PrayerSessionState.IN_PROGRESS -> "TAP"
                                PrayerSessionState.PAUSED -> "PAUSED"
                                else -> "DONE"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pause/Resume
                if (session.state == PrayerSessionState.IN_PROGRESS) {
                    OutlinedButton(
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pause")
                    }
                } else if (session.state == PrayerSessionState.PAUSED) {
                    Button(
                        onClick = onResume,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resume")
                    }
                }

                // Complete (if near target or manual complete)
                if (session.currentCount >= session.targetCount || session.currentCount > 0) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete")
                    }
                }

                // Cancel
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * Prayer text display component
 */
@Composable
private fun PrayerTextDisplay(
    prayerText: PrayerTextContent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Arabic text
            Text(
                text = prayerText.arabicText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transliteration
            Text(
                text = prayerText.transliteration,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation
            Text(
                text = prayerText.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (prayerText.meaning.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = prayerText.meaning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Start new session content
 */
@Composable
private fun StartNewSessionContent(
    memorialName: String,
    memorialPhotoUrl: String?,
    stats: PrayerSessionStats,
    recentSessions: List<MemorialPrayerSession>,
    onStartSession: (MemorialPrayerType, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPrayerType by remember { mutableStateOf(MemorialPrayerType.TAHLIL) }
    var customTargetCount by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Memorial info
        if (memorialPhotoUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(memorialPhotoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Memorial photo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = memorialName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Begin your memorial prayers",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Prayer type selection
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Prayer Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                MemorialPrayerType.values().forEach { prayerType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPrayerType = prayerType }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPrayerType == prayerType,
                            onClick = { selectedPrayerType = prayerType }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = prayerType.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${prayerType.arabicName} • Default: ${prayerType.defaultTarget}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Custom target count
        OutlinedTextField(
            value = customTargetCount,
            onValueChange = { customTargetCount = it },
            label = { Text("Custom target (optional)") },
            placeholder = { Text("Default: ${selectedPrayerType.defaultTarget}") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Start button
        Button(
            onClick = {
                val target = customTargetCount.toIntOrNull()
                onStartSession(selectedPrayerType, target)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Begin Prayer Session",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Statistics and recent sessions
        if (stats.totalSessions > 0 || recentSessions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            
            PrayerStatsDisplay(
                stats = stats,
                recentSessions = recentSessions,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Empty state when no memorial is selected
 */
@Composable
private fun EmptyStateContent(
    onSelectMemorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Memorial Selected",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose a memorial to begin your prayer session",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSelectMemorial,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Choose Memorial")
        }
    }
}