package com.pypisan.sanchitra.presentation.screens.movies

import android.media.MediaPlayer
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import androidx.core.net.toUri

@Composable
fun TrailerDialog(
    trailerUrl: String,
    onDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    val playButtonFocusRequester = remember { FocusRequester() }

    // Helper to stop playback immediately and dismiss
    val handleClose = {
        videoViewRef?.stopPlayback()
        onDismiss()
    }

    // Stop player when back button is pressed
    BackHandler {
        handleClose()
    }

    // Clean up player when composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            videoViewRef?.stopPlayback()
            videoViewRef = null
        }
    }

    // Auto-hide controls after 3 seconds when playing (and not loading)
    LaunchedEffect(showControls, isPlaying, isLoading) {
        if (showControls && isPlaying && !isLoading) {
            delay(3000)
            showControls = false
        }
    }

    // Focus play button when controls are visible and video is ready
    LaunchedEffect(showControls, isLoading) {
        if (showControls && !isLoading) {
            playButtonFocusRequester.requestFocus()
        }
    }

    Dialog(
        onDismissRequest = handleClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        videoViewRef = this
                        setVideoURI(trailerUrl.toUri())

                        // Listen for initial ready event
                        setOnPreparedListener { mp ->
                            isLoading = false
                            mp.isLooping = false

                            // Listen for buffering events during playback
                            mp.setOnInfoListener { _, what, _ ->
                                when (what) {
                                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> isLoading = true
                                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> isLoading = false
                                }
                                true
                            }
                            start()
                        }
                    }
                },
                update = { videoView ->
                    if (isPlaying) {
                        if (!videoView.isPlaying && !isLoading) videoView.start()
                    } else {
                        if (videoView.isPlaying) videoView.pause()
                    }
                },
                onRelease = { videoView ->
                    // Ensures memory is freed when view is destroyed
                    videoView.stopPlayback()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showControls = !showControls }
            )

            // Controls & Loader Overlay
            AnimatedVisibility(
                visible = isLoading || showControls || !isPlaying,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { showControls = !showControls },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        // 1. Loading Indicator while buffering
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        // 2. Play / Pause Button when ready
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .focusRequester(playButtonFocusRequester)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                                .clickable {
                                    isPlaying = !isPlaying
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}