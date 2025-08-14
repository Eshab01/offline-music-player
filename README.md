# Offline Music Player

A complete, privacy-focused Android music player built with modern technologies and offline-first design principles.

## 🎵 Features

### Phase 1 - Foundation & Core Playback
- **Modern UI**: Jetpack Compose with Material 3 design and dynamic colors
- **Comprehensive Navigation**: Home, Library, Search, Now Playing, and Settings
- **Advanced Theming**: Dynamic colors, light/dark themes, accessibility support
- **Media Playback**: Media3/ExoPlayer with MediaLibraryService and foreground notifications
- **Database**: Room database with tracks, genres, playlists, and play history
- **Local Scanning**: MediaStore integration with optional folder selection via SAF
- **Settings Management**: DataStore for preferences with privacy controls
- **Permissions**: Comprehensive permission handling for audio and notifications
- **Hidden App Label**: Privacy-focused launcher icon without visible app name

### Phase 2 - Library Power Features
- **Genre Management**: CRUD operations, custom genres, batch tagging, filtering
- **Playlists**: Manual and smart playlists with drag-and-drop reordering
- **Queue Management**: Comprehensive queue with shuffle/repeat modes
- **Audio Effects**: Equalizer (5/10 band), bass boost, loudness enhancer, playback speed
- **Metadata Editing**: Overlay system for editing track information
- **Rich Display**: Album artwork (embedded + folder.jpg), bitrate, sample rate, file details

### Phase 3 - Utilities & Insights
- **Lyrics Support**: Embedded lyrics and synchronized .lrc files with real-time highlighting
- **Duplicate Detection**: Advanced detection with audio duration+hash heuristics
- **Statistics**: Play counts, listening time, most-played tracks, smart insights
- **Export/Import**: JSON metadata export, ZIP with lyrics, merge/replace options
- **Cloud Sync**: Optional Firebase-based sync for playlists, genres, settings (privacy toggle)

### Phase 4 - Smart/Advanced
- **Mood Tagging**: Manual mood assignment with smart queue suggestions
- **Gesture Controls**: Swipe navigation, slide-to-seek, gesture customization
- **Floating Player**: Optional system overlay mini-player (with permission)
- **Accessibility**: Content descriptions, large fonts, high-contrast, TalkBack support
- **App Security**: PIN/biometric lock with BiometricPrompt integration

## 🏗️ Architecture

### Technology Stack
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Hilt dependency injection
- **Database**: Room with comprehensive relationships and FTS5 search
- **Media**: Media3/ExoPlayer with MediaSession
- **Settings**: DataStore Preferences
- **Background Work**: WorkManager with Hilt integration
- **Image Loading**: Coil for album artwork
- **Navigation**: Navigation Compose with type-safe routes

### Project Structure
```
app/src/main/java/com/offlinemusicplayer/
├── core/
│   └── di/                    # Dependency injection modules
├── data/
│   ├── database/              # Room database, DAOs
│   ├── model/                 # Data models, DTOs
│   └── repository/            # Repository implementations
├── service/                   # Media playback service
├── scanner/                   # Library scanning utilities
├── ui/
│   ├── components/            # Reusable UI components
│   ├── navigation/            # Navigation setup
│   ├── screens/               # Screen composables
│   └── theme/                 # Material 3 theming
└── MusicPlayerApplication.kt  # Application class
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK with API 24+ (Android 7.0) support
- Kotlin 1.9.22+
- Gradle 8.4+

### Build Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Eshab01/offline-music-player.git
   cd offline-music-player
   ```

2. Open in Android Studio and sync project

3. Build and run:
   ```bash
   ./gradlew assembleDebug
   # or for release build
   ./gradlew assembleRelease
   ```

### Required Permissions
- **READ_MEDIA_AUDIO** (API 33+) / **READ_EXTERNAL_STORAGE** (API 32-): Access music files
- **POST_NOTIFICATIONS** (API 33+): Playback notification controls
- **FOREGROUND_SERVICE**: Background media playback
- **WAKE_LOCK**: Prevent sleep during playback

### Optional Permissions
- **SYSTEM_ALERT_WINDOW**: Floating mini-player overlay
- **USE_BIOMETRIC**: Biometric app lock
- **INTERNET**: Cloud sync and album art (when offline mode disabled)

## 📱 Usage

