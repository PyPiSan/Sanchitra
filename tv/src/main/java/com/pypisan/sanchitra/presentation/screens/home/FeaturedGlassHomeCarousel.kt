package com.pypisan.sanchitra.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pypisan.sanchitra.data.models.TrendingChannel
import com.pypisan.sanchitra.utils.Padding


// Organic Amorphous Glass Frame Shape
private val AmorphousGlassShape = RoundedCornerShape(
    topStart = 22.dp,
    topEnd = 10.dp,
    bottomEnd = 26.dp,
    bottomStart = 14.dp
)

// Inner Image Clip Shape (Matching amorphous contours)
private val AmorphousImageShape = RoundedCornerShape(
    topStart = 12.dp,
    topEnd = 8.dp,
    bottomEnd = 18.dp,
    bottomStart = 8.dp
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedGlassHomeCarousel(
    modifier: Modifier = Modifier,
    channels: List<TrendingChannel>,
    padding: Padding,
    goToTVPlayer: (id: Int) -> Unit,
    isActive: Boolean = false,
    nextFocusDown: FocusRequester? = null,
    onCarouselFocused: () -> Unit = {}
) {
    if (channels.isEmpty()) return

    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    var isStackFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val latestIsActive by rememberUpdatedState(isActive)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && latestIsActive) {
                try {
                    focusRequester.requestFocus()
                } catch (e: Exception) {
                    // Ignore gracefully
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val density = LocalDensity.current
    val isLastCard = activeIndex == channels.size - 1

    val cardSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Dynamic Sizing: On non-last cards, use 96% width & 97% height so Card 2 can peek right and up.
    // On the last card, dynamically expand to 100% full width and 100% full height!
    val targetWidthFraction = if (isLastCard) 1.0f else 0.96f
    val targetHeightFraction = if (isLastCard) 1.0f else 0.97f

    val widthFraction by animateFloatAsState(
        targetValue = targetWidthFraction,
        animationSpec = cardSpringSpec,
        label = "WidthFraction"
    )

    val heightFraction by animateFloatAsState(
        targetValue = targetHeightFraction,
        animationSpec = cardSpringSpec,
        label = "HeightFraction"
    )

    Box(
        modifier = modifier
            .padding(
                start = padding.start,
                end = padding.start,
                top = padding.top
            )
            .focusProperties {
                nextFocusDown?.let { down = it }
            }
            .onFocusChanged { state ->
                val focused = state.isFocused || state.hasFocus
                isStackFocused = focused
                if (focused) {
                    onCarouselFocused()
                }
            }
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionLeft -> {
                            if (activeIndex > 0) {
                                activeIndex--
                                true
                            } else false
                        }

                        Key.DirectionRight -> {
                            if (activeIndex < channels.size - 1) {
                                activeIndex++
                                true
                            } else false
                        }

                        Key.DirectionCenter, Key.Enter -> {
                            goToTVPlayer(channels[activeIndex].id)
                            true
                        }

                        else -> false
                    }
                } else false
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            val visibleCount = 3
            for (i in (activeIndex + visibleCount - 1) downTo activeIndex) {
                if (i in channels.indices) {
                    val stackOffset = i - activeIndex

                    // Diagonal Stack Peek: Card 2 peeks 36dp right and 12dp up
                    val translateXPx = with(density) { (stackOffset * 42).dp.toPx() }
                    val translateYPx = with(density) { (-stackOffset * 20).dp.toPx() }

                    val isFocusedCard = isStackFocused && stackOffset == 0

                    val translateX by animateFloatAsState(
                        targetValue = translateXPx,
                        animationSpec = cardSpringSpec,
                        label = "TranslateX"
                    )
                    val translateY by animateFloatAsState(
                        targetValue = translateYPx,
                        animationSpec = cardSpringSpec,
                        label = "TranslateY"
                    )

                    val targetScale = (1f - (stackOffset * 0.03f)) * (if (isFocusedCard) 1.02f else 1f)
                    val scale by animateFloatAsState(
                        targetValue = targetScale,
                        animationSpec = cardSpringSpec,
                        label = "Scale"
                    )

                    val cardAlpha by animateFloatAsState(
                        targetValue = if (stackOffset == 0) 1f else 0.8f,
                        animationSpec = cardSpringSpec,
                        label = "Alpha"
                    )

                    GlassStackCard(
                        channel = channels[i],
                        isFocused = isFocusedCard,
                        itemCount = channels.size,
                        activeIndex = activeIndex,
                        showIndicator = stackOffset == 0,
                        modifier = Modifier
                            .fillMaxHeight(heightFraction)
                            .fillMaxWidth(widthFraction)
                            .zIndex((channels.size - stackOffset).toFloat())
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = translateX
                                translationY = translateY
                                alpha = cardAlpha
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun GlassStackCard(
    channel: TrendingChannel,
    isFocused: Boolean,
    itemCount: Int,
    activeIndex: Int,
    showIndicator: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        // Outer Amorphous Frosted Glass Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(AmorphousGlassShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f), // High specular reflection top-left
                            Color.White.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.80f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .border(
                    border = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    ),
                    shape = AmorphousGlassShape
                )
        ) {
            // Inner Image with 12dp Margin to reveal the Amorphous Glass Frame around it!
            AsyncImage(
                model = channel.bannerUrl,
                contentDescription = channel.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(AmorphousImageShape)
                    .drawWithContent {
                        drawContent()
                        // Dark bottom overlay for text contrast
                        drawRect(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = size.height * 0.25f
                            )
                        )
                    },
                contentScale = ContentScale.FillWidth // Applied FillWidth
            )

            // Text & Action Overlay Details
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.displayMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.95f),
                                offset = Offset(x = 2f, y = 4f),
                                blurRadius = 6f
                            )
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = channel.description?.takeIf { it.isNotBlank() } ?: channel.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.95f),
                                offset = Offset(x = 2f, y = 4f),
                                blurRadius = 6f
                            )
                        ),
                        maxLines = 2,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    AnimatedVisibility(
                        visible = isFocused,
                        modifier = Modifier.padding(top = 14.dp)
                    ) {
                        WatchNowButton()
                    }
                }

                if (showIndicator) {
                    GlassDotIndicator(
                        itemCount = itemCount,
                        activeIndex = activeIndex,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }

        // Bold Electric Focus Outline wrapping the outer Amorphous Frame
        if (isFocused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(999f)
                    // Clip ensures 100% pixel-perfect match to AmorphousGlassShape
                    .clip(AmorphousGlassShape)
                    .border(
                        width = 3.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White,                      // High-gloss specular top-left
                                Color(0xFFB2EBF2),                // Frosted Ice-Cyan Sheen
                                Color.White.copy(alpha = 0.9f)
                            )
                        ),
                        shape = AmorphousGlassShape
                    )
            )
        }
    }
}

@Composable
private fun GlassDotIndicator(
    itemCount: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val maxVisibleDots = 8
            val displayCount = itemCount.coerceAtMost(maxVisibleDots)

            repeat(displayCount) { index ->
                val isSelected = if (itemCount <= maxVisibleDots) {
                    index == activeIndex
                } else {
                    index == (activeIndex * maxVisibleDots / itemCount)
                }

                val width by animateFloatAsState(
                    targetValue = if (isSelected) 16f else 6f,
                    label = "DotWidth"
                )
                Box(
                    modifier = Modifier
                        .height(5.dp)
                        .width(width.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isSelected) Color.White
                            else Color.White.copy(alpha = 0.35f)
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun WatchNowButton() {
    Button(
        onClick = {},
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        scale = ButtonDefaults.scale(scale = 1f)
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = null,
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Watch Now",
            style = MaterialTheme.typography.titleSmall
        )
    }
}


