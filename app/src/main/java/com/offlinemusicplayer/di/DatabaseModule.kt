package com.offlinemusicplayer.di

import android.content.Context
import androidx.room.Room
import com.offlinemusicplayer.data.database.MusicDatabase
import com.offlinemusicplayer.data.database.SongDao
import com.offlinemusicplayer.data.database.GenreDao
import com.offlinemusicplayer.data.database.PlaylistDao
import com.offlinemusicplayer.data.repository.SongRepositoryImpl
import com.offlinemusicplayer.data.repository.GenreRepositoryImpl
import com.offlinemusicplayer.domain.repository.SongRepository
import com.offlinemusicplayer.domain.repository.GenreRepository
import dagger.Module
import dagger.Provides
import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSongRepository(
        songRepositoryImpl: SongRepositoryImpl
    ): SongRepository

    @Binds
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository
}