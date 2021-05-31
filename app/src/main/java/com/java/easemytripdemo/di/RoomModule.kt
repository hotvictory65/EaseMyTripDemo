package com.java.easemytripdemo.di

import android.content.Context
import androidx.room.Room
import com.java.easemytripdemo.dao.TripDataDao
import com.java.easemytripdemo.roomdatabase.RoomDatabaseMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): RoomDatabaseMain {
        return Room.databaseBuilder(context, RoomDatabaseMain::class.java, RoomDatabaseMain.DATABASE_NAME).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideGroupChatDao(roomDatabase: RoomDatabaseMain):TripDataDao{
        return roomDatabase.tripDap()
    }
}