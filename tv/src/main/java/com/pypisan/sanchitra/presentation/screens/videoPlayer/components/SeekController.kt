package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class SeekController(
    private val player: ExoPlayer, private val scope: CoroutineScope
) {
    companion object {
        private const val SEEK_STEP_MS = 10_000L
        private const val COMMIT_DELAY = 700L
    }

    private var commitJob: Job? = null
    val pendingPosition = MutableStateFlow<Long?>(null)

    // Tracks accumulated seconds: +10, +20, -10, -20, etc.
    var accumulatedSeconds by mutableIntStateOf(0)
        private set

    fun forward() {
        accumulatedSeconds = if (accumulatedSeconds < 0) 10 else accumulatedSeconds + 10
        update(SEEK_STEP_MS)
    }

    fun back() {
        accumulatedSeconds = if (accumulatedSeconds > 0) -10 else accumulatedSeconds - 10
        update(-SEEK_STEP_MS)
    }

    private fun update(delta: Long) {
        val duration = player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE
        val basePosition = pendingPosition.value ?: player.currentPosition

        val target = (basePosition + delta).coerceIn(0L, duration)
        pendingPosition.value = target

        commitJob?.cancel()
        commitJob = scope.launch {
            delay(COMMIT_DELAY)
            commit()
        }
    }

    fun commit() {
        commitJob?.cancel()
        pendingPosition.value?.let(player::seekTo)
        pendingPosition.value = null
        accumulatedSeconds = 0 // Reset when seek commits
    }

    fun cancel() {
        commitJob?.cancel()
        pendingPosition.value = null
        accumulatedSeconds = 0
    }
}