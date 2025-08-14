package com.eshab.offlineplayer.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["uri"], unique = true),
        Index(value = ["title"]),
        Index(value = ["artist"]),
        Index(value = ["album"]),
    ],
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val albumArtUri: String?,
)