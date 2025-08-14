package com.offlinemusicplayer.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.data.repository.MusicRepository
import com.offlinemusicplayer.data.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {
    
    private val _recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
    val recentlyPlayed: StateFlow<List<Track>> = _recentlyPlayed.asStateFlow()
    
    private val _mostPlayed = MutableStateFlow<List<Track>>(emptyList())
    val mostPlayed: StateFlow<List<Track>> = _mostPlayed.asStateFlow()
    
    private val _recentlyAdded = MutableStateFlow<List<Track>>(emptyList())
    val recentlyAdded: StateFlow<List<Track>> = _recentlyAdded.asStateFlow()
    
    private val _favorites = MutableStateFlow<List<Track>>(emptyList())
    val favorites: StateFlow<List<Track>> = _favorites.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _recentlyPlayed.value = musicRepository.getRecentlyPlayedTracks(5)
                _mostPlayed.value = musicRepository.getMostPlayedTracks(5)
                // Get recently added tracks from last 30 days
                val recentTracks = musicRepository.getRecentlyAddedTracksForSmart(30, 5)
                _recentlyAdded.value = recentTracks
                
                // Load favorites - we'll need to implement this query
                // For now, using an empty list
                _favorites.value = emptyList()
            } catch (e: Exception) {
                // Handle error - in production, show error state
            }
        }
    }
    
    fun playTrack(track: Track) {
        viewModelScope.launch {
            // TODO: Implement track playback
            // This would interact with the MediaController/PlayerManager
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
}