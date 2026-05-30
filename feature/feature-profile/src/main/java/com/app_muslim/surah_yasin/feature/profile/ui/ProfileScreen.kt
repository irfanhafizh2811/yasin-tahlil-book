package com.app_muslim.surah_yasin.feature.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.coroutines.flow.MutableStateFlow
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.profile.model.*
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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

// Preview Parameter Provider for Profile Screen states
class ProfileScreenStateProvider : PreviewParameterProvider<ProfileUiState> {
    override val values = sequenceOf(
        // Loading state
        ProfileUiState(
            isLoading = true
        ),
        
        // View mode with complete profile - Ahmad Ibn Muhammad (Saudi Arabia)
        ProfileUiState(
            isLoading = false,
            isEditing = false,
            profileData = ProfileData(
                userId = "ahmad_muhammad_001",
                displayName = "Ahmad Ibn Muhammad",
                email = "ahmad.muhammad@example.com",
                phoneNumber = "+966501234567",
                profilePhotoUrl = "https://example.com/photos/ahmad.jpg",
                isVerified = true,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Saudi Arabia",
                    primaryLanguage = "ar",
                    schoolOfThought = SchoolOfThought.HANBALI,
                    showArabicText = true,
                    showTransliteration = false
                ),
                notificationPreferences = NotificationPreferencesData(
                    memorialReminders = true,
                    fridayNightReminders = true,
                    communityUpdates = false
                ),
                privacySettings = PrivacySettingsData(
                    profileVisibility = "COMMUNITY",
                    prayerStatsVisibility = "FRIENDS_ONLY"
                ),
                prayerPreferences = PrayerPreferencesData(
                    defaultPrayerType = "YASIN",
                    enableHapticFeedback = true
                ),
                profileCompleteness = 95
            )
        ),
        
        // Edit mode with Southeast Asian profile - Fatimah Zahra (Indonesia)
        ProfileUiState(
            isLoading = false,
            isEditing = true,
            profileData = ProfileData(
                userId = "fatimah_zahra_002",
                displayName = "Fatimah Zahra Rahman",
                email = "fatimah.zahra@gmail.com",
                phoneNumber = "+62812345678",
                isVerified = false,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.SOUTHEAST_ASIA,
                    country = "Indonesia",
                    primaryLanguage = "id",
                    schoolOfThought = SchoolOfThought.SHAFI,
                    showArabicText = true,
                    showTransliteration = true
                ),
                privacySettings = PrivacySettingsData(
                    profileVisibility = "PRIVATE",
                    allowFamilyInvitations = true
                ),
                profileCompleteness = 75
            )
        ),
        
        // Incomplete profile - New user from Pakistan
        ProfileUiState(
            isLoading = false,
            isEditing = false,
            profileData = ProfileData(
                userId = "hassan_ali_003",
                displayName = "Hassan Ali",
                email = "hassan.ali@outlook.com",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.SOUTH_ASIA,
                    country = "Pakistan",
                    primaryLanguage = "ur",
                    schoolOfThought = SchoolOfThought.NOT_SPECIFIED
                ),
                profileCompleteness = 45
            )
        ),

        // Error state during profile editing
        ProfileUiState(
            isLoading = false,
            isEditing = true,
            errorMessage = "Failed to save profile. Please check your connection and try again.",
            profileData = ProfileData(
                userId = "omar_farooq_004",
                displayName = "Omar Ibn Farooq",
                email = "omar.farooq@example.com",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Egypt",
                    primaryLanguage = "ar"
                )
            )
        ),
        
        // Photo selector active state
        ProfileUiState(
            isLoading = false,
            isEditing = true,
            showPhotoSelector = true,
            profileData = ProfileData(
                userId = "mariam_hassan_005",
                displayName = "Mariam Hassan",
                email = "mariam.hassan@email.com",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.NORTH_AFRICA,
                    country = "Morocco",
                    primaryLanguage = "ar"
                )
            )
        )
    )
}

// Preview-safe content function
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    formState: ProfileFormState,
    onNavigateBack: () -> Unit,
    onEvent: (ProfileEvent) -> Unit
) {
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        Row {
                            TextButton(
                                onClick = { onEvent(ProfileEvent.CancelEditing) }
                            ) {
                                Text("Cancel")
                            }
                            TextButton(
                                onClick = { onEvent(ProfileEvent.SaveProfile) },
                                enabled = formState.isValid && !uiState.isLoading
                            ) {
                                Text("Save")
                            }
                        }
                    } else {
                        IconButton(
                            onClick = { onEvent(ProfileEvent.StartEditing) }
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
                            onEvent = onEvent
                        )
                    }

                    // Basic Information Section
                    item {
                        BasicInformationSection(
                            profileData = uiState.profileData,
                            formState = formState,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
                        )
                    }

                    // Islamic Cultural Preferences
                    item {
                        CulturalPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
                        )
                    }

                    // Privacy Settings
                    item {
                        PrivacySettingsSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
                        )
                    }

                    // Notification Preferences
                    item {
                        NotificationPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
                        )
                    }

                    // Prayer Preferences
                    item {
                        PrayerPreferencesSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
                        )
                    }

                    // Account Settings
                    item {
                        AccountSettingsSection(
                            profileData = uiState.profileData,
                            isEditing = uiState.isEditing,
                            onEvent = onEvent
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
                        onEvent(ProfileEvent.SelectProfilePhoto(uri))
                    },
                    onRemovePhoto = {
                        onEvent(ProfileEvent.RemoveProfilePhoto)
                    },
                    onDismiss = {
                        onEvent(ProfileEvent.HidePhotoSelector)
                    }
                )
            }
        }
    }
}

// Helper function to create sample UI state for previews
private fun getSampleProfileUiState(isLoading: Boolean = false, isEditing: Boolean = false): ProfileUiState {
    return ProfileUiState(
        isLoading = isLoading,
        isEditing = isEditing,
        profileData = ProfileData(
            userId = "ahmad_ibn_hassan",
            displayName = "Ahmad Ibn Hassan",
            email = "ahmad.hassan@example.com",
            phoneNumber = "+966501234567",
            culturalPreferences = CulturalPreferencesData(
                region = IslamicRegion.MIDDLE_EAST,
                country = "Saudi Arabia",
                primaryLanguage = "ar",
                schoolOfThought = SchoolOfThought.HANBALI,
                showArabicText = true,
                showTransliteration = false
            ),
            isVerified = true,
            profileCompleteness = 85
        ),
        errorMessage = null,
        isProfileSaved = false,
        showPhotoSelector = false
    )
}

// Helper function to create sample form state for previews
private fun getSampleFormState(): ProfileFormState {
    return ProfileFormState(
        isValid = true,
        displayNameError = null,
        emailError = null,
        phoneNumberError = null
    )
}

// Profile Screen Previews
@Preview(showBackground = true, name = "Profile Screen - Loading")
@Composable
private fun PreviewProfileScreenLoading() {
    TahlilTheme {
        ProfileContent(
            uiState = getSampleProfileUiState(isLoading = true),
            formState = getSampleFormState(),
            onNavigateBack = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Dark", 
         uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewProfileScreenDark() {
    TahlilTheme {
        ProfileContent(
            uiState = getSampleProfileUiState(),
            formState = getSampleFormState(),
            onNavigateBack = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Tablet",
         device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PreviewProfileScreenTablet() {
    TahlilTheme {
        ProfileContent(
            uiState = getSampleProfileUiState(isEditing = true),
            formState = getSampleFormState(),
            onNavigateBack = {},
            onEvent = {}
        )
    }
}
