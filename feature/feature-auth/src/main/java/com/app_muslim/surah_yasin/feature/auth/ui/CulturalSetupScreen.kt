package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.common.model.IslamicRegion
import com.app_muslim.surah_yasin.core.common.model.SchoolOfThought

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalSetupScreen(
    onSetupComplete: (IslamicRegion, SchoolOfThought, String) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedRegion by remember { mutableStateOf<IslamicRegion?>(null) }
    var selectedSchool by remember { mutableStateOf<SchoolOfThought?>(null) }
    var selectedLanguage by remember { mutableStateOf("en") }

    val steps = listOf("Region", "School", "Language")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            steps.forEachIndexed { index, step ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isActive = index == currentStep
                    val isCompleted = index < currentStep
                    
                    Card(
                        modifier = Modifier
                            .size(40.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isActive -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        when (currentStep) {
            0 -> RegionSelectionStep(
                selectedRegion = selectedRegion,
                onRegionSelected = { selectedRegion = it }
            )
            1 -> SchoolSelectionStep(
                selectedSchool = selectedSchool,
                onSchoolSelected = { selectedSchool = it },
                selectedRegion = selectedRegion
            )
            2 -> LanguageSelectionStep(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Previous")
                }
            }
            
            Button(
                onClick = {
                    if (currentStep < steps.size - 1) {
                        currentStep++
                    } else {
                        // Complete setup
                        onSetupComplete(
                            selectedRegion ?: IslamicRegion.NOT_SPECIFIED,
                            selectedSchool ?: SchoolOfThought.NOT_SPECIFIED,
                            selectedLanguage
                        )
                    }
                },
                enabled = when (currentStep) {
                    0 -> selectedRegion != null
                    1 -> selectedSchool != null
                    2 -> selectedLanguage.isNotEmpty()
                    else -> false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (currentStep == steps.size - 1) "Complete" else "Next"
                )
            }
        }
    }
}

@Composable
private fun RegionSelectionStep(
    selectedRegion: IslamicRegion?,
    onRegionSelected: (IslamicRegion) -> Unit
) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "Select Your Region",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "This helps us provide culturally appropriate Islamic content",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Region Options
        LazyColumn(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val regions = listOf(
                IslamicRegion.MIDDLE_EAST to "Middle East & North Africa",
                IslamicRegion.SOUTH_ASIA to "South Asia (Pakistan, India, Bangladesh)",
                IslamicRegion.SOUTHEAST_ASIA to "Southeast Asia (Indonesia, Malaysia)",
                IslamicRegion.NORTH_AFRICA to "North Africa (Morocco, Algeria, Tunisia)",
                IslamicRegion.CENTRAL_ASIA to "Central Asia (Afghanistan, Uzbekistan)",
                IslamicRegion.SUB_SAHARAN_AFRICA to "Sub-Saharan Africa",
                IslamicRegion.EUROPE to "Europe & Western Countries",
                IslamicRegion.NORTH_AMERICA to "North America (US, Canada)",
                IslamicRegion.SOUTH_AMERICA to "South America",
                IslamicRegion.OCEANIA to "Oceania (Australia, New Zealand)",
                IslamicRegion.NOT_SPECIFIED to "Prefer not to specify"
            )
            
            items(regions) { (region, displayName) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedRegion == region,
                            onClick = { onRegionSelected(region) },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRegion == region) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRegion == region,
                            onClick = null
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SchoolSelectionStep(
    selectedSchool: SchoolOfThought?,
    onSchoolSelected: (SchoolOfThought) -> Unit,
    selectedRegion: IslamicRegion?
) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "School of Thought",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Select your Islamic jurisprudence tradition (Madhab)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // School Options
        LazyColumn(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val schools = listOf(
                SchoolOfThought.HANAFI to "Hanafi (Most common in Turkey, Central Asia, Indian subcontinent)",
                SchoolOfThought.MALIKI to "Maliki (Predominantly in North and West Africa)",
                SchoolOfThought.SHAFI to "Shafi'i (Common in Southeast Asia, East Africa)",
                SchoolOfThought.HANBALI to "Hanbali (Mainly in Saudi Arabia and Gulf states)",
                SchoolOfThought.JAFARI to "Ja'fari (Twelver Shia)",
                SchoolOfThought.OTHER to "Other school of thought",
                SchoolOfThought.NOT_SPECIFIED to "Prefer not to specify"
            )
            
            items(schools) { (school, description) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedSchool == school,
                            onClick = { onSchoolSelected(school) },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedSchool == school) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSchool == school,
                            onClick = null
                        )
                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = school.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectionStep(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Choose your preferred language for prayers and interface",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Language Options
        LazyColumn(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val languages = listOf(
                "ar" to "العربية (Arabic)",
                "en" to "English",
                "id" to "Bahasa Indonesia",
                "ur" to "اردو (Urdu)",
                "tr" to "Türkçe (Turkish)",
                "fa" to "فارسی (Persian/Farsi)",
                "ms" to "Bahasa Melayu (Malay)",
                "bn" to "বাংলা (Bengali)",
                "hi" to "हिन्दी (Hindi)",
                "fr" to "Français (French)"
            )
            
            items(languages) { (code, name) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedLanguage == code,
                            onClick = { onLanguageSelected(code) },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLanguage == code) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == code,
                            onClick = null
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}