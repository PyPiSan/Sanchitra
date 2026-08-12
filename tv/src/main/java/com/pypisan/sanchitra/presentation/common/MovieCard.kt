package com.pypisan.sanchitra.presentation.common

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import com.pypisan.sanchitra.presentation.theme.SanchitraBorderWidth
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape

@Composable
fun MovieCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    StandardCardContainer(
        modifier = modifier,
        title = title,
        imageCard = {
            Surface(
                onClick = onClick,
                shape = ClickableSurfaceDefaults.shape(SanchitraCardShape),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = SanchitraBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = SanchitraCardShape
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                content = image
            )
        },
    )
}

//@OptIn(ExperimentalTvMaterial3Api::class)
//@Composable
//fun MovieGlassCard(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    aspectRatio: Float = 2f / 3f,
//    title: @Composable () -> Unit = {},
//    image: @Composable BoxScope.() -> Unit,
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//    val isFocused by interactionSource.collectIsFocusedAsState()
//
//    // Smooth linear zoom spec
//    val animSpec = tween<Float>(durationMillis = 200, easing = LinearOutSlowInEasing)
//
//    // 1. Image zooms IN inside the mask instead of shrinking
//    val imageZoomScale by animateFloatAsState(
//        targetValue = if (isFocused) 1.08f else 1.0f,
//        animationSpec = animSpec,
//        label = "ImageZoom"
//    )
//
//    // 2. Glass Vignette Alpha
//    val overlayAlpha by animateFloatAsState(
//        targetValue = if (isFocused) 0.6f else 0.0f,
//        animationSpec = animSpec,
//        label = "OverlayAlpha"
//    )
//
//    StandardCardContainer(
//        modifier = modifier,
//        title = { title() },
//        interactionSource = interactionSource,
//        imageCard = {
//            Surface(
//                onClick = onClick,
//                interactionSource = interactionSource,
//                shape = ClickableSurfaceDefaults.shape(SanchitraCardShape),
//                colors = ClickableSurfaceDefaults.colors(
//                    containerColor = Color.Transparent,
//                    focusedContainerColor = Color.Transparent
//                ),
//                border = ClickableSurfaceDefaults.border(
//                    focusedBorder = Border(
//                        border = BorderStroke(SanchitraBorderWidth, Color.White),
//                        shape = SanchitraCardShape
//                    )
//                ),
//                scale = ClickableSurfaceDefaults.scale(scale = 1f, focusedScale = 1.04f)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(aspectRatio)
//                        .clip(SanchitraCardShape)
//                ) {
//                    // Zooming Image
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .graphicsLayer {
//                                scaleX = imageZoomScale
//                                scaleY = imageZoomScale
//                            },
//                        content = image
//                    )
//
//                    // Cinematic Dark Glass Vignette on focus
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(
//                                brush = Brush.verticalGradient(
//                                    colors = listOf(
//                                        Color.Transparent,
//                                        Color.Black.copy(alpha = overlayAlpha)
//                                    )
//                                )
//                            )
//                    )
//                }
//            }
//        }
//    )
//}
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 2f / 3f,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // 1. Animate light sweep progression across the glass (0.0 -> 1.0)
    val lightSweepProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
        label = "LightSweep"
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
                    focusedContainerColor = Color.White.copy(alpha = 0.1f)
                ),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(SanchitraBorderWidth, Color.White.copy(alpha = 0.8f)),
                        shape = SanchitraCardShape
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(scale = 1f, focusedScale = 1.06f)
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(aspectRatio)
                        .clip(SanchitraCardShape)
                ) {
                    // Base Image
                    image()

                    // Glass Light Sweep Overlay
                    if (isFocused) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()

                                    // Calculate diagonal sheen translation
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