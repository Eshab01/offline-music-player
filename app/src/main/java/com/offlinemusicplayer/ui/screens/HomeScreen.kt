package com.offlinemusicplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.offlinemusicplayer.ui.components.TrackItem
import com.offlinemusicplayer.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val mostPlayed by viewModel.mostPlayed.collectAsState()
    val recentlyAdded by viewModel.recentlyAdded.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TopAppBar(
            title = { Text("Home") },
            actions = {
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { navController.navigate(Screen.Queue.route) }) {
                    Icon(Icons.Default.QueueMusic, contentDescription = "Queue")
                }
            }
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Quick Actions
            item {
                QuickActionsSection(navController)
            }
            
            // Recently Played
            if (recentlyPlayed.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recently Played",
                        onSeeAllClick = { navController.navigate(Screen.RecentlyPlayed.route) }
                    )
                }
                items(recentlyPlayed.take(5)) { track ->
                    TrackItem(
                        track = track,
                        onClick = { viewModel.playTrack(track) },
                        onMoreClick = { /* Show context menu */ }
                    )
                }
            }
            
            // Most Played
            if (mostPlayed.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Most Played",
                        onSeeAllClick = { navController.navigate(Screen.MostPlayed.route) }
                    )
                }
                items(mostPlayed.take(5)) { track ->
                    TrackItem(
                        track = track,
                        onClick = { viewModel.playTrack(track) },
                        onMoreClick = { /* Show context menu */ }
                    )
                }
            }
            
            // Recently Added
            if (recentlyAdded.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recently Added",
                        onSeeAllClick = { navController.navigate(Screen.Songs.route) }
                    )
                }
                items(recentlyAdded.take(5)) { track ->
                    TrackItem(
                        track = track,
                        onClick = { viewModel.playTrack(track) },
                        onMoreClick = { /* Show context menu */ }
                    )
                }
            }
            
            // Favorites
            if (favorites.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Favorites",
                        onSeeAllClick = { navController.navigate(Screen.Favorites.route) }
                    )
                }
                items(favorites.take(5)) { track ->
                    TrackItem(
                        track = track,
                        onClick = { viewModel.playTrack(track) },
                        onMoreClick = { /* Show context menu */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Shuffle,
                    label = "Shuffle All",
                    onClick = { /* Shuffle all tracks */ }
                )
                QuickActionButton(
                    icon = Icons.Default.PlaylistPlay,
                    label = "Last Playlist",
                    onClick = { /* Play last playlist */ }
                )
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = "Continue",
                    onClick = { /* Continue from last position */ }
                )
                QuickActionButton(
                    icon = Icons.Default.Refresh,
                    label = "Scan Library",
                    onClick = { /* Trigger library scan */ }
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        TextButton(onClick = onSeeAllClick) {
            Text("See All")
        }
    }
}