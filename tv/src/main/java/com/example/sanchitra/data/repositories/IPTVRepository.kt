package com.example.sanchitra.data.repositories


import com.example.sanchitra.data.entities.IPTVCategoryDto
import com.example.sanchitra.data.models.IPTVChannel
import kotlinx.coroutines.flow.Flow

interface IPTVRepository {
    fun getIPTVChannelsByCategory(category: String): Flow<List<IPTVChannel>>

    fun getIPTVCategories(): Flow<List<IPTVCategoryDto>>
}