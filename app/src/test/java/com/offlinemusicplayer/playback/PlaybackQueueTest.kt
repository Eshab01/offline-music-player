package com.offlinemusicplayer.playback

import com.offlinemusicplayer.data.model.Track
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlaybackQueueTest {

    private lateinit var playbackQueue: PlaybackQueue
    private lateinit var testTracks: List<Track>

    @Before
    fun setup() {
        playbackQueue = PlaybackQueue()
        testTracks = listOf(
            Track(
                id = 1,
                uri = "content://media/external/audio/media/1",
                title = "Track 1",
                artist = "Artist 1",
                album = "Album 1",
                genre = "Rock",
                duration = 180000,
                size = 5000000,
                dateAdded = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
                albumArtUri = null
            ),
            Track(
                id = 2,
                uri = "content://media/external/audio/media/2",
                title = "Track 2",
                artist = "Artist 2",
                album = "Album 2",
                genre = "Pop",
                duration = 210000,
                size = 6000000,
                dateAdded = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
                albumArtUri = null
            ),
            Track(
                id = 3,
                uri = "content://media/external/audio/media/3",
                title = "Track 3",
                artist = "Artist 3",
                album = "Album 3",
                genre = "Jazz",
                duration = 240000,
                size = 7000000,
                dateAdded = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
                albumArtUri = null
            )
        )
    }

    @Test
    fun setQueue_setsTracksAndIndex() {
        playbackQueue.setQueue(testTracks, 1)

        assertEquals(testTracks, playbackQueue.queue.value)
        assertEquals(1, playbackQueue.currentIndex.value)
        assertEquals(testTracks[1], playbackQueue.currentTrack)
    }

    @Test
    fun next_withNoRepeat_advancesIndex() {
        playbackQueue.setQueue(testTracks, 0)
        playbackQueue.setRepeatMode(PlaybackQueue.RepeatMode.NONE)

        val nextTrack = playbackQueue.next()

        assertEquals(testTracks[1], nextTrack)
        assertEquals(1, playbackQueue.currentIndex.value)
    }

    @Test
    fun next_atLastTrackWithNoRepeat_returnsNull() {
        playbackQueue.setQueue(testTracks, 2)
        playbackQueue.setRepeatMode(PlaybackQueue.RepeatMode.NONE)

        val nextTrack = playbackQueue.next()

        assertNull(nextTrack)
        assertEquals(2, playbackQueue.currentIndex.value)
    }

    @Test
    fun next_withRepeatAll_wrapsToBeginning() {
        playbackQueue.setQueue(testTracks, 2)
        playbackQueue.setRepeatMode(PlaybackQueue.RepeatMode.ALL)

        val nextTrack = playbackQueue.next()

        assertEquals(testTracks[0], nextTrack)
        assertEquals(0, playbackQueue.currentIndex.value)
    }

    @Test
    fun next_withRepeatOne_returnsSameTrack() {
        playbackQueue.setQueue(testTracks, 1)
        playbackQueue.setRepeatMode(PlaybackQueue.RepeatMode.ONE)

        val nextTrack = playbackQueue.next()

        assertEquals(testTracks[1], nextTrack)
        assertEquals(1, playbackQueue.currentIndex.value)
    }

    @Test
    fun removeFromQueue_removesTrackAndUpdatesIndex() {
        playbackQueue.setQueue(testTracks, 1)

        val removedTrack = playbackQueue.removeFromQueue(0)

        assertEquals(testTracks[0], removedTrack)
        assertEquals(2, playbackQueue.queue.value.size)
        assertEquals(0, playbackQueue.currentIndex.value) // Index should decrease
        assertEquals(testTracks[1], playbackQueue.currentTrack)
    }

    @Test
    fun moveTrack_updatesQueueOrder() {
        playbackQueue.setQueue(testTracks, 1)

        playbackQueue.moveTrack(0, 2)

        val expectedOrder = listOf(testTracks[1], testTracks[2], testTracks[0])
        assertEquals(expectedOrder, playbackQueue.queue.value)
        assertEquals(0, playbackQueue.currentIndex.value) // Current track moved
    }

    @Test
    fun shuffle_randomizesOrder() {
        playbackQueue.setQueue(testTracks, 0)
        val originalOrder = playbackQueue.queue.value

        playbackQueue.setShuffleMode(true)

        val shuffledOrder = playbackQueue.queue.value
        assertEquals(originalOrder.size, shuffledOrder.size)
        assertTrue(shuffledOrder.containsAll(originalOrder))
        // Current track should still be accessible
        assertNotNull(playbackQueue.currentTrack)
    }

    @Test
    fun unshuffle_restoresOriginalOrder() {
        playbackQueue.setQueue(testTracks, 1)
        val originalOrder = playbackQueue.queue.value
        val originalCurrentTrack = playbackQueue.currentTrack

        playbackQueue.setShuffleMode(true)
        playbackQueue.setShuffleMode(false)

        assertEquals(originalOrder, playbackQueue.queue.value)
        assertEquals(originalCurrentTrack, playbackQueue.currentTrack)
    }
}
