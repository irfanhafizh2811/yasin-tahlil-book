package com.app_muslim.surah_yasin.feature.memorial.ui.frames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.feature.memorial.model.IslamicFrameStyle
import kotlin.math.*

@Composable
fun IslamicFrameOverlay(
    frameStyle: IslamicFrameStyle,
    modifier: Modifier = Modifier,
    deceasedName: String = "",
    memorialText: String = ""
) {
    if (frameStyle == IslamicFrameStyle.NONE) return
    
    val density = LocalDensity.current
    
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        when (frameStyle) {
            IslamicFrameStyle.GEOMETRIC_GOLD -> drawGeometricGoldFrame(size, density)
            IslamicFrameStyle.CALLIGRAPHY_BORDER -> drawCalligraphyBorder(size, density, memorialText)
            IslamicFrameStyle.MOSQUE_ARCH -> drawMosqueArchFrame(size, density)
            IslamicFrameStyle.CRESCENT_STARS -> drawCrescentStarsFrame(size, density)
            IslamicFrameStyle.ARABESQUE_PATTERN -> drawArabesqueFrame(size, density)
            IslamicFrameStyle.BISMILLAH_FRAME -> drawBismillahFrame(size, density)
            IslamicFrameStyle.MEMORIAL_VERSES -> drawMemorialVersesFrame(size, density, deceasedName)
            else -> Unit
        }
    }
}

