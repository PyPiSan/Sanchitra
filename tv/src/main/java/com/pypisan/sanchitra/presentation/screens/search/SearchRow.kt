package com.pypisan.sanchitra.presentation.screens.search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.presentation.common.ChannelCard
import com.pypisan.sanchitra.presentation.common.ItemDirection
import com.pypisan.sanchitra.presentation.common.PosterImageChannel
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding


@Composable
fun <T> SearchRow(
    title: String,
    items: List<T>,
    key: (T) -> Any,
    itemTitle: (T) -> String,
    itemImageUrl: (T) -> String,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    onItemFocused: (T) -> Unit = {},
) {
    // Automatically hides the row if the list is empty
    if (items.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = startPadding, top = 20.dp, bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(start = startPadding, end = endPadding),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.focusRestorer()
        ) {
            itemsIndexed(items, key = { _, item -> key(item) }) { index, item ->
                var isFocused by remember { mutableStateOf(false) }

                ChannelCard(
                    onClick = { onItemClick(item) },
                    title = {
                        val titleAlpha by animateFloatAsState(
                            targetValue = if (isFocused) 1f else 0f,
                            animationSpec = tween(durationMillis = 200),
                            label = "TitleAlpha"
                        )
                        Text(
                            text = itemTitle(item),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .graphicsLayer { alpha = titleAlpha }
                        )
                    },
                    modifier = Modifier
                        .width(240.dp)
                        .onFocusChanged {
                            isFocused = it.isFocused
                            if (it.isFocused) onItemFocused(item)
                        }
                        .focusProperties {
                            left = if (index == 0) FocusRequester.Cancel else FocusRequester.Default
                        }
                ) {
                    PosterImageChannel(
                        logoUrl = itemImageUrl(item),
                        name = itemTitle(item),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(itemDirection.aspectRatio)
                    )
                }
            }
        }
    }
}