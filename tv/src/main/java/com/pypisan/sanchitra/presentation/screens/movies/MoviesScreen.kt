package com.pypisan.sanchitra.presentation.screens.movies


import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.common.MoviesRow
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

@Composable
fun MoviesScreen(
    onMovieClick: (video: Videos) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    moviesScreenViewModel: MoviesScreenViewModel = hiltViewModel(),
) {
    val uiState by moviesScreenViewModel.uiState.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is MoviesScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())

        is MoviesScreenUiState.Error -> {
            MovieErrorScreen(message = s.message)
        }

        is MoviesScreenUiState.Ready -> {
            Catalog(
                carouselMovieList = s.carouselMovieList,
                popularFilms = s.popularFilms,
                onMovieClick = onMovieClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun Catalog(
    carouselMovieList: List<Videos>,
    popularFilms: List<Videos>,
    onMovieClick: (video: Videos) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {

    val childPadding = rememberChildPadding()

    val lazyListState = rememberLazyListState()

    var lastFocusedMovieId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var focusedSection by rememberSaveable {
        mutableStateOf(0)
    }

    var focusedRowIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val sharedVideoVM: VideoSharedViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)

    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(focusedRowIndex)
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) {
            lazyListState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(
            top = childPadding.top,
            bottom = 104.dp
        )
    ) {

        item {

            MoviesScreenMovieList(
                videoList = carouselMovieList,
                onMovieClick = { video ->
                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                },
                lastFocusedMovieId = lastFocusedMovieId,
                isActive = focusedSection == 0,
                onMovieFocused = {
                    focusedSection = 0
                    focusedRowIndex = 0
                    lastFocusedMovieId = it
                }
            )
        }

        item {

            MoviesRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = StringConstants.Composable.PopularFilmsTitle,
                videoList = popularFilms,
                isActive = focusedSection == 1,
                onMovieFocused = {
                    focusedSection = 1
                    focusedRowIndex = 1
                },
                onMovieSelected = { video ->
                    focusedRowIndex = 1

                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                }
            )
        }
    }
}
