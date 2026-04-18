package com.example.sanchitra.presentation.screens.livetv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sanchitra.data.models.Channel


@Composable
fun TVScreen(
    onChannelClick: (Channel) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
) {
    val viewModel: TVScreenViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    when (val uiState = state) {

        TVScreenViewModel.TVUiState.Loading -> {
            TVLoadingScreen()
        }

        is TVScreenViewModel.TVUiState.Error -> {
            TVErrorScreen(message = uiState.message)
        }

        is TVScreenViewModel.TVUiState.Ready -> {
            TVCatalog(
                categories = uiState.categories,
                onChannelClick = onChannelClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun TVCatalog(
    categories: Map<String, List<Channel>>,
    onChannelClick: (Channel) -> Unit,
    onScroll: (Boolean) -> Unit,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier
) {
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
        contentPadding = PaddingValues(
            top = 80.dp,
            bottom = 104.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(
            items = categories.entries.toList(),
            key = { it.key }
        ) { entry ->

            ChannelRow(
                title = entry.key,
                channels = entry.value,
                onChannelClick = onChannelClick
            )
        }
    }
}