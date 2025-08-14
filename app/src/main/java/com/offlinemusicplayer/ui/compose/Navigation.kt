package com.offlinemusicplayer.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Library("library", "Library", Icons.Default.LibraryMusic),
    Search("search", "Search", Icons.Default.Search),
    Settings("settings", "Settings", Icons.Default.Settings),
    NowPlaying("now_playing", "Now Playing", Icons.Default.LibraryMusic)
}

sealed class MainTabs(val route: String, val title: String, val icon: ImageVector) {
    object Library : MainTabs("library", "Library", Icons.Default.LibraryMusic)
    object Search : MainTabs("search", "Search", Icons.Default.Search)
    object Settings : MainTabs("settings", "Settings", Icons.Default.Settings)
}