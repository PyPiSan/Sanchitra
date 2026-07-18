package com.pypisan.sanchitra.presentation.screens.videoPlayer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@HiltViewModel
class VideoPlayerScreenViewModel @Inject constructor(
    private val repository: VideoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoPlayerScreenUiState>(VideoPlayerScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var videoJob: Job? = null

    fun loadVideo(id: String) {
        _uiState.value = VideoPlayerScreenUiState.Loading

        val movieId = id.toIntOrNull()
        if (movieId == null) {
            _uiState.value = VideoPlayerScreenUiState.Error
            return
        }

        videoJob?.cancel()
        videoJob = viewModelScope.launch {
            repository.getVideoDetails(movieId)
                .filterNotNull()
                .catch { _uiState.value = VideoPlayerScreenUiState.Error }
                .collect { details ->
                    _uiState.value = VideoPlayerScreenUiState.Done(details)
                }
        }
    }

    fun reset() {
        videoJob?.cancel()
        _uiState.value = VideoPlayerScreenUiState.Loading
    }

    fun updateViewCount(movieId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateMovieViewCount(movieId ?: 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Immutable
sealed class VideoPlayerScreenUiState {
    data object Loading : VideoPlayerScreenUiState()
    data object Error : VideoPlayerScreenUiState()
    data class Done(val videoDetail: Videos?) : VideoPlayerScreenUiState()
}
