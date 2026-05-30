package com.app_muslim.surah_yasin.feature.memorial.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.viewmodel.CreateMemorialViewModel
import com.app_muslim.surah_yasin.feature.memorial.ui.components.*
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemorialScreen(
    onNavigateBack: () -> Unit,
    onMemorialCreated: (String) -> Unit,
    viewModel: CreateMemorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validationState by viewModel.validationState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isMemorialCreated) {
        if (uiState.isMemorialCreated && uiState.createdMemorialId.isNotEmpty()) {
            onMemorialCreated(uiState.createdMemorialId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Memorial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            CreateMemorialBottomBar(
                isLoading = uiState.isLoading,
                isValid = validationState.isValid,
                onCreateMemorial = { viewModel.createMemorial() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Islamic Header
            IslamicHeaderCard()
            
            // Deceased Information Section
            DeceasedInformationSection(
                deceasedName = uiState.deceasedName,
                deceasedNameArabic = uiState.deceasedNameArabic,
                onDeceasedNameChange = viewModel::updateDeceasedName,
                onDeceasedNameArabicChange = viewModel::updateDeceasedNameArabic,
                errors = validationState.errors
            )
            
            // Date Selection Section
            DateSelectionSection(
                gregorianDate = uiState.dateOfDeath,
                hijriDate = uiState.hijriDate,
                onGregorianDateChange = viewModel::updateDateOfDeath,
                onHijriDateChange = viewModel::updateHijriDate
            )
            
            // Memorial Message Section
            MemorialMessageSection(
                message = uiState.memorialMessage,
                messageArabic = uiState.memorialMessageArabic,
                onMessageChange = viewModel::updateMemorialMessage,
                onMessageArabicChange = viewModel::updateMemorialMessageArabic,
                errors = validationState.errors
            )
            
            // Prayer Type Selection
            PrayerTypeSection(
                selectedPrayerType = uiState.prayerType,
                onPrayerTypeChange = viewModel::updatePrayerType
            )
            
            // Privacy Level Selection
            PrivacyLevelSection(
                selectedPrivacy = uiState.privacyLevel,
                onPrivacyChange = viewModel::updatePrivacyLevel
            )
            
            // Photo Upload Section (Optional)
            PhotoUploadSection(
                photoUrl = uiState.photoUrl,
                isUploading = uiState.isUploadingPhoto,
                onPhotoSelected = viewModel::uploadPhoto,
                onPhotoRemoved = viewModel::removePhoto
            )
            
            // Validation Errors Display
            if (validationState.errors.isNotEmpty()) {
                ValidationErrorsCard(errors = validationState.errors)
            }
        }
    }
    
    // Loading Dialog
    if (uiState.isLoading) {
        CreateMemorialLoadingDialog()
    }
    
    // Error Dialog
    uiState.error?.let { error ->
        ErrorDialog(
            error = error,
            onDismiss = viewModel::clearError
        )
    }
}

@Composable
private fun IslamicHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "In the name of Allah, the Most Gracious, the Most Merciful",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create a memorial to honor your loved one and invite the global Muslim community to pray for their soul.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CreateMemorialBottomBar(
    isLoading: Boolean,
    isValid: Boolean,
    onCreateMemorial: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCreateMemorial,
                enabled = isValid && !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Creating..." else "Create Memorial",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CreateMemorialLoadingDialog() {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss while loading */ },
        title = {
            Text(
                text = "Creating Memorial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please wait while we create the memorial for your loved one...",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إِنَّا لِلّهِ وَإِنَّـا إِلَيْهِ رَاجِعُونَ",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Indeed we belong to Allah, and indeed to Him we will return",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = { }
    )
}

@Composable
private fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Error Creating Memorial",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

// Preview Data Providers
class CreateMemorialStateProvider : PreviewParameterProvider<CreateMemorialUiState> {
    override val values: Sequence<CreateMemorialUiState> = sequenceOf(
        // Empty state
        CreateMemorialUiState(),
        // Partially filled state
        CreateMemorialUiState(
            deceasedName = "Ahmed Hassan",
            deceasedNameArabic = "أحمد حسن",
            memorialMessage = "A beloved father and devoted Muslim",
            prayerType = PrayerType.YASIN
        ),
        // Complete form state
        CreateMemorialUiState(
            deceasedName = "Fatimah Al-Zahra",
            deceasedNameArabic = "فاطمة الزهراء",
            memorialMessage = "A devoted mother who lived by Islamic principles",
            memorialMessageArabic = "أم مخلصة عاشت وفقا للمبادئ الإسلامية",
            prayerType = PrayerType.TAHLIL,
            privacyLevel = PrivacyLevel.COMMUNITY,
            photoUrl = "sample_photo_url"
        ),
        // Loading state
        CreateMemorialUiState(
            deceasedName = "Omar Abdullah",
            isLoading = true
        )
    )
}

data class CreateMemorialUiState(
    val deceasedName: String = "",
    val deceasedNameArabic: String = "",
    val dateOfDeath: String = "",
    val hijriDate: String = "",
    val memorialMessage: String = "",
    val memorialMessageArabic: String = "",
    val prayerType: PrayerType = PrayerType.YASIN,
    val privacyLevel: PrivacyLevel = PrivacyLevel.FAMILY,
    val photoUrl: String = "",
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isMemorialCreated: Boolean = false,
    val createdMemorialId: String = "",
    val error: String? = null
)

enum class PrayerType { YASIN, TAHLIL, FATIHAH, DUA }
enum class PrivacyLevel { PRIVATE, FAMILY, COMMUNITY, PUBLIC }

data class CreateMemorialValidationState(
    val isValid: Boolean = false,
    val errors: List<String> = emptyList()
)

// Mock preview-friendly components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemorialScreenPreview(
    uiState: CreateMemorialUiState = CreateMemorialUiState(),
    validationState: CreateMemorialValidationState = CreateMemorialValidationState()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Memorial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            CreateMemorialBottomBar(
                isLoading = uiState.isLoading,
                isValid = validationState.isValid,
                onCreateMemorial = { }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Islamic Header
            IslamicHeaderCard()
            
            // Deceased Information Section
            DeceasedInformationSection(
                deceasedName = uiState.deceasedName,
                deceasedNameArabic = uiState.deceasedNameArabic,
                onDeceasedNameChange = { },
                onDeceasedNameArabicChange = { },
                errors = validationState.errors
            )
            
            // Date Selection Section
            DateSelectionSection(
                gregorianDate = uiState.dateOfDeath,
                hijriDate = uiState.hijriDate,
                onGregorianDateChange = { },
                onHijriDateChange = { }
            )
            
            // Memorial Message Section
            MemorialMessageSection(
                message = uiState.memorialMessage,
                messageArabic = uiState.memorialMessageArabic,
                onMessageChange = { },
                onMessageArabicChange = { },
                errors = validationState.errors
            )
            
            // Prayer Type Selection
            PrayerTypeSection(
                selectedPrayerType = uiState.prayerType,
                onPrayerTypeChange = { }
            )
            
            // Privacy Level Selection
            PrivacyLevelSection(
                selectedPrivacy = uiState.privacyLevel,
                onPrivacyChange = { }
            )
            
            // Photo Upload Section (Optional)
            PhotoUploadSection(
                photoUrl = uiState.photoUrl,
                isUploading = uiState.isUploadingPhoto,
                onPhotoSelected = { },
                onPhotoRemoved = { }
            )
            
            // Validation Errors Display
            if (validationState.errors.isNotEmpty()) {
                ValidationErrorsCard(errors = validationState.errors)
            }
        }
    }
    
    // Loading Dialog
    if (uiState.isLoading) {
        CreateMemorialLoadingDialog()
    }
    
    // Error Dialog
    uiState.error?.let { error ->
        ErrorDialog(
            error = error,
            onDismiss = { }
        )
    }
}

@Composable
fun DeceasedInformationSection(
    deceasedName: String,
    deceasedNameArabic: String,
    onDeceasedNameChange: (String) -> Unit,
    onDeceasedNameArabicChange: (String) -> Unit,
    errors: List<String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Deceased Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = deceasedName,
                onValueChange = onDeceasedNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = deceasedNameArabic,
                onValueChange = onDeceasedNameArabicChange,
                label = { Text("Arabic Name (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun DateSelectionSection(
    gregorianDate: String,
    hijriDate: String,
    onGregorianDateChange: (String) -> Unit,
    onHijriDateChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Date of Passing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = gregorianDate,
                    onValueChange = onGregorianDateChange,
                    label = { Text("Gregorian Date") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hijriDate,
                    onValueChange = onHijriDateChange,
                    label = { Text("Hijri Date") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MemorialMessageSection(
    message: String,
    messageArabic: String,
    onMessageChange: (String) -> Unit,
    onMessageArabicChange: (String) -> Unit,
    errors: List<String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Memorial Message",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Memorial Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            OutlinedTextField(
                value = messageArabic,
                onValueChange = onMessageArabicChange,
                label = { Text("Arabic Message (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}

@Composable
fun PrayerTypeSection(
    selectedPrayerType: PrayerType,
    onPrayerTypeChange: (PrayerType) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Prayer Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            PrayerType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedPrayerType == type,
                        onClick = { onPrayerTypeChange(type) }
                    )
                    Text(
                        text = type.name,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyLevelSection(
    selectedPrivacy: PrivacyLevel,
    onPrivacyChange: (PrivacyLevel) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Privacy Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            PrivacyLevel.entries.forEach { level ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedPrivacy == level,
                        onClick = { onPrivacyChange(level) }
                    )
                    Text(
                        text = level.name,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoUploadSection(
    photoUrl: String,
    isUploading: Boolean,
    onPhotoSelected: (String) -> Unit,
    onPhotoRemoved: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Photo (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { onPhotoSelected("sample_photo") },
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (photoUrl.isEmpty()) "Add Photo" else "Change Photo")
            }
            if (photoUrl.isNotEmpty()) {
                TextButton(onClick = onPhotoRemoved) {
                    Text("Remove Photo")
                }
            }
        }
    }
}

@Composable
fun ValidationErrorsCard(errors: List<String>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Please fix the following errors:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
            errors.forEach { error ->
                Text(
                    text = "• $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Preview Functions
@Preview(name = "Create Memorial Screen - Empty State")
@Composable
fun PreviewCreateMemorialScreenEmpty() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview()
        }
    }
}

@Preview(name = "Create Memorial Screen - Filled Form")
@Composable
fun PreviewCreateMemorialScreenFilled() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Khadijah bint Khuwaylid",
                    deceasedNameArabic = "خديجة بنت خويلد",
                    memorialMessage = "The first believer and beloved wife of Prophet Muhammad",
                    memorialMessageArabic = "أول المؤمنات وزوجة النبي محمد الحبيبة",
                    prayerType = PrayerType.TAHLIL,
                    privacyLevel = PrivacyLevel.COMMUNITY,
                    photoUrl = "sample_photo_url"
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - Loading State")
@Composable
fun PreviewCreateMemorialScreenLoading() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Abu Bakr As-Siddiq",
                    deceasedNameArabic = "أبو بكر الصديق",
                    isLoading = true
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - With Errors")
@Composable
fun PreviewCreateMemorialScreenWithErrors() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                validationState = CreateMemorialValidationState(
                    isValid = false,
                    errors = listOf(
                        "Deceased name is required",
                        "Memorial message cannot be empty",
                        "Please select a prayer type"
                    )
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - Dynamic States", group = "Dynamic")
@Composable
fun PreviewCreateMemorialScreenDynamic(
    @PreviewParameter(CreateMemorialStateProvider::class) uiState: CreateMemorialUiState
) {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(uiState = uiState)
        }
    }
}

@Preview(name = "Create Memorial Screen - Dark Theme")
@Composable
fun PreviewCreateMemorialScreenDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Ali ibn Abi Talib",
                    deceasedNameArabic = "علي بن أبي طالب",
                    memorialMessage = "The fourth Caliph and cousin of Prophet Muhammad",
                    prayerType = PrayerType.YASIN,
                    privacyLevel = PrivacyLevel.PUBLIC
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewCreateMemorialScreenTablet() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Umar ibn al-Khattab",
                    deceasedNameArabic = "عمر بن الخطاب",
                    memorialMessage = "The second Caliph known for his justice",
                    prayerType = PrayerType.FATIHAH
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - Landscape", device = "spec:width=640dp,height=360dp,dpi=160,orientation=landscape")
@Composable
fun PreviewCreateMemorialScreenLandscape() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Uthman ibn Affan",
                    deceasedNameArabic = "عثمان بن عفان",
                    memorialMessage = "The third Caliph and compiler of the Quran"
                )
            )
        }
    }
}

@Preview(name = "Create Memorial Screen - Small Phone", device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun PreviewCreateMemorialScreenSmallPhone() {
    TahlilTheme {
        Surface {
            CreateMemorialScreenPreview(
                uiState = CreateMemorialUiState(
                    deceasedName = "Hassan ibn Ali",
                    deceasedNameArabic = "الحسن بن علي",
                    memorialMessage = "Grandson of Prophet Muhammad"
                )
            )
        }
    }
}

@Preview(name = "Islamic Header Card")
@Composable
fun PreviewIslamicHeaderCard() {
    TahlilTheme {
        Surface {
            IslamicHeaderCard()
        }
    }
}

@Preview(name = "Loading Dialog")
@Composable
fun PreviewCreateMemorialLoadingDialog() {
    TahlilTheme {
        Surface {
            CreateMemorialLoadingDialog()
        }
    }
}

@Preview(name = "Error Dialog")
@Composable
fun PreviewErrorDialog() {
    TahlilTheme {
        Surface {
            ErrorDialog(
                error = "Failed to create memorial. Please check your internet connection and try again.",
                onDismiss = { }
            )
        }
    }
}