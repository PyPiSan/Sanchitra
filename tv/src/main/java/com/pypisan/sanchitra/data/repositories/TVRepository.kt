package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.models.Channel
import kotlinx.coroutines.flow.Flow

interface TVRepository {
    fun getChannels(): Flow<List<Channel>>
    fun getCarouselTV(): Flow<List<Channel>>
    suspend fun getChannelData(id: String, type: String): TVRepositoryImpl.ApiResult<Channel>
}