package com.pypisan.sanchitra.presentation.screens.livetv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Channel
import com.pypisan.sanchitra.data.entities.ChannelEntity
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.TVRepository
import com.pypisan.sanchitra.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TVScreenViewModel @Inject constructor(
    tvRepository: TVRepository,
    channelRepository: ChannelRepository
) : ViewModel() {

    private var cachedCategories: Map<String, List<Channel>>? = null
    private var cachedCarousel: List<Channel>? = null

    val uiState: StateFlow<TVScreenUiState> =
        if (StringConstants.Profile.isLoggedIN) {
            // Logged in -> fetch from API
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
        } else {
            // Not logged in -> read from Room DB
            channelRepository.observeAllChannels()
                .map<List<ChannelEntity>, TVScreenUiState> { entities ->
                    val activeEntities = entities.filter { it.isActive }

                    val mappedChannels = activeEntities.map { entity ->
                        entity.toUiChannel()
                    }

                    // Group channels by category
                    val groupedCategories = mappedChannels.groupBy { it.category }

                    // Pick any explicitly featured channels first, then fill up to 5 randomly
                    val explicitlyFeatured = activeEntities
                        .filter { it.isFeatured }
                        .map { it.toUiChannel() }

                    val remainingRandom = activeEntities
                        .filterNot { it.isFeatured }
                        .shuffled()
                        .map { it.toUiChannel() }

                    val carousel = (explicitlyFeatured + remainingRandom).take(5)

                    TVScreenUiState.Ready(
                        categories = groupedCategories,
                        carousel = carousel
                    )
                }
        }
            .catch { e ->
                emit(TVScreenUiState.Error(e.message ?: "Something went wrong"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                TVScreenUiState.Loading
            )

    private fun ChannelEntity.toUiChannel(): Channel = Channel(
        id = id.toInt(),
        name = name,
        category = category,
        logoUrl = logoUrl ?: logo,
        description = null,
        bannerUrl = bannerUrl,
        streamUrl = streamUrl,
        language = language ?: "English",
        licenseKey = licenseKey,
        licenseUrl = licenseUrl,
        isDrm = isDrm,
        isInternal = isInternal,
        isPremium = isPremium
    )
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