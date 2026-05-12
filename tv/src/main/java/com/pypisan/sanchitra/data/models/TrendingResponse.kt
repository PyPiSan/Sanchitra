package com.pypisan.sanchitra.data.models

data class TrendingResponse(
    val data: List<TrendingCategory>
)

data class TrendingCategory(
    val category: String,
    val channels: List<TrendingChannel>
)

data class TrendingChannel(
    val id: Int,
    val name: String,
    val category: String,
    val logoUrl: String?,
    val bannerUrl: String?,
    val isFeatured: Boolean
)
