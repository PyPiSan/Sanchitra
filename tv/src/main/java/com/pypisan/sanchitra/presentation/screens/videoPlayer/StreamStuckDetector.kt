package com.pypisan.sanchitra.presentation.screens.videoPlayer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun RememberPlaybackWatchdog(
    exoPlayer: ExoPlayer,
    onFreeze: () -> Unit
) {

    LaunchedEffect(exoPlayer) {

        var lastPosition = 0L
        var freezeStart = 0L
        var freezeReported = false

        while (true) {

            delay(1500)
//            Log.d("RememberPlaybackWatchdog", "Checking playback state , ${exoPlayer.playbackState}")

            // Handle playback ended
            if (exoPlayer.playbackState == Player.STATE_ENDED || exoPlayer.playbackState == Player.STATE_IDLE) {
                if (!freezeReported) {
                    freezeReported = true
                    onFreeze()
                }
                continue
            }

            if (!exoPlayer.isPlaying) {
                freezeStart = 0L
                freezeReported = false
                continue
            }

            // Ignore normal buffering
            if (exoPlayer.playbackState == Player.STATE_BUFFERING) {
                freezeStart = 0L
                freezeReported = false
                continue
            }

            val currentPosition = exoPlayer.currentPosition

            // tolerance avoids false positives
            val stuck =
                kotlin.math.abs(currentPosition - lastPosition) < 300

//            Log.d("RememberPlaybackWatchdog", "Checking playback state , $stuck")
//            Log.d("RememberPlaybackWatchdog", "currentPosition: $currentPosition")

            if (stuck) {

                if (freezeStart == 0L) {
                    freezeStart = System.currentTimeMillis()
                }

                val frozenFor =
                    System.currentTimeMillis() - freezeStart

                if (
                    frozenFor > 6000 &&
                    !freezeReported
                ) {

                    freezeReported = true
                    onFreeze()
                }

            } else {

                freezeStart = 0L
                freezeReported = false
            }

            lastPosition = currentPosition
        }
    }
}