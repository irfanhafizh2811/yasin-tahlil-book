package com.app_muslim.surah_yasin.feature.memorial.repository

import android.net.Uri
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemorialRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    companion object {
        private const val MEMORIALS_COLLECTION = "memorials"
        private const val PARTICIPATION_COLLECTION = "memorial_participation"
        private const val MEMORIAL_PHOTOS_PATH = "memorial_photos"
    }

    suspend fun createMemorial(memorial: MemorialData): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated to create memorial")

        // Generate unique ID for the memorial
        val memorialId = firestore.collection(MEMORIALS_COLLECTION).document().id
        
        // Get user profile data for additional context
        val userProfile = getCurrentUserProfile()
        
        val memorialWithMetadata = memorial.copy(
            id = memorialId,
            creatorId = currentUser.uid,
            creatorName = currentUser.displayName ?: "Anonymous",
            region = userProfile?.region ?: "",
            schoolOfThought = userProfile?.schoolOfThought ?: ""
        )

        // Save to Firestore
        firestore.collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .set(memorialWithMetadata.toFirestoreMap())
            .await()

        return memorialId
    }

    suspend fun getMemorial(memorialId: String): MemorialData? {
        val document = firestore.collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .get()
            .await()

        return if (document.exists()) {
            document.toMemorialData()
        } else {
            null
        }
    }

    suspend fun updateMemorial(memorial: MemorialData) {
        firestore.collection(MEMORIALS_COLLECTION)
            .document(memorial.id)
            .set(memorial.toFirestoreMap())
            .await()
    }

    suspend fun deleteMemorial(memorialId: String) {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val memorial = getMemorial(memorialId)
        if (memorial?.creatorId != currentUser.uid) {
            throw SecurityException("Only the creator can delete the memorial")
        }

        // Delete memorial photo if exists
        memorial.photoUrl?.let { photoUrl ->
            try {
                deleteMemorialPhoto(photoUrl)
            } catch (e: Exception) {
                // Log error but continue with memorial deletion
            }
        }

        // Delete memorial document
        firestore.collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .delete()
            .await()

        // Clean up participation records
        firestore.collection(PARTICIPATION_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .get()
            .await()
            .documents
            .forEach { document ->
                document.reference.delete()
            }
    }

    fun getUserMemorials(userId: String): Flow<List<MemorialData>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("creatorId", userId)
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toMemorialData() }
            }
    }

    fun getUserMemorialsWithFilter(
        userId: String,
        privacyLevel: PrivacyLevel? = null,
        isActive: Boolean? = null,
        limit: Int = 50
    ): Flow<List<MemorialData>> {
        var query = firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("creatorId", userId)

        privacyLevel?.let { 
            query = query.whereEqualTo("privacyLevel", it.name)
        }
        
        isActive?.let {
            query = query.whereEqualTo("isActive", it)
        }

        return query
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toMemorialData() }
            }
    }

    suspend fun searchMemorials(
        userId: String,
        searchQuery: String
    ): List<MemorialData> {
        // Since Firestore doesn't support full-text search natively,
        // we'll fetch user memorials and filter client-side
        val allMemorials = firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("creatorId", userId)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
            .mapNotNull { it.toMemorialData() }

        val searchTerm = searchQuery.lowercase()
        return allMemorials.filter { memorial ->
            memorial.deceasedName.lowercase().contains(searchTerm) ||
            memorial.deceasedNameArabic?.lowercase()?.contains(searchTerm) == true ||
            memorial.memorialMessage.lowercase().contains(searchTerm) ||
            memorial.memorialMessageArabic?.lowercase()?.contains(searchTerm) == true ||
            memorial.tags.any { it.lowercase().contains(searchTerm) }
        }
    }

    fun getPublicMemorials(): Flow<List<MemorialData>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("privacyLevel", PrivacyLevel.PUBLIC.name)
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toMemorialData() }
            }
    }

    fun getCommunityMemorials(region: String): Flow<List<MemorialData>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereIn("privacyLevel", listOf(PrivacyLevel.COMMUNITY.name, PrivacyLevel.PUBLIC.name))
            .whereEqualTo("region", region)
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { it.toMemorialData() }
            }
    }

    suspend fun addPrayerParticipation(participation: MemorialParticipation): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val participationId = firestore.collection(PARTICIPATION_COLLECTION).document().id
        
        val participationWithMetadata = participation.copy(
            id = participationId,
            participantId = currentUser.uid,
            participantName = currentUser.displayName ?: "Anonymous",
            participatedAt = Date()
        )

        // Add participation record
        firestore.collection(PARTICIPATION_COLLECTION)
            .document(participationId)
            .set(participationWithMetadata.toFirestoreMap())
            .await()

        // Update memorial statistics
        updateMemorialStatistics(participation.memorialId)

        return participationId
    }

    suspend fun uploadMemorialPhoto(photoUri: Uri): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val fileName = "${currentUser.uid}_${System.currentTimeMillis()}.jpg"
        val photoRef = storage.reference
            .child(MEMORIAL_PHOTOS_PATH)
            .child(fileName)

        val uploadTask = photoRef.putFile(photoUri).await()
        return photoRef.downloadUrl.await().toString()
    }

    suspend fun deleteMemorialPhoto(photoUrl: String) {
        try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.delete().await()
        } catch (e: Exception) {
            // Photo might already be deleted or URL is invalid
            throw IllegalArgumentException("Failed to delete photo: ${e.message}")
        }
    }

    suspend fun getMemorialStatistics(memorialId: String): MemorialStats {
        val memorial = getMemorial(memorialId) 
            ?: throw IllegalArgumentException("Memorial not found")

        val participationSnapshot = firestore.collection(PARTICIPATION_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .get()
            .await()

        val participations = participationSnapshot.documents.mapNotNull { doc ->
            try {
                MemorialParticipation(
                    id = doc.getString("id") ?: "",
                    memorialId = doc.getString("memorialId") ?: "",
                    participantId = doc.getString("participantId") ?: "",
                    participantName = doc.getString("participantName") ?: "",
                    prayerType = doc.getString("prayerType")?.let { 
                        try { PrayerType.valueOf(it) } catch (e: Exception) { PrayerType.TAHLIL }
                    } ?: PrayerType.TAHLIL,
                    participatedAt = doc.getDate("participatedAt") ?: Date(),
                    prayerDuration = doc.getLong("prayerDuration") ?: 0,
                    isVerified = doc.getBoolean("isVerified") ?: false,
                    notes = doc.getString("notes") ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }

        // Calculate statistics
        val totalPrayers = participations.size.toLong()
        val uniqueParticipants = participations.map { it.participantId }.distinct().size.toLong()
        
        val prayersByType = participations.groupBy { it.prayerType }
            .mapValues { it.value.size.toLong() }

        // Get region statistics from user profiles
        val participantsByRegion = mutableMapOf<String, Long>()
        val uniqueParticipantIds = participations.map { it.participantId }.distinct()
        
        for (participantId in uniqueParticipantIds) {
            try {
                val userProfile = firestore.collection("user_profiles")
                    .document(participantId)
                    .get()
                    .await()
                
                val region = userProfile.getString("region") ?: "Unknown"
                participantsByRegion[region] = (participantsByRegion[region] ?: 0) + 1
            } catch (e: Exception) {
                participantsByRegion["Unknown"] = (participantsByRegion["Unknown"] ?: 0) + 1
            }
        }

        // Daily prayer counts (last 30 days)
        val thirtyDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -30)
        }.time

        val recentParticipations = participations.filter { it.participatedAt.after(thirtyDaysAgo) }
        val dailyPrayerCounts = recentParticipations.groupBy { participation ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(participation.participatedAt)
        }.mapValues { it.value.size.toLong() }

        val lastPrayerAt = participations.maxByOrNull { it.participatedAt }?.participatedAt

        return MemorialStats(
            totalPrayers = totalPrayers,
            totalParticipants = uniqueParticipants,
            prayersByType = prayersByType,
            participantsByRegion = participantsByRegion,
            dailyPrayerCounts = dailyPrayerCounts,
            lastPrayerAt = lastPrayerAt
        )
    }

    suspend fun getUserMemorialsSummary(userId: String): UserMemorialsSummary {
        val memorials = firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("creatorId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toMemorialData() }

        val activeMemorials = memorials.filter { it.isActive && !isExpired(it.expiresAt) }
        val expiredMemorials = memorials.filter { isExpired(it.expiresAt) }
        val totalPrayers = memorials.sumOf { it.prayerCount }
        val totalParticipants = memorials.sumOf { it.participantCount }

        return UserMemorialsSummary(
            totalMemorials = memorials.size.toLong(),
            activeMemorials = activeMemorials.size.toLong(),
            expiredMemorials = expiredMemorials.size.toLong(),
            totalPrayers = totalPrayers,
            totalParticipants = totalParticipants,
            mostPopularMemorial = memorials.maxByOrNull { it.prayerCount },
            recentMemorial = memorials.maxByOrNull { it.createdAt }
        )
    }

    private fun isExpired(expiresAt: Date): Boolean {
        return expiresAt.time < System.currentTimeMillis()
    }

    private suspend fun updateMemorialStatistics(memorialId: String) {
        val memorial = getMemorial(memorialId) ?: return

        // Get participation count
        val participationSnapshot = firestore.collection(PARTICIPATION_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .get()
            .await()

        val totalPrayers = participationSnapshot.size().toLong()
        val uniqueParticipants = participationSnapshot.documents
            .map { it.getString("participantId") }
            .distinct()
            .size.toLong()

        // Update memorial with new statistics
        firestore.collection(MEMORIALS_COLLECTION)
            .document(memorialId)
            .update(
                mapOf(
                    "prayerCount" to totalPrayers,
                    "participantCount" to uniqueParticipants
                )
            )
            .await()
    }

    private suspend fun getCurrentUserProfile(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        
        return try {
            val document = firestore.collection("user_profiles")
                .document(currentUser.uid)
                .get()
                .await()
            
            if (document.exists()) {
                UserProfile(
                    region = document.getString("region") ?: "",
                    schoolOfThought = document.getString("schoolOfThought") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Extension functions for Firestore mapping
    private fun MemorialData.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "creatorId" to creatorId,
            "creatorName" to creatorName,
            "deceasedName" to deceasedName,
            "deceasedNameArabic" to deceasedNameArabic,
            "memorialMessage" to memorialMessage,
            "memorialMessageArabic" to memorialMessageArabic,
            "dateOfDeath" to dateOfDeath,
            "dateOfDeathHijri" to dateOfDeathHijri.toFirestoreMap(),
            "photoUrl" to photoUrl,
            "privacyLevel" to privacyLevel.name,
            "prayerType" to prayerType.name,
            "createdAt" to createdAt,
            "expiresAt" to expiresAt,
            "isActive" to isActive,
            "prayerCount" to prayerCount,
            "participantCount" to participantCount,
            "familyMembers" to familyMembers,
            "tags" to tags,
            "region" to region,
            "schoolOfThought" to schoolOfThought
        )
    }

    private fun HijriDate.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "year" to year,
            "month" to month,
            "day" to day,
            "monthName" to monthName,
            "yearName" to yearName
        )
    }

    private fun MemorialParticipation.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "participantId" to participantId,
            "participantName" to participantName,
            "prayerType" to prayerType.name,
            "participatedAt" to participatedAt,
            "prayerDuration" to prayerDuration,
            "isVerified" to isVerified,
            "notes" to notes
        )
    }

    // Extension function to convert Firestore document to MemorialData
    private fun com.google.firebase.firestore.DocumentSnapshot.toMemorialData(): MemorialData? {
        return try {
            val hijriMap = get("dateOfDeathHijri") as? Map<*, *>
            val hijriDate = if (hijriMap != null) {
                HijriDate(
                    year = (hijriMap["year"] as? Long)?.toInt() ?: 0,
                    month = (hijriMap["month"] as? Long)?.toInt() ?: 0,
                    day = (hijriMap["day"] as? Long)?.toInt() ?: 0,
                    monthName = hijriMap["monthName"] as? String ?: "",
                    yearName = hijriMap["yearName"] as? String ?: ""
                )
            } else {
                HijriDate()
            }

            MemorialData(
                id = getString("id") ?: "",
                creatorId = getString("creatorId") ?: "",
                creatorName = getString("creatorName") ?: "",
                deceasedName = getString("deceasedName") ?: "",
                deceasedNameArabic = getString("deceasedNameArabic"),
                memorialMessage = getString("memorialMessage") ?: "",
                memorialMessageArabic = getString("memorialMessageArabic"),
                dateOfDeath = getDate("dateOfDeath") ?: Date(),
                dateOfDeathHijri = hijriDate,
                photoUrl = getString("photoUrl"),
                privacyLevel = getString("privacyLevel")?.let { 
                    try { PrivacyLevel.valueOf(it) } catch (e: Exception) { PrivacyLevel.PRIVATE }
                } ?: PrivacyLevel.PRIVATE,
                prayerType = getString("prayerType")?.let { 
                    try { PrayerType.valueOf(it) } catch (e: Exception) { PrayerType.TAHLIL }
                } ?: PrayerType.TAHLIL,
                createdAt = getDate("createdAt") ?: Date(),
                expiresAt = getDate("expiresAt") ?: Date(),
                isActive = getBoolean("isActive") ?: true,
                prayerCount = getLong("prayerCount") ?: 0,
                participantCount = getLong("participantCount") ?: 0,
                familyMembers = get("familyMembers") as? List<String> ?: emptyList(),
                tags = get("tags") as? List<String> ?: emptyList(),
                region = getString("region") ?: "",
                schoolOfThought = getString("schoolOfThought") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private data class UserProfile(
        val region: String,
        val schoolOfThought: String
    )
}