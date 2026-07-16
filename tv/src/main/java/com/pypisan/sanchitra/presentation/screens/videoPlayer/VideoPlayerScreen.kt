package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import com.pypisan.sanchitra.presentation.common.Loading

object VideoPlayerScreen {
    const val metaID = "metaID"
}

@RequiresApi(Build.VERSION_CODES.O)
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
                drm = s.videoDetail?.drm,
                licenseKey = s.videoDetail?.licenseKey,
                licenseUrl = s.videoDetail?.licenseUrl,
                subTitleUrl = s.videoDetail?.meta?.subtitleUrl,
                onBackPressed = onBackPressed,
                onVideoStarted = {
                    videoPlayerScreenViewModel.updateViewCount(s.videoDetail?.id)
                })
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerBuild(
    metaId: String? = "",
    title: String?,
    streamUrl: String? = "",
    drm: Boolean? = false,
    licenseKey: String? = "",
    licenseUrl: String? = "",
    subTitleUrl: String? = "",
    onBackPressed: () -> Unit,
    onVideoStarted: () -> Unit
) {
    val context = LocalContext.current
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("Something went wrong") }

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


    val renderersFactory = DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = rememberPlayer(
        metaId ?: "",
        title ?: "",
        context,
        streamUrl ?: "",
        drm ?: false,
        licenseKey,
        licenseUrl,
        onError = { exception ->
            errorMessage = exception.message ?: "Playback Error"
            isError = true
        },
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

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            var hasCountedView = false // Ensures we only hit the API once per video

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying && !hasCountedView) {
                    hasCountedView = true
                    onVideoStarted()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }


    PlayerScreenContent(
        title = title ?: "",
        "",
        "",
        epgResponse = null,
        exoPlayer = exoPlayer,
        subtitles = subtitles,
        audios = audios,
        qualities = qualities,
        onBackPressed = onBackPressed,
        isBuffering = isBuffering,
        isErrorState = isError,
        errorMessage = errorMessage,
        onClearError = {
            isError = false
        },
        onSubtitlesChanged = {
            subtitles = it
        })
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayer(
    metaId: String,
    title: String,
    context: Context,
    streamUrl: String,
    drm: Boolean,
    licenseKey: String? = "",
    licenseUrl: String? = "",
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {
    return remember(metaId) {
        if (drm) {
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
        } else {
            buildDrmExoPlayer(
                context,
                title,
                streamUrl,
                licenseKey,
                licenseUrl,
                onError,
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
            )
        }
    }
}