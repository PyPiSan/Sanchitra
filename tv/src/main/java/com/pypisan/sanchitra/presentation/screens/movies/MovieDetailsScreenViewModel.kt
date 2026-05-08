package com.pypisan.sanchitra.presentation.screens.movies

sealed class MovieDetailsScreenUiState {
    data object Loading : MovieDetailsScreenUiState()
    data object Error : MovieDetailsScreenUiState()
}
