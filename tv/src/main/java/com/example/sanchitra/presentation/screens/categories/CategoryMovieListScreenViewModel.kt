package com.example.sanchitra.presentation.screens.categories
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.entities.MovieCategoryDetails
import com.example.sanchitra.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CategoryMovieListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    movieRepository: MovieRepository
) : ViewModel() {

    val uiState =
        savedStateHandle.getStateFlow<String?>(
            CategoryMovieListScreen.CategoryIdBundleKey,
            null
        ).map { id ->
            if (id == null) {
                CategoryMovieListScreenUiState.Error
            } else {
                val categoryDetails = movieRepository.getMovieCategoryDetails(id)
                CategoryMovieListScreenUiState.Done(categoryDetails)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoryMovieListScreenUiState.Loading
        )
}

sealed interface CategoryMovieListScreenUiState {
    object Loading : CategoryMovieListScreenUiState
    object Error : CategoryMovieListScreenUiState
    data class Done(val movieCategoryDetails: MovieCategoryDetails) : CategoryMovieListScreenUiState
}
