package com.pypisan.sanchitra.presentation.screens.movies
import com.pypisan.sanchitra.data.entities.MovieDetails

sealed class MovieDetailsScreenUiState {
    data object Loading : MovieDetailsScreenUiState()
    data object Error : MovieDetailsScreenUiState()
    data class Done(val movieDetails: MovieDetails) : MovieDetailsScreenUiState()
}
