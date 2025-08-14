package com.offlinemusicplayer.service

import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.Player

class AudioFocusHandler(
    private val audioManager: AudioManager,
    private val player: Player,
) {
    private var audioFocusRequest: AudioFocusRequest? = null
    private var hasAudioFocus = false
    private var playbackDelayed = false
    private var resumeOnFocusGain = false

    private val audioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (playbackDelayed || resumeOnFocusGain) {
                        player.play()
                        resumeOnFocusGain = false
                        playbackDelayed = false
                    }
                    player.setVolume(1.0f)
                    hasAudioFocus = true
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    resumeOnFocusGain = false
                    playbackDelayed = false
                    player.pause()
                    hasAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    resumeOnFocusGain = player.isPlaying
                    playbackDelayed = false
                    player.pause()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    player.setVolume(0.3f)
                }
            }
        }

    fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true

        val result =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (audioFocusRequest == null) {
                    audioFocusRequest =
                        AudioFocusRequest
                            .Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setOnAudioFocusChangeListener(audioFocusChangeListener)
                            .build()
                }
                audioManager.requestAudioFocus(audioFocusRequest!!)
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN,
                )
            }

        when (result) {
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                playbackDelayed = false
                return false
            }
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                hasAudioFocus = true
                playbackDelayed = false
                return true
            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                playbackDelayed = true
                return false
            }
            else -> return false
        }
    }

    fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
        hasAudioFocus = false
    }
}
