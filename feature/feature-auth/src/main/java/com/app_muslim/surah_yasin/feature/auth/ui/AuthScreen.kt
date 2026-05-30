package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.feature.auth.model.AuthUiState
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

enum class AuthFlow {
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    SOCIAL_AUTH,
    PHONE_AUTH,
    BIOMETRIC_AUTH
}

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onResetPassword: (String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUpMode) "Create Account" else "Sign In",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (isSignUpMode) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.semantics { 
                    contentDescription = "Loading"
                }
            )
        } else {
            Button(
                onClick = {
                    if (isSignUpMode) {
                        onSignUp(email, password, "")
                    } else {
                        onSignIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(if (isSignUpMode) "Create Account" else "Sign In")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (isSignUpMode) {
                    onNavigateToSignIn()
                    isSignUpMode = false
                } else {
                    onNavigateToSignUp()
                    isSignUpMode = true
                }
            }
        ) {
            Text(if (isSignUpMode) "Already have an account? Sign In" else "Create Account")
        }

        if (!isSignUpMode) {
            TextButton(
                onClick = { onResetPassword(email) }
            ) {
                Text("Forgot Password?")
            }
        }
    }
}

@Composable
fun SignUpScreen(
    uiState: AuthUiState,
    onSignUp: (String, String, String) -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (password != confirmPassword && confirmPassword.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    onSignUp(email, password, fullName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = password == confirmPassword && password.isNotEmpty()
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToSignIn
        ) {
            Text("Already have an account? Sign In")
        }
    }
}

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class AuthUiStateProvider : PreviewParameterProvider<AuthUiState> {
    override val values: Sequence<AuthUiState> = sequenceOf(
        AuthUiState(), // Default state
        AuthUiState(isLoading = true), // Loading state
        AuthUiState(errorMessage = "Invalid email or password"), // Error state
        AuthUiState(errorMessage = "Network connection failed. Please check your internet and try again."), // Network error
        AuthUiState(isLoading = false, errorMessage = null) // Success ready state
    )
}

// ============================================================================
// AUTH SCREEN PREVIEWS
// ============================================================================

@Preview(name = "Auth Screen - Sign In Mode")
@Composable
fun PreviewAuthScreenSignIn() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "Auth Screen - Loading State")
@Composable
fun PreviewAuthScreenLoading() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(isLoading = true),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "Auth Screen - Error State")
@Composable
fun PreviewAuthScreenError() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(errorMessage = "Invalid email or password"),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "Auth Screen - Network Error")
@Composable
fun PreviewAuthScreenNetworkError() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(errorMessage = "Network connection failed. Please check your internet and try again."),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "Auth Screen - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAuthScreenDark() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

// ============================================================================
// SIGNUP SCREEN PREVIEWS
// ============================================================================

@Preview(name = "SignUp Screen - Empty State")
@Composable
fun PreviewSignUpScreenEmpty() {
    TahlilTheme {
        Surface {
            SignUpScreen(
                uiState = AuthUiState(),
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "SignUp Screen - Password Mismatch")
@Composable
fun PreviewSignUpScreenPasswordMismatch() {
    TahlilTheme {
        Surface {
            SignUpScreenPreview(
                email = "ahmad.hassan@example.com",
                fullName = "Ahmad Hassan",
                password = "password123",
                confirmPassword = "password456",
                uiState = AuthUiState()
            )
        }
    }
}

@Preview(name = "SignUp Screen - Loading State")
@Composable
fun PreviewSignUpScreenLoading() {
    TahlilTheme {
        Surface {
            SignUpScreen(
                uiState = AuthUiState(isLoading = true),
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "SignUp Screen - Error State")
@Composable
fun PreviewSignUpScreenError() {
    TahlilTheme {
        Surface {
            SignUpScreen(
                uiState = AuthUiState(errorMessage = "Email already exists. Please use a different email address."),
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(name = "SignUp Screen - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSignUpScreenDark() {
    TahlilTheme {
        Surface {
            SignUpScreen(
                uiState = AuthUiState(),
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Auth Screen - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewAuthScreenLandscape() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(
    name = "Auth Screen - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewAuthScreenTablet() {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = AuthUiState(errorMessage = "Invalid credentials"),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

@Preview(
    name = "Auth Screen - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewAuthScreenSmallPhone() {
    TahlilTheme {
        Surface {
            SignUpScreen(
                uiState = AuthUiState(),
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Auth Screen - Various States")
@Composable
fun PreviewAuthScreenDynamic(
    @PreviewParameter(AuthUiStateProvider::class) uiState: AuthUiState
) {
    TahlilTheme {
        Surface {
            AuthScreen(
                uiState = uiState,
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}

// ============================================================================
// PREVIEW-FRIENDLY SIGNUP VERSION
// ============================================================================

@Composable
fun SignUpScreenPreview(
    email: String = "",
    fullName: String = "",
    password: String = "",
    confirmPassword: String = "",
    uiState: AuthUiState = AuthUiState(),
    onSignUp: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToSignIn: () -> Unit = { }
) {
    var currentEmail by remember { mutableStateOf(email) }
    var currentPassword by remember { mutableStateOf(password) }
    var currentConfirmPassword by remember { mutableStateOf(confirmPassword) }
    var currentFullName by remember { mutableStateOf(fullName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = currentEmail,
            onValueChange = { currentEmail = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentFullName,
            onValueChange = { currentFullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentConfirmPassword,
            onValueChange = { currentConfirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (currentPassword != currentConfirmPassword && currentConfirmPassword.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (currentPassword == currentConfirmPassword) {
                    onSignUp(currentEmail, currentPassword, currentFullName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = currentPassword == currentConfirmPassword && currentPassword.isNotEmpty()
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToSignIn
        ) {
            Text("Already have an account? Sign In")
        }
    }
}
// ============================================================================
// UNIFIED PREVIEW WRAPPER
// ============================================================================

@Composable
fun AuthScreenPreview(
    authFlow: AuthFlow,
    uiState: AuthUiState = AuthUiState(),
    hasData: Boolean = false
) {
    when (authFlow) {
        AuthFlow.SIGN_IN -> {
            AuthScreen(
                uiState = uiState,
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
        AuthFlow.SIGN_UP -> {
            SignUpScreen(
                uiState = uiState,
                onSignUp = { _, _, _ -> },
                onNavigateToSignIn = { }
            )
        }
        AuthFlow.FORGOT_PASSWORD -> {
            // Simple placeholder for forgot password - could be expanded
            AuthScreen(
                uiState = uiState.copy(errorMessage = "Reset password functionality"),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
        AuthFlow.SOCIAL_AUTH -> {
            // Show auth screen with emphasis on social login
            AuthScreen(
                uiState = uiState,
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
        AuthFlow.PHONE_AUTH -> {
            // Show auth screen for phone verification
            AuthScreen(
                uiState = if (hasData) uiState.copy(errorMessage = "Phone verification demo") else uiState,
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
        AuthFlow.BIOMETRIC_AUTH -> {
            // Show auth screen for biometric authentication
            AuthScreen(
                uiState = uiState.copy(errorMessage = "Biometric authentication demo"),
                onSignIn = { _, _ -> },
                onSignUp = { _, _, _ -> },
                onGoogleSignIn = { },
                onResetPassword = { },
                onNavigateToSignUp = { },
                onNavigateToSignIn = { }
            )
        }
    }
}
