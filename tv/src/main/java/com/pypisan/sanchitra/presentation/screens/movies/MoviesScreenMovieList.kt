package com.pypisan.sanchitra.presentation.screens.movies

import androidx.compose.animation.core.LinearOutSlowInEasing
import com.pypisan.sanchitra.presentation.theme.SanchitraBorderWidth
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

    LazyRow(
        modifier = modifier.focusRestorer(),
        contentPadding = PaddingValues(
            start = startPadding,
            end = endPadding
        )
    ) {
        itemsIndexed(
            items = videoList,
            key = { _, movie -> movie.id }
        ) { _, movie ->

            val onCardClicked = remember(movie.id) {
                { onMovieClick(movie) }
            }

            MovieListItem(
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) {
                            onMovieFocused(movie.id)
                        }
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(SanchitraBorderWidth))
        var isFocused by remember { mutableStateOf(false) }

        // Smooth glass light sweep progression on focus (0.0 -> 1.0)
        val lightSweepProgress by animateFloatAsState(
            targetValue = if (isFocused) 1f else 0f,
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearOutSlowInEasing
            ),
            label = "MovieItemLightSweep"
        )

        CompactCard(
            modifier = modifier
                .width(itemWidth)
                .aspectRatio(2f)
                .padding(end = 32.dp)
                .onFocusChanged {
                    isFocused = it.isFocused || it.hasFocus
                },

            scale = CardDefaults.scale(
                scale = 1f,
                focusedScale = 1.05f
            ),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(
                        width = SanchitraBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            ),
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = { onMovieClick(video) },
            image = {
                val contentAlpha by animateFloatAsState(
                    targetValue = if (isFocused) 1f else 0.5f,
                    label = "ContentAlpha"
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = video.meta.banner,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = contentAlpha }
                    )

                    // Glass Light Sweep Overlay
                    if (isFocused) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()

                                    val sweepOffset = lightSweepProgress * (size.width * 2.5f) - size.width

                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.White.copy(alpha = 0.35f),
                                                Color.Transparent
                                            ),
                                            start = Offset(sweepOffset, 0f),
                                            end = Offset(sweepOffset + size.width * 0.6f, size.height)
                                        )
                                    )
                                }
                        )
                    }
                }
            },
            title = {
                Column {
                    Text(
                        text = video.meta.description.takeIf { it.isNotBlank() } ?: video.title,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .graphicsLayer { alpha = 0.6f }
                            .padding(start = 24.dp, end = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 24.dp, bottom = 24.dp)
                    )
                }
            }
        )
    }
}
