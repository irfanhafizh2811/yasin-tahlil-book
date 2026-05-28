package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.core.firebase.sharing.*

/**
 * Shared Memorial Details Screen - Compose implementation
 * Displays memorial details when accessed through sharing link
 * Handles guest access and family member authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedMemorialDetailsScreen(
    memorialId: String,
    sharerUserId: String?,
    sharingType: String?,
    onNavigateToAuth: () -> Unit,
    onNavigateToPrayer: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SharedMemorialDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LaunchedEffect(memorialId, sharerUserId) {
        viewModel.loadSharedMemorial(memorialId, sharerUserId, sharingType)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        SharedMemorialTopBar(
            onNavigateBack = onNavigateBack
        )
        
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                SharedMemorialErrorScreen(
                    error = uiState.error!!,
                    onRetry = {
                        viewModel.loadSharedMemorial(memorialId, sharerUserId, sharingType)
                    }
                )
            }
            
            uiState.memorial != null -> {
                SharedMemorialContent(
                    memorial = uiState.memorial!!,
                    accessPermissions = uiState.accessPermissions,
                    isAuthenticated = uiState.isAuthenticated,
                    onJoinPrayers = {
                        if (uiState.isAuthenticated) {
                            onNavigateToPrayer(memorialId)
                        } else {
                            onNavigateToAuth()
                        }
                    },
                    onRequestAccess = {
                        viewModel.requestAccess(memorialId)
                    }
                )
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
private fun SharedMemorialTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Memorial Prayer Invitation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun SharedMemorialContent(
    memorial: SharedMemorialInfo,
    accessPermissions: MemorialSharingPermissions?,
    isAuthenticated: Boolean,
    onJoinPrayers: () -> Unit,
    onRequestAccess: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Islamic Welcome Card
            IslamicWelcomeCard(
                sharerName = memorial.sharerName
            )
        }
        
        item {
            // Memorial Information Card
            MemorialInfoDisplayCard(
                memorial = memorial
            )
        }
        
        item {
            // Memorial Photo (if available)
            memorial.photoUrl?.let { photoUrl ->
                MemorialPhotoCard(photoUrl = photoUrl)
            }
        }
        
        item {
            // Prayer Invitation Card
            PrayerInvitationCard(
                memorial = memorial,
                onJoinPrayers = onJoinPrayers
            )
        }
        
        item {
            // Access Status Card
            AccessStatusCard(
                isAuthenticated = isAuthenticated,
                accessPermissions = accessPermissions,
                onRequestAccess = onRequestAccess
            )
        }
        
        item {
            // Islamic Guidance Card
            IslamicGuidanceCard()
        }
    }
}

@Composable
private fun SharedMemorialErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Unable to Load Memorial",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("Try Again")
        }
    }
}

@Composable
private fun IslamicWelcomeCard(
    sharerName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🤲",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "السلام عليكم ورحمة الله وبركاته",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$sharerName has invited you to join in memorial prayers.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "May Allah reward you for your prayers and grant peace to the departed soul.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun MemorialInfoDisplayCard(
    memorial: SharedMemorialInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Memorial Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Deceased Name
            MemorialInfoRow(
                label = "In memory of",
                value = memorial.deceasedName,
                isHighlighted = true
            )
            
            // Memorial Message
            if (memorial.memorialMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                MemorialInfoRow(
                    label = "Memorial Message",
                    value = memorial.memorialMessage
                )
            }
            
            // Death Date (if available)
            memorial.deathDate?.let { date ->
                Spacer(modifier = Modifier.height(12.dp))
                
                MemorialInfoRow(
                    label = "Date of passing",
                    value = formatIslamicDate(date)
                )
            }
            
            // Prayer Type
            Spacer(modifier = Modifier.height(12.dp))
            
            MemorialInfoRow(
                label = "Prayer types",
                value = memorial.prayerTypes.joinToString(", ")
            )
        }
    }
}

@Composable
private fun MemorialPhotoCard(
    photoUrl: String
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
                text = "Memorial Photo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Memorial photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun PrayerInvitationCard(
    memorial: SharedMemorialInfo,
    onJoinPrayers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Join Memorial Prayers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Join us in praying ${memorial.prayerTypes.joinToString(" and ")} for ${memorial.deceasedName}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "\"وَمِنَ النَّاسِ مَن يَشْرِي نَفْسَهُ ابْتِغَاءَ مَرْضَاتِ اللَّهِ\"",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onJoinPrayers,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text("Join Prayers")
            }
        }
    }
}

@Composable
private fun AccessStatusCard(
    isAuthenticated: Boolean,
    accessPermissions: MemorialSharingPermissions?,
    onRequestAccess: () -> Unit
) {
    val (statusText, statusColor, actionNeeded) = when {
        !isAuthenticated -> Triple(
            "Sign in to join prayers and track your participation",
            MaterialTheme.colorScheme.primary,
            true
        )
        accessPermissions == null -> Triple(
            "Request access to participate in memorial prayers",
            MaterialTheme.colorScheme.secondary,
            true
        )
        else -> Triple(
            "You have ${getPermissionDisplayName(accessPermissions)} to this memorial",
            MaterialTheme.colorScheme.tertiary,
            false
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (accessPermissions != null) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = statusColor
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }
            
            if (actionNeeded && accessPermissions == null && isAuthenticated) {
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onRequestAccess,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Request Access")
                }
            }
        }
    }
}

@Composable
private fun IslamicGuidanceCard() {
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
                text = "📿 Islamic Guidance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\"The best of people are those who benefit others\"\n\n" +
                        "Your prayers for the departed are a beautiful act of kindness that benefits both the deceased and yourself. May Allah accept your prayers and grant you rewards in this life and the hereafter.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun MemorialInfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper functions

private fun getPermissionDisplayName(permission: MemorialSharingPermissions): String {
    return when (permission) {
        MemorialSharingPermissions.VIEW_ONLY -> "view access"
        MemorialSharingPermissions.PRAY_AND_VIEW -> "prayer access"
        MemorialSharingPermissions.FULL_ACCESS -> "full access"
    }
}

private fun formatIslamicDate(timestamp: Long): String {
    // In a real implementation, would format with Hijri calendar
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}

// Data model for shared memorial information
data class SharedMemorialInfo(
    val memorialId: String,
    val deceasedName: String,
    val sharerName: String,
    val memorialMessage: String,
    val prayerTypes: List<String>,
    val photoUrl: String? = null,
    val deathDate: Long? = null,
    val privacyLevel: String,
    val familyMemberCount: Int,
    val totalPrayers: Long
)