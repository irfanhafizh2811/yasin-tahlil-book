package com.app_muslim.surah_yasin.ui.memorial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.memorial.model.Memorial

@Composable
fun MemorialScreen(
    memorials: List<Memorial>,
    isLoading: Boolean,
    onCreateMemorial: () -> Unit,
    onMemorialClick: (Memorial) -> Unit,
    onPrayForMemorial: (Memorial) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Search memorials...") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.semantics {
                                contentDescription = "Search memorials"
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {},
                    modifier = Modifier.semantics {
                        contentDescription = "Clear search"
                    }
                ) {
                    Text("✕")
                }
            }

            // Content
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = "Loading memorials" },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (memorials.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "No memorials yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Create your first memorial",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(memorials) { memorial ->
                        MemorialCard(
                            memorial = memorial,
                            onMemorialClick = onMemorialClick,
                            onPrayClick = onPrayForMemorial
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onCreateMemorial,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .semantics {
                    contentDescription = "Create memorial"
                }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create memorial")
        }
    }
}

@Composable
fun MemorialCard(
    memorial: Memorial,
    onMemorialClick: (Memorial) -> Unit,
    onPrayClick: (Memorial) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Memorial card" },
        onClick = { onMemorialClick(memorial) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = memorial.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = memorial.deceasedName,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${memorial.totalPrayers} prayers",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = { onPrayClick(memorial) },
                    modifier = Modifier.semantics {
                        contentDescription = "Pray for ${memorial.deceasedName}"
                    }
                ) {
                    Text("Pray")
                }
            }
        }
    }
}

@Composable
fun MemorialDetailScreen(
    memorial: Memorial,
    onPrayerIncrement: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.semantics {
                contentDescription = "Navigate back"
            }
        ) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = memorial.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = memorial.deceasedName,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        val displayText = if (memorial.totalPrayers == 1) "prayer" else "prayers"
        Text(
            text = "${memorial.totalPrayers} $displayText",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPrayerIncrement,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Pray for ${memorial.deceasedName}"
                }
        ) {
            Text("Pray for ${memorial.deceasedName}")
        }
    }
}

@Composable
fun TabletMemorialScreen(
    memorials: List<Memorial> = emptyList(),
    isLoading: Boolean = false,
    onCreateMemorial: () -> Unit = {},
    onMemorialClick: (Memorial) -> Unit = {},
    onPrayForMemorial: (Memorial) -> Unit = {}
) {
    // Tablet-optimized layout with dual pane
    Row(modifier = Modifier.fillMaxSize()) {
        // Left pane - Memorial list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            MemorialScreen(
                memorials = memorials,
                isLoading = isLoading,
                onCreateMemorial = onCreateMemorial,
                onMemorialClick = onMemorialClick,
                onPrayForMemorial = onPrayForMemorial
            )
        }
        
        // Right pane - Detail view (for tablet layout)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 8.dp)
        ) {
            // Placeholder for detail view
            Card(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Select a memorial to view details",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ArabicTextDisplay(
    text: String,
    transliteration: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = transliteration,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}