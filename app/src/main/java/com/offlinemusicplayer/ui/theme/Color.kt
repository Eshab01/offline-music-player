package com.offlinemusicplayer.ui.theme

import androidx.compose.ui.graphics.Color

// Material 3 Dynamic Color Scheme
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Custom Colors for Music Player
val MusicPrimary = Color(0xFF1DB954)  // Spotify-like green
val MusicPrimaryDark = Color(0xFF1A9D50)
val MusicSecondary = Color(0xFF191414)  // Dark background
val MusicOnSecondary = Color(0xFFFFFFFF)

// Additional Music-themed Colors
val WaveformColor = Color(0xFF1DB954)
val ProgressColor = Color(0xFF1DB954)
val UnplayedColor = Color(0xFF535353)

// Status Colors
val FavoriteColor = Color(0xFFE91E63)
val PlayingIndicator = Color(0xFF4CAF50)
val ShuffleColor = Color(0xFF03DAC6)
val RepeatColor = Color(0xFFFF9800)

// Mood Colors
val MoodHappy = Color(0xFFFFEB3B)
val MoodSad = Color(0xFF3F51B5)
val MoodEnergetic = Color(0xFFFF5722)
val MoodCalm = Color(0xFF009688)
val MoodRomantic = Color(0xFFE91E63)

// Genre Colors (for visual distinction)
val GenreRock = Color(0xFFD32F2F)
val GenrePop = Color(0xFFE91E63)
val GenreJazz = Color(0xFF673AB7)
val GenreClassical = Color(0xFF3F51B5)
val GenreElectronic = Color(0xFF00BCD4)
val GenreCountry = Color(0xFF8BC34A)
val GenreRnB = Color(0xFF795548)
val GenreHipHop = Color(0xFF607D8B)
val GenreDefault = Color(0xFF9E9E9E)

fun getGenreColor(genre: String?): Color = when (genre?.lowercase()) {
    "rock", "metal", "punk", "alternative" -> GenreRock
    "pop", "dance", "disco" -> GenrePop
    "jazz", "blues", "soul" -> GenreJazz
    "classical", "opera", "symphony" -> GenreClassical
    "electronic", "techno", "house", "edm" -> GenreElectronic
    "country", "folk", "bluegrass" -> GenreCountry
    "r&b", "rnb", "funk" -> GenreRnB
    "hip hop", "rap", "hip-hop" -> GenreHipHop
    else -> GenreDefault
}

fun getMoodColor(mood: String?): Color = when (mood?.lowercase()) {
    "happy", "upbeat", "cheerful" -> MoodHappy
    "sad", "melancholy", "depressing" -> MoodSad
    "energetic", "pumped", "intense" -> MoodEnergetic
    "calm", "peaceful", "relaxing" -> MoodCalm
    "romantic", "love", "intimate" -> MoodRomantic
    else -> GenreDefault
}