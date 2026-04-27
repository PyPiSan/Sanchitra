package com.pypisan.sanchitra.presentation.screens.movies
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.data.entities.Movie
import com.pypisan.sanchitra.data.entities.MovieList
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.common.MoviesRow
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

@Composable
fun MoviesScreen(
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    moviesScreenViewModel: MoviesScreenViewModel = hiltViewModel(),
) {
    val uiState by moviesScreenViewModel.uiState.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is MoviesScreenUiState.Loading -> Loading()
        is MoviesScreenUiState.Ready -> {
            Catalog(
                movieList = s.movieList,
                popularFilmsThisWeek = s.popularFilmsThisWeek,
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
    movieList: MovieList,
    popularFilmsThisWeek: MovieList,
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(top = childPadding.top, bottom = 104.dp)
    ) {
        item {
            MoviesScreenMovieList(
                movieList = movieList,
                onMovieClick = onMovieClick
            )
        }
        item {
            MoviesRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = StringConstants.Composable.PopularFilmsThisWeekTitle,
                movieList = popularFilmsThisWeek,
                onMovieSelected = onMovieClick
            )
        }
    }
}
