package com.example.sanchitra.data.util

import android.content.Context
import com.example.sanchitra.utils.APIClient
import com.example.sanchitra.utils.APIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideApiService(
        @ApplicationContext context: Context
    ): APIService {
        return APIClient.create(context)
    }
}