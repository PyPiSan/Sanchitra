package com.example.sanchitra.data.entities

data class MovieDetails(
    val id: String,
    val videoUri: String,
    val subtitleUri: String?,
    val posterUri: String,
    val name: String,
    val description: String,
    val pgRating: String,
    val releaseDate: String,
    val categories: List<String>,
    val duration: String,
    val director: String,
    val screenplay: String,
    val music: String,
    val castAndCrew: List<MovieCast>,
    val status: String,
    val originalLanguage: String,
    val budget: String,
    val revenue: String,
    val similarMovies: MovieList,
    val reviewsAndRatings: List<MovieReviewsAndRatings>,
)
