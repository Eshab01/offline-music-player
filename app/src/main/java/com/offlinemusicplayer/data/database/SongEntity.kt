package com.offlinemusicplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String? = null,
    val duration: Long,
    val path: String,
    val albumArt: String? = null,
    val year: Int? = null,
    val track: Int? = null,
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val fileSize: Long = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val lastPlayed: Long? = null,
    val isFavorite: Boolean = false
)