@Composable
fun IslamicFrameSelector(
    selectedFrame: IslamicFrameStyle,
    onFrameSelected: (IslamicFrameStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Islamic Frames",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(IslamicFrameStyle.entries) { frame ->
                IslamicFramePreview(
                    frameStyle = frame,
                    isSelected = frame == selectedFrame,
                    onClick = { onFrameSelected(frame) }
                )
            }
        }
        
        // Cultural significance text
        if (selectedFrame != IslamicFrameStyle.NONE) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = selectedFrame.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedFrame.culturalSignificance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun IslamicFramePreview(
    frameStyle: IslamicFrameStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        } else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Mini preview canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when (frameStyle) {
                    IslamicFrameStyle.NONE -> drawNoFrame(size)
                    IslamicFrameStyle.GEOMETRIC_GOLD -> drawGeometricGoldFrame(size, density, true)
                    IslamicFrameStyle.CALLIGRAPHY_BORDER -> drawCalligraphyBorder(size, density, "", true)
                    IslamicFrameStyle.MOSQUE_ARCH -> drawMosqueArchFrame(size, density, true)
                    IslamicFrameStyle.CRESCENT_STARS -> drawCrescentStarsFrame(size, density, true)
                    IslamicFrameStyle.ARABESQUE_PATTERN -> drawArabesqueFrame(size, density, true)
                    IslamicFrameStyle.BISMILLAH_FRAME -> drawBismillahFrame(size, density, true)
                    IslamicFrameStyle.MEMORIAL_VERSES -> drawMemorialVersesFrame(size, density, "", true)
                }
            }
            
            // Selection indicator
            if (isSelected) {
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// Frame drawing implementations
private fun DrawScope.drawNoFrame(canvasSize: Size) {
    drawRect(
        color = Color.Gray.copy(alpha = 0.3f),
        topLeft = Offset.Zero,
        size = canvasSize,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw "None" text
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            color = Color.Gray.toArgb()
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
        canvas.nativeCanvas.drawText(
            "None",
            canvasSize.width / 2,
            canvasSize.height / 2,
            paint
        )
    }
}

private fun DrawScope.drawGeometricGoldFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    isPreview: Boolean = false
) {
    val frameWidth = if (isPreview) 4.dp.toPx() else 20.dp.toPx()
    val goldenColor = Color(0xFFD4AF37)
    val darkGold = Color(0xFFB8860B)
    
    // Outer golden border
    drawRect(
        color = goldenColor,
        topLeft = Offset.Zero,
        size = canvasSize,
        style = Stroke(width = frameWidth)
    )
    
    // Inner decorative border
    val innerInset = frameWidth * 0.3f
    drawRect(
        color = darkGold,
        topLeft = Offset(innerInset, innerInset),
        size = Size(canvasSize.width - 2 * innerInset, canvasSize.height - 2 * innerInset),
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Geometric patterns at corners
    if (!isPreview) {
        drawGeometricCorners(canvasSize, goldenColor, frameWidth)
    }
    
    // Islamic star patterns
    drawIslamicStars(canvasSize, goldenColor, isPreview)
}

private fun DrawScope.drawCalligraphyBorder(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    text: String,
    isPreview: Boolean = false
) {
    val frameWidth = if (isPreview) 3.dp.toPx() else 16.dp.toPx()
    val borderColor = Color(0xFF2E4057)
    val textColor = Color(0xFF1C3A52)
    
    // Main border
    drawRect(
        color = borderColor,
        topLeft = Offset.Zero,
        size = canvasSize,
        style = Stroke(width = frameWidth)
    )
    
    // Calligraphy text areas (simulated)
    if (!isPreview) {
        val textAreas = listOf(
            Rect(0f, 0f, canvasSize.width, frameWidth), // Top
            Rect(0f, canvasSize.height - frameWidth, canvasSize.width, frameWidth), // Bottom
            Rect(0f, 0f, frameWidth, canvasSize.height), // Left
            Rect(canvasSize.width - frameWidth, 0f, frameWidth, canvasSize.height) // Right
        )
        
        textAreas.forEach { area ->
            drawRect(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(area.left, area.top),
                size = Size(area.width, area.height)
            )
        }
        
        // Simulated Arabic calligraphy patterns
        drawCalligraphyPatterns(canvasSize, textColor, frameWidth)
    }
}

private fun DrawScope.drawMosqueArchFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    isPreview: Boolean = false
) {
    val archColor = Color(0xFF8B4513)
    val accentColor = Color(0xFFD2691E)
    val frameWidth = if (isPreview) 3.dp.toPx() else 15.dp.toPx()
    
    // Draw arch at the top
    val archHeight = canvasSize.height * 0.15f
    val archWidth = canvasSize.width * 0.6f
    val archCenter = Offset(canvasSize.width / 2, 0f)
    
    // Main arch
    drawPath(
        path = createArchPath(archCenter, archWidth, archHeight),
        color = archColor,
        style = Stroke(width = frameWidth)
    )
    
    // Side pillars
    if (!isPreview) {
        val pillarWidth = frameWidth
        val pillarHeight = canvasSize.height * 0.8f
        
        // Left pillar
        drawRect(
            color = archColor,
            topLeft = Offset(0f, archHeight),
            size = Size(pillarWidth, pillarHeight),
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Right pillar
        drawRect(
            color = archColor,
            topLeft = Offset(canvasSize.width - pillarWidth, archHeight),
            size = Size(pillarWidth, pillarHeight),
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Decorative elements
        drawMosqueDecorations(canvasSize, accentColor)
    }
}

private fun DrawScope.drawCrescentStarsFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    isPreview: Boolean = false
) {
    val crescentColor = Color(0xFF4A5568)
    val starColor = Color(0xFFEDF2F7)
    val size = if (isPreview) 8.dp.toPx() else 20.dp.toPx()
    
    // Draw crescents at corners
    val crescents = listOf(
        Offset(size, size), // Top-left
        Offset(canvasSize.width - size, size), // Top-right
        Offset(size, canvasSize.height - size), // Bottom-left
        Offset(canvasSize.width - size, canvasSize.height - size) // Bottom-right
    )
    
    crescents.forEach { center ->
        drawCrescent(center, size * 0.8f, crescentColor)
    }
    
    // Draw stars
    if (!isPreview) {
        val stars = listOf(
            Offset(canvasSize.width / 2, size), // Top center
            Offset(canvasSize.width / 2, canvasSize.height - size), // Bottom center
            Offset(size, canvasSize.height / 2), // Left center
            Offset(canvasSize.width - size, canvasSize.height / 2) // Right center
        )
        
        stars.forEach { center ->
            drawIslamicStar(center, size * 0.6f, starColor)
        }
    }
    
    // Border lines
    val borderColor = crescentColor.copy(alpha = 0.5f)
    drawRect(
        color = borderColor,
        topLeft = Offset.Zero,
        size = canvasSize,
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawArabesqueFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    isPreview: Boolean = false
) {
    val patternColor = Color(0xFF2D5016)
    val accentColor = Color(0xFF8FBC8F)
    val strokeWidth = if (isPreview) 1.dp.toPx() else 3.dp.toPx()
    
    // Draw arabesque patterns along borders
    val patternSpacing = if (isPreview) 8.dp.toPx() else 20.dp.toPx()
    
    // Top border pattern
    var x = patternSpacing
    while (x < canvasSize.width - patternSpacing) {
        drawArabesqueElement(Offset(x, patternSpacing), patternSpacing * 0.5f, patternColor, strokeWidth)
        x += patternSpacing
    }
    
    // Bottom border pattern
    x = patternSpacing
    while (x < canvasSize.width - patternSpacing) {
        drawArabesqueElement(
            Offset(x, canvasSize.height - patternSpacing), 
            patternSpacing * 0.5f, 
            patternColor, 
            strokeWidth
        )
        x += patternSpacing
    }
    
    if (!isPreview) {
        // Side patterns
        var y = patternSpacing * 2
        while (y < canvasSize.height - patternSpacing * 2) {
            // Left side
            drawArabesqueElement(Offset(patternSpacing, y), patternSpacing * 0.4f, patternColor, strokeWidth)
            // Right side
            drawArabesqueElement(
                Offset(canvasSize.width - patternSpacing, y), 
                patternSpacing * 0.4f, 
                patternColor, 
                strokeWidth
            )
            y += patternSpacing
        }
    }
}

private fun DrawScope.drawBismillahFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    isPreview: Boolean = false
) {
    val textColor = Color(0xFF1A365D)
    val borderColor = Color(0xFF4299E1)
    val frameWidth = if (isPreview) 4.dp.toPx() else 25.dp.toPx()
    
    // Main border
    drawRect(
        color = borderColor,
        topLeft = Offset.Zero,
        size = canvasSize,
        style = Stroke(width = 3.dp.toPx())
    )
    
    // Bismillah area at top
    val bismillahHeight = frameWidth
    drawRect(
        color = Color.White.copy(alpha = 0.95f),
        topLeft = Offset(0f, 0f),
        size = Size(canvasSize.width, bismillahHeight)
    )
    
    if (!isPreview) {
        // Simulated Bismillah calligraphy
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = (frameWidth * 0.6f)
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            canvas.nativeCanvas.drawText(
                "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                canvasSize.width / 2,
                bismillahHeight * 0.7f,
                paint
            )
        }
        
        // Decorative corner elements
        drawIslamicCornerDecorations(canvasSize, borderColor, frameWidth)
    }
}

