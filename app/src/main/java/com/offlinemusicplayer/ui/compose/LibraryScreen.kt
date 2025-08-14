package com.offlinemusicplayer.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.ui.viewmodel.MainViewModel

enum class LibraryTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Songs("Songs", Icons.Default.LibraryMusic),
    Albums("Albums", Icons.Default.Album),
    Artists("Artists", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.Songs) }
    val tracks = viewModel.tracks.collectAsLazyPagingItems()

    Column(modifier = modifier) {
        // Tab row
        TabRow(
            selectedTabIndex = LibraryTab.values().indexOf(selectedTab)
        ) {
            LibraryTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon, contentDescription = tab.title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            LibraryTab.Songs -> {
                SongsTab(
                    tracks = tracks,
                    onTrackClick = onTrackClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            LibraryTab.Albums -> {
                AlbumsTab(modifier = Modifier.fillMaxSize())
            }
            LibraryTab.Artists -> {
                ArtistsTab(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun SongsTab(
    tracks: LazyPagingItems<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(tracks) { track ->
            track?.let {
                TrackItem(
                    track = it,
                    onClick = { onTrackClick(it) }
                )
            }
        }
    }
}

@Composable
fun AlbumsTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Albums coming soon")
    }
}

@Composable
fun ArtistsTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Artists coming soon")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = "${track.displayArtist} • ${track.displayAlbum}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = formatDuration(track.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = (durationMs / 1000) / 60
    val seconds = (durationMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}