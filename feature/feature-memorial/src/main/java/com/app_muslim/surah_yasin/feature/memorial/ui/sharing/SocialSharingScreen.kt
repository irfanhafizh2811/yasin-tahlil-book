package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.core.firebase.sharing.*

/**
 * Social Media Sharing Screen - Compose implementation
 * Allows users to share memorial prayers on various platforms
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSharingScreen(
    memorialId: String,
    memorialName: String,
    deceasedName: String,
    onNavigateBack: () -> Unit,
    viewModel: SocialSharingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LaunchedEffect(memorialId) {
        viewModel.loadSharingData(memorialId, memorialName, deceasedName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        SocialSharingTopBar(
            memorialName = memorialName,
            onNavigateBack = onNavigateBack
        )
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Memorial Info Card
                MemorialInfoCard(
                    memorialName = memorialName,
                    deceasedName = deceasedName
                )
            }
            
            item {
                // Quick Share Options
                QuickShareCard(
                    onSharePlatform = { platform ->
                        viewModel.shareToSocialMedia(context, platform)
                    },
                    isLoading = uiState.isLoading
                )
            }
            
            item {
                // Sharing Link Generator
                SharingLinkCard(
                    sharingLink = uiState.sharingLink,
                    onGenerateLink = { sharingType ->
                        viewModel.generateSharingLink(sharingType)
                    },
                    onCopyLink = { link ->
                        viewModel.copyLinkToClipboard(context, link)
                    },
                    isGeneratingLink = uiState.isGeneratingLink
                )
            }
            
            item {
                // Custom Message Editor
                CustomMessageCard(
                    customMessage = uiState.customMessage,
                    onUpdateMessage = { message ->
                        viewModel.updateCustomMessage(message)
                    }
                )
            }
            
            item {
                // Sharing Analytics
                if (uiState.analytics != null) {
                    SharingAnalyticsCard(
                        analytics = uiState.analytics!!
                    )
                }
            }
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
private fun SocialSharingTopBar(
    memorialName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Share Memorial",
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun MemorialInfoCard(
    memorialName: String,
    deceasedName: String
) {
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
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Share Memorial Prayers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Invite others to join in prayer for $deceasedName",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickShareCard(
    onSharePlatform: (SharingPlatform) -> Unit,
    isLoading: Boolean
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
            Text(
                text = "Quick Share",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Social Media Platforms Grid
            val sharingPlatforms = getSharingPlatforms()
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(sharingPlatforms) { platform ->
                    SocialPlatformCard(
                        platform = platform,
                        onTap = { onSharePlatform(platform.type) },
                        isEnabled = !isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun SharingLinkCard(
    sharingLink: String?,
    onGenerateLink: (SharingType) -> Unit,
    onCopyLink: (String) -> Unit,
    isGeneratingLink: Boolean
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
            Text(
                text = "Sharing Link",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (sharingLink != null) {
                // Display generated link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sharingLink,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    IconButton(
                        onClick = { onCopyLink(sharingLink) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Link expires in 7 days • Share responsibly",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                // Generate link options
                Text(
                    text = "Generate a secure sharing link",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SharingType.values().forEach { type ->
                        LinkTypeButton(
                            type = type,
                            onGenerate = { onGenerateLink(type) },
                            isLoading = isGeneratingLink
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomMessageCard(
    customMessage: String,
    onUpdateMessage: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedMessage by remember { mutableStateOf(customMessage) }
    
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
                Text(
                    text = "Custom Message",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (isEditing) {
                    Row {
                        TextButton(
                            onClick = {
                                isEditing = false
                                editedMessage = customMessage // Reset
                            }
                        ) {
                            Text("Cancel")
                        }
                        
                        TextButton(
                            onClick = {
                                onUpdateMessage(editedMessage)
                                isEditing = false
                            }
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    IconButton(
                        onClick = { 
                            isEditing = true
                            editedMessage = customMessage
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Message"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = editedMessage,
                    onValueChange = { editedMessage = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    placeholder = {
                        Text("Enter your custom sharing message...")
                    }
                )
            } else {
                Text(
                    text = customMessage.ifBlank { "Using default Islamic sharing message" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (customMessage.isBlank()) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
private fun SharingAnalyticsCard(
    analytics: MemorialSharingAnalytics
) {
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
                text = "Sharing Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnalyticsItem(
                    icon = Icons.Default.Share,
                    value = analytics.totalShares.toString(),
                    label = "Total Shares"
                )
                
                AnalyticsItem(
                    icon = Icons.Default.TouchApp,
                    value = analytics.linkClicks.toString(),
                    label = "Link Clicks"
                )
                
                AnalyticsItem(
                    icon = Icons.Default.Group,
                    value = analytics.accessesGranted.toString(),
                    label = "Joined"
                )
            }
        }
    }
}

@Composable
private fun SocialPlatformCard(
    platform: SharingPlatformInfo,
    onTap: () -> Unit,
    isEnabled: Boolean
) {
    Card(
        onClick = onTap,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled,
        colors = CardDefaults.cardColors(
            containerColor = platform.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = platform.icon,
                contentDescription = platform.name,
                modifier = Modifier.size(32.dp),
                tint = platform.color
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = platform.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LinkTypeButton(
    type: SharingType,
    onGenerate: () -> Unit,
    isLoading: Boolean
) {
    OutlinedButton(
        onClick = onGenerate,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = getSharingTypeIcon(type),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(getSharingTypeDisplayName(type))
    }
}

@Composable
private fun AnalyticsItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// Helper data classes and functions

data class SharingPlatformInfo(
    val type: SharingPlatform,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun getSharingPlatforms(): List<SharingPlatformInfo> {
    val primaryColor = MaterialTheme.colorScheme.primary
    return listOf(
        SharingPlatformInfo(
            SharingPlatform.WHATSAPP,
            "WhatsApp",
            Icons.Default.ChatBubble,
            Color(0xFF25D366)
        ),
        SharingPlatformInfo(
            SharingPlatform.TELEGRAM,
            "Telegram",
            Icons.Default.Send,
            Color(0xFF0088cc)
        ),
        SharingPlatformInfo(
            SharingPlatform.EMAIL,
            "Email",
            Icons.Default.Email,
            Color(0xFF34A853)
        ),
        SharingPlatformInfo(
            SharingPlatform.SMS,
            "SMS",
            Icons.Default.Sms,
            Color(0xFF1976D2)
        ),
        SharingPlatformInfo(
            SharingPlatform.FACEBOOK,
            "Facebook",
            Icons.Default.Public,
            Color(0xFF1877F2)
        ),
        SharingPlatformInfo(
            SharingPlatform.GENERIC,
            "More",
            Icons.Default.MoreHoriz,
            primaryColor
        )
    )
}

private fun getSharingTypeIcon(type: SharingType): ImageVector {
    return when (type) {
        SharingType.FAMILY_PRIVATE -> Icons.Default.FamilyRestroom
        SharingType.CLOSE_FRIENDS -> Icons.Default.Group
        SharingType.COMMUNITY_OPEN -> Icons.Default.Public
        SharingType.PUBLIC_MEMORIAL -> Icons.Default.Language
    }
}

private fun getSharingTypeDisplayName(type: SharingType): String {
    return when (type) {
        SharingType.FAMILY_PRIVATE -> "Family Only"
        SharingType.CLOSE_FRIENDS -> "Close Friends"
        SharingType.COMMUNITY_OPEN -> "Community"
        SharingType.PUBLIC_MEMORIAL -> "Public Memorial"
    }
}