package com.offlinemusicplayer.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.offlinemusicplayer.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun scanMusicFiles(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
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
            val dataColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val yearColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            
            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val title = c.getString(titleColumn) ?: "Unknown Title"
                val artist = c.getString(artistColumn) ?: "Unknown Artist"
                val album = c.getString(albumColumn) ?: "Unknown Album"
                val duration = c.getLong(durationColumn)
                val path = c.getString(dataColumn) ?: ""
                val year = c.getInt(yearColumn).takeIf { it > 0 }
                val track = c.getInt(trackColumn).takeIf { it > 0 }
                val size = c.getLong(sizeColumn)
                val dateAdded = c.getLong(dateAddedColumn) * 1000 // Convert to milliseconds
                
                val song = Song(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    path = path,
                    year = year,
                    track = track,
                    fileSize = size,
                    dateAdded = dateAdded
                )
                
                songs.add(song)
            }
        }
        
        songs
    }
    
    suspend fun getAlbumArt(albumId: Long): String? = withContext(Dispatchers.IO) {
        val uri = ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.packageName +
                "/" + android.R.drawable.ic_media_play // Default placeholder
        
        // In a real implementation, you would get the actual album art
        // For now, return null to use default placeholder
        null
    }
}