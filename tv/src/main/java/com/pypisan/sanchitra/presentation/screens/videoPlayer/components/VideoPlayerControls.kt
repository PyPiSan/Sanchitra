package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.PlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import com.pypisan.sanchitra.data.models.ProgramDisplayModel
import com.pypisan.sanchitra.data.util.StringConstants

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerControls(
    player: Player,
    title: String,
    epgPrograms: List<ProgramDisplayModel>,
    onShowControls: () -> Unit = {},
    onShowInfo: () -> Unit = {},
    onShowAudioSettings: () -> Unit = {},
    onShowSubtitles: () -> Unit = {},
    onShowQuality: () -> Unit = {},
    state: PlayPauseButtonState = rememberPlayPauseButtonState(player),
) {

    // Detect if it's a Live stream
    val isLive = remember(player.mediaItemCount, player.playbackState) {
        player.isCurrentMediaItemLive || player.duration <= 0 || !player.isCurrentMediaItemSeekable
    }
    val playButtonRequester = remember { FocusRequester() }
    var hasFocusedPlayButton by remember { mutableStateOf(false) }

    // 1. If the list is empty, indexOfFirst safely returns -1. It will not crash.
    val currentIndex = epgPrograms.indexOfFirst {
        it.status.equals("NOW AIRING", ignoreCase = true)
    }

    // 2. Calculate current program
    val currentProgram = when {
        epgPrograms.isEmpty() -> null
        currentIndex != -1 -> epgPrograms[currentIndex]
        else -> epgPrograms.first()
    }

    val nextProgram = if (currentIndex != -1 && currentIndex + 1 < epgPrograms.size) {
        epgPrograms[currentIndex + 1]
    } else {
        null
    }

    VideoPlayerMainFrame(
        mediaTitle = {
        Column {
            VideoPlayerMediaTitle(
                title = title,
                secondaryText = currentProgram?.title ?: "",
                tertiaryText = nextProgram?.title ?: "",
                type = VideoPlayerMediaTitleType.LIVE
            )

            VideoPlayerControlsIcon(
                modifier = Modifier
                    .focusRequester(playButtonRequester)
                    .onGloballyPositioned {
                        if (!hasFocusedPlayButton) {
                            try {
                                playButtonRequester.requestFocus()
                                hasFocusedPlayButton = true
                            } catch (e: Exception) {
                            }
                        }
                    },
                icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause,
                onClick = state::onClick,
                isPlaying = player.isPlaying,
                contentDescription = StringConstants.Composable.VideoPlayerControlPlayPauseButton
            )
        }
    }, mediaActions = {
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentProgram?.title?.isNotEmpty() == true) {
                VideoPlayerControlsIcon(
                    icon = Icons.Default.Info,
                    isPlaying = player.isPlaying,
                    contentDescription = StringConstants.Composable.VideoPlayerControlInfoButton,
                    onShowControls = onShowControls,
                    onClick = onShowInfo
                )
            }
            VideoPlayerControlsIcon(
                icon = Icons.Default.ClosedCaption,
                isPlaying = player.isPlaying,
                contentDescription = StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                onShowControls = onShowControls,
                onClick = onShowSubtitles
            )

            VideoPlayerControlsIcon(
                icon = Icons.Default.Audiotrack,
                isPlaying = player.isPlaying,
                contentDescription = StringConstants.Composable.VideoPlayerControlAudioSelectionButton,
                onShowControls = onShowControls,
                onClick = onShowAudioSettings
            )

            VideoPlayerControlsIcon(
                icon = Icons.Default.Settings,
                isPlaying = player.isPlaying,
                contentDescription = StringConstants.Composable.VideoPlayerControlSettingsButton,
                onShowControls = onShowControls,
                onClick = onShowQuality
            )
        }
    }, seeker = {
        Column {
            if (isLive || epgPrograms.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (currentProgram != null) {
                            LiveEpgProgramSeeker(currentProgram)
                        } else {
                            LiveAlwaysFullSeeker()
                        }
                    }
                    LiveBadge()
                }

            } else {
                // Standard interactive seeker for movies
                VideoPlayerSeeker(
                    player = player,
                    onShowControls = onShowControls,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }, more = null
    )
}

