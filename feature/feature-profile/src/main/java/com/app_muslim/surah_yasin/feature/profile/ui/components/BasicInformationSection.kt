package com.app_muslim.surah_yasin.feature.profile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent
import com.app_muslim.surah_yasin.feature.profile.model.ProfileFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInformationSection(
    profileData: ProfileData?,
    formState: ProfileFormState,
    isEditing: Boolean,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Display Name Field
            if (isEditing) {
                OutlinedTextField(
                    value = profileData?.displayName ?: "",
                    onValueChange = { onEvent(ProfileEvent.UpdateDisplayName(it)) },
                    label = { Text("Display Name *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Display Name"
                        )
                    },
                    isError = formState.displayNameError != null,
                    supportingText = formState.displayNameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                ProfileInfoItem(
                    label = "Display Name",
                    value = profileData?.displayName ?: "Not set",
                    icon = Icons.Default.Badge
                )
            }

            // Email Field
            if (isEditing) {
                OutlinedTextField(
                    value = profileData?.email ?: "",
                    onValueChange = { onEvent(ProfileEvent.UpdateEmail(it)) },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    trailingIcon = {
                        if (profileData?.isVerified == true) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    isError = formState.emailError != null,
                    supportingText = formState.emailError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
            } else {
                ProfileInfoItem(
                    label = "Email Address",
                    value = if (!profileData?.email.isNullOrBlank()) {
                        "${profileData?.email} ${if (profileData?.isVerified == true) "✓" else "(Unverified)"}"
                    } else {
                        "Not set"
                    },
                    icon = Icons.Default.Email
                )
            }

            // Phone Number Field
            if (isEditing) {
                OutlinedTextField(
                    value = profileData?.phoneNumber ?: "",
                    onValueChange = { onEvent(ProfileEvent.UpdatePhoneNumber(it)) },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone"
                        )
                    },
                    isError = formState.phoneNumberError != null,
                    supportingText = formState.phoneNumberError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            } else {
                ProfileInfoItem(
                    label = "Phone Number",
                    value = profileData?.phoneNumber ?: "Not set",
                    icon = Icons.Default.Phone
                )
            }

            // Account Creation Info (Read-only)
            HorizontalDivider()
            
            ProfileInfoItem(
                label = "Account Type",
                value = "Islamic Memorial Platform",
                icon = Icons.Default.AccountCircle
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}