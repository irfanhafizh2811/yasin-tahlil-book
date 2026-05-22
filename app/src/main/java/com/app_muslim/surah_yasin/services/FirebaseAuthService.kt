package com.app_muslim.surah_yasin.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.app_muslim.surah_yasin.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    val isUserAuthenticated: Boolean
        get() = auth.currentUser != null
    
    // Email/Password Authentication
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            
            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            
            user.updateProfile(profileUpdates).await()
            Result.success(user)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Google Sign-In
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Anonymous Authentication (for guest users)
    suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            Result.success(result.user!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Phone Authentication Flow
    suspend fun sendPhoneVerification(phoneNumber: String): Result<String> {
        return try {
            // Implementation for phone verification would go here
            // Returns verification ID for code confirmation
            Result.success("verification_id_placeholder")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Method expected by AuthViewModel
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return signInWithEmail(email, password)
    }
    
    // Method expected by AuthViewModel  
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Method expected by AuthViewModel
    suspend fun startPhoneNumberVerification(phoneNumber: String): Result<String> {
        return sendPhoneVerification(phoneNumber)
    }
    
    // Method expected by AuthViewModel
    suspend fun verifyPhoneNumberWithCode(verificationId: String, smsCode: String): Result<FirebaseUser> {
        return try {
            // This would use PhoneAuthProvider.getCredential(verificationId, smsCode)
            // For now, return failure as it needs more implementation
            Result.failure(Exception("Phone verification not fully implemented"))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Method expected by AuthViewModel
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return resetPassword(email)
    }
    
    
    // Reset Password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Sign Out
    fun signOut() {
        auth.signOut()
    }
    
    // Delete Account
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Convert FirebaseUser to your User model
    fun toUser(firebaseUser: FirebaseUser): User {
        return User(
            hasInitialize = true,
            name = firebaseUser.displayName ?: firebaseUser.email ?: "Anonymous"
        )
    }
    
    // Auth state listener as Flow
    fun getAuthStateFlow(): Flow<FirebaseUser?> = flow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // This would emit user changes, but Flow implementation
            // requires callback-to-flow conversion for proper implementation
        }
        // Proper implementation would use callbackFlow
        emit(auth.currentUser)
    }
    
    // Update user profile
    suspend fun updateProfile(displayName: String?, photoUrl: String? = null): Result<Unit> {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                displayName?.let { setDisplayName(it) }
                photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
            }.build()
            
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Email Verification
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}