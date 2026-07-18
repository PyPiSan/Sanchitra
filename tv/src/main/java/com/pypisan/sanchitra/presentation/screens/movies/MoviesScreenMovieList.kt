package com.pypisan.sanchitra.presentation.screens.movies

import com.pypisan.sanchitra.presentation.theme.JetStreamBorderWidth
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CompactCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MoviesScreenMovieList(
    modifier: Modifier = Modifier,
    videoList: List<Videos>,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    onMovieClick: (video: Videos) -> Unit,
    onMovieFocused: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    // 1. Manually track the ID (survives perfectly because the screen is never destroyed)
    var lastFocusedId by rememberSaveable { mutableStateOf<Int?>(null) }

    // 2. Map Requesters for the interceptor
    val focusRequesters = remember(videoList) {
        videoList.associate { it.id to FocusRequester() }
    }

    LazyRow(
        modifier = modifier
            .focusGroup()
            // 3. THE MAGIC INTERCEPTOR
            .focusProperties {
                onEnter = {
                    // When focus tries to enter this row, explicitly route it to our saved ID!
                    val target = lastFocusedId ?: videoList.firstOrNull()?.id

                    if (target != null && focusRequesters.containsKey(target)) {
                        focusRequesters[target]!! // Send focus here
                    } else {
                        FocusRequester.Default // Fallback
                    }
                }
            },
        state = listState,
        contentPadding = PaddingValues(
            start = startPadding,
            end = endPadding
        )
    ) {
        itemsIndexed(
            items = videoList,
            key = { _, movie -> movie.id }
        ) { index, movie ->

            val focusRequester = focusRequesters[movie.id] ?: FocusRequester()

            val onCardClicked = remember(movie.id) {
                { onMovieClick(movie) }
            }

            MovieListItem(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            // 4. Update our memory every time a new card is highlighted
                            lastFocusedId = movie.id
                            onMovieFocused(movie.id)
                        }
                    }
                    .focusProperties {
                        left = if (index == 0) FocusRequester.Cancel else FocusRequester.Default
                    },
                itemWidth = 432.dp,
                onMovieClick = { onCardClicked() },
                video = movie,
            )
        }
    }
}

@Composable
private fun MovieListItem(
    itemWidth: Dp,
    video: Videos,
    modifier: Modifier = Modifier,
    onMovieClick: (video: Videos) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(JetStreamBorderWidth))
        var isFocused by remember { mutableStateOf(false) }

        CompactCard(
            modifier = modifier // The logged modifier is passed directly here
                .width(itemWidth)
                .aspectRatio(2f)
                .padding(end = 32.dp)
                .onFocusChanged {
                    isFocused = it.isFocused || it.hasFocus
                },
            scale = CardDefaults.scale(focusedScale = 1f),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(width = JetStreamBorderWidth, color = MaterialTheme.colorScheme.onSurface)
                )
            ),
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = { onMovieClick(video) },
            image = {
                val contentAlpha by animateFloatAsState(targetValue = if (isFocused) 1f else 0.5f, label = "")
                AsyncImage(
                    model = video.meta.banner,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().graphicsLayer { alpha = contentAlpha }
                )
            },
            title = {
                Column {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier.graphicsLayer { alpha = 0.6f }.padding(start = 24.dp)
                    )
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}
