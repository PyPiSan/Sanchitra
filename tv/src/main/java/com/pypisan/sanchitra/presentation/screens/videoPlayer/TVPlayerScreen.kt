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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.pypisan.sanchitra.data.entities.Channel
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.common.Loading
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.tv.material3.MaterialTheme
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.util.findActivity


@kotlin.OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TVPlayerScreen(
    channelId: String,
    onBackPressed: () -> Unit,
    tvPlayerScreenViewModel: TVPlayerScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(channelId) {
        tvPlayerScreenViewModel.loadChannel(channelId)
    }

    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            tvPlayerScreenViewModel.reset()
        }
    }

    val uiState by tvPlayerScreenViewModel.uiState.collectAsStateWithLifecycle()
    val epg by tvPlayerScreenViewModel.epgState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
//    var hasRequestedFocus by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .focusRequester(focusRequester)
            .focusProperties { onExit = { FocusRequester.Cancel } }
            .focusGroup()) {
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
                    })
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun TVPlayerBuild(
    channel: Channel, epg: EPGResponse, onBackPressed: () -> Unit, onVideoStarted: () -> Unit
) {
    val context = LocalContext.current
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

    val audioSink = DefaultAudioSink.Builder(context).setEnableFloatOutput(true).build()

//    val renderersFactory = DefaultRenderersFactory(context)
//        .setEnableDecoderFallback(true)
//        .forceEnableMediaCodecAsynchronousQueueing()
//        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)

    // Custom RenderersFactory using our optimized audioSink
    val renderersFactory = object : DefaultRenderersFactory(context) {
        init {
            setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
            setEnableDecoderFallback(true)
            forceEnableMediaCodecAsynchronousQueueing()
        }

        override fun buildAudioSink(
            context: Context, enableFloatOutput: Boolean, enableAudioTrackPlaybackParams: Boolean
        ): AudioSink {
            return audioSink
        }
    }

    val exoPlayer = rememberExoPlayer(
        context = context, channel = channel, onBuffering = { state ->
            isBuffering = state == Player.STATE_BUFFERING
        }, onSubtitlesChanged = { newTracks ->

            val hasSelectedTrack = newTracks.any { it.isSelected }
            subtitles = listOf(
                SubtitleTrack(
                    label = "Off",
                    language = "off",
                    group = null,
                    trackIndex = -1,
                    isSelected = !hasSelectedTrack
                )
            ) + newTracks
        },

        onAudiosChanged = {
            audios = it
        },

        onQualitiesChanged = { list ->
            qualities = list.filter { it.height >= 720 }.sortedByDescending { it.height }
        }, renderersFactory = renderersFactory
    )

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            var hasCountedView = false
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
            exoPlayer.release()
        }
    }

    PlayerScreenContent(
        title = channel.name,
        epg,
        exoPlayer = exoPlayer,
        subtitles = subtitles,
        audios = audios,
        qualities = qualities,
        onBackPressed = onBackPressed,
        isBuffering = isBuffering,
        onSubtitlesChanged = {
            subtitles = it
        })
}

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayer(
    context: Context,
    channel: Channel,
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
                subtitleUrl = null,
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
                onBuffering,
                onSubtitlesChanged,
                onAudiosChanged,
                onQualitiesChanged,
            )
        }
    }
}

