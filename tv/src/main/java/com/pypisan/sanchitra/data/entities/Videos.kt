package com.pypisan.sanchitra.data.entities


data class Videos(
    val id: Int,
    val title: String,
    val image: String,
    val logo: String?,
    val url: String,
    val drm: Boolean,
    val licenseKey: String?,
    val licenseUrl: String?,
    val meta: VideoMeta,
    val categories: List<String>,
    val duration: Int,
    val language: List<String>
)

data class VideoMeta(
    val banner: String?,
    val description: String,
    val trailer: String?,
    val releaseDate: String?,
    val subtitleUrl: String?,
    val imdbRating: String?,
    val rottenTomatoes: String?,
    val budget: String?,
    val revenue: String?,

)