package com.pypisan.sanchitra.presentation.screens.search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.data.entities.ChannelItem
import com.pypisan.sanchitra.data.entities.VideoItem
import com.pypisan.sanchitra.data.entities.VideoMeta
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.presentation.common.ItemDirection
import com.pypisan.sanchitra.presentation.common.PosterImage
import com.pypisan.sanchitra.presentation.common.PosterImageChannel
import com.pypisan.sanchitra.presentation.common.SearchCard
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding


@Composable
fun SearchRow(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
        color = MaterialTheme.colorScheme.inverseOnSurface
    ),
    movieItem: List<VideoItem>,
    iptvItem: List<ChannelItem>,
    itemType: String,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    onItemClick: (id: Int) -> Unit,
    onMovieClick: (video: Videos) -> Unit,
    onItemFocused: (Int) -> Unit,
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
) {
    // Automatically hides the row if the list is empty
    if (movieItem.isEmpty() && itemType == "movies") return
    if (iptvItem.isEmpty() && itemType == "iptv") return

    Column(modifier = modifier) {
        Text(
            text = title,
            style = titleStyle,
            modifier = Modifier
                .alpha(1f)
                .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(start = startPadding, end = endPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.focusRestorer()
        ) {
            if (itemType == "iptv") {
                itemsIndexed(iptvItem, key = { _, channel -> channel.id }) { index, channel ->

                    val onCardClicked = remember(channel.id) {
                        { onItemClick(channel.id) }
                    }

                    SearchRowItem(
                        id = channel.id,
                        name = channel.name,
                        itemType = "iptv",
                        logo = channel.logo,
                        onItemClicked = { onCardClicked() },
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                onItemFocused(channel.id)
                            }
                        },
                        index = index,
                        itemDirection = itemDirection,
                        showItemTitle = showItemTitle,
                        showIndexOverImage = showIndexOverImage,
                    )
                }
            } else {
                itemsIndexed(movieItem, key = { _, videos -> videos.id }) { index, videos ->

                    val onCardClicked = remember(videos.id) {
                        {
                            onMovieClick(
                                Videos(
                                    id = videos.id,
                                    title = videos.title,
                                    image = videos.image,
                                    logo = videos.logo,
                                    url = "",
                                    headers = null,
                                    drm = false,
                                    licenseKey = "",
                                    licenseUrl = "",
                                    duration = videos.duration,
                                    meta = VideoMeta(
                                        banner = videos.meta.banner,
                                        description = videos.meta.description,
                                        trailer = videos.meta.trailer,
                                        releaseDate = videos.meta.releaseDate,
                                        subtitleUrl = videos.meta.subtitleUrl,
                                        imdbRating = videos.meta.imdbRating,
                                        rottenTomatoes = videos.meta.rottenTomatoes,
                                        budget = videos.meta.budget,
                                        revenue = videos.meta.revenue,
                                    ),
                                    categories = videos.categories,
                                    language = videos.languages
                                )
                            )
                        }
                    }

                    SearchRowItem(
                        id = videos.id,
                        name = videos.title,
                        itemType = "movies",
                        logo = videos.meta.banner,
                        onItemClicked = onCardClicked,
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                onItemFocused(videos.id)
                            }
                        },
                        index = index,
                        itemDirection = itemDirection,
                        showItemTitle = showItemTitle,
                        showIndexOverImage = showIndexOverImage,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchRowItem(
    id: Int,
    index: Int,
    name: String,
    itemType: String,
    logo: String,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    onItemClicked: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    SearchCard(
        onClick = onItemClicked, title = {
        SearchRowItemText(
            showItemTitle = showItemTitle, isItemFocused = isFocused, name = name
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
        if (itemType == "iptv") {
            SearchRowItemImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(itemDirection.aspectRatio),
                showIndexOverImage = showIndexOverImage,
                name = name,
                logoUrl = logo,
            )
        } else {
            Box(contentAlignment = Alignment.CenterStart) {
                PosterImage(
                    image = logo,
                    title = name,
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
            }

        }
    }
}


@Composable
private fun SearchRowItemImage(
    name: String,
    logoUrl: String,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImageChannel(
            logoUrl = logoUrl,
            name = name,
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
    }
}

@Composable
private fun SearchRowItemText(
    showItemTitle: Boolean, isItemFocused: Boolean, name: String, modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val nameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "titleAlpha",
        )

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .graphicsLayer {
                    alpha = nameAlpha
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}