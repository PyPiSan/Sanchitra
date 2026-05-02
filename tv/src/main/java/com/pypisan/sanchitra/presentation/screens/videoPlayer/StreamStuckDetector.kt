package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun RememberPlaybackWatchdog(
    exoPlayer: ExoPlayer,
    onFreeze: () -> Unit
) {
    LaunchedEffect(exoPlayer) {

        var lastPosition = 0L
        var freezeStart = 0L

        while (true) {
            kotlinx.coroutines.delay(1500)

            val current = exoPlayer.currentPosition
            val isPlaying = exoPlayer.isPlaying

            // ignore buffering state completely
            if (isPlaying) {

                if (current == lastPosition) {

                    if (freezeStart == 0L) {
                        freezeStart = System.currentTimeMillis()
                    }

                    val frozenFor = System.currentTimeMillis() - freezeStart

                    if (frozenFor > 6000) {
//                        Log.e("TV", "REAL FREEZE DETECTED")
                        onFreeze()
                        freezeStart = 0L
                    }

                } else {
                    freezeStart = 0L
                }

                lastPosition = current
            } else {
                freezeStart = 0L
            }
        }
    }
}