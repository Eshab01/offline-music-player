package com.eshab.offlineplayer.data.db

import androidx.room.*
import java.time.Instant

@Entity(tableName = "tracks", indices = [Index("uri", unique = true)])
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val durationMs: Long,
    val bitrate: Int?,
    val sampleRate: Int?,
    val mimeType: String?,
    val sizeBytes: Long?,
    val dateAdded: Long?,
    val albumArt: String?, // content uri or file path
    val folder: String?,
)

@Entity(tableName = "genres", indices = [Index("name", unique = true)])
data class GenreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

@Entity(
    tableName = "track_genres",
    primaryKeys = ["trackId", "genreId"],
    foreignKeys = [
        ForeignKey(entity = TrackEntity::class, parentColumns = ["id"], childColumns = ["trackId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = GenreEntity::class, parentColumns = ["id"], childColumns = ["genreId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("genreId"), Index("trackId")]
)
data class TrackGenreCrossRef(
    val trackId: Long,
    val genreId: Long
)

@Entity(tableName = "playlists", indices = [Index("name", unique = true)])
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = Instant.now().toEpochMilli(),
)

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(entity = PlaylistEntity::class, parentColumns = ["id"], childColumns = ["playlistId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TrackEntity::class, parentColumns = ["id"], childColumns = ["trackId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("trackId"), Index("playlistId")]
)
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Long,
    val position: Int,
)

@Entity(tableName = "play_history", indices = [Index("trackId")])
data class PlayHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackId: Long,
    val playedAt: Long = Instant.now().toEpochMilli(),
)