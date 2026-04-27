package com.pypisan.sanchitra.data.util

import android.content.Context
import com.pypisan.sanchitra.utils.APIClient
import com.pypisan.sanchitra.utils.APIService
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