package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.core.firebase.sharing.*

/**
 * Privacy Controls Screen - Compose implementation
 * Allows users to manage memorial sharing privacy and access controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyControlsScreen(
    memorialId: String,
    memorialName: String,
    onNavigateBack: () -> Unit,
    viewModel: PrivacyControlsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(memorialId) {
        viewModel.loadSharingSettings(memorialId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        PrivacyControlsTopBar(
            memorialName = memorialName,
            onNavigateBack = onNavigateBack,
            onSave = { 
                viewModel.saveSharingSettings(memorialId)
            },
            isSaving = uiState.isSaving
        )
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Islamic Privacy Header
                IslamicPrivacyHeaderCard()
            }
            
            item {
                // Family Sharing Settings
                FamilySharingSettingsCard(
                    settings = uiState.settings,
                    onUpdateSettings = { updatedSettings ->
                        viewModel.updateSettings(updatedSettings)
                    }
                )
            }
            
            item {
                // Social Media Sharing Settings
                SocialMediaSharingSettingsCard(
                    settings = uiState.settings,
                    onUpdateSettings = { updatedSettings ->
                        viewModel.updateSettings(updatedSettings)
                    }
                )
            }
            
            item {
                // Access Control Settings
                AccessControlSettingsCard(
                    settings = uiState.settings,
                    onUpdateSettings = { updatedSettings ->
                        viewModel.updateSettings(updatedSettings)
                    }
                )
            }
            
            item {
                // Advanced Privacy Settings
                AdvancedPrivacySettingsCard(
                    settings = uiState.settings,
                    onUpdateSettings = { updatedSettings ->
                        viewModel.updateSettings(updatedSettings)
                    }
                )
            }
            
            item {
                // Islamic Guidelines Section
                IslamicGuidelinesCard()
            }
        }
    }
    
    // Loading indicator
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Success/Error Messages
    uiState.message?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyControlsTopBar(
    memorialName: String,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Privacy & Sharing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memorialName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
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
            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun IslamicPrivacyHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Islamic Privacy Values",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "\"And it is He who conceals sins and loves those who conceal them\"\n\nControl who can see and participate in your memorial prayers while respecting Islamic values of family privacy.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun FamilySharingSettingsCard(
    settings: MemorialSharingSettings,
    onUpdateSettings: (MemorialSharingSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Family Sharing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Allow Family Sharing Toggle
            SettingToggleRow(
                title = "Allow Family Invitations",
                description = "Let family members invite others to pray for this memorial",
                checked = settings.allowFamilySharing,
                onCheckedChange = { enabled ->
                    onUpdateSettings(settings.copy(allowFamilySharing = enabled))
                }
            )
            
            AnimatedVisibility(
                visible = settings.allowFamilySharing,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Require approval for family access
                    SettingToggleRow(
                        title = "Require Approval",
                        description = "Family invitations need your approval before granting access",
                        checked = settings.requireApprovalForAccess,
                        onCheckedChange = { enabled ->
                            onUpdateSettings(settings.copy(requireApprovalForAccess = enabled))
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Maximum family members
                    SettingSliderRow(
                        title = "Maximum Family Members",
                        description = "Limit how many family members can access this memorial",
                        value = settings.maxSimultaneousAccess,
                        range = 5..50,
                        onValueChange = { value ->
                            onUpdateSettings(settings.copy(maxSimultaneousAccess = value))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SocialMediaSharingSettingsCard(
    settings: MemorialSharingSettings,
    onUpdateSettings: (MemorialSharingSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Social Media Sharing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Allow Social Media Sharing Toggle
            SettingToggleRow(
                title = "Allow Social Media Sharing",
                description = "Enable sharing memorial prayers on WhatsApp, Telegram, and other platforms",
                checked = settings.allowSocialMediaSharing,
                onCheckedChange = { enabled ->
                    onUpdateSettings(settings.copy(allowSocialMediaSharing = enabled))
                }
            )
            
            AnimatedVisibility(
                visible = settings.allowSocialMediaSharing,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Link expiration setting
                    SettingSliderRow(
                        title = "Link Expiration (Days)",
                        description = "How long sharing links remain valid",
                        value = settings.linkExpirationDays,
                        range = 1..30,
                        onValueChange = { value ->
                            onUpdateSettings(settings.copy(linkExpirationDays = value))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessControlSettingsCard(
    settings: MemorialSharingSettings,
    onUpdateSettings: (MemorialSharingSettings) -> Unit
) {
    var showAdvanced by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Access Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                IconButton(
                    onClick = { showAdvanced = !showAdvanced }
                ) {
                    Icon(
                        imageVector = if (showAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Advanced Settings"
                    )
                }
            }
            
            AnimatedVisibility(
                visible = showAdvanced,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Advanced access control features coming soon...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvancedPrivacySettingsCard(
    settings: MemorialSharingSettings,
    onUpdateSettings: (MemorialSharingSettings) -> Unit
) {
    var customMessageTemplate by remember { mutableStateOf(settings.sharingMessageTemplate) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Custom Sharing Message",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = customMessageTemplate,
                onValueChange = { 
                    customMessageTemplate = it
                    onUpdateSettings(settings.copy(sharingMessageTemplate = it))
                },
                label = { Text("Custom Message Template (Optional)") },
                placeholder = {
                    Text("Customize the message sent with memorial invitations...")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Leave empty to use default Islamic message templates",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun IslamicGuidelinesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🤲 Islamic Guidelines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• Share memorial prayers with respect and dignity\n" +
                        "• Only invite people who knew the deceased or family\n" +
                        "• Maintain privacy of personal family matters\n" +
                        "• Use appropriate Islamic language in messages\n" +
                        "• Remember the purpose is to pray, not socialize",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingSliderRow(
    title: String,
    description: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
    }
}