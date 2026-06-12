package com.app_muslim.surah_yasin.feature.memorial.ui.list.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.memorial.model.MemorialData
import com.app_muslim.surah_yasin.feature.memorial.model.PrayerType
import com.app_muslim.surah_yasin.feature.memorial.ui.sharing.MemorialSharingNavigation
import com.app_muslim.surah_yasin.feature.memorial.ui.sharing.SocialSharingScreen
import com.app_muslim.surah_yasin.feature.memorial.ui.sharing.FamilyInvitationScreen
import java.util.*

/**
 * Enhanced Memorial Sharing Integration
 * Integrates the new sharing system with existing memorial list functionality
 */

@Composable
fun EnhancedMemorialSharingDialog(
    memorial: MemorialData,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Share Memorial",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Memorial Info Preview
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        
                        Text(
                            text = memorial.prayerType.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Sharing Options
                Text(
                    text = "Choose sharing option:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                // Quick Social Share Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickShareButton(
                        icon = Icons.Default.ChatBubble,
                        label = "WhatsApp",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = {
                            shareToWhatsAppQuick(context, memorial)
                            onDismiss()
                        }
                    )
                    
                    QuickShareButton(
                        icon = Icons.Default.Email,
                        label = "Email",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            shareViaEmailQuick(context, memorial)
                            onDismiss()
                        }
                    )
                    
                    QuickShareButton(
                        icon = Icons.Default.ContentCopy,
                        label = "Copy Link",
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            // TODO: Generate and copy sharing link
                            onDismiss()
                        }
                    )
                }
                
                HorizontalDivider()
                
                // Advanced Sharing Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SharingOptionRow(
                        icon = Icons.Default.FamilyRestroom,
                        title = "Family Invitations",
                        subtitle = "Invite family members to join prayers",
                        onClick = {
                            MemorialSharingNavigation.navigateToFamilyInvitation(
                                navController = navController,
                                memorialId = memorial.id,
                                memorialName = memorial.deceasedName
                            )
                            onDismiss()
                        }
                    )
                    
                    SharingOptionRow(
                        icon = Icons.Default.Public,
                        title = "Social Media",
                        subtitle = "Share to multiple social platforms",
                        onClick = {
                            MemorialSharingNavigation.navigateToSocialSharing(
                                navController = navController,
                                memorialId = memorial.id,
                                memorialName = memorial.deceasedName,
                                deceasedName = memorial.deceasedName
                            )
                            onDismiss()
                        }
                    )
                    
                    SharingOptionRow(
                        icon = Icons.Default.Security,
                        title = "Privacy Settings",
                        subtitle = "Manage sharing permissions and privacy",
                        onClick = {
                            // Navigate to privacy controls
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun QuickShareButton(
    icon: ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun SharingOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

// Quick sharing functions

private fun shareToWhatsAppQuick(context: Context, memorial: MemorialData) {
    try {
        val message = """
            السلام عليكم ورحمة الله وبركاته
            
            Please join me in praying for ${memorial.deceasedName}.
            
            Let us recite ${memorial.prayerType.displayName} together and ask Allah to grant them Jannatul Firdaus.
            
            "وَمِنَ النَّاسِ مَن يَشْرِي نَفْسَهُ ابْتِغَاءَ مَرْضَاتِ اللَّهِ"
            
            May Allah reward us for our prayers. Ameen.
        """.trimIndent()
        
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(android.content.Intent.EXTRA_TEXT, message)
        }
        
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to generic share
        shareGeneric(context, memorial)
    }
}

private fun shareViaEmailQuick(context: Context, memorial: MemorialData) {
    try {
        val subject = "Memorial prayers invitation for ${memorial.deceasedName}"
        val message = """
            السلام عليكم ورحمة الله وبركاته
            
            Dear family and friends,
            
            I hope this message finds you in good health and strong faith. I am reaching out to invite you to join me in memorial prayers for our beloved ${memorial.deceasedName}.
            
            As we remember their beautiful soul and the impact they had on our lives, I believe that coming together in prayer will bring us comfort and honor their memory in the way they deserve.
            
            We will be reciting ${memorial.prayerType.displayName} and making du'a for their forgiveness and peaceful rest in Jannatul Firdaus.
            
            May Allah grant our beloved ${memorial.deceasedName} the highest place in Paradise, and may our prayers reach them as a source of light and mercy.
            
            Barakallahu feekum,
            Family Member
        """.trimIndent()
        
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:")
            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            putExtra(android.content.Intent.EXTRA_TEXT, message)
        }
        
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to generic share
        shareGeneric(context, memorial)
    }
}

private fun shareGeneric(context: Context, memorial: MemorialData) {
    val message = """
        Join me in memorial prayers for ${memorial.deceasedName}
        
        Together we can pray ${memorial.prayerType.displayName} and ask Allah for their forgiveness and mercy.
        
        May Allah grant them Jannatul Firdaus. Ameen.
    """.trimIndent()
    
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, message)
    }
    
    context.startActivity(android.content.Intent.createChooser(intent, "Share Memorial Prayer Invitation"))
}

