package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.auth.model.AuthEvent
import com.app_muslim.surah_yasin.feature.auth.viewmodel.AuthViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Show success message when email is sent
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage?.contains("sent successfully") == true) {
            showSuccessMessage = true
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        emailError = validateEmail(newEmail)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Reset Password Icon
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔐",
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showSuccessMessage) {
                // Success State
                Text(
                    text = "Email Sent!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "We've sent a password reset link to your email address. Please check your inbox and follow the instructions to reset your password.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Back to Sign In")
                }
            } else {
                // Input State
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { updateEmail(it) },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            if (emailError == null && email.isNotEmpty()) {
                                viewModel.handleAuthEvent(AuthEvent.ForgotPassword(email))
                            }
                        }
                    ),
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                // Send Email Button
                Button(
                    onClick = {
                        viewModel.handleAuthEvent(AuthEvent.ForgotPassword(email))
                    },
                    enabled = emailError == null && email.isNotEmpty() && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Send Reset Email",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back to Login
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Sign In")
                }
            }

            // Error Message
            uiState.errorMessage?.takeIf { !it.contains("sent successfully") }?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// PREVIEW-FRIENDLY VERSION
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreenPreview(
    email: String = "",
    emailError: String? = null,
    isLoading: Boolean = false,
    showSuccessMessage: Boolean = false,
    errorMessage: String? = null,
    onNavigateBack: () -> Unit = {},
    onSendResetEmail: (String) -> Unit = {}
) {
    var currentEmail by remember { mutableStateOf(email) }
    var currentEmailError by remember { mutableStateOf(emailError) }
    var currentShowSuccess by remember { mutableStateOf(showSuccessMessage) }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun updateEmail(newEmail: String) {
        currentEmail = newEmail
        currentEmailError = if (emailError != null) emailError else validateEmail(newEmail)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Reset Password Icon
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔐",
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (currentShowSuccess) {
                // Success State
                Text(
                    text = "Email Sent!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "We've sent a password reset link to your email address. Please check your inbox and follow the instructions to reset your password.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Back to Sign In")
                }
            } else {
                // Input State
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Email TextField
                OutlinedTextField(
                    value = currentEmail,
                    onValueChange = { updateEmail(it) },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    isError = currentEmailError != null,
                    supportingText = currentEmailError?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                // Send Email Button
                Button(
                    onClick = { onSendResetEmail(currentEmail) },
                    enabled = currentEmailError == null && currentEmail.isNotEmpty() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Send Reset Email",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back to Login
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Sign In")
                }
            }

            // Error Message
            errorMessage?.takeIf { !it.contains("sent successfully") }?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class ForgotPasswordScreenStateProvider : PreviewParameterProvider<Triple<String, String?, Boolean>> {
    override val values: Sequence<Triple<String, String?, Boolean>> = sequenceOf(
        // Email, EmailError, ShowSuccessMessage
        Triple("", null, false), // Empty state
        Triple("ahmad.abdullah@example.com", null, false), // Valid email
        Triple("invalid-email", "Invalid email format", false), // Invalid email
        Triple("", "Email is required", false), // Required error
        Triple("fatimah.hassan@example.com", null, true) // Success state
    )
}

// ============================================================================
// BASIC PREVIEWS
// ============================================================================

@Preview(name = "Forgot Password - Empty State")
@Composable
fun PreviewForgotPasswordScreenEmpty() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview()
        }
    }
}

@Preview(name = "Forgot Password - With Valid Email")
@Composable
fun PreviewForgotPasswordScreenWithEmail() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "ahmad.abdullah@example.com"
            )
        }
    }
}

@Preview(name = "Forgot Password - Email Validation Error")
@Composable
fun PreviewForgotPasswordScreenEmailError() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "invalid-email",
                emailError = "Invalid email format"
            )
        }
    }
}

@Preview(name = "Forgot Password - Loading State")
@Composable
fun PreviewForgotPasswordScreenLoading() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "ahmad.abdullah@example.com",
                isLoading = true
            )
        }
    }
}

@Preview(name = "Forgot Password - Success State")
@Composable
fun PreviewForgotPasswordScreenSuccess() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "fatimah.hassan@example.com",
                showSuccessMessage = true
            )
        }
    }
}

@Preview(name = "Forgot Password - Error State")
@Composable
fun PreviewForgotPasswordScreenError() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "test@example.com",
                errorMessage = "Unable to send reset email. Please check your internet connection and try again."
            )
        }
    }
}

@Preview(name = "Forgot Password - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewForgotPasswordScreenDark() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "yusuf.omar@example.com"
            )
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Forgot Password - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewForgotPasswordScreenLandscape() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "hassan.muhammad@example.com"
            )
        }
    }
}

@Preview(
    name = "Forgot Password - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewForgotPasswordScreenTablet() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "aisha.ahmad@example.com"
            )
        }
    }
}

@Preview(
    name = "Forgot Password - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewForgotPasswordScreenSmallPhone() {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = "khadijah@example.com"
            )
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Forgot Password - Various States")
@Composable
fun PreviewForgotPasswordScreenDynamic(
    @PreviewParameter(ForgotPasswordScreenStateProvider::class) state: Triple<String, String?, Boolean>
) {
    TahlilTheme {
        Surface {
            ForgotPasswordScreenPreview(
                email = state.first,
                emailError = state.second,
                showSuccessMessage = state.third
            )
        }
    }
}