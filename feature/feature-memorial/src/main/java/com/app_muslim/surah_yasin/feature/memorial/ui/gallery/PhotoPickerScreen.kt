package com.app_muslim.surah_yasin.feature.memorial.ui.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.memorial.model.PhotoValidationError
import com.app_muslim.surah_yasin.feature.memorial.model.PhotoValidator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerScreen(
    onPhotoSelected: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPhotos by remember { mutableStateOf(listOf<Uri>()) }
    var validationError by remember { mutableStateOf<String?>(null) }
    
    // Storage permission for reading external storage
    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    
    // Modern Photo Picker (Android 13+)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val validationResult = PhotoValidator.validatePhoto(uri, context)
                if (validationResult.isValid) {
                    onPhotoSelected(uri)
                } else {
                    validationError = validationResult.errors.firstOrNull()?.message
                }
            }
        }
    )
    
    // Multiple photo picker for future features
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            selectedPhotos = uris
        }
    )

    LaunchedEffect(Unit) {
        if (!storagePermissionState.status.isGranted) {
            storagePermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Memorial Photo",
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
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Islamic Header
            PhotoSelectionHeader()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Photo Selection Options
            PhotoSelectionOptions(
                onSelectSinglePhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onSelectMultiplePhotos = {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            
            // Selected Photos Preview
            if (selectedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SelectedPhotosGrid(
                    photos = selectedPhotos,
                    onPhotoSelected = onPhotoSelected,
                    onPhotoRemoved = { uri ->
                        selectedPhotos = selectedPhotos - uri
                    }
                )
            }
            
            // Validation Error
            validationError?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                PhotoValidationErrorCard(
                    error = error,
                    onDismiss = { validationError = null }
                )
            }
            
            // Guidelines
            Spacer(modifier = Modifier.height(24.dp))
            PhotoGuidelines()
        }
    }
}

@Composable
private fun PhotoSelectionHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Choose a Beautiful Memorial Photo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a dignified photo that honors the memory of your loved one. The photo will be used to create a respectful memorial.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PhotoSelectionOptions(
    onSelectSinglePhoto: () -> Unit,
    onSelectMultiplePhotos: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Photo Selection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        // Single photo selection
        OutlinedCard(
            onClick = onSelectSinglePhoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Select Single Photo",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Choose one photo for the memorial",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Multiple photo selection (for future features)
        OutlinedCard(
            onClick = onSelectMultiplePhotos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Select Multiple Photos",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Choose up to 5 photos for photo album memorial",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text("Soon", style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun SelectedPhotosGrid(
    photos: List<Uri>,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: (Uri) -> Unit
) {
    Column {
        Text(
            text = "Selected Photos (${photos.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photo ->
                PhotoGridItem(
                    photoUri = photo,
                    onPhotoClick = { onPhotoSelected(photo) },
                    onRemoveClick = { onPhotoRemoved(photo) }
                )
            }
        }
    }
}

@Composable
private fun PhotoGridItem(
    photoUri: Uri,
    onPhotoClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        onClick = onPhotoClick,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Selected photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Remove button
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove photo",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }
            
            // Selection indicator
            Surface(
                shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
private fun PhotoValidationErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PhotoGuidelines() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Photo Guidelines",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val guidelines = listOf(
                "Choose high-quality photos with good lighting",
                "Select dignified photos that honor your loved one",
                "Maximum file size: 10MB",
                "Supported formats: JPG, PNG, WebP",
                "Photos will be cropped to 3:4 memorial format",
                "Inappropriate content will be automatically detected"
            )
            
            guidelines.forEach { guideline ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = guideline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Islamic Considerations",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Photos are optional in Islamic memorials. Choose images that reflect the good character and memory of your loved one in a respectful manner.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}