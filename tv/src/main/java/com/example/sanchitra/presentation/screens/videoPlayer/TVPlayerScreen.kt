package com.example.sanchitra.presentation.screens.videoPlayer

import android.R
import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.presentation.common.Error
import com.example.sanchitra.presentation.common.Loading
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControls
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.example.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.example.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.example.sanchitra.utils.handleDPadKeyEvents
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import com.example.sanchitra.data.models.AudioTrackInfo
import com.example.sanchitra.presentation.screens.videoPlayer.components.AudioSettings

object TVPlayerScreen {
    const val TVIdBundleKey = "channelId"
}

@Composable
fun TVPlayerScreen(
    onBackPressed: () -> Unit, tvPlayerScreenViewModel: TVPlayerScreenViewModel = hiltViewModel()
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
            TVPlayerScreenContent(
                channel = s.channel, onBackPressed = onBackPressed
            )
        }
    }
}

@OptIn(UnstableApi::class
)
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
    context: Context,
    channel: Channel,
): ExoPlayer {


    return remember(channel) {

        if (!channel.isDrm) {

            ExoPlayer.Builder(context)
                .build()
                .apply {
                    // Set track preferences BEFORE prepare()
                    trackSelectionParameters = trackSelectionParameters
                        .buildUpon()
                        .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                        .setForceHighestSupportedBitrate(true)
                        .setPreferredAudioLanguage("en")
                        .build()
                    addListener(object : Player.Listener {

                        override fun onPlayerError(error: PlaybackException) {
//                            hasError = true
                            Log.e("PLAYER_ERROR", "Error: ${error.message}", error)
                        }

                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_READY) {
//                                hasError = false
                            }
                        }

                        override fun onTracksChanged(tracks: Tracks) {

                            tracks.groups.forEach { group ->

                                if (group.type == C.TRACK_TYPE_VIDEO) {
                                    for (i in 0 until group.length) {
                                        val format = group.getTrackFormat(i)
                                        Log.d("VIDEO_INFO",
                                            "Res=${format.width}x${format.height}, bitrate=${format.bitrate}"
                                        )
                                    }
                                }

                                if (group.type == C.TRACK_TYPE_AUDIO) {
                                    for (i in 0 until group.length) {
                                        val format = group.getTrackFormat(i)
                                        Log.d("AUDIO_INFO",
                                            """
                                Codec=${format.codecs}
                                Bitrate=${format.bitrate}
                                Lang=${format.language}
                                """.trimIndent()
                                        )
                                    }
                                }
                            }
                        }
                    })

                    setMediaItem(MediaItem.fromUri(channel.streamUrl))
                    prepare()
                    playWhenReady = true
                }

        } else {

            val (keyHex, kidHex) = channel.getDrmKeys()!!
//            Log.d("VIDEO_DEBUG", "kidHex: $kidHex, keyHex: $keyHex")

            val drmKeyBytes = kidHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val encodedDrmKey = Base64.encodeToString(
                drmKeyBytes,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )

            val drmKeyIdBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val encodedDrmKeyId = Base64.encodeToString(
                drmKeyIdBytes,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )

            val drmBody =
                "{\"keys\":[{\"kty\":\"oct\",\"k\":\"${encodedDrmKey}\",\"kid\":\"${encodedDrmKeyId}\"}],\"type\":\"temporary\"}"

            val dashMediaItem = MediaItem.Builder()
                .setUri(channel.streamUrl)
                .setMimeType(MimeTypes.APPLICATION_MPD)
                .setMediaMetadata(MediaMetadata.Builder().setTitle("test").build())
                .build()

            val trackSelector = DefaultTrackSelector(context)
            val loadControl = DefaultLoadControl()

            val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())

            val drmSessionManager = DefaultDrmSessionManager.Builder()
                .setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false)
                .setKeyRequestParameters(HashMap())
                .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .build(drmCallback)

            val customDrmSessionManager: DrmSessionManager = drmSessionManager

            val mediaSourceFactory = DefaultMediaSourceFactory(context)
                .setDrmSessionManagerProvider { customDrmSessionManager }
                .createMediaSource(dashMediaItem)

            ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .setSeekForwardIncrementMs(10_000L)
                .setSeekBackIncrementMs(10_000L)
                .build().apply {
                    trackSelectionParameters = trackSelectionParameters
                        .buildUpon()
                        .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                        .setForceHighestSupportedBitrate(true)
                        .setPreferredAudioLanguage("en")
                        .build()
                    addListener(object : Player.Listener {
                        override fun onTracksChanged(tracks: Tracks) {

                            tracks.groups.forEach { group ->

                                if (group.type == C.TRACK_TYPE_AUDIO) {

                                    val format = group.getTrackFormat(0)

                                    Log.d(
                                        "AUDIO_INFO",
                                        """
                Codec=${format.codecs}
                Bitrate=${format.bitrate}
                Lang=${format.language}
                Label=${format.label}
                Mime=${format.sampleMimeType}
                """.trimIndent()
                                    )
                                }
                            }
                        }
                    })
                    setMediaSource(mediaSourceFactory, true)
                    prepare()
                    playWhenReady = true
                }
        }
    }
}

fun Channel.getDrmKeys(): Pair<String, String>? {
    if (!isDrm || licenseKey.isNullOrEmpty()) return null
    val parts = licenseKey.split(":")
    if (parts.size != 2) return null
    return parts[0] to parts[1]
}

fun getLanguageName(code: String?): String {
    return when (code) {
        "en" -> "English"
        "hi" -> "Hindi"
        "ta" -> "Tamil"
        "te" -> "Telugu"
        "kn" -> "Kannada"
        "ml" -> "Malayalam"
        "mr" -> "Marathi"
        else -> code ?: "Unknown"
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
