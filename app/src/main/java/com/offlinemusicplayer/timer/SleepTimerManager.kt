package com.offlinemusicplayer.timer

import android.animation.ValueAnimator
import androidx.media3.common.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SleepTimerManager(
    private val player: Player,
    private val scope: CoroutineScope
) {
    private var timerJob: Job? = null
    private var fadeOutAnimator: ValueAnimator? = null

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _remainingTimeMs = MutableStateFlow(0L)
    val remainingTimeMs: StateFlow<Long> = _remainingTimeMs.asStateFlow()

    private val _totalTimeMs = MutableStateFlow(0L)
    val totalTimeMs: StateFlow<Long> = _totalTimeMs.asStateFlow()

    fun startTimer(durationMs: Long, fadeOut: Boolean = true) {
        cancelTimer()
        
        _totalTimeMs.value = durationMs
        _remainingTimeMs.value = durationMs
        _isActive.value = true

        timerJob = scope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + durationMs
            val fadeOutDurationMs = if (fadeOut) 10000L else 0L // 10 seconds fade out
            val fadeStartTime = endTime - fadeOutDurationMs

            while (System.currentTimeMillis() < endTime && isActive(this)) {
                val currentTime = System.currentTimeMillis()
                _remainingTimeMs.value = endTime - currentTime

                // Start fade out if enabled and we're in the fade out period
                if (fadeOut && currentTime >= fadeStartTime && fadeOutAnimator == null) {
                    startFadeOut(fadeOutDurationMs)
                }

                delay(1000) // Update every second
            }

            if (isActive(this)) {
                // Timer completed
                stopPlayback()
                _isActive.value = false
                _remainingTimeMs.value = 0L
                _totalTimeMs.value = 0L
            }
        }
    }

    private fun startFadeOut(durationMs: Long) {
        val currentVolume = player.volume
        fadeOutAnimator = ValueAnimator.ofFloat(currentVolume, 0f).apply {
            duration = durationMs
            addUpdateListener { animator ->
                val volume = animator.animatedValue as Float
                player.setVolume(volume)
            }
            start()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        
        fadeOutAnimator?.cancel()
        fadeOutAnimator = null
        
        // Restore volume if it was faded out
        if (_isActive.value) {
            player.setVolume(1.0f)
        }
        
        _isActive.value = false
        _remainingTimeMs.value = 0L
        _totalTimeMs.value = 0L
    }

    fun extendTimer(additionalMs: Long) {
        if (_isActive.value) {
            val newTotal = _totalTimeMs.value + additionalMs
            startTimer(newTotal, true) // Restart with extended time
        }
    }

    private fun stopPlayback() {
        player.pause()
        // Restore volume in case it was faded out
        player.setVolume(1.0f)
    }

    private fun isActive(job: Job): Boolean {
        return job.isActive && _isActive.value
    }

    fun formatTime(timeMs: Long): String {
        val hours = timeMs / (1000 * 60 * 60)
        val minutes = (timeMs % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (timeMs % (1000 * 60)) / 1000

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    companion object {
        val PRESET_DURATIONS = listOf(
            5 * 60 * 1000L,      // 5 minutes
            10 * 60 * 1000L,     // 10 minutes
            15 * 60 * 1000L,     // 15 minutes
            30 * 60 * 1000L,     // 30 minutes
            45 * 60 * 1000L,     // 45 minutes
            60 * 60 * 1000L,     // 1 hour
            90 * 60 * 1000L,     // 1.5 hours
            120 * 60 * 1000L     // 2 hours
        )

        fun formatPresetDuration(durationMs: Long): String {
            val minutes = durationMs / (1000 * 60)
            return when {
                minutes < 60 -> "${minutes}m"
                minutes % 60 == 0L -> "${minutes / 60}h"
                else -> "${minutes / 60}h ${minutes % 60}m"
            }
        }
    }
}