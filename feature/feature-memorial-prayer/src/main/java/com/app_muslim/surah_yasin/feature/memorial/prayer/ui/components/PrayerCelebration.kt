package com.app_muslim.surah_yasin.feature.memorial.prayer.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Prayer completion celebration component with Islamic-themed animations
 * Includes confetti, star burst, and progress celebration
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrayerCompletionCelebration(
    isVisible: Boolean,
    prayerType: String,
    totalPrayers: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)) + 
               scaleIn(animationSpec = tween(800, easing = EaseOutBounce)),
        exit = fadeOut(animationSpec = tween(300)) + 
              scaleOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti background
            ConfettiAnimation(
                isVisible = isVisible,
                modifier = Modifier.fillMaxSize()
            )
            
            // Main celebration card
            CelebrationCard(
                prayerType = prayerType,
                totalPrayers = totalPrayers,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun ConfettiAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val confettiPieces = remember { 
        List(50) { 
            ConfettiPiece(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.1f,
                color = listOf(
                    Color(0xFF4CAF50), // Islamic green
                    Color(0xFFFFD700), // Gold
                    Color(0xFF00BCD4), // Cyan
                    Color(0xFFFF9800)  // Orange
                ).random(),
                rotation = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 2f + 1f
            )
        }
    }
    
    val animationProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
    )
    
    Canvas(modifier = modifier) {
        confettiPieces.forEach { piece ->
            drawConfettiPiece(piece, animationProgress, size)
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val y: Float,
    val color: Color,
    val rotation: Float,
    val speed: Float
)

private fun DrawScope.drawConfettiPiece(
    piece: ConfettiPiece,
    progress: Float,
    canvasSize: androidx.compose.ui.geometry.Size
) {
    val currentY = piece.y + (progress * piece.speed * 1.5f)
    if (currentY > 1.1f) return
    
    val x = piece.x * canvasSize.width
    val y = currentY * canvasSize.height
    
    // Draw confetti as small rectangles
    drawRect(
        color = piece.color,
        topLeft = Offset(x - 5.dp.toPx(), y - 10.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 20.dp.toPx())
    )
}

@Composable
private fun CelebrationCard(
    prayerType: String,
    totalPrayers: Int,
    onDismiss: () -> Unit
) {
    val scaleAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )
    
    val rotationAnimation by animateFloatAsState(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Card(
        modifier = Modifier
            .padding(32.dp)
            .scale(scaleAnimation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Islamic star animation
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Celebration",
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(rotationAnimation),
                    tint = Color(0xFFFFD700) // Gold
                )
                
                // Pulsing ring effect
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scaleAnimation)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            
            // Success message
            Text(
                text = "بارك الله فيك", // Barakallahu feek
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Prayer Session Completed!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            // Prayer details
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = prayerType.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "$totalPrayers prayers completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Dismiss button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Milestone celebration for reaching prayer targets (25%, 50%, 75%, 100%)
 */
@Composable
fun MilestoneCelebration(
    isVisible: Boolean,
    milestonePercentage: Int,
    prayerType: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(600, easing = EaseOutBack)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Milestone",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Milestone Reached!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "$milestonePercentage% of $prayerType completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                IconButton(onClick = onDismiss) {
                    Text(
                        text = "×",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

/**
 * Real-time prayer counter with smooth animations
 */
@Composable
fun AnimatedPrayerCounter(
    currentCount: Int,
    targetCount: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = currentCount.toFloat() / targetCount.toFloat()
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val animatedCount by animateIntAsState(
        targetValue = currentCount,
        animationSpec = tween(300, easing = EaseOutCubic)
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Circular progress indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(180.dp),
                strokeWidth = 8.dp,
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$animatedCount",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF4CAF50)
                )
                
                Text(
                    text = "/ $targetCount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        // Increment button
        FloatingActionButton(
            onClick = onIncrement,
            modifier = Modifier.size(80.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Progress text
        Text(
            text = "${(animatedProgress * 100).toInt()}% Complete",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}