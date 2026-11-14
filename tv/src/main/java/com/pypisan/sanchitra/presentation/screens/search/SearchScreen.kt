package com.pypisan.sanchitra.presentation.screens.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.animation.AnimatedVisibility
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
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
import androidx.tv.material3.Icon
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
import java.util.Locale

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
    var searchedQuery by rememberSaveable { mutableStateOf("") }

    var isSearchTriggered by rememberSaveable { mutableStateOf(false) }
    val isActuallyLoading = isLoading || isSearchTriggered

    // Only move focus down to results after an actual search, not on tab re-entry
    var shouldMoveFocusToResults by remember { mutableStateOf(false) }

    var lastSavedQuery by rememberSaveable { mutableStateOf("") }

    val tfFocusRequester = remember { FocusRequester() }
    val searchBarFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var focusedSection by rememberSaveable { mutableStateOf("") }
    var lastFocusedId by rememberSaveable { mutableStateOf<Int?>(null) }

    val historyManager = remember { SearchHistoryManager.getInstance(context) }

    val performSearch: (String) -> Unit = { query ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotBlank()) {
            searchQuery = trimmedQuery
            searchedQuery = trimmedQuery
            lastSavedQuery = ""
            shouldMoveFocusToResults = true
            isSearchTriggered = true
            keyboardController?.hide()
            searchBarFocusRequester.requestFocus()
            searchChannels(trimmedQuery)
        }
    }

    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.trim()
            if (!spokenText.isNullOrBlank()) {
                performSearch(spokenText)
            }
        }
    }

    val launchVoiceSearch = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search movies & channels...")
        }
        try {
            voiceSearchLauncher.launch(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Voice search is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            isSearchTriggered = false
        }
    }

    LaunchedEffect(isSearchTriggered) {
        if (isSearchTriggered) {
            delay(5000)
            isSearchTriggered = false
            shouldMoveFocusToResults = false
        }
    }

    LaunchedEffect(isActuallyLoading, searchResult) {
        if (!isActuallyLoading) {
            delay(100)
            val hasResults = searchResult.videos.isNotEmpty() || searchResult.channels.isNotEmpty()
            if (hasResults) {
                if (searchedQuery.isNotBlank() && searchedQuery != lastSavedQuery) {
                    historyManager.appendSearchHistory(searchedQuery)
                    lastSavedQuery = searchedQuery
                }
                if (shouldMoveFocusToResults) {
                    shouldMoveFocusToResults = false
                    focusManager.moveFocus(FocusDirection.Down)
                }
            } else if (
                shouldMoveFocusToResults &&
                searchedQuery.isNotBlank() &&
                searchedQuery == searchQuery
            ) {
                shouldMoveFocusToResults = false
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
            ModernSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { performSearch(searchQuery) },
                onVoiceClick = launchVoiceSearch,
                onClearClick = {
                    searchBarFocusRequester.requestFocus()
                    searchQuery = ""
                    searchedQuery = ""
                },
                searchBarFocusRequester = searchBarFocusRequester,
                tfFocusRequester = tfFocusRequester,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start)
                    .padding(top = 12.dp, bottom = 8.dp)
            )
        }

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
        } else if (
            searchResult.videos.isEmpty() &&
            searchResult.channels.isEmpty() &&
            searchedQuery.isNotBlank() &&
            searchedQuery == searchQuery
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
        } else {
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

@Composable
private fun ModernSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onVoiceClick: () -> Unit,
    onClearClick: () -> Unit,
    searchBarFocusRequester: FocusRequester,
    tfFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()

    val surfaceInteractionSource = remember { MutableInteractionSource() }
    val isSurfaceFocused by surfaceInteractionSource.collectIsFocusedAsState()

    val isSearchBoxFocused = isTfFocused || isSurfaceFocused

    val searchIconTint by animateColorAsState(
        targetValue = if (isSearchBoxFocused) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        label = "SearchIconTint"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSearchBoxFocused) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f),
        label = "SearchBorderColor"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSearchBoxFocused) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        label = "SearchContainerColor"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Pill Search Input
        Surface(
            shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(50)),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.01f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = containerColor,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = ClickableSurfaceDefaults.border(
                border = Border(
                    border = BorderStroke(
                        width = if (isSearchBoxFocused) 2.dp else 1.dp,
                        color = borderColor
                    ),
                    shape = RoundedCornerShape(50)
                ),
                focusedBorder = Border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(50)
                )
            ),
            tonalElevation = if (isSearchBoxFocused) 6.dp else 2.dp,
            interactionSource = surfaceInteractionSource,
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .focusRequester(searchBarFocusRequester),
            onClick = { tfFocusRequester.requestFocus() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = searchIconTint,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.search_screen_et_placeholder),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = LocalContentColor.current.copy(alpha = 0.55f)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
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
                                            onSearch()
                                            return@onKeyEvent true
                                        }
                                    }
                                }
                                false
                            },
                        cursorBrush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearch() }
                        ),
                        singleLine = true,
                        maxLines = 1,
                        interactionSource = tfInteractionSource,
                        textStyle = MaterialTheme.typography.titleSmall.copy(
                            color = LocalContentColor.current
                        )
                    )
                }

                AnimatedVisibility(
                    visible = isSurfaceFocused && !isTfFocused && searchQuery.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Press OK to type",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Clear Button (direct conditional instead of AnimatedVisibility for reliable TV focus)
        if (searchQuery.isNotEmpty()) {
            Surface(
                shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    focusedContentColor = MaterialTheme.colorScheme.surface
                ),
                border = ClickableSurfaceDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
                        ),
                        shape = CircleShape
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        shape = CircleShape
                    )
                ),
                modifier = Modifier.size(54.dp),
                onClick = onClearClick
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 3. Circular Voice Search Button
        Surface(
            shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = ClickableSurfaceDefaults.border(
                border = Border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
                    ),
                    shape = CircleShape
                ),
                focusedBorder = Border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    ),
                    shape = CircleShape
                )
            ),
            tonalElevation = 4.dp,
            modifier = Modifier.size(54.dp),
            onClick = onVoiceClick
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = "Voice search",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}