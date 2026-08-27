package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.TrendingCategory
import com.pypisan.sanchitra.data.entities.TrendingChannel
import com.pypisan.sanchitra.data.entities.TrendingResponse

data class TrendingResponseDTO(
    @SerializedName("data") val data: List<CategorySectionDTO>
)

data class CategorySectionDTO(
    @SerializedName("category") val category: String,
    @SerializedName("channels") val channels: List<MediaDTO>
)

data class MediaDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("logo_url") val logo: String?,
    @SerializedName("banner_url") val banner: String?,
    @SerializedName("is_featured") val isFeatured: Boolean
)

fun TrendingResponseDTO.toTrendingResponse(): TrendingResponse {
    return TrendingResponse(
        data = data.map { it.toTrendingCategory() })
}

fun CategorySectionDTO.toTrendingCategory(): TrendingCategory {
    return TrendingCategory(
        category = category, channels = channels.map { it.toTrendingChannel() })
}

fun MediaDTO.toTrendingChannel(): TrendingChannel {
    return TrendingChannel(
        id = id,
        name = name,
        description = description,
        category = category,
        logoUrl = logo,
        bannerUrl = banner,
        isFeatured = isFeatured
    )
}
