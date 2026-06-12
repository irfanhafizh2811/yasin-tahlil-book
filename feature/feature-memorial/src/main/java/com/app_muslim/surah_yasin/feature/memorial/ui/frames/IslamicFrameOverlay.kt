package com.app_muslim.surah_yasin.feature.memorial.ui.frames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.app_muslim.surah_yasin.feature.memorial.model.IslamicFrameStyle
import com.app_muslim.surah_yasin.feature.memorial.model.FrameComplexity
import kotlin.math.*

@Composable
fun IslamicFrameOverlay(
    frameStyle: IslamicFrameStyle,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(frameStyle.colorScheme.primary)
    val secondaryColor = Color(frameStyle.colorScheme.secondary)
    val accentColor = Color(frameStyle.colorScheme.accent)
    
    if (frameStyle == IslamicFrameStyle.NONE) return
    
    Canvas(modifier = modifier.fillMaxSize()) {
        when (frameStyle) {
            IslamicFrameStyle.CLASSIC_GOLD -> drawClassicGoldFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.GEOMETRIC_SILVER -> drawGeometricFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.CALLIGRAPHY_BORDER -> drawCalligraphyFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.MOSQUE_ARCH -> drawMosqueArchFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.FLORAL_PATTERN -> drawFloralFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.MINIMALIST_MODERN -> drawMinimalistFrame(primaryColor, secondaryColor, accentColor)
            IslamicFrameStyle.ROYAL_ORNATE -> drawRoyalOrnateFrame(primaryColor, secondaryColor, accentColor)
            else -> {}
        }
    }
}

private fun DrawScope.drawClassicGoldFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.03f
    val inset = strokeWidth * 2
    
    // Outer border
    drawRect(
        color = primary,
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        style = Stroke(width = strokeWidth)
    )
    
    // Inner decorative border
    val innerInset = inset + strokeWidth * 1.5f
    drawRect(
        color = accent,
        topLeft = Offset(innerInset, innerInset),
        size = Size(size.width - innerInset * 2, size.height - innerInset * 2),
        style = Stroke(width = strokeWidth * 0.5f)
    )
    
    // Corner decorations
    drawCornerDecorations(primary, secondary, strokeWidth)
}

private fun DrawScope.drawGeometricFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.02f
    val patternSize = size.minDimension * 0.1f
    
    // Draw geometric pattern border
    for (i in 0 until (size.width / patternSize).toInt()) {
        for (j in 0 until (size.height / patternSize).toInt()) {
            val x = i * patternSize
            val y = j * patternSize
            
            if (x < patternSize || x > size.width - patternSize * 2 || 
                y < patternSize || y > size.height - patternSize * 2) {
                drawGeometricPattern(Offset(x, y), patternSize, primary, strokeWidth)
            }
        }
    }
}

