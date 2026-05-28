package com.app_muslim.surah_yasin.feature.community.repository

import com.app_muslim.surah_yasin.feature.community.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
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
 * Repository for Memorial Discovery and Community Search
 * Handles memorial discovery, filtering, and search functionality
 */
@Singleton
class MemorialDiscoveryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    /**
     * Search and discover memorials with advanced filtering
     */
    fun discoverMemorials(
        filter: MemorialDiscoveryFilter,
        limit: Int = 20
    ): Flow<List<DiscoverableMemorial>> {
        return firestore.collection("discoverable_memorials")
            .let { query ->
                // Apply privacy filter - only show public or community memorials
                val privacyQuery = if (filter.privacyLevel != null) {
                    query.whereEqualTo("privacy_level", filter.privacyLevel!!.name)
                } else {
                    query.whereIn("privacy_level", listOf(
                        MemorialPrivacyLevel.PUBLIC.name,
                        MemorialPrivacyLevel.COMMUNITY.name
                    ))
                }
                
                privacyQuery
            }
            .let { query ->
                // Apply region filter
                if (filter.region != null) {
                    query.whereEqualTo("region", filter.region!!)
                } else query
            }
            .let { query ->
                // Apply prayer type filter
                if (filter.prayerType != null) {
                    query.whereEqualTo("prayer_type", filter.prayerType!!.name)
                } else query
            }
            .let { query ->
                // Apply active prayers filter
                if (filter.hasActivePrayers == true) {
                    query.whereGreaterThan("active_participants", 0)
                } else query
            }
            .let { query ->
                // Apply prayer count filter
                if (filter.minPrayerCount != null) {
                    query.whereGreaterThanOrEqualTo("total_prayers", filter.minPrayerCount!!.toLong())
                } else query
            }
            .let { query ->
                // Apply max results limit
                query
            }
            .let { query ->
                // Apply sorting
                when (filter.sortType) {
                    MemorialSortType.MOST_RECENT -> query.orderBy("created_at", Query.Direction.DESCENDING)
                    MemorialSortType.MOST_ACTIVE -> query.orderBy("recent_activity", Query.Direction.DESCENDING)
                    MemorialSortType.MOST_PRAYERS -> query.orderBy("total_prayers", Query.Direction.DESCENDING)
                    MemorialSortType.ALPHABETICAL -> query.orderBy("deceased_name", Query.Direction.ASCENDING)
                    MemorialSortType.NEAREST -> query.orderBy("created_at", Query.Direction.DESCENDING)
                    MemorialSortType.HIGHEST_RATED -> query.orderBy("created_at", Query.Direction.DESCENDING)
                }
            }
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        
                        DiscoverableMemorial(
                            memorialId = doc.id,
                            deceasedName = data["deceased_name"] as? String ?: "",
                            deceasedNameArabic = data["deceased_name_arabic"] as? String,
                            photoUrl = data["photo_url"] as? String,
                            createdByUserId = data["created_by_user_id"] as? String ?: "",
                            createdByName = data["created_by_name"] as? String ?: "",
                            privacyLevel = MemorialPrivacyLevel.valueOf(
                                data["privacy_level"] as? String ?: MemorialPrivacyLevel.COMMUNITY.name
                            ),
                            region = data["region"] as? String ?: "",
                            totalPrayers = (data["total_prayers"] as? Number)?.toLong() ?: 0L,
                            activePrayerCount = (data["active_prayers"] as? Number)?.toLong() ?: 0L,
                            participantCount = (data["participant_count"] as? Number)?.toInt() ?: 0,
                            lastPrayerAt = (data["last_prayer_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
                            },
                            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
                            } ?: ZonedDateTime.now(),
                            description = data["description"] as? String,
                            tags = (data["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                            isVerified = data["is_verified"] as? Boolean ?: false,
                            averageRating = (data["average_rating"] as? Number)?.toFloat() ?: 0.0f,
                            ratingCount = (data["rating_count"] as? Number)?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                .let { memorials ->
                    // Apply text search filter on the client side (for complex search)
                    if (filter.searchQuery.isNotBlank()) {
                        memorials.filter { memorial ->
                            val query = filter.searchQuery.lowercase()
                            memorial.deceasedName.lowercase().contains(query) ||
                            memorial.deceasedNameArabic?.lowercase()?.contains(query) == true ||
                            memorial.description?.lowercase()?.contains(query) == true ||
                            memorial.tags.any { it.lowercase().contains(query) } ||
                            memorial.region.lowercase().contains(query) ||
                            memorial.region.lowercase().contains(query)
                        }
                    } else {
                        memorials
                    }
                }
                .let { memorials ->
                    // Apply date range filter if specified
                    filter.dateRange?.let { dateRange ->
                        memorials.filter { memorial ->
                            memorial.createdAt.isAfter(dateRange.startDate) &&
                            memorial.createdAt.isBefore(dateRange.endDate)
                        }
                    } ?: memorials
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }
    
    /**
     * Get featured memorials based on community engagement
     */
    fun getFeaturedMemorials(limit: Int = 10): Flow<List<DiscoverableMemorial>> {
        return firestore.collection("discoverable_memorials")
            .whereEqualTo("privacy_level", MemorialPrivacyLevel.PUBLIC.name)
            .whereGreaterThan("total_participants", 5)
            .orderBy("total_participants", Query.Direction.DESCENDING)
            .orderBy("recent_activity", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToDiscoverableMemorial(doc.id, data)
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
     * Get trending memorials (high recent activity)
     */
    fun getTrendingMemorials(limit: Int = 10): Flow<List<DiscoverableMemorial>> {
        return firestore.collection("discoverable_memorials")
            .whereEqualTo("privacy_level", MemorialPrivacyLevel.PUBLIC.name)
            .whereGreaterThan("active_participants", 0)
            .orderBy("active_participants", Query.Direction.DESCENDING)
            .orderBy("recent_activity", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        mapDocumentToDiscoverableMemorial(doc.id, data)
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
     * Get memorial discovery suggestions based on user's region and preferences
     */
    suspend fun getPersonalizedSuggestions(
        userRegion: String,
        userLanguage: String,
        limit: Int = 10
    ): Result<List<DiscoverableMemorial>> {
        return try {
            val querySnapshot = firestore.collection("discoverable_memorials")
                .whereEqualTo("region", userRegion)
                .whereEqualTo("language", userLanguage)
                .whereIn("privacy_level", listOf(
                    MemorialPrivacyLevel.PUBLIC.name,
                    MemorialPrivacyLevel.COMMUNITY.name
                ))
                .whereGreaterThan("total_participants", 2)
                .orderBy("total_participants", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val suggestions = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    mapDocumentToDiscoverableMemorial(doc.id, data)
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(suggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get memorial details for joining
     */
    suspend fun getMemorialForJoining(memorialId: String): Result<DiscoverableMemorial?> {
        return try {
            val doc = firestore.collection("discoverable_memorials")
                .document(memorialId)
                .get()
                .await()
            
            if (doc.exists()) {
                val data = doc.data!!
                val memorial = mapDocumentToDiscoverableMemorial(doc.id, data)
                Result.success(memorial)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Track memorial discovery interaction for analytics
     */
    suspend fun trackMemorialDiscovery(
        memorialId: String,
        action: String,
        searchQuery: String? = null,
        filter: MemorialDiscoveryFilter? = null
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: "anonymous"
            
            val trackingData = mutableMapOf<String, Any>(
                "user_id" to userId,
                "memorial_id" to memorialId,
                "action" to action,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            
            searchQuery?.let { trackingData["search_query"] = it }
            filter?.let {
                trackingData["filter_region"] = it.region ?: ""
                trackingData["filter_prayer_type"] = it.prayerType?.name ?: ""
                trackingData["filter_sort_by"] = it.sortType.name
            }
            
            firestore.collection("memorial_discovery_tracking")
                .add(trackingData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get available regions for filtering
     */
    suspend fun getAvailableRegions(): Result<List<String>> {
        return try {
            val querySnapshot = firestore.collection("discoverable_memorials")
                .whereIn("privacy_level", listOf(
                    MemorialPrivacyLevel.PUBLIC.name,
                    MemorialPrivacyLevel.COMMUNITY.name
                ))
                .get()
                .await()
            
            val regions = querySnapshot.documents
                .mapNotNull { it.getString("region") }
                .distinct()
                .sorted()
            
            Result.success(regions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get available languages for filtering
     */
    suspend fun getAvailableLanguages(): Result<List<String>> {
        return try {
            val querySnapshot = firestore.collection("discoverable_memorials")
                .whereIn("privacy_level", listOf(
                    MemorialPrivacyLevel.PUBLIC.name,
                    MemorialPrivacyLevel.COMMUNITY.name
                ))
                .get()
                .await()
            
            val languages = querySnapshot.documents
                .mapNotNull { it.getString("language") }
                .distinct()
                .sorted()
            
            Result.success(languages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper method to map Firestore document to DiscoverableMemorial
    private fun mapDocumentToDiscoverableMemorial(
        documentId: String,
        data: Map<String, Any>
    ): DiscoverableMemorial {
        return DiscoverableMemorial(
            memorialId = documentId,
            deceasedName = data["deceased_name"] as? String ?: "",
            deceasedNameArabic = data["deceased_name_arabic"] as? String,
            photoUrl = data["photo_url"] as? String,
            createdByUserId = data["created_by_user_id"] as? String ?: "",
            createdByName = data["created_by_name"] as? String ?: "",
            privacyLevel = MemorialPrivacyLevel.valueOf(
                data["privacy_level"] as? String ?: MemorialPrivacyLevel.COMMUNITY.name
            ),
            region = data["region"] as? String ?: "",
            totalPrayers = (data["total_prayers"] as? Number)?.toLong() ?: 0L,
            activePrayerCount = (data["active_prayers"] as? Number)?.toLong() ?: 0L,
            participantCount = (data["participant_count"] as? Number)?.toInt() ?: 0,
            lastPrayerAt = (data["last_prayer_at"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            },
            createdAt = (data["created_at"] as? com.google.firebase.Timestamp)?.let {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.seconds), ZoneId.systemDefault())
            } ?: ZonedDateTime.now(),
            description = data["description"] as? String,
            tags = (data["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            isVerified = data["is_verified"] as? Boolean ?: false,
            averageRating = (data["average_rating"] as? Number)?.toFloat() ?: 0.0f,
            ratingCount = (data["rating_count"] as? Number)?.toInt() ?: 0
        )
    }
}