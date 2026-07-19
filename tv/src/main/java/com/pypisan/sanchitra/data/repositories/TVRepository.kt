package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.Channel
import kotlinx.coroutines.flow.StateFlow

interface TVRepository {
    fun getChannels(): StateFlow<Map<String, List<Channel>>>
    fun getCarouselTV(): StateFlow<Map<String, List<Channel>>>
    suspend fun getChannelData(id: String, type: String): TVRepositoryImpl.ApiResult<Channel>
    suspend fun updateViewCount(channelId: Int)
}