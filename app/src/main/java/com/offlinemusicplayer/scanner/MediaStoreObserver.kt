package com.offlinemusicplayer.scanner

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaStoreObserver(
    private val context: Context,
    private val onMediaChanged: suspend () -> Unit,
) : ContentObserver(Handler(Looper.getMainLooper())) {
    companion object {
        private const val TAG = "MediaStoreObserver"
        private const val DEBOUNCE_DELAY_MS = 500L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var debounceJob: Job? = null

    fun startObserving() {
        try {
            context.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                this,
            )
            Log.d(TAG, "Started observing MediaStore changes")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register content observer", e)
        }
    }

    fun stopObserving() {
        try {
            context.contentResolver.unregisterContentObserver(this)
            scope.cancel()
            Log.d(TAG, "Stopped observing MediaStore changes")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister content observer", e)
        }
    }

    override fun onChange(
        selfChange: Boolean,
        uri: Uri?,
    ) {
        super.onChange(selfChange, uri)

        // Debounce rapid changes
        debounceJob?.cancel()
        debounceJob =
            scope.launch {
                delay(DEBOUNCE_DELAY_MS)
                try {
                    Log.d(TAG, "MediaStore changed, triggering refresh")
                    onMediaChanged()
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling MediaStore change", e)
                }
            }
    }
}
