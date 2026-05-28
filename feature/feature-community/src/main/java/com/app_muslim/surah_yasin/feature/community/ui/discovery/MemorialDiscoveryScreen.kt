package com.app_muslim.surah_yasin.feature.community.ui.discovery

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.viewmodel.MemorialDiscoveryViewModel
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Memorial Discovery Screen - Compose Implementation
 * Advanced search and filtering for community memorial discovery
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialDiscoveryScreen(
    onNavigateToMemorial: (String) -> Unit,
    onNavigateToFilter: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MemorialDiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.refreshDiscovery()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar with Search
        MemorialDiscoveryTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onNavigateBack = onNavigateBack,
            onNavigateToFilter = onNavigateToFilter,
            hasActiveFilters = currentFilter.hasActiveFilters()
        )
        
        // Discovery Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Quick Categories
                DiscoveryQuickCategories(
                    onCategorySelected = { categoryId ->
                        viewModel.applyQuickFilter(categoryId)
                    }
                )
            }
            
            item {
                // Featured Memorials - for now showing from main memorials list
                val featuredMemorials = uiState.memorials.take(5)
                if (featuredMemorials.isNotEmpty()) {
                    FeaturedMemorialsSection(
                        memorials = featuredMemorials,
                        onMemorialSelected = onNavigateToMemorial
                    )
                }
            }
            
            item {
                // Search Results Header
                DiscoveryResultsHeader(
                    resultCount = uiState.memorials.size,
                    isLoading = uiState.isLoading,
                    currentFilter = currentFilter
                )
            }
            
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.memorials.isEmpty()) {
                item {
                    DiscoveryEmptyState(
                        hasSearchQuery = searchQuery.isNotBlank(),
                        hasActiveFilters = currentFilter.hasActiveFilters(),
                        onClearFilters = viewModel::clearFilters,
                        onExploreAll = viewModel::exploreAll
                    )
                }
            } else {
                // Memorial Grid
                items(uiState.memorials) { memorial ->
                    DiscoverableMemorialCard(
                        memorial = memorial,
                        onMemorialClick = { onNavigateToMemorial(memorial.memorialId) },
                        onJoinClick = { 
                            viewModel.trackMemorialInteraction(memorial.memorialId, "join_clicked")
                            onNavigateToMemorial(memorial.memorialId) 
                        }
                    )
                }
                
                if (uiState.hasMoreResults) {
                    item {
                        LoadMoreButton(
                            isLoading = uiState.isLoadingMore,
                            onLoadMore = viewModel::loadMoreResults
                        )
                    }
                }
            }
        }
    }
    
    // Error Handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemorialDiscoveryTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToFilter: () -> Unit,
    hasActiveFilters: Boolean
) {
    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Discover Memorials",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToFilter) {
                    Badge(
                        modifier = Modifier.size(if (hasActiveFilters) 20.dp else 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = {
                Text("Search memorials, names, regions...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

data class DiscoveryCategory(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun DiscoveryQuickCategories(
    onCategorySelected: (String) -> Unit
) {
    val categories = remember {
        listOf(
            DiscoveryCategory("featured", "Featured", Icons.Default.Star, Color(0xFFFFD700)),
            DiscoveryCategory("trending", "Trending", Icons.Default.TrendingUp, Color(0xFF4CAF50)),
            DiscoveryCategory("recent", "Recent", Icons.Default.Schedule, Color(0xFF2196F3)),
            DiscoveryCategory("active", "Most Active", Icons.Default.Group, Color(0xFF9C27B0)),
            DiscoveryCategory("regional", "My Region", Icons.Default.LocationOn, Color(0xFFFF5722))
        )
    }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            QuickCategoryChip(
                category = category,
                onSelected = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun QuickCategoryChip(
    category: DiscoveryCategory,
    onSelected: () -> Unit
) {
    FilterChip(
        onClick = onSelected,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = category.color
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        selected = false
    )
}

@Composable
private fun FeaturedMemorialsSection(
    memorials: List<DiscoverableMemorial>,
    onMemorialSelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ Featured Memorials",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "${memorials.size} featured",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(memorials) { memorial ->
                FeaturedMemorialCard(
                    memorial = memorial,
                    onMemorialClick = { onMemorialSelected(memorial.memorialId) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedMemorialCard(
    memorial: DiscoverableMemorial,
    onMemorialClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
            .clickable { onMemorialClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            memorial.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Featured",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "${memorial.participantCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
                
                Column {
                    Text(
                        text = memorial.deceasedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = memorial.region,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoveryResultsHeader(
    resultCount: Int,
    isLoading: Boolean,
    currentFilter: MemorialDiscoveryFilter
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isLoading) "Searching..." else "$resultCount memorials found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            if (currentFilter.hasActiveFilters()) {
                Text(
                    text = getFilterSummary(currentFilter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DiscoverableMemorialCard(
    memorial: DiscoverableMemorial,
    onMemorialClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMemorialClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = memorial.deceasedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    memorial.deceasedNameArabic?.let { arabicName ->
                        Text(
                            text = arabicName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = memorial.privacyLevel.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            memorial.description?.let { description ->
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Memorial Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MemorialStatItem(
                        icon = Icons.Default.Group,
                        value = "${memorial.participantCount}",
                        label = "Participants"
                    )
                    
                    MemorialStatItem(
                        icon = Icons.Default.Favorite,
                        value = "${memorial.totalPrayers}",
                        label = "Prayers"
                    )
                    
                    MemorialStatItem(
                        icon = Icons.Default.LocationOn,
                        value = memorial.region,
                        label = ""
                    )
                }
                
                Button(
                    onClick = onJoinClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Join")
                }
            }
            
            // Time Information
            Text(
                text = "Created ${formatRelativeTime(memorial.createdAt)}${memorial.lastPrayerAt?.let { " • Last prayer ${formatRelativeTime(it)}" } ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun MemorialStatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (label.isNotEmpty()) "$value $label" else value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DiscoveryEmptyState(
    hasSearchQuery: Boolean,
    hasActiveFilters: Boolean,
    onClearFilters: () -> Unit,
    onExploreAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (hasSearchQuery) Icons.Default.SearchOff else Icons.Default.Explore,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when {
                hasSearchQuery -> "No memorials found for your search"
                hasActiveFilters -> "No memorials match your filters"
                else -> "No memorials to discover"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when {
                hasSearchQuery -> "Try adjusting your search terms or explore different categories"
                hasActiveFilters -> "Try removing some filters to see more results"
                else -> "Be the first to create a memorial in your community"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (hasActiveFilters) {
            OutlinedButton(onClick = onClearFilters) {
                Text("Clear Filters")
            }
        } else {
            Button(onClick = onExploreAll) {
                Text("Explore All Memorials")
            }
        }
    }
}

@Composable
private fun LoadMoreButton(
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            TextButton(onClick = onLoadMore) {
                Text("Load More")
            }
        }
    }
}

// Helper Functions
private fun getPrayerTypeColor(prayerType: CommunityPrayerType): Color {
    return when (prayerType) {
        CommunityPrayerType.TAHLIL -> Color(0xFF4CAF50)
        CommunityPrayerType.YASIN -> Color(0xFF2196F3)
        CommunityPrayerType.FATIHAH -> Color(0xFF9C27B0)
        CommunityPrayerType.DHIKR -> Color(0xFF3F51B5)
        CommunityPrayerType.DUA -> Color(0xFFFF9800)
        CommunityPrayerType.QURAN -> Color(0xFF607D8B)
        CommunityPrayerType.COMMUNITY_PRAYER -> Color(0xFF795548)
    }
}

private fun formatRelativeTime(dateTime: java.time.ZonedDateTime): String {
    val now = java.time.ZonedDateTime.now()
    val duration = java.time.Duration.between(dateTime, now)
    
    return when {
        duration.toDays() > 7 -> "${duration.toDays() / 7} weeks ago"
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "Just now"
    }
}

private fun getFilterSummary(filter: MemorialDiscoveryFilter): String {
    val parts = mutableListOf<String>()
    
    filter.region?.let { parts.add("region: $it") }
    filter.prayerType?.let { parts.add("prayer: ${it.name}") }
    filter.privacyLevel?.let { parts.add("privacy: ${it.name}") }
    filter.dateRange?.let { parts.add("date range") }
    filter.minPrayerCount?.let { parts.add("min prayers: $it") }
    if (filter.hasActivePrayers == true) { parts.add("active prayers") }
    
    return if (parts.isNotEmpty()) "Filtered by: ${parts.joinToString(", ")}" else ""
}

private fun MemorialDiscoveryFilter.hasActiveFilters(): Boolean {
    return region != null ||
            prayerType != null ||
            privacyLevel != null ||
            dateRange != null ||
            hasActivePrayers != null ||
            minPrayerCount != null ||
            sortType != MemorialSortType.MOST_RECENT
}

// Supporting Data Classes
// (DiscoveryCategory already defined above)