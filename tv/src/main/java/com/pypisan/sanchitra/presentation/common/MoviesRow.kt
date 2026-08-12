package com.pypisan.sanchitra.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f), Horizontal(16f / 9f);
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MoviesRow(
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium, fontSize = 30.sp
    ),
    videoList: List<Videos>,
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onMovieFocused: (video: Videos) -> Unit = {},
    onMovieSelected: (video: Videos) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.focusRestorer()
        ) {
            itemsIndexed(
                videoList, key = { _, movie -> movie.id }) { index, movie ->

                val onCardClicked = remember(movie.id) {
                    { onMovieSelected(movie) }
                }

                MoviesGlassRowItem(
                    modifier = Modifier
                        .onFocusChanged {
                            if (it.isFocused) {
                                onMovieFocused(movie)
                            }
                        }
                        .focusProperties {
                            left = if (index == 0) FocusRequester.Cancel else FocusRequester.Default
                        },
                    index = index,
                    itemDirection = itemDirection,
                    onMovieSelected = { onCardClicked() },
                    video = movie,
                    showItemTitle = showItemTitle,
                    showIndexOverImage = showIndexOverImage
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImmersiveListMoviesRow(
    movieList: List<Videos>,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium, fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onMovieSelected: (Videos) -> Unit = {},
    onMovieFocused: (Videos) -> Unit = {}
) {
    val (lazyRow) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(start = startPadding, end = endPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .focusRequester(lazyRow)
                .focusRestorer()
        ) {
            itemsIndexed(
                movieList, key = { _, movie ->
                    movie.id
                }) { index, movie ->

                MoviesRowItem(
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (it.isFocused) {
                                onMovieFocused(movie)
                            }
                        },
                    index = index,
                    itemDirection = itemDirection,
                    onMovieSelected = {
                        lazyRow.saveFocusedChild()
                        onMovieSelected(it)
                    },
                    video = movie,
                    showItemTitle = showItemTitle,
                    showIndexOverImage = showIndexOverImage
                )
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MoviesGlassRowItem(
    index: Int,
    video: Videos,
    onMovieSelected: (Videos) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
) {
    var isFocused by remember { mutableStateOf(false) }

    MovieGlassCard(
        onClick = { onMovieSelected(video) },
        aspectRatio = itemDirection.aspectRatio,
        title = {
            MoviesRowItemText(
                showItemTitle = showItemTitle, isItemFocused = isFocused, video = video
            )
        },
        modifier = Modifier
            .width(200.dp)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .focusProperties {
                left = if (index == 0) FocusRequester.Cancel else FocusRequester.Default
            }
            .then(modifier)) {
        MoviesRowItemImage(
            modifier = Modifier.fillMaxSize(),
            showIndexOverImage = showIndexOverImage,
            video = video,
            index = index
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MoviesRowItem(
    index: Int,
    video: Videos,
    onMovieSelected: (Videos) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
) {
    var isFocused by remember { mutableStateOf(false) }

    MovieCard(
        onClick = { onMovieSelected(video) }, title = {
        MoviesRowItemText(
            showItemTitle = showItemTitle, isItemFocused = isFocused, video = video
        )
    }, modifier = Modifier
            .width(240.dp)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .focusProperties {
                left = if (index == 0) FocusRequester.Cancel else FocusRequester.Default
            }
            .then(modifier)) {
        MoviesRowItemImageImmersive(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            video = video,
            index = index
        )
    }
}

@Composable
private fun MoviesRowItemImageImmersive(
    video: Videos,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImage(
            title = video.title,
            image = video.meta.banner,
            modifier = modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (showIndexOverImage) {
                        drawRect(
                            color = Color.Black.copy(
                                alpha = 0.1f
                            )
                        )
                    }
                },
        )
        if (showIndexOverImage) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "#${index.inc()}",
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = Shadow(
                        offset = Offset(0.5f, 0.5f), blurRadius = 5f
                    ), color = Color.White
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MoviesRowItemImage(
    video: Videos,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImage(
            title = video.title,
            image = video.image,
            modifier = modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (showIndexOverImage) {
                        drawRect(
                            color = Color.Black.copy(
                                alpha = 0.1f
                            )
                        )
                    }
                },
        )
        if (showIndexOverImage) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "#${index.inc()}",
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = Shadow(
                        offset = Offset(0.5f, 0.5f), blurRadius = 5f
                    ), color = Color.White
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun MoviesRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    video: Videos,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val channelNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "ChannelTitleAlpha",
        )

        Text(
            text = video.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .graphicsLayer {
                    alpha = channelNameAlpha
                },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
