package com.pypisan.sanchitra.presentation.screens.livetv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Channel
import com.pypisan.sanchitra.data.repositories.TVRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TVScreenViewModel @Inject constructor(
    tvRepository: TVRepository
) : ViewModel() {

    private var cachedCategories: Map<String, List<Channel>>? = null
    private var cachedCarousel: List<Channel>? = null

    val uiState: StateFlow<TVScreenUiState> =
        combine<Map<String, List<Channel>>, Map<String, List<Channel>>, TVScreenUiState>(
            tvRepository.getChannels(),
            tvRepository.getCarouselTV()
        ) { channels, carousel ->

            val featuredChannels = carousel["Featured Channels"] ?: emptyList()
            if (channels.isEmpty() && featuredChannels.isEmpty()) {
                if (cachedCategories != null && cachedCarousel != null) {
                    return@combine TVScreenUiState.Ready(
                        categories = cachedCategories!!,
                        carousel = cachedCarousel!!
                    )
                }
                return@combine TVScreenUiState.Loading
            }

            cachedCategories = channels
            cachedCarousel = featuredChannels

            TVScreenUiState.Ready(
                categories = channels,
                carousel = featuredChannels
            )
        }
            .catch { e ->
                emit(TVScreenUiState.Error(e.message ?: "Something went wrong"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                TVScreenUiState.Loading
            )

    sealed interface TVScreenUiState {
        data object Loading : TVScreenUiState

        data class Ready(
            val categories: Map<String, List<Channel>>,
            val carousel: List<Channel>
        ) : TVScreenUiState

        data class Error(
            val message: String
        ) : TVScreenUiState
    }
}