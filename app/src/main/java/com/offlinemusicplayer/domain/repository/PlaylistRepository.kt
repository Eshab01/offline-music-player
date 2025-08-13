package com.offlinemusicplayer.domain.repository

import com.offlinemusicplayer.domain.model.Playlist
import com.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Long): Flow<Playlist?>
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>>
    suspend fun insertPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    suspend fun clearPlaylist(playlistId: Long)
}