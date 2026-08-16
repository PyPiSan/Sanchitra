package com.pypisan.sanchitra.presentation.common

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.pypisan.sanchitra.presentation.theme.SanchitraBorderWidth
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 9f,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Smooth light sweep animation progression (0.0 -> 1.0) on focus
    val lightSweepProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearOutSlowInEasing
        ),
        label = "ChannelLightSweep"
    )

    StandardCardContainer(
        modifier = modifier,
        title = { title() },
        interactionSource = interactionSource,
        imageCard = {
            Surface(
                onClick = onClick,
                interactionSource = interactionSource,
                shape = ClickableSurfaceDefaults.shape(SanchitraCardShape),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.15f)
                ),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = SanchitraBorderWidth,
                            color = Color.White
                        ),
                        shape = SanchitraCardShape
                    )
                ),
                glow = ClickableSurfaceDefaults.glow(
                    focusedGlow = Glow(
                        elevationColor = Color.White.copy(alpha = 0.4f),
                        elevation = 10.dp
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(
                    scale = 1f,
                    focusedScale = 1.06f
                )
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(aspectRatio)
                        .clip(SanchitraCardShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isFocused) 0.15f else 0.0f),
                                    Color.White.copy(alpha = if (isFocused) 0.03f else 0.0f)
                                )
                            )
                        )
                ) {
                    // Channel Image
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        content = image
                    )

                    // Glass Light Sweep Overlay
                    if (isFocused) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()

                                    // Offset calibrated for 16:9 landscape cards
                                    val sweepOffset = lightSweepProgress * (size.width * 2.5f) - size.width

                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.White.copy(alpha = 0.35f),
                                                Color.Transparent
                                            ),
                                            start = Offset(sweepOffset, 0f),
                                            end = Offset(sweepOffset + size.width * 0.6f, size.height)
                                        )
                                    )
                                }
                        )
                    }
                }
            }
        }
    )
}
