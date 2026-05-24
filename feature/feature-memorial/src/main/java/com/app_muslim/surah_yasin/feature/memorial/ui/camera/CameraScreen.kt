package com.app_muslim.surah_yasin.feature.memorial.ui.camera

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var isCapturing by remember { mutableStateOf(false) }
    var captureError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    when {
        cameraPermissionState.status.isGranted -> {
            Box(modifier = modifier.fillMaxSize()) {
                // Camera Preview
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onCameraProviderReady = { provider ->
                        cameraProvider = provider
                        imageCapture = setupCamera(provider, lifecycleOwner, lensFacing, flashMode)
                    }
                )
                
                // Memorial Frame Overlay
                MemorialFrameOverlay(
                    modifier = Modifier.fillMaxSize()
                )
                
                // Top Controls
                CameraTopControls(
                    flashMode = flashMode,
                    onFlashToggle = { 
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                        cameraProvider?.let { provider ->
                            imageCapture = setupCamera(provider, lifecycleOwner, lensFacing, flashMode)
                        }
                    },
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding()
                )
                
                // Bottom Controls
                CameraBottomControls(
                    lensFacing = lensFacing,
                    isCapturing = isCapturing,
                    onCapturePhoto = {
                        coroutineScope.launch {
                            capturePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                onPhotoTaken = onPhotoTaken,
                                onCapturingChange = { isCapturing = it },
                                onError = { captureError = it }
                            )
                        }
                    },
                    onSwitchCamera = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                        cameraProvider?.let { provider ->
                            imageCapture = setupCamera(provider, lifecycleOwner, lensFacing, flashMode)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                )
                
                // Error Message
                captureError?.let { error ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = { captureError = null }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
        cameraPermissionState.status.shouldShowRationale -> {
            CameraPermissionRationale(
                onRequestPermission = {
                    cameraPermissionState.launchPermissionRequest()
                },
                onNavigateBack = onNavigateBack
            )
        }
        else -> {
            CameraPermissionDenied(
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraProviderReady: (ProcessCameraProvider) -> Unit
) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                
                // Setup camera provider
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    onCameraProviderReady(cameraProviderFuture.get())
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun MemorialFrameOverlay(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Memorial frame guide (3:4 aspect ratio)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(3f / 4f)
                .fillMaxHeight(0.8f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        // Corner guides
        val cornerSize = 20.dp
        val cornerStroke = 3.dp
        val cornerColor = MaterialTheme.colorScheme.primary
        
        // Top-left corner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 50.dp, y = 100.dp)
                .size(cornerSize)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cornerStroke)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .width(cornerStroke)
                    .fillMaxHeight()
                    .background(cornerColor)
            )
        }
        
        // Instructions
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Text(
                text = "Position the photo within the frame\nfor the perfect memorial",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun CameraTopControls(
    flashMode: Int,
    onFlashToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Flash toggle
        IconButton(
            onClick = onFlashToggle,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            val flashIcon = when (flashMode) {
                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                else -> Icons.Default.FlashOff
            }
            Icon(
                imageVector = flashIcon,
                contentDescription = "Flash mode",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Composable
private fun CameraBottomControls(
    lensFacing: Int,
    isCapturing: Boolean,
    onCapturePhoto: () -> Unit,
    onSwitchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gallery button placeholder
        Box(modifier = Modifier.size(64.dp))
        
        // Capture button
        Button(
            onClick = onCapturePhoto,
            enabled = !isCapturing,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture photo",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Switch camera button
        IconButton(
            onClick = onSwitchCamera,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.CameraFront,
                contentDescription = "Switch camera",
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun CameraPermissionRationale(
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "To capture memorial photos, we need access to your camera. This allows you to take beautiful photos to honor your loved ones.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Camera Permission")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Cancel")
        }
    }
}

@Composable
private fun CameraPermissionDenied(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Block,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera Access Denied",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Camera permission is required to capture photos. You can grant permission in your device settings.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Go Back")
        }
    }
}

private fun setupCamera(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    lensFacing: Int,
    flashMode: Int
): ImageCapture {
    // Unbind all use cases before rebinding
    cameraProvider.unbindAll()
    
    // Create camera selector
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    
    // Create preview use case
    val preview = Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
    
    // Create image capture use case
    val imageCapture = ImageCapture.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .setFlashMode(flashMode)
        .build()
    
    // Bind use cases to camera
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture
    )
    
    return imageCapture
}

private suspend fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onPhotoTaken: (Uri) -> Unit,
    onCapturingChange: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    if (imageCapture == null) {
        onError("Camera not ready. Please try again.")
        return
    }
    
    onCapturingChange(true)
    
    try {
        // Create output file
        val photoFile = createImageFile(context)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        // Capture image
        val result = suspendCoroutine<ImageCapture.OutputFileResults> { continuation ->
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        continuation.resume(outputFileResults)
                    }
                    
                    override fun onError(exception: ImageCaptureException) {
                        onError("Failed to capture photo: ${exception.message}")
                        onCapturingChange(false)
                    }
                }
            )
        }
        
        // Photo captured successfully
        val photoUri = Uri.fromFile(photoFile)
        onPhotoTaken(photoUri)
        
    } catch (e: Exception) {
        onError("Failed to capture photo: ${e.message}")
    } finally {
        onCapturingChange(false)
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "MEMORIAL_${timeStamp}.jpg"
    return File(context.getExternalFilesDir(null), fileName)
}