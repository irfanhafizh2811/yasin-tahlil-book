package com.app_muslim.surah_yasin.core.ui.islamic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

/**
 * Text component with automatic RTL detection and BiDi support
 */
@Composable
fun BiDiText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    isArabic: Boolean? = null,
    forceRTL: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    // Detect Arabic characters automatically if not specified
    val containsArabic = isArabic ?: remember(text) {
        text.any { char ->
            char.code in 0x0600..0x06FF || // Arabic block
            char.code in 0x0750..0x077F || // Arabic Supplement
            char.code in 0x08A0..0x08FF || // Arabic Extended-A
            char.code in 0xFB50..0xFDFF || // Arabic Presentation Forms-A
            char.code in 0xFE70..0xFEFF    // Arabic Presentation Forms-B
        }
    }
    
    val shouldUseRTL = containsArabic || forceRTL
    val layoutDirection = if (shouldUseRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    val textDirection = if (shouldUseRTL) TextDirection.Rtl else TextDirection.Ltr
    val textAlign = if (shouldUseRTL) TextAlign.End else TextAlign.Start
    
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Text(
            text = text,
            modifier = modifier.semantics {
                contentDescription = if (shouldUseRTL) {
                    "Arabic text: $text"
                } else {
                    text
                }
            },
            style = style.copy(
                color = color,
                textDirection = textDirection,
                textAlign = textAlign
            ),
            maxLines = maxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

/**
 * RTL-aware container for Arabic content layout
 */
@Composable
fun RTLContainer(
    modifier: Modifier = Modifier,
    forceRTL: Boolean = true,
    reverseFlexDirection: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val layoutDirection = if (forceRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = modifier,
            content = content
        )
    }
}

/**
 * RTL-aware row layout for Arabic content
 */
@Composable
fun RTLRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    forceRTL: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val layoutDirection = if (forceRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
            content = content
        )
    }
}

/**
 * Mixed content display with proper BiDi handling
 */
@Composable
fun MixedContentText(
    arabicText: String?,
    latinText: String?,
    modifier: Modifier = Modifier,
    arabicStyle: TextStyle = IslamicTextStyles.ArabicPrayerMedium,
    latinStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    spacing: Dp = 8.dp,
    arabicFirst: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (arabicFirst) {
            if (arabicText != null) {
                BiDiText(
                    text = arabicText,
                    style = arabicStyle,
                    isArabic = true
                )
            }
            if (latinText != null) {
                BiDiText(
                    text = latinText,
                    style = latinStyle,
                    isArabic = false
                )
            }
        } else {
            if (latinText != null) {
                BiDiText(
                    text = latinText,
                    style = latinStyle,
                    isArabic = false
                )
            }
            if (arabicText != null) {
                BiDiText(
                    text = arabicText,
                    style = arabicStyle,
                    isArabic = true
                )
            }
        }
    }
}

/**
 * RTL-aware column layout for Arabic content
 */
