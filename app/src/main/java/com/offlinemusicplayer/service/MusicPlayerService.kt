package com.offlinemusicplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.offlinemusicplayer.R
import com.offlinemusicplayer.ui.main.MainActivity

class MusicPlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusHandler: AudioFocusHandler

    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        audioManager = getSystemService(AudioManager::class.java)
        audioFocusHandler = AudioFocusHandler(audioManager, player)

        val sessionActivityPendingIntent = 
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        // Set up audio focus handling
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        if (player.playWhenReady) {
                            audioFocusHandler.requestAudioFocus()
                            startForeground()
                        }
                    }
                    Player.STATE_IDLE, Player.STATE_ENDED -> {
                        audioFocusHandler.abandonAudioFocus()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    audioFocusHandler.requestAudioFocus()
                    startForeground()
                } else {
                    audioFocusHandler.abandonAudioFocus()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }
        })
    }

    private fun startForeground() {
        val notification = MediaNotificationHelper.createNotification(
            this,
            mediaSession!!,
            player
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems = mediaItems.map { mediaItem ->
                mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri)
                    .build()
            }
            return Futures.immediateFuture(updatedMediaItems)
        }

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}