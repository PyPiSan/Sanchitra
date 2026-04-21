package com.example.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.example.sanchitra.data.models.IPTVChannel
import com.example.sanchitra.data.util.StringConstants

@Composable
fun VideoPlayerControlsVLC(
    isPlaying: Boolean,
    focusRequester: FocusRequester,
    iptvChannelDetail: IPTVChannel,
    isLive: Boolean,
    currentTime: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBack: () -> Unit,
) {
    VideoPlayerMainFrame(
        // TITLE AREA
        mediaTitle = {
            Column {
                if (isLive) {
                    LiveBadge()
                    Spacer(modifier = Modifier.height(8.dp))
                }

                VideoPlayerMediaTitle(
                    title = iptvChannelDetail.name,
                    secondaryText = "",
                    tertiaryText = "",
                    type = VideoPlayerMediaTitleType.DEFAULT
                )
                Spacer(modifier = Modifier.height(8.dp))

                VideoPlayerControlsIcon(
                    modifier = Modifier.focusRequester(focusRequester),
                    icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    isPlaying = isPlaying,
                    onClick = onPlayPause,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                    onShowControls = {}
                )
            }
        },

        // ACTION BUTTONS (Centered Row)
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (!isLive) {
//                    PreviousButton(
//                        player = {},
//                        onShowControls = {}
//                    )
//                    NextButton(
//                        player = player,
//                        onShowControls = {}
//                    )
//                    RepeatButton(
//                        player = player,
//                        onShowControls = {},
//                    )}
                    Spacer(modifier = Modifier.height(8.dp))

                    VideoPlayerControlsIcon(
                        icon = Icons.Default.Forward10,
                        isPlaying = isPlaying,
                        contentDescription =
                            StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                        onShowControls = {}
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            VideoPlayerControlsIcon(
                icon = Icons.Default.ClosedCaption,
                isPlaying = isPlaying,
                contentDescription =
                    StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                onShowControls = {}
            )
            Spacer(modifier = Modifier.width(8.dp))

            VideoPlayerControlsIcon(
                icon = Icons.Default.Settings,
                isPlaying = isPlaying,
                contentDescription =
                    StringConstants.Composable.VideoPlayerControlSettingsButton,
                onShowControls = {}
            )

        },

        // 🎯 SEEKER
        seeker = {
            if (isLive) {
                LiveAlwaysFullSeeker()
            } else {
                VLCSeeker(
                    currentTime = currentTime,
                    duration = duration
                )
            }
        },
        more = null,
    )
}

@Composable
fun VLCSeeker(
    currentTime: Long,
    duration: Long
) {
    val progress = if (duration > 0) {
        currentTime.toFloat() / duration.toFloat()
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentTime), color = Color.White)
            Text(formatTime(duration), color = Color.White)
        }
    }
}


fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val seconds = (totalSeconds % 60).toInt()
    val minutes = ((totalSeconds / 60) % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()

    return if (hours > 0)
        "%d:%02d:%02d".format(hours, minutes, seconds)
    else
        "%02d:%02d".format(minutes, seconds)
}