package com.example.sanchitra.presentation.screens.videoPlayer.components

import PreviousButton
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sanchitra.data.entities.MovieDetails
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.presentation.screens.videoPlayer.getVlcAudioTracks
import com.example.sanchitra.presentation.screens.videoPlayer.selectAudio
import com.example.sanchitra.presentation.screens.videoPlayer.setAudioTrack
import org.videolan.libvlc.MediaPlayer

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerControls(
    player: Player,
    channel: Channel,
    focusRequester: FocusRequester,
    onShowControls: () -> Unit = {},
    onShowAudioSettings: () -> Unit = {},
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    DisposableEffect(player) {

        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
        }
    }

    // Detect if it's a Live stream
    val isLive = remember(player.mediaItemCount, player.playbackState) {
        player.isCurrentMediaItemLive ||
                player.duration <= 0 ||
                !player.isCurrentMediaItemSeekable
    }

    VideoPlayerMainFrame(
        mediaTitle = {
            Column {
                if (isLive) {
                    LiveBadge()
                    Spacer(modifier = Modifier.height(8.dp))
                }
                VideoPlayerMediaTitle(
                    title = channel.name,
                    secondaryText = "",
                    tertiaryText = "",
                    type = VideoPlayerMediaTitleType.DEFAULT
                )

                VideoPlayerControlsIcon(
                    modifier = Modifier.focusRequester(focusRequester),
                    icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    isPlaying = isPlaying,
                    onClick = {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    },
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                    onShowControls = {}
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
                    isPlaying = isPlaying,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                    onShowControls = onShowControls
                )

                VideoPlayerControlsIcon(
                    icon = Icons.Default.Settings,
                    isPlaying = isPlaying,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlSettingsButton,
                    onShowControls = onShowAudioSettings
                )
            }
        },
        seeker = {
            Column {
                if (isLive) {
                    // Show a "Full" Seekbar that doesn't move or calculate math
                    LiveAlwaysFullSeeker()
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
fun VideoPlayerControlsVLC(
    isPlaying: Boolean,
    focusRequester: FocusRequester,
    movieDetails: MovieDetails,
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
                    title = movieDetails.name,
                    secondaryText = movieDetails.releaseDate,
                    tertiaryText = movieDetails.director,
                    type = VideoPlayerMediaTitleType.DEFAULT
                )
                Spacer(modifier = Modifier.height(8.dp))

                VideoPlayerControlsIcon(
                    icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    isPlaying = isPlaying,
                    onClick = onPlayPause,
                    contentDescription =
                        StringConstants.Composable.VideoPlayerControlClosedCaptionsButton,
                    onShowControls = {}
                )
            }
        },

        // 🎮 ACTION BUTTONS (Centered Row)
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
fun AudioTrackSelectorVLC(
    player: MediaPlayer,
    onDismiss: () -> Unit
) {
    val tracks = remember { getVlcAudioTracks(player) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        Text("Audio Tracks", color = Color.White)

        Spacer(modifier = Modifier.height(12.dp))

        tracks.forEach { (id, name) ->
            Text(
                text = name,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        setAudioTrack(player, id)
                        onDismiss()
                    }
                    .padding(12.dp)
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

