package com.pypisan.sanchitra.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.models.TrendingChannel
import com.pypisan.sanchitra.presentation.common.ChannelCard
import com.pypisan.sanchitra.presentation.common.PosterImageTrendingChannel
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding


enum class ItemDirection(val aspectRatio: Float) {
    Horizontal(16f / 9f);
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TrendingChannelRow(
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    channels: List<TrendingChannel>,
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    goToTVPlayer: (id: Int) -> Unit,
    onChannelFocused: (Int) -> Unit,
    isActive: Boolean = false,
    lastFocusedChannelId: Int?,
) {

    val (lazyRow) = remember { FocusRequester.createRefs() }
    Column(
        modifier = modifier
            .focusGroup()
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
            modifier = Modifier
                .focusRestorer()
        ) {
            itemsIndexed(channels, key = { _, channels -> channels.id }) { index, channel ->

                TrendingChannelRowItem(
                    channel = channel,
                    onChannelFocused = {
                        onChannelFocused(channel.id)
                    },
                    goToTVPlayer = {
                        goToTVPlayer(channel.id)
                    },
                    modifier = Modifier,
                    index = index,
                    itemDirection = itemDirection,
                    showItemTitle = showItemTitle,
                    showIndexOverImage = showIndexOverImage,
                )
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TrendingChannelRowItem(
    index: Int,
    channel: TrendingChannel,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    onChannelFocused: (Int) -> Unit,
    goToTVPlayer: (id: Int) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    ChannelCard(
        onClick = { goToTVPlayer(channel.id) },
        title = {
            ChannelRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                channel = channel
            )
        },
        modifier = Modifier
            .width(220.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused && it.hasFocus) {
                    onChannelFocused(channel.id)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
    ) {
        ChannelRowItemImage(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            channel = channel,
            index = index
        )
    }
}


@Composable
private fun ChannelRowItemImage(
    channel: TrendingChannel,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImageTrendingChannel(
            channel = channel,
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
                style = MaterialTheme.typography.displayLarge
                    .copy(
                        shadow = Shadow(
                            offset = Offset(0.5f, 0.5f),
                            blurRadius = 5f
                        ),
                        color = Color.White
                    ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ChannelRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    channel: TrendingChannel,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val channelNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            label = "",
        )
        Text(
            text = channel.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(channelNameAlpha)
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}