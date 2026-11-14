package com.pypisan.sanchitra.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.ChannelEntity
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.entities.TrendingChannel
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.HomeRepository
import com.pypisan.sanchitra.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeScreeViewModel @Inject constructor(
    homeRepository: HomeRepository,
    channelRepository: ChannelRepository
) : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> =
        if (StringConstants.Profile.isLoggedIN) {
            // Logged in -> fetch from API
            combine(
                homeRepository.getTrendingLiveChannels(),
                homeRepository.getTrendingMovies()
            ) { trendingChannels, trendingMovieList ->

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

                val trendingMovies = trendingMovieList.data
                    .firstOrNull { it.category.equals("trending", ignoreCase = true) }
                    ?.videos
                    .orEmpty()

                HomeScreenUiState.Ready(
                    featuredHomeList = featuredHomeList,
                    trendingHomeChannels = trendingHomeChannels,
                    trendingMovieList = trendingMovies,
                    top10MovieList = top10MovieList
                )
            }
        } else {
            // Not logged in -> read from Room DB (max 5 featured + max 5 trending, empty movies)
            channelRepository.observeAllChannels()
                .map<List<ChannelEntity>, HomeScreenUiState> { entities ->
                    val activeChannels = entities
                        .filter { it.isActive }
                        .map { it.toTrendingChannel() }

                    val explicitlyFeatured = activeChannels.filter { it.isFeatured }
                    val nonFeaturedShuffled = activeChannels.filterNot { it.isFeatured }.shuffled()

                    val featuredHomeList = (explicitlyFeatured + nonFeaturedShuffled).take(5)

                    val featuredIds = featuredHomeList.map { it.id }.toSet()
                    val remainingPool = activeChannels.filterNot { it.id in featuredIds }.shuffled()
                    val trendingHomeChannels = remainingPool
                        .groupBy { it.category }
                        .entries
                        .shuffled()
                        .take(3)
                        .flatMap { (_, categoryChannels) ->
                            categoryChannels.shuffled().take(5)
                        }

                    HomeScreenUiState.Ready(
                        featuredHomeList = featuredHomeList,
                        trendingHomeChannels = trendingHomeChannels,
                        trendingMovieList = emptyList(),
                        top10MovieList = emptyList()
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenUiState.Loading
        )

    private fun ChannelEntity.toTrendingChannel(): TrendingChannel = TrendingChannel(
        id = id.toInt(),
        name = name,
        description = null,
        category = category,
        logoUrl = logoUrl ?: logo,
        bannerUrl = bannerUrl,
        isFeatured = isFeatured
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
