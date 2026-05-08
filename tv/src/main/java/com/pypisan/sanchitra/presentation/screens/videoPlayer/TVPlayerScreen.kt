package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControls
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.pypisan.sanchitra.utils.handleDPadKeyEvents
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.pypisan.sanchitra.data.models.AudioTrackInfo
import android.util.Log
import androidx.compose.runtime.saveable.rememberSaveable

object TVPlayerScreen {
    const val TVIdBundleKey = "channelId"
}

@Composable
fun TVPlayerScreen(
    onBackPressed: () -> Unit,
    tvPlayerScreenViewModel: TVPlayerScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity

    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val uiState by tvPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is TVPlayerScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is TVPlayerScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is TVPlayerScreenUiState.Done -> {
            TVPlayerScreenContent(
                channel = s.channel, onBackPressed = onBackPressed
            )
        }
    }
}

@OptIn(
    UnstableApi::class
)
@Composable
fun TVPlayerScreenContent(
    channel: Channel,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    val videoPlayerState = rememberVideoPlayerState()
    val pulseState = rememberVideoPlayerPulseState()
    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    val isError = rememberSaveable { mutableStateOf(false) }
    var isBuffering by rememberSaveable { mutableStateOf(false) }

    val exoPlayer = rememberExoPlayer(
        context = context,
        channel = channel,
        onError = { isError.value = true },
        onBuffering = { state ->
            isBuffering = state == Player.STATE_BUFFERING
        },
        renderersFactory = renderersFactory
    )

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

        val focusRequester = remember { FocusRequester() }
        var subtitlesEnabled by remember { mutableStateOf(true) }

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            showControls = videoPlayerState::showControls,
            isError = isError.value,
            isBuffering = isBuffering,
            onRetry = {
                exoPlayer.stop()
                exoPlayer.prepare()
                exoPlayer.play()
            },
            controls = {
                VideoPlayerControls(
                    player = exoPlayer, channel = channel, focusRequester = focusRequester,
                    onShowSubtitles = {
                        subtitlesEnabled = !subtitlesEnabled
                        toggleSubtitles(player = exoPlayer, subtitlesEnabled)
                    }
                )
            })
    }
}


@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayer(
    context: Context,
    channel: Channel,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {

    return remember(channel.id) {
        if (!channel.isDrm) {
            buildDefaultExoPlayer(
                context,
                channel,
                onError,
                onBuffering,
                renderersFactory
            )
        } else {
            buildDrmExoPlayer(context, channel)
        }
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


@OptIn(UnstableApi::class)
fun getAudioTracks(player: Player): List<AudioTrackInfo> {

    val list = mutableListOf<AudioTrackInfo>()

    player.currentTracks.groups.forEach { group ->

        if (group.type == C.TRACK_TYPE_AUDIO) {

            val format = group.getTrackFormat(0)

            list.add(
                AudioTrackInfo(
                    language = format.language ?: "und",
                    bitrate = format.bitrate,
                    format = format
                )
            )
        }
    }

    return list
}

@OptIn(UnstableApi::class)
fun selectAudio(player: Player, language: String) {

    val trackSelector = (player as ExoPlayer).trackSelector as DefaultTrackSelector

    val params = trackSelector.parameters
        .buildUpon()
        .setPreferredAudioLanguage(language)
        .setForceHighestSupportedBitrate(true)
        .build()

    trackSelector.parameters = params
}

fun toggleSubtitles(player: ExoPlayer, enable: Boolean) {
    val params = player.trackSelectionParameters
        .buildUpon()
        .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !enable)
        .build()

    player.trackSelectionParameters = params
}
