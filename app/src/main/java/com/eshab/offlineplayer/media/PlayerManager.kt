package com.eshab.offlineplayer.media

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor() {
    
    fun setQueue(uris: List<String>, startIndex: Int) {
        // TODO: Implement queue management
    }
    
    fun playPause() {
        // TODO: Implement play/pause
    }
    
    fun next() {
        // TODO: Implement next track
    }
    
    fun prev() {
        // TODO: Implement previous track
    }
}