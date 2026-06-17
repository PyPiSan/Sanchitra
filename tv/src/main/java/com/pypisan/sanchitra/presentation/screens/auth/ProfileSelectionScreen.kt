package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.models.UserProfileMap
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    profiles: List<UserProfileMap>,
    onProfileSelected: (UserProfileMap) -> Unit,
    onGuestSelected: () -> Unit = {},
    onAddProfile: () -> Unit = {}
) {

    if (profiles.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize

    val screenWidth = with(density) { containerSize.width.toDp() }
    val screenHeight = with(density) { containerSize.height.toDp() }

    // Dynamic icon sizing (shrinks if more than 4 profiles)
    val baseIconSize = screenHeight * 0.20f
    val dynamicIconSize = remember(profiles.size) {
        if (profiles.size > 4) {
            // Scales down proportionally, preventing it from getting smaller than 50%
            val scale = (5f / profiles.size.toFloat()).coerceAtLeast(0.5f)
            baseIconSize * scale
        } else {
            baseIconSize
        }
    }

    var focusedIndex by remember { mutableIntStateOf(0) }
    var isSelecting by remember { mutableStateOf(false) }

    val progressAnim = remember { Animatable(1f) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val primaryIndex = remember(profiles) {
        profiles.indexOfFirst { it.id != "guest" && it.id != "add" }
    }

    LaunchedEffect(focusedIndex) { isSelecting = false }

    LaunchedEffect(focusedIndex) {
        val isPrimary = focusedIndex == primaryIndex

        if (!isPrimary || isSelecting) {
            progressAnim.snapTo(1f)
            return@LaunchedEffect
        }

        progressAnim.snapTo(1f)
        delay(300)

        if (!isPrimary || isSelecting) return@LaunchedEffect

        progressAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(5000, easing = LinearEasing)
        )

        if (!isSelecting && focusedIndex == primaryIndex) {
            isSelecting = true
            onProfileSelected(profiles[focusedIndex])
        }
    }

    val maxHorizontalSpread = screenWidth.value * 0.6f // Spreads across 70% of screen width
    val arcCurveHeight = screenHeight.value * 0.08f     // How deep the curve is. Increase this to make the arc sharper!

    val arcOffsets = remember(profiles.size, maxHorizontalSpread, arcCurveHeight) {
        val itemCount = profiles.size

        List(itemCount) { index ->
            val normalizedX = if (itemCount == 1) 0f else (index.toFloat() / (itemCount - 1)) * 2f - 1f

            // X spreads evenly horizontally
            val x = (maxHorizontalSpread / 2f) * normalizedX

            // Y forms a parabola
            val y = -(arcCurveHeight * (normalizedX * normalizedX))

            Pair(x, y)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // 1. Deep Cinematic Base Gradient (Dark Slate to Pure Black)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A), // Dark Slate
                            Color(0xFF020617), // Deep Space Black
                            Color(0xFF000000)  // Pure TV Black
                        )
                    )
                )

                // 2. Soft Red Spotlight (Highlights the "Who's watching?" text)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(

                            Color(0x25E50914),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height * 0.15f),
                        radius = size.width * 0.5f
                    )
                )

                // 3. First Glowing Background Arc (Simulating a Live TV / Broadcast wave)
                val wavePath1 = Path().apply {
                    moveTo(-size.width * 0.1f, size.height * 0.45f)
                    // Creates a smooth U-shape matching your profile layout
                    quadraticTo(
                        x1 = size.width / 2f,
                        y1 = size.height * 0.85f, // Lowest point of arc
                        x2 = size.width * 1.1f,
                        y2 = size.height * 0.45f // End point
                    )
                }

                drawPath(
                    path = wavePath1,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            // ✅ CHANGED: 0x33 = 20% opacity, E50914 = Rich Red
                            Color(0x33E50914),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 4.dp.toPx())
                )

                // 4. Second Subtle Arc (Echo effect for the "Broadcast" vibe)
                val wavePath2 = Path().apply {
                    moveTo(-size.width * 0.1f, size.height * 0.50f)
                    quadraticBezierTo(
                        x1 = size.width / 2f, y1 = size.height * 0.95f,
                        x2 = size.width * 1.1f, y2 = size.height * 0.50f
                    )
                }

                drawPath(
                    path = wavePath2,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x1AFFFFFF), // Faint white glow left here as a nice accent
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    ) {

        Text(
            text = "Who's Watching?",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.15f),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = (screenWidth.value * 0.04f).sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = screenHeight * 0.45f),
            contentAlignment = Alignment.Center
        ) {

            profiles.forEachIndexed { index, profile ->
                key(profile.id) {
                    val offset = arcOffsets[index]

                    ProfileArcItem(
                        profile = profile,
                        isFocusedByParent = (index == focusedIndex),
                        timerProgress = if (index == focusedIndex) progressAnim.value else 1f,
                        modifier = Modifier
                            .offset(
                                x = offset.first.dp,
                                y = offset.second.dp
                            ),
                        iconSize = dynamicIconSize, // Passes the smaller size if > 4
                        focusRequester = if (index == 0) focusRequester else null,
                        onFocusGained = { focusedIndex = index },
                        onSelected = {
                            if (isSelecting) return@ProfileArcItem
                            isSelecting = true
                            when (profile.id) {
                                "guest" -> onGuestSelected()
                                "add" -> onAddProfile()
                                else -> onProfileSelected(profile)
                            }
                        }
                    )
                }
            }
        }
    }
}