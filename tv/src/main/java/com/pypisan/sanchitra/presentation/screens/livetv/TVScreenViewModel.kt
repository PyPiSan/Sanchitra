package com.pypisan.sanchitra.presentation.screens.livetv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.data.repositories.TVRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TVScreenViewModel @Inject constructor(
    tvRepository: TVRepository
) : ViewModel() {

    // 1. Create cache variables
    private var cachedCategories: Map<String, List<Channel>>? = null
    private var cachedCarousel: List<Channel>? = null

    val uiState: StateFlow<TVScreenUiState> =
        combine<List<Channel>, List<Channel>, TVScreenUiState>(
            tvRepository.getChannels(),
            tvRepository.getCarouselTV()
        ) { channels, carousel ->

            if (channels.isEmpty() && carousel.isEmpty()) {
                // If lists are empty, check if we have a cache!
                // Only show loading if cache is totally empty.
                if (cachedCategories != null && cachedCarousel != null) {
                    return@combine TVScreenUiState.Ready(
                        categories = cachedCategories!!,
                        carousel = cachedCarousel!!
                    )
                }
                return@combine TVScreenUiState.Loading
            }

            val channelGrouped = channels
                .filter { it.isValid() }
                .groupBy { it.category.ifBlank { "Others" } }

            // 2. Save the newly fetched data into the cache
            cachedCategories = channelGrouped
            cachedCarousel = carousel


            TVScreenUiState.Ready(
                categories = channelGrouped,
                carousel = carousel
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

    private fun Channel.isValid(): Boolean {
        return name.isNotBlank()
    }

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