package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class SeekController(
    private val player: ExoPlayer,
    private val scope: CoroutineScope
) {
    companion object {
        private const val SEEK_STEP_MS = 10_000L
        private const val COMMIT_DELAY = 700L
    }

    private var pendingOffset = 0L
    private var commitJob: Job? = null

    val pendingPosition = MutableStateFlow<Long?>(null)

    fun forward() {
        update(SEEK_STEP_MS)
    }

    fun back() {
        update(-SEEK_STEP_MS)
    }

    private fun update(delta: Long) {
        pendingOffset += delta

        val current = player.currentPosition
        val duration = player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE

        val target = (current + pendingOffset)
            .coerceIn(0L, duration)

        pendingPosition.value = target

        commitJob?.cancel()
        commitJob = scope.launch {
            delay(COMMIT_DELAY)
            commit()
        }
    }

    fun commit() {
        commitJob?.cancel()

        pendingPosition.value?.let {
            player.seekTo(it)
        }

        pendingOffset = 0L
        pendingPosition.value = null
    }

    fun cancel() {
        commitJob?.cancel()
        pendingOffset = 0L
        pendingPosition.value = null
    }
}