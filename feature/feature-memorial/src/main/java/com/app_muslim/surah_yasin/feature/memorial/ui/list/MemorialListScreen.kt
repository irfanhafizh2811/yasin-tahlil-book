package com.app_muslim.surah_yasin.feature.memorial.ui.list

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.text.input.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.*
import com.app_muslim.surah_yasin.feature.memorial.ui.list.components.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
// Simple refresh without pull-to-refresh for now
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun MemorialListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MemorialListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    
    var isSearchActive by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<MemorialData?>(null) }
    var showShareBottomSheet by remember { mutableStateOf<MemorialData?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMemorials()
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchAppBar(
                    query = searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onSearchClose = {
                        isSearchActive = false
                        viewModel.clearSearch()
                    },
                    onSearch = { viewModel.performSearch() }
                )
            } else {
                MemorialListTopBar(
                    title = "My Memorials",
                    onNavigateBack = onNavigateBack,
                    onSearchClick = { isSearchActive = true },
                    onFilterClick = { /* Show filter bottom sheet */ }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Memorial"
                    )
                },
                text = { Text("Create Memorial") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            // Filter chips
            if (selectedFilter != MemorialFilter.ALL || isSearchActive) {
                FilterChipRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = viewModel::updateFilter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Memorial list content
            MemorialListContent(
                uiState = uiState,
                onRefresh = viewModel::refreshMemorials,
                onMemorialClick = onNavigateToDetail,
                onMemorialEdit = onNavigateToEdit,
                onMemorialDelete = { showDeleteDialog = it },
                onMemorialShare = { showShareBottomSheet = it },
                onLoadMore = viewModel::loadMoreMemorials,
                modifier = Modifier.weight(1f)
            )
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { memorial ->
        DeleteMemorialDialog(
            memorial = memorial,
            onConfirm = { 
                viewModel.deleteMemorial(memorial.id)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
    
    // Share bottom sheet
    showShareBottomSheet?.let { memorial ->
        MemorialShareBottomSheet(
            memorial = memorial,
            onShare = { shareType, content ->
                viewModel.shareMemorial(memorial, shareType, content)
                showShareBottomSheet = null
            },
            onDismiss = { showShareBottomSheet = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemorialListTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Memorials"
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter Memorials"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSearchClose) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Close Search"
                )
            }
            
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { 
                    Text(
                        "Search memorials...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun FilterChipRow(
    selectedFilter: MemorialFilter,
    onFilterSelected: (MemorialFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(MemorialFilter.values()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun MemorialListContent(
    uiState: MemorialListUiState,
    onRefresh: () -> Unit,
    onMemorialClick: (String) -> Unit,
    onMemorialEdit: (String) -> Unit,
    onMemorialDelete: (MemorialData) -> Unit,
    onMemorialShare: (MemorialData) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            uiState.isLoading && uiState.memorials.isEmpty() -> {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
            
            uiState.error != null && uiState.memorials.isEmpty() -> {
                ErrorState(
                    error = uiState.error,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            uiState.memorials.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.memorials,
                        key = { it.id }
                    ) { memorial ->
                        MemorialListItem(
                            memorial = memorial,
                            onClick = { onMemorialClick(memorial.id) },
                            onEdit = { onMemorialEdit(memorial.id) },
                            onDelete = { onMemorialDelete(memorial) },
                            onShare = { onMemorialShare(memorial) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Load more indicator
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                    
                    // Trigger load more when reaching the end
                    if (uiState.hasMoreData && !uiState.isLoadingMore) {
                        item {
                            LaunchedEffect(Unit) {
                                onLoadMore()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemorialListItem(
    memorial: MemorialData,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with photo and basic info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Memorial photo
                    Card(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        if (memorial.photoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(memorial.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Memorial Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Memorial Icon",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Name and dates
                    Column {
                        Text(
                            text = memorial.deceasedName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (memorial.deceasedNameArabic?.isNotEmpty() == true) {
                            Text(
                                text = memorial.deceasedNameArabic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Serif // Arabic font would be better
                            )
                        }
                        
                        Text(
                            text = formatDateOfDeath(memorial.dateOfDeath),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Menu button
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Memorial Options"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                showMenu = false
                                onShare()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Delete",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Memorial message preview
            if (memorial.memorialMessage.isNotEmpty()) {
                Text(
                    text = memorial.memorialMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Stats and info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Prayer stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Prayer Count",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${memorial.prayerCount} prayers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Participant Count",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${memorial.participantCount} participants",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Privacy and expiry info
                Column(horizontalAlignment = Alignment.End) {
                    PrivacyChip(privacyLevel = memorial.privacyLevel)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    memorial.expiresAt?.let { expiryDate ->
                        Text(
                            text = formatExpiryDate(expiryDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isExpiringSoon(expiryDate)) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            }
                        )
                    } ?: Text(
                        text = "Permanent memorial",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyChip(privacyLevel: PrivacyLevel) {
    val (icon, color) = when (privacyLevel) {
        PrivacyLevel.PRIVATE -> Icons.Default.Lock to MaterialTheme.colorScheme.error
        PrivacyLevel.FAMILY -> Icons.Default.Group to MaterialTheme.colorScheme.tertiary
        PrivacyLevel.COMMUNITY -> Icons.Default.LocationOn to MaterialTheme.colorScheme.secondary
        PrivacyLevel.PUBLIC -> Icons.Default.Public to MaterialTheme.colorScheme.primary
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = privacyLevel.displayName,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = privacyLevel.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = "Loading memorials...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "Unable to load memorials",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "No Memorials",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            
            Text(
                text = "No memorials yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Create your first memorial to honor\nyour loved ones with prayers",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions
private fun formatDateOfDeath(date: Date): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return "Passed away ${formatter.format(date)}"
}

private fun formatExpiryDate(date: Date): String {
    val now = Date()
    val diffInMs = date.time - now.time
    val diffInDays = (diffInMs / (1000 * 60 * 60 * 24)).toInt()
    
    return when {
        diffInDays < 0 -> "Expired"
        diffInDays == 0 -> "Expires today"
        diffInDays == 1 -> "Expires tomorrow"
        diffInDays <= 7 -> "Expires in $diffInDays days"
        else -> {
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            "Expires ${formatter.format(date)}"
        }
    }
}

private fun isExpiringSoon(date: Date): Boolean {
    val now = Date()
    val diffInMs = date.time - now.time
    val diffInDays = (diffInMs / (1000 * 60 * 60 * 24)).toInt()
    return diffInDays <= 3
}