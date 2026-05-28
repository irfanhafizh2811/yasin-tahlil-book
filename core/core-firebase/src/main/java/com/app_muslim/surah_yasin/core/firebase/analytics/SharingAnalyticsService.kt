package com.app_muslim.surah_yasin.core.firebase.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.app_muslim.surah_yasin.core.firebase.sharing.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Analytics service for Memorial Sharing System
 * Tracks sharing events, user engagement, and family invitation analytics
 */
@Singleton
class SharingAnalyticsService @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val auth: FirebaseAuth
) {
    
    companion object {
        // Custom event names
        const val EVENT_MEMORIAL_SHARED = "memorial_shared"
        const val EVENT_FAMILY_INVITATION_SENT = "family_invitation_sent"
        const val EVENT_FAMILY_INVITATION_ACCEPTED = "family_invitation_accepted"
        const val EVENT_SHARING_LINK_GENERATED = "sharing_link_generated"
        const val EVENT_SHARING_LINK_CLICKED = "sharing_link_clicked"
        const val EVENT_MEMORIAL_ACCESS_GRANTED = "memorial_access_granted"
        const val EVENT_MEMORIAL_ACCESS_REVOKED = "memorial_access_revoked"
        const val EVENT_SOCIAL_PLATFORM_SHARE = "social_platform_share"
        
        // Parameter names
        const val PARAM_MEMORIAL_ID = "memorial_id"
        const val PARAM_SHARING_PLATFORM = "sharing_platform"
        const val PARAM_SHARING_TYPE = "sharing_type"
        const val PARAM_INVITATION_TYPE = "invitation_type"
        const val PARAM_ACCESS_PERMISSIONS = "access_permissions"
        const val PARAM_INVITEE_RELATIONSHIP = "invitee_relationship"
        const val PARAM_LINK_EXPIRATION_DAYS = "link_expiration_days"
        const val PARAM_CUSTOM_MESSAGE_LENGTH = "custom_message_length"
    }
    
    /**
     * Track when a memorial is shared to social media
     */
    fun trackMemorialShared(
        memorialId: String,
        platform: SharingPlatform,
        sharingType: SharingType,
        customMessageLength: Int = 0,
        additionalParams: Map<String, Any> = emptyMap()
    ) {
        val params = mutableMapOf<String, Any>(
            PARAM_MEMORIAL_ID to memorialId,
            PARAM_SHARING_PLATFORM to platform.name,
            PARAM_SHARING_TYPE to sharingType.name,
            PARAM_CUSTOM_MESSAGE_LENGTH to customMessageLength,
            "user_id" to (auth.currentUser?.uid ?: "anonymous"),
            "timestamp" to System.currentTimeMillis()
        )
        
        params.putAll(additionalParams)
        
        firebaseAnalytics.logEvent(EVENT_MEMORIAL_SHARED) {
            params.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
    
    /**
     * Track when a family invitation is sent
     */
    fun trackFamilyInvitationSent(
        memorialId: String,
        invitationType: InvitationType,
        permissions: MemorialSharingPermissions,
        inviteeRelationship: String = "family_member",
        personalMessageLength: Int = 0
    ) {
        firebaseAnalytics.logEvent(EVENT_FAMILY_INVITATION_SENT) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param(PARAM_INVITATION_TYPE, invitationType.name)
            param(PARAM_ACCESS_PERMISSIONS, permissions.name)
            param(PARAM_INVITEE_RELATIONSHIP, inviteeRelationship)
            param(PARAM_CUSTOM_MESSAGE_LENGTH, personalMessageLength.toLong())
            param("user_id", auth.currentUser?.uid ?: "anonymous")
        }
    }
    
    /**
     * Track when a family invitation is accepted
     */
    fun trackFamilyInvitationAccepted(
        memorialId: String,
        invitationId: String,
        permissions: MemorialSharingPermissions,
        timeToAccept: Long // milliseconds from sent to accepted
    ) {
        firebaseAnalytics.logEvent(EVENT_FAMILY_INVITATION_ACCEPTED) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param("invitation_id", invitationId)
            param(PARAM_ACCESS_PERMISSIONS, permissions.name)
            param("time_to_accept_hours", (timeToAccept / (1000 * 60 * 60)).toDouble())
            param("user_id", auth.currentUser?.uid ?: "anonymous")
        }
    }
    
    /**
     * Track when a sharing link is generated
     */
    fun trackSharingLinkGenerated(
        memorialId: String,
        sharingType: SharingType,
        expirationDays: Int,
        linkId: String? = null
    ) {
        firebaseAnalytics.logEvent(EVENT_SHARING_LINK_GENERATED) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param(PARAM_SHARING_TYPE, sharingType.name)
            param(PARAM_LINK_EXPIRATION_DAYS, expirationDays.toLong())
            param("link_id", linkId ?: "unknown")
            param("user_id", auth.currentUser?.uid ?: "anonymous")
        }
    }
    
    /**
     * Track when a sharing link is clicked
     */
    fun trackSharingLinkClicked(
        memorialId: String,
        linkId: String,
        clickerUserId: String? = null,
        referrerPlatform: String = "unknown"
    ) {
        firebaseAnalytics.logEvent(EVENT_SHARING_LINK_CLICKED) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param("link_id", linkId)
            param("clicker_user_id", clickerUserId ?: "anonymous")
            param("referrer_platform", referrerPlatform)
            param("timestamp", System.currentTimeMillis().toDouble())
        }
    }
    
    /**
     * Track when memorial access is granted to a user
     */
    fun trackMemorialAccessGranted(
        memorialId: String,
        grantedToUserId: String,
        permissions: MemorialSharingPermissions,
        accessSource: String // "invitation", "family_add", "admin_grant"
    ) {
        firebaseAnalytics.logEvent(EVENT_MEMORIAL_ACCESS_GRANTED) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param("granted_to_user_id", grantedToUserId)
            param(PARAM_ACCESS_PERMISSIONS, permissions.name)
            param("access_source", accessSource)
            param("granted_by_user_id", auth.currentUser?.uid ?: "system")
        }
    }
    
    /**
     * Track when memorial access is revoked
     */
    fun trackMemorialAccessRevoked(
        memorialId: String,
        revokedFromUserId: String,
        previousPermissions: MemorialSharingPermissions,
        revocationReason: String = "manual"
    ) {
        firebaseAnalytics.logEvent(EVENT_MEMORIAL_ACCESS_REVOKED) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param("revoked_from_user_id", revokedFromUserId)
            param("previous_permissions", previousPermissions.name)
            param("revocation_reason", revocationReason)
            param("revoked_by_user_id", auth.currentUser?.uid ?: "system")
        }
    }
    
    /**
     * Track specific social platform sharing
     */
    fun trackSocialPlatformShare(
        memorialId: String,
        platform: SharingPlatform,
        shareSuccess: Boolean,
        errorMessage: String? = null
    ) {
        firebaseAnalytics.logEvent(EVENT_SOCIAL_PLATFORM_SHARE) {
            param(PARAM_MEMORIAL_ID, memorialId)
            param(PARAM_SHARING_PLATFORM, platform.name)
            param("share_success", if (shareSuccess) "true" else "false")
            param("error_message", errorMessage ?: "none")
            param("user_id", auth.currentUser?.uid ?: "anonymous")
        }
    }
    
    /**
     * Set user properties related to sharing behavior
     */
    fun setUserSharingProperties(
        totalMemorialsShared: Int,
        totalFamilyInvitationsSent: Int,
        mostUsedSharingPlatform: SharingPlatform?,
        isActiveSharer: Boolean
    ) {
        firebaseAnalytics.setUserProperty("total_memorials_shared", totalMemorialsShared.toString())
        firebaseAnalytics.setUserProperty("total_family_invitations_sent", totalFamilyInvitationsSent.toString())
        firebaseAnalytics.setUserProperty("most_used_sharing_platform", mostUsedSharingPlatform?.name ?: "none")
        firebaseAnalytics.setUserProperty("is_active_sharer", if (isActiveSharer) "true" else "false")
        firebaseAnalytics.setUserProperty("last_sharing_activity", System.currentTimeMillis().toString())
    }
    
    /**
     * Track conversion funnel from invitation to prayer participation
     */
    fun trackSharingConversionFunnel(
        memorialId: String,
        funnelStep: String, // "invitation_sent", "invitation_viewed", "invitation_accepted", "first_prayer"
        additionalData: Map<String, Any> = emptyMap()
    ) {
        val params = mutableMapOf<String, Any>(
            PARAM_MEMORIAL_ID to memorialId,
            "funnel_step" to funnelStep,
            "user_id" to (auth.currentUser?.uid ?: "anonymous"),
            "timestamp" to System.currentTimeMillis()
        )
        
        params.putAll(additionalData)
        
        firebaseAnalytics.logEvent("sharing_conversion_funnel") {
            params.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
    
    /**
     * Track memorial sharing session duration and engagement
     */
    fun trackSharingSessionEngagement(
        memorialId: String,
        sessionDurationMs: Long,
        sharingActionsCount: Int,
        platformsUsed: List<SharingPlatform>,
        invitationsSent: Int
    ) {
        firebaseAnalytics.logEvent("sharing_session_engagement") {
            param(PARAM_MEMORIAL_ID, memorialId)
            param("session_duration_minutes", (sessionDurationMs / 60000).toDouble())
            param("sharing_actions_count", sharingActionsCount.toLong())
            param("platforms_used_count", platformsUsed.size.toLong())
            param("platforms_used", platformsUsed.joinToString(",") { it.name })
            param("invitations_sent", invitationsSent.toLong())
            param("user_id", auth.currentUser?.uid ?: "anonymous")
        }
    }
}