package com.pypisan.sanchitra.presentation.screens.videoPlayer

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.SubtitleDrawer
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControls
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.pypisan.sanchitra.utils.handleDPadKeyEvents
import com.pypisan.sanchitra.data.entities.SubtitleTrack

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenContent(
    title: String,
    exoPlayer: ExoPlayer,
    onBackPressed: () -> Unit,
    isBuffering: Boolean,
    isError: Boolean,
) {
    val videoPlayerState = rememberVideoPlayerState()
    val pulseState = rememberVideoPlayerPulseState()
    val focusRequester = remember { FocusRequester() }

    var showSubtitleDrawer by rememberSaveable {
        mutableStateOf(false)
    }

    var subtitles by remember {
        mutableStateOf(
            listOf(
                SubtitleTrack("off", "Off", true),
                SubtitleTrack("en", "English"),
                SubtitleTrack("hi", "Hindi"),
                SubtitleTrack("ta", "Tamil")
            )
        )
    }

    LaunchedEffect(exoPlayer.isPlaying) {
        if (exoPlayer.isPlaying) {
            videoPlayerState.showControls(true)
        } else {
            videoPlayerState.showControls(false)
        }
    }

    RememberPlaybackWatchdog(
        exoPlayer = exoPlayer,
        onFreeze = {

            val mediaItem = exoPlayer.currentMediaItem

            if (mediaItem != null) {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.seekToDefaultPosition()
                exoPlayer.play()
            }
        }
    )

    BackHandler {
        exoPlayer.release()
        onBackPressed()
    }

    Box(
        Modifier
            .dPadEvents(
                exoPlayer, videoPlayerState, pulseState
            )
            .focusable()
    ) {
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.resizeWithContentScale(
                contentScale = ContentScale.Fit, sourceSizeDp = null
            )
        )

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            showControls = videoPlayerState::showControls,
            isError = isError,
            isBuffering = isBuffering,
            isSubtitleDrawerVisible = showSubtitleDrawer,
            onRetry = {
                exoPlayer.stop()
                exoPlayer.prepare()
                exoPlayer.play()
            },
            controls = {
                VideoPlayerControls(
                    player = exoPlayer,
                    title = title,
                    focusRequester = focusRequester,
                    onShowSubtitles = {
                        showSubtitleDrawer = true
                        exoPlayer.pause()
                    }
                )
            }
        )

        SubtitleDrawer(
            visible = showSubtitleDrawer,
            subtitles = subtitles,
            onDismiss = {
                showSubtitleDrawer = false
                exoPlayer.play()
            },
            onSubtitleSelected = { selected ->

                subtitles = subtitles.map {
                    it.copy(isSelected = it.id == selected.id)
                }
                showSubtitleDrawer = false
            }
        )
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer, videoPlayerState: VideoPlayerState, pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.isControlsVisible) {
            exoPlayer.seekBack()
            pulseState.setType(VideoPlayerPulse.Type.BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.isControlsVisible) {
            exoPlayer.seekForward()
            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        exoPlayer.pause()
        videoPlayerState.showControls()
    })