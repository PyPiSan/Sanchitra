package com.example.sanchitra.presentation.screens.categories

import JetStreamBottomListPadding
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sanchitra.data.models.IPTVChannel
import com.example.sanchitra.presentation.common.Error
import com.example.sanchitra.presentation.common.Loading
import com.example.sanchitra.presentation.common.MovieCard
import com.example.sanchitra.presentation.common.PosterImageIPTVChannel
import com.example.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.example.sanchitra.utils.focusOnInitialVisibility

object CategoryIPTVListScreen {
    const val CategoryNameKey = "categoryName"
}

@Composable
fun CategoryIPTVListScreen(
    onBackPressed: () -> Unit,
    onChannelSelected: (IPTVChannel) -> Unit,
    categoryIPTVListScreenViewModel: CategoryIPTVListScreenViewModel = hiltViewModel()
) {
    val uiState by categoryIPTVListScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        CategoryIPTVListScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        CategoryIPTVListScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is CategoryIPTVListScreenUiState.Done -> {
            CategoryDetails(
                categoryName = s.categoryName,
                categoryChannels = s.channels,
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
    onBackPressed: () -> Unit,
    onChannelSelected: (IPTVChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPadding = rememberChildPadding()
    val isFirstItemVisible = remember { mutableStateOf(false) }

    BackHandler(onBack = onBackPressed)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(
                vertical = childPadding.top.times(3.5f)
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = modifier,
            contentPadding = PaddingValues(JetStreamBottomListPadding)
        ) {
            itemsIndexed(
                categoryChannels,
                key = { _, channel -> channel.id }
            ) { index, iptvChannel ->
                MovieCard(
                    onClick = { onChannelSelected(iptvChannel) },
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .padding(8.dp)
                        .then(
                            if (index == 0)
                                Modifier.focusOnInitialVisibility(isFirstItemVisible)
                            else Modifier
                        ),
                ) {
                    PosterImageIPTVChannel(iptvChannel = iptvChannel, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
