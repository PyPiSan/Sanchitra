package com.pypisan.sanchitra.presentation.screens.livetv

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding


@Composable
fun TVScreen(
    goToTVPlayer: (id: Int) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    tvScreenViewModel: TVScreenViewModel = hiltViewModel(),
) {
    val uiState by tvScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is TVScreenViewModel.TVScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is TVScreenViewModel.TVScreenUiState.Error -> {
            TVErrorScreen(message = s.message)
        }

        is TVScreenViewModel.TVScreenUiState.Ready -> {
            TVCatalog(
                channelCategories = s.categories,
                carouselList = s.carousel,
                goToTVPlayer = goToTVPlayer,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun TVCatalog(
    channelCategories: Map<String, List<Channel>>,
    carouselList: List<Channel>,
    goToTVPlayer:  (id: Int) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier
) {

    var lastFocusedChannelId by rememberSaveable { mutableStateOf<Int?>(null) }

    var restoreFocusKey by rememberSaveable { mutableStateOf<String?>(null) }
    var isRestoring by remember { mutableStateOf(false) }


    val childPadding = rememberChildPadding()
    val lazyListState = rememberLazyListState()

    val categoryList = remember(channelCategories) {
        channelCategories.entries.toList()
    }

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
        // 2. BLOCK scrolling if we are actively restoring focus!
        if (isTopBarVisible && !isRestoring) {
            lazyListState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(top = childPadding.top, bottom = 104.dp),
    ) {
        item{
            TVScreenChannelList(
                channelList = carouselList,
                goToTVPlayer = { id ->
                    restoreFocusKey = "carousel_$id"
                    isRestoring = true
                    goToTVPlayer(id)
                },
                lastFocusedChannelId = lastFocusedChannelId,
                onChannelFocused = { lastFocusedChannelId = it }
            )
        }
        items(
            items = categoryList,
            key = { it.key }
        ) { entry ->
            val rowKey = "category_${entry.key}"

            TVRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = entry.key,
                channels = entry.value,
                rowKey = rowKey, // Pass the row prefix
                restoreFocusKey = restoreFocusKey,
                onFocusRestored = {
                    restoreFocusKey = null
                    isRestoring = false // Unblock native scrolling
                },
                goToTVPlayer = { id ->
                    // Combine Row name + ID so it is 100% unique
                    restoreFocusKey = "${rowKey}_$id"
                    isRestoring = true
                    goToTVPlayer(id)
                }
            )
        }
    }
}