package com.pypisan.sanchitra.presentation.screens.categories


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.ChannelEntity
import com.pypisan.sanchitra.data.entities.IPTVChannel
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import com.pypisan.sanchitra.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoryIPTVListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val iptvRepository: IPTVRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CategoryIPTVListScreenUiState> =
        savedStateHandle.getStateFlow<String?>(
            CategoryIPTVListScreen.CategoryNameKey,
            null
        )
            .flatMapLatest { name ->
                when {
                    name == null -> flowOf(CategoryIPTVListScreenUiState.Error)

                    StringConstants.Profile.isLoggedIN -> {
                        // Logged in -> fetch channels from API
                        iptvRepository.getIPTVChannelsByCategory(name)
                            .map<List<IPTVChannel>, CategoryIPTVListScreenUiState> { channels ->
                                if (channels.isEmpty()) {
                                    CategoryIPTVListScreenUiState.Loading
                                } else {
                                    CategoryIPTVListScreenUiState.Done(
                                        categoryName = name,
                                        channels = channels
                                    )
                                }
                            }
                            .catch {
                                emit(CategoryIPTVListScreenUiState.Error)
                            }
                    }

                    else -> {
                        // Not logged in -> fetch channels from Room DB
                        channelRepository.observeByCategory(name)
                            .map<List<ChannelEntity>, CategoryIPTVListScreenUiState> { entities ->
                                val mappedChannels = entities.map { entity ->
                                    IPTVChannel(
                                        id = entity.id.toString(),
                                        name = entity.name,
                                        country = entity.country ?: "IN",
                                        categories = entity.category,
                                        isApp = entity.isApp,
                                        isWeb = entity.isWeb,
                                        logo = entity.logoUrl ?: entity.logo.orEmpty()
                                    )
                                }
                                CategoryIPTVListScreenUiState.Done(
                                    categoryName = name,
                                    channels = mappedChannels
                                )
                            }
                            .catch {
                                emit(CategoryIPTVListScreenUiState.Error)
                            }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoryIPTVListScreenUiState.Loading
            )
}

sealed interface CategoryIPTVListScreenUiState {
    object Loading : CategoryIPTVListScreenUiState
    object Error : CategoryIPTVListScreenUiState

    data class Done(
        val categoryName: String,
        val channels: List<IPTVChannel>
    ) : CategoryIPTVListScreenUiState
}
