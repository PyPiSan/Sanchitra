package com.pypisan.sanchitra.presentation.screens.videoPlayer

//Main Player Screen--Common for all

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DrmSession
import androidx.media3.exoplayer.drm.MediaDrmCallbackException
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.SubtitleDrawer
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControls
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerPulseState
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.rememberVideoPlayerState
import com.pypisan.sanchitra.utils.handleDPadKeyEvents
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.util.prepareEPGProgramData
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.AudioTrackDrawer
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.NowAiringDialog
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.SeekController
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.SubtitleOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoQualityDrawer
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenContent(
    title: String,
    epgResponse: EPGResponse?,
    isMovie: Boolean = false,
    exoPlayer: ExoPlayer,
    subtitles: List<SubtitleTrack>,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    audios: List<AudioTrack>,
    qualities: List<VideoQuality>,
    onBackPressed: () -> Unit,
    isBuffering: Boolean
) {
    val videoPlayerState = rememberVideoPlayerState()
    val pulseState = rememberVideoPlayerPulseState()
    val scope = rememberCoroutineScope()

    val fallbackFocusRequester = remember { FocusRequester() }

    val seekController = remember(exoPlayer) {
        SeekController(exoPlayer, scope)
    }
    var showQualityDrawer by rememberSaveable { mutableStateOf(false) }
    var showNowAiring by rememberSaveable { mutableStateOf(false) }
    var showAudioQualityDrawer by rememberSaveable { mutableStateOf(false) }
    var showSubtitleDrawer by rememberSaveable { mutableStateOf(false) }

    var subtitleText by remember { mutableStateOf<String?>(null) }
    var subtitleBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var aspectRatio by remember { mutableFloatStateOf(16f / 9f) }
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    var fatalError by rememberSaveable { mutableStateOf(false) }
    var isErrored by rememberSaveable { mutableStateOf(false) }
    var errorValue by rememberSaveable { mutableStateOf("Something Went Wrong") }
    var hasSuccessfullyPlayedOnce by remember { mutableStateOf(false) }
    val maxRetries = 3

//    val doubleClickHandler = remember {
//        DoubleClickHandler()
//    }

    // to capture focus
    LaunchedEffect(videoPlayerState.isControlsVisible) {
        if (!videoPlayerState.isControlsVisible) {
            var isRescued = false
            while (!isRescued) {
                try {
                    fallbackFocusRequester.requestFocus()
                    isRescued = true
                } catch (e: Exception) {
                    delay(10) // Try again in 10ms if it fails
                }
            }
        }
    }

    LaunchedEffect(exoPlayer.isPlaying) {
        if (exoPlayer.isPlaying) {
            videoPlayerState.showControls(true)
        } else {
            videoPlayerState.showControls(false)
        }
    }

    // A ticker that updates EPG every minute
    var ticker by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000) // Wait 60 seconds
            ticker++      // Trigger a recalculation
        }
    }

    val (epgPrograms, initialAiringIndex) = remember(
        epgResponse, showNowAiring, ticker
    ) {
        if (epgResponse != null) {
            prepareEPGProgramData(epgResponse)
        } else {
            Pair(emptyList(), 0)
        }
    }

    //    Player Listener for Error and Buffer
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                hasSuccessfullyPlayedOnce = true
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {

                if (videoSize.width > 0 && videoSize.height > 0) {
                    val ratio =
                        (videoSize.width * videoSize.pixelWidthHeightRatio) / videoSize.height.toFloat()

                    if (ratio.isFinite() && ratio > 0f) {
                        aspectRatio = ratio
                    }
                }
            }

            override fun onCues(cueGroup: CueGroup) {
                //Combine all text cues into a single string separated by newlines
                subtitleText = if (cueGroup.cues.isEmpty()) {
                    null
                } else {
                    cueGroup.cues.mapNotNull { it.text }.joinToString(separator = "\n")
                }
                subtitleBitmap = cueGroup.cues.firstNotNullOfOrNull { it.bitmap }
            }

            override fun onPlayerError(error: PlaybackException) {
                val cause = error.cause
                // Set fatalError to true if the video HAS NEVER played successfully
                if (!hasSuccessfullyPlayedOnce) {
                    if (cause is DrmSession.DrmSessionException) {
                        fatalError = true
                        isErrored = true

                        val drmCause = cause.cause
                        errorValue = if (drmCause is MediaDrmCallbackException) {
                            "DRM License Server Error"
                        } else {
                            "DRM Session Exception"
                        }
                    }
                    if (cause is HttpDataSource.InvalidResponseCodeException) {
                        when (cause.responseCode) {
                            400, 401, 403, 404, 500, 503, 410 -> {
                                fatalError = true
                                isErrored = true
                                errorValue = "Broken Stream Error"
                            }
                        }
                    }
                    if (error is ExoPlaybackException) {
                        fatalError = true
                        isErrored = true
                        errorValue = "Unexpected Error"
                    }
                } else {
                    // Optionally log this event here:
                    Log.e("Player2", "Stream interrupted after playing, watchdog will retry $cause")
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    retryCount = 0
                    fatalError = false
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    RememberPlaybackWatchdog(
        exoPlayer = exoPlayer, onFreeze = {
            if (fatalError) {
                return@RememberPlaybackWatchdog false
            }

            if (retryCount >= maxRetries) {
                isErrored = true
                return@RememberPlaybackWatchdog false
            }
            retryCount++
            exoPlayer.seekToDefaultPosition()
            exoPlayer.prepare()
            exoPlayer.play()
            true
        })

    BackHandler {
        exoPlayer.release()
        onBackPressed()
    }

    Box(
        Modifier
            .dPadEvents(exoPlayer, videoPlayerState, pulseState, seekController)
            .focusRequester(fallbackFocusRequester)
            .focusGroup()
            .focusable()
            .background(Color.Black), contentAlignment = Alignment.Center
    ) {

        PlayerSurface(
            player = exoPlayer, surfaceType = SURFACE_TYPE_TEXTURE_VIEW, modifier = if (isMovie) {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
            } else {
                Modifier.fillMaxSize()
            }
        )

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {
                SubtitleOverlay(
                    subtitleText = subtitleText, subtitleBitmap = subtitleBitmap
                )
            },
            showControls = videoPlayerState::showControls,
            isError = isErrored,
            errorMessage = errorValue,
            onRetry = {
                isErrored = false
                errorValue = "Something Went Wrong"
                fatalError = false
                exoPlayer.stop()
                exoPlayer.prepare()
                exoPlayer.play()
            },
            isBuffering = isBuffering,
            isSubtitleDrawerVisible = showSubtitleDrawer || showQualityDrawer || showAudioQualityDrawer || showNowAiring,
            controls = {
                VideoPlayerControls(
                    player = exoPlayer,
                    title = title,
                    epgPrograms = epgPrograms,
                    onShowInfo = {
                        showNowAiring = true
                        exoPlayer.pause()
                    },
                    onShowAudioSettings = {
                        showAudioQualityDrawer = true
                        exoPlayer.pause()
                    },
                    onShowSubtitles = {
                        showSubtitleDrawer = true
                        exoPlayer.pause()
                    },
                    onShowQuality = {
                        showQualityDrawer = true
                        exoPlayer.pause()
                    })
            })

        SubtitleDrawer(visible = showSubtitleDrawer, subtitles = subtitles, onDismiss = {
            showSubtitleDrawer = false
            exoPlayer.play()
        }, onSubtitleSelected = { selected ->
            if (selected.trackIndex == -1) {
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true).build()
            } else {
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .setPreferredTextLanguage(selected.language)
                    .setSelectUndeterminedTextLanguage(true).build()
            }
            onSubtitlesChanged(
                subtitles.map {
                    it.copy(isSelected = it.label == selected.label)
                })
            showSubtitleDrawer = false
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
        })

        AudioTrackDrawer(visible = showAudioQualityDrawer, audioTracks = audios, onDismiss = {
            showAudioQualityDrawer = false
            exoPlayer.play()
        }, onTrackSelected = { selected ->
            if (selected.trackIndex == -1) {
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .clearOverridesOfType(C.TRACK_TYPE_AUDIO).build()
            } else {
                val group = selected.group
                exoPlayer.trackSelectionParameters = group?.mediaTrackGroup?.let {
                    exoPlayer.trackSelectionParameters.buildUpon()
                        .clearOverridesOfType(C.TRACK_TYPE_AUDIO).setOverrideForType(
                            TrackSelectionOverride(it, listOf(selected.trackIndex))
                        )
                }?.build()!!
            }
            showAudioQualityDrawer = false
            exoPlayer.playWhenReady = true
        })

        VideoQualityDrawer(visible = showQualityDrawer, qualities = qualities, onDismiss = {
            showQualityDrawer = false
            exoPlayer.play()
        }, onQualitySelected = { selected ->
            if (selected.height == -1) {
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .clearOverridesOfType(C.TRACK_TYPE_VIDEO).build()
            } else {
                val group = selected.group
                if (group != null) {
                    exoPlayer.trackSelectionParameters =
                        exoPlayer.trackSelectionParameters.buildUpon()
                            .clearOverridesOfType(C.TRACK_TYPE_VIDEO).setOverrideForType(
                                TrackSelectionOverride(
                                    group.mediaTrackGroup, listOf(selected.trackIndex)
                                )
                            ).build()
                }
            }
            showQualityDrawer = false
            exoPlayer.playWhenReady = true
        })

        if (showNowAiring && epgPrograms.isNotEmpty()) {
            NowAiringDialog(
                visible = showNowAiring,
                programs = epgPrograms,
                initialIndex = initialAiringIndex,
                onDismiss = {
                    showNowAiring = false
                    exoPlayer.play()
                })
        }
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState,
    seekController: SeekController
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.isControlsVisible) {
            seekController.back()
            pulseState.setType(VideoPlayerPulse.Type.BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.isControlsVisible) {
            seekController.forward()
            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        exoPlayer.pause()
        videoPlayerState.showControls()
    })