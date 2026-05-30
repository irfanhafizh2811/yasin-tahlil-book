package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
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
import com.app_muslim.surah_yasin.feature.auth.model.RegisterFormState
import com.app_muslim.surah_yasin.feature.auth.viewmodel.AuthViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.registerFormState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Handle successful registration
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Islamic Welcome Message
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 24.dp),
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
                        text = "🤲",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = "Join Our Islamic Community",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Create your account to start memorial prayers",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Name TextField
            OutlinedTextField(
                value = formState.name,
                onValueChange = { viewModel.updateRegisterForm(name = it) },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = formState.nameError != null,
                supportingText = formState.nameError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Email TextField
            OutlinedTextField(
                value = formState.email,
                onValueChange = { viewModel.updateRegisterForm(email = it) },
                label = { Text("Email Address") },
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
                onValueChange = { viewModel.updateRegisterForm(password = it) },
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = formState.passwordError != null,
                supportingText = formState.passwordError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Confirm Password TextField
            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = { viewModel.updateRegisterForm(confirmPassword = it) },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        if (formState.isValid) {
                            viewModel.handleAuthEvent(AuthEvent.EmailRegister(formState.email, formState.password, formState.name))
                        }
                    }
                ),
                isError = formState.confirmPasswordError != null,
                supportingText = formState.confirmPasswordError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Password Requirements
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Password Requirements:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val requirements = listOf(
                        "At least 8 characters",
                        "One uppercase letter",
                        "One lowercase letter", 
                        "One number",
                        "One special character"
                    )
                    
                    requirements.forEach { requirement ->
                        Text(
                            text = "• $requirement",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Register Button
            Button(
                onClick = {
                    viewModel.handleAuthEvent(AuthEvent.EmailRegister(formState.email, formState.password, formState.name))
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
                        text = "Create Account",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In")
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
}

// ============================================================================
// PREVIEW-FRIENDLY VERSION
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenPreview(
    uiState: AuthUiState = AuthUiState(),
    formState: RegisterFormState = RegisterFormState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onEmailRegister: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Islamic Welcome Message
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 24.dp),
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
                        text = "🤲",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = "Join Our Islamic Community",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Create your account to start memorial prayers",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Name TextField
            OutlinedTextField(
                value = formState.name,
                onValueChange = {},
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.nameError != null,
                supportingText = formState.nameError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Email TextField
            OutlinedTextField(
                value = formState.email,
                onValueChange = {},
                label = { Text("Email Address") },
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
                    imeAction = ImeAction.Next
                ),
                isError = formState.passwordError != null,
                supportingText = formState.passwordError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Confirm Password TextField
            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = {},
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = formState.confirmPasswordError != null,
                supportingText = formState.confirmPasswordError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Password Requirements
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Password Requirements:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val requirements = listOf(
                        "At least 8 characters",
                        "One uppercase letter",
                        "One lowercase letter", 
                        "One number",
                        "One special character"
                    )
                    
                    requirements.forEach { requirement ->
                        Text(
                            text = "• $requirement",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Register Button
            Button(
                onClick = { onEmailRegister(formState.email, formState.password, formState.name) },
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
                        text = "Create Account",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In")
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
}

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class RegisterScreenPreviewParameterProvider : PreviewParameterProvider<Pair<AuthUiState, RegisterFormState>> {
    override val values: Sequence<Pair<AuthUiState, RegisterFormState>> = sequenceOf(
        // Empty state
        Pair(AuthUiState(), RegisterFormState()),
        
        // Partially filled
        Pair(
            AuthUiState(),
            RegisterFormState(
                name = "Ahmad Abdullah",
                email = "ahmad.abdullah@example.com"
            )
        ),
        
        // Fully filled valid
        Pair(
            AuthUiState(),
            RegisterFormState(
                name = "Fatimah Hassan",
                email = "fatimah.hassan@example.com",
                password = "SecurePass123!",
                confirmPassword = "SecurePass123!",
                isValid = true
            )
        ),
        
        // Loading state
        Pair(
            AuthUiState(isLoading = true),
            RegisterFormState(
                name = "Yusuf Omar",
                email = "yusuf.omar@example.com",
                password = "SecurePass123!",
                confirmPassword = "SecurePass123!",
                isValid = true
            )
        ),
        
        // Validation errors
        Pair(
            AuthUiState(),
            RegisterFormState(
                name = "A",
                email = "invalid-email",
                password = "123",
                confirmPassword = "456",
                nameError = "Name must be at least 2 characters",
                emailError = "Please enter a valid email address",
                passwordError = "Password must be at least 8 characters",
                confirmPasswordError = "Passwords do not match"
            )
        ),
        
        // Error state
        Pair(
            AuthUiState(errorMessage = "Email address is already registered. Please use a different email or sign in."),
            RegisterFormState(
                name = "Ahmad Abdullah",
                email = "existing@example.com",
                password = "SecurePass123!",
                confirmPassword = "SecurePass123!"
            )
        )
    )
}

// ============================================================================
// SCREEN PREVIEWS
// ============================================================================

@Preview(name = "Register Screen - Empty State")
@Composable
fun PreviewRegisterScreenEmpty() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview()
        }
    }
}

@Preview(name = "Register Screen - Partially Filled")
@Composable
fun PreviewRegisterScreenPartial() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Ahmad Abdullah",
                    email = "ahmad.abdullah@example.com"
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Fully Filled")
@Composable  
fun PreviewRegisterScreenFilled() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Fatimah Hassan",
                    email = "fatimah.hassan@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!",
                    isValid = true
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Loading State")
@Composable
fun PreviewRegisterScreenLoading() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                uiState = AuthUiState(isLoading = true),
                formState = RegisterFormState(
                    name = "Yusuf Omar",
                    email = "yusuf.omar@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!",
                    isValid = true
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Validation Errors")
@Composable
fun PreviewRegisterScreenValidationErrors() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "A",
                    email = "invalid-email",
                    password = "123",
                    confirmPassword = "456",
                    nameError = "Name must be at least 2 characters",
                    emailError = "Please enter a valid email address",
                    passwordError = "Password must be at least 8 characters",
                    confirmPasswordError = "Passwords do not match"
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Error State")
@Composable
fun PreviewRegisterScreenError() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                uiState = AuthUiState(
                    errorMessage = "Email address is already registered. Please use a different email or sign in."
                ),
                formState = RegisterFormState(
                    name = "Ahmad Abdullah",
                    email = "existing@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!"
                )
            )
        }
    }
}

