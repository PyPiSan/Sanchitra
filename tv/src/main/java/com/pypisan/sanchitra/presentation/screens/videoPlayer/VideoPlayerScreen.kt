package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.view.WindowManager
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
import androidx.media3.common.PlaybackException
import com.pypisan.sanchitra.presentation.common.Error
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.Loading

object VideoPlayerScreen {
    const val metaID = "metaID"
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

    val uiState by videoPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {

        VideoPlayerScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        VideoPlayerScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is VideoPlayerScreenUiState.Done -> {
            VideoPlayerBuild(
                metaId = s.videoDetail?.id.toString(),
                title = s.videoDetail?.title,
                streamUrl = s.videoDetail?.url,
                subTitleUrl = s.videoDetail?.meta?.subtitleUrl,
                onBackPressed = onBackPressed
            )
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerBuild(
    metaId: String?= "",
    title: String?,
    streamUrl: String? = "",
    subTitleUrl: String? = "",
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

    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = rememberPlayer(
        metaId?:"",
        context,
        streamUrl?:"",
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
        title = title?:"",
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

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayer(
    metaId: String,
    context: Context,
    streamUrl: String,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {
    return remember(metaId) {
            buildDefaultExoPlayer(
                context,
                streamUrl,
                onError,
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
                renderersFactory
            )
    }
}