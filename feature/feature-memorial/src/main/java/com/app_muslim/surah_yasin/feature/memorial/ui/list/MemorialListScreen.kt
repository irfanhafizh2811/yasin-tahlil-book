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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.*
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialListUiState
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialFilter
import com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.ShareType
import com.app_muslim.surah_yasin.feature.memorial.ui.list.components.*
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
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
            onShare = { shareType: ShareType, content ->
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

// Preview Data Providers
class MemorialListStateProvider : PreviewParameterProvider<MemorialListUiState> {
    override val values: Sequence<MemorialListUiState> = sequenceOf(
        // Loading state
        MemorialListUiState(isLoading = true),
        
        // Empty state
        MemorialListUiState(memorials = emptyList()),
        
        // Error state
        MemorialListUiState(
            error = "Unable to connect to the server. Please check your internet connection.",
            memorials = emptyList()
        ),
        
        // Populated state
        MemorialListUiState(
            memorials = getSampleMemorials()
        ),
        
        // Loading more state
        MemorialListUiState(
            memorials = getSampleMemorials(),
            isLoadingMore = true,
            hasMoreData = true
        )
    )
}

class SingleMemorialProvider : PreviewParameterProvider<MemorialData> {
    override val values: Sequence<MemorialData> = sequenceOf(
        // Standard memorial
        MemorialData(
            id = "1",
            deceasedName = "Ahmed Hassan",
            deceasedNameArabic = "أحمد حسن",
            memorialMessage = "A beloved father and devoted servant of Allah who dedicated his life to helping others.",
            dateOfDeath = Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)), // 30 days ago
            privacyLevel = PrivacyLevel.FAMILY,
            prayerCount = 245,
            participantCount = 23,
            photoUrl = null
        ),
        
        // Memorial with photo
        MemorialData(
            id = "2",
            deceasedName = "Fatimah Al-Zahra",
            deceasedNameArabic = "فاطمة الزهراء",
            memorialMessage = "A devoted mother and wife who exemplified Islamic values throughout her life.",
            dateOfDeath = Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)), // 7 days ago
            privacyLevel = PrivacyLevel.COMMUNITY,
            prayerCount = 892,
            participantCount = 156,
            photoUrl = "sample_photo_url",
            expiresAt = Date(System.currentTimeMillis() + (2L * 24 * 60 * 60 * 1000)) // Expires in 2 days
        ),
        
        // Public memorial with high engagement
        MemorialData(
            id = "3",
            deceasedName = "Muhammad Ali",
            deceasedNameArabic = "محمد علي",
            memorialMessage = "A respected community leader and Islamic scholar who touched countless lives.",
            dateOfDeath = Date(System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)), // 90 days ago
            privacyLevel = PrivacyLevel.PUBLIC,
            prayerCount = 5420,
            participantCount = 1250,
            photoUrl = "sample_photo_url"
        )
    )
}

// Mock data functions
private fun getSampleMemorials(): List<MemorialData> {
    return listOf(
        MemorialData(
            id = "1",
            deceasedName = "Ali ibn Abi Talib",
            deceasedNameArabic = "علي بن أبي طالب",
            memorialMessage = "The fourth Caliph and cousin of Prophet Muhammad, known for his wisdom and courage.",
            dateOfDeath = Date(System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000)),
            privacyLevel = PrivacyLevel.PUBLIC,
            prayerCount = 1250,
            participantCount = 300,
            photoUrl = null
        ),
        MemorialData(
            id = "2",
            deceasedName = "Khadijah bint Khuwaylid",
            deceasedNameArabic = "خديجة بنت خويلد",
            memorialMessage = "The first wife of Prophet Muhammad and the first person to accept Islam.",
            dateOfDeath = Date(System.currentTimeMillis() - (45L * 24 * 60 * 60 * 1000)),
            privacyLevel = PrivacyLevel.COMMUNITY,
            prayerCount = 2100,
            participantCount = 520,
            photoUrl = "sample_photo",
            expiresAt = Date(System.currentTimeMillis() + (5L * 24 * 60 * 60 * 1000))
        ),
        MemorialData(
            id = "3",
            deceasedName = "Abu Bakr As-Siddiq",
            deceasedNameArabic = "أبو بكر الصديق",
            memorialMessage = "The first Caliph and closest companion of Prophet Muhammad.",
            dateOfDeath = Date(System.currentTimeMillis() - (120L * 24 * 60 * 60 * 1000)),
            privacyLevel = PrivacyLevel.FAMILY,
            prayerCount = 756,
            participantCount = 89,
            photoUrl = null
        )
    )
}

// Mock data for previews using the actual model types
private fun getSampleMemorialListUiState(): com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialListUiState {
    return com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialListUiState(
        memorials = getSampleMemorialData(),
        isLoading = false,
        isLoadingMore = false,
        hasMoreData = true,
        error = null
    )
}

