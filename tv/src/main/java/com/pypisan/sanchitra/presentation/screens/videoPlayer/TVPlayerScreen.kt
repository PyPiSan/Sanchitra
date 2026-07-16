package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.compose.runtime.saveable.rememberSaveable
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.EPGItem
import com.pypisan.sanchitra.data.models.EPGResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TVPlayerScreen {
    const val TVIdBundleKey = "channelId"
}

@RequiresApi(Build.VERSION_CODES.O)
private val epgTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@RequiresApi(Build.VERSION_CODES.O)
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
    val epg by tvPlayerScreenViewModel.epgState.collectAsStateWithLifecycle()

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
                epg = epg,
                onBackPressed = onBackPressed,
                onVideoStarted = {
                    tvPlayerScreenViewModel.updateViewCount(s.channel.id)
                }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun TVPlayerBuild(
    channel: Channel,
    epg: EPGResponse,
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

    val currentProgram = epg.getCurrentProgram()
    val nextProgram = epg.getNextProgram()

    val renderersFactory = DefaultRenderersFactory(context)
        .setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = rememberExoPlayer(
        context = context,
        channel = channel,
        onError = { exception ->
            errorMessage = exception.message ?: "Playback Error"
            isError = true
        },
        onBuffering = { state ->
            isBuffering = state == Player.STATE_BUFFERING
        },
        onSubtitlesChanged = { newTracks ->

            val hasSelectedTrack = newTracks.any { it.isSelected }

            subtitles = listOf(
                SubtitleTrack(
                    label = "Off", language = "off", group = null, trackIndex = -1,

                    // OFF only selected when nothing selected
                    isSelected = !hasSelectedTrack
                )
            ) + newTracks
        },

        onAudiosChanged = {
            audios = it
        },

        onQualitiesChanged = { list ->
            qualities = list.filter { it.height >= 720 }.sortedByDescending { it.height }
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
        title = channel.name,
        currentProgram?.name ?: "",
        nextProgram?.name ?: "",
        epg,
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
fun rememberExoPlayer(
    context: Context,
    channel: Channel,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {
    return remember(channel.id) {
        if (!channel.isDrm) {
            buildDefaultExoPlayer(
                context,
                channel.streamUrl,
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
                channel.name,
                channel.streamUrl,
                channel.licenseKey,
                channel.licenseUrl,
                onError,
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun EPGResponse.getCurrentProgram(): EPGItem? {
    val now = LocalDateTime.now()
    return epg.firstOrNull { item ->
        try {

            val start = LocalDateTime.parse(item.start, epgTimeFormatter)
            val end = LocalDateTime.parse(item.end, epgTimeFormatter)

            !now.isBefore(start) && !now.isAfter(end)
        } catch (e: Exception) {
            false
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun EPGResponse.getNextProgram(): EPGItem? {
    val now = LocalDateTime.now()
    val current = getCurrentProgram()

    return if (current != null) {
        val currentIndex = epg.indexOf(current)
        if (currentIndex != -1 && currentIndex < epg.size - 1) {
            epg[currentIndex + 1]
        } else {
            null
        }
    } else {
        epg.firstOrNull { item ->
            try {
                val start = LocalDateTime.parse(item.start, epgTimeFormatter)
                start.isAfter(now)
            } catch (e: Exception) {
                false
            }
        }
    }
}
