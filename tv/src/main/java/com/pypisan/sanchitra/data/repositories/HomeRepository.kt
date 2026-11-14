package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.TrendingMovieResponse
import com.pypisan.sanchitra.data.entities.TrendingResponse
import kotlinx.coroutines.flow.StateFlow

interface HomeRepository {
    fun getTrendingLiveChannels(): StateFlow<TrendingResponse>
    fun getTrendingMovies(): StateFlow<TrendingMovieResponse>
}