package com.eshab.offlineplayer.media

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager(private val player: ExoPlayer) {

    fun setQueue(uris: List<String>, startIndex: Int = 0) {
        val items = uris.map { MediaItem.fromUri(Uri.parse(it)) }
        player.setMediaItems(items, startIndex, 0L)
        player.prepare()
        player.playWhenReady = true
    }

    fun playPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun next() = player.seekToNext()
    fun prev() = player.seekToPrevious()

    fun player(): Player = player
}