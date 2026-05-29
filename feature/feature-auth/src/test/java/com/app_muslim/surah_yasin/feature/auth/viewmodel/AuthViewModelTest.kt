package com.app_muslim.surah_yasin.feature.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockAuthService = mockk<FirebaseAuthService>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = AuthViewModel(mockAuthService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun initialState_isCorrect() {
        assertEquals(AuthUiState(), viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isAuthenticated)
    }

    @Test
    fun checkAuthenticationStatus_whenUserLoggedIn_updatesState() = runTest {
        val mockUser = mockk<com.google.firebase.auth.FirebaseUser>()
        every { mockUser.uid } returns "test-uid"
        every { mockUser.email } returns "test@example.com"
        every { mockUser.displayName } returns "Test User"
        
        every { mockAuthService.currentUser } returns flowOf(mockUser)
        every { mockAuthService.isUserAuthenticated() } returns true

        viewModel.checkAuthenticationStatus()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertTrue(currentState.isAuthenticated)
        assertEquals("test@example.com", currentState.userEmail)
    }

    @Test
    fun checkAuthenticationStatus_whenUserNotLoggedIn_updatesState() = runTest {
        every { mockAuthService.currentUser } returns flowOf(null)
        every { mockAuthService.isUserAuthenticated() } returns false

        viewModel.checkAuthenticationStatus()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isAuthenticated)
        assertEquals(null, currentState.userEmail)
    }

    @Test
    fun signInWithEmail_success_updatesState() = runTest {
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { mockAuthService.signInWithEmailAndPassword(email, password) } returns mockk {
            every { isSuccessful } returns true
            every { result } returns mockk {
                every { user } returns mockk {
                    every { uid } returns "test-uid"
                    every { email } returns email
                    every { displayName } returns "Test User"
                }
            }
        }

        viewModel.signInWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isLoading)
        assertEquals(null, currentState.error)
    }

    @Test
    fun signInWithEmail_failure_updatesStateWithError() = runTest {
        val email = "test@example.com"
        val password = "wrongpassword"
        val exception = Exception("Authentication failed")
        
        coEvery { mockAuthService.signInWithEmailAndPassword(email, password) } returns mockk {
            every { isSuccessful } returns false
            every { exception } returns exception
        }

        viewModel.signInWithEmail(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isLoading)
        assertTrue(currentState.error != null)
    }

    @Test
    fun signUpWithEmail_success_updatesState() = runTest {
        val email = "newuser@example.com"
        val password = "password123"
        val displayName = "New User"
        
        coEvery { mockAuthService.createUserWithEmailAndPassword(email, password) } returns mockk {
            every { isSuccessful } returns true
            every { result } returns mockk {
                every { user } returns mockk {
                    every { uid } returns "new-uid"
                    every { email } returns email
                    every { displayName } returns displayName
                }
            }
        }

        viewModel.signUpWithEmail(email, password, displayName)
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isLoading)
        assertEquals(null, currentState.error)
    }

    @Test
    fun signOut_clearsUserState() = runTest {
        every { mockAuthService.signOut() } just Runs

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertFalse(currentState.isAuthenticated)
        assertEquals(null, currentState.userEmail)
    }

    @Test
    fun resetPassword_sendsResetEmail() = runTest {
        val email = "test@example.com"
        
        coEvery { mockAuthService.sendPasswordResetEmail(email) } returns mockk {
            every { isSuccessful } returns true
        }

        viewModel.resetPassword(email)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockAuthService.sendPasswordResetEmail(email) }
    }

    @Test
    fun updateProfile_updatesUserInformation() = runTest {
        val newDisplayName = "Updated Name"
        val newPhotoUrl = "https://example.com/photo.jpg"
        
        coEvery { mockAuthService.updateProfile(newDisplayName, newPhotoUrl) } returns mockk {
            every { isSuccessful } returns true
        }

        viewModel.updateProfile(newDisplayName, newPhotoUrl)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockAuthService.updateProfile(newDisplayName, newPhotoUrl) }
    }
}