@Composable
fun RTLColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    forceRTL: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val layoutDirection = if (forceRTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Column(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

/**
 * Intelligent text component that handles mixed RTL/LTR content
 */
@Composable
fun IntelligentText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE
) {
    // Analyze text to determine primary direction
    val arabicCharCount = text.count { char ->
        char.code in 0x0600..0x06FF || char.code in 0x0750..0x077F || char.code in 0x08A0..0x08FF
    }
    val latinCharCount = text.count { char ->
        char.code in 0x0020..0x007F || char.code in 0x00A0..0x00FF
    }
    
    val isPrimaryRTL = arabicCharCount > latinCharCount
    
    BiDiText(
        text = text,
        modifier = modifier,
        style = if (isPrimaryRTL) IslamicTextStyles.ArabicPrayerMedium else style,
        color = color,
        isArabic = isPrimaryRTL,
        maxLines = maxLines
    )
}

/**
 * Text component with explicit direction control
 */
@Composable
fun DirectionalText(
    text: String,
    direction: TextDirection,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    alignment: TextAlign = when (direction) {
        TextDirection.Rtl -> TextAlign.End
        TextDirection.Ltr -> TextAlign.Start
        else -> TextAlign.Start
    },
    maxLines: Int = Int.MAX_VALUE
) {
    val layoutDirection = when (direction) {
        TextDirection.Rtl -> LayoutDirection.Rtl
        TextDirection.Ltr -> LayoutDirection.Ltr
        else -> LayoutDirection.Ltr
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(
                color = color,
                textDirection = direction,
                textAlign = alignment
            ),
            maxLines = maxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

/**
 * Utility function to detect if text contains Arabic characters
 */
fun String.containsArabic(): Boolean {
    return any { char ->
        char.code in 0x0600..0x06FF || // Arabic block
        char.code in 0x0750..0x077F || // Arabic Supplement
        char.code in 0x08A0..0x08FF || // Arabic Extended-A
        char.code in 0xFB50..0xFDFF || // Arabic Presentation Forms-A
        char.code in 0xFE70..0xFEFF    // Arabic Presentation Forms-B
    }
}

/**
 * Utility function to get the dominant text direction
 */
fun String.getDominantDirection(): TextDirection {
    val arabicCharCount = count { char ->
        char.code in 0x0600..0x06FF || char.code in 0x0750..0x077F || char.code in 0x08A0..0x08FF
    }
    val latinCharCount = count { char ->
        char.code in 0x0020..0x007F || char.code in 0x00A0..0x00FF
    }
    
    return if (arabicCharCount > latinCharCount) TextDirection.Rtl else TextDirection.Ltr
}

// Preview Functions
@Preview(showBackground = true, name = "BiDi Text - Arabic")
@Composable
private fun PreviewBiDiTextArabic() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            BiDiText(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "BiDi Text - Latin")
@Composable
private fun PreviewBiDiTextLatin() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            BiDiText(
                text = "In the name of Allah, the Most Gracious, the Most Merciful",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "RTL Container")
@Composable
private fun PreviewRTLContainer() {
    TahlilTheme {
        RTLContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            forceRTL = true
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text("Arabic Content")
                Text("محتوى عربي")
                Text("More Arabic Text")
            }
        }
    }
}

@Preview(showBackground = true, name = "RTL Row")
@Composable
private fun PreviewRTLRow() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            RTLRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("العربية")
                Text("English")
                Text("مختلط")
            }
        }
    }
}

@Preview(showBackground = true, name = "Mixed Content Text - Arabic First")
@Composable
private fun PreviewMixedContentTextArabicFirst() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MixedContentText(
                arabicText = "اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ",
                latinText = "O Allah, forgive him and have mercy on him",
                modifier = Modifier.fillMaxWidth(),
                spacing = 12.dp,
                arabicFirst = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Mixed Content Text - Latin First")
@Composable
private fun PreviewMixedContentTextLatinFirst() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MixedContentText(
                arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                latinText = "Glory be to Allah and praise be to Him",
                modifier = Modifier.fillMaxWidth(),
                spacing = 8.dp,
                arabicFirst = false
            )
        }
    }
}

@Preview(showBackground = true, name = "RTL Column")
@Composable
private fun PreviewRTLColumn() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            RTLColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text("العنصر الأول")
                Text("العنصر الثاني")
                Text("العنصر الثالث")
            }
        }
    }
}

@Preview(showBackground = true, name = "Intelligent Text - Arabic Dominant")
@Composable
private fun PreviewIntelligentTextArabic() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            IntelligentText(
                text = "هذا نص عربي مع some English words",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, name = "Intelligent Text - Latin Dominant")
@Composable
private fun PreviewIntelligentTextLatin() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            IntelligentText(
                text = "This is English text with some عربي words",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, name = "Directional Text - RTL")
@Composable
private fun PreviewDirectionalTextRTL() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            DirectionalText(
                text = "نص موجه يمين إلى يسار",
                direction = TextDirection.Rtl,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true, name = "Directional Text - LTR")
@Composable
private fun PreviewDirectionalTextLTR() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            DirectionalText(
                text = "Left-to-right directed text",
                direction = TextDirection.Ltr,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}