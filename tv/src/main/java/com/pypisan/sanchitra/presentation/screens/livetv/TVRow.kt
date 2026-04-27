package com.pypisan.sanchitra.presentation.screens.livetv

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
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
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.ChannelCard
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.presentation.common.PosterImageChannel

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f),
    Horizontal(16f / 9f);
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TVRow(
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    channels: List<Channel>,
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    goToTVPlayer: (channel: Channel) -> Unit,
) {
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }
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
        AnimatedContent(
            targetState = channels,
            label = "",
        ) { tvState ->
            LazyRow(
                contentPadding = PaddingValues(
                    start = startPadding,
                    end = endPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer {
                        firstItem
                    }
            ) {
                itemsIndexed(tvState, key = { _, channels -> channels.id }) { index, channel ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }
                    TVRowItem(
                        channel = channel,
                        goToTVPlayer = {
                            lazyRow.saveFocusedChild()
                            goToTVPlayer(channel)
                        },
                        modifier = itemModifier,
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
private fun TVRowItem(
    index: Int,
    channel: Channel,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    onChannelFocused: (Channel) -> Unit = {},
    goToTVPlayer: (channel: Channel) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    ChannelCard(
        onClick = { goToTVPlayer(channel) },
        title = {
            ChannelRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                channel = channel
            )
        },
        modifier = Modifier
            .width(220.dp)
            .aspectRatio(16f / 9f)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onChannelFocused(channel)
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
//            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            modifier = Modifier.fillMaxSize(),
            showIndexOverImage = showIndexOverImage,
            channel = channel,
            index = index
        )
    }
}


@Composable
private fun ChannelRowItemImage(
    channel: Channel,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImageChannel(
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
    channel: Channel,
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