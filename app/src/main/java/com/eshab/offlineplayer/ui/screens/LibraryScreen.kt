package com.eshab.offlineplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.eshab.offlineplayer.ui.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    onPlay: (uris: List<String>, index: Int) -> Unit,
    vm: LibraryViewModel = hiltViewModel()
) {
    val paging = vm.tracks.collectAsLazyPagingItems()

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Library", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { vm.triggerScan() }) { Text("Scan") }
        }
        Divider()
        LazyColumn(Modifier.fillMaxSize()) {
            items(paging) { track ->
                if (track != null) {
                    ListItem(
                        headlineContent = { Text(track.title ?: "Unknown title") },
                        supportingContent = { Text((track.artist ?: "Unknown artist") + " • " + (track.album ?: "")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable {
                                val uris = (0 until paging.itemCount).mapNotNull { i -> paging.peek(i)?.uri }
                                val idx = uris.indexOf(track.uri)
                                if (idx >= 0) onPlay(uris, idx)
                            }
                    )
                }
            }
        }
    }
}