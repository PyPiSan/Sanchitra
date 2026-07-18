package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.material3.MaterialTheme
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.models.IPTVChannelDetail
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IPTVPlayerScreen(
    channelId: String,
    onBackPressed: () -> Unit,
    iptvPlayerScreenViewModel: IPTVPlayerScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity

    // 1. Tell ViewModel to load data based on the ID passed from Overlay
    LaunchedEffect(channelId) {
        iptvPlayerScreenViewModel.loadChannel(channelId)
    }

    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            // 2. Wipe memory so the next video starts clean
            iptvPlayerScreenViewModel.reset()
        }
    }

    val uiState by iptvPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()
    val epg by iptvPlayerScreenViewModel.epgState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    // 3. Request focus with a dynamic delay
    // FIXED: Corrected type check to use IPTVPlayerScreenUiState.Done
    LaunchedEffect(uiState) {
        val delayTime = if (uiState is IPTVPlayerScreenUiState.Done) 250L else 50L
        kotlinx.coroutines.delay(delayTime)
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignore silently
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .focusRequester(focusRequester)
            .focusProperties { onExit = { FocusRequester.Cancel } } // Traps D-Pad
            .focusGroup()
            // FIXED: Corrected type check to use IPTVPlayerScreenUiState.Done
            .focusable(uiState !is IPTVPlayerScreenUiState.Done)
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        when (val s = uiState) {
            is IPTVPlayerScreenUiState.Loading -> {
                // 4. Wrap loaders in focusable Boxes to hold focus while buffering
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable()
                ) {
                    Loading(modifier = Modifier.fillMaxSize())
                }
            }

            is IPTVPlayerScreenUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable()
                ) {
                    Error(modifier = Modifier.fillMaxSize())
                }
            }

            is IPTVPlayerScreenUiState.Done -> {
                IPTVPlayerBuild(
                    iptvChannel = s.iptvChannel,
                    epg = epg,
                    onBackPressed = onBackPressed
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun IPTVPlayerBuild(
    iptvChannel: IPTVChannelDetail,
    epg: EPGResponse,
    onBackPressed: () -> Unit
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

    val renderersFactory = DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        .forceEnableMediaCodecAsynchronousQueueing()
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    val exoPlayer = rememberExoPlayer(
        context = context,
        iptvChannel = iptvChannel,
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
            var hasCountedView = false

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying && !hasCountedView) {
                    hasCountedView = true
//                    onVideoStarted()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    PlayerScreenContent(
        title = iptvChannel.name,
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
    iptvChannel: IPTVChannelDetail,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {
    return remember(iptvChannel.id) {
//        if (!iptvChannel.isDrm) {
            buildDefaultExoPlayer(
                context,
                iptvChannel.streamUrl,
                onError,
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
                renderersFactory
            )
//        }
        //        else {
//            buildDrmExoPlayer(
//                context,
//                channel,
//                onError,
//                onBuffering,
//                onSubtitlesChanged,
//                onAudiosChanged,
//                onQualitiesChanged
//            )
//        }
    }
}