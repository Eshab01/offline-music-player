package com.offlinemusicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.offlinemusicplayer.data.model.*

@Database(
    entities = [
        Track::class,
        Setting::class,
        Genre::class,
        Playlist::class,
        PlaylistTrack::class,
        PlayHistory::class,
        TrackGenre::class,
        TrackMood::class,
        QueueItem::class,
        TrackFts::class
    ],
    version = 2,
    exportSchema = true,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        // Attach this callback when building the DB
        val FTS_CALLBACK =
            object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // Create FTS5 table if supported
                    if (isFts5Supported(db)) {
                        createFtsTable(db)
                    }
                }
            }

        // Migration from version 1 to 2 - add all new tables
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Create new tables for version 2
                    createNewTables(database)
                    // Migrate existing tracks to new schema
                    migrateTracksSchema(database)
                }
            }

        private fun createNewTables(database: SupportSQLiteDatabase) {
            // Create genres table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `genres` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `isCustom` INTEGER NOT NULL DEFAULT 0,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
            """)
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_genres_name` ON `genres` (`name`)")

            // Create playlists table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `playlists` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL DEFAULT '',
                    `isSmartPlaylist` INTEGER NOT NULL DEFAULT 0,
                    `smartCriteria` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `trackCount` INTEGER NOT NULL DEFAULT 0,
                    `totalDuration` INTEGER NOT NULL DEFAULT 0
                )
            """)
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_playlists_name` ON `playlists` (`name`)")

            // Create playlist_tracks table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `playlist_tracks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `playlistId` INTEGER NOT NULL,
                    `trackId` INTEGER NOT NULL,
                    `position` INTEGER NOT NULL,
                    `addedAt` INTEGER NOT NULL,
                    FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`trackId`) REFERENCES `tracks`(`id`) ON DELETE CASCADE
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_playlistId` ON `playlist_tracks` (`playlistId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_trackId` ON `playlist_tracks` (`trackId`)")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_playlist_tracks_playlistId_trackId` ON `playlist_tracks` (`playlistId`, `trackId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_playlistId_position` ON `playlist_tracks` (`playlistId`, `position`)")

            // Create play_history table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `play_history` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `trackId` INTEGER NOT NULL,
                    `playedAt` INTEGER NOT NULL,
                    `duration` INTEGER NOT NULL DEFAULT 0,
                    `isCompleted` INTEGER NOT NULL DEFAULT 0,
                    `playbackSource` TEXT NOT NULL DEFAULT 'manual',
                    FOREIGN KEY(`trackId`) REFERENCES `tracks`(`id`) ON DELETE CASCADE
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_play_history_trackId` ON `play_history` (`trackId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_play_history_playedAt` ON `play_history` (`playedAt`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_play_history_isCompleted` ON `play_history` (`isCompleted`)")

            // Create track_genres table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `track_genres` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `trackId` INTEGER NOT NULL,
                    `genreId` INTEGER NOT NULL,
                    `addedAt` INTEGER NOT NULL,
                    FOREIGN KEY(`trackId`) REFERENCES `tracks`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`genreId`) REFERENCES `genres`(`id`) ON DELETE CASCADE
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_track_genres_trackId` ON `track_genres` (`trackId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_track_genres_genreId` ON `track_genres` (`genreId`)")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_track_genres_trackId_genreId` ON `track_genres` (`trackId`, `genreId`)")

            // Create track_moods table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `track_moods` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `trackId` INTEGER NOT NULL,
                    `mood` TEXT NOT NULL,
                    `addedAt` INTEGER NOT NULL,
                    FOREIGN KEY(`trackId`) REFERENCES `tracks`(`id`) ON DELETE CASCADE
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_track_moods_trackId` ON `track_moods` (`trackId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_track_moods_mood` ON `track_moods` (`mood`)")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_track_moods_trackId_mood` ON `track_moods` (`trackId`, `mood`)")

            // Create queue_items table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `queue_items` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `trackId` INTEGER NOT NULL,
                    `position` INTEGER NOT NULL,
                    `isCurrentlyPlaying` INTEGER NOT NULL DEFAULT 0,
                    `addedAt` INTEGER NOT NULL,
                    `source` TEXT NOT NULL DEFAULT 'manual'
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_queue_items_position` ON `queue_items` (`position`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_queue_items_trackId` ON `queue_items` (`trackId`)")
        }

        private fun migrateTracksSchema(database: SupportSQLiteDatabase) {
            // Create new tracks table with updated schema
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `tracks_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `uri` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `artist` TEXT,
                    `album` TEXT,
                    `genre` TEXT,
                    `albumArtist` TEXT,
                    `composer` TEXT,
                    `year` INTEGER,
                    `trackNumber` INTEGER,
                    `discNumber` INTEGER,
                    `duration` INTEGER NOT NULL,
                    `bitrate` INTEGER,
                    `sampleRate` INTEGER,
                    `channels` INTEGER,
                    `mimeType` TEXT,
                    `size` INTEGER NOT NULL,
                    `dateAdded` INTEGER NOT NULL,
                    `dateModified` INTEGER NOT NULL,
                    `folderPath` TEXT,
                    `fileName` TEXT,
                    `albumArtUri` TEXT,
                    `hasEmbeddedArt` INTEGER NOT NULL DEFAULT 0,
                    `hasLyrics` INTEGER NOT NULL DEFAULT 0,
                    `lyricsPath` TEXT,
                    `rating` REAL,
                    `playCount` INTEGER NOT NULL DEFAULT 0,
                    `skipCount` INTEGER NOT NULL DEFAULT 0,
                    `lastPlayed` INTEGER,
                    `isFavorite` INTEGER NOT NULL DEFAULT 0,
                    `isHidden` INTEGER NOT NULL DEFAULT 0,
                    `audioHash` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `ovTitle` TEXT,
                    `ovArtist` TEXT,
                    `ovAlbum` TEXT,
                    `ovGenre` TEXT,
                    `ovAlbumArtist` TEXT,
                    `ovComposer` TEXT,
                    `ovYear` INTEGER,
                    `ovTrackNumber` INTEGER,
                    `ovDiscNumber` INTEGER
                )
            """)

            // Copy data from old table to new table
            database.execSQL("""
                INSERT INTO tracks_new 
                (id, uri, title, artist, album, genre, duration, size, dateAdded, dateModified, 
                 albumArtUri, ovTitle, ovArtist, ovAlbum, ovGenre, createdAt, updatedAt)
                SELECT id, uri, title, artist, album, genre, duration, size, dateAdded, dateModified,
                       albumArtUri, ovTitle, ovArtist, ovAlbum, ovGenre, 
                       COALESCE(dateAdded, ${System.currentTimeMillis()}) as createdAt,
                       COALESCE(dateModified, ${System.currentTimeMillis()}) as updatedAt
                FROM tracks
            """)

            // Drop old table and rename new one
            database.execSQL("DROP TABLE tracks")
            database.execSQL("ALTER TABLE tracks_new RENAME TO tracks")

            // Recreate indexes
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tracks_uri` ON `tracks` (`uri`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_title` ON `tracks` (`title`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_artist` ON `tracks` (`artist`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_album` ON `tracks` (`album`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_genre` ON `tracks` (`genre`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_ovTitle` ON `tracks` (`ovTitle`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_ovArtist` ON `tracks` (`ovArtist`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_ovAlbum` ON `tracks` (`ovAlbum`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_ovGenre` ON `tracks` (`ovGenre`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_dateAdded` ON `tracks` (`dateAdded`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_dateModified` ON `tracks` (`dateModified`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_duration` ON `tracks` (`duration`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_bitrate` ON `tracks` (`bitrate`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_sampleRate` ON `tracks` (`sampleRate`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_year` ON `tracks` (`year`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_folderPath` ON `tracks` (`folderPath`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_hasLyrics` ON `tracks` (`hasLyrics`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_playCount` ON `tracks` (`playCount`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_tracks_lastPlayed` ON `tracks` (`lastPlayed`)")
        }

        // FTS5 table creation
        fun createFtsTable(database: SupportSQLiteDatabase) {
            try {
                // Create FTS5 virtual table if supported
                database.execSQL(
                    """
                    CREATE VIRTUAL TABLE IF NOT EXISTS tracks_fts USING fts5(
                        title, artist, album, genre, albumArtist, composer,
                        ovTitle, ovArtist, ovAlbum, ovGenre, ovAlbumArtist, ovComposer,
                        content='tracks',
                        content_rowid='id'
                    )
                    """.trimIndent(),
                )

                // Create triggers to keep FTS table in sync
                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ai AFTER INSERT ON tracks BEGIN
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, albumArtist, composer, 
                                              ovTitle, ovArtist, ovAlbum, ovGenre, ovAlbumArtist, ovComposer)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.albumArtist, new.composer,
                                new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre, new.ovAlbumArtist, new.ovComposer);
                    END
                    """.trimIndent(),
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ad AFTER DELETE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, albumArtist, composer,
                                              ovTitle, ovArtist, ovAlbum, ovGenre, ovAlbumArtist, ovComposer)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.albumArtist, old.composer,
                                old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre, old.ovAlbumArtist, old.ovComposer);
                    END
                    """.trimIndent(),
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_au AFTER UPDATE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, albumArtist, composer,
                                              ovTitle, ovArtist, ovAlbum, ovGenre, ovAlbumArtist, ovComposer)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.albumArtist, old.composer,
                                old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre, old.ovAlbumArtist, old.ovComposer);
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, albumArtist, composer,
                                              ovTitle, ovArtist, ovAlbum, ovGenre, ovAlbumArtist, ovComposer)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.albumArtist, new.composer,
                                new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre, new.ovAlbumArtist, new.ovComposer);
                    END
                    """.trimIndent(),
                )
            } catch (e: Exception) {
                // FTS5 not supported, will fall back to LIKE search
            }
        }

        fun isFts5Supported(database: SupportSQLiteDatabase): Boolean =
            try {
                database.query("SELECT fts5_version()").use { cursor ->
                    cursor.moveToFirst()
                    true
                }
            } catch (e: Exception) {
                false
            }
    }
}
