package com.app_muslim.surah_yasin.feature.memorial.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.memorial.model.ValidationError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialMessageSection(
    message: String,
    messageArabic: String,
    onMessageChange: (String) -> Unit,
    onMessageArabicChange: (String) -> Unit,
    errors: List<ValidationError>,
    modifier: Modifier = Modifier
) {
    val messageErrors = errors.filter { 
        it in listOf(
            ValidationError.MEMORIAL_MESSAGE_TOO_LONG,
            ValidationError.ARABIC_TEXT_INVALID,
            ValidationError.INAPPROPRIATE_CONTENT
        )
    }

    var showArabicInput by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Memorial Message",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { showArabicInput = !showArabicInput }
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showArabicInput) "Hide Arabic" else "Add Arabic",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            // Memorial Message in Local Language
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { 
                    Text("Memorial Message (Optional)")
                },
                placeholder = { 
                    Text("Write a heartfelt message remembering your loved one...")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                isError = messageErrors.any { 
                    it in listOf(
                        ValidationError.MEMORIAL_MESSAGE_TOO_LONG,
                        ValidationError.INAPPROPRIATE_CONTENT
                    )
                },
                supportingText = {
                    val messageError = messageErrors.firstOrNull { 
                        it in listOf(
                            ValidationError.MEMORIAL_MESSAGE_TOO_LONG,
                            ValidationError.INAPPROPRIATE_CONTENT
                        )
                    }
                    if (messageError != null) {
                        Text(
                            text = messageError.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = "${message.length}/1000 characters",
                            color = if (message.length > 800) 
                                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
            
            // Arabic Memorial Message (Optional)
            AnimatedVisibility(visible = showArabicInput) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    OutlinedTextField(
                        value = messageArabic,
                        onValueChange = onMessageArabicChange,
                        label = { 
                            Text("رسالة تأبين بالعربية (اختياري)")
                        },
                        placeholder = { 
                            Text("اكتب رسالة مؤثرة لتذكر الفقيد...")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 8,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Default
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Right
                        ),
                        isError = messageErrors.contains(ValidationError.ARABIC_TEXT_INVALID),
                        supportingText = {
                            if (messageErrors.contains(ValidationError.ARABIC_TEXT_INVALID)) {
                                Text(
                                    text = "النص العربي يحتوي على أحرف غير صالحة",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Right
                                )
                            } else {
                                Text(
                                    text = "${messageArabic.length}/1000 حرف",
                                    color = if (messageArabic.length > 800) 
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    )
                }
            }
            
            // Message Templates
            MessageTemplateSection(
                onTemplateSelected = { template ->
                    onMessageChange(template.english)
                    if (template.arabic.isNotEmpty()) {
                        onMessageArabicChange(template.arabic)
                        showArabicInput = true
                    }
                }
            )
            
            // Guidelines Card
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
                        text = "Islamic Guidelines",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Focus on the good deeds and character of the deceased\n" +
                                "• Avoid excessive praise or claims about their afterlife\n" +
                                "• Use respectful and dignified language\n" +
                                "• Include prayers for their soul and forgiveness",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageTemplateSection(
    onTemplateSelected: (MessageTemplate) -> Unit
) {
    var showTemplates by remember { mutableStateOf(false) }
    
    val messageTemplates = remember {
        listOf(
            MessageTemplate(
                title = "Basic Memorial",
                english = "We remember [Name] with love and pray for their peaceful rest. May Allah grant them Jannah and forgive their sins.",
                arabic = "نتذكر [الاسم] بالحب وندعو لهم بالراحة الأبدية. اللهم ارحمهم واغفر لهم ذنوبهم وأدخلهم الجنة."
            ),
            MessageTemplate(
                title = "Parent Memorial",
                english = "A loving parent who guided us with wisdom and kindness. May Allah reward them for their sacrifices and grant them paradise.",
                arabic = "والد محب وجهنا بالحكمة واللطف. اللهم اجزه خيراً عن تربيتنا وأدخله فسيح جناتك."
            ),
            MessageTemplate(
                title = "Grandparent Memorial",
                english = "A pillar of our family who blessed us with their presence. May their legacy of faith and love continue in our hearts.",
                arabic = "ركن من أركان أسرتنا باركنا بحضورهم. اللهم اجعل إرث إيمانهم ومحبتهم باقياً في قلوبنا."
            ),
            MessageTemplate(
                title = "Simple Prayer",
                english = "May Allah have mercy on their soul and grant them eternal peace.",
                arabic = "اللهم ارحم روحهم واغفر لهم وأنعم عليهم بالسكينة الأبدية."
            )
        )
    }

    Column {
        TextButton(
            onClick = { showTemplates = !showTemplates },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Use Message Template")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (showTemplates) "▲" else "▼",
                style = MaterialTheme.typography.bodySmall
            )
        }

        AnimatedVisibility(visible = showTemplates) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Message Templates",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    messageTemplates.forEach { template ->
                        OutlinedCard(
                            onClick = { onTemplateSelected(template) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = template.english,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                if (template.arabic.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                        Text(
                                            text = template.arabic,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Right
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class MessageTemplate(
    val title: String,
    val english: String,
    val arabic: String
)