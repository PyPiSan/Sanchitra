package com.pypisan.sanchitra.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.models.TrendingChannel
import com.pypisan.sanchitra.data.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeScreeViewModel @Inject constructor(
    homeRepository: HomeRepository
) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        homeRepository.getTrendingLiveChannels(),
        homeRepository.getTrendingMovies()
    ) { trendingChannels,
        trendingMovieList ->

        if (trendingChannels.data.isEmpty() || trendingMovieList.data.isEmpty()) {
            return@combine HomeScreenUiState.Loading
        }

        val featuredHomeList = trendingChannels.data
            .firstOrNull { it.category.equals("featured", ignoreCase = true) }
            ?.channels
            .orEmpty()

        val trendingHomeChannels = trendingChannels.data
            .firstOrNull { it.category.equals("trending", ignoreCase = true) }
            ?.channels
            .orEmpty()

        val top10MovieList = trendingMovieList.data
            .firstOrNull { it.category.equals("featured", ignoreCase = true) }
            ?.videos
            .orEmpty()

        val trendingMovieList = trendingMovieList.data
            .firstOrNull { it.category.equals("trending", ignoreCase = true) }
            ?.videos
            .orEmpty()

        HomeScreenUiState.Ready(
            featuredHomeList = featuredHomeList,
            trendingHomeChannels = trendingHomeChannels,
            trendingMovieList = trendingMovieList,
            top10MovieList = top10MovieList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState

    data class Ready(
        val featuredHomeList: List<TrendingChannel>,
        val trendingHomeChannels: List<TrendingChannel>,
        val trendingMovieList: List<Videos>,
        val top10MovieList: List<Videos>
    ) : HomeScreenUiState
}
