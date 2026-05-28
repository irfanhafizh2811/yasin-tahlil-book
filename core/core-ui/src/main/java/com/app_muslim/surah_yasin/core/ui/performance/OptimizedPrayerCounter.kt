package com.app_muslim.surah_yasin.core.ui.performance

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlinx.coroutines.delay

/**
 * High-Performance Prayer Counter Component
 * 
 * Optimized for <50ms response time with:
 * - Immediate visual feedback
 * - Optimized recomposition
 * - Efficient animation
 * - Smart state management
 * 
 * Performance targets:
 * - Response time: <50ms
 * - Animation smoothness: 60 FPS
 * - Memory efficiency: Minimal allocations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedPrayerCounter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    maxCount: Int = 100,
    enableHapticFeedback: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val haptic = LocalHapticFeedback.current
    
    // Performance-optimized animation states
    val animatedScale by rememberInfiniteTransition(label = "prayer_scale").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )
    
    // Immediate response animation (triggered on press)
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100, // Quick response
            easing = FastOutLinearInEasing
        ),
        label = "press_scale"
    )
    
    // Count animation for visual feedback
    var displayCount by remember { mutableIntStateOf(count) }
    val countScale by animateFloatAsState(
        targetValue = if (displayCount != count) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "count_scale"
    )
    
    // Update display count with animation
    LaunchedEffect(count) {
        if (count != displayCount) {
            displayCount = count
            delay(150) // Short animation duration
        }
    }
    
    // Progress calculation
    val progress = (count.toFloat() / maxCount.toFloat()).coerceIn(0f, 1f)
    
    Card(
        modifier = modifier
            .size(200.dp)
            .scale(pressScale * animatedScale),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Custom indication with immediate feedback
                ) {
                    // Immediate visual feedback
                    isPressed = true
                    
                    // Haptic feedback (if enabled)
                    if (enableHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    
                    // Trigger increment
                    onIncrement()
                    
                    // Reset press state
                    isPressed = false
                },
            contentAlignment = Alignment.Center
        ) {
            // Background circle with progress
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f + (progress * 0.3f))),
                contentAlignment = Alignment.Center
            ) {
                // Inner counting area
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Count display with animation
                    Text(
                        text = displayCount.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = (32 + (progress * 16)).sp // Grows with progress
                        ),
                        color = color,
                        modifier = Modifier.scale(countScale)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Progress indicator
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Linear progress indicator
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(80.dp)
                            .height(4.dp)
                            .clip(CircleShape),
                        color = color,
                        trackColor = color.copy(alpha = 0.2f),
                    )
                }
            }
            
            // Tap instruction (show when count is low)
            if (count < 5) {
                Text(
                    text = "Tap to Count",
                    style = MaterialTheme.typography.bodySmall,
                    color = color.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

/**
 * Compact Prayer Counter for Lists
 * Optimized for use in prayer lists and session history
 */
@Composable
fun CompactPrayerCounter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 60,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "compact_scale"
    )
    
    Box(
        modifier = modifier
            .size(size.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onIncrement()
                isPressed = false
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

/**
 * Prayer Counter with Voice Feedback
 * Provides audio cues for accessibility and enhanced user experience
 */
@Composable
fun VoiceEnabledPrayerCounter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    maxCount: Int = 100,
    enableVoice: Boolean = true,
    onVoiceAnnouncement: (String) -> Unit = {}
) {
    // Voice announcement trigger
    LaunchedEffect(count) {
        if (enableVoice && count > 0) {
            when {
                count % 10 == 0 -> onVoiceAnnouncement("$count prayers completed")
                count == maxCount -> onVoiceAnnouncement("Target reached! $count prayers completed")
                count == 1 -> onVoiceAnnouncement("Prayer counting started")
                count == 50 -> onVoiceAnnouncement("Halfway to your goal")
            }
        }
    }
    
    OptimizedPrayerCounter(
        count = count,
        onIncrement = onIncrement,
        modifier = modifier,
        maxCount = maxCount
    )
}

/**
 * Multi-Prayer Type Counter
 * Optimized for different Islamic prayer types (Tahlil, Yasin, etc.)
 */
@Composable
fun MultiPrayerTypeCounter(
    counts: Map<String, Int>,
    onIncrement: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedPrayerType: String,
    prayerTypes: List<String> = listOf("Tahlil", "Yasin", "Fatihah", "Istighfar", "Salawat")
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main counter for selected prayer type
        OptimizedPrayerCounter(
            count = counts[selectedPrayerType] ?: 0,
            onIncrement = { onIncrement(selectedPrayerType) },
            maxCount = when (selectedPrayerType) {
                "Tahlil" -> 100
                "Yasin" -> 3
                "Fatihah" -> 7
                "Istighfar" -> 100
                "Salawat" -> 100
                else -> 100
            }
        )
        
        // Prayer type selector
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(prayerTypes.size) { index ->
                val prayerType = prayerTypes[index]
                val isSelected = prayerType == selectedPrayerType
                val count = counts[prayerType] ?: 0
                
                FilterChip(
                    onClick = { /* Prayer type selection handled by parent */ },
                    label = {
                        Text(
                            text = "$prayerType ($count)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    selected = isSelected
                )
            }
        }
    }
}

// LazyRow wrapper removed - using direct androidx.compose.foundation.lazy.LazyRow instead