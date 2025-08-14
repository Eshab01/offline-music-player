package com.offlinemusicplayer.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.offlinemusicplayer.data.model.Setting
import com.offlinemusicplayer.data.model.Track

@Database(
    entities = [Track::class, Setting::class],
    version = 1,
    exportSchema = true,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    abstract fun settingsDao(): SettingsDao

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // Create FTS5 table if supported
        if (isFts5Supported(db)) {
            createFtsTable(db)
        }
    }

    companion object {
        // Migration objects for future version changes
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Example migration - will be implemented when needed
                    // database.execSQL("ALTER TABLE tracks ADD COLUMN new_column TEXT")
                }
            }

        // FTS5 table creation
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
                """,
                )

                // Create triggers to keep FTS table in sync
                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ai AFTER INSERT ON tracks BEGIN
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre);
                    END
                """,
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_ad AFTER DELETE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre);
                    END
                """,
                )

                database.execSQL(
                    """
                    CREATE TRIGGER IF NOT EXISTS tracks_au AFTER UPDATE ON tracks BEGIN
                        INSERT INTO tracks_fts(tracks_fts, rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES ('delete', old.id, old.title, old.artist, old.album, old.genre, old.ovTitle, old.ovArtist, old.ovAlbum, old.ovGenre);
                        INSERT INTO tracks_fts(rowid, title, artist, album, genre, ovTitle, ovArtist, ovAlbum, ovGenre)
                        VALUES (new.id, new.title, new.artist, new.album, new.genre, new.ovTitle, new.ovArtist, new.ovAlbum, new.ovGenre);
                    END
                """,
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
