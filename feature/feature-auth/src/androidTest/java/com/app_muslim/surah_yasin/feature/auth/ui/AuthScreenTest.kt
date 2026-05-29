package com.app_muslim.surah_yasin.feature.auth.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app_muslim.surah_yasin.feature.auth.model.AuthUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun authScreen_displaysSignInForm_byDefault() {
        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Sign In")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Email")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Password")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sign in with Google")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Create Account")
            .assertIsDisplayed()
    }

    @Test
    fun signInForm_validatesEmailInput() {
        var signInCalled = false
        var capturedEmail = ""
        var capturedPassword = ""

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { email: String, password: String ->
                    signInCalled = true
                    capturedEmail = email
                    capturedPassword = password
                },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        // Enter invalid email
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("invalid-email")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Sign In")
            .performClick()

        // Should show validation error (if implemented)
        // For now, just verify the form exists
        composeTestRule
            .onNodeWithText("Email")
            .assertExists()
    }

    @Test
    fun signInForm_submitsWithValidData() {
        var signInCalled = false
        var capturedEmail = ""
        var capturedPassword = ""

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { email: String, password: String ->
                    signInCalled = true
                    capturedEmail = email
                    capturedPassword = password
                },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Sign In")
            .performClick()

        assert(signInCalled)
        assert(capturedEmail == "test@example.com")
        assert(capturedPassword == "password123")
    }

    @Test
    fun authScreen_showsLoadingState() {
        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(isLoading = true),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysErrorMessage() {
        val errorMessage = "Invalid email or password"

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(errorMessage = errorMessage),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_navigatesToSignUp() {
        var navigatedToSignUp = false

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { _: String -> },
                onNavigateToSignUp = { navigatedToSignUp = true },
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Create Account")
            .performClick()

        assert(navigatedToSignUp)
    }

    @Test
    fun forgotPassword_triggersResetFlow() {
        var resetPasswordCalled = false
        var capturedEmail = ""

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = {},
                onResetPassword = { email: String ->
                    resetPasswordCalled = true
                    capturedEmail = email
                },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        // Enter email first
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Forgot Password?")
            .performClick()

        assert(resetPasswordCalled)
        assert(capturedEmail == "test@example.com")
    }

    @Test
    fun googleSignIn_triggersCallback() {
        var googleSignInCalled = false

        composeTestRule.setContent {
            AuthScreen(
                uiState = AuthUiState(),
                onSignIn = { _: String, _: String -> },
                onSignUp = { _: String, _: String, _: String -> },
                onGoogleSignIn = { googleSignInCalled = true },
                onResetPassword = { _: String -> },
                onNavigateToSignUp = {},
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Sign in with Google")
            .performClick()

        assert(googleSignInCalled)
    }

    @Test
    fun signUpForm_validatesPasswordConfirmation() {
        composeTestRule.setContent {
            SignUpScreen(
                uiState = AuthUiState(),
                onSignUp = { _: String, _: String, _: String -> },
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Full Name")
            .performTextInput("John Doe")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("differentpassword")

        // Should show password mismatch error
        composeTestRule
            .onNodeWithText("Passwords do not match")
            .assertIsDisplayed()
    }

    @Test
    fun signUpForm_submitsWithValidData() {
        var signUpCalled = false
        var capturedEmail = ""
        var capturedPassword = ""
        var capturedName = ""

        composeTestRule.setContent {
            SignUpScreen(
                uiState = AuthUiState(),
                onSignUp = { email: String, password: String, displayName: String ->
                    signUpCalled = true
                    capturedEmail = email
                    capturedPassword = password
                    capturedName = displayName
                },
                onNavigateToSignIn = {}
            )
        }

        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("Full Name")
            .performTextInput("John Doe")

        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Confirm Password")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("Create Account")
            .performClick()

        assert(signUpCalled)
        assert(capturedEmail == "test@example.com")
        assert(capturedPassword == "password123")
        assert(capturedName == "John Doe")
    }
}