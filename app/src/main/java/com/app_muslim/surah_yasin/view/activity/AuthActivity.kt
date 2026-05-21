package com.app_muslim.surah_yasin.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.databinding.ActivityAuthBinding
import com.app_muslim.surah_yasin.data.model.*
import com.app_muslim.surah_yasin.services.FirebaseAuthService
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.view.dialog.LanguageDialog
import com.app_muslim.surah_yasin.view.dialog.RegionDialog
import com.app_muslim.surah_yasin.view.dialog.SchoolOfThoughtDialog
import com.app_muslim.surah_yasin.vm.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : BaseActivity() {

    companion object {
        private const val TAG = "AuthActivity"
        const val EXTRA_FLOW_TYPE = "flow_type"
        const val FLOW_LOGIN = "login"
        const val FLOW_REGISTER = "register"
        const val FLOW_CULTURAL_SETUP = "cultural_setup"
    }

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModel()
    private val authService: FirebaseAuthService by inject()
    private val firestoreService: FirestoreService by inject()
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private var currentFlowType = FLOW_LOGIN
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentFlowType = intent.getStringExtra(EXTRA_FLOW_TYPE) ?: FLOW_LOGIN
        
        setupGoogleSignIn()
        setupUI()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupUI() {
        // Apply Islamic theme
        applyIslamicTheme()
        
        when (currentFlowType) {
            FLOW_LOGIN -> setupLoginFlow()
            FLOW_REGISTER -> setupRegisterFlow()
            FLOW_CULTURAL_SETUP -> setupCulturalSetupFlow()
        }
    }

    private fun applyIslamicTheme() {
        // Set Islamic background and theme colors
        binding.root.setBackgroundResource(R.drawable.bg_islamic_auth)
        
        // Set Islamic greeting based on time
        val islamicGreeting = getIslamicGreeting()
        binding.textGreeting.text = islamicGreeting
        
        // Apply RTL support if needed
        setupRTLSupport()
    }

    private fun getIslamicGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..11 -> getString(R.string.greeting_morning) // "Good morning"
            in 12..17 -> getString(R.string.greeting_afternoon) // "Peace be upon you"
            in 18..21 -> getString(R.string.greeting_evening) // "Good evening"
            else -> getString(R.string.greeting_general) // "Welcome"
        }
    }

    private fun setupRTLSupport() {
        // Check if current language is RTL
        val currentLanguage = resources.configuration.locale.language
        if (currentLanguage == "ar" || currentLanguage == "ur" || currentLanguage == "fa") {
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    private fun setupLoginFlow() {
        binding.apply {
            textTitle.text = getString(R.string.welcome_back)
            textSubtitle.text = getString(R.string.sign_in_to_continue_prayers)
            
            // Show login fields
            layoutEmailLogin.visibility = View.VISIBLE
            layoutRegister.visibility = View.GONE
            layoutCulturalSetup.visibility = View.GONE
            
            buttonPrimary.text = getString(R.string.sign_in)
            textSecondaryAction.text = getString(R.string.dont_have_account)
            buttonSecondaryAction.text = getString(R.string.create_account)
        }
    }

    private fun setupRegisterFlow() {
        binding.apply {
            textTitle.text = getString(R.string.join_global_ummah)
            textSubtitle.text = getString(R.string.create_account_for_memorial_prayers)
            
            // Show register fields
            layoutEmailLogin.visibility = View.GONE
            layoutRegister.visibility = View.VISIBLE
            layoutCulturalSetup.visibility = View.GONE
            
            buttonPrimary.text = getString(R.string.create_account)
            textSecondaryAction.text = getString(R.string.already_have_account)
            buttonSecondaryAction.text = getString(R.string.sign_in)
        }
    }

    private fun setupCulturalSetupFlow() {
        binding.apply {
            textTitle.text = getString(R.string.cultural_preferences)
            textSubtitle.text = getString(R.string.customize_islamic_experience)
            
            // Show cultural setup
            layoutEmailLogin.visibility = View.GONE
            layoutRegister.visibility = View.GONE
            layoutCulturalSetup.visibility = View.VISIBLE
            
            buttonPrimary.text = getString(R.string.save_preferences)
            textSecondaryAction.visibility = View.GONE
            buttonSecondaryAction.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.Success -> {
                    showLoading(false)
                    handleAuthSuccess(state.user)
                }
                is AuthState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                AuthState.Idle -> showLoading(false)
            }
        }

        authViewModel.profileCreationState.observe(this) { state ->
            when (state) {
                is ProfileCreationState.Loading -> showLoading(true)
                is ProfileCreationState.Success -> {
                    showLoading(false)
                    navigateToMainActivity()
                }
                is ProfileCreationState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                ProfileCreationState.Idle -> showLoading(false)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Primary button (Sign In / Create Account / Save Preferences)
            buttonPrimary.setOnClickListener {
                when (currentFlowType) {
                    FLOW_LOGIN -> handleEmailLogin()
                    FLOW_REGISTER -> handleEmailRegistration()
                    FLOW_CULTURAL_SETUP -> handleCulturalSetup()
                }
            }

            // Secondary action button
            buttonSecondaryAction.setOnClickListener {
                when (currentFlowType) {
                    FLOW_LOGIN -> switchToRegisterFlow()
                    FLOW_REGISTER -> switchToLoginFlow()
                }
            }

            // Google Sign-In
            buttonGoogleSignIn.setOnClickListener {
                handleGoogleSignIn()
            }

            // Phone Authentication
            buttonPhoneSignIn.setOnClickListener {
                handlePhoneAuthentication()
            }

            // Anonymous/Guest access
            buttonGuestAccess.setOnClickListener {
                handleGuestAccess()
            }

            // Forgot password
            textForgotPassword.setOnClickListener {
                handleForgotPassword()
            }

            // Language selection
            buttonLanguageSelector.setOnClickListener {
                showLanguageDialog()
            }

            // Region selection
            buttonRegionSelector.setOnClickListener {
                showRegionDialog()
            }

            // School of thought selection
            buttonSchoolOfThoughtSelector.setOnClickListener {
                showSchoolOfThoughtDialog()
            }
        }
    }

    private fun handleEmailLogin() {
        if (isLoading) return
        
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        if (!validateEmailLogin(email, password)) return

        authViewModel.signInWithEmail(email, password)
    }

    private fun handleEmailRegistration() {
        if (isLoading) return
        
        val name = binding.editTextName.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val password = binding.editTextPasswordRegister.text.toString()
        val confirmPassword = binding.editTextPasswordConfirm.text.toString()

        if (!validateEmailRegistration(name, email, password, confirmPassword)) return

        authViewModel.registerWithEmail(email, password, name)
    }

    private fun handleCulturalSetup() {
        if (isLoading) return
        
        val culturalPreferences = buildCulturalPreferences()
        val userProfile = buildUserProfile(culturalPreferences)
        
        authViewModel.createUserProfile(userProfile)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            handleGoogleSignInResult(account)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            showError(getString(R.string.google_signin_failed))
        }
    }

    private fun handleGoogleSignIn() {
        if (isLoading) return
        
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        account.idToken?.let { idToken ->
            authViewModel.signInWithGoogle(idToken)
        }
    }

    private fun handlePhoneAuthentication() {
        // TODO: Implement phone authentication
        Toast.makeText(this, "Phone authentication will be available soon", Toast.LENGTH_SHORT).show()
    }

    private fun handleGuestAccess() {
        if (isLoading) return
        authViewModel.signInAnonymously()
    }

    private fun handleForgotPassword() {
        val email = binding.editTextEmail.text.toString().trim()
        if (email.isBlank()) {
            Toast.makeText(this, getString(R.string.enter_email_for_reset), Toast.LENGTH_SHORT).show()
            return
        }
        
        authViewModel.resetPassword(email)
        Toast.makeText(this, getString(R.string.password_reset_sent), Toast.LENGTH_LONG).show()
    }

    private fun handleAuthSuccess(user: com.google.firebase.auth.FirebaseUser) {
        Log.d(TAG, "Authentication successful for user: ${user.uid}")
        
        // Check if user profile exists
        lifecycleScope.launch {
            try {
                val existingProfile = firestoreService.getUserProfile(user.uid)
                if (existingProfile != null) {
                    // Profile exists, navigate to main activity
                    navigateToMainActivity()
                } else {
                    // New user, show cultural setup
                    switchToCulturalSetupFlow()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user profile", e)
                // Assume new user and show cultural setup
                switchToCulturalSetupFlow()
            }
        }
    }

    private fun switchToLoginFlow() {
        currentFlowType = FLOW_LOGIN
        setupLoginFlow()
        clearInputFields()
    }

    private fun switchToRegisterFlow() {
        currentFlowType = FLOW_REGISTER
        setupRegisterFlow()
        clearInputFields()
    }

    private fun switchToCulturalSetupFlow() {
        currentFlowType = FLOW_CULTURAL_SETUP
        setupCulturalSetupFlow()
    }

    private fun clearInputFields() {
        binding.apply {
            editTextEmail.text?.clear()
            editTextPassword.text?.clear()
            editTextName.text?.clear()
            editTextEmailRegister.text?.clear()
            editTextPasswordRegister.text?.clear()
            editTextPasswordConfirm.text?.clear()
        }
    }

    private fun showLanguageDialog() {
        LanguageDialog().show(supportFragmentManager, "language_dialog")
    }

    private fun showRegionDialog() {
        val dialog = RegionDialog.newInstance()
        dialog.setSelectedRegion(getCurrentSelectedRegion())
        dialog.setOnRegionSelectedListener { region ->
            binding.textSelectedRegion.text = region.displayName
            updateCulturalPreferences()
        }
        dialog.show(supportFragmentManager, "region_dialog")
    }

    private fun showSchoolOfThoughtDialog() {
        val dialog = SchoolOfThoughtDialog.newInstance()
        dialog.setSelectedSchool(getCurrentSelectedSchool())
        dialog.setOnSchoolSelectedListener { school ->
            binding.textSelectedSchoolOfThought.text = school.displayName
            updateCulturalPreferences()
        }
        dialog.show(supportFragmentManager, "school_dialog")
    }

    private fun getCurrentSelectedRegion(): IslamicRegion? {
        val currentText = binding.textSelectedRegion.text.toString()
        return IslamicRegion.values().find { it.displayName == currentText }
    }

    private fun getCurrentSelectedSchool(): SchoolOfThought? {
        val currentText = binding.textSelectedSchoolOfThought.text.toString()
        return SchoolOfThought.values().find { it.displayName == currentText }
    }

    private fun updateCulturalPreferences() {
        // Update UI state when cultural preferences change
        val hasRegion = binding.textSelectedRegion.text.toString() != getString(R.string.select_region)
        val hasLanguage = binding.textSelectedLanguage.text.toString() != getString(R.string.select_language)
        val hasSchool = binding.textSelectedSchoolOfThought.text.toString() != getString(R.string.select_school)
        
        // Enable/disable save button based on required selections
        binding.buttonPrimary.isEnabled = hasRegion && hasLanguage && hasSchool
    }

    private fun buildCulturalPreferences(): CulturalPreferences {
        // Get selections from UI components
        val selectedLanguage = binding.textSelectedLanguage.text.toString()
        val selectedRegion = binding.textSelectedRegion.text.toString()
        val selectedSchool = binding.textSelectedSchoolOfThought.text.toString()
        
        return CulturalPreferences(
            region = IslamicRegion.valueOf(selectedRegion.uppercase().replace(" ", "_")),
            primaryLanguage = selectedLanguage,
            schoolOfThought = SchoolOfThought.valueOf(selectedSchool.uppercase().replace(" ", "_")),
            showArabicText = binding.checkboxArabicText.isChecked,
            showTransliteration = binding.checkboxTransliteration.isChecked
        )
    }

    private fun buildUserProfile(culturalPreferences: CulturalPreferences): UserProfile {
        val currentUser = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("No authenticated user")

        return UserProfile(
            userId = currentUser.uid,
            displayName = currentUser.displayName ?: "User",
            email = currentUser.email,
            phoneNumber = currentUser.phoneNumber,
            culturalPreferences = culturalPreferences,
            notificationPreferences = NotificationPreferences(),
            privacySettings = PrivacySettings(),
            prayerPreferences = PrayerPreferences(),
            accountSettings = AccountSettings()
        )
    }

    private fun validateEmailLogin(email: String, password: String): Boolean {
        if (email.isBlank()) {
            binding.editTextEmail.error = getString(R.string.email_required)
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = getString(R.string.invalid_email)
            return false
        }
        
        if (password.isBlank()) {
            binding.editTextPassword.error = getString(R.string.password_required)
            return false
        }
        
        return true
    }

    private fun validateEmailRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.name_required)
            return false
        }
        
        if (email.isBlank()) {
            binding.editTextEmailRegister.error = getString(R.string.email_required)
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmailRegister.error = getString(R.string.invalid_email)
            return false
        }
        
        if (password.length < 8) {
            binding.editTextPasswordRegister.error = getString(R.string.password_too_short)
            return false
        }
        
        if (password != confirmPassword) {
            binding.editTextPasswordConfirm.error = getString(R.string.passwords_dont_match)
            return false
        }
        
        return true
    }

    private fun showLoading(show: Boolean) {
        isLoading = show
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.buttonPrimary.isEnabled = !show
        binding.buttonGoogleSignIn.isEnabled = !show
        binding.buttonPhoneSignIn.isEnabled = !show
        binding.buttonGuestAccess.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, "Auth error: $message")
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources
    }
}

// States for ViewModel
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: com.google.firebase.auth.FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class ProfileCreationState {
    object Idle : ProfileCreationState()
    object Loading : ProfileCreationState()
    object Success : ProfileCreationState()
    data class Error(val message: String) : ProfileCreationState()
}