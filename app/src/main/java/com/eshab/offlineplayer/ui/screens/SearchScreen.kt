package com.eshab.offlineplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun SearchScreen() {
    var q by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Search") })
        // TODO: results list with paging
    }
}