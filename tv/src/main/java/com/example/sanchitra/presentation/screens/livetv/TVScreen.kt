package com.example.sanchitra.presentation.screens.livetv

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.presentation.common.Loading
import com.example.sanchitra.presentation.screens.dashboard.rememberChildPadding


@Composable
fun TVScreen(
    goToVideoPlayer: (channel: Channel) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    tvScreenViewModel: TVScreenViewModel = hiltViewModel(),
) {

    val uiState by tvScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {

        TVScreenViewModel.TVScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is TVScreenViewModel.TVScreenUiState.Error -> {
            TVErrorScreen(message = s.message)
        }

        is TVScreenViewModel.TVScreenUiState.Ready -> {
            TVCatalog(
                channelCategories = s.categories,
                carouselList = s.carousel,
                goToVideoPlayer = goToVideoPlayer,
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
    goToVideoPlayer: (channel: Channel) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier
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
        if (isTopBarVisible) {
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
                goToVideoPlayer = goToVideoPlayer,
            )
        }
        items(
            items = channelCategories.entries.toList(),
            key = { it.key }
        ) { entry ->

            TVRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = entry.key,
                channels = entry.value,
                goToVideoPlayer = goToVideoPlayer,
            )
        }
    }
}