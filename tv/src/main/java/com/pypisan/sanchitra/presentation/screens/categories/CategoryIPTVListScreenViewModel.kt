package com.pypisan.sanchitra.presentation.screens.categories

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.IPTVChannel
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoryIPTVListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val iptvRepository: IPTVRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState =
        savedStateHandle.getStateFlow<String?>(
            CategoryIPTVListScreen.CategoryNameKey,
            null
        )
            .flatMapLatest { name ->

                if (name == null) {
                    flowOf(CategoryIPTVListScreenUiState.Error)
                } else {
                    iptvRepository.getIPTVChannelsByCategory(name)
                        .map<List<IPTVChannel>, CategoryIPTVListScreenUiState> { channels ->
                            CategoryIPTVListScreenUiState.Done(
                                categoryName = name,
                                channels = channels
                            )
                        }
                        .onStart {
                            emit(CategoryIPTVListScreenUiState.Loading)
                        }
                        .catch {
                            emit(CategoryIPTVListScreenUiState.Error)
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
