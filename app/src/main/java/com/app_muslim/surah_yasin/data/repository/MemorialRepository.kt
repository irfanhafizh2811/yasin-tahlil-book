package com.app_muslim.surah_yasin.data.repository

import com.app_muslim.surah_yasin.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Islamic memorial prayers and community participation.
 * 
 * Handles creation, management, and participation in memorial prayers following
 * Islamic traditions and the 40-day memorial period.
 * 
 * @author Tahlil Development Team
 * @since 2026
 */
interface MemorialRepository {
    
    // Memorial Management
    
    /**
     * Creates a new memorial with Islamic traditions and privacy controls.
     * 
     * The memorial will automatically expire after 40 days following Islamic tradition.
     * Content is validated for cultural appropriateness before creation.
     * 
     * @param memorial The memorial data to create
     * @return Result containing memorial ID on success, exception on failure
     * @throws CulturalException if content doesn't meet Islamic guidelines
     * @throws NetworkException if Firebase operation fails
     */
    suspend fun createMemorial(memorial: Memorial): Result<String>
    
    /**
     * Retrieves memorials for a specific user based on privacy permissions.
     * 
     * @param userId The user ID to fetch memorials for
     * @return Flow of memorial list with real-time updates
     */
    fun getMemorials(userId: String): Flow<List<Memorial>>
    
    /**
     * Retrieves community memorials accessible to the user.
     * 
     * @param region Optional Islamic region filter
     * @param limit Maximum number of memorials to return
     * @return Flow of community memorial list
     */
    fun getCommunityMemorials(
        region: IslamicRegion? = null,
        limit: Int = 20
    ): Flow<List<Memorial>>
    
    /**
     * Updates an existing memorial (creator only).
     * 
     * @param memorial Updated memorial data
     * @return Result indicating success or failure
     */
    suspend fun updateMemorial(memorial: Memorial): Result<Unit>
    
    /**
     * Deletes a memorial (creator only).
     * 
     * @param memorialId ID of memorial to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMemorial(memorialId: String): Result<Unit>
    
    /**
     * Retrieves a specific memorial by ID if user has access.
     * 
     * @param memorialId The memorial ID
     * @return Result containing memorial or error
     */
    suspend fun getMemorial(memorialId: String): Result<Memorial>
    
    /**
     * Observes a specific memorial for real-time updates.
     * 
     * @param memorialId The memorial ID to observe
     * @return Flow of memorial updates (null if deleted/expired)
     */
    fun observeMemorial(memorialId: String): Flow<Memorial?>
    
    // Prayer Session Management
    
    /**
     * Starts a prayer session for a memorial.
     * 
     * @param memorialId ID of the memorial
     * @param prayerType Type of prayer (Tahlil, Yasin, etc.)
     * @param userId User starting the prayer
     * @return Result containing session ID
     */
    suspend fun startPrayerSession(
        memorialId: String,
        prayerType: PrayerType,
        userId: String
    ): Result<String>
    
    /**
     * Completes a prayer session.
     * 
     * @param sessionId The prayer session ID
     * @param completed Whether the prayer was completed
     * @param notes Optional notes about the prayer session
     * @return Result indicating success or failure
     */
    suspend fun completePrayerSession(
        sessionId: String,
        completed: Boolean = true,
        notes: String? = null
    ): Result<Unit>
    
    /**
     * Retrieves prayer sessions for a user.
     * 
     * @param userId User ID
     * @param limit Maximum number of sessions to return
     * @return Flow of prayer session list
     */
    fun getUserPrayerSessions(
        userId: String,
        limit: Int = 50
    ): Flow<List<MemorialPrayer>>
    
    /**
     * Retrieves prayer sessions for a memorial.
     * 
     * @param memorialId Memorial ID
     * @return Flow of memorial prayer session list
     */
    fun getMemorialPrayerSessions(memorialId: String): Flow<List<MemorialPrayer>>
    
    // Family & Community Management
    
    /**
     * Adds family members to a memorial.
     * 
     * @param memorialId Memorial ID
     * @param familyMemberIds List of user IDs to add
     * @return Result indicating success or failure
     */
    suspend fun addFamilyMembers(
        memorialId: String,
        familyMemberIds: List<String>
    ): Result<Unit>
    
    /**
     * Removes family members from a memorial.
     * 
     * @param memorialId Memorial ID
     * @param familyMemberIds List of user IDs to remove
     * @return Result indicating success or failure
     */
    suspend fun removeFamilyMembers(
        memorialId: String,
        familyMemberIds: List<String>
    ): Result<Unit>
    
    /**
     * Updates memorial privacy level.
     * 
     * @param memorialId Memorial ID
     * @param privacy New privacy level
     * @return Result indicating success or failure
     */
    suspend fun updateMemorialPrivacy(
        memorialId: String,
        privacy: MemorialPrivacy
    ): Result<Unit>
    
    // Statistics & Analytics
    
    /**
     * Retrieves global prayer statistics.
     * 
     * @return Flow of global statistics
     */
    fun getGlobalPrayerStats(): Flow<GlobalPrayerStats>
    
    /**
     * Retrieves regional prayer statistics.
     * 
     * @param region Islamic region
     * @return Flow of regional statistics
     */
    fun getRegionalStats(region: IslamicRegion): Flow<RegionalStats>
    
    /**
     * Retrieves prayer statistics for a specific memorial.
     * 
     * @param memorialId Memorial ID
     * @return Result containing memorial statistics
     */
    suspend fun getMemorialStats(memorialId: String): Result<MemorialStats>
    
    // Offline Support
    
    /**
     * Syncs local changes with cloud storage.
     * 
     * @return Result indicating sync success or failure
     */
    suspend fun syncWithCloud(): Result<Unit>
    
    /**
     * Retrieves cached memorials for offline access.
     * 
     * @param userId User ID
     * @return List of cached memorials
     */
    suspend fun getCachedMemorials(userId: String): List<Memorial>
    
    /**
     * Caches memorial data for offline access.
     * 
     * @param memorial Memorial to cache
     */
    suspend fun cacheMemorial(memorial: Memorial)
    
    /**
     * Clears expired cached data.
     */
    suspend fun clearExpiredCache()
}