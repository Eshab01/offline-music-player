package com.offlinemusicplayer.playback

import androidx.media3.common.Player
import kotlin.math.max
import kotlin.math.min

object PlaybackUtils {
    fun safeSeek(player: Player, positionMs: Long) {
        val duration = player.duration
        if (duration > 0) {
            val clamped = max(0L, min(positionMs, duration - 50L))
            player.seekTo(clamped)
        }
    }
}