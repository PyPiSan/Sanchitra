package com.pypisan.sanchitra

import android.app.Application
import com.pypisan.sanchitra.data.repositories.TVRepository
import com.pypisan.sanchitra.data.repositories.TVRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class SanchitraApp : Application()

@InstallIn(SingletonComponent::class)
@Module
abstract class TVRepositoryModule {
    @Binds
    abstract fun bindTVRepository(
        impl: TVRepositoryImpl
    ): TVRepository
}