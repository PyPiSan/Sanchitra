package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.presentation.common.Error
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.material3.MaterialTheme
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.util.findActivity
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.storage.WatchProgressManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoPlayerScreen(
    metaId: String,
    isContinue: Boolean,
    onBackPressed: () -> Unit,
    videoPlayerScreenViewModel: VideoPlayerScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(metaId, isContinue) {
        videoPlayerScreenViewModel.loadVideo(metaId, isContinue)
    }

    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            videoPlayerScreenViewModel.reset()
        }
    }

    val uiState by videoPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .focusRequester(focusRequester)
            .focusProperties { onExit = { FocusRequester.Cancel } }
            .focusGroup()
    ) {
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
                    isContinue = s.isContinue,
                    onBackPressed = onBackPressed,
                    onVideoStarted = {
                        videoPlayerScreenViewModel.updateViewCount(s.videoDetail?.id)
                    })
            }
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
    subTitleUrl: String?,
    isContinue: Boolean = false,
    onBackPressed: () -> Unit,
    onVideoStarted: () -> Unit
) {
    val context = LocalContext.current

    var isBuffering by rememberSaveable { mutableStateOf(false) }

    var subtitles by remember { mutableStateOf<List<SubtitleTrack>>(emptyList()) }
    var audios by remember { mutableStateOf<List<AudioTrack>>(emptyList()) }
    var qualities by remember { mutableStateOf<List<VideoQuality>>(emptyList()) }

    // Remember WatchProgressManager safely
    val manager = remember(context) { WatchProgressManager(context.applicationContext) }

    val renderersFactory = remember(context) {
        DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .forceEnableMediaCodecAsynchronousQueueing()
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    }

    val exoPlayer = rememberPlayer(
        metaId ?: "",
        title ?: "",
        context,
        streamUrl ?: "",
        subTitleUrl,
        drm ?: false,
        licenseKey,
        licenseUrl,
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
        onAudiosChanged = { audios = it },
        onQualitiesChanged = { list ->
            qualities = list.filter { it.width >= 1280 }.sortedByDescending { it.height }
        },
        renderersFactory = renderersFactory
    )

    // RESUME PLAYBACK & PERIODIC SAVE LOOP
    LaunchedEffect(exoPlayer, metaId, isContinue) {
        if (metaId.isNullOrBlank()) return@LaunchedEffect

        if (isContinue) {
            val savedProgress = manager.getProgress(metaId).firstOrNull()
            if (savedProgress != null && savedProgress.timeMillis > 0) {
                exoPlayer.seekTo(savedProgress.timeMillis)
            }
        } else {
            manager.clearProgress(metaId)
            exoPlayer.seekTo(0L)
        }

        // Periodically save progress every 5 seconds while video is playing
        while (isActive) {
            if (exoPlayer.isPlaying && exoPlayer.duration > 0) {
                manager.saveProgress(
                    id = metaId,
                    timeMillis = exoPlayer.currentPosition,
                    durationMillis = exoPlayer.duration
                )
            }
            delay(5000L) // Wait 5 seconds before saving again
        }
    }

    // EVENT LISTENERS & SAVE ON EXIT
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
            // Save final progress right when leaving the screen
            if (!metaId.isNullOrBlank() && exoPlayer.duration > 0) {
                val finalPosition = exoPlayer.currentPosition
                val totalDuration = exoPlayer.duration

                // Launch on background scope so it completes after screen closes
                CoroutineScope(Dispatchers.IO).launch {
                    manager.saveProgress(
                        id = metaId,
                        timeMillis = finalPosition,
                        durationMillis = totalDuration
                    )
                }
            }
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    PlayerScreenContent(
        title = title ?: "",
        epgResponse = null,
        isMovie = true,
        exoPlayer = exoPlayer,
        subtitles = subtitles,
        audios = audios,
        qualities = qualities,
        onBackPressed = onBackPressed,
        isBuffering = isBuffering,
        onSubtitlesChanged = { subtitles = it }
    )
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayer(
    metaId: String,
    title: String,
    context: Context,
    streamUrl: String,
    subTitleUrl: String?,
    drm: Boolean,
    licenseKey: String? = "",
    licenseUrl: String? = "",
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {
    return remember(metaId) {
        if (!drm) {
            buildDefaultExoPlayer(
                context,
                streamUrl,
                "movie",
                subTitleUrl,
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
                "",
                false,
                null,
                streamUrl,
                licenseKey,
                licenseUrl,
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
            )
        }
    }
}