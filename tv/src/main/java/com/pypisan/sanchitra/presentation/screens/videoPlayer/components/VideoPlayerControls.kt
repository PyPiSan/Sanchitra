package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.PlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.util.StringConstants

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerControls(
    player: Player,
    title: String,
    focusRequester: FocusRequester,
    onShowControls: () -> Unit = {},
    onShowAudioSettings: () -> Unit = {},
    onShowSubtitles: () -> Unit = {},
    state: PlayPauseButtonState = rememberPlayPauseButtonState(player),
) {

    // Detect if it's a Live stream
    val isLive = remember(player.mediaItemCount, player.playbackState) {
        player.isCurrentMediaItemLive ||
                player.duration <= 0 ||
                !player.isCurrentMediaItemSeekable
    }

    VideoPlayerMainFrame(
        mediaTitle = {
            Column {
                VideoPlayerMediaTitle(
                    title = title,
                    secondaryText = "",
                    tertiaryText = "",
                    type = VideoPlayerMediaTitleType.DEFAULT
                )

                VideoPlayerControlsIcon(
                    modifier = Modifier.focusRequester(focusRequester),
                    icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause,
                    onClick = state::onClick,
                    isPlaying = player.isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlPlayPauseButton
                )
            }
        },
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isLive) {
                    PreviousButton(
                        player = player,
                        onShowControls = onShowControls
                    )
                    NextButton(
                        player = player,
                        onShowControls = onShowControls
                    )
                    RepeatButton(
                        player = player,
                        onShowControls = onShowControls,
                    )
                }
//                VideoPlayerControlsIcon(
//                    icon = Icons.Default.AutoAwesomeMotion,
//                    isPlaying = isPlaying,
//                    contentDescription =
//                    StringConstants.Composable.VideoPlayerControlPlaylistButton,
//                    onShowControls = onShowControls
//                )
//
                VideoPlayerControlsIcon(
                    icon = Icons.Default.ClosedCaption,
                    isPlaying = player.isPlaying,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                    onShowControls = onShowControls,
                    onClick = onShowSubtitles
                )

                VideoPlayerControlsIcon(
                    icon = Icons.Default.Settings,
                    isPlaying = player.isPlaying,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlSettingsButton,
                    onShowControls = onShowAudioSettings
                )
            }
        },
        seeker = {
            Column {
                if (isLive) {
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
                            LiveAlwaysFullSeeker()
                        }

                        LiveBadge()
                    }

                } else {
                    // Standard interactive seeker for movies
                    VideoPlayerSeeker(
                        player = player,
                        focusRequester = focusRequester,
                        onShowControls = onShowControls,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

//                AudioSettings(
//                    player = player,
//                    onLanguageSelected = { language ->
//                        selectAudio(player, language)
//                    }
//                )
            }
        },
        more = null
    )
}

@Composable
fun LiveAlwaysFullSeeker() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // The track (The background of the seekbar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(2.dp))
        ) {
            // The progress (Always 100% width)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.White, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun LiveBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, shape = CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "LIVE",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

