package com.app_muslim.surah_yasin.feature.memorial.ui.frames

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.memorial.model.IslamicFrameStyle

@Composable
fun IslamicFrameSelector(
    selectedFrame: IslamicFrameStyle,
    onFrameSelected: (IslamicFrameStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Islamic Frame Styles",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(IslamicFrameStyle.values()) { frameStyle ->
                FrameStyleItem(
                    frameStyle = frameStyle,
                    isSelected = selectedFrame == frameStyle,
                    onSelected = { onFrameSelected(frameStyle) }
                )
            }
        }
    }
}

@Composable
private fun FrameStyleItem(
    frameStyle: IslamicFrameStyle,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable { onSelected() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frame preview
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            // Frame preview overlay
            IslamicFrameOverlay(
                frameStyle = frameStyle,
                modifier = Modifier.fillMaxSize()
            )
            
            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Frame name
        Text(
            text = frameStyle.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}