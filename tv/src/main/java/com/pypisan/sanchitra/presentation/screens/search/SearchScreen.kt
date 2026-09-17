package com.pypisan.sanchitra.presentation.screens.search

import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.data.entities.SearchResult
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.screens.common.CommonErrorScreen
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.presentation.screens.movies.VideoSharedViewModel
import com.pypisan.sanchitra.storage.SearchHistoryManager
import com.pypisan.sanchitra.utils.AnalyticsTracker
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    onMovieClick: (video: Videos) -> Unit,
    goToIPTVPlayer: (id: Int) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val searchState by searchScreenViewModel.searchState.collectAsStateWithLifecycle()

    val isLoading = searchState is SearchState.Searching
    val searchResult = when (val s = searchState) {
        is SearchState.Done -> s.searchResult
        else -> SearchResult()
    }

    when (searchState) {
        is SearchState.Error -> {
            CommonErrorScreen(message = "Error Occurred, Try again with a different title")
        }
        else -> {
            SearchResultView(
                onMovieClick = onMovieClick,
                goToIPTVPlayer = goToIPTVPlayer,
                searchResult = searchResult,
                isLoading = isLoading,
                searchChannels = searchScreenViewModel::query,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
            )
        }
    }
}


@Composable
fun SearchResultView(
    onMovieClick: (video: Videos) -> Unit,
    goToIPTVPlayer: (id: Int) -> Unit,
    searchResult: SearchResult,
    isLoading: Boolean,
    searchChannels: (queryString: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lazyColumnState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyColumnState.firstVisibleItemIndex == 0 && lazyColumnState.firstVisibleItemScrollOffset < 100
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) {
            lazyColumnState.animateScrollToItem(0)
        }
    }

    LaunchedEffect("SearchScreen") {
        AnalyticsTracker.logScreenView("SearchScreen")
    }

    val childPadding = rememberChildPadding()

    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Track what was actually submitted to avoid triggering while typing
    var searchedQuery by rememberSaveable { mutableStateOf("") }

    // FIX ISSUE 1: Immediate loading state to prevent "No results found" flicker
    var isSearchTriggered by rememberSaveable { mutableStateOf(false) }
    val isActuallyLoading = isLoading || isSearchTriggered

    // FIX ISSUE 2: Avoid duplicate history appends for the same search
    var lastSavedQuery by rememberSaveable { mutableStateOf("") }

    val tfFocusRequester = remember { FocusRequester() }
    val searchBarFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()

    val surfaceInteractionSource = remember { MutableInteractionSource() }
    val isSurfaceFocused by surfaceInteractionSource.collectIsFocusedAsState()

    val isSearchBoxFocused = isTfFocused || isSurfaceFocused

    var focusedSection by rememberSaveable { mutableStateOf("") }
    var lastFocusedId by rememberSaveable { mutableStateOf<Int?>(null) }

    val historyManager = remember { SearchHistoryManager.getInstance(context) }

    val performSearch = {
        if (searchQuery.isNotBlank()) {
            searchedQuery = searchQuery
            lastSavedQuery = "" // Reset so this new search can be recorded
            isSearchTriggered = true // Triggers loader instantly
            keyboardController?.hide()
            searchBarFocusRequester.requestFocus()
            searchChannels(searchQuery)
        }
    }

    // Reset local trigger when ViewModel acknowledges loading or safety timeout
    LaunchedEffect(isLoading) {
        if (isLoading) {
            isSearchTriggered = false
        }
    }

    LaunchedEffect(isSearchTriggered) {
        if (isSearchTriggered) {
            delay(5000) // Fallback timeout in case of unexpected network failure
            isSearchTriggered = false
        }
    }

    // Handle results, focus, and save history ONLY when there are results
    LaunchedEffect(isActuallyLoading, searchResult) {
        if (!isActuallyLoading) {
            delay(100)
            val hasResults = searchResult.videos.isNotEmpty() || searchResult.channels.isNotEmpty()
            if (hasResults) {
                // FIX ISSUE 2: Append ONLY when query has actual search results
                if (searchedQuery.isNotBlank() && searchedQuery != lastSavedQuery) {
                    historyManager.appendSearchHistory(searchedQuery)
                    lastSavedQuery = searchedQuery
                }
                focusManager.moveFocus(FocusDirection.Down)
            } else if (searchedQuery.isNotBlank() && searchedQuery == searchQuery) {
                try {
                    searchBarFocusRequester.requestFocus()
                } catch (_: Exception) {}
            }
        }
    }

    val sharedVideoVM: VideoSharedViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .focusGroup()
            .focusRestorer(),
        state = lazyColumnState,
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        item {
            Surface(
                shape = ClickableSurfaceDefaults.shape(shape = SanchitraCardShape),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    focusedContentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = ClickableSurfaceDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            width = if (isSearchBoxFocused) 2.dp else 1.dp,
                            color = animateColorAsState(
                                targetValue = if (isSearchBoxFocused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.border,
                                label = ""
                            ).value
                        ),
                        shape = SanchitraCardShape
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        shape = SanchitraCardShape
                    )
                ),
                tonalElevation = 2.dp,
                interactionSource = surfaceInteractionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start)
                    .padding(top = 8.dp)
                    .focusRequester(searchBarFocusRequester),
                onClick = { tfFocusRequester.requestFocus() }
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { updatedQuery -> searchQuery = updatedQuery },
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .padding(start = 20.dp),
                        ) {
                            innerTextField()
                            if (searchQuery.isEmpty()) {
                                Text(
                                    modifier = Modifier.graphicsLayer { alpha = 0.6f },
                                    text = stringResource(R.string.search_screen_et_placeholder),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = LocalContentColor.current
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .focusRequester(tfFocusRequester)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                when (keyEvent.nativeKeyEvent.keyCode) {
                                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                                        keyboardController?.hide()
                                        focusManager.moveFocus(FocusDirection.Down)
                                        return@onKeyEvent true
                                    }
                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        focusManager.moveFocus(FocusDirection.Up)
                                        return@onKeyEvent true
                                    }
                                    KeyEvent.KEYCODE_BACK -> {
                                        keyboardController?.hide()
                                        focusManager.moveFocus(FocusDirection.Exit)
                                        return@onKeyEvent true
                                    }
                                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                                        performSearch()
                                        return@onKeyEvent true
                                    }
                                }
                            }
                            false
                        },
                    cursorBrush = Brush.verticalGradient(
                        colors = listOf(LocalContentColor.current, LocalContentColor.current)
                    ),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { performSearch() }
                    ),
                    maxLines = 1,
                    interactionSource = tfInteractionSource,
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        color = LocalContentColor.current
                    )
                )
            }
        }

        // Shows loader immediately while loading
        if (isActuallyLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Loading(modifier = Modifier.size(60.dp))
                }
            }
        }
        // Shows only when search finished and returned 0 results
        else if (
            searchResult.videos.isEmpty() && searchResult.channels.isEmpty() && searchedQuery.isNotBlank() && searchedQuery == searchQuery
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No results found for \"$searchedQuery\"",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Check the spelling or try searching for something else",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        // 3. Results view (history appending was removed from here)
        else {
            item {
                SearchRow(
                    title = "Movies",
                    movieItem = searchResult.videos,
                    iptvItem = searchResult.channels,
                    itemType = "movies",
                    onItemClick = goToIPTVPlayer,
                    onMovieClick = { video ->
                        sharedVideoVM.setVideo(video)
                        onMovieClick(video)
                    },
                    onItemFocused = {
                        focusedSection = "movies"
                        lastFocusedId = it
                    }
                )
            }
            item {
                SearchRow(
                    title = "Channels",
                    movieItem = searchResult.videos,
                    iptvItem = searchResult.channels,
                    itemType = "iptv",
                    onItemClick = goToIPTVPlayer,
                    onMovieClick = { video ->
                        sharedVideoVM.setVideo(video)
                        onMovieClick(video)
                    },
                    onItemFocused = {
                        focusedSection = "iptv"
                        lastFocusedId = it
                    }
                )
            }
        }
    }
}
