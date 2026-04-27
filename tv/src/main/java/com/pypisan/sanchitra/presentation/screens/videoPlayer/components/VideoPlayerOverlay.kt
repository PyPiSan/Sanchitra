package com.pypisan.sanchitra.presentation.screens.videoPlayer.components
import android.util.Log
import com.pypisan.sanchitra.presentation.theme.SanchitraTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
@Composable
fun VideoPlayerOverlay(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    isControlsVisible: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    showControls: () -> Unit = {},
    centerButton: @Composable () -> Unit = {},
    subtitles: @Composable () -> Unit = {},
    controls: @Composable () -> Unit = {}
) {
    LaunchedEffect(isControlsVisible) {
        if (isControlsVisible) {
            try {
                kotlinx.coroutines.android.awaitFrame()
                focusRequester.requestFocus()
            } catch (e: Exception) {
                Log.e("Focus", "Focus request failed", e)
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
        AnimatedVisibility(isControlsVisible, Modifier, fadeIn(), fadeOut()) {
            CinematicBackground(Modifier.fillMaxSize())
        }

        Column {
            Box(
                Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                subtitles()
            }

            AnimatedVisibility(
                isControlsVisible,
                Modifier,
                slideInVertically { it },
                slideOutVertically { it }
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
        centerButton()
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
