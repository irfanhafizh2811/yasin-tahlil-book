package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import android.content.res.Configuration
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.community.model.*
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

/**
 * Family Memorial Sharing Card Component
 * Displays shared family memorials and prayer invitations
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FamilyMemorialSharingCard(
    sharedMemorials: List<SharedMemorial>,
    familyPrayerInvitations: List<PrayerInvitation>,
    onJoinFamilyPrayer: (String) -> Unit,
    onViewMemorial: (String) -> Unit,
    onInviteFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(FamilyTabUI.SHARED_MEMORIALS) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with family icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FamilyRestroom,
                        contentDescription = "Family",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Text(
                        text = "Family Memorial Sharing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onInviteFamily
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Invite Family",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab selection
            FamilyTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content based on selected tab
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with
                            fadeOut(animationSpec = tween(300))
                }
            ) { tab ->
                when (tab) {
                    FamilyTabUI.SHARED_MEMORIALS -> {
                        SharedMemorialsContent(
                            memorials = sharedMemorials,
                            onViewMemorial = onViewMemorial
                        )
                    }
                    FamilyTabUI.PRAYER_INVITATIONS -> {
                        PrayerInvitationsContent(
                            invitations = familyPrayerInvitations,
                            onJoinPrayer = onJoinFamilyPrayer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilyTabSelector(
    selectedTab: FamilyTabUI,
    onTabSelected: (FamilyTabUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FamilyTabUI.values().forEach { tab ->
            FilterChip(
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = tab.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = tab == selectedTab,
                leadingIcon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SharedMemorialsContent(
    memorials: List<SharedMemorial>,
    onViewMemorial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (memorials.isEmpty()) {
        EmptyFamilyMemorialsState()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(memorials) { memorial ->
                SharedMemorialCard(
                    memorial = memorial,
                    onViewMemorial = onViewMemorial
                )
            }
        }
    }
}

@Composable
private fun PrayerInvitationsContent(
    invitations: List<PrayerInvitation>,
    onJoinPrayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (invitations.isEmpty()) {
        EmptyPrayerInvitationsState()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invitations) { invitation ->
                PrayerInvitationCard(
                    invitation = invitation,
                    onJoinPrayer = onJoinPrayer
                )
            }
        }
    }
}

@Composable
private fun SharedMemorialCard(
    memorial: SharedMemorial,
    onViewMemorial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewMemorial(memorial.memorialId) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Memorial photo
            memorial.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Memorial Photo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Memorial",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Memorial details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = memorial.deceasedName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Shared by ${memorial.sharedByName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Family Members",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Text(
                        text = "${memorial.familyMembers.size} family members",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = "Total Prayers: ${memorial.totalPrayers}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Prayer status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (memorial.isActivePrayerSession) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Green)
                        )
                        
                        Text(
                            text = "Live",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = formatDate(memorial.sharedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerInvitationCard(
    invitation: PrayerInvitation,
    onJoinPrayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Invitation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Prayer Invitation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "From ${invitation.inviterName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                if (invitation.isUrgent) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = "Urgent",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Memorial details
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Memorial",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = invitation.memorialName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Prayer Type",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = "${invitation.prayerType.displayName} • ${invitation.targetPrayerCount} prayers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Decline invitation */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decline")
                }
                
                Button(
                    onClick = { onJoinPrayer(invitation.invitationId) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Join Prayer")
                }
            }
            
            // Family members joining
            if (invitation.acceptedFamilyMembers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(invitation.acceptedFamilyMembers.take(5)) { member ->
                        AsyncImage(
                            model = member.profilePhotoUrl,
                            contentDescription = member.name,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    if (invitation.acceptedFamilyMembers.size > 5) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${invitation.acceptedFamilyMembers.size - 5}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFamilyMemorialsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FamilyRestroom,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Family Memorials Yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Invite family members to share memorials and pray together",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyPrayerInvitationsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MailOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Prayer Invitations",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Family prayer invitations will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

// Family Tab with icons for UI
enum class FamilyTabUI(val displayName: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SHARED_MEMORIALS("Shared Memorials", Icons.Default.Share),
    PRAYER_INVITATIONS("Prayer Invitations", Icons.Default.MailOutline)
}

private fun formatDate(date: java.time.ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    return date.format(formatter)
}

// ========================================
// PREVIEW IMPLEMENTATIONS
// ========================================

@Preview(
    name = "Family Memorial Sharing - With Memorials",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardWithMemorials() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = getSampleSharedMemorials(),
            familyPrayerInvitations = emptyList(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Family Memorial Sharing - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewFamilyMemorialSharingCardDark() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = getSampleSharedMemorials(),
            familyPrayerInvitations = getSamplePrayerInvitations(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Prayer Invitations Tab",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardInvitations() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = emptyList(),
            familyPrayerInvitations = getSamplePrayerInvitations(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Empty Family Memorials",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardEmpty() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = emptyList(),
            familyPrayerInvitations = emptyList(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Active Prayer Sessions",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardActiveSessions() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = getActiveSessionMemorials(),
            familyPrayerInvitations = emptyList(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Urgent Prayer Invitations",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardUrgentInvitations() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = emptyList(),
            familyPrayerInvitations = getUrgentPrayerInvitations(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Large Family Memorial",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardLargeFamily() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = getLargeFamilyMemorials(),
            familyPrayerInvitations = emptyList(),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

@Preview(
    name = "Mixed Content",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardMixed() {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = getSampleSharedMemorials().take(2),
            familyPrayerInvitations = getSamplePrayerInvitations().take(1),
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

// Preview Parameter Providers
class FamilyMemorialContentPreviewProvider : PreviewParameterProvider<Pair<List<SharedMemorial>, List<PrayerInvitation>>> {
    override val values = sequenceOf(
        Pair(emptyList(), emptyList()),
        Pair(getSampleSharedMemorials(), emptyList()),
        Pair(emptyList(), getSamplePrayerInvitations()),
        Pair(getSampleSharedMemorials(), getSamplePrayerInvitations())
    )
}

@Preview(
    name = "Dynamic Family Content",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewFamilyMemorialSharingCardDynamic(
    @PreviewParameter(FamilyMemorialContentPreviewProvider::class) content: Pair<List<SharedMemorial>, List<PrayerInvitation>>
) {
    TahlilTheme {
        FamilyMemorialSharingCard(
            sharedMemorials = content.first,
            familyPrayerInvitations = content.second,
            onJoinFamilyPrayer = { },
            onViewMemorial = { },
            onInviteFamily = { }
        )
    }
}

// Sample Data Functions
private fun getSampleSharedMemorials(): List<SharedMemorial> = listOf(
    SharedMemorial(
        memorialId = "memorial_1",
        deceasedName = "Abdullah Ibn Ahmad",
        photoUrl = "https://example.com/photo1.jpg",
        sharedByName = "Fatima (Wife)",
        sharedByUserId = "user_1",
        familyMembers = getSampleFamilyMembers(),
        totalPrayers = 2847,
        isActivePrayerSession = false,
        sharedAt = ZonedDateTime.now().minusDays(2)
    ),
    SharedMemorial(
        memorialId = "memorial_2", 
        deceasedName = "Khadijah Bint Muhammad",
        photoUrl = null,
        sharedByName = "Omar (Son)",
        sharedByUserId = "user_2",
        familyMembers = getSampleFamilyMembers().take(3),
        totalPrayers = 1567,
        isActivePrayerSession = true,
        sharedAt = ZonedDateTime.now().minusHours(6)
    ),
    SharedMemorial(
        memorialId = "memorial_3",
        deceasedName = "Ali Ibn Abu Talib",
        photoUrl = "https://example.com/photo3.jpg",
        sharedByName = "Aisha (Daughter)",
        sharedByUserId = "user_3",
        familyMembers = getSampleFamilyMembers().take(2),
        totalPrayers = 892,
        isActivePrayerSession = false,
        sharedAt = ZonedDateTime.now().minusDays(5)
    )
)

private fun getActiveSessionMemorials(): List<SharedMemorial> = listOf(
    SharedMemorial(
        memorialId = "active_1",
        deceasedName = "Hassan Ibn Ali",
        photoUrl = "https://example.com/active.jpg", 
        sharedByName = "Zainab (Sister)",
        sharedByUserId = "user_active",
        familyMembers = getSampleFamilyMembers(),
        totalPrayers = 5634,
        isActivePrayerSession = true,
        sharedAt = ZonedDateTime.now().minusMinutes(30)
    )
)

private fun getLargeFamilyMemorials(): List<SharedMemorial> = listOf(
    SharedMemorial(
        memorialId = "large_family",
        deceasedName = "Muhammad Ibn Abdullah",
        photoUrl = "https://example.com/large.jpg",
        sharedByName = "Ahmad (Son)", 
        sharedByUserId = "user_large",
        familyMembers = getLargeFamilyMembers(),
        totalPrayers = 15847,
        isActivePrayerSession = false,
        sharedAt = ZonedDateTime.now().minusDays(1)
    )
)

private fun getSamplePrayerInvitations(): List<PrayerInvitation> = listOf(
    PrayerInvitation(
        invitationId = "invitation_1",
        memorialId = "memorial_1",
        memorialName = "Abdullah Ibn Ahmad",
        inviterName = "Fatima (Wife)",
        inviterUserId = "inviter_1",
        prayerType = CommunityPrayerType.TAHLIL,
        targetPrayerCount = 100,
        isUrgent = false,
        acceptedFamilyMembers = getSampleFamilyMembers().take(3),
        invitedAt = ZonedDateTime.now().minusHours(2)
    ),
    PrayerInvitation(
        invitationId = "invitation_2",
        memorialId = "memorial_2",
        memorialName = "Khadijah Bint Muhammad", 
        inviterName = "Omar (Son)",
        inviterUserId = "inviter_2",
        prayerType = CommunityPrayerType.YASIN,
        targetPrayerCount = 7,
        isUrgent = true,
        acceptedFamilyMembers = getSampleFamilyMembers().take(2),
        invitedAt = ZonedDateTime.now().minusMinutes(30)
    )
)

private fun getUrgentPrayerInvitations(): List<PrayerInvitation> = listOf(
    PrayerInvitation(
        invitationId = "urgent_1",
        memorialId = "urgent_memorial",
        memorialName = "Urgent Prayer for Grandmother",
        inviterName = "Ahmad (Grandson)",
        inviterUserId = "urgent_user",
        prayerType = CommunityPrayerType.FATIHAH,
        targetPrayerCount = 41,
        isUrgent = true,
        acceptedFamilyMembers = getSampleFamilyMembers(),
        invitedAt = ZonedDateTime.now().minusMinutes(15)
    )
)

private fun getSampleFamilyMembers(): List<FamilyMember> = listOf(
    FamilyMember(
        userId = "family_1",
        name = "Ahmad",
        profilePhotoUrl = "https://example.com/ahmad.jpg",
        relationshipType = "Son"
    ),
    FamilyMember(
        userId = "family_2", 
        name = "Fatima",
        profilePhotoUrl = "https://example.com/fatima.jpg",
        relationshipType = "Daughter"
    ),
    FamilyMember(
        userId = "family_3",
        name = "Omar",
        profilePhotoUrl = null,
        relationshipType = "Brother"
    ),
    FamilyMember(
        userId = "family_4",
        name = "Aisha",
        profilePhotoUrl = "https://example.com/aisha.jpg",
        relationshipType = "Sister"
    ),
    FamilyMember(
        userId = "family_5",
        name = "Ali",
        profilePhotoUrl = "https://example.com/ali.jpg",
        relationshipType = "Nephew"
    )
)

private fun getLargeFamilyMembers(): List<FamilyMember> = getSampleFamilyMembers() + listOf(
    FamilyMember(
        userId = "family_6",
        name = "Zainab",
        profilePhotoUrl = "https://example.com/zainab.jpg",
        relationshipType = "Niece"
    ),
    FamilyMember(
        userId = "family_7",
        name = "Hassan",
        profilePhotoUrl = null,
        relationshipType = "Grandson"
    ),
    FamilyMember(
        userId = "family_8",
        name = "Hussain",
        profilePhotoUrl = "https://example.com/hussain.jpg",
        relationshipType = "Grandson"
    ),
    FamilyMember(
        userId = "family_9",
        name = "Ruqayya",
        profilePhotoUrl = "https://example.com/ruqayya.jpg",
        relationshipType = "Granddaughter"
    )
)