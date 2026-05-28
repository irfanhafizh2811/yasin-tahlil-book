package com.app_muslim.surah_yasin.feature.community.repository

import com.app_muslim.surah_yasin.feature.community.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.FieldValue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Community Interactions and Memorial Engagement
 * Manages respectful memorial interactions, regional communities, and engagement analytics
 */
@Singleton
class CommunityInteractionsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    /**
     * Get memorial interactions for a specific memorial
     */
    fun getMemorialInteractions(
        memorialId: String,
        limit: Int = 50
    ): Flow<List<MemorialInteraction>> {
        return firestore.collection("memorial_interactions")
            .whereEqualTo("memorial_id", memorialId)
            .whereEqualTo("is_public", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToMemorialInteraction(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Create a respectful memorial interaction
     */
    suspend fun createMemorialInteraction(
        memorialId: String,
        interactionType: MemorialInteractionType,
        content: String = "",
        isPublic: Boolean = true,
        isAnonymous: Boolean = false
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // Validate content for Islamic appropriateness
            if (!isContentAppropriate(content, interactionType)) {
                return Result.failure(IllegalArgumentException("Content does not meet Islamic guidelines"))
            }
            
            val interactionData = mapOf(
                "memorial_id" to memorialId,
                "user_id" to userId,
                "user_name" to if (isAnonymous) "Anonymous" else getCurrentUserName(),
                "interaction_type" to interactionType.name,
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp(),
                "is_public" to isPublic,
                "likes" to 0,
                "replies" to emptyList<Map<String, Any>>(),
                "is_anonymous" to isAnonymous,
                "region" to getCurrentUserRegion(),
                "language" to getCurrentUserLanguage(),
                "is_approved" to true, // Auto-approve for now, implement moderation later
                "cultural_validation_score" to calculateCulturalValidationScore(content, interactionType)
            )
            
            val docRef = firestore.collection("memorial_interactions")
                .add(interactionData)
                .await()
            
            // Update memorial interaction count
            updateMemorialInteractionCount(memorialId)
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get regional Islamic communities
     */
    fun getRegionalCommunities(
        region: String? = null,
        limit: Int = 20
    ): Flow<List<RegionalIslamicCommunity>> {
        return firestore.collection("regional_islamic_communities")
            .let { query ->
                if (region != null) {
                    query.whereEqualTo("region", region)
                } else query
            }
            .whereEqualTo("is_active", true)
            .orderBy("member_count", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToRegionalCommunity(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Join a regional Islamic community
     */
    suspend fun joinCommunity(communityId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val communityRef = firestore.collection("regional_islamic_communities").document(communityId)
            
            firestore.runTransaction { transaction ->
                val communityDoc = transaction.get(communityRef)
                
                if (!communityDoc.exists()) {
                    throw Exception("Community not found")
                }
                
                val communityData = communityDoc.data!!
                val currentMembers = (communityData["members"] as? List<String>) ?: emptyList()
                
                if (userId in currentMembers) {
                    throw Exception("You are already a member of this community")
                }
                
                val updatedMembers = currentMembers + userId
                val memberCount = (communityData["member_count"] as? Number)?.toLong() ?: 0L
                
                transaction.update(communityRef, mapOf(
                    "members" to updatedMembers,
                    "member_count" to memberCount + 1,
                    "last_active_at" to FieldValue.serverTimestamp()
                ))
                
                // Create member record
                val memberData = mapOf(
                    "user_id" to userId,
                    "community_id" to communityId,
                    "joined_at" to FieldValue.serverTimestamp(),
                    "is_active" to true,
                    "role" to "member",
                    "contribution_score" to 0L
                )
                
                transaction.set(
                    firestore.collection("community_members").document("${communityId}_${userId}"),
                    memberData
                )
                
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get community events for a region or community
     */
    fun getCommunityEvents(
        communityId: String? = null,
        region: String? = null,
        limit: Int = 20
    ): Flow<List<CommunityScheduledEvent>> {
        return firestore.collection("community_events")
            .let { query ->
                when {
                    communityId != null -> query.whereEqualTo("community_id", communityId)
                    region != null -> query.whereEqualTo("region", region)
                    else -> query
                }
            }
            .whereEqualTo("is_active", true)
            .whereGreaterThan("scheduled_time", com.google.firebase.Timestamp.now())
            .orderBy("scheduled_time", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToCommunityScheduledEvent(doc.id, data)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Create a community event (for moderators/admins)
     */
    suspend fun createCommunityEvent(
        title: String,
        titleArabic: String? = null,
        description: String,
        type: CommunityEventType,
        scheduledTime: ZonedDateTime,
        duration: Long, // in minutes
        communityId: String? = null,
        region: String = getCurrentUserRegion(),
        participantLimit: Int? = null,
        requirements: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            // TODO: Check if user has permission to create events in this community/region
            
            val eventData = mapOf(
                "title" to title,
                "title_arabic" to titleArabic,
                "description" to description,
                "type" to type.name,
                "scheduled_time" to com.google.firebase.Timestamp(scheduledTime.toEpochSecond(), scheduledTime.nano),
                "duration" to duration,
                "community_id" to communityId,
                "region" to region,
                "participant_limit" to participantLimit,
                "current_participants" to 0,
                "is_recurring" to false,
                "recurrence_pattern" to null,
                "is_active" to true,
                "requirements" to requirements,
                "tags" to tags,
                "created_by" to userId,
                "created_at" to FieldValue.serverTimestamp(),
                "updated_at" to FieldValue.serverTimestamp()
            )
            
            val docRef = firestore.collection("community_events")
                .add(eventData)
                .await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get community engagement metrics
     */
    suspend fun getCommunityEngagementMetrics(
        region: String? = null,
        timeFrame: String = "weekly" // daily, weekly, monthly
    ): Result<CommunityEngagementMetrics> {
        return try {
            // Get aggregated metrics from analytics collection
            val metricsDoc = if (region != null) {
                firestore.collection("community_engagement_metrics")
                    .document("${region}_${timeFrame}")
                    .get()
                    .await()
            } else {
                firestore.collection("community_engagement_metrics")
                    .document("global_${timeFrame}")
                    .get()
                    .await()
            }
            
            if (metricsDoc.exists()) {
                val data = metricsDoc.data!!
                val metrics = mapDocumentToEngagementMetrics(data)
                Result.success(metrics)
            } else {
                // Return default metrics if no data available
                Result.success(
                    CommunityEngagementMetrics(
                        totalCommunities = 0L,
                        activeCommunities = 0L,
                        totalMembers = 0L,
                        activeMembers = 0L,
                        totalInteractions = 0L,
                        averageParticipationRate = 0.0f,
                        lastCalculated = ZonedDateTime.now()
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Like a memorial interaction
     */
    suspend fun likeInteraction(interactionId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            val interactionRef = firestore.collection("memorial_interactions").document(interactionId)
            
            firestore.runTransaction { transaction ->
                val doc = transaction.get(interactionRef)
                if (!doc.exists()) {
                    throw Exception("Interaction not found")
                }
                
                val currentLikes = (doc.data!!["likes"] as? Number)?.toInt() ?: 0
                transaction.update(interactionRef, mapOf("likes" to currentLikes + 1))
                
                // Record the like
                val likeData = mapOf(
                    "user_id" to userId,
                    "interaction_id" to interactionId,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                
                transaction.set(
                    firestore.collection("interaction_likes").document("${interactionId}_${userId}"),
                    likeData
                )
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add a reply to a memorial interaction
     */
    suspend fun replyToInteraction(
        interactionId: String,
        content: String,
        isAnonymous: Boolean = false
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(IllegalStateException("User not authenticated"))
            
            if (!isContentAppropriate(content, MemorialInteractionType.PRAYER_COMPLETION)) {
                return Result.failure(IllegalArgumentException("Reply content does not meet Islamic guidelines"))
            }
            
            val replyId = java.util.UUID.randomUUID().toString()
            val reply = mapOf(
                "reply_id" to replyId,
                "user_id" to userId,
                "user_name" to if (isAnonymous) "Anonymous" else getCurrentUserName(),
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp(),
                "is_anonymous" to isAnonymous
            )
            
            firestore.collection("memorial_interactions")
                .document(interactionId)
                .update("replies", FieldValue.arrayUnion(reply))
                .await()
            
            Result.success(replyId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper methods
    
    private suspend fun updateMemorialInteractionCount(memorialId: String) {
        try {
            val memorialRef = firestore.collection("memorials").document(memorialId)
            firestore.runTransaction { transaction ->
                val doc = transaction.get(memorialRef)
                if (doc.exists()) {
                    val currentCount = (doc.data!!["interaction_count"] as? Number)?.toLong() ?: 0L
                    transaction.update(memorialRef, "interaction_count", currentCount + 1)
                }
            }.await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    private fun isContentAppropriate(content: String, type: MemorialInteractionType): Boolean {
        // Implement Islamic content validation
        // This is a simplified version - in production, use AI/ML for better validation
        
        val inappropriateWords = listOf<String>(
            // Add inappropriate words based on Islamic values
        )
        
        val lowerContent = content.lowercase()
        return !inappropriateWords.any { word -> lowerContent.contains(word) } && 
               content.length <= 500 && // Reasonable length limit
               content.trim().isNotEmpty()
    }
    
    private fun calculateCulturalValidationScore(content: String, type: MemorialInteractionType): Float {
        // Simplified scoring system
        var score = 1.0f
        
        // Check for Islamic phrases
        val islamicPhrases = listOf(
            "insha allah", "masha allah", "subhan allah", "alhamdulillah",
            "allah yarhamah", "allah yarhama", "may allah", "bismillah"
        )
        
        val lowerContent = content.lowercase()
        if (islamicPhrases.any { lowerContent.contains(it) }) {
            score += 0.5f
        }
        
        // Check for appropriate length and structure
        if (content.length in 20..200) {
            score += 0.3f
        }
        
        return minOf(score, 2.0f) // Max score of 2.0
    }
    
    private fun mapDocumentToMemorialInteraction(
        documentId: String,
        data: Map<String, Any>
    ): MemorialInteraction {
        return MemorialInteraction(
            interactionId = documentId,
            memorialId = data["memorial_id"] as? String ?: "",
            userId = data["user_id"] as? String ?: "",
            userName = data["user_name"] as? String ?: "",
            interactionType = MemorialInteractionType.valueOf(
                data["interaction_type"] as? String ?: MemorialInteractionType.PRAYER_COMPLETION.name
            ),
            content = data["content"] as? String ?: "",
            timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            isPublic = data["is_public"] as? Boolean ?: true,
            likes = (data["likes"] as? Number)?.toInt() ?: 0,
            replies = mapRepliesList(data["replies"] as? List<Map<String, Any>> ?: emptyList()),
            isAnonymous = data["is_anonymous"] as? Boolean ?: false,
            region = data["region"] as? String ?: "",
            language = data["language"] as? String ?: "en"
        )
    }
    
    private fun mapRepliesList(repliesList: List<Map<String, Any>>): List<InteractionReply> {
        return repliesList.mapNotNull { replyData ->
            try {
                InteractionReply(
                    replyId = replyData["reply_id"] as? String ?: "",
                    userId = replyData["user_id"] as? String ?: "",
                    userName = replyData["user_name"] as? String ?: "",
                    content = replyData["content"] as? String ?: "",
                    timestamp = (replyData["timestamp"] as? com.google.firebase.Timestamp)?.let {
                        ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
                    } ?: ZonedDateTime.now(),
                    isAnonymous = replyData["is_anonymous"] as? Boolean ?: false
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun mapDocumentToRegionalCommunity(
        documentId: String,
        data: Map<String, Any>
    ): RegionalIslamicCommunity {
        return RegionalIslamicCommunity(
            communityId = documentId,
            name = data["name"] as? String ?: "",
            nameArabic = data["name_arabic"] as? String,
            region = data["region"] as? String ?: "",
            country = data["country"] as? String ?: "",
            city = data["city"] as? String,
            islamicSchool = IslamicSchoolOfThought.valueOf(
                data["islamic_school"] as? String ?: IslamicSchoolOfThought.GENERAL.name
            ),
            language = data["language"] as? String ?: "en",
            memberCount = (data["member_count"] as? Number)?.toLong() ?: 0L,
            activeMemberCount = (data["active_member_count"] as? Number)?.toLong() ?: 0L,
            totalMemorials = (data["total_memorials"] as? Number)?.toLong() ?: 0L,
            totalPrayers = (data["total_prayers"] as? Number)?.toLong() ?: 0L,
            weeklyGoal = (data["weekly_goal"] as? Number)?.toLong() ?: 1000L,
            currentWeekProgress = (data["current_week_progress"] as? Number)?.toLong() ?: 0L,
            isVerified = data["is_verified"] as? Boolean ?: false,
            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            lastActiveAt = (data["last_active_at"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            description = data["description"] as? String ?: "",
            guidelines = (data["guidelines"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            timeZone = data["time_zone"] as? String ?: "UTC"
        )
    }
    
    private fun mapDocumentToCommunityScheduledEvent(
        documentId: String,
        data: Map<String, Any>
    ): CommunityScheduledEvent {
        return CommunityScheduledEvent(
            eventId = documentId,
            title = data["title"] as? String ?: "",
            titleArabic = data["title_arabic"] as? String,
            description = data["description"] as? String ?: "",
            type = CommunityEventType.valueOf(
                data["type"] as? String ?: CommunityEventType.GROUP_PRAYER.name
            ),
            scheduledTime = (data["scheduled_time"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            duration = (data["duration"] as? Number)?.toLong() ?: 60L,
            communityId = data["community_id"] as? String,
            region = data["region"] as? String ?: "",
            participantLimit = (data["participant_limit"] as? Number)?.toInt(),
            currentParticipants = (data["current_participants"] as? Number)?.toInt() ?: 0,
            requirements = (data["requirements"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            tags = (data["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            isRecurring = data["is_recurring"] as? Boolean ?: false,
            recurrencePattern = data["recurrence_pattern"] as? String,
            isActive = data["is_active"] as? Boolean ?: true
        )
    }
    
    private fun mapDocumentToEngagementMetrics(data: Map<String, Any>): CommunityEngagementMetrics {
        return CommunityEngagementMetrics(
            totalCommunities = (data["total_communities"] as? Number)?.toLong() ?: 0L,
            activeCommunities = (data["active_communities"] as? Number)?.toLong() ?: 0L,
            totalMembers = (data["total_members"] as? Number)?.toLong() ?: 0L,
            activeMembers = (data["active_members"] as? Number)?.toLong() ?: 0L,
            totalInteractions = (data["total_interactions"] as? Number)?.toLong() ?: 0L,
            averageParticipationRate = (data["average_participation_rate"] as? Number)?.toFloat() ?: 0.0f,
            lastCalculated = (data["last_calculated"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now()
        )
    }
    
    // Helper methods - would normally get from user service/preferences
    private fun getCurrentUserName(): String = "Community Member" // TODO: Get from user profile
    private fun getCurrentUserRegion(): String = "Middle East" // TODO: Get from user profile/location
    private fun getCurrentUserLanguage(): String = "en" // TODO: Get from user preferences
}