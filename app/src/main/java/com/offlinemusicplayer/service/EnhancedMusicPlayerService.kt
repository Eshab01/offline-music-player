package com.offlinemusicplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.content.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.datastore.UserPreferencesRepository
import com.offlinemusicplayer.equalizer.EqualizerController
import com.offlinemusicplayer.timer.SleepTimerManager
import com.offlinemusicplayer.ui.compose.ComposeMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EnhancedMusicPlayerService : MediaSessionService() {
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null
    private lateinit var audioFocusHandler: AudioFocusHandler
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var sleepTimerManager: SleepTimerManager
    private var equalizerController: EqualizerController? = null
    
    private val serviceScope = CoroutineScope(SupervisorJob())

    companion object {
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_STOP = "action_stop"
    }

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(this)
        
        // Create initial notification to start foreground
        val initialNotification = NotificationCompat.Builder(this, MusicPlayerApplication.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_preparing))
            .setSmallIcon(R.drawable.ic_music_note)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, initialNotification)
        }

        initializeSessionAndPlayer()
    }

    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            setHandleAudioBecomingNoisy(true)
            setWakeMode(C.WAKE_MODE_LOCAL)
        }

        // Initialize equalizer if available
        try {
            equalizerController = EqualizerController(player.audioSessionId)
        } catch (e: Exception) {
            // Equalizer not supported
        }

        // Initialize sleep timer
        sleepTimerManager = SleepTimerManager(player, serviceScope)

        audioFocusHandler = AudioFocusHandler(getSystemService(android.media.AudioManager::class.java), player)

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, ComposeMainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        mediaSession = MediaSession
            .Builder(this, player)
            .setCallback(MediaSessionCallback())
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> if (player.playWhenReady) audioFocusHandler.requestAudioFocus()
                    Player.STATE_ENDED, Player.STATE_IDLE -> audioFocusHandler.abandonAudioFocus()
                }
                updateNotification()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) audioFocusHandler.requestAudioFocus() else audioFocusHandler.abandonAudioFocus()
                updateNotification()
                
                // Apply settings when playback starts
                if (playWhenReady) {
                    serviceScope.launch {
                        applyUserSettings()
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateNotification()
                
                // Apply crossfade if enabled
                serviceScope.launch {
                    if (userPreferencesRepository.crossfadeEnabled.first()) {
                        // TODO: Implement crossfade logic
                    }
                }
            }
        })

        // Apply initial user settings
        serviceScope.launch {
            applyUserSettings()
        }
    }

    private suspend fun applyUserSettings() {
        val skipSilence = userPreferencesRepository.skipSilence.first()
        player.setSkipSilenceEnabled(skipSilence)

        val equalizerEnabled = userPreferencesRepository.equalizerEnabled.first()
        equalizerController?.setEnabled(equalizerEnabled)

        if (equalizerEnabled) {
            val preset = userPreferencesRepository.equalizerPreset.first()
            equalizerController?.usePreset(preset)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY_PAUSE -> {
                    if (player.isPlaying) player.pause() else player.play()
                }
                ACTION_NEXT -> player.seekToNextMediaItem()
                ACTION_PREVIOUS -> player.seekToPreviousMediaItem()
                ACTION_STOP -> {
                    player.stop()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    private fun updateNotification() {
        mediaSession?.let { session ->
            val notification = MediaNotificationHelper.createNotification(this, session, player)
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.notify(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        sleepTimerManager.cancelTimer()
        equalizerController?.release()
        serviceScope.cancel()
        
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    // Public methods for external control
    fun getSleepTimerManager(): SleepTimerManager = sleepTimerManager
    fun getEqualizerController(): EqualizerController? = equalizerController
}