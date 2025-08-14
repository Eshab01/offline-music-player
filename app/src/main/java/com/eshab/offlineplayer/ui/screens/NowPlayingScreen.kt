package com.eshab.offlineplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eshab.offlineplayer.ui.viewmodel.PlayerViewModel

@Composable
fun NowPlayingScreen(vm: PlayerViewModel = hiltViewModel()) {
    val title = vm.title.collectAsState().value
    val sub = vm.subtitle.collectAsState().value
    val playing = vm.isPlaying.collectAsState().value

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(title.ifEmpty { "Now Playing" }, style = MaterialTheme.typography.headlineSmall)
        Text(sub.ifEmpty { "Offline queue" }, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = { vm.prev() }) { Text("Prev") }
            Button(onClick = { vm.playPause() }) { Text(if (playing) "Pause" else "Play") }
            OutlinedButton(onClick = { vm.next() }) { Text("Next") }
        }
    }
}