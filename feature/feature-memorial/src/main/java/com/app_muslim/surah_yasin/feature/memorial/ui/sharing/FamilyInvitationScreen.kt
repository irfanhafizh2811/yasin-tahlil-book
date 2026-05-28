package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.core.firebase.sharing.*

/**
 * Family Invitation Screen - Compose implementation
 * Allows users to invite family members to memorial prayers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyInvitationScreen(
    memorialId: String,
    memorialName: String,
    onNavigateBack: () -> Unit,
    viewModel: FamilyInvitationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LaunchedEffect(memorialId) {
        viewModel.loadMemorialInvitations(memorialId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        FamilyInvitationTopBar(
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
                // Islamic Greeting Card
                IslamicGreetingCard()
            }
            
            item {
                // New Invitation Form
                NewInvitationCard(
                    uiState = uiState,
                    onSendInvitation = { email, phone, message, permissions ->
                        viewModel.sendFamilyInvitation(
                            memorialId = memorialId,
                            inviteeEmail = email,
                            inviteePhone = phone,
                            personalMessage = message,
                            permissions = permissions
                        )
                    }
                )
            }
            
            item {
                // Pending Invitations Section
                PendingInvitationsSection(
                    invitations = uiState.pendingInvitations,
                    onResendInvitation = { invitationId ->
                        viewModel.resendInvitation(invitationId)
                    },
                    onCancelInvitation = { invitationId ->
                        viewModel.cancelInvitation(invitationId)
                    }
                )
            }
            
            item {
                // Family Members with Access Section
                FamilyAccessSection(
                    familyMembers = uiState.familyMembersWithAccess,
                    onRevokeAccess = { userId ->
                        viewModel.revokeAccess(memorialId, userId)
                    }
                )
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
    
    // Error message
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Show snackbar or error dialog
        }
    }
    
    // Success message
    uiState.successMessage?.let { successMessage ->
        LaunchedEffect(successMessage) {
            // Show success snackbar
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilyInvitationTopBar(
    memorialName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Family Invitations",
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
private fun IslamicGreetingCard() {
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
            Text(
                text = "🤲",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "السلام عليكم ورحمة الله وبركاته",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Invite your family to join in memorial prayers.\nTogether, we can honor our beloved's memory.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun NewInvitationCard(
    uiState: FamilyInvitationUiState,
    onSendInvitation: (String, String?, String, MemorialSharingPermissions) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var personalMessage by remember { mutableStateOf("") }
    var selectedPermissions by remember { mutableStateOf(MemorialSharingPermissions.VIEW_ONLY) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Invite Family Member",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address *") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Phone Field (Optional)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number (Optional)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Personal Message Field
            OutlinedTextField(
                value = personalMessage,
                onValueChange = { personalMessage = it },
                label = { Text("Personal Message (Optional)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                placeholder = {
                    Text("Add a personal message to your invitation...")
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Advanced Options Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Advanced Options",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Switch(
                    checked = showAdvancedOptions,
                    onCheckedChange = { showAdvancedOptions = it }
                )
            }
            
            // Advanced Options Content
            AnimatedVisibility(
                visible = showAdvancedOptions,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Permission Selection
                    Text(
                        text = "Access Permissions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    MemorialSharingPermissions.values().forEach { permission ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPermissions == permission,
                                onClick = { selectedPermissions = permission }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column {
                                Text(
                                    text = getPermissionDisplayName(permission),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = getPermissionDescription(permission),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Send Invitation Button
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        onSendInvitation(
                            email.trim(),
                            if (phone.isNotBlank()) phone.trim() else null,
                            personalMessage.trim(),
                            selectedPermissions
                        )
                        
                        // Reset form
                        email = ""
                        phone = ""
                        personalMessage = ""
                        selectedPermissions = MemorialSharingPermissions.VIEW_ONLY
                        showAdvancedOptions = false
                        
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text("Send Invitation")
            }
        }
    }
}

@Composable
private fun PendingInvitationsSection(
    invitations: List<MemorialInvitation>,
    onResendInvitation: (String) -> Unit,
    onCancelInvitation: (String) -> Unit
) {
    if (invitations.isEmpty()) {
        return
    }
    
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
                text = "Pending Invitations (${invitations.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            invitations.forEach { invitation ->
                PendingInvitationItem(
                    invitation = invitation,
                    onResend = { onResendInvitation(invitation.id) },
                    onCancel = { onCancelInvitation(invitation.id) }
                )
                
                if (invitation != invitations.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingInvitationItem(
    invitation: MemorialInvitation,
    onResend: () -> Unit,
    onCancel: () -> Unit
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
                text = invitation.inviteeEmail,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Sent ${formatRelativeTime(invitation.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Row {
            IconButton(
                onClick = onResend,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Resend",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancel",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun FamilyAccessSection(
    familyMembers: List<MemorialAccess>,
    onRevokeAccess: (String) -> Unit
) {
    if (familyMembers.isEmpty()) {
        return
    }
    
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
                text = "Family Members with Access (${familyMembers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            familyMembers.forEach { member ->
                FamilyMemberAccessItem(
                    member = member,
                    onRevokeAccess = { onRevokeAccess(member.userId) }
                )
                
                if (member != familyMembers.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilyMemberAccessItem(
    member: MemorialAccess,
    onRevokeAccess: () -> Unit
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
                text = member.userId, // In real app, would display user name
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${getPermissionDisplayName(member.permissions)} • Added ${formatRelativeTime(member.grantedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        TextButton(
            onClick = onRevokeAccess,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Remove")
        }
    }
}

// Helper functions

private fun getPermissionDisplayName(permission: MemorialSharingPermissions): String {
    return when (permission) {
        MemorialSharingPermissions.VIEW_ONLY -> "View Only"
        MemorialSharingPermissions.PRAY_AND_VIEW -> "Pray & View"
        MemorialSharingPermissions.FULL_ACCESS -> "Full Access"
    }
}

private fun getPermissionDescription(permission: MemorialSharingPermissions): String {
    return when (permission) {
        MemorialSharingPermissions.VIEW_ONLY -> "Can view memorial details and prayer count"
        MemorialSharingPermissions.PRAY_AND_VIEW -> "Can participate in prayers and view all activities"
        MemorialSharingPermissions.FULL_ACCESS -> "Can manage sharing and invite others"
    }
}

private fun formatRelativeTime(dateTime: java.time.ZonedDateTime): String {
    val now = java.time.ZonedDateTime.now()
    val duration = java.time.Duration.between(dateTime, now)
    
    return when {
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "Just now"
    }
}