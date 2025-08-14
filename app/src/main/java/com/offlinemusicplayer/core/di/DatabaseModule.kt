package com.offlinemusicplayer.core.di

import android.content.Context
import androidx.room.Room
import com.offlinemusicplayer.data.database.MusicDatabase
import com.offlinemusicplayer.data.database.MusicDao
import com.offlinemusicplayer.data.database.SettingsDao
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
            context,
            MusicDatabase::class.java,
            "music_database"
        )
        .addCallback(MusicDatabase.FTS_CALLBACK)
        .fallbackToDestructiveMigration() // Remove in production
        .build()
    }

    @Provides
    fun provideMusicDao(database: MusicDatabase): MusicDao = database.musicDao()

    @Provides
    fun provideSettingsDao(database: MusicDatabase): SettingsDao = database.settingsDao()
}