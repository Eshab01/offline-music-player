package com.offlinemusicplayer.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isSmartPlaylist: Boolean = false,
    val smartCriteria: String? = null, // JSON criteria for smart playlists
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val trackCount: Int = 0,
    val totalDuration: Long = 0L
)