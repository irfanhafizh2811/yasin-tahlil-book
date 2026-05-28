package com.app_muslim.surah_yasin.core.firebase.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.analytics.FirebaseAnalytics
// Firebase Dynamic Links is deprecated, using custom deep links instead
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase service for memorial sharing functionality
 * Handles family invitations, social media sharing, and access management
 */
@Singleton
class MemorialSharingService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val analytics: FirebaseAnalytics
) {
    
    /**
     * Create a family invitation for memorial sharing
     */
    suspend fun inviteFamilyMember(
        memorialId: String,
        inviterName: String,
        inviteeEmail: String,
        inviteePhone: String? = null,
        personalMessage: String = "",
        permissions: MemorialSharingPermissions = MemorialSharingPermissions.VIEW_ONLY
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Create invitation document
            val invitationId = firestore.collection("memorial_invitations").document().id
            
            val invitation = mapOf(
                "invitation_id" to invitationId,
                "memorial_id" to memorialId,
                "inviter_id" to userId,
                "inviter_name" to inviterName,
                "invitee_email" to inviteeEmail,
                "invitee_phone" to inviteePhone,
                "personal_message" to personalMessage,
                "permissions" to permissions.name,
                "status" to InvitationStatus.PENDING.name,
                "created_at" to FieldValue.serverTimestamp(),
                "expires_at" to FieldValue.serverTimestamp(), // 7 days from now
                "invitation_type" to InvitationType.FAMILY_MEMBER.name
            )
            
            // Save invitation
            firestore.collection("memorial_invitations")
                .document(invitationId)
                .set(invitation)
                .await()
            
            // Update memorial sharing stats
            updateMemorialSharingStats(memorialId, SharingAction.FAMILY_INVITATION_SENT)
            
            // Log analytics
            val analyticsBundle = android.os.Bundle().apply {
                putString("memorial_id", memorialId)
                putString("invitation_type", "family_member")
            }
            analytics.logEvent("memorial_family_invitation_sent", analyticsBundle)
            
            Result.success(invitationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate secure sharing link with Firebase Dynamic Links
     */
    suspend fun generateSharingLink(
        memorialId: String,
        sharingType: SharingType,
        expirationDays: Int = 7
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Create custom deep link (replace with your app's URL scheme)
            val deepLink = "https://tahlil.app/memorial/$memorialId?sharer=$userId&type=${sharingType.name}"
            
            // Generate a simple sharing URL (in production, use a URL shortener service)
            val shortLink = "https://tahlil.app/s/${generateShortId()}"
            
            // Save sharing link to Firestore for tracking
            val linkDocument = mapOf(
                "memorial_id" to memorialId,
                "sharer_id" to userId,
                "short_link" to shortLink,
                "deep_link" to deepLink,
                "sharing_type" to sharingType.name,
                "created_at" to FieldValue.serverTimestamp(),
                "expires_at" to FieldValue.serverTimestamp(), // Add expiration logic
                "click_count" to 0,
                "is_active" to true
            )
            
            firestore.collection("memorial_sharing_links")
                .add(linkDocument)
                .await()
            
            // Update sharing stats
            updateMemorialSharingStats(memorialId, SharingAction.LINK_GENERATED)
            
            // Log analytics
            val analyticsBundle = android.os.Bundle().apply {
                putString("memorial_id", memorialId)
                putString("sharing_type", sharingType.name)
            }
            analytics.logEvent("memorial_sharing_link_generated", analyticsBundle)
            
            Result.success(shortLink)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create Android sharing intent for social media
     */
    fun createSocialSharingIntent(
        context: Context,
        memorialData: MemorialSharingData,
        sharingPlatform: SharingPlatform
    ): Intent {
        val message = generateSharingMessage(memorialData, sharingPlatform)
        
        return when (sharingPlatform) {
            SharingPlatform.WHATSAPP -> createWhatsAppIntent(message)
            SharingPlatform.TELEGRAM -> createTelegramIntent(message)
            SharingPlatform.EMAIL -> createEmailIntent(memorialData, message)
            SharingPlatform.SMS -> createSMSIntent(message)
            SharingPlatform.GENERIC -> createGenericShareIntent(message)
            SharingPlatform.FACEBOOK -> createGenericShareIntent(message) // Facebook deprecated direct sharing
        }
    }
    
    /**
     * Grant memorial access to invited user
     */
    suspend fun grantMemorialAccess(
        memorialId: String,
        inviteeUserId: String,
        permissions: MemorialSharingPermissions
    ): Result<Unit> {
        return try {
            val accessData = mapOf(
                "memorial_id" to memorialId,
                "user_id" to inviteeUserId,
                "permissions" to permissions.name,
                "granted_at" to FieldValue.serverTimestamp(),
                "granted_by" to auth.currentUser?.uid,
                "is_active" to true,
                "access_type" to "invitation_accepted"
            )
            
            firestore.collection("memorial_access")
                .add(accessData)
                .await()
            
            // Update memorial sharing stats
            updateMemorialSharingStats(memorialId, SharingAction.ACCESS_GRANTED)
            
            // Log analytics
            val analyticsBundle = android.os.Bundle().apply {
                putString("memorial_id", memorialId)
                putString("permissions", permissions.name)
            }
            analytics.logEvent("memorial_access_granted", analyticsBundle)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if user has access to memorial
     */
    suspend fun checkMemorialAccess(
        memorialId: String,
        userId: String
    ): Result<MemorialSharingPermissions?> {
        return try {
            val accessQuery = firestore.collection("memorial_access")
                .whereEqualTo("memorial_id", memorialId)
                .whereEqualTo("user_id", userId)
                .whereEqualTo("is_active", true)
                .limit(1)
                .get()
                .await()
            
            if (accessQuery.documents.isNotEmpty()) {
                val accessDoc = accessQuery.documents.first()
                val permissions = accessDoc.getString("permissions")?.let {
                    MemorialSharingPermissions.valueOf(it)
                }
                Result.success(permissions)
            } else {
                Result.success(null) // No access
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get sharing analytics for memorial
     */
    suspend fun getMemorialSharingAnalytics(memorialId: String): Result<MemorialSharingAnalytics> {
        return try {
            val sharingStatsDoc = firestore.collection("memorial_sharing_stats")
                .document(memorialId)
                .get()
                .await()
            
            val data = sharingStatsDoc.data ?: emptyMap()
            
            val analytics = MemorialSharingAnalytics(
                memorialId = memorialId,
                totalShares = (data["total_shares"] as? Long) ?: 0L,
                familyInvitationsSent = (data["family_invitations_sent"] as? Long) ?: 0L,
                socialMediaShares = (data["social_media_shares"] as? Long) ?: 0L,
                linkClicks = (data["link_clicks"] as? Long) ?: 0L,
                accessesGranted = (data["accesses_granted"] as? Long) ?: 0L,
                lastSharedAt = data["last_shared_at"] as? com.google.firebase.Timestamp,
                mostPopularPlatform = data["most_popular_platform"] as? String ?: "unknown"
            )
            
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Revoke memorial access
     */
    suspend fun revokeMemorialAccess(
        memorialId: String,
        userId: String
    ): Result<Unit> {
        return try {
            val accessQuery = firestore.collection("memorial_access")
                .whereEqualTo("memorial_id", memorialId)
                .whereEqualTo("user_id", userId)
                .whereEqualTo("is_active", true)
                .get()
                .await()
            
            val batch = firestore.batch()
            accessQuery.documents.forEach { document ->
                batch.update(document.reference, "is_active", false)
                batch.update(document.reference, "revoked_at", FieldValue.serverTimestamp())
            }
            batch.commit().await()
            
            // Log analytics
            val analyticsBundle = android.os.Bundle().apply {
                putString("memorial_id", memorialId)
            }
            analytics.logEvent("memorial_access_revoked", analyticsBundle)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Private helper methods
    
    private suspend fun updateMemorialSharingStats(
        memorialId: String,
        action: SharingAction
    ) {
        try {
            val statsRef = firestore.collection("memorial_sharing_stats").document(memorialId)
            
            val updates = mutableMapOf<String, Any>(
                "last_shared_at" to FieldValue.serverTimestamp()
            )
            
            when (action) {
                SharingAction.FAMILY_INVITATION_SENT -> {
                    updates["family_invitations_sent"] = FieldValue.increment(1)
                    updates["total_shares"] = FieldValue.increment(1)
                }
                SharingAction.SOCIAL_MEDIA_SHARE -> {
                    updates["social_media_shares"] = FieldValue.increment(1)
                    updates["total_shares"] = FieldValue.increment(1)
                }
                SharingAction.LINK_GENERATED -> {
                    updates["links_generated"] = FieldValue.increment(1)
                }
                SharingAction.LINK_CLICKED -> {
                    updates["link_clicks"] = FieldValue.increment(1)
                }
                SharingAction.ACCESS_GRANTED -> {
                    updates["accesses_granted"] = FieldValue.increment(1)
                }
            }
            
            statsRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            // Log error but don't fail the main operation
        }
    }
    
    private fun generateSharingMessage(
        memorialData: MemorialSharingData,
        platform: SharingPlatform
    ): String {
        val deceasedName = memorialData.deceasedName
        val sharerName = memorialData.sharerName
        val prayerType = memorialData.prayerType
        val sharingLink = memorialData.sharingLink
        
        return when (platform) {
            SharingPlatform.WHATSAPP, SharingPlatform.TELEGRAM -> {
                """
                السلام عليكم ورحمة الله وبركاته
                
                I invite you to join me in memorial prayers for our beloved $deceasedName.
                
                Let us pray $prayerType together and ask Allah to grant them Jannatul Firdaus and forgive their sins.
                
                "وَمِنَ النَّاسِ مَن يَشْرِي نَفْسَهُ ابْتِغَاءَ مَرْضَاتِ اللَّهِ"
                
                Join prayers: $sharingLink
                
                May Allah reward us for our prayers.
                
                $sharerName
                """.trimIndent()
            }
            
            SharingPlatform.EMAIL -> {
                """
                السلام عليكم ورحمة الله وبركاته
                
                Dear family and friends,
                
                I hope this message finds you in good health and strong faith. I am reaching out to invite you to join me in memorial prayers for our beloved $deceasedName.
                
                As we remember their beautiful soul and the impact they had on our lives, I believe that coming together in prayer will bring us comfort and help honor their memory in the way they deserve.
                
                We will be reciting $prayerType and making du'a for their forgiveness and peaceful rest in Jannatul Firdaus.
                
                Please join us through this link: $sharingLink
                
                May Allah grant our beloved $deceasedName the highest place in Paradise, and may our prayers reach them as a source of light and mercy.
                
                Barakallahu feekum,
                $sharerName
                """.trimIndent()
            }
            
            SharingPlatform.SMS -> {
                "السلام عليكم. Please join me in memorial prayers for $deceasedName. Let's pray $prayerType together: $sharingLink - $sharerName"
            }
            
            else -> {
                """
                Join me in memorial prayers for $deceasedName
                
                Together we can pray $prayerType and ask Allah for their forgiveness and mercy.
                
                $sharingLink
                
                From: $sharerName
                """.trimIndent()
            }
        }
    }
    
    private fun createWhatsAppIntent(message: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        return intent
    }
    
    private fun createTelegramIntent(message: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("org.telegram.messenger")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        return intent
    }
    
    private fun createEmailIntent(memorialData: MemorialSharingData, message: String): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Memorial prayers invitation for ${memorialData.deceasedName}")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        return intent
    }
    
    private fun createSMSIntent(message: String): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:")
        intent.putExtra("sms_body", message)
        return intent
    }
    
    private fun createGenericShareIntent(message: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        return Intent.createChooser(intent, "Share Memorial Prayer Invitation")
    }
    
    private fun generateShortId(): String {
        return (1..8).map { ('a'..'z').random() }.joinToString("")
    }
}

// Data models for sharing system

data class MemorialSharingData(
    val memorialId: String,
    val deceasedName: String,
    val sharerName: String,
    val prayerType: String,
    val sharingLink: String,
    val memorialPhotoUrl: String? = null
)

data class MemorialSharingAnalytics(
    val memorialId: String,
    val totalShares: Long,
    val familyInvitationsSent: Long,
    val socialMediaShares: Long,
    val linkClicks: Long,
    val accessesGranted: Long,
    val lastSharedAt: com.google.firebase.Timestamp?,
    val mostPopularPlatform: String
)

enum class MemorialSharingPermissions {
    VIEW_ONLY,           // Can view memorial and prayer count
    PRAY_AND_VIEW,       // Can pray and view all activities
    FULL_ACCESS          // Can invite others and manage sharing (family admin)
}

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED
}

enum class InvitationType {
    FAMILY_MEMBER,
    FRIEND,
    COMMUNITY_MEMBER
}

enum class SharingType {
    FAMILY_PRIVATE,
    CLOSE_FRIENDS,
    COMMUNITY_OPEN,
    PUBLIC_MEMORIAL
}

enum class SharingPlatform {
    WHATSAPP,
    TELEGRAM,
    FACEBOOK,
    EMAIL,
    SMS,
    GENERIC
}

enum class SharingAction {
    FAMILY_INVITATION_SENT,
    SOCIAL_MEDIA_SHARE,
    LINK_GENERATED,
    LINK_CLICKED,
    ACCESS_GRANTED
}