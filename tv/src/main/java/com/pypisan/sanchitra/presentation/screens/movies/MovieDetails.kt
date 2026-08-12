package com.pypisan.sanchitra.presentation.screens.movies

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.toHrMinFormat
import com.pypisan.sanchitra.storage.WatchProgress
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetails(
    video: Videos,
    isPlayerActive: Boolean,
    watchProgress: WatchProgress?,
    openVideoPlayer: (metaId: String, isContinue: Boolean) -> Unit
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
    val hasHistory = watchProgress != null && watchProgress.timeMillis > 0

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
                    onWatchNowClick = { openVideoPlayer(video.id.toString(), false) },
                    onTrailerClick = { showTrailerDialog = true },
                    onContinueWatchingClick = {openVideoPlayer(video.id.toString(), true) },
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
    onWatchNowClick: () -> Unit,
    onContinueWatchingClick: () -> Unit,
    onTrailerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonButton(
                modifier = Modifier,
                onButtonClick = onWatchNowClick,
                imageVectorIcon = if (!hasHistory) Icons.Outlined.PlayArrow else Icons.Outlined.Replay,
                buttonText = if (!hasHistory) "Watch Now" else "Start Over"
            )

            // 2. Watch Trailer
            if (hasTrailerLink) {
                CommonButton(
                    modifier = Modifier,
                    onButtonClick = onTrailerClick,
                    imageVectorIcon = Icons.Outlined.Movie,
                    buttonText = stringResource(R.string.watch_trailer)
                )
            }
        }

        if (hasHistory) {
            CommonButton(
                modifier = Modifier,
                onButtonClick = onContinueWatchingClick,
                imageVectorIcon = Icons.Outlined.PlayArrow,
                buttonText = "Continue Watching"
            )

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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CommonButton(
    onButtonClick: () -> Unit,
    imageVectorIcon: ImageVector,
    buttonText: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier,
        enabled = true,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.colors(
            containerColor = Color.White.copy(alpha = 0.12f),
            contentColor = Color.White,

            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface
        ),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(8.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(1.5.dp, Color.White),
                shape = RoundedCornerShape(8.dp)
            )
        ),
        scale = ButtonDefaults.scale(
            scale = 1f,
            focusedScale = 1.05f
        ),
        glow = ButtonDefaults.glow(
            glow = Glow.None,
            focusedGlow = Glow(
                elevationColor = Color.White.copy(alpha = 0.8f), // Intense glow on focus
                elevation = 20.dp
            )
        )
    ) {
        Icon(
            imageVector = imageVectorIcon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = buttonText,
            style = MaterialTheme.typography.titleSmall
        )
    }
}