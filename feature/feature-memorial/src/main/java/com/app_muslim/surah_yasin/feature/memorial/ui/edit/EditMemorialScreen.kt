package com.app_muslim.surah_yasin.feature.memorial.ui.edit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.ui.components.*
import com.app_muslim.surah_yasin.feature.memorial.ui.edit.viewmodel.EditMemorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemorialScreen(
    memorialId: String,
    onNavigateBack: () -> Unit,
    onMemorialUpdated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditMemorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validationState by viewModel.validationState.collectAsStateWithLifecycle()

    LaunchedEffect(memorialId) {
        viewModel.loadMemorial(memorialId)
    }

    LaunchedEffect(uiState.isUpdateComplete) {
        if (uiState.isUpdateComplete) {
            onMemorialUpdated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Memorial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.updateMemorial() },
                        enabled = validationState.isValid && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        
        when {
            uiState.isInitialLoading -> {
                LoadingContent(modifier = Modifier.fillMaxSize())
            }
            
            uiState.error != null -> {
                val error = uiState.error ?: "Unknown error"
                ErrorContent(
                    error = error,
                    onRetry = { viewModel.loadMemorial(memorialId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            uiState.memorial != null -> {
                val memorial = uiState.memorial ?: return@Scaffold
                EditMemorialContent(
                    memorial = memorial,
                    validationState = validationState,
                    onFieldChange = viewModel::updateField,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
        
        // Show error snackbar
        if (uiState.error != null) {
            LaunchedEffect(uiState.error) {
                // Show snackbar for error
                // You might want to pass SnackbarHostState from parent
            }
        }
    }
}

@Composable
private fun EditMemorialContent(
    memorial: com.app_muslim.surah_yasin.feature.memorial.model.MemorialData,
    validationState: com.app_muslim.surah_yasin.feature.memorial.ui.edit.viewmodel.EditMemorialValidationState,
    onFieldChange: (String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // Memorial Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Edit Memorial Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Update the memorial details below. Changes will be reflected immediately for all participants.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
        
        // Deceased Information Section
        DeceasedInformationSection(
            deceasedName = memorial.deceasedName,
            deceasedNameArabic = memorial.deceasedNameArabic ?: "",
            onDeceasedNameChange = { onFieldChange("deceasedName", it) },
            onDeceasedNameArabicChange = { onFieldChange("deceasedNameArabic", it) },
            errors = buildValidationErrors(validationState.deceasedNameError, validationState.deceasedNameArabicError)
        )
        
        // Date Selection Section
        DateSelectionSection(
            gregorianDate = memorial.dateOfDeath,
            hijriDate = memorial.dateOfDeathHijri,
            onGregorianDateChange = { onFieldChange("dateOfDeath", it) },
            onHijriDateChange = { onFieldChange("dateOfDeathHijri", it) }
        )
        
        // Memorial Message Section
        MemorialMessageSection(
            message = memorial.memorialMessage,
            messageArabic = memorial.memorialMessageArabic ?: "",
            onMessageChange = { onFieldChange("memorialMessage", it) },
            onMessageArabicChange = { onFieldChange("memorialMessageArabic", it) },
            errors = buildValidationErrors(validationState.memorialMessageError, validationState.memorialMessageArabicError)
        )
        
        // Prayer Type Section
        PrayerTypeSection(
            selectedPrayerType = memorial.prayerType,
            onPrayerTypeSelected = { onFieldChange("prayerType", it) }
        )
        
        // Privacy Level Section
        PrivacyLevelSection(
            selectedPrivacy = memorial.privacyLevel,
            onPrivacyChange = { onFieldChange("privacyLevel", it) }
        )
        
        // Photo Upload Section
        PhotoUploadSection(
            photoUrl = memorial.photoUrl,
            isUploading = false,
            onPhotoSelected = { /* handle photo selection */ },
            onPhotoRemoved = { onFieldChange("photoUrl", "") }
        )
        
        // Update Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Last Updated",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = java.text.SimpleDateFormat(
                        "MMMM d, yyyy 'at' h:mm a",
                        java.util.Locale.getDefault()
                    ).format(memorial.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
        
        // Add bottom padding for floating action button
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = "Loading memorial...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "Failed to load memorial",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

// Add missing components that might not exist yet
@Composable
private fun PrayerTypeSection(
    selectedPrayerType: com.app_muslim.surah_yasin.feature.memorial.model.PrayerType,
    onPrayerTypeSelected: (com.app_muslim.surah_yasin.feature.memorial.model.PrayerType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Prayer Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        com.app_muslim.surah_yasin.feature.memorial.model.PrayerType.values().forEach { prayerType ->
            RadioButtonWithLabel(
                selected = selectedPrayerType == prayerType,
                onClick = { onPrayerTypeSelected(prayerType) },
                label = prayerType.displayName,
                description = prayerType.description
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    description: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// Helper function to convert string errors to ValidationError list
private fun buildValidationErrors(vararg errors: String?): List<com.app_muslim.surah_yasin.feature.memorial.model.ValidationError> {
    return errors.filterNotNull().mapNotNull { error ->
        when (error) {
            "Deceased name is required" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.DECEASED_NAME_EMPTY
            "Name must be at least 2 characters" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.DECEASED_NAME_TOO_SHORT
            "Name must not exceed 100 characters" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.DECEASED_NAME_TOO_LONG
            "Arabic name must not exceed 100 characters" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.ARABIC_TEXT_INVALID
            "Date of death cannot be in the future" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.INVALID_DATE_OF_DEATH
            "Invalid date" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.INVALID_DATE_OF_DEATH
            "Memorial message must not exceed 1000 characters" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.MEMORIAL_MESSAGE_TOO_LONG
            "Arabic memorial message must not exceed 1000 characters" -> com.app_muslim.surah_yasin.feature.memorial.model.ValidationError.MEMORIAL_MESSAGE_TOO_LONG
            else -> null
        }
    }
}