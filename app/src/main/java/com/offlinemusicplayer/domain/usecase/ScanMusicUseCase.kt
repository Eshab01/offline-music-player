package com.offlinemusicplayer.domain.usecase

import com.offlinemusicplayer.domain.repository.SongRepository
import com.offlinemusicplayer.utils.MusicScanner
import javax.inject.Inject

class ScanMusicUseCase @Inject constructor(
    private val musicScanner: MusicScanner,
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            val scannedSongs = musicScanner.scanMusicFiles()
            songRepository.insertSongs(scannedSongs)
            Result.success(scannedSongs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}