package com.pypisan.sanchitra.presentation.screens.videoPlayer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Videos
import kotlinx.coroutines.flow.map
import com.pypisan.sanchitra.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: VideoRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = savedStateHandle
        .getStateFlow<String?>(VideoPlayerScreen.metaID, null)
        .flatMapLatest { id ->

            val movieId = id?.toIntOrNull()

            if (movieId == null) {

                flowOf(VideoPlayerScreenUiState.Error)

            } else {

                repository.getVideoDetails(movieId)
                    .filterNotNull()
                    .map<Videos, VideoPlayerScreenUiState> { details ->
                        VideoPlayerScreenUiState.Done(details)
                    }
                    .catch {
                        emit(VideoPlayerScreenUiState.Error)
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoPlayerScreenUiState.Loading
        )
}

@Immutable
sealed class VideoPlayerScreenUiState {
    data object Loading : VideoPlayerScreenUiState()
    data object Error : VideoPlayerScreenUiState()
    data class Done(val videoDetail: Videos?) : VideoPlayerScreenUiState()
}
