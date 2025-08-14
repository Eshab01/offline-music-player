package com.offlinemusicplayer.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionsGranted: () -> Unit
) {
    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else null
    
    val permissions = listOfNotNull(audioPermission, notificationPermission)
    val permissionState = rememberMultiplePermissionsState(permissions)
    
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            onPermissionsGranted()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app needs permission to access your music files and send notifications for playback controls.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Show specific permission requests
        permissionState.permissions.forEach { permission ->
            PermissionItem(
                permissionState = permission,
                permissionName = when (permission.permission) {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE -> "Music Library Access"
                    Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
                    else -> "Unknown Permission"
                },
                permissionDescription = when (permission.permission) {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE -> "Required to scan and play your music files"
                    Manifest.permission.POST_NOTIFICATIONS -> "Required for playback controls and now playing notifications"
                    else -> ""
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { permissionState.launchMultiplePermissionRequest() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !permissionState.allPermissionsGranted
        ) {
            Text(if (permissionState.allPermissionsGranted) "All Permissions Granted" else "Grant Permissions")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionItem(
    permissionState: PermissionState,
    permissionName: String,
    permissionDescription: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = permissionName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                when {
                    permissionState.status.isGranted -> {
                        Text(
                            text = "✓ Granted",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    permissionState.status.shouldShowRationale -> {
                        Text(
                            text = "Denied",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    else -> {
                        Text(
                            text = "Not Granted",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            
            if (permissionDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = permissionDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (permissionState.status.shouldShowRationale) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This permission was denied. Please grant it in the app settings to use the music player.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}