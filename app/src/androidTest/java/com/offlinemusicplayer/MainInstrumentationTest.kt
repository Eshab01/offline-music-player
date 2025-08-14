package com.offlinemusicplayer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4::class)
class MainInstrumentationTest {
    
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.offlinemusicplayer.debug", appContext.packageName)
    }

    @Test
    fun testDatabaseCreation() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val app = appContext.applicationContext as MusicPlayerApplication
        
        // Test that database can be created
        val database = app.database
        assertNotNull(database)
        
        // Test that DAOs are accessible
        val musicDao = database.musicDao()
        val settingsDao = database.settingsDao()
        assertNotNull(musicDao)
        assertNotNull(settingsDao)
    }
}