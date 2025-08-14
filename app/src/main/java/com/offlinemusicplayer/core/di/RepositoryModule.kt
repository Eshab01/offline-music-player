package com.offlinemusicplayer.core.di

import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.database.SettingsDao
import com.offlinemusicplayer.data.repository.MusicRepository
import com.offlinemusicplayer.data.repository.SettingsRepository
import com.offlinemusicplayer.data.repository.PlaylistRepository
import com.offlinemusicplayer.data.repository.GenreRepository
import com.offlinemusicplayer.data.repository.QueueRepository
import com.offlinemusicplayer.data.repository.StatsRepository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(musicDao: MusicDao): MusicRepository =
        MusicRepository(musicDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao,
        dataStore: DataStore<Preferences>
    ): SettingsRepository =
        SettingsRepository(settingsDao, dataStore)

    @Provides
    @Singleton
    fun providePlaylistRepository(musicDao: MusicDao): PlaylistRepository =
        PlaylistRepository(musicDao)

    @Provides
    @Singleton
    fun provideGenreRepository(musicDao: MusicDao): GenreRepository =
        GenreRepository(musicDao)

    @Provides
    @Singleton
    fun provideQueueRepository(musicDao: MusicDao): QueueRepository =
        QueueRepository(musicDao)

    @Provides
    @Singleton
    fun provideStatsRepository(musicDao: MusicDao): StatsRepository =
        StatsRepository(musicDao)
}