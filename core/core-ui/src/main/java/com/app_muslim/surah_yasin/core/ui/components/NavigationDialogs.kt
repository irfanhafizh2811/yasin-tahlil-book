package com.app_muslim.surah_yasin.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@Composable
fun GuestModeRestrictionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUpgradeAccount: (email: String, password: String) -> Unit,
    onSignInWithAccount: () -> Unit
) {
    if (!isVisible) return

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isUpgradeMode by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Account Required",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Create an account to access this feature",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider()

                // Content
                Text(
                    text = if (isUpgradeMode) {
                        "You're currently using guest mode. Create an account to save your data and access all features."
                    } else {
                        "Sign in to your existing account to access this feature."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isUpgradeMode) {
                    // Upgrade form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { 
                                Icon(Icons.Default.Email, contentDescription = null) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { 
                                Icon(Icons.Default.Lock, contentDescription = null) 
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Text(
                            text = "Your current progress will be preserved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { isUpgradeMode = !isUpgradeMode }
                    ) {
                        Text(
                            if (isUpgradeMode) {
                                "Already have an account? Sign in"
                            } else {
                                "Create new account instead"
                            }
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (isUpgradeMode) {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    onUpgradeAccount(email, password)
                                }
                            } else {
                                onSignInWithAccount()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = if (isUpgradeMode) email.isNotBlank() && password.isNotBlank() else true
                    ) {
                        Text(
                            if (isUpgradeMode) "Create Account" else "Sign In"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmailVerificationDialog(
    isVisible: Boolean,
    userEmail: String,
    onDismiss: () -> Unit,
    onSendVerification: () -> Unit,
    onRefreshStatus: () -> Unit
) {
    if (!isVisible) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Email Verification Required",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Verify your email to continue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider()

                // Content
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "To access this feature, please verify your email address:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Text(
                        text = "Check your email for the verification link. If you don't see it, check your spam folder.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSendVerification,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Send, 
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Verification Email")
                    }
                    
                    OutlinedButton(
                        onClick = onRefreshStatus,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("I've Verified My Email")
                    }
                    
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun SignOutConfirmationDialog(
    isVisible: Boolean,
    isSigningOut: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = if (!isSigningOut) onDismiss else { {} },
        icon = {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Sign Out")
        },
        text = {
            Text(
                "Are you sure you want to sign out? You'll need to sign in again to access your account features.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSigningOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isSigningOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Sign Out")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSigningOut
            ) {
                Text("Cancel")
            }
        }
    )
}

// Preview Data Providers
class GuestModeDialogStateProvider : PreviewParameterProvider<Triple<String, String, Boolean>> {
    override val values: Sequence<Triple<String, String, Boolean>> = sequenceOf(
        Triple("", "", true), // Empty state
        Triple("ahmed.hassan@example.com", "", true), // Email only
        Triple("ahmed.hassan@example.com", "password123", true), // Complete upgrade form
        Triple("fatimah.alzahra@gmail.com", "securepass", false) // Sign-in mode
    )
}

class DialogVisibilityProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

// Preview Functions
@Preview(name = "Guest Mode Dialog - Upgrade Mode")
@Composable
fun PreviewGuestModeRestrictionDialogUpgrade() {
    TahlilTheme {
        Surface {
            GuestModeRestrictionDialog(
                isVisible = true,
                onDismiss = { },
                onUpgradeAccount = { _, _ -> },
                onSignInWithAccount = { }
            )
        }
    }
}

@Preview(name = "Guest Mode Dialog - Sign In Mode")
@Composable
fun PreviewGuestModeRestrictionDialogSignIn() {
    TahlilTheme {
        Surface {
            var isUpgradeMode by remember { mutableStateOf(false) }
            GuestModeRestrictionDialog(
                isVisible = true,
                onDismiss = { },
                onUpgradeAccount = { _, _ -> },
                onSignInWithAccount = { }
            )
        }
    }
}

@Preview(name = "Guest Mode Dialog - Dynamic States", group = "Dynamic")
@Composable
fun PreviewGuestModeRestrictionDialogDynamic(
    @PreviewParameter(GuestModeDialogStateProvider::class) state: Triple<String, String, Boolean>
) {
    TahlilTheme {
        Surface {
            GuestModeRestrictionDialog(
                isVisible = true,
                onDismiss = { },
                onUpgradeAccount = { _, _ -> },
                onSignInWithAccount = { }
            )
        }
    }
}

@Preview(name = "Guest Mode Dialog - Dark Theme")
@Composable
fun PreviewGuestModeRestrictionDialogDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            GuestModeRestrictionDialog(
                isVisible = true,
                onDismiss = { },
                onUpgradeAccount = { _, _ -> },
                onSignInWithAccount = { }
            )
        }
    }
}

@Preview(name = "Guest Mode Dialog - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewGuestModeRestrictionDialogTablet() {
    TahlilTheme {
        Surface {
            GuestModeRestrictionDialog(
                isVisible = true,
                onDismiss = { },
                onUpgradeAccount = { _, _ -> },
                onSignInWithAccount = { }
            )
        }
    }
}

@Preview(name = "Email Verification Dialog - Default")
@Composable
fun PreviewEmailVerificationDialog() {
    TahlilTheme {
        Surface {
            EmailVerificationDialog(
                isVisible = true,
                userEmail = "muhammad.ali@example.com",
                onDismiss = { },
                onSendVerification = { },
                onRefreshStatus = { }
            )
        }
    }
}

@Preview(name = "Email Verification Dialog - Arabic Email")
@Composable
fun PreviewEmailVerificationDialogArabic() {
    TahlilTheme {
        Surface {
            EmailVerificationDialog(
                isVisible = true,
                userEmail = "عبدالرحمن.الحسن@example.com",
                onDismiss = { },
                onSendVerification = { },
                onRefreshStatus = { }
            )
        }
    }
}

@Preview(name = "Email Verification Dialog - Long Email")
@Composable
fun PreviewEmailVerificationDialogLongEmail() {
    TahlilTheme {
        Surface {
            EmailVerificationDialog(
                isVisible = true,
                userEmail = "very.long.email.address.for.testing@subdomain.example.com",
                onDismiss = { },
                onSendVerification = { },
                onRefreshStatus = { }
            )
        }
    }
}

@Preview(name = "Email Verification Dialog - Dark Theme")
@Composable
fun PreviewEmailVerificationDialogDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            EmailVerificationDialog(
                isVisible = true,
                userEmail = "aisha.rahman@gmail.com",
                onDismiss = { },
                onSendVerification = { },
                onRefreshStatus = { }
            )
        }
    }
}

