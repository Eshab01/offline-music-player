package com.offlinemusicplayer.data.model

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
        Index(value = ["ovTitle"]),
        Index(value = ["ovArtist"]),
        Index(value = ["ovAlbum"]),
        Index(value = ["ovGenre"])
    ]
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val albumArtUri: String?,
    // Override fields for user customization
    val ovTitle: String? = null,
    val ovArtist: String? = null,
    val ovAlbum: String? = null,
    val ovGenre: String? = null
) {
    val displayTitle: String get() = ovTitle ?: title
    val displayArtist: String get() = ovArtist ?: artist ?: "Unknown Artist"
    val displayAlbum: String get() = ovAlbum ?: album ?: "Unknown Album"
    val displayGenre: String get() = ovGenre ?: genre ?: "Unknown Genre"
}