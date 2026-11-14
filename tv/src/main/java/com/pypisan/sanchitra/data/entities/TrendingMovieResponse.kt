package com.pypisan.sanchitra.data.entities

data class TrendingMovieResponse(
    val data: List<TrendingMovieCategory>
)

data class TrendingMovieCategory(
    val category: String,
    val videos: List<Videos>
)