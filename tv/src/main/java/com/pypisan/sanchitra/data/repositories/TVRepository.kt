package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.models.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TVRepository {
    fun getChannels(): StateFlow<Map<String, List<Channel>>>
    fun getCarouselTV(): StateFlow<Map<String, List<Channel>>>
    suspend fun getChannelData(id: String, type: String): TVRepositoryImpl.ApiResult<Channel>
    suspend fun updateViewCount(channelId: Int)
}