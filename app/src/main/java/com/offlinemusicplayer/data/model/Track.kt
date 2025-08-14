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
        Index(value = ["genre"]),
        Index(value = ["ovTitle"]),
        Index(value = ["ovArtist"]),
        Index(value = ["ovAlbum"]),
        Index(value = ["ovGenre"]),
        Index(value = ["dateAdded"]),
        Index(value = ["dateModified"]),
        Index(value = ["duration"]),
        Index(value = ["bitrate"]),
        Index(value = ["sampleRate"]),
        Index(value = ["year"]),
        Index(value = ["folderPath"]),
        Index(value = ["hasLyrics"]),
        Index(value = ["playCount"]),
        Index(value = ["lastPlayed"])
    ],
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val albumArtist: String? = null,
    val composer: String? = null,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val duration: Long,
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val channels: Int? = null,
    val mimeType: String? = null,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val folderPath: String? = null,
    val fileName: String? = null,
    val albumArtUri: String? = null,
    val hasEmbeddedArt: Boolean = false,
    val hasLyrics: Boolean = false,
    val lyricsPath: String? = null,
    val rating: Float? = null,
    val playCount: Int = 0,
    val skipCount: Int = 0,
    val lastPlayed: Long? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val audioHash: String? = null, // For duplicate detection
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Override fields for user customization
    val ovTitle: String? = null,
    val ovArtist: String? = null,
    val ovAlbum: String? = null,
    val ovGenre: String? = null,
    val ovAlbumArtist: String? = null,
    val ovComposer: String? = null,
    val ovYear: Int? = null,
    val ovTrackNumber: Int? = null,
    val ovDiscNumber: Int? = null
) {
    val displayTitle: String get() = ovTitle ?: title
    val displayArtist: String get() = ovArtist ?: artist ?: "Unknown Artist"
    val displayAlbum: String get() = ovAlbum ?: album ?: "Unknown Album"
    val displayGenre: String get() = ovGenre ?: genre ?: "Unknown Genre"
    val displayAlbumArtist: String get() = ovAlbumArtist ?: albumArtist ?: displayArtist
    val displayComposer: String get() = ovComposer ?: composer
    val displayYear: Int? get() = ovYear ?: year
    val displayTrackNumber: Int? get() = ovTrackNumber ?: trackNumber
    val displayDiscNumber: Int? get() = ovDiscNumber ?: discNumber
    
    val isEdited: Boolean get() = 
        ovTitle != null || ovArtist != null || ovAlbum != null || 
        ovGenre != null || ovAlbumArtist != null || ovComposer != null ||
        ovYear != null || ovTrackNumber != null || ovDiscNumber != null
}
