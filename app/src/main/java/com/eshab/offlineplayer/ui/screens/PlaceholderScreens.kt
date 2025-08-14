package com.eshab.offlineplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home", style = MaterialTheme.typography.headlineMedium)
        Text("Welcome to Offline Music Player", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SearchScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Search", style = MaterialTheme.typography.headlineMedium)
        Text("Search functionality coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingsScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Settings coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}