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
class PlaylistRepository @Inject constructor(
    private val musicDao: MusicDao,
) {
    companion object {
        private val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
        )
    }

    // =============== PLAYLIST OPERATIONS ===============

    fun getAllPlaylists(): Flow<List<Playlist>> = musicDao.getAllPlaylists()

    fun getAllPlaylistsPaged(): Flow<PagingData<Playlist>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getAllPlaylistsPaged() },
        ).flow

    suspend fun getPlaylistById(id: Long): Playlist? = musicDao.getPlaylistById(id)

    suspend fun getPlaylistByName(name: String): Playlist? = musicDao.getPlaylistByName(name)

    suspend fun createPlaylist(
        name: String,
        description: String = "",
        isSmartPlaylist: Boolean = false,
        smartCriteria: String? = null
    ): Long {
        val playlist = Playlist(
            name = name,
            description = description,
            isSmartPlaylist = isSmartPlaylist,
            smartCriteria = smartCriteria
        )
        return musicDao.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        musicDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePlaylist(playlist: Playlist) = musicDao.deletePlaylist(playlist)

    suspend fun deletePlaylistById(playlistId: Long) = musicDao.deletePlaylistById(playlistId)

    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks? = 
        musicDao.getPlaylistWithTracks(playlistId)

    suspend fun updatePlaylistCounts(playlistId: Long) = 
        musicDao.updatePlaylistCounts(playlistId)

    // =============== PLAYLIST TRACK OPERATIONS ===============

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val position = musicDao.getNextPlaylistTrackPosition(playlistId)
        val playlistTrack = PlaylistTrack(
            playlistId = playlistId,
            trackId = trackId,
            position = position
        )
        musicDao.insertPlaylistTrack(playlistTrack)
        updatePlaylistCounts(playlistId)
    }

    suspend fun addTracksToPlaylist(playlistId: Long, trackIds: List<Long>) {
        var position = musicDao.getNextPlaylistTrackPosition(playlistId)
        val playlistTracks = trackIds.map { trackId ->
            PlaylistTrack(
                playlistId = playlistId,
                trackId = trackId,
                position = position++
            )
        }
        musicDao.insertPlaylistTracks(playlistTracks)
        updatePlaylistCounts(playlistId)
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        musicDao.removeTrackFromPlaylist(playlistId, trackId)
        updatePlaylistCounts(playlistId)
    }

    suspend fun removeAllTracksFromPlaylist(playlistId: Long) {
        musicDao.removeAllTracksFromPlaylist(playlistId)
        updatePlaylistCounts(playlistId)
    }

    suspend fun getPlaylistTracks(playlistId: Long): List<PlaylistTrackWithTrack> = 
        musicDao.getPlaylistTracks(playlistId)

    fun getPlaylistTracksPaged(playlistId: Long): Flow<PagingData<PlaylistTrackWithTrack>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { musicDao.getPlaylistTracksPaged(playlistId) },
        ).flow

    suspend fun reorderPlaylistTracks(playlistId: Long, trackOrders: List<Pair<Long, Int>>) {
        trackOrders.forEach { (playlistTrackId, newPosition) ->
            musicDao.updatePlaylistTrackPosition(playlistTrackId, newPosition)
        }
    }

    // =============== SMART PLAYLIST OPERATIONS ===============

    suspend fun updateSmartPlaylist(playlistId: Long) {
        val playlist = getPlaylistById(playlistId) ?: return
        if (!playlist.isSmartPlaylist || playlist.smartCriteria == null) return

        // Remove existing tracks
        removeAllTracksFromPlaylist(playlistId)

        // Add new tracks based on criteria
        val tracks = generateSmartPlaylistTracks(playlist.smartCriteria)
        if (tracks.isNotEmpty()) {
            addTracksToPlaylist(playlistId, tracks.map { it.id })
        }
    }

    private suspend fun generateSmartPlaylistTracks(criteria: String): List<Track> {
        // Parse JSON criteria and generate track list
        // This is a simplified implementation - real implementation would parse JSON
        return when {
            criteria.contains("never_played") -> musicDao.getNeverPlayedTracksForSmart(100)
            criteria.contains("recently_added") -> musicDao.getRecentlyAddedTracksForSmart(
                System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L), 100
            )
            criteria.contains("most_played") -> musicDao.getMostPlayedTracksForSmart(100)
            else -> emptyList()
        }
    }
}