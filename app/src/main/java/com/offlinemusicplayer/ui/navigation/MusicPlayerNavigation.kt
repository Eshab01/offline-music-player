package com.offlinemusicplayer.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.offlinemusicplayer.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerNavigation(
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                MusicPlayerBottomNavigation(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Main screens
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController)
            }
            
            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }
            
            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(navController = navController)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            
            // Library sub-screens
            composable(Screen.Songs.route) {
                SongsScreen(navController = navController)
            }
            
            composable(Screen.Albums.route) {
                AlbumsScreen(navController = navController)
            }
            
            composable(Screen.Artists.route) {
                ArtistsScreen(navController = navController)
            }
            
            composable(Screen.Genres.route) {
                GenresScreen(navController = navController)
            }
            
            composable(Screen.Playlists.route) {
                PlaylistsScreen(navController = navController)
            }
            
            composable(Screen.Folders.route) {
                FoldersScreen(navController = navController)
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen(navController = navController)
            }
            
            composable(Screen.RecentlyPlayed.route) {
                RecentlyPlayedScreen(navController = navController)
            }
            
            composable(Screen.MostPlayed.route) {
                MostPlayedScreen(navController = navController)
            }
            
            composable(Screen.NeverPlayed.route) {
                NeverPlayedScreen(navController = navController)
            }
            
            // Feature screens
            composable(Screen.Queue.route) {
                QueueScreen(navController = navController)
            }
            
            composable(Screen.Equalizer.route) {
                EqualizerScreen(navController = navController)
            }
            
            composable(Screen.Stats.route) {
                StatsScreen(navController = navController)
            }
            
            composable(Screen.Export.route) {
                ExportScreen(navController = navController)
            }
            
            composable(Screen.Import.route) {
                ImportScreen(navController = navController)
            }
            
            composable(Screen.DuplicateDetection.route) {
                DuplicateDetectionScreen(navController = navController)
            }
            
            composable(Screen.CreatePlaylist.route) {
                CreatePlaylistScreen(navController = navController)
            }
            
            // Detail screens with parameters
            composable(Screen.AlbumDetail.route) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString(NavigationArgs.ALBUM_ID)
                AlbumDetailScreen(
                    albumId = albumId ?: "",
                    navController = navController
                )
            }
            
            composable(Screen.ArtistDetail.route) { backStackEntry ->
                val artistId = backStackEntry.arguments?.getString(NavigationArgs.ARTIST_ID)
                ArtistDetailScreen(
                    artistId = artistId ?: "",
                    navController = navController
                )
            }
            
            composable(Screen.GenreDetail.route) { backStackEntry ->
                val genreId = backStackEntry.arguments?.getString(NavigationArgs.GENRE_ID)?.toLongOrNull()
                GenreDetailScreen(
                    genreId = genreId ?: 0L,
                    navController = navController
                )
            }
            
            composable(Screen.PlaylistDetail.route) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getString(NavigationArgs.PLAYLIST_ID)?.toLongOrNull()
                PlaylistDetailScreen(
                    playlistId = playlistId ?: 0L,
                    navController = navController
                )
            }
            
            composable(Screen.EditTrack.route) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString(NavigationArgs.TRACK_ID)?.toLongOrNull()
                EditTrackScreen(
                    trackId = trackId ?: 0L,
                    navController = navController
                )
            }
            
            composable(Screen.LyricsViewer.route) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString(NavigationArgs.TRACK_ID)?.toLongOrNull()
                LyricsViewerScreen(
                    trackId = trackId ?: 0L,
                    navController = navController
                )
            }
            
            // Settings sub-screens
            composable(Screen.ThemeSettings.route) {
                ThemeSettingsScreen(navController = navController)
            }
            
            composable(Screen.AudioSettings.route) {
                AudioSettingsScreen(navController = navController)
            }
            
            composable(Screen.LibrarySettings.route) {
                LibrarySettingsScreen(navController = navController)
            }
            
            composable(Screen.SyncSettings.route) {
                SyncSettingsScreen(navController = navController)
            }
            
            composable(Screen.SecuritySettings.route) {
                SecuritySettingsScreen(navController = navController)
            }
            
            composable(Screen.AccessibilitySettings.route) {
                AccessibilitySettingsScreen(navController = navController)
            }
            
            composable(Screen.AboutSettings.route) {
                AboutSettingsScreen(navController = navController)
            }
        }
    }
}

@Composable
fun MusicPlayerBottomNavigation(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        Screen.bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { 
                    screen.icon?.let { 
                        Icon(imageVector = it, contentDescription = screen.title) 
                    }
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        Screen.NowPlaying.route,
        Screen.Equalizer.route,
        Screen.AppLock.route -> false
        else -> currentRoute?.startsWith("edit_") != true && 
               currentRoute?.startsWith("create_") != true &&
               currentRoute?.startsWith("onboarding") != true &&
               currentRoute?.startsWith("permissions") != true
    }
}