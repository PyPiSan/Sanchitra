package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.Videos
import kotlinx.coroutines.flow.StateFlow

interface VideoRepository {
    fun getVideos(): StateFlow<Map<String, List<Videos>>>
    fun getCarouselVideos(): StateFlow<Map<String, List<Videos>>>
    fun getVideoDetails(movieId: Int): StateFlow<Videos?>
    suspend fun updateMovieViewCount(movieID: Int)
}