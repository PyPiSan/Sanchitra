package com.example.sanchitra.presentation.screens.categories
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.entities.IPTVCategoryDto
import com.example.sanchitra.data.entities.MovieCategoryList
import com.example.sanchitra.data.repositories.IPTVRepository
import com.example.sanchitra.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.log

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    iptvRepository: IPTVRepository
) : ViewModel() {

    val uiState = iptvRepository.getIPTVCategories()
        .map { categories ->
            CategoriesScreenUiState.Ready(categoryList = categories)
        }
        .stateIn(
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
