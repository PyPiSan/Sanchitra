package com.pypisan.sanchitra.presentation.screens.categories

import com.pypisan.sanchitra.presentation.theme.JetStreamBottomListPadding
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.models.IPTVChannel
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.common.MovieCard
import com.pypisan.sanchitra.presentation.common.PosterImageIPTVChannel
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

object CategoryIPTVListScreen {
    const val CategoryNameKey = "categoryName"
}

@Composable
fun CategoryIPTVListScreen(
    isPlayerActive: Boolean, // Receive the state here
    onBackPressed: () -> Unit,
    onChannelSelected: (iptvChannelId: String) -> Unit,
    categoryIPTVListScreenViewModel: CategoryIPTVListScreenViewModel = hiltViewModel()
) {
    val uiState by categoryIPTVListScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is CategoryIPTVListScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is CategoryIPTVListScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is CategoryIPTVListScreenUiState.Done -> {
            CategoryDetails(
                categoryName = s.categoryName,
                categoryChannels = s.channels,
                isPlayerActive = isPlayerActive, // Pass it down
                onBackPressed = onBackPressed,
                onChannelSelected = onChannelSelected
            )
        }
    }
}

@Composable
private fun CategoryDetails(
    categoryChannels: List<IPTVChannel>,
    categoryName: String,
    isPlayerActive: Boolean, // Receive the state here
    onBackPressed: () -> Unit,
    onChannelSelected: (iptvChannelId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPadding = rememberChildPadding()
    val gridState = rememberLazyGridState()

    var lastFocusedIndex by rememberSaveable(categoryName) { mutableIntStateOf(0) }

    val focusRequesters = remember(categoryChannels) {
        List(categoryChannels.size) { FocusRequester() }
    }

    LaunchedEffect(isPlayerActive) {
        if (!isPlayerActive) {
            kotlinx.coroutines.delay(100) // Wait for player overlay to unmount
            try {
                focusRequesters.getOrNull(lastFocusedIndex)?.requestFocus()
            } catch (e: Exception) { }
        }
    }

    LaunchedEffect(categoryChannels) {
        gridState.scrollToItem(lastFocusedIndex)
    }

    BackHandler(onBack = onBackPressed)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().focusGroup(),
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(vertical = childPadding.top.times(3.5f))
        )
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(JetStreamBottomListPadding)
        ) {
            itemsIndexed(
                categoryChannels, key = { _, channel -> channel.id }) { index, iptvChannel ->
                MovieCard(
                    onClick = {
                        onChannelSelected(iptvChannel.id)
                    },
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .padding(8.dp)
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged {
                            if (it.isFocused) {
                                lastFocusedIndex = index
                            }
                        },
                ) {
                    PosterImageIPTVChannel(
                        iptvChannel = iptvChannel, modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
