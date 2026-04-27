package com.pypisan.sanchitra.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.MovieList
import com.pypisan.sanchitra.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeScreeViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        movieRepository.getFeaturedMovies(),
        movieRepository.getTrendingMovies(),
        movieRepository.getTop10Movies(),
        movieRepository.getNowPlayingMovies(),
    ) { featuredMovieList, trendingMovieList, top10MovieList, nowPlayingMovieList ->
        HomeScreenUiState.Ready(
            featuredMovieList,
            trendingMovieList,
            top10MovieList,
            nowPlayingMovieList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val featuredMovieList: MovieList,
        val trendingMovieList: MovieList,
        val top10MovieList: MovieList,
        val nowPlayingMovieList: MovieList
    ) : HomeScreenUiState
}
