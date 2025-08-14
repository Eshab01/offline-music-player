package com.offlinemusicplayer.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data classes for complex queries and relationships
 */

data class TrackWithPlaylists(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
        entity = PlaylistTrack::class
    )
    val playlistTracks: List<PlaylistTrack>
)

data class PlaylistWithTracks(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlistId",
        entity = PlaylistTrack::class
    )
    val playlistTracks: List<PlaylistTrackWithTrack>
)

data class PlaylistTrackWithTrack(
    @Embedded val playlistTrack: PlaylistTrack,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "id"
    )
    val track: Track
)

data class TrackWithGenres(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
        entity = TrackGenre::class
    )
    val trackGenres: List<TrackGenreWithGenre>
)

data class TrackGenreWithGenre(
    @Embedded val trackGenre: TrackGenre,
    @Relation(
        parentColumn = "genreId",
        entityColumn = "id"
    )
    val genre: Genre
)

data class TrackWithMoods(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId"
    )
    val moods: List<TrackMood>
)

data class TrackWithStats(
    @Embedded val track: Track,
    val playCount: Int,
    val totalPlayTime: Long,
    val lastPlayed: Long?,
    val averageRating: Float?,
    val skipCount: Int
)

data class GenreWithTrackCount(
    @Embedded val genre: Genre,
    val trackCount: Int,
    val totalDuration: Long
)

data class AlbumInfo(
    val album: String,
    val artist: String?,
    val trackCount: Int,
    val totalDuration: Long,
    val year: Int?,
    val albumArtUri: String?
)

data class ArtistInfo(
    val artist: String,
    val albumCount: Int,
    val trackCount: Int,
    val totalDuration: Long,
    val genres: List<String>
)

/**
 * Export/Import data structures
 */
data class ExportData(
    val metadata: ExportMetadata,
    val tracks: List<Track>,
    val playlists: List<Playlist>,
    val playlistTracks: List<PlaylistTrack>,
    val genres: List<Genre>,
    val trackGenres: List<TrackGenre>,
    val trackMoods: List<TrackMood>,
    val playHistory: List<PlayHistory>,
    val settings: List<Setting>
)

data class ExportMetadata(
    val version: String,
    val exportedAt: Long,
    val appVersion: String,
    val trackCount: Int,
    val playlistCount: Int,
    val genreCount: Int
)

/**
 * Search and filter models
 */
data class SearchFilter(
    val query: String = "",
    val genres: List<String> = emptyList(),
    val moods: List<String> = emptyList(),
    val artists: List<String> = emptyList(),
    val albums: List<String> = emptyList(),
    val minDuration: Long? = null,
    val maxDuration: Long? = null,
    val dateAddedAfter: Long? = null,
    val dateAddedBefore: Long? = null,
    val hasLyrics: Boolean? = null,
    val playedRecently: Boolean? = null,
    val neverPlayed: Boolean? = null
)

data class SortOption(
    val field: SortField,
    val ascending: Boolean = true
)

enum class SortField {
    TITLE, ARTIST, ALBUM, DURATION, DATE_ADDED, DATE_MODIFIED, PLAY_COUNT, LAST_PLAYED
}