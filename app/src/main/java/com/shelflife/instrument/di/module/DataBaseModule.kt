package com.shelflife.instrument.di.module

import android.content.Context
import com.shelflife.instrument.room.Dao
import com.shelflife.instrument.room.RoomDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    fun provideRoomDb(context: Context): RoomDb {
        return RoomDb.getDb(context.applicationContext)
    }

    @Provides
    @Singleton
    fun provideDao(context: Context): Dao {
        return RoomDb.getDb(context.applicationContext).getDao()
    }
}