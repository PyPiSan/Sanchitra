package com.example.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.ExoMediaDrm
import androidx.media3.exoplayer.drm.MediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import com.example.sanchitra.data.entities.MovieDetails
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.presentation.common.Error
import com.example.sanchitra.presentation.common.Loading
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControls
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControlsVLC
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.example.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.example.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.example.sanchitra.utils.handleDPadKeyEvents
import java.util.UUID

@Composable
fun TVPlayerScreen(
    onBackPressed: () -> Unit, tvPlayerScreenViewModel: TVPlayerScreenViewModel = hiltViewModel()
) {
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

@OptIn(UnstableApi::class)
@Composable
fun TVPlayerScreenContent(
    channel: Channel, onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)
    val pulseState = rememberVideoPlayerPulseState()

    val exoPlayer = rememberExoPlayer(context, channel)

    BackHandler(onBack = onBackPressed)

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

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            showControls = videoPlayerState::showControls,
            controls = {
                VideoPlayerControls(
                    player = exoPlayer, channel = channel, focusRequester = focusRequester
                )
            })
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun BackHandler(onBack: () -> Unit) {
    TODO("Not yet implemented")
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
@Composable
fun rememberExoPlayer(
    context: Context, channel: Channel
): ExoPlayer {

    return remember(channel) {

        val isDrm = channel.isDrm
        Log.d("VIDEO_PLAYER_DEBUG", "isDrm: $isDrm")
        val mediaItem = if (!isDrm) {
            MediaItem.fromUri(channel.streamUrl)
        } else {

            val (kidHex, keyHex) = channel.getDrmKeys()!!

            val kid = hexToBase64(kidHex)
            val key = hexToBase64(keyHex)

            val clearKeyJson = """
            {
             "keys":[
            {
              "kty":"oct",
              "k":"$key",
              "kid":"$kid"
            }
             ],
          "type":"temporary"
         }
            """.trimIndent()

            MediaItem.Builder().setUri(channel.streamUrl).setDrmConfiguration(
                    MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID).build()
                ).setTag(clearKeyJson.toByteArray()).build()
        }

        val drmCallback = object : MediaDrmCallback {
            override fun executeProvisionRequest(
                uuid: UUID, request: ExoMediaDrm.ProvisionRequest
            ): ByteArray = ByteArray(0)

            override fun executeKeyRequest(
                uuid: UUID, request: ExoMediaDrm.KeyRequest
            ): ByteArray {
                return if (isDrm) {
                    mediaItem.localConfiguration?.tag as ByteArray
                } else {
                    request.data
                }
            }
        }

        val drmSessionManager = DefaultDrmSessionManager.Builder().build(drmCallback)

        val mediaSourceFactory =
            DefaultMediaSourceFactory(context).setDrmSessionManagerProvider { drmSessionManager }

        ExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build().apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
    }
}

fun hexToBase64(hex: String): String {
    val bytes = hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

fun Channel.getDrmKeys(): Pair<String, String>? {
    if (!isDrm || licenseKey.isNullOrEmpty()) return null
    val parts = licenseKey.split(":")
    if (parts.size != 2) return null
    return parts[0] to parts[1]
}
