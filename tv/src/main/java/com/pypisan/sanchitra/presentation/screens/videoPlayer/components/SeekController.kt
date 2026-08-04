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

    private var commitJob: Job? = null
    val pendingPosition = MutableStateFlow<Long?>(null)

    fun forward() {
        update(SEEK_STEP_MS)
    }

    fun back() {
        update(-SEEK_STEP_MS)
    }

    private fun update(delta: Long) {
        val duration = player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE

        val basePosition = pendingPosition.value ?: player.currentPosition

        val target = (basePosition + delta)
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
        pendingPosition.value?.let(player::seekTo)
        pendingPosition.value = null
    }

    fun cancel() {
        commitJob?.cancel()
        pendingPosition.value = null
    }
}

class DoubleClickHandler(
    private val timeoutMs: Long = 400
) {
    private var lastRightClick = 0L
    private var lastLeftClick = 0L

    fun onRightDoubleClick(action: () -> Unit) {
        val now = System.currentTimeMillis()

        if (now - lastRightClick < timeoutMs) {
            action()
            lastRightClick = 0L
        } else {
            lastRightClick = now
        }
    }

    fun onLeftDoubleClick(action: () -> Unit) {
        val now = System.currentTimeMillis()

        if (now - lastLeftClick < timeoutMs) {
            action()
            lastLeftClick = 0L
        } else {
            lastLeftClick = now
        }
    }
}