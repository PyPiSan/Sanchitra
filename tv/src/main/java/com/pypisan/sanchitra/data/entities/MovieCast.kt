package com.pypisan.sanchitra.data.entities

import MovieCastResponseItem

data class MovieCast(
    val id: String,
    val characterName: String,
    val realName: String,
    val avatarUrl: String
)

fun MovieCastResponseItem.toMovieCast(): MovieCast =
    MovieCast(
        id,
        characterName,
        realName,
        avatarUrl
    )
