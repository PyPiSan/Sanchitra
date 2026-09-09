package com.pypisan.sanchitra.presentation.screens.search

import android.os.Build
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
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
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.screens.common.CommonErrorScreen
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SearchScreen(
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val lazyColumnState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyColumnState.firstVisibleItemIndex == 0 &&
                    lazyColumnState.firstVisibleItemScrollOffset < 100
        }
    }

    val searchState by searchScreenViewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    when (val s = searchState) {
        is SearchState.Searching -> Loading(modifier = Modifier.fillMaxSize())

        is SearchState.Done -> {
            SearchResultView(
                lazyColumnState = lazyColumnState,
                searchResult = s.searchResult,
                searchChannels = searchScreenViewModel::query,
            )
        }
        is SearchState.Error -> {
            CommonErrorScreen(message = "")
        }

        is SearchState.Idle -> {
            SearchResultView(
                lazyColumnState = lazyColumnState,
                searchResult = SearchResult(),
                searchChannels = searchScreenViewModel::query,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultView(
    searchResult: SearchResult,
    searchChannels: (queryString: String) -> Unit,
    modifier: Modifier = Modifier,
    lazyColumnState: LazyListState = rememberLazyListState(),
) {
    val childPadding = rememberChildPadding()
    var searchQuery by remember { mutableStateOf("") }
    val tfFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()


    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyColumnState
    ) {
        item {
            Surface(
                shape = ClickableSurfaceDefaults.shape(shape = SanchitraCardShape),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    pressedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    focusedContentColor = MaterialTheme.colorScheme.onSurface,
                    pressedContentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = if (isTfFocused) 2.dp else 1.dp,
                            color = animateColorAsState(
                                targetValue = if (isTfFocused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.border,
                                label = ""
                            ).value
                        ),
                        shape = SanchitraCardShape
                    )
                ),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start)
                    .padding(top = 8.dp),
                onClick = { tfFocusRequester.requestFocus() }
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { updatedQuery -> searchQuery = updatedQuery },
                    decorationBox = {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .padding(start = 20.dp),
                        ) {
                            it()
                            if (searchQuery.isEmpty()) {
                                Text(
                                    modifier = Modifier.graphicsLayer { alpha = 0.6f },
                                    text = stringResource(R.string.search_screen_et_placeholder),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 4.dp,
                            horizontal = 8.dp
                        )
                        .focusRequester(tfFocusRequester)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                when (it.nativeKeyEvent.keyCode) {
                                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }

                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        focusManager.moveFocus(FocusDirection.Up)
                                    }

                                    KeyEvent.KEYCODE_BACK -> {
                                        focusManager.moveFocus(FocusDirection.Exit)
                                    }
                                }
                            }
                            true
                        },
                    cursorBrush = Brush.verticalGradient(
                        colors = listOf(
                            LocalContentColor.current,
                            LocalContentColor.current,
                        )
                    ),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchChannels(searchQuery)
                        }
                    ),
                    maxLines = 1,
                    interactionSource = tfInteractionSource,
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        item {
            SearchRow(
                title = "Movies",
                items = searchResult.videos,
                key = { it.id },
                itemTitle = { it.title },
                itemImageUrl = { it.image.ifEmpty { it.logo } },
                onItemClick = { selectedVideo ->
                    // onMovieClick(selectedVideo.id)
                }
            )
        }
        item {
            SearchRow(
                title = "Channels",
                items = searchResult.channels,
                key = { it.id },
                itemTitle = { it.name },
                itemImageUrl = { it.logo },
                onItemClick = { selectedChannel ->
                    // onChannelClick(selectedChannel.id)
                }
            )
        }
    }
}