private fun DrawScope.drawMemorialVersesFrame(
    canvasSize: Size,
    density: androidx.compose.ui.unit.Density,
    deceasedName: String,
    isPreview: Boolean = false
) {
    val verseColor = Color(0xFF2C5530)
    val backgroundTint = Color(0xFFF0F8F0)
    val frameWidth = if (isPreview) 4.dp.toPx() else 30.dp.toPx()
    
    // Background tint
    drawRect(
        color = backgroundTint.copy(alpha = 0.3f),
        topLeft = Offset.Zero,
        size = canvasSize
    )
    
    // Verse areas
    val verseHeight = frameWidth * 0.8f
    
    // Top verse area
    drawRect(
        color = Color.White.copy(alpha = 0.9f),
        topLeft = Offset(0f, 0f),
        size = Size(canvasSize.width, verseHeight)
    )
    
    // Bottom verse area
    drawRect(
        color = Color.White.copy(alpha = 0.9f),
        topLeft = Offset(0f, canvasSize.height - verseHeight),
        size = Size(canvasSize.width, verseHeight)
    )
    
    if (!isPreview) {
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = verseColor.toArgb()
                textSize = verseHeight * 0.4f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            
            // Top verse (Inna Lillahi)
            canvas.nativeCanvas.drawText(
                "إِنَّا لِلّهِ وَإِنَّـا إِلَيْهِ رَاجِعُونَ",
                canvasSize.width / 2,
                verseHeight * 0.6f,
                paint
            )
            
            // Bottom verse (prayer for deceased)
            paint.textSize = verseHeight * 0.35f
            canvas.nativeCanvas.drawText(
                "رَحِمَهُ اللَّهُ وَأَسْكَنَهُ فِي الْجَنَّةِ",
                canvasSize.width / 2,
                canvasSize.height - verseHeight * 0.4f,
                paint
            )
        }
        
        // Decorative borders
        drawRect(
            color = verseColor,
            topLeft = Offset.Zero,
            size = canvasSize,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

// Helper drawing functions
private fun DrawScope.drawGeometricCorners(canvasSize: Size, color: Color, frameWidth: Float) {
    val cornerSize = frameWidth * 0.8f
    val positions = listOf(
        Offset(frameWidth, frameWidth),
        Offset(canvasSize.width - frameWidth - cornerSize, frameWidth),
        Offset(frameWidth, canvasSize.height - frameWidth - cornerSize),
        Offset(canvasSize.width - frameWidth - cornerSize, canvasSize.height - frameWidth - cornerSize)
    )
    
    positions.forEach { position ->
        drawRect(
            color = color,
            topLeft = position,
            size = Size(cornerSize, cornerSize),
            style = Stroke(width = 1.dp.toPx())
        )
        
        // Inner pattern
        val innerSize = cornerSize * 0.5f
        val innerOffset = Offset(
            position.x + (cornerSize - innerSize) / 2,
            position.y + (cornerSize - innerSize) / 2
        )
        drawRect(
            color = color.copy(alpha = 0.7f),
            topLeft = innerOffset,
            size = Size(innerSize, innerSize),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun DrawScope.drawIslamicStars(canvasSize: Size, color: Color, isPreview: Boolean) {
    val starSize = if (isPreview) 4.dp.toPx() else 10.dp.toPx()
    val positions = if (isPreview) {
        listOf(Offset(canvasSize.width / 2, canvasSize.height / 2))
    } else {
        listOf(
            Offset(canvasSize.width * 0.25f, canvasSize.height * 0.25f),
            Offset(canvasSize.width * 0.75f, canvasSize.height * 0.25f),
            Offset(canvasSize.width * 0.25f, canvasSize.height * 0.75f),
            Offset(canvasSize.width * 0.75f, canvasSize.height * 0.75f)
        )
    }
    
    positions.forEach { center ->
        drawIslamicStar(center, starSize, color)
    }
}

private fun DrawScope.drawIslamicStar(center: Offset, size: Float, color: Color) {
    val path = Path()
    val outerRadius = size
    val innerRadius = size * 0.4f
    val points = 8
    
    for (i in 0 until points * 2) {
        val angle = (i * PI / points).toFloat()
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + cos(angle) * radius
        val y = center.y + sin(angle) * radius
        
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawCrescent(center: Offset, size: Float, color: Color) {
    val outerRadius = size
    val innerRadius = size * 0.7f
    val offset = size * 0.2f
    
    // Draw outer circle
    drawCircle(
        color = color,
        radius = outerRadius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw inner circle to create crescent shape
    drawCircle(
        color = Color.White,
        radius = innerRadius,
        center = center + Offset(offset, 0f)
    )
}

private fun DrawScope.drawArabesqueElement(center: Offset, size: Float, color: Color, strokeWidth: Float) {
    val path = Path()
    
    // Create a simple arabesque pattern
    path.moveTo(center.x - size, center.y)
    path.quadraticBezierTo(center.x, center.y - size, center.x + size, center.y)
    path.quadraticBezierTo(center.x, center.y + size, center.x - size, center.y)
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

private fun createArchPath(center: Offset, width: Float, height: Float): Path {
    val path = Path()
    val radius = width / 2
    
    path.moveTo(center.x - radius, center.y + height)
    path.arcTo(
        rect = androidx.compose.ui.geometry.Rect(
            offset = Offset(center.x - radius, center.y),
            size = Size(width, height * 2)
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
    )
    
    return path
}

private fun DrawScope.drawMosqueDecorations(canvasSize: Size, color: Color) {
    // Simple geometric decorations for mosque style
    val decorSize = 8.dp.toPx()
    val spacing = decorSize * 2
    
    var y = canvasSize.height * 0.2f
    while (y < canvasSize.height * 0.8f) {
        // Left decorations
        drawRect(
            color = color,
            topLeft = Offset(spacing, y),
            size = Size(decorSize, decorSize),
            style = Stroke(width = 1.dp.toPx())
        )
        
        // Right decorations
        drawRect(
            color = color,
            topLeft = Offset(canvasSize.width - spacing - decorSize, y),
            size = Size(decorSize, decorSize),
            style = Stroke(width = 1.dp.toPx())
        )
        
        y += spacing
    }
}

private fun DrawScope.drawCalligraphyPatterns(canvasSize: Size, color: Color, frameWidth: Float) {
    // Simplified calligraphy pattern simulation
    val patternHeight = frameWidth * 0.6f
    val patternWidth = frameWidth * 0.8f
    
    // Top and bottom borders
    var x = patternWidth
    while (x < canvasSize.width - patternWidth) {
        // Top pattern
        drawOval(
            color = color.copy(alpha = 0.6f),
            topLeft = Offset(x, (frameWidth - patternHeight) / 2),
            size = Size(patternWidth, patternHeight)
        )
        
        // Bottom pattern
        drawOval(
            color = color.copy(alpha = 0.6f),
            topLeft = Offset(x, canvasSize.height - frameWidth + (frameWidth - patternHeight) / 2),
            size = Size(patternWidth, patternHeight)
        )
        
        x += patternWidth * 1.5f
    }
}

private fun DrawScope.drawIslamicCornerDecorations(canvasSize: Size, color: Color, frameWidth: Float) {
    val decorSize = frameWidth * 0.4f
    val corners = listOf(
        Offset(frameWidth, frameWidth),
        Offset(canvasSize.width - frameWidth - decorSize, frameWidth),
        Offset(frameWidth, canvasSize.height - frameWidth - decorSize),
        Offset(canvasSize.width - frameWidth - decorSize, canvasSize.height - frameWidth - decorSize)
    )
    
    corners.forEach { corner ->
        drawCircle(
            color = color.copy(alpha = 0.7f),
            radius = decorSize / 2,
            center = Offset(corner.x + decorSize / 2, corner.y + decorSize / 2),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}