package com.offlinemusicplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.offlinemusicplayer.service.MusicPlayerService

class MediaButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MEDIA_BUTTON) {
            val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            keyEvent?.let { event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    handleMediaButton(context, event.keyCode)
                }
            }
        }
    }

    private fun handleMediaButton(context: Context, keyCode: Int) {
        val sessionToken = SessionToken(context, android.content.ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture: ListenableFuture<MediaController> =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            var controller: MediaController? = null
            try {
                controller = controllerFuture.get()
                when (keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY -> controller.play()
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> controller.pause()
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> if (controller.isPlaying) controller.pause() else controller.play()
                    KeyEvent.KEYCODE_MEDIA_NEXT -> controller.seekToNext()
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> controller.seekToPrevious()
                    KeyEvent.KEYCODE_MEDIA_STOP -> controller.stop()
                }
            } finally {
                controller?.release()
            }
        }, context.mainExecutor)
    }
}