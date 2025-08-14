package com.offlinemusicplayer.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.offlinemusicplayer.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY name ASC")
    fun getAllAlbums(): PagingSource<Int, Album>

    @Query("SELECT * FROM albums WHERE name LIKE '%' || :query || '%' OR artistName LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchAlbums(query: String): PagingSource<Int, Album>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): Album?

    @Query("SELECT * FROM albums WHERE name = :name AND artistName = :artistName")
    suspend fun getAlbumByNameAndArtist(name: String, artistName: String): Album?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album): Long

    @Update
    suspend fun updateAlbum(album: Album)

    @Delete
    suspend fun deleteAlbum(album: Album)

    @Query("UPDATE albums SET trackCount = (SELECT COUNT(*) FROM tracks WHERE tracks.album = albums.name)")
    suspend fun updateTrackCounts()
}

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists ORDER BY name ASC")
    fun getAllArtists(): PagingSource<Int, Artist>

    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtists(query: String): PagingSource<Int, Artist>

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getArtistById(id: Long): Artist?

    @Query("SELECT * FROM artists WHERE name = :name")
    suspend fun getArtistByName(name: String): Artist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist): Long

    @Update
    suspend fun updateArtist(artist: Artist)

    @Delete
    suspend fun deleteArtist(artist: Artist)

    @Query("UPDATE artists SET trackCount = (SELECT COUNT(*) FROM tracks WHERE tracks.artist = artists.name)")
    suspend fun updateTrackCounts()

    @Query("UPDATE artists SET albumCount = (SELECT COUNT(DISTINCT album) FROM tracks WHERE tracks.artist = artists.name)")
    suspend fun updateAlbumCounts()
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY dateModified DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists ORDER BY dateModified DESC")
    fun getAllPlaylistsPaged(): PagingSource<Int, Playlist>

    @Query("SELECT * FROM playlists WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPlaylists(query: String): PagingSource<Int, Playlist>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Query("SELECT * FROM playlists WHERE name = :name")
    suspend fun getPlaylistByName(name: String): Playlist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("UPDATE playlists SET trackCount = (SELECT COUNT(*) FROM playlist_songs WHERE playlist_songs.playlistId = playlists.id)")
    suspend fun updateTrackCounts()

    // Playlist songs
    @Query("""
        SELECT tracks.* FROM tracks 
        INNER JOIN playlist_songs ON tracks.id = playlist_songs.trackId 
        WHERE playlist_songs.playlistId = :playlistId 
        ORDER BY playlist_songs.position ASC
    """)
    fun getPlaylistTracks(playlistId: Long): PagingSource<Int, Track>

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getPlaylistSongs(playlistId: Long): List<PlaylistSong>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)

    @Delete
    suspend fun deletePlaylistSong(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("UPDATE playlist_songs SET position = position - 1 WHERE playlistId = :playlistId AND position > :removedPosition")
    suspend fun reorderAfterRemoval(playlistId: Long, removedPosition: Int)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getNextPosition(playlistId: Long): Int

    // Play counts
    @Query("SELECT * FROM play_counts WHERE trackId = :trackId")
    suspend fun getPlayCount(trackId: Long): PlayCount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayCount(playCount: PlayCount)

    @Query("UPDATE play_counts SET playCount = playCount + 1, lastPlayed = :timestamp WHERE trackId = :trackId")
    suspend fun incrementPlayCount(trackId: Long, timestamp: Long)

    @Query("""
        INSERT OR REPLACE INTO play_counts (trackId, playCount, lastPlayed) 
        VALUES (:trackId, COALESCE((SELECT playCount FROM play_counts WHERE trackId = :trackId), 0) + 1, :timestamp)
    """)
    suspend fun recordPlay(trackId: Long, timestamp: Long)
}