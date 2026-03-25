package com.example.sanchitra.data.entities

import MovieCategoriesResponseItem

data class MovieCategory(
    val id: String,
    val name: String,
)

fun MovieCategoriesResponseItem.toMovieCategory(): MovieCategory =
    MovieCategory(id, name)
