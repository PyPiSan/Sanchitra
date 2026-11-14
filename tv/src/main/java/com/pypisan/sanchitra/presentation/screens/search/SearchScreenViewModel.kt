package com.pypisan.sanchitra.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.SearchResult
import com.pypisan.sanchitra.data.repositories.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchState: StateFlow<SearchState> = _query
        .debounce(300L) // Wait 300ms for user to stop typing
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.isBlank()) {
                    emit(SearchState.Idle)
                } else {
                    emit(SearchState.Searching)
                    searchRepository.searchContents(query).collect { result ->
                        emit(SearchState.Done(result))
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchState.Idle
        )

    fun query(queryString: String) {
        _query.value = queryString
    }
}

sealed interface SearchState {
    data object Idle : SearchState
    data object Searching : SearchState
    data class Done(val searchResult: SearchResult) : SearchState
    data class Error(val message: String? = null) : SearchState
}