package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
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
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.SubtitleTextOverlay
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoQualityDrawer


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenContent(
    title: String,
    currentEpisode: String,
    nextEpisode: String,
    epgResponse: EPGResponse?,
    exoPlayer: ExoPlayer,
    subtitles: List<SubtitleTrack>,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    audios: List<AudioTrack>,
    qualities: List<VideoQuality>,
    onBackPressed: () -> Unit,
    isBuffering: Boolean,
    isErrorState: Boolean,
    errorMessage: String,
    onClearError: () -> Unit
) {
    val videoPlayerState = rememberVideoPlayerState()
    val pulseState = rememberVideoPlayerPulseState()
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    val fallbackFocusRequester = remember { FocusRequester() }

    val seekController = remember(exoPlayer) {
        SeekController(exoPlayer, scope)
    }

    // Watch the visibility state of the controls!
    LaunchedEffect(videoPlayerState.isControlsVisible) {
        if (videoPlayerState.isControlsVisible) {
            // 1. Controls are VISIBLE: Wait a tiny bit and focus the Play Button
            kotlinx.coroutines.delay(50)
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        } else {
            // 2. Controls are HIDDEN: Safely park the focus on the invisible video background
            // so it doesn't escape to the catalog!
            try { fallbackFocusRequester.requestFocus() } catch (e: Exception) {}
        }
    }

    var showQualityDrawer by rememberSaveable {
        mutableStateOf(false)
    }

    var showNowAiring by rememberSaveable {
        mutableStateOf(false)
    }

    val (epgPrograms, initialAiringIndex) = remember(epgResponse, showNowAiring) {
        if (showNowAiring && epgResponse != null) {
            prepareEPGProgramData(epgResponse)
        } else {
            Pair(emptyList(), 0)
        }
    }

    var showAudioQualityDrawer by rememberSaveable {
        mutableStateOf(false)
    }

    var subtitleText by remember {
        mutableStateOf("")
    }

    var showSubtitleDrawer by rememberSaveable {
        mutableStateOf(false)
    }

    DisposableEffect(exoPlayer) {

        val listener = object : Player.Listener {

            override fun onCues(cues: CueGroup) {
                subtitleText = cues.cues.joinToString("\n") { cue ->
                    cue.text?.toString() ?: ""
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }


    LaunchedEffect(exoPlayer.isPlaying) {
        if (exoPlayer.isPlaying) {
            videoPlayerState.showControls(true)
        } else {
            videoPlayerState.showControls(false)
        }
    }

    RememberPlaybackWatchdog(
        exoPlayer = exoPlayer, onFreeze = {
            val mediaItem = exoPlayer.currentMediaItem
            if (mediaItem != null) {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.seekToDefaultPosition()
                exoPlayer.play()
            }
        })

    BackHandler {
        exoPlayer.release()
        onBackPressed()
    }

    Box(
        Modifier
            .dPadEvents(
                exoPlayer, videoPlayerState, pulseState, seekController
            )
            // 1. Attach the fallback requester
            .focusRequester(fallbackFocusRequester)
            // 2. Lock the D-Pad so pressing arrows while controls are hidden doesn't jump to the catalog
            .focusProperties { onExit = { FocusRequester.Cancel } }
            // 3. Make this box focusable so it can safely catch the parked focus!
            .focusable()
    ) {
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.resizeWithContentScale(
                contentScale = ContentScale.Fit, sourceSizeDp = null
            )
        )

        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = videoPlayerState.isControlsVisible,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {
                SubtitleTextOverlay(
                    subtitleText = subtitleText
                )
            },
            showControls = videoPlayerState::showControls,
            // Pass the state variables you receive from TVPlayerBuild
            isError = isErrorState,
            errorMessage = errorMessage,
            onRetry = {
                onClearError()
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
                    currentEpisode = currentEpisode,
                    nextEpisode = nextEpisode,
                    focusRequester = focusRequester,
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
                    it.copy(
                        isSelected = it.label == selected.label
                    )
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
                            TrackSelectionOverride(
                                it, listOf(selected.trackIndex)
                            )
                        )
                }?.build()!!
            }

            showAudioQualityDrawer = false
            exoPlayer.playWhenReady = true
        })

        VideoQualityDrawer(
            visible = showQualityDrawer, qualities = qualities, onDismiss = {
                showQualityDrawer = false
                exoPlayer.play()
            },

            onQualitySelected = { selected ->
                if (selected.height == -1) {

                    // AUTO QUALITY
                    exoPlayer.trackSelectionParameters =
                        exoPlayer.trackSelectionParameters.buildUpon()
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
//    onLeft = {
//        if (!videoPlayerState.isControlsVisible) {
//            exoPlayer.seekBack()
//            pulseState.setType(VideoPlayerPulse.Type.BACK)
//        }
//    },
    onLeft = {
        if (!videoPlayerState.isControlsVisible) {
            seekController.back()
            pulseState.setType(VideoPlayerPulse.Type.BACK)
        }
    },
//    onRight = {
//        if (!videoPlayerState.isControlsVisible) {
//            exoPlayer.seekForward()
//            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
//        }
//    },
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