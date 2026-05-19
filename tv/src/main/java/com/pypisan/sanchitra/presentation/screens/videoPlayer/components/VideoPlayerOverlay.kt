package com.pypisan.sanchitra.presentation.screens.videoPlayer.components


import com.pypisan.sanchitra.presentation.theme.SanchitraTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.PlaybackException
import kotlinx.coroutines.android.awaitFrame

@Composable
fun VideoPlayerOverlay(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "Something went wrong",
    isBuffering: Boolean = false,
    isSubtitleDrawerVisible: Boolean = false,
    isControlsVisible: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    showControls: () -> Unit = {},
    onRetry: () -> Unit = {},
    centerButton: @Composable () -> Unit = {},
    subtitles: @Composable () -> Unit = {},
    controls: @Composable () -> Unit = {}
) {

    val subtitleBottomPadding by animateDpAsState(
        targetValue = if (isControlsVisible) 140.dp else 36.dp,
        label = ""
    )

    LaunchedEffect(isControlsVisible, isSubtitleDrawerVisible) {
        if (isControlsVisible && !isSubtitleDrawerVisible) {
            try {
                awaitFrame()
                focusRequester.requestFocus()
            } catch (_: Exception) {
            }
        }
    }

    LaunchedEffect(isPlaying) {
        showControls()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // Background overlay when controls visible
        AnimatedVisibility(
            visible = isControlsVisible && !isError && !isBuffering ,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CinematicBackground(Modifier.fillMaxSize())
        }

        // Main content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // SUBTITLES
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = subtitleBottomPadding)
            ) {
                subtitles()
            }

            // CONTROLS
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = isControlsVisible && !isError && !isBuffering,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 56.dp)
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    controls()
                }
            }
        }

        if (!isError) {
            centerButton()
        }

        if (isBuffering && !isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // ERROR OVERLAY (Retry UI)
        if (isError) {
            ErrorOverlay(
                message = errorMessage,
                onRetry = onRetry // Pass the onRetry callback down
            )
        }
    }
}

@Composable
fun CinematicBackground(modifier: Modifier = Modifier) {
    Spacer(
        modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color.Black.copy(alpha = 0.1f),
                    Color.Black.copy(alpha = 0.8f)
                )
            )
        )
    )
}

@Preview(device = "id:tv_4k")
@Composable
private fun VideoPlayerOverlayPreview() {
    SanchitraTheme {
        Box(Modifier.fillMaxSize()) {
            VideoPlayerOverlay(
                modifier = Modifier.align(Alignment.BottomCenter),
                isPlaying = true,
                subtitles = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Red)
                    )
                },
                controls = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Blue)
                    )
                },
                centerButton = {
                    Box(
                        Modifier
                            .size(88.dp)
                            .background(Color.Green)
                    )
                }
            )
        }
    }
}
