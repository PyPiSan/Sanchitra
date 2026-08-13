package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerSeeker(
    player: Player,
    modifier: Modifier = Modifier,
    onSeek: (Float) -> Unit = { progress ->
        val targetMs = (player.duration * progress).toLong()
        player.seekTo(targetMs)
    },
    onShowControls: () -> Unit = {},
) {
    val contentDurationMs = player.contentDuration.coerceAtLeast(0L)
    val contentDuration = contentDurationMs.milliseconds

    var currentPositionMs by remember(player) { mutableLongStateOf(0L) }

    // Fast 200ms position listener
    LaunchedEffect(player) {
        while (true) {
            currentPositionMs = player.currentPosition.coerceAtLeast(0L)
            delay(200)
        }
    }

    val currentProgress = remember(currentPositionMs, contentDurationMs) {
        if (contentDurationMs > 0) {
            (currentPositionMs.toFloat() / contentDurationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    val contentProgressString = remember(currentPositionMs) {
        currentPositionMs.milliseconds.toComponents { h, m, s, _ ->
            if (h > 0) "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            else "${m.padStartWith0()}:${s.padStartWith0()}"
        }
    }

    val contentDurationString = remember(contentDurationMs) {
        contentDuration.toComponents { h, m, s, _ ->
            if (h > 0) "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            else "${m.padStartWith0()}:${s.padStartWith0()}"
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoPlayerControllerText(text = contentProgressString)

        VideoPlayerControllerIndicator(
            progress = currentProgress,
            onSeek = onSeek,
            onShowControls = onShowControls
        )

        VideoPlayerControllerText(text = contentDurationString)
    }
}

private fun Number.padStartWith0() = this.toString().padStart(2, '0')
