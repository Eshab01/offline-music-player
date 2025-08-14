package com.offlinemusicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.offlinemusicplayer.data.model.Setting
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.data.model.TrackFts
import com.offlinemusicplayer.data.model.Album
import com.offlinemusicplayer.data.model.Artist
import com.offlinemusicplayer.data.model.Playlist
import com.offlinemusicplayer.data.model.PlaylistSong
import com.offlinemusicplayer.data.model.PlayCount

@Database(
    entities = [Track::class, Setting::class, TrackFts::class, Album::class, Artist::class, Playlist::class, PlaylistSong::class, PlayCount::class],
    version = 2,
    exportSchema = true,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun settingsDao(): SettingsDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        // Attach this callback when building the DB (see note below)
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

        // Migration objects for future version changes
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Add new tables for version 2
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS albums (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            artistName TEXT NOT NULL,
                            albumArtUri TEXT,
                            trackCount INTEGER NOT NULL DEFAULT 0,
                            year INTEGER
                        )
                    """)
                    
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_albums_name ON albums (name)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_albums_artistName ON albums (artistName)")
                    
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS artists (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            albumCount INTEGER NOT NULL DEFAULT 0,
                            trackCount INTEGER NOT NULL DEFAULT 0
                        )
                    """)
                    
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_artists_name ON artists (name)")
                    
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS playlists (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            dateCreated INTEGER NOT NULL,
                            dateModified INTEGER NOT NULL,
                            trackCount INTEGER NOT NULL DEFAULT 0
                        )
                    """)
                    
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_playlists_name ON playlists (name)")
                    
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS playlist_songs (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            playlistId INTEGER NOT NULL,
                            trackId INTEGER NOT NULL,
                            position INTEGER NOT NULL,
                            dateAdded INTEGER NOT NULL
                        )
                    """)
                    
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_playlist_songs_playlistId ON playlist_songs (playlistId)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_playlist_songs_trackId ON playlist_songs (trackId)")
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_playlist_songs_playlistId_position ON playlist_songs (playlistId, position)")
                    
                    database.execSQL("""
                        CREATE TABLE IF NOT EXISTS play_counts (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            trackId INTEGER NOT NULL,
                            playCount INTEGER NOT NULL DEFAULT 0,
                            lastPlayed INTEGER
                        )
                    """)
                    
                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_play_counts_trackId ON play_counts (trackId)")
                }
            }

        // FTS5 table creation (kept for compatibility/fallback)
        fun createFtsTable(database: SupportSQLiteDatabase) {
            try {
                // Create FTS5 virtual table if supported
                database.execSQL(
                    """
                    CREATE VIRTUAL TABLE IF NOT EXISTS tracks_fts USING fts5(
                        title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre,
                        content='tracks',
                        content_rowid='id'
                    )
                    """.trimIndent(),
                )

                // Create triggers to keep FTS table in sync
                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ai AFTER INSERT ON tracks BEGIN
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre);
                    END
                    """.trimIndent(),
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ad AFTER DELETE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre);
                    END
                    """.trimIndent(),
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_au AFTER UPDATE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre);
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre);
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
