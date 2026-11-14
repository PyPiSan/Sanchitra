package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.TrendingMovieCategory
import com.pypisan.sanchitra.data.entities.TrendingMovieResponse

data class TrendingMovieResponseDTO(
    @SerializedName("data") val data: List<MovieCategorySectionDTO>
)

data class MovieCategorySectionDTO(
    @SerializedName("category") val category: String,
    @SerializedName("videos") val videos: List<VideoDTO>
)


fun TrendingMovieResponseDTO.toTrendingMovieResponse(): TrendingMovieResponse {
    return TrendingMovieResponse(
        data = data.map { it.TrendingMovieCategory() })
}

fun MovieCategorySectionDTO.TrendingMovieCategory(): TrendingMovieCategory {
    return TrendingMovieCategory(
        category = category, videos = videos.map { it.toDomain() })
}