// Preview Parameter Provider for different sharing scenarios
class MemorialSharingPreviewProvider : PreviewParameterProvider<MemorialData> {
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
            memorialMessage = "A devoted mother and grandmother who taught us faith and kindness.",
            prayerType = PrayerType.TAHLIL,
            prayerCount = 892,
            participantCount = 156,
            photoUrl = "https://via.placeholder.com/200x200/2196F3/FFFFFF?text=Fatima",
            createdAt = Date()
        ),
        MemorialData(
            id = "3",
            deceasedName = "Mohammad Abdullah",
            deceasedNameArabic = "محمد عبد الله",
            dateOfDeath = Calendar.getInstance().apply { 
                set(2022, Calendar.AUGUST, 5) 
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

@Preview(name = "Enhanced Sharing Dialog - Simple Name", showBackground = true)
@Composable
private fun EnhancedMemorialSharingDialogSimplePreview() {
    TahlilTheme {
        EnhancedMemorialSharingDialog(
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
            onDismiss = { },
            navController = rememberNavController()
        )
    }
}

@Preview(name = "Enhanced Sharing Dialog - Arabic Name", showBackground = true)
@Composable
private fun EnhancedMemorialSharingDialogArabicPreview() {
    TahlilTheme {
        EnhancedMemorialSharingDialog(
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
            onDismiss = { },
            navController = rememberNavController()
        )
    }
}

@Preview(name = "Enhanced Sharing Dialog - Fatihah Prayer", showBackground = true)
@Composable
private fun EnhancedMemorialSharingDialogFatihahPreview() {
    TahlilTheme {
        EnhancedMemorialSharingDialog(
            memorial = MemorialData(
                id = "3",
                deceasedName = "Abdullah Al-Rashid",
                deceasedNameArabic = "عبد الله الراشد",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2022, Calendar.AUGUST, 5) 
                }.time,
                memorialMessage = "A wise scholar and community leader.",
                prayerType = PrayerType.FATIHAH,
                prayerCount = 2456,
                participantCount = 387,
                photoUrl = null,
                createdAt = Date(),
            ),
            onDismiss = { },
            navController = rememberNavController()
        )
    }
}

// Preview removed to reduce UI rendering overhead

@Preview(name = "Enhanced Sharing Dialog - Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EnhancedMemorialSharingDialogDarkPreview() {
    TahlilTheme {
        EnhancedMemorialSharingDialog(
            memorial = MemorialData(
                id = "5",
                deceasedName = "Aisha Bint Omar",
                deceasedNameArabic = "عائشة بنت عمر",
                dateOfDeath = Calendar.getInstance().apply { 
                    set(2024, Calendar.MARCH, 8) 
                }.time,
                memorialMessage = "In loving memory of our beloved mother.",
                prayerType = PrayerType.YASIN,
                prayerCount = 445,
                participantCount = 78,
                photoUrl = null,
                createdAt = Date(),
            ),
            onDismiss = { },
            navController = rememberNavController()
        )
    }
}

// Component previews removed to reduce UI rendering overhead

// Sharing option previews removed to reduce UI rendering overhead

// Layout and dynamic previews removed to reduce UI rendering overhead and fix Choreographer issues