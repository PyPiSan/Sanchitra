package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import com.pypisan.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerPulse.Type.NONE

object VideoPlayerPulse {
    enum class Type { FORWARD, BACK, NONE }
}

@Composable
fun VideoPlayerPulse(
    state: VideoPlayerPulseState = rememberVideoPlayerPulseState()
) {
    val icon = when (state.type) {
        VideoPlayerPulse.Type.FORWARD -> Icons.Default.Forward10
        VideoPlayerPulse.Type.BACK -> Icons.Default.Replay10
        NONE -> null
    }

    val xOffset = when (state.type) {
        VideoPlayerPulse.Type.FORWARD -> 120.dp
        VideoPlayerPulse.Type.BACK -> (-120).dp
        else -> 0.dp
    }

    if (icon != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        CircleShape
                    )
                    .size(88.dp)
                    .padding(20.dp) // controls inner icon spacing
            )
        }
    }
}

class VideoPlayerPulseState {
    private var _type by mutableStateOf(NONE)
    val type: VideoPlayerPulse.Type get() = _type

    private val channel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce(2.seconds)
            .collect { _type = NONE }
    }

    fun setType(type: VideoPlayerPulse.Type) {
        _type = type
        channel.trySend(Unit)
    }
}

@Composable
fun rememberVideoPlayerPulseState(): VideoPlayerPulseState {
    val state = remember { VideoPlayerPulseState() }

    LaunchedEffect(state) {
        state.observe()
    }

    return state
}
