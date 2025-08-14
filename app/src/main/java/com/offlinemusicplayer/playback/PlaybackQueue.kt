package com.offlinemusicplayer.playback

import com.offlinemusicplayer.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackQueue {
    
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()
    
    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()
    
    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()
    
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()
    
    private var originalQueue: List<Track> = emptyList()
    private var shuffledIndices: List<Int> = emptyList()

    val currentTrack: Track?
        get() = if (_currentIndex.value >= 0 && _currentIndex.value < _queue.value.size) {
            _queue.value[_currentIndex.value]
        } else null

    val hasNext: Boolean
        get() = when (_repeatMode.value) {
            RepeatMode.ALL -> _queue.value.isNotEmpty()
            RepeatMode.ONE -> true
            RepeatMode.NONE -> _currentIndex.value < _queue.value.size - 1
        }

    val hasPrevious: Boolean
        get() = when (_repeatMode.value) {
            RepeatMode.ALL -> _queue.value.isNotEmpty()
            RepeatMode.ONE -> true
            RepeatMode.NONE -> _currentIndex.value > 0
        }

    fun setQueue(tracks: List<Track>, startIndex: Int = 0) {
        originalQueue = tracks
        _queue.value = tracks
        _currentIndex.value = if (tracks.isNotEmpty()) startIndex.coerceIn(0, tracks.size - 1) else -1
        
        if (_shuffleMode.value) {
            applyShuffle()
        }
    }

    fun addToQueue(track: Track) {
        val newQueue = _queue.value.toMutableList()
        newQueue.add(track)
        _queue.value = newQueue
        
        if (_queue.value.size == 1) {
            _currentIndex.value = 0
        }
    }

    fun addToQueue(tracks: List<Track>) {
        val newQueue = _queue.value.toMutableList()
        newQueue.addAll(tracks)
        _queue.value = newQueue
        
        if (_currentIndex.value == -1 && newQueue.isNotEmpty()) {
            _currentIndex.value = 0
        }
    }

    fun removeFromQueue(index: Int): Track? {
        if (index < 0 || index >= _queue.value.size) return null
        
        val newQueue = _queue.value.toMutableList()
        val removedTrack = newQueue.removeAt(index)
        _queue.value = newQueue
        
        when {
            _queue.value.isEmpty() -> _currentIndex.value = -1
            index < _currentIndex.value -> _currentIndex.value -= 1
            index == _currentIndex.value -> {
                if (_currentIndex.value >= _queue.value.size) {
                    _currentIndex.value = _queue.value.size - 1
                }
            }
        }
        
        return removedTrack
    }

    fun moveTrack(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex || fromIndex < 0 || toIndex < 0 || 
            fromIndex >= _queue.value.size || toIndex >= _queue.value.size) {
            return
        }
        
        val newQueue = _queue.value.toMutableList()
        val track = newQueue.removeAt(fromIndex)
        newQueue.add(toIndex, track)
        _queue.value = newQueue
        
        // Update current index if necessary
        when {
            fromIndex == _currentIndex.value -> _currentIndex.value = toIndex
            fromIndex < _currentIndex.value && toIndex >= _currentIndex.value -> _currentIndex.value -= 1
            fromIndex > _currentIndex.value && toIndex <= _currentIndex.value -> _currentIndex.value += 1
        }
    }

    fun next(): Track? {
        return when (_repeatMode.value) {
            RepeatMode.ONE -> currentTrack
            RepeatMode.ALL -> {
                if (_queue.value.isEmpty()) return null
                _currentIndex.value = (_currentIndex.value + 1) % _queue.value.size
                currentTrack
            }
            RepeatMode.NONE -> {
                if (_currentIndex.value < _queue.value.size - 1) {
                    _currentIndex.value += 1
                    currentTrack
                } else null
            }
        }
    }

    fun previous(): Track? {
        return when (_repeatMode.value) {
            RepeatMode.ONE -> currentTrack
            RepeatMode.ALL -> {
                if (_queue.value.isEmpty()) return null
                _currentIndex.value = if (_currentIndex.value > 0) _currentIndex.value - 1 else _queue.value.size - 1
                currentTrack
            }
            RepeatMode.NONE -> {
                if (_currentIndex.value > 0) {
                    _currentIndex.value -= 1
                    currentTrack
                } else null
            }
        }
    }

    fun skipTo(index: Int): Track? {
        if (index < 0 || index >= _queue.value.size) return null
        _currentIndex.value = index
        return currentTrack
    }

    fun setShuffleMode(enabled: Boolean) {
        if (_shuffleMode.value == enabled) return
        
        _shuffleMode.value = enabled
        
        if (enabled) {
            applyShuffle()
        } else {
            removeShuffleMode()
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }

    fun clear() {
        _queue.value = emptyList()
        _currentIndex.value = -1
        originalQueue = emptyList()
        shuffledIndices = emptyList()
    }

    private fun applyShuffle() {
        if (_queue.value.isEmpty()) return
        
        val currentTrack = this.currentTrack
        shuffledIndices = _queue.value.indices.shuffled()
        val newQueue = shuffledIndices.map { originalQueue[it] }
        _queue.value = newQueue
        
        // Find the new index of the current track
        currentTrack?.let { track ->
            _currentIndex.value = newQueue.indexOfFirst { it.id == track.id }
        }
    }

    private fun removeShuffleMode() {
        if (originalQueue.isEmpty()) return
        
        val currentTrack = this.currentTrack
        _queue.value = originalQueue
        
        // Find the original index of the current track
        currentTrack?.let { track ->
            _currentIndex.value = originalQueue.indexOfFirst { it.id == track.id }
        }
        
        shuffledIndices = emptyList()
    }

    enum class RepeatMode {
        NONE, ONE, ALL
    }
}