package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer


@Composable
fun loopPlayer(
    videoUrl: String,
    level: Float = 0f
): ExoPlayer {
    val context = LocalContext.current

    return remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            volume = level
            prepare()
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun trailerPlayer(
    videoUrl: String,
    level: Float = 0f
): ExoPlayer {
    val context = LocalContext.current

    return remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)

            // Forces video to scale & crop to fill the layout bounds (removes black bars)
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING

            playWhenReady = true
            volume = level
            prepare()
        }
    }
}
