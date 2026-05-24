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
import coil.compose.AsyncImage
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent

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