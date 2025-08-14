package com.offlinemusicplayer.tile

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.offlinemusicplayer.R
import com.offlinemusicplayer.service.MusicPlayerService

@RequiresApi(Build.VERSION_CODES.N)
class MusicTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // TODO: Listen to player state changes and update tile accordingly
        updateTile(isPlaying = false) // Default state
    }

    override fun onClick() {
        super.onClick()
        
        // Toggle playback
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            action = "com.offlinemusicplayer.PLAY_PAUSE"
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        // Toggle tile state immediately for responsive feel
        val currentState = qsTile.state
        val newIsPlaying = currentState != Tile.STATE_ACTIVE
        updateTile(newIsPlaying)
    }

    private fun updateTile(isPlaying: Boolean) {
        qsTile?.let { tile ->
            tile.state = if (isPlaying) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            tile.label = getString(R.string.app_name)
            tile.contentDescription = if (isPlaying) "Pause music" else "Play music"
            
            val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            tile.icon = Icon.createWithResource(this, iconRes)
            
            tile.updateTile()
        }
    }

    companion object {
        // Method to be called from the service when playback state changes
        fun updateTileState(isPlaying: Boolean) {
            // This would need to be implemented to communicate with active tile instances
            // For now, we'll rely on the onClick toggle behavior
        }
    }
}