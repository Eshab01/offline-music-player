package com.offlinemusicplayer.domain.repository

import com.offlinemusicplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getSongById(id: Long): Flow<Song?>
    fun getSongsByGenre(genre: String): Flow<List<Song>>
    fun getSongsByArtist(artist: String): Flow<List<Song>>
    fun getSongsByAlbum(album: String): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun searchSongs(query: String): Flow<List<Song>>
    suspend fun insertSong(song: Song): Long
    suspend fun insertSongs(songs: List<Song>)
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(song: Song)
    suspend fun incrementPlayCount(songId: Long)
    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean)
    fun getAllArtists(): Flow<List<String>>
    fun getAllAlbums(): Flow<List<String>>
    fun getAllGenres(): Flow<List<String>>
}