package com.pypisan.sanchitra.data.repositories


import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.data.entities.IPTVChannel
import com.pypisan.sanchitra.data.entities.IPTVChannelDetail
import kotlinx.coroutines.flow.Flow

interface IPTVRepository {
    fun getIPTVChannelsByCategory(category: String): Flow<List<IPTVChannel>>

    fun getIPTVCategories(): Flow<List<IPTVCategoryDto>>

    suspend fun getIPTVChannelDetail(id: String): IPTVRepositoryImpl.ApiResult<IPTVChannelDetail>
}