package com.app_muslim.surah_yasin.feature.memorial.ui.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.memorial.model.PhotoCropAspectRatio
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import kotlin.math.max
import kotlin.math.min

@Composable
fun PhotoCropScreen(
    photoUri: Uri,
    onCropCompleted: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var aspectRatio by remember { mutableStateOf(PhotoCropAspectRatio.MEMORIAL_STANDARD) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isCropping by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CropTopBar(
                onNavigateBack = onNavigateBack,
                onCropConfirm = {
                    // TODO: Implement actual cropping logic
                    onCropCompleted(photoUri)
                },
                isCropping = isCropping
            )
        },
        bottomBar = {
            CropBottomBar(
                selectedAspectRatio = aspectRatio,
                onAspectRatioChange = { newRatio ->
                    aspectRatio = newRatio
                    // Reset crop when aspect ratio changes
                    scale = 1f
                    offset = Offset.Zero
                },
                onResetCrop = {
                    scale = 1f
                    offset = Offset.Zero
                    cropRect = Rect.Zero
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Instructions
            CropInstructions()
            
            // Crop area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                PhotoCropView(
                    photoUri = photoUri,
                    aspectRatio = aspectRatio,
                    scale = scale,
                    offset = offset,
                    onScaleChange = { newScale -> scale = newScale },
                    onOffsetChange = { newOffset -> offset = newOffset },
                    onImageSizeChanged = { size -> imageSize = size },
                    onCropRectChanged = { rect -> cropRect = rect }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropTopBar(
    onNavigateBack: () -> Unit,
    onCropConfirm: () -> Unit,
    isCropping: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "Crop Memorial Photo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel"
                )
            }
        },
        actions = {
            if (isCropping) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                TextButton(
                    onClick = onCropConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Done",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun CropInstructions() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Drag to move • Pinch to zoom • Position your loved one's photo within the frame",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PhotoCropView(
    photoUri: Uri,
    aspectRatio: PhotoCropAspectRatio,
    scale: Float,
    offset: Offset,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Offset) -> Unit,
    onImageSizeChanged: (IntSize) -> Unit,
    onCropRectChanged: (Rect) -> Unit
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                containerSize = coordinates.size
                onImageSizeChanged(coordinates.size)
            }
    ) {
        // Background image with gestures
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUri)
                .crossfade(true)
                .build(),
            contentDescription = "Photo to crop",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.5f, 3f)
                        onScaleChange(newScale)
                        onOffsetChange(offset + pan)
                    }
                },
            contentScale = ContentScale.Crop
        )
        
        // Crop overlay
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCropOverlay(
                containerSize = size,
                aspectRatio = aspectRatio,
                onCropRectChanged = onCropRectChanged
            )
        }
    }
}

