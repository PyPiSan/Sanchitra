package com.pypisan.sanchitra.presentation.screens.movies

import com.pypisan.sanchitra.presentation.theme.SanchitraButtonShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.toHrMinFormat
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetails(
    video: Videos,
    isPlayerActive: Boolean,
    hasHistory: Boolean = false,
    openVideoPlayer: (metaId: String) -> Unit
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var showTrailerDialog by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val screenHeight = with(density) { containerSize.height.toDp() }
    val dynamicHeaderHeight = screenHeight * 0.85f

    // Store surface color to use consistently across all layers
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dynamicHeaderHeight)
            .background(surfaceColor)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        // 1. Movie Banner / Background Trailer
        MovieImageWithGradients(
            video = video,
            modifier = Modifier.fillMaxSize(),
            isPlayerActive = isPlayerActive,
            isTrailerDialogOpen = showTrailerDialog,
            gradientColor = surfaceColor
        )

        // 2. Full-Height Glass Overlay Panel (Uses surfaceColor instead of Color.Black)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            surfaceColor,
                            surfaceColor.copy(alpha = 0.9f),
                            surfaceColor.copy(alpha = 0.5f),
                            surfaceColor.copy(alpha = 0f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = childPadding.start, top = 80.dp, end = 24.dp, bottom = 24.dp)
            ) {
                MovieLargeTitle(movieTitle = video.title)

                Column(
                    modifier = Modifier.alpha(0.85f)
                ) {
                    MovieDescription(description = video.meta.description)
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 16.dp),
                        texts = listOf(
                            video.meta.releaseDate!!,
                            video.categories.joinToString(", "),
                            video.duration.toHrMinFormat()
                        )
                    )
                }

                // Side-by-Side Action Buttons
                MovieActionButtons(
                    hasTrailerLink = !video.meta.trailer.isNullOrEmpty(),
                    hasHistory = hasHistory,
                    onWatchClick = { openVideoPlayer(video.id.toString()) },
                    onTrailerClick = { showTrailerDialog = true },
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                )
            }
        }
    }

    // Trailer Dialog Overlay
    if (showTrailerDialog && !video.meta.trailer.isNullOrEmpty()) {
        TrailerDialog(
            trailerUrl = video.meta.trailer,
            onDismiss = { showTrailerDialog = false }
        )
    }
}

@Composable
private fun MovieActionButtons(
    hasTrailerLink: Boolean,
    hasHistory: Boolean,
    onWatchClick: () -> Unit,
    onTrailerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Button 1: Watch Now / Continue Watching
        Button(
            onClick = onWatchClick,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            shape = ButtonDefaults.shape(shape = SanchitraButtonShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = if (hasHistory) "Continue Watching" else "Watch Now",
                style = MaterialTheme.typography.titleSmall
            )
        }

        // Button 2: Watch Trailer
        if (hasTrailerLink) {
            Button(
                onClick = onTrailerClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                shape = ButtonDefaults.shape(shape = SanchitraButtonShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Movie,
                    contentDescription = null
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.watch_trailer),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun MovieDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 5
    )
}

@Composable
private fun MovieLargeTitle(
    movieTitle: String,
    modifier: Modifier = Modifier
) {
    // Gold/Amber gradient (or replace with your brand colors)
    val titleGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFFA500), // Orange
            Color(0xFFFF4500)  // Red-Orange
        )
    )

    Text(
        text = movieTitle,
        modifier = modifier,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            brush = titleGradient
        ),
        maxLines = 2
    )
}