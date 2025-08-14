package com.offlinemusicplayer.data.repository

import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(
    private val musicDao: MusicDao,
) {

    // =============== PLAYBACK STATISTICS ===============

    suspend fun recordPlayback(
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

        // Update track statistics
        if (isCompleted) {
            musicDao.incrementPlayCount(trackId)
        } else if (duration < 15000) { // Less than 15 seconds counts as skip
            musicDao.incrementSkipCount(trackId)
        }
    }

    suspend fun getTrackStats(trackId: Long): TrackWithStats? = 
        musicDao.getTrackStats(trackId)

    suspend fun getRecentPlayHistory(limit: Int = 100): List<PlayHistory> = 
        musicDao.getRecentPlayHistory(limit)

    // =============== LISTENING STATISTICS ===============

    suspend fun getListeningStats(): ListeningStats {
        val totalTracks = musicDao.getTrackCount()
        val favoriteCount = musicDao.getFavoriteTrackCount()
        val totalDuration = musicDao.getTotalDuration() ?: 0L
        val playHistory = getRecentPlayHistory(1000)
        
        val totalPlayTime = playHistory.sumOf { it.duration }
        val totalPlays = playHistory.count { it.isCompleted }
        val totalSkips = playHistory.count { !it.isCompleted && it.duration < 15000 }
        
        val averageSessionLength = if (playHistory.isNotEmpty()) {
            playHistory.groupBy { it.playedAt / (1000 * 60 * 30) } // Group by 30-min sessions
                .values
                .map { session -> session.sumOf { it.duration } }
                .average()
        } else 0.0

        return ListeningStats(
            totalTracks = totalTracks,
            favoriteCount = favoriteCount,
            totalLibraryDuration = totalDuration,
            totalPlayTime = totalPlayTime,
            totalPlays = totalPlays,
            totalSkips = totalSkips,
            averageSessionLength = averageSessionLength.toLong(),
            skipRate = if (totalPlays > 0) totalSkips.toFloat() / (totalPlays + totalSkips) else 0f
        )
    }

    suspend fun getTopTracks(limit: Int = 50): List<Track> = 
        musicDao.getMostPlayedTracks(limit)

    suspend fun getRecentlyPlayedTracks(limit: Int = 50): List<Track> = 
        musicDao.getRecentlyPlayedTracks(limit)

    suspend fun getNeverPlayedTracks(limit: Int = 100): List<Track> = 
        musicDao.getNeverPlayedTracksForSmart(limit)

    // =============== TIME-BASED STATISTICS ===============

    suspend fun getWeeklyStats(): WeeklyStats {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val weekHistory = getRecentPlayHistory(1000)
            .filter { it.playedAt >= oneWeekAgo }

        val dailyStats = (0..6).map { daysAgo ->
            val dayStart = System.currentTimeMillis() - (daysAgo * 24 * 60 * 60 * 1000L)
            val dayEnd = dayStart + (24 * 60 * 60 * 1000L)
            
            val dayHistory = weekHistory.filter { it.playedAt in dayStart..dayEnd }
            
            DailyStats(
                date = dayStart,
                totalPlayTime = dayHistory.sumOf { it.duration },
                tracksPlayed = dayHistory.count { it.isCompleted },
                uniqueTracks = dayHistory.map { it.trackId }.distinct().size
            )
        }.reversed()

        return WeeklyStats(
            dailyStats = dailyStats,
            totalWeeklyPlayTime = dailyStats.sumOf { it.totalPlayTime },
            averageDailyPlayTime = dailyStats.map { it.totalPlayTime }.average().toLong(),
            mostActiveDay = dailyStats.maxByOrNull { it.totalPlayTime }?.date ?: 0L
        )
    }

    suspend fun getMonthlyStats(): MonthlyStats {
        val oneMonthAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val monthHistory = getRecentPlayHistory(5000)
            .filter { it.playedAt >= oneMonthAgo }

        return MonthlyStats(
            totalPlayTime = monthHistory.sumOf { it.duration },
            tracksPlayed = monthHistory.count { it.isCompleted },
            uniqueTracks = monthHistory.map { it.trackId }.distinct().size,
            averageDailyPlayTime = monthHistory.sumOf { it.duration } / 30,
            topGenres = getTopGenresForPeriod(monthHistory),
            topArtists = getTopArtistsForPeriod(monthHistory)
        )
    }

    // =============== GENRE AND ARTIST STATISTICS ===============

    private suspend fun getTopGenresForPeriod(playHistory: List<PlayHistory>): List<GenreStats> {
        // This would require complex queries joining with track genres
        // Simplified implementation for now
        return emptyList()
    }

    private suspend fun getTopArtistsForPeriod(playHistory: List<PlayHistory>): List<ArtistStats> {
        // This would require complex queries joining with tracks
        // Simplified implementation for now
        return emptyList()
    }

    // =============== MAINTENANCE ===============

    suspend fun cleanupOldData(maxHistoryEntries: Int = 10000) {
        musicDao.cleanupOldPlayHistory(maxHistoryEntries)
    }

    suspend fun recalculateAllStats() {
        // This would recalculate all derived statistics
        // Implementation would depend on specific requirements
        TODO("Implement full stats recalculation")
    }
}

// =============== DATA CLASSES ===============

data class ListeningStats(
    val totalTracks: Int,
    val favoriteCount: Int,
    val totalLibraryDuration: Long,
    val totalPlayTime: Long,
    val totalPlays: Int,
    val totalSkips: Int,
    val averageSessionLength: Long,
    val skipRate: Float
)

data class WeeklyStats(
    val dailyStats: List<DailyStats>,
    val totalWeeklyPlayTime: Long,
    val averageDailyPlayTime: Long,
    val mostActiveDay: Long
)

data class DailyStats(
    val date: Long,
    val totalPlayTime: Long,
    val tracksPlayed: Int,
    val uniqueTracks: Int
)

data class MonthlyStats(
    val totalPlayTime: Long,
    val tracksPlayed: Int,
    val uniqueTracks: Int,
    val averageDailyPlayTime: Long,
    val topGenres: List<GenreStats>,
    val topArtists: List<ArtistStats>
)

data class GenreStats(
    val genreName: String,
    val playCount: Int,
    val totalPlayTime: Long
)

data class ArtistStats(
    val artistName: String,
    val playCount: Int,
    val totalPlayTime: Long,
    val uniqueTracks: Int
)