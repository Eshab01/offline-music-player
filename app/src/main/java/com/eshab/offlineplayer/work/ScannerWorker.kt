package com.eshab.offlineplayer.work

import android.content.Context
import android.provider.MediaStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eshab.offlineplayer.data.db.AppDatabase
import com.eshab.offlineplayer.data.db.TrackEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScannerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val db: AppDatabase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val tracks = mutableListOf<TrackEntity>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        applicationContext.contentResolver.query(uri, projection, "${MediaStore.Audio.Media.IS_MUSIC}=1", null, null)?.use { cursor ->
            val idxData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val idxTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val idxArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val idxAlbum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val idxDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val idxMime = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val idxSize = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val idxAdded = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val path = cursor.getString(idxData)
                val title = cursor.getString(idxTitle)
                val artist = cursor.getString(idxArtist)
                val album = cursor.getString(idxAlbum)
                val duration = cursor.getLong(idxDuration)
                val mime = cursor.getString(idxMime)
                val size = cursor.getLong(idxSize)
                val added = cursor.getLong(idxAdded)

                tracks += TrackEntity(
                    uri = "file://$path",
                    title = title,
                    artist = artist,
                    album = album,
                    durationMs = duration,
                    bitrate = null,
                    sampleRate = null,
                    mimeType = mime,
                    sizeBytes = size,
                    dateAdded = added,
                    albumArt = null,
                    folder = path.substringBeforeLast("/", missingDelimiterValue = "")
                )
            }
        }

        val dao = db.trackDao()
        dao.insertAll(tracks)
        return Result.success()
    }
}