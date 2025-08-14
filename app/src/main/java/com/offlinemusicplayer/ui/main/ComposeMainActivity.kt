package com.offlinemusicplayer.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.*
import com.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import com.offlinemusicplayer.ui.navigation.MusicPlayerNavigation
import com.offlinemusicplayer.ui.components.PermissionHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keep splash screen on-screen longer while checking first run
        splashScreen.setKeepOnScreenCondition {
            viewModel.isCheckingFirstRun.value
        }
        
        setContent {
            val themeState by viewModel.themeState.collectAsState()
            val isFirstRun by viewModel.isFirstRun.collectAsState()
            val hasRequiredPermissions by viewModel.hasRequiredPermissions.collectAsState()
            
            OfflineMusicPlayerTheme(
                darkTheme = when (themeState.themeMode) {
                    "dark" -> true
                    "light" -> false
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColors,
                useMusicTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        isFirstRun -> {
                            OnboardingFlow(
                                onCompleted = { viewModel.completeFirstRun() }
                            )
                        }
                        !hasRequiredPermissions -> {
                            PermissionHandler(
                                onPermissionsGranted = { viewModel.checkPermissions() }
                            )
                        }
                        else -> {
                            MusicPlayerApp()
                        }
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions()
    }
}

@Composable
private fun MusicPlayerApp() {
    MusicPlayerNavigation()
}

@Composable
private fun OnboardingFlow(
    onCompleted: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (currentStep) {
            0 -> WelcomeStep(
                onNext = { currentStep = 1 }
            )
            1 -> PermissionsStep(
                onNext = { currentStep = 2 }
            )
            2 -> LibrarySetupStep(
                onNext = { currentStep = 3 }
            )
            3 -> PrivacyStep(
                onCompleted = onCompleted
            )
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column {
        Text(
            text = "Welcome to Offline Music Player",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "A privacy-focused music player that keeps your music completely offline by default.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionsStep(onNext: () -> Unit) {
    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val permissionState = rememberPermissionState(audioPermission)
    
    Column {
        Text(
            text = "Music Library Access",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "We need permission to access your music files to build your library.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        if (permissionState.status.isGranted) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        } else {
            Button(
                onClick = { permissionState.launchPermissionRequest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
private fun LibrarySetupStep(onNext: () -> Unit) {
    Column {
        Text(
            text = "Library Setup",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "We'll scan your device for music files. This may take a few moments.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Scan")
        }
    }
}

@Composable
private fun PrivacyStep(onCompleted: () -> Unit) {
    Column {
        Text(
            text = "Privacy First",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your music stays on your device. All network features are disabled by default and can be enabled later in settings.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onCompleted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Listening")
        }
    }
}