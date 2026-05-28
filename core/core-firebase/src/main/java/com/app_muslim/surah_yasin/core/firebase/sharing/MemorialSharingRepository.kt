package com.app_muslim.surah_yasin.core.firebase.sharing

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for Memorial Sharing System
 * Handles family invitations, social sharing, and access management
 */
interface MemorialSharingRepository {
    
    // Family Invitations
    suspend fun sendFamilyInvitation(
        memorialId: String,
        inviterName: String,
        inviteeEmail: String,
        inviteePhone: String? = null,
        personalMessage: String = "",
        permissions: MemorialSharingPermissions = MemorialSharingPermissions.VIEW_ONLY
    ): Result<String>
    
    suspend fun acceptInvitation(invitationId: String): Result<Unit>
    suspend fun declineInvitation(invitationId: String, reason: String? = null): Result<Unit>
    fun getUserInvitationsFlow(userId: String): Flow<List<MemorialInvitation>>
    fun getMemorialInvitationsFlow(memorialId: String): Flow<List<MemorialInvitation>>
    
    // Social Media Sharing
    suspend fun generateSharingLink(
        memorialId: String,
        sharingType: SharingType,
        expirationDays: Int = 7
    ): Result<String>
    
    fun createSocialSharingIntent(
        context: Context,
        memorialData: MemorialSharingData,
        platform: SharingPlatform
    ): Intent
    
    // Access Management
    suspend fun grantMemorialAccess(
        memorialId: String,
        userId: String,
        permissions: MemorialSharingPermissions
    ): Result<Unit>
    
    suspend fun revokeMemorialAccess(memorialId: String, userId: String): Result<Unit>
    suspend fun checkMemorialAccess(memorialId: String, userId: String): Result<MemorialSharingPermissions?>
    fun getMemorialAccessListFlow(memorialId: String): Flow<List<MemorialAccess>>
    
    // Analytics
    suspend fun trackSharingEvent(
        memorialId: String,
        platform: SharingPlatform,
        action: String
    ): Result<Unit>
    
    suspend fun getSharingAnalytics(memorialId: String): Result<MemorialSharingAnalytics>
    fun getSharingAnalyticsFlow(memorialId: String): Flow<MemorialSharingAnalytics>
    
    // Memorial Sharing Settings
    suspend fun updateSharingSettings(
        memorialId: String,
        settings: MemorialSharingSettings
    ): Result<Unit>
    
    suspend fun getSharingSettings(memorialId: String): Result<MemorialSharingSettings>
}

/**
 * Firebase implementation of Memorial Sharing Repository
 */