@Preview(name = "Email Verification Dialog - Landscape", device = "spec:width=640dp,height=360dp,dpi=160,orientation=landscape")
@Composable
fun PreviewEmailVerificationDialogLandscape() {
    TahlilTheme {
        Surface {
            EmailVerificationDialog(
                isVisible = true,
                userEmail = "omar.abdullah@islamicfoundation.org",
                onDismiss = { },
                onSendVerification = { },
                onRefreshStatus = { }
            )
        }
    }
}

@Preview(name = "Sign Out Dialog - Default")
@Composable
fun PreviewSignOutConfirmationDialog() {
    TahlilTheme {
        Surface {
            SignOutConfirmationDialog(
                isVisible = true,
                isSigningOut = false,
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}

@Preview(name = "Sign Out Dialog - Loading State")
@Composable
fun PreviewSignOutConfirmationDialogLoading() {
    TahlilTheme {
        Surface {
            SignOutConfirmationDialog(
                isVisible = true,
                isSigningOut = true,
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}

@Preview(name = "Sign Out Dialog - Dark Theme")
@Composable
fun PreviewSignOutConfirmationDialogDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            SignOutConfirmationDialog(
                isVisible = true,
                isSigningOut = false,
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}

@Preview(name = "Sign Out Dialog - Loading Dark")
@Composable
fun PreviewSignOutConfirmationDialogLoadingDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            SignOutConfirmationDialog(
                isVisible = true,
                isSigningOut = true,
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}

@Preview(name = "Sign Out Dialog - Small Phone", device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun PreviewSignOutConfirmationDialogSmallPhone() {
    TahlilTheme {
        Surface {
            SignOutConfirmationDialog(
                isVisible = true,
                isSigningOut = false,
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}