private fun DrawScope.drawCropOverlay(
    containerSize: Size,
    aspectRatio: PhotoCropAspectRatio,
    onCropRectChanged: (Rect) -> Unit
) {
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val cropBorderColor = Color.White
    val gridColor = Color.White.copy(alpha = 0.5f)
    
    // Calculate crop area based on aspect ratio
    val cropRect = calculateCropRect(containerSize, aspectRatio.ratio)
    onCropRectChanged(cropRect)
    
    // Draw overlay (darken areas outside crop)
    // Top
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, 0f),
        size = Size(containerSize.width, cropRect.top)
    )
    
    // Bottom
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.bottom),
        size = Size(containerSize.width, containerSize.height - cropRect.bottom)
    )
    
    // Left
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.top),
        size = Size(cropRect.left, cropRect.height)
    )
    
    // Right
    drawRect(
        color = overlayColor,
        topLeft = Offset(cropRect.right, cropRect.top),
        size = Size(containerSize.width - cropRect.right, cropRect.height)
    )
    
    // Draw crop border
    drawRect(
        color = cropBorderColor,
        topLeft = cropRect.topLeft,
        size = cropRect.size,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw rule of thirds grid
    val gridLineWidth = 1.dp.toPx()
    
    // Vertical grid lines
    val verticalLine1 = cropRect.left + cropRect.width / 3
    val verticalLine2 = cropRect.left + 2 * cropRect.width / 3
    
    drawLine(
        color = gridColor,
        start = Offset(verticalLine1, cropRect.top),
        end = Offset(verticalLine1, cropRect.bottom),
        strokeWidth = gridLineWidth
    )
    
    drawLine(
        color = gridColor,
        start = Offset(verticalLine2, cropRect.top),
        end = Offset(verticalLine2, cropRect.bottom),
        strokeWidth = gridLineWidth
    )
    
    // Horizontal grid lines
    val horizontalLine1 = cropRect.top + cropRect.height / 3
    val horizontalLine2 = cropRect.top + 2 * cropRect.height / 3
    
    drawLine(
        color = gridColor,
        start = Offset(cropRect.left, horizontalLine1),
        end = Offset(cropRect.right, horizontalLine1),
        strokeWidth = gridLineWidth
    )
    
    drawLine(
        color = gridColor,
        start = Offset(cropRect.left, horizontalLine2),
        end = Offset(cropRect.right, horizontalLine2),
        strokeWidth = gridLineWidth
    )
    
    // Draw corner handles
    val handleSize = 20.dp.toPx()
    val handleLength = 40.dp.toPx()
    val handleStroke = 3.dp.toPx()
    
    // Top-left corner
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.left, cropRect.top),
        end = Offset(cropRect.left + handleLength, cropRect.top),
        strokeWidth = handleStroke
    )
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.left, cropRect.top),
        end = Offset(cropRect.left, cropRect.top + handleLength),
        strokeWidth = handleStroke
    )
    
    // Top-right corner
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.right, cropRect.top),
        end = Offset(cropRect.right - handleLength, cropRect.top),
        strokeWidth = handleStroke
    )
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.right, cropRect.top),
        end = Offset(cropRect.right, cropRect.top + handleLength),
        strokeWidth = handleStroke
    )
    
    // Bottom-left corner
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.left, cropRect.bottom),
        end = Offset(cropRect.left + handleLength, cropRect.bottom),
        strokeWidth = handleStroke
    )
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.left, cropRect.bottom),
        end = Offset(cropRect.left, cropRect.bottom - handleLength),
        strokeWidth = handleStroke
    )
    
    // Bottom-right corner
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.right, cropRect.bottom),
        end = Offset(cropRect.right - handleLength, cropRect.bottom),
        strokeWidth = handleStroke
    )
    drawLine(
        color = cropBorderColor,
        start = Offset(cropRect.right, cropRect.bottom),
        end = Offset(cropRect.right, cropRect.bottom - handleLength),
        strokeWidth = handleStroke
    )
}

private fun calculateCropRect(containerSize: Size, aspectRatio: Float): Rect {
    return if (aspectRatio == 0f) {
        // Original aspect ratio - use full container
        Rect(Offset.Zero, containerSize)
    } else {
        val containerAspectRatio = containerSize.width / containerSize.height
        
        val cropSize = if (containerAspectRatio > aspectRatio) {
            // Container is wider than crop - fit height
            val cropHeight = containerSize.height * 0.8f
            val cropWidth = cropHeight * aspectRatio
            Size(cropWidth, cropHeight)
        } else {
            // Container is taller than crop - fit width
            val cropWidth = containerSize.width * 0.8f
            val cropHeight = cropWidth / aspectRatio
            Size(cropWidth, cropHeight)
        }
        
        val cropLeft = (containerSize.width - cropSize.width) / 2
        val cropTop = (containerSize.height - cropSize.height) / 2
        
        Rect(Offset(cropLeft, cropTop), cropSize)
    }
}

