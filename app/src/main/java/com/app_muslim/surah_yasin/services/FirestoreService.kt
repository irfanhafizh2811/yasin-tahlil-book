package com.app_muslim.surah_yasin.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreService(private val firestore: FirebaseFirestore) {
    
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val MEMORIALS_COLLECTION = "memorials"
        private const val PRAYER_SESSIONS_COLLECTION = "prayer_sessions"
        private const val GLOBAL_STATS_COLLECTION = "global_stats"
        private const val USER_PROFILES_COLLECTION = "user_profiles"
    }
    
    // User Profile Operations
    suspend fun createUserProfile(userId: String, profile: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(USER_PROFILES_COLLECTION)
                .document(userId)
                .set(profile)
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun getUserProfile(userId: String): Result<Map<String, Any>?> {
        return try {
            val document = firestore.collection(USER_PROFILES_COLLECTION)
                .document(userId)
                .get()
                .await()
            Result.success(document.data)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(USER_PROFILES_COLLECTION)
                .document(userId)
                .set(updates, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Memorial Operations
    suspend fun createMemorial(memorial: Map<String, Any>): Result<String> {
        return try {
            val documentRef = firestore.collection(MEMORIALS_COLLECTION)
                .add(memorial)
                .await()
            Result.success(documentRef.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun getMemorial(memorialId: String): Result<Map<String, Any>?> {
        return try {
            val document = firestore.collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .get()
                .await()
            val data = document.data?.toMutableMap()
            data?.put("id", document.id)
            Result.success(data)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun updateMemorial(memorialId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .set(updates, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun deleteMemorial(memorialId: String): Result<Unit> {
        return try {
            firestore.collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Get user's memorials
    fun getUserMemorials(userId: String): Flow<List<Map<String, Any>>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("createdBy", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                    }
                }
            }
    }
    
    // Get community memorials (public)
    fun getCommunityMemorials(): Flow<List<Map<String, Any>>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("privacy", "community")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                    }
                }
            }
    }
    
    // Prayer Session Operations
    suspend fun createPrayerSession(session: Map<String, Any>): Result<String> {
        return try {
            val documentRef = firestore.collection(PRAYER_SESSIONS_COLLECTION)
                .add(session)
                .await()
            Result.success(documentRef.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun updatePrayerSession(sessionId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(PRAYER_SESSIONS_COLLECTION)
                .document(sessionId)
                .set(updates, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Get prayer sessions for a memorial
    fun getMemorialPrayerSessions(memorialId: String): Flow<List<Map<String, Any>>> {
        return firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                    }
                }
            }
    }
    
    // Get user's prayer sessions
    fun getUserPrayerSessions(userId: String): Flow<List<Map<String, Any>>> {
        return firestore.collection(PRAYER_SESSIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                    }
                }
            }
    }
    
    // Global Statistics Operations
    suspend fun updateGlobalStats(statId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(GLOBAL_STATS_COLLECTION)
                .document(statId)
                .set(updates, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    fun getGlobalStats(): Flow<Map<String, Any>?> {
        return firestore.collection(GLOBAL_STATS_COLLECTION)
            .document("current")
            .snapshots()
            .map { snapshot -> snapshot.data }
    }
    
    // Memorial Search
    fun searchMemorials(searchTerm: String, userId: String): Flow<List<Map<String, Any>>> {
        return firestore.collection(MEMORIALS_COLLECTION)
            .whereEqualTo("createdBy", userId)
            .whereGreaterThanOrEqualTo("name", searchTerm)
            .whereLessThanOrEqualTo("name", searchTerm + "\uf8ff")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        put("id", document.id)
                    }
                }
            }
    }
    
    // Batch Operations
    suspend fun batchUpdatePrayerCounts(updates: List<Pair<String, Map<String, Any>>>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            
            updates.forEach { (documentId, updateData) ->
                val docRef = firestore.collection(PRAYER_SESSIONS_COLLECTION).document(documentId)
                batch.update(docRef, updateData)
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Memorial sharing - add family member
    suspend fun addFamilyMemberToMemorial(memorialId: String, memberUserId: String): Result<Unit> {
        return try {
            firestore.collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .update("familyMembers", com.google.firebase.firestore.FieldValue.arrayUnion(memberUserId))
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Memorial sharing - remove family member
    suspend fun removeFamilyMemberFromMemorial(memorialId: String, memberUserId: String): Result<Unit> {
        return try {
            firestore.collection(MEMORIALS_COLLECTION)
                .document(memorialId)
                .update("familyMembers", com.google.firebase.firestore.FieldValue.arrayRemove(memberUserId))
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}