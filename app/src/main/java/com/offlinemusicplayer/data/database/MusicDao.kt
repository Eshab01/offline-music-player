package com.offlinemusicplayer.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.offlinemusicplayer.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    
    // =============== TRACK QUERIES ===============
    
    @Query("SELECT * FROM tracks WHERE isHidden = 0 ORDER BY title ASC")
    fun getAllTracksPaged(): PagingSource<Int, Track>

    @Query("SELECT * FROM tracks WHERE isHidden = 0 ORDER BY title ASC")
    fun getAllTracksFlow(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): Track?

    @Query("SELECT * FROM tracks WHERE uri = :uri")
    suspend fun getTrackByUri(uri: String): Track?

    @Query("SELECT * FROM tracks WHERE audioHash = :hash AND id != :excludeId")
    suspend fun getTracksByAudioHash(hash: String, excludeId: Long = -1): List<Track>

    @Query("SELECT * FROM tracks WHERE folderPath = :folderPath ORDER BY fileName ASC")
    suspend fun getTracksByFolder(folderPath: String): List<Track>

    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteTracksPaged(): PagingSource<Int, Track>

    @Query("SELECT * FROM tracks WHERE lastPlayed IS NOT NULL ORDER BY lastPlayed DESC LIMIT :limit")
    suspend fun getRecentlyPlayedTracks(limit: Int = 50): List<Track>

    @Query("SELECT * FROM tracks WHERE playCount = 0 ORDER BY dateAdded DESC")
    fun getNeverPlayedTracksPaged(): PagingSource<Int, Track>

    @Query("SELECT * FROM tracks WHERE playCount > 0 ORDER BY playCount DESC, lastPlayed DESC LIMIT :limit")
    suspend fun getMostPlayedTracks(limit: Int = 50): List<Track>

    @Query("SELECT * FROM tracks WHERE dateAdded >= :timestamp ORDER BY dateAdded DESC")
    fun getRecentlyAddedTracksPaged(timestamp: Long): PagingSource<Int, Track>

    @Query("SELECT * FROM tracks WHERE hasLyrics = 1 ORDER BY title ASC")
    fun getTracksWithLyricsPaged(): PagingSource<Int, Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)

    @Update
    suspend fun updateTrack(track: Track)

    @Query("UPDATE tracks SET playCount = playCount + 1, lastPlayed = :timestamp WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tracks SET skipCount = skipCount + 1 WHERE id = :trackId")
    suspend fun incrementSkipCount(trackId: Long)

    @Query("UPDATE tracks SET isFavorite = :isFavorite WHERE id = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("UPDATE tracks SET rating = :rating WHERE id = :trackId")
    suspend fun updateRating(trackId: Long, rating: Float?)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("DELETE FROM tracks WHERE uri = :uri")
    suspend fun deleteTrackByUri(uri: String)

    @Query("DELETE FROM tracks WHERE id IN (:trackIds)")
    suspend fun deleteTracksByIds(trackIds: List<Long>)

    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()

    @Query("SELECT COUNT(*) FROM tracks WHERE isHidden = 0")
    suspend fun getTrackCount(): Int

    @Query("SELECT COUNT(*) FROM tracks WHERE isFavorite = 1")
    suspend fun getFavoriteTrackCount(): Int

    @Query("SELECT SUM(duration) FROM tracks WHERE isHidden = 0")
    suspend fun getTotalDuration(): Long?

    // =============== ALBUM QUERIES ===============
    
    @Query("""
        SELECT album as album, artist, COUNT(*) as trackCount, 
               SUM(duration) as totalDuration, year, albumArtUri
        FROM tracks 
        WHERE album IS NOT NULL AND album != '' AND isHidden = 0
        GROUP BY album, artist
        ORDER BY album ASC
    """)
    fun getAlbumsPaged(): PagingSource<Int, AlbumInfo>

    @Query("SELECT * FROM tracks WHERE album = :albumName AND artist = :artistName ORDER BY trackNumber ASC, title ASC")
    suspend fun getTracksByAlbum(albumName: String, artistName: String): List<Track>

    // =============== ARTIST QUERIES ===============
    
    @Query("""
        SELECT artist, COUNT(DISTINCT album) as albumCount, COUNT(*) as trackCount,
               SUM(duration) as totalDuration, GROUP_CONCAT(DISTINCT genre) as genres
        FROM tracks 
        WHERE artist IS NOT NULL AND artist != '' AND isHidden = 0
        GROUP BY artist
        ORDER BY artist ASC
    """)
    fun getArtistsPaged(): PagingSource<Int, ArtistInfo>

    @Query("SELECT * FROM tracks WHERE artist = :artistName ORDER BY album ASC, trackNumber ASC, title ASC")
    suspend fun getTracksByArtist(artistName: String): List<Track>

    // =============== SEARCH QUERIES ===============
    
    @Query("""
        SELECT * FROM tracks 
        WHERE isHidden = 0 AND (
            title LIKE '%' || :query || '%' 
            OR artist LIKE '%' || :query || '%' 
            OR album LIKE '%' || :query || '%'
            OR genre LIKE '%' || :query || '%'
            OR ovTitle LIKE '%' || :query || '%'
            OR ovArtist LIKE '%' || :query || '%'
            OR ovAlbum LIKE '%' || :query || '%'
            OR ovGenre LIKE '%' || :query || '%'
        )
        ORDER BY 
            CASE WHEN title LIKE :query || '%' THEN 1
                 WHEN artist LIKE :query || '%' THEN 2
                 WHEN album LIKE :query || '%' THEN 3
                 ELSE 4 END,
            title ASC
    """)
    fun searchTracksLike(query: String): PagingSource<Int, Track>

    @Query("""
        SELECT tracks.* FROM tracks_fts 
        JOIN tracks ON tracks.id = tracks_fts.rowid 
        WHERE tracks_fts MATCH :query AND tracks.isHidden = 0
        ORDER BY bm25(tracks_fts)
    """)
    fun searchTracksFts(query: String): PagingSource<Int, Track>

    fun escapeForSqlLike(input: String): String =
        input.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

    // =============== GENRE QUERIES ===============
    
    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenresPaged(): PagingSource<Int, Genre>

    @Query("SELECT * FROM genres WHERE id = :id")
    suspend fun getGenreById(id: Long): Genre?

    @Query("SELECT * FROM genres WHERE name = :name")
    suspend fun getGenreByName(name: String): Genre?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<Genre>)

    @Update
    suspend fun updateGenre(genre: Genre)

    @Delete
    suspend fun deleteGenre(genre: Genre)

    @Query("DELETE FROM genres WHERE id = :genreId")
    suspend fun deleteGenreById(genreId: Long)

    @Query("""
        SELECT g.*, COUNT(tg.trackId) as trackCount, 
               COALESCE(SUM(t.duration), 0) as totalDuration
        FROM genres g
        LEFT JOIN track_genres tg ON g.id = tg.genreId
        LEFT JOIN tracks t ON tg.trackId = t.id AND t.isHidden = 0
        GROUP BY g.id
        ORDER BY g.name ASC
    """)
    fun getGenresWithTrackCount(): Flow<List<GenreWithTrackCount>>

    // =============== TRACK-GENRE RELATIONSHIP ===============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackGenre(trackGenre: TrackGenre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackGenres(trackGenres: List<TrackGenre>)

    @Query("DELETE FROM track_genres WHERE trackId = :trackId AND genreId = :genreId")
    suspend fun deleteTrackGenre(trackId: Long, genreId: Long)

    @Query("DELETE FROM track_genres WHERE trackId = :trackId")
    suspend fun deleteAllTrackGenres(trackId: Long)

    @Query("SELECT * FROM track_genres WHERE trackId = :trackId")
    suspend fun getTrackGenres(trackId: Long): List<TrackGenre>

    @Transaction
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackWithGenres(trackId: Long): TrackWithGenres?

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN track_genres tg ON t.id = tg.trackId
        WHERE tg.genreId = :genreId AND t.isHidden = 0
        ORDER BY t.title ASC
    """)
    fun getTracksByGenre(genreId: Long): PagingSource<Int, Track>

    // =============== PLAYLIST QUERIES ===============
    
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylistsPaged(): PagingSource<Int, Playlist>

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

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks?

    @Query("""
        UPDATE playlists SET 
        trackCount = (SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId),
        totalDuration = (SELECT COALESCE(SUM(t.duration), 0) FROM playlist_tracks pt 
                        INNER JOIN tracks t ON pt.trackId = t.id WHERE pt.playlistId = :playlistId),
        updatedAt = :timestamp
        WHERE id = :playlistId
    """)
    suspend fun updatePlaylistCounts(playlistId: Long, timestamp: Long = System.currentTimeMillis())

    // =============== PLAYLIST-TRACK RELATIONSHIP ===============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(playlistTrack: PlaylistTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)  
    suspend fun insertPlaylistTracks(playlistTracks: List<PlaylistTrack>)

    @Delete
    suspend fun deletePlaylistTrack(playlistTrack: PlaylistTrack)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun removeAllTracksFromPlaylist(playlistId: Long)

    @Query("""
        SELECT pt.*, t.* FROM playlist_tracks pt
        INNER JOIN tracks t ON pt.trackId = t.id
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.position ASC
    """)
    suspend fun getPlaylistTracks(playlistId: Long): List<PlaylistTrackWithTrack>

    @Query("""
        SELECT pt.*, t.* FROM playlist_tracks pt
        INNER JOIN tracks t ON pt.trackId = t.id
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.position ASC
    """)
    fun getPlaylistTracksPaged(playlistId: Long): PagingSource<Int, PlaylistTrackWithTrack>

    @Query("UPDATE playlist_tracks SET position = :newPosition WHERE id = :playlistTrackId")
    suspend fun updatePlaylistTrackPosition(playlistTrackId: Long, newPosition: Int)

    @Query("""
        SELECT COALESCE(MAX(position), -1) + 1 
        FROM playlist_tracks 
        WHERE playlistId = :playlistId
    """)
    suspend fun getNextPlaylistTrackPosition(playlistId: Long): Int

    // =============== QUEUE QUERIES ===============
    
    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    fun getAllQueueItems(): Flow<List<QueueItem>>

    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    suspend fun getAllQueueItemsList(): List<QueueItem>

    @Query("SELECT * FROM queue_items WHERE isCurrentlyPlaying = 1 LIMIT 1")
    suspend fun getCurrentlyPlayingQueueItem(): QueueItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(queueItem: QueueItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(queueItems: List<QueueItem>)

    @Update
    suspend fun updateQueueItem(queueItem: QueueItem)

    @Delete
    suspend fun deleteQueueItem(queueItem: QueueItem)

    @Query("DELETE FROM queue_items")
    suspend fun clearQueue()

    @Query("UPDATE queue_items SET isCurrentlyPlaying = 0")
    suspend fun clearCurrentlyPlaying()

    @Query("UPDATE queue_items SET isCurrentlyPlaying = 1 WHERE id = :queueItemId")
    suspend fun setCurrentlyPlaying(queueItemId: Long)

    @Query("UPDATE queue_items SET position = :newPosition WHERE id = :queueItemId")
    suspend fun updateQueueItemPosition(queueItemId: Long, newPosition: Int)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM queue_items")
    suspend fun getNextQueuePosition(): Int

    // =============== PLAY HISTORY ===============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayHistory(playHistory: PlayHistory)

    @Query("SELECT * FROM play_history ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecentPlayHistory(limit: Int = 100): List<PlayHistory>

    @Query("""
        SELECT trackId, COUNT(*) as playCount, 
               SUM(duration) as totalPlayTime,
               MAX(playedAt) as lastPlayed,
               AVG(CASE WHEN isCompleted = 1 THEN 1.0 ELSE 0.0 END) * 5 as averageRating,
               COUNT(CASE WHEN isCompleted = 0 THEN 1 END) as skipCount
        FROM play_history 
        WHERE trackId = :trackId
        GROUP BY trackId
    """)
    suspend fun getTrackStats(trackId: Long): TrackWithStats?

    @Query("""
        DELETE FROM play_history 
        WHERE id NOT IN (
            SELECT id FROM play_history 
            ORDER BY playedAt DESC 
            LIMIT :maxEntries
        )
    """)
    suspend fun cleanupOldPlayHistory(maxEntries: Int = 10000)

    // =============== MOOD QUERIES ===============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackMood(trackMood: TrackMood)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackMoods(trackMoods: List<TrackMood>)

    @Query("DELETE FROM track_moods WHERE trackId = :trackId AND mood = :mood")
    suspend fun deleteTrackMood(trackId: Long, mood: String)

    @Query("DELETE FROM track_moods WHERE trackId = :trackId")
    suspend fun deleteAllTrackMoods(trackId: Long)

    @Query("SELECT * FROM track_moods WHERE trackId = :trackId")
    suspend fun getTrackMoods(trackId: Long): List<TrackMood>

    @Query("SELECT DISTINCT mood FROM track_moods ORDER BY mood ASC")
    suspend fun getAllMoods(): List<String>

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN track_moods tm ON t.id = tm.trackId
        WHERE tm.mood = :mood AND t.isHidden = 0
        ORDER BY t.title ASC
    """)
    fun getTracksByMood(mood: String): PagingSource<Int, Track>

    @Transaction
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackWithMoods(trackId: Long): TrackWithMoods?

    // =============== COMPLEX QUERIES FOR SMART PLAYLISTS ===============
    
    @Query("""
        SELECT t.* FROM tracks t
        LEFT JOIN play_history ph ON t.id = ph.trackId
        WHERE t.isHidden = 0
        GROUP BY t.id
        HAVING COUNT(ph.id) = 0
        ORDER BY t.dateAdded DESC
        LIMIT :limit
    """)
    suspend fun getNeverPlayedTracksForSmart(limit: Int = 100): List<Track>

    @Query("""
        SELECT t.* FROM tracks t
        WHERE t.isHidden = 0 AND t.dateAdded >= :timestamp
        ORDER BY t.dateAdded DESC
        LIMIT :limit
    """)
    suspend fun getRecentlyAddedTracksForSmart(timestamp: Long, limit: Int = 100): List<Track>

    @Query("""
        SELECT t.*, COUNT(ph.id) as playCount FROM tracks t
        INNER JOIN play_history ph ON t.id = ph.trackId
        WHERE t.isHidden = 0
        GROUP BY t.id
        ORDER BY playCount DESC, MAX(ph.playedAt) DESC
        LIMIT :limit
    """)
    suspend fun getMostPlayedTracksForSmart(limit: Int = 100): List<Track>
}
