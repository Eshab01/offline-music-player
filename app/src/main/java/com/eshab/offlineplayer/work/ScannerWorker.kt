package com.eshab.offlineplayer.work

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eshab.offlineplayer.data.db.AppDatabase
import com.eshab.offlineplayer.data.db.TrackEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ScannerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "ScannerWorker"
        private val SUPPORTED_EXTENSIONS = setOf("mp3", "flac", "ogg", "m4a", "aac", "wav")
    }

    override suspend fun doWork(): Result = try {
        val tracks = scanMusicLibrary()
        database.trackDao().deleteAll()
        database.trackDao().insertAll(tracks)
        Log.d(TAG, "Music scan completed. Found ${tracks.size} tracks")
        Result.success()
    } catch (e: Exception) {
        Log.e(TAG, "Error scanning music library", e)
        Result.failure()
    }

    private suspend fun scanMusicLibrary(): List<TrackEntity> =
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                scanMediaStoreModern()
            } else {
                scanMediaStoreLegacy()
            }
        }

    private fun scanMediaStoreModern(): List<TrackEntity> {
        val tracks = mutableListOf<TrackEntity>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn) * 1000L
                    val dateModified = cursor.getLong(dateModifiedColumn) * 1000L
                    val albumId = cursor.getLong(albumIdColumn)

                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    val track = TrackEntity(
                        uri = uri,
                        title = title,
                        artist = artist,
                        album = album,
                        genre = null,
                        duration = duration,
                        size = size,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        albumArtUri = albumArtUri,
                    )
                    tracks.add(track)
                } catch (_: Exception) {
                    // skip bad row
                }
            }
        }
        return tracks
    }

    private fun scanMediaStoreLegacy(): List<TrackEntity> {
        // Keep existing legacy path or implement minimal variant mirroring modern.
        return emptyList()
    }
}