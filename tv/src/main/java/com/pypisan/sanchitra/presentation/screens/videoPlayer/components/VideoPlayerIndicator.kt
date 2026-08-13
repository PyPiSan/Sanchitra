package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.pypisan.sanchitra.utils.GradientColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun RowScope.VideoPlayerControllerIndicator(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = GradientColors,
    onShowControls: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSelected by remember { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var seekProgress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    var seekJob by remember { mutableStateOf<Job?>(null) }

    // Sync seekProgress with current playback progress when idle
    LaunchedEffect(progress, isSelected, seekJob) {
        if (!isSelected && seekJob == null) {
            seekProgress = progress
        }
    }

    // Determine target progress to draw
    val isSeekingActive = isSelected || seekJob != null
    val currentTargetProgress = if (isSeekingActive) seekProgress else progress

    // 1. Smooth progress interpolation for 60/120 FPS rendering
    val animatedProgress by animateFloatAsState(
        targetValue = currentTargetProgress.coerceIn(0f, 1f),
        animationSpec = if (isSeekingActive) {
            snap() // Instant update during D-Pad hold/tap seeking
        } else {
            tween(durationMillis = 200, easing = LinearEasing) // Smooth continuous video playback
        },
        label = "IndicatorProgressAnim"
    )

    // Animated focus sizes for Android TV
    val animatedTrackHeight by animateDpAsState(
        targetValue = if (isFocused || isSeekingActive) 8.dp else 4.dp,
        animationSpec = tween(150),
        label = "TrackHeightAnim"
    )

    val animatedDotRadius by animateDpAsState(
        targetValue = if (isSeekingActive) 8.dp else if (isFocused) 6.dp else 0.dp,
        animationSpec = tween(150),
        label = "DotRadiusAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "GlowPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    // Handle Hold/Long-Press D-Pad Seeking Coroutine Loop
    fun startSeeking(direction: Int) { // -1 for Left, +1 for Right
        if (seekJob != null) return // Already seeking
        seekJob = coroutineScope.launch {
            onShowControls()
            if (!isSelected) {
                seekProgress = progress
            }

            val baseStep = 0.01f * direction // 1% initial jump
            seekProgress = (seekProgress + baseStep).coerceIn(0f, 1f)

            // Initial hold delay before continuous scrubbing starts (standard TV key delay)
            delay(300)

            // Continuous fast-forward/rewind loop while key is held down
            var speedMultiplier = 1.0f
            while (isActive) {
                seekProgress = (seekProgress + (baseStep * speedMultiplier)).coerceIn(0f, 1f)
                onShowControls()

                // Accelerate seek speed smoothly up to 4x over time
                if (speedMultiplier < 4.0f) {
                    speedMultiplier += 0.15f
                }
                delay(40) // ~25 updates/second for smooth seekbar motion
            }
        }
    }

    fun stopSeeking() {
        if (seekJob != null) {
            seekJob?.cancel()
            seekJob = null
            // Auto-commit seek on key release if not in explicit selection mode
            if (!isSelected) {
                onSeek(seekProgress)
            }
        }
    }

    Box(
        modifier = modifier
            .weight(1f)
            .height(28.dp)
            .padding(horizontal = 12.dp)
            .onPreviewKeyEvent { keyEvent ->
                val key = keyEvent.key
                val isLeft = key == Key.DirectionLeft
                val isRight = key == Key.DirectionRight
                val isEnter = key == Key.DirectionCenter || key == Key.Enter || key == Key.NumPadEnter

                when {
                    isLeft || isRight -> {
                        val direction = if (isRight) 1 else -1
                        when (keyEvent.type) {
                            KeyEventType.KeyDown -> {
                                startSeeking(direction)
                                true // Consume event so focus doesn't jump to other views
                            }
                            KeyEventType.KeyUp -> {
                                stopSeeking()
                                true
                            }
                            else -> false
                        }
                    }
                    isEnter -> {
                        if (keyEvent.type == KeyEventType.KeyUp) {
                            onShowControls()
                            if (isSelected) {
                                isSelected = false
                                onSeek(seekProgress)
                            } else {
                                seekProgress = progress
                                isSelected = true
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            .focusable(interactionSource = interactionSource)
            .drawWithCache {
                val trackHeightPx = animatedTrackHeight.toPx()
                val cornerRadius = CornerRadius(trackHeightPx / 2)
                val yCenter = size.height / 2
                val yOffset = yCenter - (trackHeightPx / 2)
                val dotRadiusPx = animatedDotRadius.toPx()

                val progressWidth = size.width * animatedProgress

                // Cached Brush allocation to prevent garbage collection lag on TV
                val progressBrush = if (progressWidth > 0) {
                    Brush.horizontalGradient(
                        colors = gradientColors,
                        startX = 0f,
                        endX = size.width
                    )
                } else null

                val accentColor = gradientColors.lastOrNull() ?: Color.Red
                val trackBackgroundColor = Color.White.copy(alpha = 0.2f)

                onDrawBehind {
                    // A. Background Track
                    drawRoundRect(
                        color = trackBackgroundColor,
                        topLeft = Offset(0f, yOffset),
                        size = Size(size.width, trackHeightPx),
                        cornerRadius = cornerRadius
                    )

                    // B. Gradient Progress Line
                    if (progressWidth > 0 && progressBrush != null) {
                        drawRoundRect(
                            brush = progressBrush,
                            topLeft = Offset(0f, yOffset),
                            size = Size(progressWidth, trackHeightPx),
                            cornerRadius = cornerRadius
                        )
                    }

                    // C. Focused/Seeking Head Dot & Accent Glow
                    if (dotRadiusPx > 0f) {
                        drawCircle(
                            color = accentColor.copy(alpha = if (isSeekingActive) pulseAlpha else 0.4f),
                            radius = dotRadiusPx * 2.2f,
                            center = Offset(progressWidth, yCenter)
                        )

                        drawCircle(
                            color = Color.White,
                            radius = dotRadiusPx,
                            center = Offset(progressWidth, yCenter)
                        )

                        if (isSelected) {
                            drawCircle(
                                color = Color.White,
                                radius = dotRadiusPx * 1.5f,
                                center = Offset(progressWidth, yCenter),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }
            }
    )
}