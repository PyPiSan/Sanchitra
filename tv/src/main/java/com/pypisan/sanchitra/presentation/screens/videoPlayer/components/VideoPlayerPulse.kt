package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

//View for Fast forward and back button pulse animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlin.math.abs

object VideoPlayerPulse {
    enum class Type { FORWARD, BACK, NONE }
}

class VideoPlayerPulseState {
    private var _type by mutableStateOf(VideoPlayerPulse.Type.NONE)
    val type: VideoPlayerPulse.Type get() = _type

    var seconds by mutableIntStateOf(0)
        private set

    private val channel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce(1.seconds) // Fades out 1s after last click
            .collect {
                _type = VideoPlayerPulse.Type.NONE
                seconds = 0
            }
    }

    fun show(type: VideoPlayerPulse.Type, sec: Int) {
        _type = type
        seconds = abs(sec)
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

@Composable
fun VideoPlayerPulse(
    state: VideoPlayerPulseState = rememberVideoPlayerPulseState()
) {
    var lastActiveType by remember { mutableStateOf(VideoPlayerPulse.Type.FORWARD) }
    if (state.type != VideoPlayerPulse.Type.NONE) {
        lastActiveType = state.type
    }

    // Keep the last non-zero seconds so it doesn't flash "0s" while fading out
    var lastSeconds by remember { mutableIntStateOf(0) }
    if (state.seconds > 0) {
        lastSeconds = state.seconds
    }

    val isVisible = state.type != VideoPlayerPulse.Type.NONE

    val icon = when (lastActiveType) {
        VideoPlayerPulse.Type.FORWARD -> Icons.Rounded.KeyboardDoubleArrowRight
        VideoPlayerPulse.Type.BACK -> Icons.Rounded.KeyboardDoubleArrowLeft
        VideoPlayerPulse.Type.NONE -> null
    }

    val xOffset = when (lastActiveType) {
        VideoPlayerPulse.Type.FORWARD -> 160.dp
        VideoPlayerPulse.Type.BACK -> (-160).dp
        VideoPlayerPulse.Type.NONE -> 0.dp
    }

    // Bounce every time seconds increment
    val bumpAnim = remember { Animatable(1f) }
    LaunchedEffect(state.seconds) {
        if (state.seconds > 0) {
            bumpAnim.animateTo(
                targetValue = 1.12f,
                animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
            )
            bumpAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible && icon != null,
            enter = fadeIn(animationSpec = tween(150)) +
                    scaleIn(initialScale = 0.85f, animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(250)) +
                    scaleOut(targetScale = 0.9f, animationSpec = tween(250)),
            modifier = Modifier.offset(x = xOffset)
        ) {
            Box(
                contentAlignment = Alignment.Center, // Centers everything inside the circle
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer {
                        scaleX = bumpAnim.value
                        scaleY = bumpAnim.value
                    }
                    // Semi-transparent frosted dark glass look (no .shadow() bleed-through)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xDD1C1D24),
                                Color(0xF0101116)
                            )
                        ),
                        shape = CircleShape
                    )
                    // Subtle gradient border ring
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .padding(8.dp) // Light inner padding instead of 20.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    AnimatedContent(
                        targetState = lastSeconds,
                        contentAlignment = Alignment.Center,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { height -> height / 2 } + fadeIn())
                                    .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut())
                            } else {
                                fadeIn() togetherWith fadeOut()
                            }
                        },
                        label = "SeekSecondsAnimation"
                    ) { sec ->
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$sec",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                )
                            )
                            Text(
                                text = "s",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 1.dp, bottom = 1.dp),
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
