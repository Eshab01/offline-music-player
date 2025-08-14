package com.eshab.offlineplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eshab.offlineplayer.media.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerManager: PlayerManager
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _subtitle = MutableStateFlow("")
    val subtitle: StateFlow<String> = _subtitle

    fun setQueueAndPlay(uris: List<String>, startIndex: Int) {
        playerManager.setQueue(uris, startIndex)
        _isPlaying.value = true
        // Titles would normally come from metadata; set lightweight placeholders here
        _title.value = "Playing"
        _subtitle.value = "Offline"
    }

    fun playPause() {
        playerManager.playPause()
        _isPlaying.value = !_isPlaying.value
    }

    fun next() = playerManager.next()
    fun prev() = playerManager.prev()
}