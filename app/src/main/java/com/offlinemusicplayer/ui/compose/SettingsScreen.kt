package com.offlinemusicplayer.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Playback",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingItem(
                title = "Skip Silence",
                description = "Skip silent parts in audio tracks",
                icon = Icons.Default.SkipNext,
                trailing = {
                    var checked by remember { mutableStateOf(false) }
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                }
            )
        }

        item {
            SettingItem(
                title = "Crossfade",
                description = "Fade between tracks",
                icon = Icons.Default.Tune,
                trailing = {
                    var checked by remember { mutableStateOf(false) }
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                }
            )
        }

        item {
            SettingItem(
                title = "Crossfade Duration",
                description = "3 seconds",
                icon = Icons.Default.Timer,
                onClick = { /* TODO: Show duration picker */ }
            )
        }

        item {
            Text(
                text = "Audio",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingItem(
                title = "Equalizer",
                description = "Adjust audio frequencies",
                icon = Icons.Default.Equalizer,
                trailing = {
                    var checked by remember { mutableStateOf(false) }
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                }
            )
        }

        item {
            SettingItem(
                title = "Equalizer Settings",
                description = "Configure presets and bands",
                icon = Icons.Default.Settings,
                onClick = { /* TODO: Navigate to equalizer screen */ }
            )
        }

        item {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingItem(
                title = "Sleep Timer",
                description = "Stop playback after a set time",
                icon = Icons.Default.Bedtime,
                onClick = { /* TODO: Show sleep timer picker */ }
            )
        }

        item {
            Text(
                text = "Library",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            SettingItem(
                title = "Rescan Library",
                description = "Scan for new music files",
                icon = Icons.Default.Refresh,
                onClick = { /* TODO: Trigger rescan */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier.fillMaxWidth(),
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            trailing?.invoke()
        }
    }
}