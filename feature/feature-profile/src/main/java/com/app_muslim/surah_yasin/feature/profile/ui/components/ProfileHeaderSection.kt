package com.app_muslim.surah_yasin.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImage
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent
import com.app_muslim.surah_yasin.feature.profile.model.CulturalPreferencesData
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileHeaderSection(
    profileData: ProfileData?,
    isEditing: Boolean,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Photo
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                if (!profileData?.profilePhotoUrl.isNullOrBlank() || profileData?.profilePhotoUri != null) {
                    AsyncImage(
                        model = profileData?.profilePhotoUri ?: profileData?.profilePhotoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(enabled = isEditing) {
                                onEvent(ProfileEvent.ShowPhotoSelector)
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(enabled = isEditing) {
                                onEvent(ProfileEvent.ShowPhotoSelector)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Photo edit button
                if (isEditing) {
                    FloatingActionButton(
                        onClick = { onEvent(ProfileEvent.ShowPhotoSelector) },
                        modifier = Modifier.size(32.dp),
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            // Display Name
            Text(
                text = profileData?.displayName ?: "Unknown User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            // Email
            if (!profileData?.email.isNullOrBlank()) {
                Text(
                    text = profileData?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            // Verification Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (profileData?.isVerified == true) {
                        Icons.Default.Verified
                    } else {
                        Icons.Default.Warning
                    },
                    contentDescription = if (profileData?.isVerified == true) {
                        "Verified"
                    } else {
                        "Not Verified"
                    },
                    modifier = Modifier.size(16.dp),
                    tint = if (profileData?.isVerified == true) {
                        Color(0xFF4CAF50) // Green
                    } else {
                        Color(0xFFFFA726) // Orange
                    }
                )
                Text(
                    text = if (profileData?.isVerified == true) {
                        "Verified Account"
                    } else {
                        "Account Not Verified"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // Profile Completeness
            ProfileCompletenessIndicator(
                completeness = profileData?.calculateCompleteness() ?: 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileCompletenessIndicator(
    completeness: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile Completeness",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$completeness%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        LinearProgressIndicator(
            progress = { completeness / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = when {
                completeness >= 80 -> Color(0xFF4CAF50) // Green
                completeness >= 60 -> Color(0xFFFFA726) // Orange
                else -> Color(0xFFF44336) // Red
            }
        )

        if (completeness < 100) {
            Text(
                text = "Complete your profile to enhance your experience",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Preview Parameter Provider for different profile states
class ProfileHeaderPreviewProvider : PreviewParameterProvider<ProfileData?> {
    override val values = sequenceOf(
        null, // No profile data
        ProfileData(
            userId = "1",
            email = "ahmed.hassan@example.com",
            displayName = "Ahmed Hassan",
            profilePhotoUrl = null,
            profilePhotoUri = null,
            isVerified = false,
            phoneNumber = null
        ),
        ProfileData(
            userId = "2",
            email = "fatima.alzahra@example.com",
            displayName = "Fatima Al-Zahra",
            profilePhotoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
            profilePhotoUri = null,
            isVerified = true,
            phoneNumber = "+1234567890"
        ),
        ProfileData(
            userId = "3",
            email = "mohammad.abdullah@example.com",
            displayName = "Mohammad Abdullah Ibn Ahmad Al-Masri",
            profilePhotoUrl = "https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=Mohammad",
            profilePhotoUri = null,
            isVerified = true,
            phoneNumber = "+9876543210"
        ),
        ProfileData(
            userId = "4",
            email = "incomplete@example.com",
            displayName = "Incomplete User",
            profilePhotoUrl = null,
            profilePhotoUri = null,
            isVerified = false,
            phoneNumber = null
        )
    )
}

@Preview(name = "Profile Header - No Data", showBackground = true)
@Composable
private fun ProfileHeaderSectionNoDataPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = null,
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Basic Profile", showBackground = true)
@Composable
private fun ProfileHeaderSectionBasicPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "1",
                email = "ahmed.hassan@example.com",
                displayName = "Ahmed Hassan",
                profilePhotoUrl = null,
                profilePhotoUri = null,
                isVerified = false,
                phoneNumber = null
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Complete Profile", showBackground = true)
@Composable
private fun ProfileHeaderSectionCompletePreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "2",
                email = "fatima.alzahra@example.com",
                displayName = "Fatima Al-Zahra",
                profilePhotoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
                profilePhotoUri = null,
                isVerified = true,
                phoneNumber = "+1234567890",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Saudi Arabia",
                    primaryLanguage = "Arabic",
                    schoolOfThought = SchoolOfThought.HANAFI
                )
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Editing Mode", showBackground = true)
@Composable
private fun ProfileHeaderSectionEditingPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "2",
                email = "fatima.alzahra@example.com",
                displayName = "Fatima Al-Zahra",
                profilePhotoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
                profilePhotoUri = null,
                isVerified = true,
                phoneNumber = "+1234567890",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Saudi Arabia",
                    primaryLanguage = "Arabic",
                    schoolOfThought = SchoolOfThought.HANAFI
                )
            ),
            isEditing = true,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Long Name", showBackground = true)
@Composable
private fun ProfileHeaderSectionLongNamePreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "3",
                email = "mohammad.abdullah@example.com",
                displayName = "Mohammad Abdullah Ibn Ahmad Al-Masri Al-Qurashi",
                profilePhotoUrl = "https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=Mohammad",
                profilePhotoUri = null,
                isVerified = true,
                phoneNumber = "+9876543210",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.NORTH_AFRICA,
                    country = "Egypt",
                    primaryLanguage = "Arabic",
                    schoolOfThought = SchoolOfThought.MALIKI
                )
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Unverified", showBackground = true)
@Composable
private fun ProfileHeaderSectionUnverifiedPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "4",
                email = "new.user@example.com",
                displayName = "New User",
                profilePhotoUrl = null,
                profilePhotoUri = null,
                isVerified = false,
                phoneNumber = null
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - No Email", showBackground = true)
@Composable
private fun ProfileHeaderSectionNoEmailPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "5",
                email = null,
                displayName = "Anonymous User",
                profilePhotoUrl = null,
                profilePhotoUri = null,
                isVerified = false,
                phoneNumber = null
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Profile Header - Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileHeaderSectionDarkPreview() {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = ProfileData(
                userId = "2",
                email = "aisha.bintomar@example.com",
                displayName = "Aisha Bint Omar",
                profilePhotoUrl = "https://via.placeholder.com/200x200/FF9800/FFFFFF?text=Aisha",
                profilePhotoUri = null,
                isVerified = true,
                phoneNumber = "+1122334455",
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Saudi Arabia",
                    primaryLanguage = "Arabic",
                    schoolOfThought = SchoolOfThought.SHAFI
                )
            ),
            isEditing = false,
            onEvent = { }
        )
    }
}

@Preview(name = "Completeness Indicator - Low", showBackground = true)
@Composable
private fun ProfileCompletenessIndicatorLowPreview() {
    TahlilTheme {
        ProfileCompletenessIndicator(
            completeness = 25
        )
    }
}

@Preview(name = "Completeness Indicator - Medium", showBackground = true)
@Composable
private fun ProfileCompletenessIndicatorMediumPreview() {
    TahlilTheme {
        ProfileCompletenessIndicator(
            completeness = 65
        )
    }
}

@Preview(name = "Completeness Indicator - High", showBackground = true)
@Composable
private fun ProfileCompletenessIndicatorHighPreview() {
    TahlilTheme {
        ProfileCompletenessIndicator(
            completeness = 85
        )
    }
}

@Preview(name = "Completeness Indicator - Complete", showBackground = true)
@Composable
private fun ProfileCompletenessIndicatorCompletePreview() {
    TahlilTheme {
        ProfileCompletenessIndicator(
            completeness = 100
        )
    }
}

@Preview(name = "Dynamic Profile Header", showBackground = true)
@Composable
private fun ProfileHeaderSectionDynamicPreview(
    @PreviewParameter(ProfileHeaderPreviewProvider::class) profileData: ProfileData?
) {
    TahlilTheme {
        ProfileHeaderSection(
            profileData = profileData,
            isEditing = false,
            onEvent = { }
        )
    }
}