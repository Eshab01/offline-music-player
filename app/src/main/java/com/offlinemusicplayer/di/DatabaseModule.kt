package com.offlinemusicplayer.di

import android.content.Context
import androidx.room.Room
import com.offlinemusicplayer.data.database.MusicDatabase
import com.offlinemusicplayer.data.database.SongDao
import com.offlinemusicplayer.data.database.GenreDao
import com.offlinemusicplayer.data.database.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }

    @Provides
    fun provideSongDao(database: MusicDatabase): SongDao {
        return database.songDao()
    }

    @Provides
    fun provideGenreDao(database: MusicDatabase): GenreDao {
        return database.genreDao()
    }

    @Provides
    fun providePlaylistDao(database: MusicDatabase): PlaylistDao {
        return database.playlistDao()
    }
}