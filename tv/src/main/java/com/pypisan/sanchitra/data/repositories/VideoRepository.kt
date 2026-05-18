package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.Videos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VideoRepository {
    fun getVideos(): Flow<List<Videos>>
    fun getCarouselVideos(): Flow<List<Videos>>
    fun getVideoDetails(movieId: Int): StateFlow<Videos?>
}