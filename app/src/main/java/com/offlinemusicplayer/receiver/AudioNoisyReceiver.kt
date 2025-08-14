package com.offlinemusicplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.offlinemusicplayer.service.MusicPlayerService

class AudioNoisyReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            pausePlayback(context)
        }
    }

    private fun pausePlayback(context: Context) {
        val sessionToken = SessionToken(context, android.content.ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture: ListenableFuture<MediaController> =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            var controller: MediaController? = null
            try {
                controller = controllerFuture.get()
                if (controller.isPlaying) controller.pause()
            } catch (_: Exception) {
                // Service not available; ignore
            } finally {
                controller?.release()
            }
        }, context.mainExecutor)
    }
}
