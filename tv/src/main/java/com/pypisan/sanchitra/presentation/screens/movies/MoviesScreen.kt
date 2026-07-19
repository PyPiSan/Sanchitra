package com.pypisan.sanchitra.presentation.screens.movies

//Entry Point for Movies TAB

import androidx.activity.ComponentActivity
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.common.MoviesRow
import com.pypisan.sanchitra.presentation.screens.common.CommonErrorScreen
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
            CommonErrorScreen(message = s.message)
        }

        is MoviesScreenUiState.Ready -> {
            Catalog(
                carouselMovieList = s.carouselMovieList,
                categorizedMovies = s.categorizedMovies,
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
    categorizedMovies: Map<String, List<Videos>>,
    onMovieClick: (video: Videos) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {

    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()

    var focusedRowIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val categoryList = remember(categorizedMovies) {
        categorizedMovies.entries.toList()
    }

    var focusedSection by rememberSaveable { mutableStateOf("") }
    var lastFocusedMovieId by rememberSaveable { mutableStateOf<Int?>(null) }

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
        modifier = modifier
            .focusGroup()
            .focusRestorer(),
        state = lazyListState,
        contentPadding = PaddingValues(
            top = childPadding.top,
            bottom = 104.dp
        )
    ) {

        item(key = "carousel") {
            MoviesScreenMovieList(
                videoList = carouselMovieList,
                onMovieClick = { video ->
                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                },
                onMovieFocused = {
                    focusedSection = "Carousel"
                    lastFocusedMovieId = it
                }
            )
        }

        items(
            items = categoryList,
            key = { it.key }
        ) { entry ->
            MoviesRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = entry.key,
                videoList = entry.value,
                onMovieFocused = {
                    focusedSection = entry.key
                    lastFocusedMovieId = it.id
                },
                onMovieSelected = { video ->
                    focusedSection = entry.key
                    sharedVideoVM.setVideo(video)
                    onMovieClick(video)
                }
            )
        }
    }
}
