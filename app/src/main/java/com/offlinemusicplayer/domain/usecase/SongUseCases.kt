package com.offlinemusicplayer.domain.usecase

import com.offlinemusicplayer.domain.model.Song
import com.offlinemusicplayer.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return songRepository.getAllSongs()
    }
}

class GetSongsByGenreUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(genre: String): Flow<List<Song>> {
        return songRepository.getSongsByGenre(genre)
    }
}

class SearchSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(query: String): Flow<List<Song>> {
        return songRepository.searchSongs(query)
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long, isFavorite: Boolean) {
        songRepository.toggleFavorite(songId, isFavorite)
    }
}

class IncrementPlayCountUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(songId: Long) {
        songRepository.incrementPlayCount(songId)
    }
}