### First Run Setup
1. **Welcome**: Introduction to privacy-focused design
2. **Permissions**: Grant required audio and notification permissions
3. **Library Scan**: Automatic detection of music files on device
4. **Privacy Settings**: Confirm offline-first operation

### Navigation
- **Home**: Quick access to recent, popular, and favorite tracks
- **Library**: Browse by Songs, Albums, Artists, Genres, Playlists, Folders
- **Search**: Powerful search with filters and FTS5 full-text search
- **Now Playing**: Full player with lyrics, queue, and controls
- **Settings**: Comprehensive customization options

### Key Features
- **Smart Playlists**: Auto-updating playlists (Recently Added, Most Played, Never Played)
- **Batch Operations**: Multi-select for genre assignment, playlist management
- **Queue Management**: Drag-and-drop reordering, save queue as playlist
- **Metadata Overlay**: Edit track information without modifying files
- **Export/Import**: Backup your library metadata and playlists

## 🔧 Configuration

### Privacy Controls
All network features are **disabled by default**. Enable in Settings > Privacy:
- **Cloud Sync**: Sync playlists and settings via Firebase
- **Crash Reporting**: Anonymous crash reports via Firebase Crashlytics
- **Analytics**: Usage analytics for app improvement

### Audio Settings
- **Equalizer**: 5-band or 10-band equalizer (device dependent)
- **Audio Effects**: Bass boost, loudness enhancer
- **Playback**: Speed control, silence skipping, crossfade
- **Focus Management**: Audio ducking, auto-pause on headphone disconnect

### Library Management
- **Auto Scan**: Automatic library updates
- **Folder Exclusion**: Skip specific directories
- **Minimum Duration**: Filter very short audio files
- **Duplicate Detection**: Find and manage duplicate tracks

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Code Quality
```bash
# Lint checks
./gradlew lintDebug

# Kotlin code style
./gradlew ktlintCheck

# Static analysis
./gradlew detekt
```

## 🏃‍♂️ CI/CD

GitHub Actions workflow automatically:
- Runs tests and code quality checks
- Builds debug and release APKs
- Performs security scans
- Tests on multiple API levels (24, 29, 33)

## 🔒 Privacy & Security

### Data Storage
- **Local Only**: All music data remains on device by default
- **No Tracking**: No analytics or tracking unless explicitly enabled
- **Minimal Network**: All cloud features are opt-in and clearly labeled

### Security Features
- **App Lock**: PIN or biometric protection
- **Secure Storage**: Sensitive settings encrypted with DataStore
- **Permission Scoping**: Minimal required permissions with clear rationale

### Cloud Sync (Optional)
When enabled, only the following data is synced:
- Playlist names and track references (not audio files)
- Genre assignments and mood tags
- App settings and preferences
- Play history statistics

## 🛠️ Development

### Contributing Guidelines
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Follow existing code style and architecture patterns
4. Add tests for new functionality
5. Update documentation as needed
6. Submit a pull request

### Code Style
- **Kotlin**: Follow official Kotlin coding conventions
- **Compose**: Use modern Compose patterns and best practices
- **Architecture**: MVVM with Hilt DI, reactive programming with Flows

### Debug Features
Debug builds include additional features:
- Detailed logging with Timber
- Database inspection tools
- Network request logging (when enabled)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design 3** for the beautiful design system
- **Jetpack Compose** for modern Android UI development
- **Media3** for robust media playback capabilities
- **Room** for local database management
- **Hilt** for dependency injection
- **Coil** for efficient image loading

## 📞 Support

For issues, feature requests, or questions:
1. Check existing [GitHub Issues](https://github.com/Eshab01/offline-music-player/issues)
2. Create a new issue with detailed information
3. Include device information, Android version, and reproduction steps

## 🗺️ Roadmap

### Upcoming Features
- [ ] Advanced audio visualization
- [ ] Custom themes and color schemes
- [ ] Podcast support
- [ ] Sleep timer and alarm integration
- [ ] Scrobbling to Last.fm (optional)
- [ ] CarPlay/Android Auto support
- [ ] Folder-based playlist generation
- [ ] Advanced audio format support (FLAC, DSD)

### Known Limitations
- **File Tag Writing**: Limited by Android's scoped storage on API 30+
- **Equalizer**: Availability depends on device audio capabilities
- **Floating Player**: Requires SYSTEM_ALERT_WINDOW permission
- **Cloud Sync**: Requires network connectivity and Firebase setup

---

**Built with ❤️ for music lovers who value privacy and control over their listening experience.**
