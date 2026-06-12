package com.app_muslim.surah_yasin.feature.memorial.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.memorial.model.ValidationError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeceasedInformationSection(
    deceasedName: String,
    deceasedNameArabic: String,
    onDeceasedNameChange: (String) -> Unit,
    onDeceasedNameArabicChange: (String) -> Unit,
    errors: List<ValidationError>,
    modifier: Modifier = Modifier
) {
    val nameErrors = errors.filter { 
        it in listOf(
            ValidationError.DECEASED_NAME_EMPTY,
            ValidationError.DECEASED_NAME_TOO_SHORT,
            ValidationError.DECEASED_NAME_TOO_LONG,
            ValidationError.ARABIC_TEXT_INVALID
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deceased Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Name in Latin/Local Language
            OutlinedTextField(
                value = deceasedName,
                onValueChange = onDeceasedNameChange,
                label = { 
                    Text("Full Name *")
                },
                placeholder = { 
                    Text("Enter the full name of the deceased")
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = nameErrors.any { 
                    it in listOf(
                        ValidationError.DECEASED_NAME_EMPTY,
                        ValidationError.DECEASED_NAME_TOO_SHORT,
                        ValidationError.DECEASED_NAME_TOO_LONG
                    )
                },
                supportingText = {
                    val nameError = nameErrors.firstOrNull { 
                        it in listOf(
                            ValidationError.DECEASED_NAME_EMPTY,
                            ValidationError.DECEASED_NAME_TOO_SHORT,
                            ValidationError.DECEASED_NAME_TOO_LONG
                        )
                    }
                    if (nameError != null) {
                        Text(
                            text = nameError.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (deceasedName.isNotBlank()) {
                        Text(
                            text = "✓ Name entered",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
            
            // Name in Arabic (Optional)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                OutlinedTextField(
                    value = deceasedNameArabic,
                    onValueChange = onDeceasedNameArabicChange,
                    label = { 
                        Text("الاسم بالعربية (اختياري)")
                    },
                    placeholder = { 
                        Text("أدخل الاسم بالعربية إن أردت")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Right
                    ),
                    isError = nameErrors.contains(ValidationError.ARABIC_TEXT_INVALID),
                    supportingText = {
                        if (nameErrors.contains(ValidationError.ARABIC_TEXT_INVALID)) {
                            Text(
                                text = "Arabic text contains invalid characters",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Right
                            )
                        } else if (deceasedNameArabic.isNotBlank()) {
                            Text(
                                text = "✓ تم إدخال الاسم بالعربية",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Right
                            )
                        } else {
                            Text(
                                text = "Adding the Arabic name helps with prayers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Right
                            )
                        }
                    }
                )
            }
            
            // Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Islamic Tradition",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "In Islamic tradition, it is recommended to mention the deceased by their full name in prayers. Adding the Arabic name, if known, enhances the spiritual connection during recitation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// Preview Parameter Provider for different states
class DeceasedInfoPreviewProvider : PreviewParameterProvider<DeceasedInfoPreviewData> {
    override val values = sequenceOf(
        DeceasedInfoPreviewData(
            name = "",
            nameArabic = "",
            errors = listOf(ValidationError.DECEASED_NAME_EMPTY)
        ),
        DeceasedInfoPreviewData(
            name = "Ahmed Hassan",
            nameArabic = "",
            errors = emptyList()
        ),
        DeceasedInfoPreviewData(
            name = "Fatima Al-Zahra",
            nameArabic = "فاطمة الزهراء",
            errors = emptyList()
        ),
        DeceasedInfoPreviewData(
            name = "A",
            nameArabic = "",
            errors = listOf(ValidationError.DECEASED_NAME_TOO_SHORT)
        ),
        DeceasedInfoPreviewData(
            name = "Mohammad Abdullah Ibn Ahmad Al-Masri Al-Qurashi",
            nameArabic = "محمد عبد الله بن أحمد المصري القرشي",
            errors = listOf(ValidationError.DECEASED_NAME_TOO_LONG)
        )
    )
}

data class DeceasedInfoPreviewData(
    val name: String,
    val nameArabic: String,
    val errors: List<ValidationError>
)

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun DeceasedInformationSectionEmptyPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "",
            deceasedNameArabic = "",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = listOf(ValidationError.DECEASED_NAME_EMPTY)
        )
    }
}

@Preview(name = "With Latin Name", showBackground = true)
@Composable
private fun DeceasedInformationSectionLatinPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "Ahmed Hassan",
            deceasedNameArabic = "",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = emptyList()
        )
    }
}

@Preview(name = "With Arabic Name", showBackground = true)
@Composable
private fun DeceasedInformationSectionArabicPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "Fatima Al-Zahra",
            deceasedNameArabic = "فاطمة الزهراء",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = emptyList()
        )
    }
}

@Preview(name = "Name Too Short Error", showBackground = true)
@Composable
private fun DeceasedInformationSectionErrorPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "A",
            deceasedNameArabic = "",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = listOf(ValidationError.DECEASED_NAME_TOO_SHORT)
        )
    }
}

@Preview(name = "Name Too Long Error", showBackground = true)
@Composable
private fun DeceasedInformationSectionLongErrorPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "Mohammad Abdullah Ibn Ahmad Al-Masri Al-Qurashi Al-Hanafi",
            deceasedNameArabic = "محمد عبد الله بن أحمد المصري القرشي الحنفي",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = listOf(ValidationError.DECEASED_NAME_TOO_LONG)
        )
    }
}

@Preview(name = "Arabic Text Invalid", showBackground = true)
@Composable
private fun DeceasedInformationSectionArabicErrorPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "Ahmed Hassan",
            deceasedNameArabic = "Ahmed123!@#",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = listOf(ValidationError.ARABIC_TEXT_INVALID)
        )
    }
}

@Preview(name = "Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeceasedInformationSectionDarkPreview() {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = "Abdullah Al-Rashid",
            deceasedNameArabic = "عبد الله الراشد",
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = emptyList()
        )
    }
}

@Preview(name = "Dynamic Preview", showBackground = true)
@Composable
private fun DeceasedInformationSectionDynamicPreview(
    @PreviewParameter(DeceasedInfoPreviewProvider::class) data: DeceasedInfoPreviewData
) {
    TahlilTheme {
        DeceasedInformationSection(
            deceasedName = data.name,
            deceasedNameArabic = data.nameArabic,
            onDeceasedNameChange = { },
            onDeceasedNameArabicChange = { },
            errors = data.errors
        )
    }
}