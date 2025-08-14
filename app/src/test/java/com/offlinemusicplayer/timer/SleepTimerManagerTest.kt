package com.offlinemusicplayer.timer

import androidx.media3.common.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SleepTimerManagerTest {

    @Mock
    private lateinit var mockPlayer: Player

    private lateinit var sleepTimerManager: SleepTimerManager
    private val testScope = TestScope()

    @Before
    fun setup() {
        sleepTimerManager = SleepTimerManager(mockPlayer, testScope)
    }

    @Test
    fun `formatTime formats correctly`() {
        assertEquals("00:30", sleepTimerManager.formatTime(30000L))
        assertEquals("01:30", sleepTimerManager.formatTime(90000L))
        assertEquals("1:01:30", sleepTimerManager.formatTime(3690000L))
    }

    @Test
    fun `formatPresetDuration formats correctly`() {
        assertEquals("5m", SleepTimerManager.formatPresetDuration(5 * 60 * 1000L))
        assertEquals("1h", SleepTimerManager.formatPresetDuration(60 * 60 * 1000L))
        assertEquals("1h 30m", SleepTimerManager.formatPresetDuration(90 * 60 * 1000L))
    }

    @Test
    fun `startTimer sets active state`() {
        sleepTimerManager.startTimer(60000L, false)
        
        assertTrue(sleepTimerManager.isActive.value)
        assertEquals(60000L, sleepTimerManager.totalTimeMs.value)
    }

    @Test
    fun `cancelTimer clears state`() {
        sleepTimerManager.startTimer(60000L, false)
        sleepTimerManager.cancelTimer()
        
        assertFalse(sleepTimerManager.isActive.value)
        assertEquals(0L, sleepTimerManager.remainingTimeMs.value)
        assertEquals(0L, sleepTimerManager.totalTimeMs.value)
        verify(mockPlayer).setVolume(1.0f)
    }

    @Test
    fun `timer completion pauses player`() = testScope.runTest {
        sleepTimerManager.startTimer(1000L, false) // 1 second
        
        // Advance time to trigger timer completion
        advanceTimeBy(1100L)
        
        verify(mockPlayer).pause()
        verify(mockPlayer).setVolume(1.0f)
        assertFalse(sleepTimerManager.isActive.value)
    }
}