@Preview(name = "Register Screen - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegisterScreenDark() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Khadijah Ali",
                    email = "khadijah.ali@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!",
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
    name = "Register Screen - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewRegisterScreenLandscape() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Hassan Ibn Muhammad",
                    email = "hassan.muhammad@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!",
                    isValid = true
                )
            )
        }
    }
}

@Preview(
    name = "Register Screen - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewRegisterScreenTablet() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Aisha Yusuf",
                    email = "aisha.yusuf@example.com",
                    password = "SecurePass123!",
                    confirmPassword = "SecurePass123!",
                    isValid = true
                )
            )
        }
    }
}

@Preview(
    name = "Register Screen - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewRegisterScreenSmallPhone() {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                formState = RegisterFormState(
                    name = "Omar Ibn Khalid",
                    email = "omar.khalid@example.com"
                )
            )
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEW WITH PARAMETERS
// ============================================================================

@Preview(name = "Register Screen - Various States")
@Composable
fun PreviewRegisterScreenDynamic(
    @PreviewParameter(RegisterScreenPreviewParameterProvider::class) statesPair: Pair<AuthUiState, RegisterFormState>
) {
    TahlilTheme {
        Surface {
            RegisterScreenPreview(
                uiState = statesPair.first,
                formState = statesPair.second
            )
        }
    }
}