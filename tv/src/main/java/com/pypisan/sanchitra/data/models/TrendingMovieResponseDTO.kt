package com.pypisan.sanchitra.data.models

data class TrendingMovieResponseDTO (
    val data: List<MovieCategorySectionDTO>
)

data class MovieCategorySectionDTO(
    val category: String,
    val videos: List<VideoDTO>
)


fun TrendingMovieResponseDTO.toTrendingMovieResponse(): TrendingMovieResponse {
    return TrendingMovieResponse(
        data = data.map { it.TrendingMovieCategory() }
    )
}

fun MovieCategorySectionDTO.TrendingMovieCategory(): TrendingMovieCategory {
    return TrendingMovieCategory(
        category = category,
        videos = videos.map { it.toDomain() }
    )
}