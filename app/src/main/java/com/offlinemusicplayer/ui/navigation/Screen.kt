package com.offlinemusicplayer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Library : Screen("library", "Library", Icons.Default.LibraryMusic)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object NowPlaying : Screen("now_playing", "Now Playing", Icons.Default.MusicNote)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Library sub-screens
    object Songs : Screen("library/songs", "Songs")
    object Albums : Screen("library/albums", "Albums")
    object Artists : Screen("library/artists", "Artists")
    object Genres : Screen("library/genres", "Genres")
    object Playlists : Screen("library/playlists", "Playlists")
    object Folders : Screen("library/folders", "Folders")
    object Favorites : Screen("library/favorites", "Favorites")
    object RecentlyPlayed : Screen("library/recently_played", "Recently Played")
    object MostPlayed : Screen("library/most_played", "Most Played")
    object NeverPlayed : Screen("library/never_played", "Never Played")
    
    // Detail screens
    object AlbumDetail : Screen("album/{albumId}", "Album")
    object ArtistDetail : Screen("artist/{artistId}", "Artist")
    object GenreDetail : Screen("genre/{genreId}", "Genre")
    object PlaylistDetail : Screen("playlist/{playlistId}", "Playlist")
    object FolderDetail : Screen("folder/{folderPath}", "Folder")
    
    // Edit screens
    object EditTrack : Screen("edit_track/{trackId}", "Edit Track")
    object EditPlaylist : Screen("edit_playlist/{playlistId}", "Edit Playlist")
    object CreatePlaylist : Screen("create_playlist", "Create Playlist")
    object EditGenres : Screen("edit_genres/{trackId}", "Edit Genres")
    
    // Feature screens
    object Queue : Screen("queue", "Queue")
    object Equalizer : Screen("equalizer", "Equalizer")
    object Stats : Screen("stats", "Statistics")
    object Export : Screen("export", "Export")
    object Import : Screen("import", "Import")
    object DuplicateDetection : Screen("duplicate_detection", "Duplicate Detection")
    object AppLock : Screen("app_lock", "App Lock")
    object LyricsViewer : Screen("lyrics/{trackId}", "Lyrics")
    
    // Settings sub-screens
    object ThemeSettings : Screen("settings/theme", "Theme")
    object AudioSettings : Screen("settings/audio", "Audio")
    object LibrarySettings : Screen("settings/library", "Library")
    object SyncSettings : Screen("settings/sync", "Sync")
    object SecuritySettings : Screen("settings/security", "Security")
    object AccessibilitySettings : Screen("settings/accessibility", "Accessibility")
    object AboutSettings : Screen("settings/about", "About")
    
    // Onboarding
    object Onboarding : Screen("onboarding", "Welcome")
    object PermissionsSetup : Screen("permissions_setup", "Permissions")
    object LibrarySetup : Screen("library_setup", "Library Setup")
    
    companion object {
        val bottomNavItems = listOf(Home, Library, Search, NowPlaying, Settings)
        
        fun fromRoute(route: String?): Screen? = when (route) {
            Home.route -> Home
            Library.route -> Library
            Search.route -> Search
            NowPlaying.route -> NowPlaying
            Settings.route -> Settings
            Songs.route -> Songs
            Albums.route -> Albums
            Artists.route -> Artists
            Genres.route -> Genres
            Playlists.route -> Playlists
            Folders.route -> Folders
            Favorites.route -> Favorites
            RecentlyPlayed.route -> RecentlyPlayed
            MostPlayed.route -> MostPlayed
            NeverPlayed.route -> NeverPlayed
            Queue.route -> Queue
            Equalizer.route -> Equalizer
            Stats.route -> Stats
            Export.route -> Export
            Import.route -> Import
            DuplicateDetection.route -> DuplicateDetection
            AppLock.route -> AppLock
            CreatePlaylist.route -> CreatePlaylist
            ThemeSettings.route -> ThemeSettings
            AudioSettings.route -> AudioSettings
            LibrarySettings.route -> LibrarySettings
            SyncSettings.route -> SyncSettings
            SecuritySettings.route -> SecuritySettings
            AccessibilitySettings.route -> AccessibilitySettings
            AboutSettings.route -> AboutSettings
            Onboarding.route -> Onboarding
            PermissionsSetup.route -> PermissionsSetup
            LibrarySetup.route -> LibrarySetup
            else -> null
        }
    }
}

// Navigation arguments
object NavigationArgs {
    const val ALBUM_ID = "albumId"
    const val ARTIST_ID = "artistId"
    const val GENRE_ID = "genreId"
    const val PLAYLIST_ID = "playlistId"
    const val TRACK_ID = "trackId"
    const val FOLDER_PATH = "folderPath"
}