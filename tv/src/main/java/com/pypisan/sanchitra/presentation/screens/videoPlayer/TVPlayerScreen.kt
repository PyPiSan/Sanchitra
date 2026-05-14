package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.pypisan.sanchitra.data.models.AudioTrackInfo
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
            TVPlayerBuild(
                channel = s.channel,
                onBackPressed = onBackPressed
            )
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun TVPlayerBuild(
    channel: Channel,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val isError = rememberSaveable { mutableStateOf(false) }
    var isBuffering by rememberSaveable { mutableStateOf(false) }

    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = rememberExoPlayer(
        context = context,
        channel = channel,
        onError = { isError.value = true },
        onBuffering = { state ->
            isBuffering = state == Player.STATE_BUFFERING
        },
        renderersFactory = renderersFactory
    )

    PlayerScreenContent(
        title = channel.name,
        exoPlayer = exoPlayer,
        onBackPressed = onBackPressed,
        isBuffering = isBuffering,
        isError = isError.value
    )
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
                channel.streamUrl,
                onError,
                onBuffering,
                renderersFactory
            )
        } else {
            buildDrmExoPlayer(context, channel)
        }
    }
}


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
