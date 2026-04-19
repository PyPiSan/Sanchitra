package com.example.sanchitra.data.repositories

import com.example.sanchitra.data.models.Channel
import kotlinx.coroutines.flow.Flow

interface TVRepository {
    fun getChannels(): Flow<List<Channel>>
    suspend fun getChannelData(id: String, type: String): TVRepositoryImpl.ApiResult<Channel>
}