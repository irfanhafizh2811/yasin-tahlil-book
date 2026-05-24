package com.app_muslim.surah_yasin.feature.profile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought
import com.app_muslim.surah_yasin.feature.profile.model.ProfileData
import com.app_muslim.surah_yasin.feature.profile.model.ProfileEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalPreferencesSection(
    profileData: ProfileData?,
    isEditing: Boolean,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRegionSelector by remember { mutableStateOf(false) }
    var showSchoolSelector by remember { mutableStateOf(false) }
    var showLanguageSelector by remember { mutableStateOf(false) }

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
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Islamic Cultural Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "Configure your preferences according to Islamic traditions and regional customs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Islamic Region Selection
            if (isEditing) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRegionSelector = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Islamic Region",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profileData?.culturalPreferences?.region?.displayName ?: "Select your region",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Region"
                        )
                    }
                }
            } else {
                ProfileInfoItem(
                    label = "Islamic Region",
                    value = profileData?.culturalPreferences?.region?.displayName ?: "Not specified",
                    icon = Icons.Default.Public
                )
            }

            // School of Thought Selection
            if (isEditing) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSchoolSelector = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "School of Thought (Madhab)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profileData?.culturalPreferences?.schoolOfThought?.displayName ?: "Select your school",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select School of Thought"
                        )
                    }
                }
            } else {
                ProfileInfoItem(
                    label = "School of Thought (Madhab)",
                    value = profileData?.culturalPreferences?.schoolOfThought?.let { school ->
                        "${school.displayName} (${school.arabicName})"
                    } ?: "Not specified",
                    icon = Icons.Default.MenuBook
                )
            }

            // Primary Language
            if (isEditing) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageSelector = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Primary Language",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = getLanguageDisplayName(profileData?.culturalPreferences?.primaryLanguage ?: "en"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Language"
                        )
                    }
                }
            } else {
                ProfileInfoItem(
                    label = "Primary Language",
                    value = getLanguageDisplayName(profileData?.culturalPreferences?.primaryLanguage ?: "en"),
                    icon = Icons.Default.Language
                )
            }

            HorizontalDivider()

            // Arabic Text Display Preferences
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Text Display Preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                // Show Arabic Text Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Show Arabic Text",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Display original Arabic prayers and verses",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = profileData?.culturalPreferences?.showArabicText ?: true,
                        onCheckedChange = { 
                            if (isEditing) {
                                onEvent(ProfileEvent.UpdateArabicTextVisibility(it))
                            }
                        },
                        enabled = isEditing
                    )
                }

                // Show Transliteration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Show Transliteration",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Display pronunciation guide for Arabic text",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = profileData?.culturalPreferences?.showTransliteration ?: true,
                        onCheckedChange = { 
                            if (isEditing) {
                                onEvent(ProfileEvent.UpdateTransliterationVisibility(it))
                            }
                        },
                        enabled = isEditing
                    )
                }
            }
        }
    }

    // Region Selector Dialog
    if (showRegionSelector) {
        RegionSelectorDialog(
            selectedRegion = profileData?.culturalPreferences?.region ?: IslamicRegion.NOT_SPECIFIED,
            onRegionSelected = { region ->
                onEvent(ProfileEvent.UpdateCulturalRegion(region))
                showRegionSelector = false
            },
            onDismiss = { showRegionSelector = false }
        )
    }

    // School of Thought Selector Dialog
    if (showSchoolSelector) {
        SchoolOfThoughtDialog(
            selectedSchool = profileData?.culturalPreferences?.schoolOfThought ?: SchoolOfThought.NOT_SPECIFIED,
            onSchoolSelected = { school ->
                onEvent(ProfileEvent.UpdateSchoolOfThought(school))
                showSchoolSelector = false
            },
            onDismiss = { showSchoolSelector = false }
        )
    }

    // Language Selector Dialog
    if (showLanguageSelector) {
        LanguageSelectorDialog(
            selectedLanguage = profileData?.culturalPreferences?.primaryLanguage ?: "en",
            onLanguageSelected = { language ->
                onEvent(ProfileEvent.UpdateLanguage(language))
                showLanguageSelector = false
            },
            onDismiss = { showLanguageSelector = false }
        )
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

private fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        "en" -> "English"
        "ar" -> "العربية (Arabic)"
        "id" -> "Bahasa Indonesia"
        "ms" -> "Bahasa Malaysia"
        "ur" -> "اردو (Urdu)"
        "tr" -> "Türkçe (Turkish)"
        "fa" -> "فارسی (Persian)"
        "bn" -> "বাংলা (Bengali)"
        "hi" -> "हिन्दी (Hindi)"
        "fr" -> "Français (French)"
        "de" -> "Deutsch (German)"
        "es" -> "Español (Spanish)"
        "pt" -> "Português (Portuguese)"
        "ru" -> "Русский (Russian)"
        else -> "English"
    }
}