package com.offlinemusicplayer.ui.main

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinemusicplayer.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _isCheckingFirstRun = MutableStateFlow(true)
    val isCheckingFirstRun: StateFlow<Boolean> = _isCheckingFirstRun.asStateFlow()
    
    private val _isFirstRun = MutableStateFlow(false)
    val isFirstRun: StateFlow<Boolean> = _isFirstRun.asStateFlow()
    
    private val _hasRequiredPermissions = MutableStateFlow(false)
    val hasRequiredPermissions: StateFlow<Boolean> = _hasRequiredPermissions.asStateFlow()
    
    val themeState: StateFlow<ThemeState> = combine(
        settingsRepository.getThemeMode(),
        settingsRepository.getDynamicColors(),
        settingsRepository.getHighContrast(),
        settingsRepository.getLargeFont()
    ) { themeMode, dynamicColors, highContrast, largeFont ->
        ThemeState(
            themeMode = themeMode,
            dynamicColors = dynamicColors,
            highContrast = highContrast,
            largeFont = largeFont
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState()
    )
    
    init {
        checkFirstRun()
        checkPermissions()
    }
    
    private fun checkFirstRun() {
        viewModelScope.launch {
            settingsRepository.getFirstRunCompleted().collect { completed ->
                _isFirstRun.value = !completed
                _isCheckingFirstRun.value = false
            }
        }
    }
    
    fun checkPermissions() {
        val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            application,
            audioPermission
        ) == PackageManager.PERMISSION_GRANTED
        
        _hasRequiredPermissions.value = hasAudioPermission
    }
    
    fun completeFirstRun() {
        viewModelScope.launch {
            settingsRepository.setFirstRunCompleted(true)
        }
    }
}

data class ThemeState(
    val themeMode: String = "system",
    val dynamicColors: Boolean = true,
    val highContrast: Boolean = false,
    val largeFont: Boolean = false
)