@Composable
private fun CropBottomBar(
    selectedAspectRatio: PhotoCropAspectRatio,
    onAspectRatioChange: (PhotoCropAspectRatio) -> Unit,
    onResetCrop: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Aspect Ratio Selection
            Text(
                text = "Aspect Ratio",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PhotoCropAspectRatio.entries) { ratio ->
                    AspectRatioChip(
                        aspectRatio = ratio,
                        isSelected = ratio == selectedAspectRatio,
                        onClick = { onAspectRatioChange(ratio) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reset button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onResetCrop,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun AspectRatioChip(
    aspectRatio: PhotoCropAspectRatio,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = aspectRatio.displayName,
                    style = MaterialTheme.typography.labelMedium
                )
                if (aspectRatio.ratio != 0f) {
                    Text(
                        text = "${aspectRatio.ratio}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else null
    )
}

// Preview parameter provider for PhotoCrop previews

class PhotoCropStateProvider : PreviewParameterProvider<Pair<PhotoCropAspectRatio, Boolean>> {
    override val values = sequenceOf(
        Pair(PhotoCropAspectRatio.MEMORIAL_STANDARD, false),
        Pair(PhotoCropAspectRatio.SQUARE, false),
        Pair(PhotoCropAspectRatio.PORTRAIT, false),
        Pair(PhotoCropAspectRatio.LANDSCAPE, false),
        Pair(PhotoCropAspectRatio.ORIGINAL, false),
        Pair(PhotoCropAspectRatio.MEMORIAL_STANDARD, true) // Processing state
    )
}

// Preview Functions
@Preview(name = "Photo Crop - Memorial Standard")
@Composable
private fun PhotoCropScreenPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Square")
@Composable
private fun PhotoCropScreenSquarePreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.SQUARE,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Portrait")
@Composable
private fun PhotoCropScreenPortraitPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.PORTRAIT,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Wide")
@Composable
private fun PhotoCropScreenWidePreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.LANDSCAPE,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Original")
@Composable
private fun PhotoCropScreenOriginalPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.ORIGINAL,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Processing")
@Composable
private fun PhotoCropScreenProcessingPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
            isCropping = true,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PhotoCropScreenDarkPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhotoCropScreenTabletPreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Photo Crop - Landscape", device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape")
@Composable
private fun PhotoCropScreenLandscapePreview() {
    TahlilTheme {
        PhotoCropScreenContent(
            aspectRatio = PhotoCropAspectRatio.MEMORIAL_STANDARD,
            isCropping = false,
            onNavigateBack = {},
            onCropConfirm = {},
            onAspectRatioChange = {},
            onResetCrop = {}
        )
    }
}

@Preview(name = "Aspect Ratio Chips")
@Composable
private fun AspectRatioChipsPreview() {
    TahlilTheme {
        Surface {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PhotoCropAspectRatio.entries) { ratio ->
                    AspectRatioChip(
                        aspectRatio = ratio,
                        isSelected = ratio == PhotoCropAspectRatio.MEMORIAL_STANDARD,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Preview(name = "Crop Instructions")
@Composable
private fun CropInstructionsPreview() {
    TahlilTheme {
        Surface {
            CropInstructions()
        }
    }
}

@Composable
private fun PhotoCropScreenContent(
    aspectRatio: PhotoCropAspectRatio,
    isCropping: Boolean,
    onNavigateBack: () -> Unit,
    onCropConfirm: () -> Unit,
    onAspectRatioChange: (PhotoCropAspectRatio) -> Unit,
    onResetCrop: () -> Unit
) {
    Scaffold(
        topBar = {
            CropTopBar(
                onNavigateBack = onNavigateBack,
                onCropConfirm = onCropConfirm,
                isCropping = isCropping
            )
        },
        bottomBar = {
            CropBottomBar(
                selectedAspectRatio = aspectRatio,
                onAspectRatioChange = onAspectRatioChange,
                onResetCrop = onResetCrop
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CropInstructions()
            
            // Mock crop area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Mock crop overlay
                    drawCropOverlay(
                        containerSize = size,
                        aspectRatio = aspectRatio,
                        onCropRectChanged = {}
                    )
                }
                
                // Placeholder for actual photo
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(120.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Memorial Photo Placeholder",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}