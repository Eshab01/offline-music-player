package com.offlinemusicplayer.scanner

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.offlinemusicplayer.data.repository.MusicRepository
import com.offlinemusicplayer.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicScanner(
    private val context: Context,
    private val repository: MusicRepository
) {

    companion object {
        private const val TAG = "MusicScanner"
        private val SUPPORTED_EXTENSIONS = setOf("mp3", "flac", "ogg", "m4a", "aac", "wav")
    }

    suspend fun scanMusicLibrary(): Int = withContext(Dispatchers.IO) {
        try {
            val tracks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                scanMediaStoreModern()
            } else {
                scanMediaStoreLegacy()
            }

            repository.deleteAllTracks()
            repository.insertTracks(tracks)

            Log.d(TAG, "Music scan completed. Found ${tracks.size} tracks")
            tracks.size
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning music library", e)
            0
        }
    }

    private fun scanMediaStoreModern(): List<Track> {
        val tracks = mutableListOf<Track>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (c.moveToNext()) {
                try {
                    val id = c.getLong(idColumn)
                    val title = c.getString(titleColumn) ?: "Unknown Title"
                    val artist = c.getString(artistColumn)
                    val album = c.getString(albumColumn)
                    val duration = c.getLong(durationColumn)
                    val size = c.getLong(sizeColumn)
                    val dateAdded = c.getLong(dateAddedColumn) * 1000L
                    val dateModified = c.getLong(dateModifiedColumn) * 1000L
                    val albumId = c.getLong(albumIdColumn)

                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    val track = Track(
                        uri = uri,
                        title = title,
                        artist = artist,
                        album = album,
                        genre = null,
                        duration = duration,
                        size = size,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        albumArtUri = albumArtUri
                    )
                    tracks.add(track)
                } catch (_: Exception) { /* skip bad row */ }
            }
        }
        return tracks
    }

    private fun scanMediaStoreLegacy(): List<Track> {
        // Keep existing legacy path or implement minimal variant mirroring modern.
        return emptyList()
    }
}