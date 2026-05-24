package com.app_muslim.surah_yasin.feature.memorial.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.viewmodel.CreateMemorialViewModel
import com.app_muslim.surah_yasin.feature.memorial.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemorialScreen(
    onNavigateBack: () -> Unit,
    onMemorialCreated: (String) -> Unit,
    viewModel: CreateMemorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validationState by viewModel.validationState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isMemorialCreated) {
        if (uiState.isMemorialCreated && uiState.createdMemorialId.isNotEmpty()) {
            onMemorialCreated(uiState.createdMemorialId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Memorial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            CreateMemorialBottomBar(
                isLoading = uiState.isLoading,
                isValid = validationState.isValid,
                onCreateMemorial = { viewModel.createMemorial() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Islamic Header
            IslamicHeaderCard()
            
            // Deceased Information Section
            DeceasedInformationSection(
                deceasedName = uiState.deceasedName,
                deceasedNameArabic = uiState.deceasedNameArabic,
                onDeceasedNameChange = viewModel::updateDeceasedName,
                onDeceasedNameArabicChange = viewModel::updateDeceasedNameArabic,
                errors = validationState.errors
            )
            
            // Date Selection Section
            DateSelectionSection(
                gregorianDate = uiState.dateOfDeath,
                hijriDate = uiState.hijriDate,
                onGregorianDateChange = viewModel::updateDateOfDeath,
                onHijriDateChange = viewModel::updateHijriDate
            )
            
            // Memorial Message Section
            MemorialMessageSection(
                message = uiState.memorialMessage,
                messageArabic = uiState.memorialMessageArabic,
                onMessageChange = viewModel::updateMemorialMessage,
                onMessageArabicChange = viewModel::updateMemorialMessageArabic,
                errors = validationState.errors
            )
            
            // Prayer Type Selection
            PrayerTypeSection(
                selectedPrayerType = uiState.prayerType,
                onPrayerTypeChange = viewModel::updatePrayerType
            )
            
            // Privacy Level Selection
            PrivacyLevelSection(
                selectedPrivacy = uiState.privacyLevel,
                onPrivacyChange = viewModel::updatePrivacyLevel
            )
            
            // Photo Upload Section (Optional)
            PhotoUploadSection(
                photoUrl = uiState.photoUrl,
                isUploading = uiState.isUploadingPhoto,
                onPhotoSelected = viewModel::uploadPhoto,
                onPhotoRemoved = viewModel::removePhoto
            )
            
            // Validation Errors Display
            if (validationState.errors.isNotEmpty()) {
                ValidationErrorsCard(errors = validationState.errors)
            }
        }
    }
    
    // Loading Dialog
    if (uiState.isLoading) {
        CreateMemorialLoadingDialog()
    }
    
    // Error Dialog
    uiState.error?.let { error ->
        ErrorDialog(
            error = error,
            onDismiss = viewModel::clearError
        )
    }
}

@Composable
private fun IslamicHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "In the name of Allah, the Most Gracious, the Most Merciful",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create a memorial to honor your loved one and invite the global Muslim community to pray for their soul.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CreateMemorialBottomBar(
    isLoading: Boolean,
    isValid: Boolean,
    onCreateMemorial: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCreateMemorial,
                enabled = isValid && !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Creating..." else "Create Memorial",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CreateMemorialLoadingDialog() {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss while loading */ },
        title = {
            Text(
                text = "Creating Memorial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please wait while we create the memorial for your loved one...",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إِنَّا لِلّهِ وَإِنَّـا إِلَيْهِ رَاجِعُونَ",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Indeed we belong to Allah, and indeed to Him we will return",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = { }
    )
}

@Composable
private fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Error Creating Memorial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}