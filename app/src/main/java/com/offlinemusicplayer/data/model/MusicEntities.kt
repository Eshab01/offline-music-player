package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albums",
    indices = [
        Index(value = ["name"]),
        Index(value = ["artistName"])
    ]
)
data class Album(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val artistName: String,
    val albumArtUri: String? = null,
    val trackCount: Int = 0,
    val year: Int? = null
)

@Entity(
    tableName = "artists",
    indices = [Index(value = ["name"], unique = true)]
)
data class Artist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val albumCount: Int = 0,
    val trackCount: Int = 0
)

@Entity(
    tableName = "playlists",
    indices = [Index(value = ["name"], unique = true)]
)
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val trackCount: Int = 0
)

@Entity(
    tableName = "playlist_songs",
    indices = [
        Index(value = ["playlistId"]),
        Index(value = ["trackId"]),
        Index(value = ["playlistId", "position"], unique = true)
    ]
)
data class PlaylistSong(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playlistId: Long,
    val trackId: Long,
    val position: Int,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "play_counts",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class PlayCount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val playCount: Int = 0,
    val lastPlayed: Long? = null
)