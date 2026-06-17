package com.pypisan.sanchitra.presentation.screens.home


import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusGroup
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.models.TrendingChannel
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.common.MoviesRow
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.presentation.screens.movies.VideoSharedViewModel

@Composable
fun HomeScreen(
    onMovieClick: (video: Videos) -> Unit,
    goToTVPlayer: (id: Int) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    homeScreeViewModel: HomeScreeViewModel = hiltViewModel(),
) {
    val uiState by homeScreeViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            Catalog(
                featuredHome = s.featuredHomeList,
                trendingHomeChannels = s.trendingHomeChannels,
                top10Movies = s.top10MovieList,
                trendingMovies = s.trendingMovieList,
                onMovieClick = onMovieClick,
                onScroll = onScroll,
                goToTVPlayer = goToTVPlayer,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }

        is HomeScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        is HomeScreenUiState.Error -> Error(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Catalog(
    featuredHome: List<TrendingChannel>,
    trendingHomeChannels: List<TrendingChannel>,
    top10Movies: List<Videos>,
    trendingMovies: List<Videos>,
    onMovieClick: (video: Videos) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    goToTVPlayer: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
) {

    val lazyListState = rememberLazyListState()
    val childPadding = rememberChildPadding()

    var focusedSection by rememberSaveable {
        mutableStateOf("")
    }

    var focusedRowIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    var lastFocusedMovieId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var lastFocusedChannelId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var immersiveListHasFocus by remember { mutableStateOf(false) }

    val groupedTrendingChannels = trendingHomeChannels.groupBy { it.category }
    val sharedVideoVM: VideoSharedViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)

    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset < 300
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        modifier = modifier
            .focusGroup()
            .focusRestorer(),
    ) {

        item(key = "FeaturedHomeCarousel", contentType = "FeaturedHomeCarousel") {
            FeaturedHomeCarousel(
                channels = featuredHome,
                padding = childPadding,
                goToTVPlayer = goToTVPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(324.dp)
            )
        }

        item(key = "MoviesRow", contentType = "MoviesRow") {

            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                videoList = trendingMovies,
                title = StringConstants.Composable.HomeScreenTrendingTitle,

                isActive = focusedSection == "movies",

                onMovieFocused = {
                    focusedSection = "movies"
                    focusedRowIndex = 1
                },

                onMovieSelected = { video ->
                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                }
            )
        }
        item(key = "Top10Movies", contentType = "Top10MoviesList") {

            Top10MoviesList(
                movieList = top10Movies,

                isActive = focusedSection == "top10",

                onMovieFocused = {
                    focusedSection = "top10"
                    focusedRowIndex = 2
                },

                onMovieClick = { video ->
                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                },

                modifier = Modifier.onFocusChanged {
                    immersiveListHasFocus = it.hasFocus
                },
            )
        }

        groupedTrendingChannels.entries.toList().forEachIndexed { index, entry ->

            val category = entry.key
            val channels = entry.value

            item(key = "Trending_$category", contentType = "TrendingChannelRow") {

                TrendingChannelRow(
                    modifier = Modifier.padding(top = 16.dp),

                    channels = channels,
                    title = category,

                    isActive = focusedSection == "channel_$index",

                    lastFocusedChannelId = lastFocusedChannelId,

                    onChannelFocused = {
                        focusedSection = "channel_$index"
                        focusedRowIndex = index + 3
                        lastFocusedChannelId = it
                    },

                    goToTVPlayer = goToTVPlayer,
                )
            }
        }

    }
}
