package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.presentation.common.Error
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.IPTVChannel
import com.pypisan.sanchitra.data.models.Stream

object VideoPlayerScreen {
    const val IPTVStreamIdBundleKey = "iptvStreamId"
}

@Composable
fun VideoPlayerScreen(
    onBackPressed: () -> Unit,
    videoPlayerScreenViewModel: VideoPlayerScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity

    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val sharedVM: PlayerSharedViewModel = hiltViewModel(LocalContext.current as ComponentActivity)

    val channel by sharedVM.selectedChannel.collectAsStateWithLifecycle()
    val stream by sharedVM.selectedStream.collectAsStateWithLifecycle()

//    Log.d("VIDEO_PLAYER_DEBUG", "Channel: $channel, Stream: $stream")

    when {
        channel == null || stream == null -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        else -> {
            VideoPlayerBuild(
                iptvChannel = channel!!,
                streams = channel!!.streams,
                stream = stream!!,
                onBackPressed = onBackPressed
            )
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerBuild(
    iptvChannel: IPTVChannel,
    streams: List<Stream>,
    stream: Stream,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val isError = rememberSaveable { mutableStateOf(false) }
    var isBuffering by rememberSaveable { mutableStateOf(false) }

    var subtitles by remember {
        mutableStateOf<List<SubtitleTrack>>(emptyList())
    }

    var audios by remember {
        mutableStateOf<List<AudioTrack>>(emptyList())
    }

    var qualities by remember {
        mutableStateOf<List<VideoQuality>>(emptyList())
    }

    var currentIndex by remember {
        mutableStateOf(streams.indexOfFirst { it.id == stream.id }.coerceAtLeast(0))
    }
    val currentStream = streams.getOrNull(currentIndex)

    val streamUrl = currentStream?.url ?: ""

    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = buildDefaultExoPlayer(
        context,
        streamUrl,
        { isError.value = true },
        { state ->
            isBuffering = state == Player.STATE_BUFFERING
        },
        onSubtitlesChanged = {
            subtitles = listOf(
                SubtitleTrack(
                    label = "Off",
                    language = "off",
                    group = null,
                    trackIndex = -1,
                    isSelected = false,
                )
            ) + it
        },

        onAudiosChanged = {
            audios = it
        },

        onQualitiesChanged = {
            qualities = it
        },
        renderersFactory = renderersFactory
    )
    PlayerScreenContent(
        title = iptvChannel.name,
        "",
        "",
        exoPlayer = exoPlayer,
        subtitles = subtitles,
        audios = audios,
        qualities = qualities,
        onBackPressed = onBackPressed,
        isBuffering = isBuffering,
        isError = isError.value,
        onSubtitlesChanged = {
            subtitles = it
        }
    )
}