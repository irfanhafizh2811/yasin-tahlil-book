package com.app_muslim.surah_yasin.feature.memorial.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import android.content.res.Configuration
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.ui.frames.IslamicFrameOverlay
import com.app_muslim.surah_yasin.feature.memorial.ui.frames.IslamicFrameSelector
import java.util.*

@Composable
fun EnhancedPhotoUpload(
    photoData: PhotoData?,
    frameStyle: IslamicFrameStyle,
    uploadProgress: PhotoUploadProgress?,
    processingResult: PhotoProcessingResult?,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit,
    onFrameSelected: (IslamicFrameStyle) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onEditPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main photo display area
        PhotoDisplaySection(
            photoData = photoData,
            frameStyle = frameStyle,
            uploadProgress = uploadProgress,
            processingResult = processingResult,
            onPhotoSelected = onPhotoSelected,
            onPhotoRemoved = onPhotoRemoved,
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick,
            onEditPhoto = onEditPhoto
        )
        
        // Frame selection (only show when photo is present)
        AnimatedVisibility(
            visible = photoData != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            IslamicFrameSelector(
                selectedFrame = frameStyle,
                onFrameSelected = onFrameSelected
            )
        }
        
        // Photo processing status
        processingResult?.let { result ->
            PhotoProcessingStatus(result = result)
        }
        
        // Upload progress
        uploadProgress?.let { progress ->
            if (progress.isUploading) {
                PhotoUploadProgressCard(progress = progress)
            }
        }
        
        // Photo guidelines and tips
        PhotoUploadGuidelines()
    }
}

