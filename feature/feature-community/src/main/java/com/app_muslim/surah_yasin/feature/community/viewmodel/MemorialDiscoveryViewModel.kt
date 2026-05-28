package com.app_muslim.surah_yasin.feature.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.repository.MemorialDiscoveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Memorial Discovery Screen
 * Manages memorial search, filtering, and discovery features
 */
@HiltViewModel
class MemorialDiscoveryViewModel @Inject constructor(
    private val discoveryRepository: MemorialDiscoveryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MemorialDiscoveryUiState())
    val uiState: StateFlow<MemorialDiscoveryUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _currentFilter = MutableStateFlow(MemorialDiscoveryFilter())
    val currentFilter: StateFlow<MemorialDiscoveryFilter> = _currentFilter.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = 20
    
    init {
        // Observe search query changes
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    updateFilter(_currentFilter.value.copy(searchQuery = query))
                    performSearch()
                }
        }
        
        // Load initial data
        loadFeaturedMemorials()
    }
    
    fun refreshDiscovery() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Refresh featured memorials
                loadFeaturedMemorials()
                
                // Refresh search results
                performSearch()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to refresh discovery: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateFilter(filter: MemorialDiscoveryFilter) {
        _currentFilter.value = filter
        currentPage = 0
        performSearch()
    }
    
    fun applyQuickFilter(categoryId: String) {
        viewModelScope.launch {
            val newFilter = when (categoryId) {
                "featured" -> _currentFilter.value.copy(
                    sortType = MemorialSortType.MOST_PRAYERS,
                    minPrayerCount = 5
                )
                "trending" -> _currentFilter.value.copy(
                    sortType = MemorialSortType.MOST_ACTIVE,
                    hasActivePrayers = true
                )
                "recent" -> _currentFilter.value.copy(
                    sortType = MemorialSortType.MOST_RECENT
                )
                "active" -> _currentFilter.value.copy(
                    sortType = MemorialSortType.MOST_PRAYERS,
                    hasActivePrayers = true
                )
                "regional" -> {
                    // Get user's region from preferences or location
                    val userRegion = getUserRegion()
                    _currentFilter.value.copy(
                        region = userRegion,
                        sortType = MemorialSortType.MOST_RECENT
                    )
                }
                else -> _currentFilter.value
            }
            
            updateFilter(newFilter)
        }
    }
    
    fun clearFilters() {
        updateFilter(MemorialDiscoveryFilter())
    }
    
    fun exploreAll() {
        updateFilter(MemorialDiscoveryFilter(sortType = MemorialSortType.MOST_ACTIVE))
    }
    
    fun loadMoreResults() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreResults) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            
            try {
                currentPage++
                
                discoveryRepository.discoverMemorials(
                    filter = _currentFilter.value,
                    limit = pageSize
                ).take(1).collect { newMemorials ->
                    val currentMemorials = _uiState.value.memorials
                    val updatedMemorials = currentMemorials + newMemorials
                    
                    _uiState.value = _uiState.value.copy(
                        memorials = updatedMemorials,
                        hasMoreResults = newMemorials.size == pageSize,
                        isLoadingMore = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    errorMessage = "Failed to load more results: ${e.message}"
                )
            }
        }
    }
    
    fun trackMemorialInteraction(memorialId: String, action: String) {
        viewModelScope.launch {
            discoveryRepository.trackMemorialDiscovery(
                memorialId = memorialId,
                action = action,
                searchQuery = _searchQuery.value.takeIf { it.isNotBlank() },
                filter = _currentFilter.value.takeIf { hasActiveFilters(_currentFilter.value) }
            )
        }
    }
    
    fun getPersonalizedSuggestions() {
        viewModelScope.launch {
            try {
                val userRegion = getUserRegion()
                val userLanguage = getUserLanguage()
                
                val result = discoveryRepository.getPersonalizedSuggestions(
                    userRegion = userRegion,
                    userLanguage = userLanguage,
                    limit = 10
                )
                
                result.fold(
                    onSuccess = { suggestions ->
                        // Add suggestions to main memorials list
                        _uiState.value = _uiState.value.copy(
                            memorials = _uiState.value.memorials + suggestions
                        )
                    },
                    onFailure = { error ->
                        // Handle error silently for suggestions
                    }
                )
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
    
    private fun performSearch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                currentPage = 0
                
                discoveryRepository.discoverMemorials(
                    filter = _currentFilter.value,
                    limit = pageSize
                ).take(1).collect { memorials ->
                    _uiState.value = _uiState.value.copy(
                        memorials = memorials,
                        hasMoreResults = memorials.size == pageSize,
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Search failed: ${e.message}"
                )
            }
        }
    }
    
    private fun loadFeaturedMemorials() {
        viewModelScope.launch {
            try {
                discoveryRepository.getFeaturedMemorials(limit = 8)
                    .take(1)
                    .collect { featured ->
                        // Featured memorials handled in UI layer
                        // Could store in separate state if needed
                    }
            } catch (e: Exception) {
                // Handle featured loading error silently
            }
        }
    }
    
    private fun loadTrendingMemorials() {
        viewModelScope.launch {
            try {
                discoveryRepository.getTrendingMemorials(limit = 6)
                    .take(1)
                    .collect { trending ->
                        // Trending memorials handled in UI layer
                        // Could store in separate state if needed
                    }
            } catch (e: Exception) {
                // Handle trending loading error silently
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Helper methods - would normally get from user preferences or profile service
    private fun getUserRegion(): String {
        // TODO: Implement user region detection from profile/preferences
        return "Middle East" // Default region
    }
    
    private fun getUserLanguage(): String {
        // TODO: Implement user language detection from preferences
        return "en" // Default language
    }
    
    private fun hasActiveFilters(filter: MemorialDiscoveryFilter): Boolean {
        return filter.region != null ||
                filter.prayerType != null ||
                filter.privacyLevel != null ||
                filter.dateRange != null ||
                filter.hasActivePrayers != null ||
                filter.minPrayerCount != null ||
                filter.sortType != MemorialSortType.MOST_RECENT
    }
}

/**
 * UI State for Memorial Discovery Screen
 * Uses the consolidated MemorialDiscoveryUiState from CommunityModels.kt
 */