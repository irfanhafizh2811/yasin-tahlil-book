package com.app_muslim.surah_yasin.feature.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent
import com.app_muslim.surah_yasin.feature.profile.viewmodel.ProfileViewModel
import com.app_muslim.surah_yasin.feature.profile.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    // Handle errors
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Error will be shown in UI
        }
    }

    // Handle profile saved
    LaunchedEffect(uiState.isProfileSaved) {
        if (uiState.isProfileSaved) {
            // Profile saved successfully
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
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
                    if (uiState.isEditing) {
                        Row {
                            TextButton(
                                onClick = { viewModel.handleProfileEvent(ProfileEvent.CancelEditing) }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = { viewModel.handleProfileEvent(ProfileEvent.SaveProfile) },
                                enabled = formState.isValid && !uiState.isLoading
                            ) {
                                Text("Save")
                            }
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.handleProfileEvent(ProfileEvent.StartEditing) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile"
                            )
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header with Photo and Basic Info
                    item {
                        ProfileHeaderSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Basic Information Section
                    item {
                        BasicInformationSection(
                            profileData = uiState.profileData,
                            formState = formState,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Islamic Cultural Preferences
                    item {
                        CulturalPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Privacy Settings
                    item {
                        PrivacySettingsSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Notification Preferences
                    item {
                        NotificationPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Prayer Preferences
                    item {
                        PrayerPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }

                    // Account Settings
                    item {
                        AccountSettingsSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = viewModel::handleProfileEvent
                        )
                    }
                }
            }

            // Error Snackbar
            uiState.errorMessage?.let { errorMessage ->
                LaunchedEffect(errorMessage) {
                    // Show error snackbar
                }
            }

            // Photo Selector Dialog
            if (uiState.showPhotoSelector) {
                ProfilePhotoSelector(
                    onPhotoSelected = { uri ->
                        viewModel.handleProfileEvent(ProfileEvent.SelectProfilePhoto(uri))
                    },
                    onRemovePhoto = {
                        viewModel.handleProfileEvent(ProfileEvent.RemoveProfilePhoto)
                    },
                    onDismiss = {
                        viewModel.handleProfileEvent(ProfileEvent.HidePhotoSelector)
                    }
                )
            }
        }
    }
}