private fun getSampleMemorialData(): List<MemorialData> {
    return listOf(
        MemorialData(
            id = "mem_001",
            creatorId = "user_001",
            creatorName = "Ahmad Hassan",
            deceasedName = "Ali ibn Abi Talib",
            deceasedNameArabic = "علي بن أبي طالب",
            memorialMessage = "The fourth Caliph and cousin of Prophet Muhammad (PBUH), known for his wisdom, courage, and dedication to justice.",
            memorialMessageArabic = "الخليفة الرابع وابن عم النبي محمد صلى الله عليه وسلم، المعروف بحكمته وشجاعته وتفانيه في العدالة.",
            dateOfDeath = Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)),
            dateOfDeathHijri = HijriDate(),
            photoUrl = null,
            privacyLevel = PrivacyLevel.COMMUNITY,
            prayerType = PrayerType.YASIN,
            createdAt = Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)),
            isActive = true
        ),
        MemorialData(
            id = "mem_002", 
            creatorId = "user_001",
            creatorName = "Ahmad Hassan",
            deceasedName = "Fatimah bint Muhammad",
            deceasedNameArabic = "فاطمة بنت محمد",
            memorialMessage = "Beloved daughter of Prophet Muhammad (PBUH), the mother of Hassan and Hussain.",
            memorialMessageArabic = "ابنة النبي محمد الحبيبة صلى الله عليه وسلم، أم الحسن والحسين.",
            dateOfDeath = Date(System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000)),
            dateOfDeathHijri = HijriDate(),
            photoUrl = null,
            privacyLevel = PrivacyLevel.FAMILY,
            prayerType = PrayerType.TAHLIL,
            createdAt = Date(System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000)),
            isActive = true
        ),
        MemorialData(
            id = "mem_003",
            creatorId = "user_001",
            creatorName = "Ahmad Hassan", 
            deceasedName = "Hassan al-Basri",
            deceasedNameArabic = "الحسن البصري",
            memorialMessage = "Great Islamic scholar, ascetic, and one of the most prominent Tabi'un.",
            memorialMessageArabic = "عالم إسلامي عظيم، زاهد، وأحد أبرز التابعين.",
            dateOfDeath = Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)),
            dateOfDeathHijri = HijriDate(),
            photoUrl = null,
            privacyLevel = PrivacyLevel.PUBLIC,
            prayerType = PrayerType.DUA,
            createdAt = Date(System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)),
            isActive = true
        )
    )
}

// Mock components that would normally require ViewModels
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialListScreenPreview(
    uiState: com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialListUiState = getSampleMemorialListUiState(),
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    selectedFilter: com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialFilter = com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialFilter.ALL
) {
    var showDeleteDialog by remember { mutableStateOf<MemorialData?>(null) }
    var showShareBottomSheet by remember { mutableStateOf<MemorialData?>(null) }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchAppBar(
                    query = searchQuery,
                    onQueryChange = { },
                    onSearchClose = { },
                    onSearch = { }
                )
            } else {
                MemorialListTopBar(
                    title = "My Memorials",
                    onNavigateBack = { },
                    onSearchClick = { },
                    onFilterClick = { }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            // Filter chips
            if (selectedFilter != com.app_muslim.surah_yasin.feature.memorial.ui.list.viewmodel.MemorialFilter.ALL || isSearchActive) {
                FilterChipRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Memorial list content
            MemorialListContent(
                uiState = uiState,
                onRefresh = { },
                onMemorialClick = { },
                onMemorialEdit = { },
                onMemorialDelete = { showDeleteDialog = it },
                onMemorialShare = { showShareBottomSheet = it },
                onLoadMore = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { memorial ->
        DeleteMemorialDialog(
            memorial = memorial,
            onConfirm = { showDeleteDialog = null },
            onDismiss = { showDeleteDialog = null }
        )
    }
    
    // Share bottom sheet
    showShareBottomSheet?.let { memorial ->
        MemorialShareBottomSheet(
            memorial = memorial,
            onShare = { _, _ -> showShareBottomSheet = null },
            onDismiss = { showShareBottomSheet = null }
        )
    }
}

// Placeholder components for dialogs/bottom sheets
@Composable
fun DeleteMemorialDialog(
    memorial: MemorialData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Memorial") },
        text = { Text("Are you sure you want to delete the memorial for ${memorial.deceasedName}?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialShareBottomSheet(
    memorial: MemorialData,
    onShare: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheetDefaults.ExpandedShape
    // Simplified bottom sheet content for preview
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Share Memorial", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Share ${memorial.deceasedName}'s memorial with family and friends.")
        }
    }
}

// Preview Functions
@Preview(name = "Memorial List Screen - Loading")
@Composable
fun PreviewMemorialListScreenLoading() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(isLoading = true)
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Empty")
@Composable
fun PreviewMemorialListScreenEmpty() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(memorials = emptyList())
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Error")
@Composable
fun PreviewMemorialListScreenError() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(
                    error = "Unable to load memorials. Please check your internet connection.",
                    memorials = emptyList()
                )
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Populated")
@Composable
fun PreviewMemorialListScreenPopulated() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(memorials = getSampleMemorials())
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Search Mode")
@Composable
fun PreviewMemorialListScreenSearch() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(memorials = getSampleMemorials()),
                isSearchActive = true,
                searchQuery = "Ali"
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Dynamic States", group = "Dynamic")
@Composable
fun PreviewMemorialListScreenDynamic(
    @PreviewParameter(MemorialListStateProvider::class) uiState: MemorialListUiState
) {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(uiState = uiState)
        }
    }
}

