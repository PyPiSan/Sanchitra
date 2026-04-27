package com.pypisan.sanchitra.data.repositories


import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.data.models.IPTVChannel
import kotlinx.coroutines.flow.Flow

interface IPTVRepository {
    fun getIPTVChannelsByCategory(category: String): Flow<List<IPTVChannel>>

    fun getIPTVCategories(): Flow<List<IPTVCategoryDto>>
}