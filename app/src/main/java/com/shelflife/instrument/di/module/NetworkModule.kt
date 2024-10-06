package com.shelflife.instrument.di.module

import com.shelflife.instrument.BuildConfig
import com.shelflife.instrument.MyConst
import com.shelflife.instrument.api.NetClientApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): NetClientApi =
        retrofit.create(NetClientApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SOURCE1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}