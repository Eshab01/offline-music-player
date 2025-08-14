package com.eshab.offlineplayer.media

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    
    fun setQueue(uris: List<String>, startIndex: Int) {
        val mediaItems = uris.map { uri ->
            MediaItem.Builder()
                .setUri(uri)
                .build()
        }
        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()
        exoPlayer.play()
    }
    
    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }
    
    fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }
    
    fun prev() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }
    
    val isPlaying: Boolean
        get() = exoPlayer.isPlaying
}