@Composable
private fun PhotoDisplaySection(
    photoData: PhotoData?,
    frameStyle: IslamicFrameStyle,
    uploadProgress: PhotoUploadProgress?,
    processingResult: PhotoProcessingResult?,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onEditPhoto: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f), // Memorial aspect ratio
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                photoData != null -> {
                    // Display uploaded photo
                    PhotoDisplayWithFrame(
                        photoData = photoData,
                        frameStyle = frameStyle,
                        onEditPhoto = onEditPhoto,
                        onRemovePhoto = onPhotoRemoved
                    )
                }
                uploadProgress?.isUploading == true -> {
                    // Show upload progress
                    PhotoUploadingState(progress = uploadProgress)
                }
                processingResult?.success == false -> {
                    // Show error state
                    PhotoErrorState(
                        error = processingResult.errorMessage ?: "Unknown error",
                        onRetry = { /* Handle retry */ }
                    )
                }
                else -> {
                    // Show photo selection options
                    PhotoSelectionOptions(
                        onCameraClick = onCameraClick,
                        onGalleryClick = onGalleryClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoDisplayWithFrame(
    photoData: PhotoData,
    frameStyle: IslamicFrameStyle,
    onEditPhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoData.firebaseUrl.ifEmpty { photoData.croppedUri ?: photoData.originalUri })
                .crossfade(true)
                .scale(Scale.FILL)
                .build(),
            contentDescription = "Memorial photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Islamic frame overlay
        IslamicFrameOverlay(
            frameStyle = frameStyle,
            modifier = Modifier.fillMaxSize()
        )
        
        // Edit controls overlay
        PhotoEditControls(
            onEditPhoto = onEditPhoto,
            onRemovePhoto = onRemovePhoto,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        
        // Photo info badge
        PhotoInfoBadge(
            photoData = photoData,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun PhotoEditControls(
    onEditPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Edit button
        IconButton(
            onClick = onEditPhoto,
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit photo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Remove button
        IconButton(
            onClick = onRemovePhoto,
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove photo",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PhotoInfoBadge(
    photoData: PhotoData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (photoData.isUploaded) Icons.Default.CloudDone else Icons.Default.Storage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (photoData.isUploaded) "Uploaded" else "Local",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PhotoUploadingState(
    progress: PhotoUploadProgress
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = progress.progress,
            modifier = Modifier.size(64.dp),
            strokeWidth = 6.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Uploading Memorial Photo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${(progress.progress * 100).toInt()}% complete",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        if (progress.uploadSpeed.isNotEmpty()) {
            Text(
                text = progress.uploadSpeed,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        if (progress.estimatedTimeRemaining.isNotEmpty()) {
            Text(
                text = "Time remaining: ${progress.estimatedTimeRemaining}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun PhotoErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Photo Upload Failed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Try Again")
        }
    }
}

@Composable
private fun PhotoSelectionOptions(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Add Memorial Photo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Choose a beautiful photo to honor your loved one",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Camera button
            ElevatedButton(
                onClick = onCameraClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Camera",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            // Gallery button
            ElevatedButton(
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gallery",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
private fun PhotoProcessingStatus(
    result: PhotoProcessingResult
) {
    if (result.errorMessage != null && !result.success) return // Error handled elsewhere
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (result.success) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (result.success) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = result.errorMessage ?: "Processing complete",
                style = MaterialTheme.typography.bodyMedium,
                color = if (result.success) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PhotoUploadProgressCard(
    progress: PhotoUploadProgress
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uploading to secure storage",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${(progress.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            if (progress.uploadSpeed.isNotEmpty() || progress.estimatedTimeRemaining.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = progress.uploadSpeed,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = progress.estimatedTimeRemaining,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoUploadGuidelines() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Photo Guidelines",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            val guidelines = listOf(
                "Photos are automatically optimized for best quality",
                "Choose from beautiful Islamic frames",
                "Photos are stored securely with encryption",
                "Maximum file size: 10MB",
                "Supported formats: JPG, PNG, WebP"
            )
            
            guidelines.forEach { guideline ->
                Row(
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    Text(
                        text = "• ",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = guideline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ========================================
// PREVIEW IMPLEMENTATIONS
// ========================================

@Preview(
    name = "No Photo State - Light Theme",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadNoPhoto() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = null,
            frameStyle = IslamicFrameStyle.NONE,
            uploadProgress = null,
            processingResult = null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "No Photo State - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewEnhancedPhotoUploadNoPhotoDark() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = null,
            frameStyle = IslamicFrameStyle.NONE,
            uploadProgress = null,
            processingResult = null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Photo with Classic Gold Frame",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadClassicGold() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "sample_photo_1",
                originalUri = null,
                firebaseUrl = "https://sample.com/photo.jpg",
                fileName = "Memorial Photo",
                fileSize = 1024000,
                mimeType = "image/jpeg",
                width = 1920,
                height = 1080,
                metadata = PhotoMetadata()
            ),
            frameStyle = IslamicFrameStyle.CLASSIC_GOLD,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 2048000,
                compressedSize = 1024000,
                optimizations = listOf(
                    PhotoOptimization.COMPRESSION,
                    PhotoOptimization.COLOR_ENHANCEMENT
                )
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Photo with Geometric Silver Frame",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadGeometric() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "sample_photo_2",
                originalUri = null,
                firebaseUrl = "https://sample.com/photo2.jpg",
                fileName = "Family Memorial",
                fileSize = 1536000,
                mimeType = "image/jpeg",
                width = 1920,
                height = 1280,
                metadata = PhotoMetadata()
            ),
            frameStyle = IslamicFrameStyle.GEOMETRIC_SILVER,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 3072000,
                compressedSize = 1536000,
                optimizations = listOf(
                    PhotoOptimization.COMPRESSION,
                    PhotoOptimization.ORIENTATION_FIX,
                    PhotoOptimization.METADATA_REMOVAL
                )
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Photo with Mosque Arch Frame",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadMosqueArch() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "sample_photo_3",
                originalUri = null,
                firebaseUrl = "https://sample.com/photo3.jpg",
                fileName = "Beloved Father",
                fileSize = 2048000,
                mimeType = "image/png",
                width = 1600,
                height = 1200,
                metadata = PhotoMetadata()
            ),
            frameStyle = IslamicFrameStyle.MOSQUE_ARCH,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 4096000,
                compressedSize = 2048000,
                optimizations = listOf(
                    PhotoOptimization.COMPRESSION,
                    PhotoOptimization.NOISE_REDUCTION,
                    PhotoOptimization.SHARPENING
                )
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Photo with Royal Ornate Frame",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadRoyalOrnate() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "sample_photo_4",
                originalUri = null,
                firebaseUrl = "https://sample.com/photo4.jpg",
                fileName = "Grandmother's Memory",
                fileSize = 1792000,
                mimeType = "image/jpeg",
                width = 2048,
                height = 1536,
                metadata = PhotoMetadata()
            ),
            frameStyle = IslamicFrameStyle.ROYAL_ORNATE,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 3584000,
                compressedSize = 1792000,
                optimizations = listOf(
                    PhotoOptimization.COMPRESSION,
                    PhotoOptimization.COLOR_ENHANCEMENT,
                    PhotoOptimization.METADATA_REMOVAL
                )
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Upload Progress - Preparing",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadProgressPreparing() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "uploading_photo",
                originalUri = null,
                firebaseUrl = "",
                fileName = "Uploading Memorial"
            ),
            frameStyle = IslamicFrameStyle.CLASSIC_GOLD,
            uploadProgress = PhotoUploadProgress(
                progress = 0.15f,
                bytesUploaded = 153600,
                totalBytes = 1024000,
                uploadSpeed = "2.3 MB/s",
                estimatedTimeRemaining = "12s",
                stage = UploadStage.PREPARING
            ),
            processingResult = null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Upload Progress - Uploading",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadProgressUploading() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "uploading_photo",
                originalUri = null,
                firebaseUrl = "",
                fileName = "Memorial Photo"
            ),
            frameStyle = IslamicFrameStyle.GEOMETRIC_SILVER,
            uploadProgress = PhotoUploadProgress(
                progress = 0.67f,
                bytesUploaded = 686080,
                totalBytes = 1024000,
                uploadSpeed = "1.8 MB/s",
                estimatedTimeRemaining = "5s",
                stage = UploadStage.UPLOADING
            ),
            processingResult = null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Upload Progress - Finalizing",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadProgressFinalizing() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "uploading_photo",
                originalUri = null,
                firebaseUrl = "",
                fileName = "Family Memorial"
            ),
            frameStyle = IslamicFrameStyle.FLORAL_PATTERN,
            uploadProgress = PhotoUploadProgress(
                progress = 0.95f,
                bytesUploaded = 972800,
                totalBytes = 1024000,
                uploadSpeed = "3.1 MB/s",
                estimatedTimeRemaining = "1s",
                stage = UploadStage.FINALIZING
            ),
            processingResult = null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Processing Error State",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadError() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = null,
            frameStyle = IslamicFrameStyle.NONE,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = false,
                errorMessage = "File size exceeds 10MB limit",
                errorCode = PhotoProcessingError.FILE_TOO_LARGE,
                warnings = listOf("Please choose a smaller image file")
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Network Error State",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadNetworkError() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "failed_upload",
                originalUri = null,
                firebaseUrl = "",
                fileName = "Failed Upload"
            ),
            frameStyle = IslamicFrameStyle.MINIMALIST_MODERN,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = false,
                errorMessage = "Upload failed - please check your internet connection",
                errorCode = PhotoProcessingError.NETWORK_ERROR,
                warnings = listOf("Retry when connected to the internet")
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Calligraphy Border Frame",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadCalligraphyFrame() {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "calligraphy_photo",
                originalUri = null,
                firebaseUrl = "https://sample.com/memorial.jpg",
                fileName = "Sacred Memorial",
                fileSize = 1843200,
                mimeType = "image/jpeg",
                width = 1800,
                height = 1350,
                metadata = PhotoMetadata()
            ),
            frameStyle = IslamicFrameStyle.CALLIGRAPHY_BORDER,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 3686400,
                compressedSize = 1843200,
                optimizations = listOf(
                    PhotoOptimization.COMPRESSION,
                    PhotoOptimization.COLOR_ENHANCEMENT,
                    PhotoOptimization.NOISE_REDUCTION,
                    PhotoOptimization.SHARPENING
                )
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

// Preview Parameter Providers for Dynamic Content

class PhotoDataPreviewProvider : PreviewParameterProvider<PhotoData?> {
    override val values = sequenceOf(
        null, // No photo state
        PhotoData(
            id = "memorial_1",
            originalUri = null,
            firebaseUrl = "https://sample.com/memorial1.jpg",
            fileName = "beloved_father.jpg"
        ),
        PhotoData(
            id = "memorial_2",
            originalUri = null,
            firebaseUrl = "https://sample.com/memorial2.jpg",
            fileName = "dear_mother.jpg"
        )
    )
}

class IslamicFramePreviewProvider : PreviewParameterProvider<IslamicFrameStyle> {
    override val values = sequenceOf(
        IslamicFrameStyle.NONE,
        IslamicFrameStyle.CLASSIC_GOLD,
        IslamicFrameStyle.GEOMETRIC_SILVER,
        IslamicFrameStyle.MOSQUE_ARCH,
        IslamicFrameStyle.ROYAL_ORNATE
    )
}

@Preview(
    name = "Dynamic Photo Data",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadDynamicPhoto(
    @PreviewParameter(PhotoDataPreviewProvider::class) photoData: PhotoData?
) {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = photoData,
            frameStyle = IslamicFrameStyle.CLASSIC_GOLD,
            uploadProgress = null,
            processingResult = if (photoData != null) {
                PhotoProcessingResult(
                    success = true,
                    originalSize = 2048000,
                    compressedSize = 1024000,
                    optimizations = listOf(PhotoOptimization.COMPRESSION)
                )
            } else null,
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}

@Preview(
    name = "Dynamic Islamic Frames",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewEnhancedPhotoUploadDynamicFrames(
    @PreviewParameter(IslamicFramePreviewProvider::class) frameStyle: IslamicFrameStyle
) {
    TahlilTheme {
        EnhancedPhotoUpload(
            photoData = PhotoData(
                id = "frame_demo",
                originalUri = null,
                firebaseUrl = "https://sample.com/demo.jpg",
                fileName = "frame_demonstration.jpg"
            ),
            frameStyle = frameStyle,
            uploadProgress = null,
            processingResult = PhotoProcessingResult(
                success = true,
                originalSize = 1536000,
                compressedSize = 768000,
                optimizations = listOf(PhotoOptimization.COMPRESSION)
            ),
            onPhotoSelected = { },
            onPhotoRemoved = { },
            onFrameSelected = { },
            onCameraClick = { },
            onGalleryClick = { },
            onEditPhoto = { }
        )
    }
}