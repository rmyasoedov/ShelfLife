package com.shelflife.instrument.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences{
        return context.getSharedPreferences("userData", Context.MODE_PRIVATE)
    }
}