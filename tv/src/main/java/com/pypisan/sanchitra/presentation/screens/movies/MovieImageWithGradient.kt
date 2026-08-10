package com.pypisan.sanchitra.presentation.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.screens.auth.trailerPlayer
import com.pypisan.sanchitra.utils.BlueGray300
import com.pypisan.sanchitra.utils.DeepPurple300

@androidx.annotation.OptIn(UnstableApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun MovieImageWithGradients(
    modifier: Modifier = Modifier,
    video: Videos,
    isPlayerActive: Boolean,
    isTrailerDialogOpen: Boolean = false,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {
    // Tracks if dialog was ever opened so background trailer stops permanently
    var hasDialogBeenOpened by remember { mutableStateOf(false) }

    LaunchedEffect(isTrailerDialogOpen) {
        if (isTrailerDialogOpen) {
            hasDialogBeenOpened = true
        }
    }

    val shouldPlayBackgroundTrailer = !video.meta.trailer.isNullOrEmpty() &&
            !isPlayerActive &&
            !isTrailerDialogOpen &&
            !hasDialogBeenOpened

    Box(modifier = modifier) {

        // 1. Base Poster Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.meta.banner)
                .crossfade(true)
                .build(),
            contentDescription = StringConstants
                .Composable
                .ContentDescription
                .moviePoster(video.title),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Background Trailer Player
        if (shouldPlayBackgroundTrailer) {
            val exoPlayer = trailerPlayer(video.meta.trailer, 50f)
            var isVideoReady by remember { mutableStateOf(false) }

            DisposableEffect(exoPlayer) {
                val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isVideoReady = state == Player.STATE_READY && exoPlayer.isPlaying
                    }
                }
                exoPlayer.addListener(listener)

                onDispose {
                    exoPlayer.removeListener(listener)
                    exoPlayer.release()
                }
            }

            if (isVideoReady) {
                PlayerSurface(
                    player = exoPlayer,
                    surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 3. Bottom Vertical Surface Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()

                    // Fades smoothly into solid surface color at the bottom
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                gradientColor.copy(alpha = 0f),
                                gradientColor.copy(alpha = 0.5f),
                                gradientColor
                            ),
                            startY = size.height * 0.4f,
                            endY = size.height
                        )
                    )
                }
        )
    }
}