package com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.repository.MemorialRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemorialListViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemorialListUiState())
    val uiState: StateFlow<MemorialListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(MemorialFilter.ALL)
    val selectedFilter: StateFlow<MemorialFilter> = _selectedFilter.asStateFlow()

    private val allMemorials = MutableStateFlow<List<MemorialData>>(emptyList())

    init {
        // Observe user memorials and apply current filters
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                memorialRepository.getUserMemorials(user.uid)
                    .catch { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to load memorials",
                            isLoading = false
                        )
                    }
                    .collect { memorials ->
                        allMemorials.value = memorials
                        applyFiltersAndSearch()
                    }
            }
        }

        // Combine search query and filter to update UI state
        viewModelScope.launch {
            combine(
                allMemorials,
                _searchQuery,
                _selectedFilter
            ) { memorials, query, filter ->
                val filtered = filterMemorials(memorials, query, filter)
                _uiState.value = _uiState.value.copy(
                    memorials = filtered,
                    isLoading = false,
                    error = null
                )
            }.collect()
        }
    }

    fun loadMemorials() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        // The flow will automatically load memorials
    }

    fun refreshMemorials() {
        loadMemorials()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch()
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun performSearch() {
        applyFiltersAndSearch()
    }

    fun updateFilter(filter: MemorialFilter) {
        _selectedFilter.value = filter
    }

    fun deleteMemorial(memorialId: String) {
        viewModelScope.launch {
            try {
                memorialRepository.deleteMemorial(memorialId)
                // Remove from current list immediately for better UX
                val currentMemorials = _uiState.value.memorials.toMutableList()
                currentMemorials.removeAll { it.id == memorialId }
                _uiState.value = _uiState.value.copy(memorials = currentMemorials)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete memorial: ${e.message}"
                )
            }
        }
    }

    fun shareMemorial(memorial: MemorialData, shareType: ShareType, content: String) {
        viewModelScope.launch {
            try {
                // Implementation for sharing memorial
                // This could involve generating share links, social media integration, etc.
                when (shareType) {
                    ShareType.LINK -> shareMemorialLink(memorial)
                    ShareType.TEXT -> shareMemorialText(memorial, content)
                    ShareType.IMAGE -> shareMemorialImage(memorial)
                    ShareType.WHATSAPP -> shareToWhatsApp(memorial)
                    ShareType.FACEBOOK -> shareToFacebook(memorial)
                    ShareType.EMAIL -> shareViaEmail(memorial, content)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to share memorial: ${e.message}"
                )
            }
        }
    }

    fun loadMoreMemorials() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreData) return

        _uiState.value = _uiState.value.copy(isLoadingMore = true)

        // Implementation for pagination
        viewModelScope.launch {
            try {
                // For now, mark as no more data since we're loading all user memorials
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    hasMoreData = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = "Failed to load more memorials: ${e.message}"
                )
            }
        }
    }

    private fun applyFiltersAndSearch() {
        val memorials = allMemorials.value
        val query = _searchQuery.value
        val filter = _selectedFilter.value

        val filtered = filterMemorials(memorials, query, filter)

        _uiState.value = _uiState.value.copy(
            memorials = filtered,
            isLoading = false
        )
    }

    private fun filterMemorials(
        memorials: List<MemorialData>,
        query: String,
        filter: MemorialFilter
    ): List<MemorialData> {
        var filtered = memorials

        // Apply filter
        filtered = when (filter) {
            MemorialFilter.ALL -> filtered
            MemorialFilter.ACTIVE -> filtered.filter { it.isActive && !isExpired(it.expiresAt) }
            MemorialFilter.EXPIRED -> filtered.filter { isExpired(it.expiresAt) }
            MemorialFilter.PRIVATE -> filtered.filter { it.privacyLevel == PrivacyLevel.PRIVATE }
            MemorialFilter.FAMILY -> filtered.filter { it.privacyLevel == PrivacyLevel.FAMILY }
            MemorialFilter.COMMUNITY -> filtered.filter { it.privacyLevel == PrivacyLevel.COMMUNITY }
            MemorialFilter.PUBLIC -> filtered.filter { it.privacyLevel == PrivacyLevel.PUBLIC }
            MemorialFilter.RECENT -> filtered.filter { 
                val daysSinceCreation = (System.currentTimeMillis() - it.createdAt.time) / (1000 * 60 * 60 * 24)
                daysSinceCreation <= 7
            }
            MemorialFilter.POPULAR -> filtered.sortedByDescending { it.prayerCount }.take(20)
        }

        // Apply search
        if (query.isNotEmpty()) {
            val searchTerm = query.lowercase()
            filtered = filtered.filter { memorial ->
                memorial.deceasedName.lowercase().contains(searchTerm) ||
                memorial.deceasedNameArabic?.lowercase()?.contains(searchTerm) == true ||
                memorial.memorialMessage.lowercase().contains(searchTerm) ||
                memorial.memorialMessageArabic?.lowercase()?.contains(searchTerm) == true ||
                memorial.tags.any { it.lowercase().contains(searchTerm) }
            }
        }

        return filtered
    }

    private fun isExpired(expiresAt: java.util.Date): Boolean {
        return expiresAt.time < System.currentTimeMillis()
    }

    private suspend fun shareMemorialLink(memorial: MemorialData) {
        // Generate shareable link for memorial
        // This could use Firebase Dynamic Links
    }

    private suspend fun shareMemorialText(memorial: MemorialData, customMessage: String) {
        // Create formatted text for sharing
    }

    private suspend fun shareMemorialImage(memorial: MemorialData) {
        // Generate memorial image with photo and details
    }

    private suspend fun shareToWhatsApp(memorial: MemorialData) {
        // Format for WhatsApp sharing
    }

    private suspend fun shareToFacebook(memorial: MemorialData) {
        // Format for Facebook sharing
    }

    private suspend fun shareViaEmail(memorial: MemorialData, content: String) {
        // Format for email sharing
    }
}

data class MemorialListUiState(
    val memorials: List<MemorialData> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null
)

enum class MemorialFilter(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    PRIVATE("Private"),
    FAMILY("Family"),
    COMMUNITY("Community"),
    PUBLIC("Public"),
    RECENT("Recent"),
    POPULAR("Popular")
}

enum class ShareType {
    LINK,
    TEXT,
    IMAGE,
    WHATSAPP,
    FACEBOOK,
    EMAIL
}