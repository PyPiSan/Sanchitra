package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class TrendingResponseDTO(
    val data: List<CategorySectionDTO>
)

data class CategorySectionDTO(
    val category: String,
    val channels: List<MediaDTO>
)

data class MediaDTO(
    val id: Int,
    val name: String,
    val category: String,
    @SerializedName("logo_url")
    val logo: String?,
    @SerializedName("banner_url")
    val banner: String?,
    @SerializedName("is_featured")
    val isFeatured: Boolean
)

fun TrendingResponseDTO.toTrendingResponse(): TrendingResponse {
    return TrendingResponse(
        data = data.map { it.toTrendingCategory() }
    )
}

fun CategorySectionDTO.toTrendingCategory(): TrendingCategory {
    return TrendingCategory(
        category = category,
        channels = channels.map { it.toTrendingChannel() }
    )
}

fun MediaDTO.toTrendingChannel(): TrendingChannel {
    return TrendingChannel(
        id = id,
        name = name,
        category = category,
        logoUrl = logo,
        bannerUrl = banner,
        isFeatured = isFeatured
    )
}
