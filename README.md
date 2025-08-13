# Offline Music Player

A fully offline-capable Android music player app built with modern Android development practices.

## Features

### 🗂 Offline Music Library Management
- Local music scan from user-selected folders
- Supported formats: MP3, FLAC, AAC, WAV, etc.
- ID3 tag support (Title, Artist, Album, Year)
- Editable metadata (title, artist, genre, etc.)
- Embedded album art display

### 🏷 Genre Management System
- Ability to create, edit, delete custom genres
- Assign genres manually or via batch tagging
- Filter/sort/group tracks by genre
- Smart suggestions from metadata
- Export/import genres (JSON or CSV)

### 🔁 Playback & Queue
- Background playback with notification controls
- Gapless playback, repeat, shuffle, and next/previous
- Playlist support (manual + smart playlists)
- Drag-and-drop reordering in queue or playlists

### 🎚 Audio Controls
- Built-in equalizer (5/10 band) with presets
- Bass boost, reverb, loudness normalization
- Playback speed adjustment (0.5x–2x)
- Hi-res audio support (24-bit, FLAC, ALAC)

### 🎨 Design Guidelines
- Material You support (Dynamic theming)
- Light & Dark themes
- Intuitive, minimalist UI
- Smooth animations, transitions
- Bottom navigation bar for main tabs

## Architecture

This app follows modern Android development best practices:

- **MVVM Architecture** with Repository pattern
- **Jetpack Compose** for modern UI
- **Room Database** for local storage
- **Hilt** for dependency injection
- **ExoPlayer** for audio playback
- **Material 3** for Material You theming
- **Coroutines** for asynchronous operations

## Project Structure

```
app/src/main/java/com/offlinemusicplayer/
├── data/
│   ├── database/          # Room entities, DAOs, and database
│   └── repository/        # Repository implementations
├── di/                    # Hilt dependency injection modules
├── domain/
│   ├── model/            # Domain models (Song, Genre, Playlist, etc.)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business logic use cases
├── presentation/
│   ├── navigation/       # Navigation components
│   ├── ui/
│   │   ├── screens/      # Compose screens
│   │   └── theme/        # Material You theme
│   └── viewmodel/        # ViewModels
├── service/              # Background services
└── utils/                # Utility classes
```

## Database Schema

- **Songs**: Core music file metadata
- **Genres**: Custom genre definitions with colors
- **Playlists**: User-created playlists
- **PlayHistory**: Listening statistics
- **Settings**: App configuration

## Current Status

✅ **Completed:**
- Complete Android project structure
- MVVM architecture setup
- Room database schema
- Hilt dependency injection
- Jetpack Compose UI framework
- Material You theming system
- Navigation with bottom tabs
- Basic screen layouts

🚧 **In Progress:**
- Build configuration resolution
- Music scanning implementation
- Audio playback integration

📋 **Todo:**
- Local music file scanning
- ExoPlayer integration
- Genre management features
- Playlist functionality
- Search and filtering
- Settings management
- Cloud sync capabilities

## Development Setup

This project requires:
- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK 33+
- Kotlin 1.7.20+
- Gradle 7.6.1+

## Building the Project

```bash
./gradlew assembleDebug
```

## License

Licensed under the Apache License, Version 2.0. See LICENSE for details.