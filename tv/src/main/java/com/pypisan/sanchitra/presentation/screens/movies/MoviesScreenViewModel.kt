package com.pypisan.sanchitra.presentation.screens.movies

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MoviesScreenViewModel @Inject constructor(
    videoRepository: VideoRepository
) : ViewModel() {

    val uiState : StateFlow<MoviesScreenUiState> =
        combine(
            videoRepository.getVideos(),
            videoRepository.getCarouselVideos()
        ) { videos, carousel ->

            if (videos.isEmpty() && carousel.isEmpty()) {
                return@combine MoviesScreenUiState.Loading
            }


            MoviesScreenUiState.Ready(
                carouselMovieList = carousel,
                popularFilms = videos
            )
        }.catch { e ->
            emit(MoviesScreenUiState.Error(e.message ?: "Something went wrong"))
        }
            .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MoviesScreenUiState.Loading
    )
}

sealed interface MoviesScreenUiState {
    data object Loading : MoviesScreenUiState
    data class Ready(
        val carouselMovieList: List<Videos>,
        val popularFilms: List<Videos>
    ) : MoviesScreenUiState

    data class Error(
        val message: String
    ) : MoviesScreenUiState
}
