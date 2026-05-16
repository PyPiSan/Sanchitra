package com.pypisan.sanchitra.data.util

import android.content.Context
import com.pypisan.sanchitra.utils.APIClient
import com.pypisan.sanchitra.utils.APIService
import com.pypisan.sanchitra.utils.MediaAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(
        @ApplicationContext context: Context
    ): APIService {
        return APIClient.createApiService(context)
    }

    @Provides
    @Singleton
    fun provideMediaApiService(
        @ApplicationContext context: Context
    ): MediaAPIService {
        return APIClient.createMediaApiService(context)
    }
}