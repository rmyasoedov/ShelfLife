package com.shelflife.instrument.di.module

import com.shelflife.instrument.BuildConfig
import com.shelflife.instrument.MyConst
import com.shelflife.instrument.api.NetClientApi
import com.shelflife.instrument.api.NetGreenApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideApi1(@GreenRetrofit retrofit: Retrofit): NetGreenApi =
        retrofit.create(NetGreenApi::class.java)

    @Provides
    @Singleton
    fun provideApi2(@BaseRetrofit retrofit: Retrofit): NetClientApi =
        retrofit.create(NetClientApi::class.java)

    @Provides
    @Singleton
    @GreenRetrofit
    fun provideRetrofit1(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SOURCE1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @BaseRetrofit
    fun provideRetrofit2(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SOURCE2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GreenRetrofit