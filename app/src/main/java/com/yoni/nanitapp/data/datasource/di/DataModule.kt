package com.yoni.nanitapp.data.datasource.di

import com.yoni.nanitapp.data.datasource.WebSocketDataSource
import com.yoni.nanitapp.data.repository.BirthdayRepositoryImpl
import com.yoni.nanitapp.domain.BirthdayRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideWebSocketDataSource(
        okHttpClient: OkHttpClient,
        json: Json
    ): WebSocketDataSource {
        return WebSocketDataSource(okHttpClient, json)
    }

    @Provides
    @Singleton
    fun provideBirthdayRepository(
        webSocketDataSource: WebSocketDataSource
    ): BirthdayRepository {
        return BirthdayRepositoryImpl(webSocketDataSource)
    }
}