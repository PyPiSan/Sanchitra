package com.pypisan.sanchitra.data.models

import com.pypisan.sanchitra.data.entities.Videos

data class TrendingMovieResponse(
    val data: List<TrendingMovieCategory>
)

data class TrendingMovieCategory(
    val category: String,
    val videos: List<Videos>
)