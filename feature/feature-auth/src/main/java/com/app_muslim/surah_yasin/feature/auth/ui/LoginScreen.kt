package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.auth.model.AuthEvent
import com.app_muslim.surah_yasin.feature.auth.model.AuthUiState
import com.app_muslim.surah_yasin.feature.auth.model.LoginFormState
import com.app_muslim.surah_yasin.feature.auth.viewmodel.AuthViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.loginFormState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    
    var passwordVisible by remember { mutableStateOf(false) }

    // Handle successful login
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    // Show error snackbar
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast
            viewModel.handleAuthEvent(AuthEvent.ClearError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Islamic App Logo/Icon
        Card(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🕌",
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Welcome Text
        Text(
            text = "Welcome to Tahlil",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sign in to continue your memorial prayers",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = formState.email,
            onValueChange = { viewModel.updateLoginForm(email = it) },
            label = { Text("Email") },
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
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = formState.emailError != null,
            supportingText = formState.emailError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = formState.password,
            onValueChange = { viewModel.updateLoginForm(password = it) },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    if (formState.isValid) {
                        viewModel.handleAuthEvent(AuthEvent.EmailLogin(formState.email, formState.password))
                    }
                }
            ),
            isError = formState.passwordError != null,
            supportingText = formState.passwordError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Forgot Password
        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
                viewModel.handleAuthEvent(AuthEvent.EmailLogin(formState.email, formState.password))
            },
            enabled = formState.isValid && !uiState.isLoading,
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
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider with "OR"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Google Sign In Button
        OutlinedButton(
            onClick = { viewModel.handleAuthEvent(AuthEvent.GoogleSignIn) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 16.dp),
            enabled = !uiState.isLoading
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔗", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                Text("Continue with Google")
            }
        }

        // Anonymous Sign In Button
        TextButton(
            onClick = { viewModel.handleAuthEvent(AuthEvent.AnonymousSignIn) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Continue as Guest")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            TextButton(onClick = onNavigateToRegister) {
                Text("Sign Up")
            }
        }

        // Error Message
        uiState.errorMessage?.let { message ->
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

// ============================================================================
// PREVIEW-FRIENDLY VERSION
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenPreview(
    uiState: AuthUiState = AuthUiState(),
    formState: LoginFormState = LoginFormState(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onEmailLogin: (String, String) -> Unit = { _, _ -> },
    onGoogleSignIn: () -> Unit = {},
    onAnonymousSignIn: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Islamic App Logo/Icon
        Card(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🕌",
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Welcome Text
        Text(
            text = "Welcome to Tahlil",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sign in to continue your memorial prayers",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = formState.email,
            onValueChange = {},
            label = { Text("Email") },
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
            isError = formState.emailError != null,
            supportingText = formState.emailError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = formState.password,
            onValueChange = {},
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = formState.passwordError != null,
            supportingText = formState.passwordError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Forgot Password
        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = { onEmailLogin(formState.email, formState.password) },
            enabled = formState.isValid && !uiState.isLoading,
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
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider with "OR"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Google Sign In Button
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 16.dp),
            enabled = !uiState.isLoading
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔗", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                Text("Continue with Google")
            }
        }

        // Anonymous Sign In Button
        TextButton(
            onClick = onAnonymousSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Continue as Guest")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            TextButton(onClick = onNavigateToRegister) {
                Text("Sign Up")
            }
        }

        // Error Message
        uiState.errorMessage?.let { message ->
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

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class LoginScreenPreviewParameterProvider : PreviewParameterProvider<Pair<AuthUiState, LoginFormState>> {
    override val values: Sequence<Pair<AuthUiState, LoginFormState>> = sequenceOf(
        // Default state
        Pair(
            AuthUiState(),
            LoginFormState()
        ),
        // With data
        Pair(
            AuthUiState(),
            LoginFormState(
                email = "ahmad.abdullah@example.com",
                password = "password123",
                isValid = true
            )
        ),
        // Loading state
        Pair(
            AuthUiState(isLoading = true),
            LoginFormState(
                email = "ahmad.abdullah@example.com", 
                password = "password123",
                isValid = true
            )
        ),
        // Error state
        Pair(
            AuthUiState(errorMessage = "Invalid email or password. Please try again."),
            LoginFormState(
                email = "invalid@example.com",
                password = "wrongpass",
                emailError = "Please enter a valid email",
                passwordError = "Password must be at least 8 characters"
            )
        ),
        // Validation errors
        Pair(
            AuthUiState(),
            LoginFormState(
                email = "invalid-email",
                password = "123",
                emailError = "Please enter a valid email address",
                passwordError = "Password must be at least 8 characters"
            )
        )
    )
}

// ============================================================================
// SCREEN PREVIEWS
// ============================================================================

@Preview(name = "Login Screen - Empty State")
@Composable
fun PreviewLoginScreenEmpty() {
    TahlilTheme {
        Surface {
            LoginScreenPreview()
        }
    }
}

@Preview(name = "Login Screen - With Data")
@Composable  
fun PreviewLoginScreenWithData() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123", 
                    isValid = true
                )
            )
        }
    }
}

@Preview(name = "Login Screen - Loading State")
@Composable
fun PreviewLoginScreenLoading() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                uiState = AuthUiState(isLoading = true),
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123",
                    isValid = true
                )
            )
        }
    }
}

@Preview(name = "Login Screen - Error State")
@Composable
fun PreviewLoginScreenError() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                uiState = AuthUiState(
                    errorMessage = "Invalid email or password. Please check your credentials and try again."
                ),
                formState = LoginFormState(
                    email = "invalid@example.com",
                    password = "wrongpass"
                )
            )
        }
    }
}

@Preview(name = "Login Screen - Validation Errors")
@Composable
fun PreviewLoginScreenValidationErrors() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "invalid-email",
                    password = "123", 
                    emailError = "Please enter a valid email address",
                    passwordError = "Password must be at least 8 characters"
                )
            )
        }
    }
}

@Preview(name = "Login Screen - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLoginScreenDark() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123",
                    isValid = true
                )
            )
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Login Screen - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewLoginScreenLandscape() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123",
                    isValid = true
                )
            )
        }
    }
}

@Preview(
    name = "Login Screen - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewLoginScreenTablet() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123",
                    isValid = true
                )
            )
        }
    }
}

@Preview(
    name = "Login Screen - Small Phone", 
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewLoginScreenSmallPhone() {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                formState = LoginFormState(
                    email = "ahmad.abdullah@example.com",
                    password = "password123",
                    isValid = true
                )
            )
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Login Screen - Various States")
@Composable
fun PreviewLoginScreenDynamic(
    @PreviewParameter(LoginScreenPreviewParameterProvider::class) statesPair: Pair<AuthUiState, LoginFormState>
) {
    TahlilTheme {
        Surface {
            LoginScreenPreview(
                uiState = statesPair.first,
                formState = statesPair.second
            )
        }
    }
}