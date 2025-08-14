package com.offlinemusicplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val musicDao: MusicDao,
) {
    companion object {
        private val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
        )
    }

    // =============== TRACK OPERATIONS ===============

    fun getAllTracksPaged(): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getAllTracksPaged() },
        ).flow

    fun getAllTracksFlow(): Flow<List<Track>> = musicDao.getAllTracksFlow()

    suspend fun getTrackById(id: Long): Track? = musicDao.getTrackById(id)

    suspend fun getTrackByUri(uri: String): Track? = musicDao.getTrackByUri(uri)

    suspend fun getTracksByAudioHash(hash: String, excludeId: Long = -1): List<Track> = 
        musicDao.getTracksByAudioHash(hash, excludeId)

    suspend fun getTracksByFolder(folderPath: String): List<Track> = 
        musicDao.getTracksByFolder(folderPath)

    fun getFavoriteTracksPaged(): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getFavoriteTracksPaged() },
        ).flow

    suspend fun getRecentlyPlayedTracks(limit: Int = 50): List<Track> = 
        musicDao.getRecentlyPlayedTracks(limit)

    fun getNeverPlayedTracksPaged(): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getNeverPlayedTracksPaged() },
        ).flow

    suspend fun getMostPlayedTracks(limit: Int = 50): List<Track> = 
        musicDao.getMostPlayedTracks(limit)

    fun getRecentlyAddedTracksPaged(daysBack: Int = 30): Flow<PagingData<Track>> {
        val timestamp = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000L)
        return Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getRecentlyAddedTracksPaged(timestamp) },
        ).flow
    }

    fun getTracksWithLyricsPaged(): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getTracksWithLyricsPaged() },
        ).flow

    suspend fun insertTrack(track: Track): Long = musicDao.insertTrack(track)

    suspend fun insertTracks(tracks: List<Track>) = musicDao.insertTracks(tracks)

    suspend fun updateTrack(track: Track) = musicDao.updateTrack(track)

    suspend fun incrementPlayCount(trackId: Long) = 
        musicDao.incrementPlayCount(trackId)

    suspend fun incrementSkipCount(trackId: Long) = 
        musicDao.incrementSkipCount(trackId)

    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean) = 
        musicDao.updateFavoriteStatus(trackId, isFavorite)

    suspend fun updateRating(trackId: Long, rating: Float?) = 
        musicDao.updateRating(trackId, rating)

    suspend fun deleteTrack(track: Track) = musicDao.deleteTrack(track)

    suspend fun deleteTrackByUri(uri: String) = musicDao.deleteTrackByUri(uri)

    suspend fun deleteTracksByIds(trackIds: List<Long>) = 
        musicDao.deleteTracksByIds(trackIds)

    suspend fun deleteAllTracks() = musicDao.deleteAllTracks()

    suspend fun getTrackCount(): Int = musicDao.getTrackCount()

    suspend fun getFavoriteTrackCount(): Int = musicDao.getFavoriteTrackCount()

    suspend fun getTotalDuration(): Long = musicDao.getTotalDuration() ?: 0L

    // =============== ALBUM OPERATIONS ===============

    fun getAlbumsPaged(): Flow<PagingData<AlbumInfo>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getAlbumsPaged() },
        ).flow

    suspend fun getTracksByAlbum(albumName: String, artistName: String): List<Track> = 
        musicDao.getTracksByAlbum(albumName, artistName)

    // =============== ARTIST OPERATIONS ===============

    fun getArtistsPaged(): Flow<PagingData<ArtistInfo>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getArtistsPaged() },
        ).flow

    suspend fun getTracksByArtist(artistName: String): List<Track> = 
        musicDao.getTracksByArtist(artistName)

    // =============== SEARCH OPERATIONS ===============

    fun searchTracks(
        query: String,
        useFts: Boolean = true,
    ): Flow<PagingData<Track>> {
        val searchQuery = if (useFts) query else musicDao.escapeForSqlLike(query)

        return Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = {
                if (useFts) {
                    musicDao.searchTracksFts(searchQuery)
                } else {
                    musicDao.searchTracksLike(searchQuery)
                }
            },
        ).flow
    }

    // =============== GENRE OPERATIONS ===============

    suspend fun getTrackWithGenres(trackId: Long): TrackWithGenres? = 
        musicDao.getTrackWithGenres(trackId)

    suspend fun addGenreToTrack(trackId: Long, genreId: Long) {
        musicDao.insertTrackGenre(
            TrackGenre(trackId = trackId, genreId = genreId)
        )
    }

    suspend fun removeGenreFromTrack(trackId: Long, genreId: Long) = 
        musicDao.deleteTrackGenre(trackId, genreId)

    suspend fun removeAllGenresFromTrack(trackId: Long) = 
        musicDao.deleteAllTrackGenres(trackId)

    fun getTracksByGenre(genreId: Long): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getTracksByGenre(genreId) },
        ).flow

    // =============== MOOD OPERATIONS ===============

    suspend fun getTrackWithMoods(trackId: Long): TrackWithMoods? = 
        musicDao.getTrackWithMoods(trackId)

    suspend fun addMoodToTrack(trackId: Long, mood: String) {
        musicDao.insertTrackMood(
            TrackMood(trackId = trackId, mood = mood)
        )
    }

    suspend fun removeMoodFromTrack(trackId: Long, mood: String) = 
        musicDao.deleteTrackMood(trackId, mood)

    suspend fun removeAllMoodsFromTrack(trackId: Long) = 
        musicDao.deleteAllTrackMoods(trackId)

    suspend fun getAllMoods(): List<String> = musicDao.getAllMoods()

    fun getTracksByMood(mood: String): Flow<PagingData<Track>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getTracksByMood(mood) },
        ).flow

    // =============== STATS OPERATIONS ===============

    suspend fun getTrackStats(trackId: Long): TrackWithStats? = 
        musicDao.getTrackStats(trackId)

    suspend fun recordPlayHistory(
        trackId: Long,
        duration: Long,
        isCompleted: Boolean,
        source: String = "manual"
    ) {
        musicDao.insertPlayHistory(
            PlayHistory(
                trackId = trackId,
                duration = duration,
                isCompleted = isCompleted,
                playbackSource = source
            )
        )
        
        // Update track play count if completed
        if (isCompleted) {
            musicDao.incrementPlayCount(trackId)
        } else if (duration < 15000) { // Less than 15 seconds counts as skip
            musicDao.incrementSkipCount(trackId)
        }
    }

    suspend fun getRecentPlayHistory(limit: Int = 100): List<PlayHistory> = 
        musicDao.getRecentPlayHistory(limit)

    suspend fun cleanupOldPlayHistory(maxEntries: Int = 10000) = 
        musicDao.cleanupOldPlayHistory(maxEntries)

    // =============== SMART PLAYLIST HELPERS ===============

    suspend fun getNeverPlayedTracksForSmart(limit: Int = 100): List<Track> = 
        musicDao.getNeverPlayedTracksForSmart(limit)

    suspend fun getRecentlyAddedTracksForSmart(daysBack: Int = 7, limit: Int = 100): List<Track> {
        val timestamp = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000L)
        return musicDao.getRecentlyAddedTracksForSmart(timestamp, limit)
    }

    suspend fun getMostPlayedTracksForSmart(limit: Int = 100): List<Track> = 
        musicDao.getMostPlayedTracksForSmart(limit)
}