@Singleton
class MemorialSharingRepositoryImpl @Inject constructor(
    private val sharingService: MemorialSharingService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MemorialSharingRepository {
    
    override suspend fun sendFamilyInvitation(
        memorialId: String,
        inviterName: String,
        inviteeEmail: String,
        inviteePhone: String?,
        personalMessage: String,
        permissions: MemorialSharingPermissions
    ): Result<String> {
        return sharingService.inviteFamilyMember(
            memorialId = memorialId,
            inviterName = inviterName,
            inviteeEmail = inviteeEmail,
            inviteePhone = inviteePhone,
            personalMessage = personalMessage,
            permissions = permissions
        )
    }
    
    override suspend fun acceptInvitation(invitationId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Get invitation details
            val invitationDoc = firestore.collection("memorial_invitations")
                .document(invitationId)
                .get()
                .await()
            
            if (!invitationDoc.exists()) {
                return Result.failure(IllegalArgumentException("Invitation not found"))
            }
            
            val invitationData = invitationDoc.data!!
            val memorialId = invitationData["memorial_id"] as String
            val permissions = MemorialSharingPermissions.valueOf(
                invitationData["permissions"] as String
            )
            
            // Update invitation status
            firestore.collection("memorial_invitations")
                .document(invitationId)
                .update(mapOf(
                    "status" to InvitationStatus.ACCEPTED.name,
                    "accepted_at" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    "accepted_by_user_id" to userId
                ))
                .await()
            
            // Grant access to memorial
            sharingService.grantMemorialAccess(memorialId, userId, permissions)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun declineInvitation(invitationId: String, reason: String?): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val updates = mutableMapOf<String, Any>(
                "status" to InvitationStatus.DECLINED.name,
                "declined_at" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "declined_by_user_id" to userId
            )
            
            if (reason != null) {
                updates["decline_reason"] = reason
            }
            
            firestore.collection("memorial_invitations")
                .document(invitationId)
                .update(updates)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getUserInvitationsFlow(userId: String): Flow<List<MemorialInvitation>> {
        return firestore.collection("memorial_invitations")
            .whereEqualTo("invitee_email", userId) // Assuming userId can be email
            .whereEqualTo("status", InvitationStatus.PENDING.name)
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        MemorialInvitation(
                            id = doc.id,
                            memorialId = data["memorial_id"] as String,
                            inviterName = data["inviter_name"] as String,
                            inviteeEmail = data["invitee_email"] as String,
                            personalMessage = data["personal_message"] as? String ?: "",
                            permissions = MemorialSharingPermissions.valueOf(
                                data["permissions"] as String
                            ),
                            status = InvitationStatus.valueOf(data["status"] as String),
                            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(it.seconds),
                                    ZoneId.systemDefault()
                                )
                            } ?: ZonedDateTime.now(),
                            expiresAt = (data["expires_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(it.seconds),
                                    ZoneId.systemDefault()
                                )
                            }
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override fun getMemorialInvitationsFlow(memorialId: String): Flow<List<MemorialInvitation>> {
        return firestore.collection("memorial_invitations")
            .whereEqualTo("memorial_id", memorialId)
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        MemorialInvitation(
                            id = doc.id,
                            memorialId = data["memorial_id"] as String,
                            inviterName = data["inviter_name"] as String,
                            inviteeEmail = data["invitee_email"] as String,
                            personalMessage = data["personal_message"] as? String ?: "",
                            permissions = MemorialSharingPermissions.valueOf(
                                data["permissions"] as String
                            ),
                            status = InvitationStatus.valueOf(data["status"] as String),
                            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(it.seconds),
                                    ZoneId.systemDefault()
                                )
                            } ?: ZonedDateTime.now(),
                            expiresAt = (data["expires_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(it.seconds),
                                    ZoneId.systemDefault()
                                )
                            }
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override suspend fun generateSharingLink(
        memorialId: String,
        sharingType: SharingType,
        expirationDays: Int
    ): Result<String> {
        return sharingService.generateSharingLink(memorialId, sharingType, expirationDays)
    }
    
    override fun createSocialSharingIntent(
        context: Context,
        memorialData: MemorialSharingData,
        platform: SharingPlatform
    ): Intent {
        return sharingService.createSocialSharingIntent(context, memorialData, platform)
    }
    
    override suspend fun grantMemorialAccess(
        memorialId: String,
        userId: String,
        permissions: MemorialSharingPermissions
    ): Result<Unit> {
        return sharingService.grantMemorialAccess(memorialId, userId, permissions)
    }
    
    override suspend fun revokeMemorialAccess(memorialId: String, userId: String): Result<Unit> {
        return sharingService.revokeMemorialAccess(memorialId, userId)
    }
    
    override suspend fun checkMemorialAccess(
        memorialId: String,
        userId: String
    ): Result<MemorialSharingPermissions?> {
        return sharingService.checkMemorialAccess(memorialId, userId)
    }
    
    override fun getMemorialAccessListFlow(memorialId: String): Flow<List<MemorialAccess>> {
        return firestore.collection("memorial_access")
            .whereEqualTo("memorial_id", memorialId)
            .whereEqualTo("is_active", true)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        MemorialAccess(
                            id = doc.id,
                            memorialId = data["memorial_id"] as String,
                            userId = data["user_id"] as String,
                            permissions = MemorialSharingPermissions.valueOf(
                                data["permissions"] as String
                            ),
                            grantedAt = (data["granted_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(it.seconds),
                                    ZoneId.systemDefault()
                                )
                            } ?: ZonedDateTime.now(),
                            grantedBy = data["granted_by"] as? String,
                            accessType = data["access_type"] as? String ?: "unknown"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    override suspend fun trackSharingEvent(
        memorialId: String,
        platform: SharingPlatform,
        action: String
    ): Result<Unit> {
        return try {
            val eventData = mapOf(
                "memorial_id" to memorialId,
                "platform" to platform.name,
                "action" to action,
                "user_id" to (auth.currentUser?.uid ?: "anonymous"),
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            
            firestore.collection("sharing_events")
                .add(eventData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSharingAnalytics(memorialId: String): Result<MemorialSharingAnalytics> {
        return sharingService.getMemorialSharingAnalytics(memorialId)
    }
    
    override fun getSharingAnalyticsFlow(memorialId: String): Flow<MemorialSharingAnalytics> {
        return firestore.collection("memorial_sharing_stats")
            .document(memorialId)
            .snapshots()
            .map { documentSnapshot ->
                val data = documentSnapshot.data ?: emptyMap()
                MemorialSharingAnalytics(
                    memorialId = memorialId,
                    totalShares = (data["total_shares"] as? Long) ?: 0L,
                    familyInvitationsSent = (data["family_invitations_sent"] as? Long) ?: 0L,
                    socialMediaShares = (data["social_media_shares"] as? Long) ?: 0L,
                    linkClicks = (data["link_clicks"] as? Long) ?: 0L,
                    accessesGranted = (data["accesses_granted"] as? Long) ?: 0L,
                    lastSharedAt = data["last_shared_at"] as? com.google.firebase.Timestamp,
                    mostPopularPlatform = data["most_popular_platform"] as? String ?: "unknown"
                )
            }
            .catch { e ->
                emit(MemorialSharingAnalytics(
                    memorialId = memorialId,
                    totalShares = 0L,
                    familyInvitationsSent = 0L,
                    socialMediaShares = 0L,
                    linkClicks = 0L,
                    accessesGranted = 0L,
                    lastSharedAt = null,
                    mostPopularPlatform = "unknown"
                ))
            }
    }
    
    override suspend fun updateSharingSettings(
        memorialId: String,
        settings: MemorialSharingSettings
    ): Result<Unit> {
        return try {
            val settingsData = mapOf(
                "memorial_id" to memorialId,
                "allow_family_sharing" to settings.allowFamilySharing,
                "allow_social_media_sharing" to settings.allowSocialMediaSharing,
                "require_approval_for_access" to settings.requireApprovalForAccess,
                "link_expiration_days" to settings.linkExpirationDays,
                "max_simultaneous_access" to settings.maxSimultaneousAccess,
                "sharing_message_template" to settings.sharingMessageTemplate,
                "updated_at" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            
            firestore.collection("memorial_sharing_settings")
                .document(memorialId)
                .set(settingsData, com.google.firebase.firestore.SetOptions.merge())
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSharingSettings(memorialId: String): Result<MemorialSharingSettings> {
        return try {
            val doc = firestore.collection("memorial_sharing_settings")
                .document(memorialId)
                .get()
                .await()
            
            val data = doc.data
            val settings = if (data != null) {
                MemorialSharingSettings(
                    allowFamilySharing = data["allow_family_sharing"] as? Boolean ?: true,
                    allowSocialMediaSharing = data["allow_social_media_sharing"] as? Boolean ?: true,
                    requireApprovalForAccess = data["require_approval_for_access"] as? Boolean ?: false,
                    linkExpirationDays = (data["link_expiration_days"] as? Long)?.toInt() ?: 7,
                    maxSimultaneousAccess = (data["max_simultaneous_access"] as? Long)?.toInt() ?: 10,
                    sharingMessageTemplate = data["sharing_message_template"] as? String ?: ""
                )
            } else {
                // Default settings
                MemorialSharingSettings()
            }
            
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Additional data models

data class MemorialInvitation(
    val id: String,
    val memorialId: String,
    val inviterName: String,
    val inviteeEmail: String,
    val personalMessage: String,
    val permissions: MemorialSharingPermissions,
    val status: InvitationStatus,
    val createdAt: ZonedDateTime,
    val expiresAt: ZonedDateTime? = null
)

data class MemorialAccess(
    val id: String,
    val memorialId: String,
    val userId: String,
    val permissions: MemorialSharingPermissions,
    val grantedAt: ZonedDateTime,
    val grantedBy: String?,
    val accessType: String
)

data class MemorialSharingSettings(
    val allowFamilySharing: Boolean = true,
    val allowSocialMediaSharing: Boolean = true,
    val requireApprovalForAccess: Boolean = false,
    val linkExpirationDays: Int = 7,
    val maxSimultaneousAccess: Int = 10,
    val sharingMessageTemplate: String = ""
)