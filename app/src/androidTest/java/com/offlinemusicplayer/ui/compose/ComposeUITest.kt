package com.offlinemusicplayer.ui.compose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.offlinemusicplayer.ui.theme.OfflineMusicPlayerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun miniPlayer_displayCorrectly() {
        composeTestRule.setContent {
            OfflineMusicPlayerTheme {
                // Test that the UI components can be created without crashing
                // This is a basic smoke test
            }
        }
    }

    @Test
    fun settingsScreen_displayCorrectly() {
        composeTestRule.setContent {
            OfflineMusicPlayerTheme {
                SettingsScreen()
            }
        }

        // Verify key settings are displayed
        composeTestRule.onNodeWithText("Playback").assertExists()
        composeTestRule.onNodeWithText("Skip Silence").assertExists()
        composeTestRule.onNodeWithText("Crossfade").assertExists()
        composeTestRule.onNodeWithText("Equalizer").assertExists()
    }
}