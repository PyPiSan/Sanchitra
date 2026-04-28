package com.pypisan.sanchitra.presentation.screens.videoPlayer
import android.app.Activity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControlsVLC
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.pypisan.sanchitra.utils.handleDPadKeyEvents
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import androidx.core.net.toUri
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
//            Log.d("VIDEO_PLAYER_DEBUG", "Channel or stream is null")
            Error(modifier = Modifier.fillMaxSize())
        }

        else -> {
            VideoPlayerScreenContent(
                iptvChannel = channel!!,
                streams = channel!!.streams,
                stream = stream!!,
                onBackPressed = onBackPressed
            )
        }
    }
}

@Composable
fun VideoPlayerScreenContent(
    iptvChannel : IPTVChannel,
    streams : List<Stream>,
    stream: Stream,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    var currentIndex by remember {
        mutableStateOf(streams.indexOfFirst { it.id == stream.id }.coerceAtLeast(0))
    }
    val currentStream = streams.getOrNull(currentIndex)

    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)
    val pulseState = rememberVideoPlayerPulseState()
    var showAudioDialog by remember { mutableStateOf(false) }

    // VLC INIT
    val libVLC = remember {
        LibVLC(context, arrayListOf(
            "--network-caching=2500",        // 🔥 more buffer
            "--live-caching=2500",
            "--file-caching=2500",
            "--vout=android-display",
            "--drop-late-frames",           // ✅ allow smooth playback
            "--skip-frames",
            "--codec=mediacodec,all",       // 🔥 force HW decode
            "--avcodec-fast",               // faster decoding
            "--avcodec-skiploopfilter=4",   // reduce CPU load
            "--audio-resampler=soxr",       // better audio sync
            "--aout=opensles"
        ))
    }

    val mediaPlayer = remember { MediaPlayer(libVLC) }
    val videoLayout = remember { VLCVideoLayout(context) }

    // 🎯 PLAY STREAM
    LaunchedEffect(currentStream) {

        mediaPlayer.stop()
        mediaPlayer.detachViews()

        mediaPlayer.attachViews(videoLayout, null, false, false)

        val media = Media(
            libVLC,
            currentStream?.url?.toUri()
        )

        media.setHWDecoderEnabled(true, false)

        media.addOption(":network-caching=2500")
        media.addOption(":live-caching=2500")
        media.addOption(":file-caching=2500")
        media.addOption(":codec=mediacodec,all")
        media.addOption(":avcodec-fast")
        media.addOption(":avcodec-skiploopfilter=4")
        media.addOption(":no-spu")

        mediaPlayer.media = media
        media.release()

        mediaPlayer.play()
    }

    // CLEANUP
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.detachViews()
            mediaPlayer.release()
            libVLC.release()
        }
    }

    BackHandler(onBack = onBackPressed)

    Box(
        Modifier
            .dPadEventsVLC(mediaPlayer, videoPlayerState, pulseState)
            .focusable()
    ) {

        // VIDEO VIEW
        AndroidView(
            factory = { videoLayout },
            modifier = Modifier.fillMaxSize()
        )

        val focusRequester = remember { FocusRequester() }

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = mediaPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            showControls = videoPlayerState::showControls,
            controls = {
                VideoPlayerControlsVLC(
                    isPlaying = mediaPlayer.isPlaying,
                    iptvChannelDetail = iptvChannel,
                    isLive = true,
                    currentTime = mediaPlayer.time,
                    duration = mediaPlayer.length,
                    onPlayPause = {
                        if (mediaPlayer.isPlaying) mediaPlayer.pause()
                        else mediaPlayer.play()
                    },
                    onSeekForward = {
                        mediaPlayer.time += 10_000
                    },
                    focusRequester = focusRequester,
                    onSeekBack = {
                        mediaPlayer.time -= 10_000
                    }

                )
            }
        )
    }
}

// REMOTE CONTROL SUPPORT
private fun Modifier.dPadEventsVLC(
    player: MediaPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.isControlsVisible) {
            player.time -= 10_000
            pulseState.setType(VideoPlayerPulse.Type.BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.isControlsVisible) {
            player.time += 10_000
            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        if (player.isPlaying) player.pause() else player.play()
        videoPlayerState.showControls()
    }
)

fun getVlcAudioTracks(player: MediaPlayer): List<Pair<Int, String>> {
    return player.audioTracks?.map {
        Pair(it.id, it.name ?: "Track ${it.id}")
    } ?: emptyList()
}

fun setAudioTrack(player: MediaPlayer, trackId: Int) {
    player.audioTrack = trackId
}