package com.example.sanchitra

import android.app.Application
import com.example.sanchitra.data.repositories.MovieRepository
import com.example.sanchitra.data.repositories.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class SanchitraApp : Application()

@InstallIn(SingletonComponent::class)
@Module
abstract class MovieRepositoryModule {

    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}