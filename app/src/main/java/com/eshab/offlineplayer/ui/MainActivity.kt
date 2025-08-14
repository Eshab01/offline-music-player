package com.eshab.offlineplayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.eshab.offlineplayer.ui.screens.LibraryScreen
import com.eshab.offlineplayer.ui.screens.NowPlayingScreen
import com.eshab.offlineplayer.ui.screens.SearchScreen
import com.eshab.offlineplayer.ui.screens.SettingsScreen
import com.eshab.offlineplayer.ui.screens.HomeScreen
import com.eshab.offlineplayer.ui.components.MiniPlayer
import com.eshab.offlineplayer.ui.theme.AppTheme
import com.eshab.offlineplayer.ui.viewmodel.PlayerViewModel
import com.eshab.offlineplayer.util.audioPermission
import com.eshab.offlineplayer.util.ensurePlaybackService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val playerVm: PlayerViewModel = hiltViewModel()

                // Permission flow
                val perm = audioPermission()
                val permissionState = rememberPermissionState(perm)
                LaunchedEffect(Unit) {
                    if (!permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    }
                }
                
                // Trigger scan when permission is granted
                LaunchedEffect(permissionState.status.isGranted) {
                    if (permissionState.status.isGranted) {
                        // Trigger initial scan when permission is granted
                        val libraryVm: com.eshab.offlineplayer.ui.viewmodel.LibraryViewModel = hiltViewModel()
                        libraryVm.triggerScan()
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            listOf(
                                BottomItem("home", "Home"),
                                BottomItem("library", "Library"),
                                BottomItem("search", "Search"),
                                BottomItem("now", "Now"),
                                BottomItem("settings", "Settings"),
                            ).forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        when (item.route) {
                                            "home" -> Icon(Icons.Default.Home, contentDescription = item.label)
                                            "library" -> Icon(Icons.Default.LibraryMusic, contentDescription = item.label)
                                            "search" -> Icon(Icons.Default.Search, contentDescription = item.label)
                                            "now" -> Icon(Icons.Outlined.PlayCircleFilled, contentDescription = item.label)
                                            "settings" -> Icon(Icons.Default.Settings, contentDescription = item.label)
                                        }
                                    },
                                    label = { Text(item.label) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Column(Modifier.padding(padding)) {
                        Box(Modifier.weight(1f)) {
                            NavHost(navController, startDestination = "home") {
                                composable("home") { HomeScreen() }
                                composable("library") {
                                    LibraryScreen(
                                        onPlay = { uris, index ->
                                            ensurePlaybackService(this@MainActivity)
                                            playerVm.setQueueAndPlay(uris, index)
                                        }
                                    )
                                }
                                composable("search") { SearchScreen() }
                                composable("now") { NowPlayingScreen() }
                                composable("settings") { SettingsScreen() }
                            }
                        }
                        
                        // Persistent Mini-player at bottom
                        MiniPlayer(vm = playerVm) {
                            navController.navigate("now")
                        }
                    }
                }
            }
        }
    }
}

data class BottomItem(val route: String, val label: String)