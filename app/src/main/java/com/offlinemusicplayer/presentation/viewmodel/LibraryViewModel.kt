package com.offlinemusicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinemusicplayer.domain.model.Song
import com.offlinemusicplayer.domain.usecase.GetAllSongsUseCase
import com.offlinemusicplayer.domain.usecase.SearchSongsUseCase
import com.offlinemusicplayer.domain.usecase.ScanMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val searchSongsUseCase: SearchSongsUseCase,
    private val scanMusicUseCase: ScanMusicUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllSongsUseCase().collect { songs ->
                _uiState.value = _uiState.value.copy(
                    songs = songs,
                    isLoading = false
                )
            }
        }
    }

    fun searchSongs(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadSongs()
            } else {
                searchSongsUseCase(query).collect { songs ->
                    _uiState.value = _uiState.value.copy(
                        songs = songs,
                        searchQuery = query
                    )
                }
            }
        }
    }

    fun scanForMusic() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, error = null)
            
            scanMusicUseCase().fold(
                onSuccess = { scannedCount ->
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        scanMessage = "Scanned $scannedCount songs"
                    )
                    loadSongs()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        error = "Failed to scan music: ${exception.message}"
                    )
                }
            )
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        loadSongs()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearScanMessage() {
        _uiState.value = _uiState.value.copy(scanMessage = null)
    }
}

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null,
    val scanMessage: String? = null
)