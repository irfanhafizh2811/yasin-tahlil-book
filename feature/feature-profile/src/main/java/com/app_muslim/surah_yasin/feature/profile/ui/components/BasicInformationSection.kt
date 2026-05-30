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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent
import com.app_muslim.surah_yasin.feature.profile.model.ProfileFormState
import com.app_muslim.surah_yasin.feature.profile.model.CulturalPreferencesData

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

// Basic Information Section Previews
@Preview(name = "Basic Info View - Complete Saudi", showBackground = true)
@Composable
fun BasicInformationSectionCompletePreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "ahmad_muhammad_001",
                displayName = "Ahmad Ibn Muhammad Al-Faisal",
                email = "ahmad.muhammad@example.com",
                phoneNumber = "+966501234567",
                isVerified = true,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    country = "Saudi Arabia"
                )
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Edit - Indonesia", showBackground = true)
@Composable
fun BasicInformationSectionEditPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "siti_fatimah_002",
                displayName = "Siti Fatimah Zahra",
                email = "siti.fatimah@gmail.com",
                phoneNumber = "+62812345678",
                isVerified = false
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = true,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Edit - Validation Errors", showBackground = true)
@Composable
fun BasicInformationSectionErrorsPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "omar_test_003",
                displayName = "",
                email = "invalid-email",
                phoneNumber = "123"
            ),
            formState = ProfileFormState(
                displayNameError = "Display name is required",
                emailError = "Invalid email format",
                phoneNumberError = "Phone number must include country code",
                isValid = false
            ),
            isEditing = true,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info View - Unverified Email", showBackground = true)
@Composable
fun BasicInformationSectionUnverifiedPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "mariam_hassan_004",
                displayName = "Mariam Hassan Al-Zahra",
                email = "mariam.hassan@email.com",
                phoneNumber = "+20123456789",
                isVerified = false,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.NORTH_AFRICA,
                    country = "Egypt"
                )
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Minimal Profile", showBackground = true)
@Composable
fun BasicInformationSectionMinimalPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "yusuf_new_005",
                displayName = "Yusuf Ibrahim",
                email = "",
                phoneNumber = ""
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Empty State", showBackground = true)
@Composable
fun BasicInformationSectionEmptyPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = null,
            formState = ProfileFormState(isValid = false),
            isEditing = true,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Pakistani Profile", showBackground = true)
@Composable
fun BasicInformationSectionPakistanPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "hassan_ali_006",
                displayName = "Hassan Ali Khan",
                email = "hassan.ali@outlook.com",
                phoneNumber = "",
                isVerified = true,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.SOUTH_ASIA,
                    country = "Pakistan",
                    primaryLanguage = "ur"
                )
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasicInformationSectionDarkPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "ahmed_omar_007",
                displayName = "Ahmed Omar Al-Rashid",
                email = "ahmed.omar@example.com",
                phoneNumber = "+971501234567",
                isVerified = true
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info Tablet", showBackground = true, widthDp = 840)
@Composable
fun BasicInformationSectionTabletPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "khadijah_008",
                displayName = "Khadijah Rahman",
                email = "khadijah.rahman@example.com",
                phoneNumber = "+60123456789",
                isVerified = true,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.SOUTHEAST_ASIA,
                    country = "Malaysia"
                )
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}

@Preview(name = "Basic Info RTL Arabic", showBackground = true, locale = "ar")
@Composable
fun BasicInformationSectionRTLPreview() {
    TahlilTheme {
        BasicInformationSection(
            profileData = ProfileData(
                userId = "abdullah_009",
                displayName = "عبد الله عمر الفاروق",
                email = "abdullah.omar@example.com",
                phoneNumber = "+966505678901",
                isVerified = true,
                culturalPreferences = CulturalPreferencesData(
                    region = IslamicRegion.MIDDLE_EAST,
                    primaryLanguage = "ar"
                )
            ),
            formState = ProfileFormState(isValid = true),
            isEditing = false,
            onEvent = {}
        )
    }
}