@Preview(name = "Memorial List Screen - Dark Theme")
@Composable
fun PreviewMemorialListScreenDark() {
    TahlilTheme(darkTheme = true) {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(memorials = getSampleMemorials())
            )
        }
    }
}

@Preview(name = "Memorial List Screen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewMemorialListScreenTablet() {
    TahlilTheme {
        Surface {
            MemorialListScreenPreview(
                uiState = MemorialListUiState(memorials = getSampleMemorials())
            )
        }
    }
}

@Preview(name = "Memorial List Item - Standard")
@Composable
fun PreviewMemorialListItem() {
    TahlilTheme {
        Surface {
            MemorialListItem(
                memorial = MemorialData(
                    id = "1",
                    deceasedName = "Omar ibn al-Khattab",
                    deceasedNameArabic = "عمر بن الخطاب",
                    memorialMessage = "The second Caliph, known for his justice and expansion of the Islamic empire.",
                    dateOfDeath = Date(),
                    privacyLevel = PrivacyLevel.COMMUNITY,
                    prayerCount = 456,
                    participantCount = 78
                ),
                onClick = { },
                onEdit = { },
                onDelete = { },
                onShare = { }
            )
        }
    }
}

@Preview(name = "Memorial List Item - With Photo")
@Composable
fun PreviewMemorialListItemWithPhoto() {
    TahlilTheme {
        Surface {
            MemorialListItem(
                memorial = MemorialData(
                    id = "2",
                    deceasedName = "Aisha bint Abu Bakr",
                    deceasedNameArabic = "عائشة بنت أبي بكر",
                    memorialMessage = "The beloved wife of Prophet Muhammad and a scholar of Islam.",
                    dateOfDeath = Date(),
                    privacyLevel = PrivacyLevel.PUBLIC,
                    prayerCount = 1250,
                    participantCount = 320,
                    photoUrl = "sample_photo",
                    expiresAt = Date(System.currentTimeMillis() + (2L * 24 * 60 * 60 * 1000))
                ),
                onClick = { },
                onEdit = { },
                onDelete = { },
                onShare = { }
            )
        }
    }
}

@Preview(name = "Memorial List Item - Dynamic", group = "Dynamic")
@Composable
fun PreviewMemorialListItemDynamic(
    @PreviewParameter(SingleMemorialProvider::class) memorial: MemorialData
) {
    TahlilTheme {
        Surface {
            MemorialListItem(
                memorial = memorial,
                onClick = { },
                onEdit = { },
                onDelete = { },
                onShare = { }
            )
        }
    }
}

@Preview(name = "Search App Bar")
@Composable
fun PreviewSearchAppBar() {
    TahlilTheme {
        Surface {
            SearchAppBar(
                query = "Ali",
                onQueryChange = { },
                onSearchClose = { },
                onSearch = { }
            )
        }
    }
}

@Preview(name = "Filter Chip Row")
@Composable
fun PreviewFilterChipRow() {
    TahlilTheme {
        Surface {
            FilterChipRow(
                selectedFilter = MemorialFilter.COMMUNITY,
                onFilterSelected = { }
            )
        }
    }
}

@Preview(name = "Privacy Chips")
@Composable
fun PreviewPrivacyChips() {
    TahlilTheme {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyLevel.entries.forEach { level ->
                    PrivacyChip(privacyLevel = level)
                }
            }
        }
    }
}

@Preview(name = "Loading State")
@Composable
fun PreviewLoadingState() {
    TahlilTheme {
        Surface {
            LoadingState(modifier = Modifier.size(300.dp))
        }
    }
}

@Preview(name = "Error State")
@Composable
fun PreviewErrorState() {
    TahlilTheme {
        Surface {
            ErrorState(
                error = "Network connection failed. Please try again.",
                onRetry = { },
                modifier = Modifier.size(300.dp)
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
fun PreviewEmptyState() {
    TahlilTheme {
        Surface {
            EmptyState(modifier = Modifier.size(300.dp))
        }
    }
}