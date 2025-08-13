package com.offlinemusicplayer.data.repository

import com.offlinemusicplayer.data.database.SongDao
import com.offlinemusicplayer.data.database.SongEntity
import com.offlinemusicplayer.domain.model.Song
import com.offlinemusicplayer.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSongById(id: Long): Flow<Song?> {
        return songDao.getAllSongs().map { entities ->
            entities.find { it.id == id }?.toDomainModel()
        }
    }

    override fun getSongsByGenre(genre: String): Flow<List<Song>> {
        return songDao.getSongsByGenre(genre).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSongsByArtist(artist: String): Flow<List<Song>> {
        return songDao.getSongsByArtist(artist).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSongsByAlbum(album: String): Flow<List<Song>> {
        return songDao.getSongsByAlbum(album).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao.getFavoriteSongs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertSong(song: Song): Long {
        return songDao.insertSong(song.toEntity())
    }

    override suspend fun insertSongs(songs: List<Song>) {
        songDao.insertSongs(songs.map { it.toEntity() })
    }

    override suspend fun updateSong(song: Song) {
        songDao.updateSong(song.toEntity())
    }

    override suspend fun deleteSong(song: Song) {
        songDao.deleteSong(song.toEntity())
    }

    override suspend fun incrementPlayCount(songId: Long) {
        songDao.incrementPlayCount(songId, System.currentTimeMillis())
    }

    override suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        songDao.updateFavoriteStatus(songId, isFavorite)
    }

    override fun getAllArtists(): Flow<List<String>> {
        return songDao.getAllArtists()
    }

    override fun getAllAlbums(): Flow<List<String>> {
        return songDao.getAllAlbums()
    }

    override fun getAllGenres(): Flow<List<String>> {
        return songDao.getAllGenres()
    }

    private fun SongEntity.toDomainModel(): Song {
        return Song(
            id = id,
            title = title,
            artist = artist,
            album = album,
            genre = genre,
            duration = duration,
            path = path,
            albumArt = albumArt,
            year = year,
            track = track,
            bitrate = bitrate,
            sampleRate = sampleRate,
            fileSize = fileSize,
            dateAdded = dateAdded,
            playCount = playCount,
            lastPlayed = lastPlayed,
            isFavorite = isFavorite
        )
    }

    private fun Song.toEntity(): SongEntity {
        return SongEntity(
            id = id,
            title = title,
            artist = artist,
            album = album,
            genre = genre,
            duration = duration,
            path = path,
            albumArt = albumArt,
            year = year,
            track = track,
            bitrate = bitrate,
            sampleRate = sampleRate,
            fileSize = fileSize,
            dateAdded = dateAdded,
            playCount = playCount,
            lastPlayed = lastPlayed,
            isFavorite = isFavorite
        )
    }
}