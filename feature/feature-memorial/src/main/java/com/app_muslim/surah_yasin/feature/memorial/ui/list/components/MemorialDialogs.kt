package com.app_muslim.surah_yasin.feature.memorial.ui.list.components

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.memorial.model.MemorialData
import com.app_muslim.surah_yasin.feature.memorial.model.PrayerType
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.ShareType
import com.app_muslim.surah_yasin.core.firebase.sharing.SharingPlatform
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeleteMemorialDialog(
    memorial: MemorialData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Delete Memorial",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Are you sure you want to delete the memorial for:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = memorial.deceasedName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (memorial.deceasedNameArabic?.isNotEmpty() == true) {
                            Text(
                                text = memorial.deceasedNameArabic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Text(
                    text = "This action cannot be undone. All prayer data and participation records will be permanently deleted.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialShareBottomSheet(
    memorial: MemorialData,
    onShare: (ShareType, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var customMessage by remember { mutableStateOf("") }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Share Memorial",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Choose how you'd like to share this memorial",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
            
            HorizontalDivider()
            
            // Memorial preview
            MemorialSharePreview(memorial = memorial)
            
            HorizontalDivider()
            
            // Custom message input
            OutlinedTextField(
                value = customMessage,
                onValueChange = { customMessage = it },
                label = { Text("Add a personal message") },
                placeholder = { 
                    Text("Please remember ${memorial.deceasedName} in your prayers...") 
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                minLines = 2
            )
            
            // Share options
            Text(
                text = "Share Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ShareOptionCard(
                        icon = Icons.Default.Link,
                        title = "Share Link",
                        subtitle = "Generate shareable link",
                        onClick = { onShare(ShareType.LINK, customMessage) }
                    )
                }
                
                item {
                    ShareOptionCard(
                        icon = Icons.Default.TextSnippet,
                        title = "Share Text",
                        subtitle = "Copy formatted text",
                        onClick = { onShare(ShareType.TEXT, customMessage) }
                    )
                }
                
                item {
                    ShareOptionCard(
                        icon = Icons.Default.Image,
                        title = "Share Image",
                        subtitle = "Create memorial image",
                        onClick = { onShare(ShareType.IMAGE, customMessage) }
                    )
                }
                
                item {
                    ShareOptionCard(
                        icon = Icons.Default.Message,
                        title = "WhatsApp",
                        subtitle = "Share via WhatsApp",
                        onClick = { 
                            shareToWhatsApp(context, memorial, customMessage)
                            onDismiss()
                        }
                    )
                }
                
                item {
                    ShareOptionCard(
                        icon = Icons.Default.Facebook,
                        title = "Facebook",
                        subtitle = "Post to Facebook",
                        onClick = { onShare(ShareType.FACEBOOK, customMessage) }
                    )
                }
                
                item {
                    ShareOptionCard(
                        icon = Icons.Default.Email,
                        title = "Email",
                        subtitle = "Send via email",
                        onClick = { 
                            shareViaEmail(context, memorial, customMessage)
                            onDismiss()
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MemorialSharePreview(memorial: MemorialData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo
            if (memorial.photoUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(memorial.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Memorial Photo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Memorial info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = memorial.deceasedName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (memorial.deceasedNameArabic?.isNotEmpty() == true) {
                    Text(
                        text = memorial.deceasedNameArabic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = formatDateOfDeath(memorial.dateOfDeath),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for native sharing
private fun shareToWhatsApp(
    context: android.content.Context,
    memorial: MemorialData,
    customMessage: String
) {
    val shareText = buildMemorialShareText(memorial, customMessage)
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        `package` = "com.whatsapp"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // WhatsApp not installed, fallback to general share
        shareGeneral(context, shareText)
    }
}

private fun shareViaEmail(
    context: android.content.Context,
    memorial: MemorialData,
    customMessage: String
) {
    val shareText = buildMemorialShareText(memorial, customMessage)
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Memorial for ${memorial.deceasedName}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    
    context.startActivity(Intent.createChooser(intent, "Share via Email"))
}

private fun shareGeneral(context: android.content.Context, text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    
    context.startActivity(Intent.createChooser(intent, "Share Memorial"))
}

private fun buildMemorialShareText(memorial: MemorialData, customMessage: String): String {
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    
    return buildString {
        if (customMessage.isNotEmpty()) {
            append(customMessage)
            append("\n\n")
        }
        
        append("🤲 Memorial Prayer Request\n")
        append("━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")
        
        append("In memory of: ${memorial.deceasedName}")
        if (memorial.deceasedNameArabic?.isNotEmpty() == true) {
            append(" (${memorial.deceasedNameArabic})")
        }
        append("\n")
        
        append("Passed away: ${formatter.format(memorial.dateOfDeath)}\n\n")
        
        if (memorial.memorialMessage.isNotEmpty()) {
            append("Memorial Message:\n${memorial.memorialMessage}\n\n")
        }
        
        append("Prayer Type: ${memorial.prayerType.displayName}\n")
        append("Current Prayers: ${memorial.prayerCount}\n")
        append("Participants: ${memorial.participantCount}\n\n")
        
        append("Please remember ${memorial.deceasedName} in your prayers.\n")
        append("May Allah grant them peace and forgiveness. Ameen.\n\n")
        
        append("🕊️ Shared from Tahlil Memorial Platform")
    }
}

private fun formatDateOfDeath(date: Date): String {
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return "Passed away ${formatter.format(date)}"
}

// Preview Parameter Provider for different memorial states
class MemorialDialogsPreviewProvider : PreviewParameterProvider<MemorialData> {
    override val values = sequenceOf(
        MemorialData(
            id = "1",
            deceasedName = "Ahmed Hassan",
            deceasedNameArabic = null,
            dateOfDeath = Calendar.getInstance().apply { 
                set(2024, Calendar.JANUARY, 15) 
            }.time,
            memorialMessage = "A loving father who guided us with wisdom and kindness.",
            prayerType = PrayerType.YASIN,
            prayerCount = 127,
            participantCount = 45,
            photoUrl = null,
            createdAt = Date()
        ),
        MemorialData(
            id = "2",
            deceasedName = "Fatima Al-Zahra",
            deceasedNameArabic = "فاطمة الزهراء",
            dateOfDeath = Calendar.getInstance().apply { 
                set(2023, Calendar.DECEMBER, 20) 
            }.time,
            memorialMessage = "A devoted mother and grandmother who taught us the importance of faith, family, and kindness. Her warm smile and gentle wisdom will forever remain in our hearts. May Allah bless her soul and grant her the highest place in paradise.",
            prayerType = PrayerType.TAHLIL,
            prayerCount = 892,
            participantCount = 156,
            photoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
            createdAt = Date()
        ),
        MemorialData(
            id = "3",
            deceasedName = "Mohammad Abdullah Ibn Ahmad Al-Masri",
            deceasedNameArabic = "محمد عبد الله بن أحمد المصري",
            dateOfDeath = Calendar.getInstance().apply { 
                set(2020, Calendar.JUNE, 10) 
            }.time,
            memorialMessage = "",
            prayerType = PrayerType.FATIHAH,
            prayerCount = 2456,
            participantCount = 387,
            photoUrl = "https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=Mohammad",
            createdAt = Date()
        )
    )
}

@Preview(name = "Delete Dialog - Simple Name", showBackground = true)
@Composable
private fun DeleteMemorialDialogSimplePreview() {
    TahlilTheme {
        DeleteMemorialDialog(
            memorial = MemorialData(
                id = "1",
                deceasedName = "Ahmed Hassan",
                deceasedNameArabic = null,
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2024, Calendar.JANUARY, 15) 
                }.time,
                memorialMessage = "A loving father who guided us with wisdom.",
                prayerType = PrayerType.YASIN,
                prayerCount = 127,
                participantCount = 45,
                photoUrl = null,
                createdAt = Date(),
            ),
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview(name = "Delete Dialog - Arabic Name", showBackground = true)
@Composable
private fun DeleteMemorialDialogArabicPreview() {
    TahlilTheme {
        DeleteMemorialDialog(
            memorial = MemorialData(
                id = "2",
                deceasedName = "Fatima Al-Zahra",
                deceasedNameArabic = "فاطمة الزهراء",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2023, Calendar.DECEMBER, 20) 
                }.time,
                memorialMessage = "A devoted grandmother who taught us faith and kindness.",
                prayerType = PrayerType.TAHLIL,
                prayerCount = 892,
                participantCount = 156,
                photoUrl = null,
                createdAt = Date(),
            ),
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview(name = "Delete Dialog - Long Name", showBackground = true)
@Composable
private fun DeleteMemorialDialogLongNamePreview() {
    TahlilTheme {
        DeleteMemorialDialog(
            memorial = MemorialData(
                id = "3",
                deceasedName = "Mohammad Abdullah Ibn Ahmad Al-Masri Al-Qurashi",
                deceasedNameArabic = "محمد عبد الله بن أحمد المصري القرشي",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2020, Calendar.JUNE, 10) 
                }.time,
                memorialMessage = "",
                prayerType = PrayerType.FATIHAH,
                prayerCount = 2456,
                participantCount = 387,
                photoUrl = null,
                createdAt = Date(),
            ),
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview(name = "Delete Dialog - Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeleteMemorialDialogDarkPreview() {
    TahlilTheme {
        DeleteMemorialDialog(
            memorial = MemorialData(
                id = "2",
                deceasedName = "Abdullah Al-Rashid",
                deceasedNameArabic = "عبد الله الراشد",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2023, Calendar.NOVEMBER, 5) 
                }.time,
                memorialMessage = "In loving memory of a dear soul who touched many hearts.",
                prayerType = PrayerType.TAHLIL,
                prayerCount = 567,
                participantCount = 89,
                photoUrl = null,
                createdAt = Date(),
            ),
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview(name = "Share Bottom Sheet - No Photo", showBackground = true)
@Composable
private fun MemorialShareBottomSheetNoPhotoPreview() {
    TahlilTheme {
        MemorialShareBottomSheet(
            memorial = MemorialData(
                id = "1",
                deceasedName = "Ahmed Hassan",
                deceasedNameArabic = null,
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2024, Calendar.JANUARY, 15) 
                }.time,
                memorialMessage = "A loving father who guided us with wisdom and kindness.",
                prayerType = PrayerType.YASIN,
                prayerCount = 127,
                participantCount = 45,
                photoUrl = null,
                createdAt = Date(),
            ),
            onShare = { _, _ -> },
            onDismiss = { }
        )
    }
}

@Preview(name = "Share Bottom Sheet - With Photo", showBackground = true)
@Composable
private fun MemorialShareBottomSheetWithPhotoPreview() {
    TahlilTheme {
        MemorialShareBottomSheet(
            memorial = MemorialData(
                id = "2",
                deceasedName = "Fatima Al-Zahra",
                deceasedNameArabic = "فاطمة الزهراء",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2023, Calendar.DECEMBER, 20) 
                }.time,
                memorialMessage = "A devoted mother and grandmother who taught us the importance of faith.",
                prayerType = PrayerType.TAHLIL,
                prayerCount = 892,
                participantCount = 156,
                photoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
                createdAt = Date(),
            ),
            onShare = { _, _ -> },
            onDismiss = { }
        )
    }
}

@Preview(name = "Share Bottom Sheet - Long Content", showBackground = true)
@Composable
private fun MemorialShareBottomSheetLongContentPreview() {
    TahlilTheme {
        MemorialShareBottomSheet(
            memorial = MemorialData(
                id = "3",
                deceasedName = "Mohammad Abdullah Ibn Ahmad Al-Masri Al-Qurashi",
                deceasedNameArabic = "محمد عبد الله بن أحمد المصري القرشي",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2020, Calendar.JUNE, 10) 
                }.time,
                memorialMessage = "A wonderful grandfather who taught us the importance of faith, family, and kindness. His warm smile and gentle wisdom will forever remain in our hearts. May Allah bless his soul and grant him the highest place in paradise. We will continue to pray for him and follow the beautiful example he set for us throughout his life.",
                prayerType = PrayerType.FATIHAH,
                prayerCount = 2456,
                participantCount = 387,
                photoUrl = "https://via.placeholder.com/200x200/4CAF50/FFFFFF?text=Mohammad",
                createdAt = Date(),
            ),
            onShare = { _, _ -> },
            onDismiss = { }
        )
    }
}

@Preview(name = "Share Bottom Sheet - Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MemorialShareBottomSheetDarkPreview() {
    TahlilTheme {
        MemorialShareBottomSheet(
            memorial = MemorialData(
                id = "4",
                deceasedName = "Aisha Bint Omar",
                deceasedNameArabic = "عائشة بنت عمر",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2024, Calendar.MARCH, 8) 
                }.time,
                memorialMessage = "In loving memory of our beloved mother.",
                prayerType = PrayerType.TAHLIL,
                prayerCount = 345,
                participantCount = 78,
                photoUrl = "https://via.placeholder.com/200x200/FF9800/FFFFFF?text=Aisha",
                createdAt = Date(),
            ),
            onShare = { _, _ -> },
            onDismiss = { }
        )
    }
}

@Preview(name = "Dynamic Dialogs", showBackground = true)
@Composable
private fun MemorialDialogsDynamicPreview(
    @PreviewParameter(MemorialDialogsPreviewProvider::class) memorial: MemorialData
) {
    TahlilTheme {
        DeleteMemorialDialog(
            memorial = memorial,
            onConfirm = { },
            onDismiss = { }
        )
    }
}