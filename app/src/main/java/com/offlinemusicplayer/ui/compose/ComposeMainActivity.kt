package com.offlinemusicplayer.ui.compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.snackbar.Snackbar
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.scanner.MusicScanner
import com.offlinemusicplayer.service.MusicPlayerService
import com.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import com.offlinemusicplayer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class ComposeMainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private var mediaController: MediaController? = null

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                checkNotificationPermission()
                loadMusicFiles()
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (!granted) {
                // Show info that notification features may not work
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val app = application as MusicPlayerApplication
        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(app.repository)
        )[MainViewModel::class.java]

        setContent {
            OfflineMusicPlayerTheme {
                MainScreen(
                    viewModel = viewModel,
                    onTrackClick = ::playTrack,
                    onPermissionRequest = ::requestPermissions
                )
            }
        }

        requestPermissions()
        initializeMediaController()
    }

    private fun requestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        } else {
            checkNotificationPermission()
            loadMusicFiles()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun loadMusicFiles() {
        lifecycleScope.launch {
            try {
                val scanner = MusicScanner(this@ComposeMainActivity, viewModel.repository)
                val count = scanner.scanMusicLibrary()
                // Could show a snackbar here, but since we're in Compose, we'd need state management
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun initializeMediaController() {
        lifecycleScope.launch {
            try {
                val sessionToken = SessionToken(this@ComposeMainActivity, MusicPlayerService::class.java)
                val controllerFuture = MediaController.Builder(this@ComposeMainActivity, sessionToken).buildAsync()
                mediaController = controllerFuture.get()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun playTrack(track: Track) {
        mediaController?.let { controller ->
            val mediaItem = MediaItem.Builder()
                .setUri(track.uri)
                .setMediaId(track.id.toString())
                .build()
            controller.setMediaItem(mediaItem)
            controller.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController?.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onTrackClick: (Track) -> Unit,
    onPermissionRequest: () -> Unit
) {
    val navController = rememberNavController()
    var currentTrack by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column {
                // Mini Player
                MiniPlayer(
                    navController = navController,
                    currentTrack = currentTrack,
                    isPlaying = isPlaying,
                    onPlayPause = { /* TODO: Implement */ },
                    onNext = { /* TODO: Implement */ }
                )
                
                // Bottom Navigation
                NavigationBar {
                    listOf(MainTabs.Library, MainTabs.Search, MainTabs.Settings).forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            selected = navController.currentDestination?.route == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainTabs.Library.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainTabs.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    onTrackClick = onTrackClick
                )
            }
            composable(MainTabs.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onTrackClick = onTrackClick
                )
            }
            composable(MainTabs.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}