private fun DrawScope.drawGeometricPattern(offset: Offset, patternSize: Float, color: Color, strokeWidth: Float) {
    val center = Offset(offset.x + patternSize / 2, offset.y + patternSize / 2)
    val radius = patternSize / 4
    
    // Draw star pattern
    val points = 8
    val path = Path()
    for (i in 0 until points * 2) {
        val angle = i * PI / points
        val r = if (i % 2 == 0) radius else radius * 0.6f
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    
    drawPath(path, color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawCalligraphyFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.015f
    val borderWidth = size.minDimension * 0.08f
    
    // Main border
    drawRect(
        color = primary,
        topLeft = Offset(borderWidth, borderWidth),
        size = Size(size.width - borderWidth * 2, size.height - borderWidth * 2),
        style = Stroke(width = strokeWidth * 2)
    )
    
    // Calligraphic decorations along the border
    drawCalligraphicElements(primary, secondary, borderWidth, strokeWidth)
}

private fun DrawScope.drawCalligraphicElements(primary: Color, secondary: Color, borderWidth: Float, strokeWidth: Float) {
    val segments = 6
    val segmentLength = size.width / segments
    
    for (i in 0 until segments) {
        val x = i * segmentLength + segmentLength / 2
        
        // Top border calligraphy
        drawCalligraphicSegment(Offset(x, borderWidth / 2), secondary, strokeWidth)
        
        // Bottom border calligraphy
        drawCalligraphicSegment(Offset(x, size.height - borderWidth / 2), secondary, strokeWidth)
    }
}

private fun DrawScope.drawCalligraphicSegment(center: Offset, color: Color, strokeWidth: Float) {
    val path = Path().apply {
        moveTo(center.x - 20, center.y)
        quadraticBezierTo(center.x, center.y - 15, center.x + 20, center.y)
        quadraticBezierTo(center.x + 10, center.y + 10, center.x, center.y)
        quadraticBezierTo(center.x - 10, center.y + 10, center.x - 20, center.y)
    }
    
    drawPath(path, color, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawMosqueArchFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.025f
    val archHeight = size.height * 0.15f
    val archWidth = size.width * 0.3f
    
    // Main rectangular border
    drawRect(
        color = primary,
        style = Stroke(width = strokeWidth)
    )
    
    // Top arch
    val archCenter = Offset(size.width / 2, 0f)
    val archRect = androidx.compose.ui.geometry.Rect(
        left = archCenter.x - archWidth / 2,
        top = -archHeight / 2,
        right = archCenter.x + archWidth / 2,
        bottom = archHeight / 2
    )
    
    drawArc(
        color = secondary,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = archRect.topLeft,
        size = archRect.size,
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawFloralFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.02f
    val petalSize = size.minDimension * 0.08f
    
    // Border
    drawRect(
        color = primary,
        style = Stroke(width = strokeWidth)
    )
    
    // Corner flowers
    val corners = listOf(
        Offset(petalSize, petalSize),
        Offset(size.width - petalSize, petalSize),
        Offset(petalSize, size.height - petalSize),
        Offset(size.width - petalSize, size.height - petalSize)
    )
    
    corners.forEach { corner ->
        drawFlower(corner, petalSize, secondary, strokeWidth)
    }
}

private fun DrawScope.drawFlower(center: Offset, size: Float, color: Color, strokeWidth: Float) {
    val petals = 6
    for (i in 0 until petals) {
        val angle = i * 2 * PI / petals
        val petalCenter = Offset(
            center.x + (size * 0.6f * cos(angle)).toFloat(),
            center.y + (size * 0.6f * sin(angle)).toFloat()
        )
        
        drawCircle(
            color = color,
            radius = size * 0.3f,
            center = petalCenter,
            style = Stroke(width = strokeWidth)
        )
    }
    
    // Center circle
    drawCircle(
        color = color,
        radius = size * 0.2f,
        center = center,
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawMinimalistFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.01f
    val inset = size.minDimension * 0.05f
    
    // Simple clean border
    drawRect(
        color = primary,
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        style = Stroke(width = strokeWidth)
    )
    
    // Subtle corner accents
    val cornerSize = inset * 0.5f
    listOf(
        Offset(inset, inset),
        Offset(size.width - inset - cornerSize, inset),
        Offset(inset, size.height - inset - cornerSize),
        Offset(size.width - inset - cornerSize, size.height - inset - cornerSize)
    ).forEach { corner: Offset ->
        drawRect(
            color = secondary,
            topLeft = corner,
            size = Size(cornerSize, cornerSize),
            style = Stroke(width = strokeWidth * 2)
        )
    }
}

private fun DrawScope.drawRoyalOrnateFrame(primary: Color, secondary: Color, accent: Color) {
    val strokeWidth = size.minDimension * 0.03f
    val ornamentSize = size.minDimension * 0.12f
    
    // Multiple border layers
    for (i in 0..2) {
        val inset = strokeWidth * (i + 1) * 2
        val color = listOf(primary, secondary, accent)[i]
        
        drawRect(
            color = color,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            style = Stroke(width = strokeWidth / (i + 1))
        )
    }
    
    // Elaborate corner ornaments
    listOf(
        Offset(ornamentSize, ornamentSize),
        Offset(size.width - ornamentSize, ornamentSize),
        Offset(ornamentSize, size.height - ornamentSize),
        Offset(size.width - ornamentSize, size.height - ornamentSize)
    ).forEach { corner: Offset ->
        drawOrnateCorner(corner, ornamentSize, primary, secondary, accent, strokeWidth)
    }
}

private fun DrawScope.drawOrnateCorner(center: Offset, size: Float, primary: Color, secondary: Color, accent: Color, strokeWidth: Float) {
    // Central diamond
    val path = Path().apply {
        moveTo(center.x, center.y - size / 2)
        lineTo(center.x + size / 2, center.y)
        lineTo(center.x, center.y + size / 2)
        lineTo(center.x - size / 2, center.y)
        close()
    }
    
    drawPath(path, primary, style = Stroke(width = strokeWidth))
    
    // Surrounding circles
    for (i in 0 until 4) {
        val angle = i * PI / 2
        val circleCenter = Offset(
            center.x + (size * 0.3f * cos(angle)).toFloat(),
            center.y + (size * 0.3f * sin(angle)).toFloat()
        )
        
        drawCircle(
            color = secondary,
            radius = size * 0.15f,
            center = circleCenter,
            style = Stroke(width = strokeWidth * 0.5f)
        )
    }
}

private fun DrawScope.drawCornerDecorations(primary: Color, secondary: Color, strokeWidth: Float) {
    val cornerSize = strokeWidth * 3
    val corners = listOf(
        Offset(cornerSize, cornerSize),
        Offset(size.width - cornerSize, cornerSize),
        Offset(cornerSize, size.height - cornerSize),
        Offset(size.width - cornerSize, size.height - cornerSize)
    )
    
    corners.forEach { corner ->
        drawCircle(
            color = secondary,
            radius = cornerSize,
            center = corner,
            style = Stroke(width = strokeWidth * 0.5f)
        )
    }
}