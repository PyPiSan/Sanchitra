package com.pypisan.sanchitra.data.entities

import MoviesResponseItem

data class Movie(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val posterUri: String,
    val name: String,
    val description: String
)

fun MoviesResponseItem.toMovie(thumbnailType: ThumbnailType = ThumbnailType.Standard): Movie {
    val thumbnail = when (thumbnailType) {
        ThumbnailType.Standard -> image_2_3
        ThumbnailType.Long -> image_16_9
    }
    return Movie(
        id,
        videoUri,
        subtitleUri,
        thumbnail,
        title,
        fullTitle
    )
}

enum class ThumbnailType {
    Standard,
    Long
}
