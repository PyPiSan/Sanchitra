package com.pypisan.sanchitra.presentation.screens.movies

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

    private var cachedVideosCategories: Map<String, List<Videos>>? = null

    private var cachedCarousel: List<Videos>? = null

    val uiState : StateFlow<MoviesScreenUiState> =
        combine<Map<String, List<Videos>>, Map<String, List<Videos>>, MoviesScreenUiState>(
            videoRepository.getVideos(),
            videoRepository.getCarouselVideos()
        ) { videos, carousel ->

            val featuredVideos = carousel["Featured Videos"] ?: emptyList()
            if (videos.isEmpty() && featuredVideos.isEmpty()) {
                if (cachedVideosCategories != null && cachedCarousel != null) {
                    return@combine MoviesScreenUiState.Ready(
                        categorizedMovies = cachedVideosCategories!!,
                        carouselMovieList = cachedCarousel!!
                    )
                }
                return@combine MoviesScreenUiState.Loading
            }

            cachedVideosCategories = videos
            cachedCarousel = featuredVideos


            MoviesScreenUiState.Ready(
                carouselMovieList = featuredVideos,
                categorizedMovies = videos
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
        val categorizedMovies: Map<String, List<Videos>>
    ) : MoviesScreenUiState

    data class Error(
        val message: String
    ) : MoviesScreenUiState
}
