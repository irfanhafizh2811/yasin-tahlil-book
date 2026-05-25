package com.app_muslim.surah_yasin.feature.memorial.ui.validation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.ui.validation.viewmodel.ContentValidationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentValidationScreen(
    validationId: String,
    onNavigateBack: () -> Unit,
    viewModel: ContentValidationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validation by viewModel.currentValidation.collectAsStateWithLifecycle()

    LaunchedEffect(validationId) {
        viewModel.loadValidation(validationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Validation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (validation?.validationStatus == ValidationStatus.PENDING) {
                        IconButton(
                            onClick = { viewModel.refreshValidation(validationId) }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
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
            } else if (uiState.error != null) {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadValidation(validationId) }
                )
            } else if (validation != null) {
                ValidationContent(
                    validation = validation!!,
                    onRequestScholarReview = { viewModel.requestScholarReview(validationId) },
                    onAcceptSuggestions = { suggestions ->
                        viewModel.applySuggestions(validationId, suggestions)
                    }
                )
            }
            
            // Handle error display
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // Auto-clear error after some time
                    kotlinx.coroutines.delay(5000)
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun ValidationContent(
    validation: IslamicContentValidation,
    onRequestScholarReview: () -> Unit,
    onAcceptSuggestions: (List<ContentSuggestion>) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Validation Status
        item {
            ValidationStatusCard(validation = validation)
        }

        // Original Content
        item {
            OriginalContentCard(validation = validation)
        }

        // Validation Results
        if (validation.inappropriateFlags.isNotEmpty()) {
            item {
                InappropriateFlagsCard(flags = validation.inappropriateFlags)
            }
        }

        // Suggestions
        if (validation.suggestedCorrections.isNotEmpty()) {
            item {
                SuggestionsCard(
                    suggestions = validation.suggestedCorrections,
                    onAcceptSuggestions = onAcceptSuggestions
                )
            }
        }

        // Scores
        item {
            ValidationScoresCard(validation = validation)
        }

        // Actions
        if (validation.validationStatus == ValidationStatus.NEEDS_REVISION ||
            validation.validationStatus == ValidationStatus.PENDING) {
            item {
                ValidationActionsCard(
                    validation = validation,
                    onRequestScholarReview = onRequestScholarReview
                )
            }
        }

        // Scholar Review (if available)
        // This would be implemented when scholar review system is added
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValidationStatusCard(validation: IslamicContentValidation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (validation.validationStatus) {
                ValidationStatus.APPROVED -> MaterialTheme.colorScheme.primaryContainer
                ValidationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
                ValidationStatus.PENDING, ValidationStatus.UNDER_REVIEW -> MaterialTheme.colorScheme.secondaryContainer
                ValidationStatus.NEEDS_REVISION -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (validation.validationStatus) {
                        ValidationStatus.APPROVED -> Icons.Default.CheckCircle
                        ValidationStatus.REJECTED -> Icons.Default.Cancel
                        ValidationStatus.PENDING, ValidationStatus.UNDER_REVIEW -> Icons.Default.Schedule
                        ValidationStatus.NEEDS_REVISION -> Icons.Default.Edit
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = when (validation.validationStatus) {
                        ValidationStatus.APPROVED -> MaterialTheme.colorScheme.primary
                        ValidationStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Status: ${validation.validationStatus.name.replace('_', ' ')}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (validation.validationNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validation.validationNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (validation.rejectionReason?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason: ${validation.rejectionReason}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OriginalContentCard(validation: IslamicContentValidation) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Original Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Type: ${validation.contentType.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Language: ${validation.language}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SelectionContainer {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = validation.contentText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InappropriateFlagsCard(flags: List<InappropriateFlag>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Issues Found (${flags.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            flags.forEach { flag ->
                FlagItem(flag = flag)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FlagItem(flag: InappropriateFlag) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when (flag.flagType) {
                        FlagType.CULTURAL_INSENSITIVITY -> Icons.Default.People
                        FlagType.RELIGIOUS_INAPPROPRIATENESS -> Icons.Default.Place
                        FlagType.OFFENSIVE_LANGUAGE -> Icons.Default.Warning
                        else -> Icons.Default.Flag
                    },
                    contentDescription = null,
                    tint = when (flag.severity) {
                        FlagSeverity.CRITICAL, FlagSeverity.BLOCKING -> MaterialTheme.colorScheme.error
                        FlagSeverity.HIGH -> Color(0xFFFF6B35)
                        FlagSeverity.MEDIUM -> Color(0xFFFFB347)
                        FlagSeverity.LOW -> MaterialTheme.colorScheme.primary
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${flag.flagType.name.replace('_', ' ')} (${flag.severity.name})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (flag.flaggedText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Text: \"${flag.flaggedText}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (flag.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = flag.explanation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestionsCard(
    suggestions: List<ContentSuggestion>,
    onAcceptSuggestions: (List<ContentSuggestion>) -> Unit
) {
    var selectedSuggestions by remember { mutableStateOf(emptySet<Int>()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Suggestions (${suggestions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (selectedSuggestions.isNotEmpty()) {
                    Button(
                        onClick = {
                            val selected = suggestions.filterIndexed { index, _ ->
                                index in selectedSuggestions
                            }
                            onAcceptSuggestions(selected)
                        }
                    ) {
                        Text("Apply Selected")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            suggestions.forEachIndexed { index, suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    isSelected = index in selectedSuggestions,
                    onToggleSelection = {
                        selectedSuggestions = if (index in selectedSuggestions) {
                            selectedSuggestions - index
                        } else {
                            selectedSuggestions + index
                        }
                    }
                )
                if (index < suggestions.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: ContentSuggestion,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    Card(
        onClick = onToggleSelection,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = suggestion.category.name.replace('_', ' '),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = suggestion.reason,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (suggestion.originalText != suggestion.suggestedText) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Original:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = suggestion.originalText,
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Suggested:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = suggestion.suggestedText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValidationScoresCard(validation: IslamicContentValidation) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Validation Scores",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ScoreRow(
                label = "Islamic Compliance",
                score = validation.islamicComplianceScore,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ScoreRow(
                label = "Cultural Sensitivity",
                score = validation.culturalSensitivityScore,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun ScoreRow(
    label: String,
    score: Float,
    color: Color
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = score,
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValidationActionsCard(
    validation: IslamicContentValidation,
    onRequestScholarReview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Available Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (validation.validationStatus == ValidationStatus.NEEDS_REVISION) {
                OutlinedButton(
                    onClick = onRequestScholarReview,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.School, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Scholar Review")
                }
            }
            
            if (validation.validationStatus == ValidationStatus.PENDING) {
                OutlinedButton(
                    onClick = { /* Handle resubmission */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Resubmit for Validation")
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Validation Error",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}