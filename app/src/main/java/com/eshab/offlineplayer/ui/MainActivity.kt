package com.eshab.offlineplayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eshab.offlineplayer.ui.screens.LibraryScreen
import com.eshab.offlineplayer.ui.screens.NowPlayingScreen
import com.eshab.offlineplayer.ui.screens.SearchScreen
import com.eshab.offlineplayer.ui.screens.SettingsScreen
import com.eshab.offlineplayer.ui.screens.HomeScreen
import com.eshab.offlineplayer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                val items = listOf(
                    BottomItem("home", "Home"),
                    BottomItem("library", "Library"),
                    BottomItem("search", "Search"),
                    BottomItem("now", "Now"),
                    BottomItem("settings", "Settings"),
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
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
                    NavHost(navController, startDestination = "home", modifier = Modifier.padding(padding)) {
                        composable("home") { HomeScreen() }
                        composable("library") { LibraryScreen() }
                        composable("search") { SearchScreen() }
                        composable("now") { NowPlayingScreen() }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}

data class BottomItem(val route: String, val label: String)