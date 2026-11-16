package com.pypisan.sanchitra.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.IPTVCategoryDto
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import com.pypisan.sanchitra.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    iptvRepository: IPTVRepository,
    channelRepository: ChannelRepository
) : ViewModel() {

    val uiState: StateFlow<CategoriesScreenUiState> =
        if (StringConstants.Profile.isLoggedIN) {
            // Logged in -> fetch categories from API
            iptvRepository.getIPTVCategories()
                .map { categories ->
                    if (categories.isEmpty()) {
                        CategoriesScreenUiState.Loading
                    } else {
                        CategoriesScreenUiState.Ready(categoryList = categories)
                    }
                }
                .catch { e ->
                    emit(CategoriesScreenUiState.Error(e.message ?: "Failed to load categories"))
                }
        } else {
            // Not logged in -> read categories from Room DB (returns empty list if nothing saved)
            channelRepository.observeCategories()
                .map<List<String>, CategoriesScreenUiState> { categoryNames ->
                    val mapped = categoryNames
                        .take(12)
                        .mapIndexed { index, categoryName ->
                        IPTVCategoryDto(
                            id = index + 1,
                            name = categoryName
                        )
                    }
                    CategoriesScreenUiState.Ready(categoryList = mapped)
                }
                .catch { e ->
                    emit(CategoriesScreenUiState.Error(e.message ?: "Failed to load local categories"))
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoriesScreenUiState.Loading
        )
}
sealed interface CategoriesScreenUiState {
    data object Loading : CategoriesScreenUiState
    data class Ready(val categoryList: List<IPTVCategoryDto>) : CategoriesScreenUiState
    data class Error(val message: String) : CategoriesScreenUiState
}
