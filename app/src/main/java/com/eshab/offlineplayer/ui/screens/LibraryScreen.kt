package com.eshab.offlineplayer.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun LibraryScreen() {
    // Placeholder list until paging is wired
    val items = remember { (1..20).map { "Track $it" } }
    LazyColumn(Modifier.fillMaxSize()) {
        items(items) { t ->
            ListItem(
                headlineContent = { Text(t) },
                supportingContent = { Text("Artist • Album") }
            )
        }
    }
}