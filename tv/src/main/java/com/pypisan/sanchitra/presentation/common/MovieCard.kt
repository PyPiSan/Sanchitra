package com.pypisan.sanchitra.presentation.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val innerImagePadding by animateDpAsState(
        targetValue = if (isFocused) 6.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "InnerImagePadding"
    )

    val safePadding = innerImagePadding.coerceAtLeast(0.dp)

    val innerCornerRadius by animateDpAsState(
        targetValue = if (isFocused) 8.dp else 12.dp,
        label = "InnerCornerRadius"
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
                        elevationColor = Color.White.copy(alpha = 0.5f),
                        elevation = 12.dp
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(
                    scale = 1f,
                    focusedScale = 1.04f
                )
            ) {
                // NO fillMaxSize() here -> allows image to dictate flexible height!
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isFocused) 0.15f else 0.0f),
                                    Color.White.copy(alpha = if (isFocused) 0.03f else 0.0f)
                                )
                            )
                        )
                        .padding(safePadding)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(innerCornerRadius.coerceAtLeast(0.dp))),
                        content = image
                    )
                }
            }
        }
    )
}
